package com.enation.app.javashop.service.base.service.impl;

import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.model.base.CachePrefix;
import com.enation.app.javashop.model.base.SceneType;
import com.enation.app.javashop.model.base.SettingGroup;
import com.enation.app.javashop.model.base.vo.ConfigItem;
import com.enation.app.javashop.model.base.vo.SmsSendVO;
import com.enation.app.javashop.model.errorcode.MemberErrorCode;
import com.enation.app.javashop.service.base.plugin.sms.SmsPlatform;
import com.enation.app.javashop.model.base.rabbitmq.AmqpExchange;
import com.enation.app.javashop.service.base.service.SettingManager;
import com.enation.app.javashop.service.base.service.SmsManager;
import com.enation.app.javashop.client.system.MessageTemplateClient;
import com.enation.app.javashop.service.system.impl.RandomCreate;
import com.enation.app.javashop.model.system.enums.MessageCodeEnum;
import com.enation.app.javashop.model.system.dos.MessageTemplateDO;
import com.enation.app.javashop.model.system.vo.SiteSetting;
import com.enation.app.javashop.model.system.vo.SmsPlatformVO;
import com.enation.app.javashop.service.system.factory.SmsFactory;
import com.enation.app.javashop.framework.JavashopConfig;
import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.logs.Debugger;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import com.enation.app.javashop.framework.util.JsonUtil;
import com.enation.app.javashop.framework.util.StringUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.text.StrSubstitutor;
import com.enation.app.javashop.framework.rabbitmq.MessageSender;
import com.enation.app.javashop.framework.rabbitmq.MqMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 手机短信实现
 *
 * @author zh
 * @version v7.0
 * @since v7.0.0
 * 2018年3月19日 下午4:01:49
 */
@Service
public class SmsManagerImpl implements SmsManager {

    @Autowired
    private SettingManager settingManager;

    @Autowired
    private MessageTemplateClient messageTemplateClient;

    @Autowired
    private Cache cache;

    @Autowired
    private JavashopConfig javashopConfig;

    @Autowired
    private MessageSender messageSender;

    protected static final Logger logger = LoggerFactory.getLogger(SmsManagerImpl.class);

    @Autowired
    private Debugger debugger;

    @Autowired
    private SmsFactory smsFactory;

    @Override
    public void send(SmsSendVO smsSendVO) {

        SmsPlatform sendEvent = smsFactory.getSmsPlatform();
        debugger.log("找到短信插件:", sendEvent.getPluginName());

        sendEvent.onSend(smsSendVO.getMobile(), smsSendVO.getContent(), this.getConfig());

        logger.debug("已发送短信:短信内容为:" + smsSendVO.getContent() + "手机号:" + smsSendVO.getMobile());
    }

    @Override
    public boolean valid(String scene, String mobile, String code) {
        //从传入参数组织key
        String valCode = CachePrefix.SMS_CODE.getPrefix() + scene + "_" + mobile;
        //redis中获取验证码
        Object obj = cache.get(valCode);
        if (obj != null && obj.equals(code)) {
            //验证码校验通过后清除缓存
            cache.remove(valCode);
            //将标识放入缓存中，在验证验证码正确后，下一步操作需要校验是否经过验证验证码(缓存中是否存在)
            cache.put(CachePrefix.MOBILE_VALIDATE.getPrefix() + "_" + scene + "_" + mobile, mobile, javashopConfig.getCaptchaTimout());
            return true;
        }
        return false;
    }

    @Override
    public String validMobile(String scene, String mobile) {
        String validMobile = (String) cache.get(CachePrefix.MOBILE_VALIDATE.getPrefix() + "_" + scene + "_" + mobile);
        if(!StringUtil.isEmpty(validMobile)){
            return validMobile;
        }
        return null;
    }

    @Override
    public void record(String scene, String mobile, String code) {
        cache.put(CachePrefix.SMS_CODE.getPrefix() + scene + "_" + mobile, code, javashopConfig.getSmscodeTimout());
    }

    @Override
    public void sendSmsMessage(String byName, String mobile, SceneType sceneType) {

        StringBuffer num = new StringBuffer();
        num.append(CachePrefix.PHONE_SMS_NUMBER.getPrefix());
        num.append(mobile);
        Object o = cache.get(num.toString());
        if (o != null) {
            throw new ServiceException(MemberErrorCode.E152.code(), "发送短信频繁，请稍后再试");
        }
        cache.put(num.toString(), "1", 60);

        // 随机生成的动态码
        String dynamicCode = "";

        MessageTemplateDO template = messageTemplateClient.getModel(MessageCodeEnum.MOBILECODESEND);

        String siteSettingJson = settingManager.get(SettingGroup.SITE);

        SiteSetting siteSetting = JsonUtil.jsonToObject(siteSettingJson, SiteSetting.class);

        if (siteSetting.getTestMode().equals(1)) {
            dynamicCode = "1111";
        } else {
            dynamicCode = RandomCreate.getRandomCode();
        }

        String smsContent = template.getSmsContent();
        Map<String, Object> valuesMap = new HashMap<String, Object>(4);
        valuesMap.put("byName", byName);
        valuesMap.put("code", dynamicCode);
        valuesMap.put("siteName", siteSetting.getSiteName());
        StrSubstitutor strSubstitutor = new StrSubstitutor(valuesMap);
        String replace = strSubstitutor.replace(smsContent);

        SmsSendVO smsSendVO = new SmsSendVO();
        smsSendVO.setContent(replace);
        smsSendVO.setMobile(mobile);
        //发送短信验证码
        messageSender.send(new MqMessage(AmqpExchange.SEND_MESSAGE, AmqpExchange.SEND_MESSAGE + "_QUEUE",
                smsSendVO));

        //缓存中记录验证码
        this.record(sceneType.name(), mobile, dynamicCode);
    }

    /**
     * 将json参数转换为map格式
     *
     * @return 短信参数
     */
    private Map getConfig() {
        SmsPlatformVO smsPlatformVO = (SmsPlatformVO) this.cache.get(CachePrefix.SPlATFORM.getPrefix());

        if (StringUtil.isEmpty(smsPlatformVO.getConfig())) {
            return new HashMap<>(16);
        }
        Gson gson = new Gson();
        List<ConfigItem> list = gson.fromJson(smsPlatformVO.getConfig(), new TypeToken<List<ConfigItem>>() {
        }.getType());
        Map<String, String> result = new HashMap<>(16);
        if (list != null) {
            for (ConfigItem item : list) {
                result.put(item.getName(), StringUtil.toString(item.getValue()));
            }
        }
        return result;

    }
}
