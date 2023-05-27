package com.enation.app.javashop.service.base.plugin.validator;

import com.enation.app.javashop.framework.util.StringUtil;
import com.enation.app.javashop.model.base.vo.ConfigItem;
import com.enation.app.javashop.client.system.CaptchaClient;
import com.enation.app.javashop.model.errorcode.SystemErrorCode;
import com.enation.app.javashop.framework.context.request.ThreadContextHolder;
import com.enation.app.javashop.framework.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 图片验证码验证插件
 * @author duanmingyu
 * @version v1.0
 * @since v7.1.6
 * 2019-12-18
 */
@Component
public class CaptchaValidatorPlugin implements ValidatorPlugin {

    @Autowired
    private CaptchaClient captchaClient;

    @Override
    public List<ConfigItem> definitionConfigItem() {
        return null;
    }

    @Override
    public String getPluginId() {
        return "captchaValidatorPlugin";
    }

    @Override
    public String getPluginName() {
        return "图片验证码";
    }

    @Override
    public Integer getIsOpen() {
        return 0;
    }

    @Override
    public void onValidate(Map param) {
        HttpServletRequest req = ThreadContextHolder.getHttpRequest();

        /** 获取客户端唯一标识 */
        String uuid = req.getParameter("uuid");
        /** 获取图片验证码 */
        String captcha = req.getParameter("captcha");
        /** 获取图片验证码业务类型 */
        String sceneType = req.getParameter("scene");

        if(StringUtil.isEmpty(uuid)){
            throw new ServiceException(SystemErrorCode.E930.code(), "验证方式已更换，请刷新界面重试");
        }

        /** 校验图片验证码 */
        boolean isPass = captchaClient.valid(uuid, captcha, sceneType);

        if (!isPass) {
            throw new ServiceException(SystemErrorCode.E930.code(), "图片验证码不正确");
        }
    }
}
