package com.enation.app.javashop.service.member.plugin.wechat;

import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.model.base.DomainHelper;
import com.enation.app.javashop.model.errorcode.MemberErrorCode;
import com.enation.app.javashop.model.member.dos.Member;
import com.enation.app.javashop.model.member.enums.ConnectTypeEnum;
import com.enation.app.javashop.model.member.enums.WechatConnectConfigGroupEnum;
import com.enation.app.javashop.model.member.enums.WechatConnectConfigItemEnm;
import com.enation.app.javashop.model.member.vo.Auth2Token;
import com.enation.app.javashop.model.member.vo.ConnectSettingConfigItem;
import com.enation.app.javashop.model.member.vo.ConnectSettingParametersVO;
import com.enation.app.javashop.model.member.vo.ConnectSettingVO;
import com.enation.app.javashop.service.member.AbstractConnectLoginPlugin;
import com.enation.app.javashop.service.passport.signaturer.WechatSignaturer;
import com.enation.app.javashop.model.payment.enums.WechatTypeEnmu;
import com.enation.app.javashop.framework.context.request.ThreadContextHolder;
import com.enation.app.javashop.framework.logs.Debugger;
import com.enation.app.javashop.framework.util.HttpUtils;
import com.enation.app.javashop.framework.util.JsonUtil;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zjp
 * @version v7.0
 * @Description 微信信任登录插件类
 * @since v7.0 上午11:18 2018/6/5
 */
@Component
public class WechatConnectLoginPlugin extends AbstractConnectLoginPlugin {


    @Autowired
    private DomainHelper domainHelper;

    @Autowired
    protected Debugger debugger;

    @Autowired
    private WechatSignaturer wechatSignaturer;


    public WechatConnectLoginPlugin() {
        super();
    }


    @Override
    public String getLoginUrl() {
        String ua = ThreadContextHolder.getHttpRequest().getHeader("user-agent").toLowerCase();
        if (ua.indexOf("micromessenger") > -1) {
            return wechatSignaturer.getAuthorizeUrl(domainHelper.getCallback() + "/passport/connect/wechat/auth/back");
        }
        return wechatSignaturer.getAuthorizeUrl(this.getCallBackUrl(ConnectTypeEnum.WECHAT.value()));
    }

    @Override
    public Auth2Token loginCallback(String client) {
        return wechatSignaturer.getCallbackAccessToken(client);
    }

    @Override
    public Member fillInformation(Auth2Token auth2Token, Member member) {
        JSONObject jsonObject = wechatSignaturer.getWechatInfo(auth2Token.getAccessToken(), auth2Token.getOpneId());

        member.setNickname(jsonObject.containsKey("nickname") ? jsonObject.getString("nickname") : member.getNickname());
        member.setFace(jsonObject.getString("headimgurl"));
        String sex = jsonObject.getString("sex");
        if ("1".equals(sex)) {
            member.setSex(1);
        } else {
            member.setSex(0);
        }
        return member;
    }

    @Override
    public ConnectSettingVO assembleConfig() {
        ConnectSettingVO connectSetting = new ConnectSettingVO();
        List<ConnectSettingParametersVO> list = new ArrayList<>();
        for (WechatConnectConfigGroupEnum wechatConnectConfigGroupEnum : WechatConnectConfigGroupEnum.values()) {
            ConnectSettingParametersVO connectSettingParametersVO = new ConnectSettingParametersVO();
            List<ConnectSettingConfigItem> lists = new ArrayList<>();
            for (WechatConnectConfigItemEnm wechatConnectConfigItem : WechatConnectConfigItemEnm.values()) {
                ConnectSettingConfigItem connectSettingConfigItem = new ConnectSettingConfigItem();
                connectSettingConfigItem.setKey("wechat_" + wechatConnectConfigGroupEnum.value() + "_" + wechatConnectConfigItem.value());
                connectSettingConfigItem.setName(wechatConnectConfigItem.getText());
                lists.add(connectSettingConfigItem);
            }
            connectSettingParametersVO.setConfigList(lists);
            connectSettingParametersVO.setName(wechatConnectConfigGroupEnum.getText());
            list.add(connectSettingParametersVO);
        }
        connectSetting.setName("微信参数配置");
        connectSetting.setType(ConnectTypeEnum.WECHAT.value());
        connectSetting.setConfig(JsonUtil.objectToJson(list));
        return connectSetting;
    }


    /**
     * 小程序自动登录
     *
     * @return
     */
    public String miniProgramAutoLogin(String code) {

        Map map = initConnectSetting();
        Object appId = map.get("wechat_miniprogram_app_id");
        Object appKey = map.get("wechat_miniprogram_app_key");
        if (appId == null || appKey == null) {
            throw new ServiceException(MemberErrorCode.E106.code(), "小程序微信参数没有配置");
        }

        if ("".equals(appId.toString()) || "".equals(appKey.toString())) {
            throw new ServiceException(MemberErrorCode.E106.code(), "小程序微信参数没有配置");
        }
        String url = "https://api.weixin.qq.com/sns/jscode2session?" +
                "appid=" + map.get("wechat_miniprogram_app_id") + "&" +
                "secret=" + map.get("wechat_miniprogram_app_key") + "&" +
                "js_code=" + code + "&" +
                "grant_type=authorization_code";
        String content = HttpUtils.doGet(url, "UTF-8", 100, 1000);

        return content;
    }

    /**
     * 获取accesstoken
     *
     * @return
     */
    public String getWXAccessToken() {
        return wechatSignaturer.getCgiAccessToken(WechatTypeEnmu.MINI);
    }
}
