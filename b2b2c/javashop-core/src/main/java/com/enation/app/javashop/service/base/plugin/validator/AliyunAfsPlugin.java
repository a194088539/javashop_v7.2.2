package com.enation.app.javashop.service.base.plugin.validator;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.afs.model.v20180112.AuthenticateSigRequest;
import com.aliyuncs.afs.model.v20180112.AuthenticateSigResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.enation.app.javashop.framework.util.StringUtil;
import com.enation.app.javashop.model.base.vo.ConfigItem;
import com.enation.app.javashop.model.errorcode.SystemErrorCode;
import com.enation.app.javashop.framework.context.request.ThreadContextHolder;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 阿里云滑动验证插件
 *
 * @author duanmingyu
 * @version v1.0
 * @since v7.1.6
 * 2019-12-18
 */
@Component
public class AliyunAfsPlugin implements ValidatorPlugin {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public List<ConfigItem> definitionConfigItem() {
        List<ConfigItem> list = new ArrayList<>();
        ConfigItem appKeyItem = new ConfigItem();
        appKeyItem.setName("appKey");
        appKeyItem.setText("应用程序秘钥");
        appKeyItem.setType("text");

        ConfigItem sceneItem = new ConfigItem();
        sceneItem.setName("scene");
        sceneItem.setText("场景标识");
        sceneItem.setType("text");

        ConfigItem regionItem = new ConfigItem();
        regionItem.setName("regionId");
        regionItem.setText("地区标识");
        regionItem.setType("text");

        ConfigItem accessIdItem = new ConfigItem();
        accessIdItem.setName("accessKeyId");
        accessIdItem.setText("访问密钥ID");
        accessIdItem.setType("text");

        ConfigItem accessKeyItem = new ConfigItem();
        accessKeyItem.setName("accessKeySecret");
        accessKeyItem.setText("访问密钥");
        accessKeyItem.setType("text");

        list.add(appKeyItem);
        list.add(regionItem);
        list.add(accessIdItem);
        list.add(accessKeyItem);
        return list;
    }

    @Override
    public String getPluginId() {
        return "aliyunAfsPlugin";
    }

    @Override
    public String getPluginName() {
        return "阿里云滑动验证";
    }

    @Override
    public Integer getIsOpen() {
        return 0;
    }

    @Override
    public void onValidate(Map param) {

        HttpServletRequest req = ThreadContextHolder.getHttpRequest();

        /** 应用标示。它和scene字段一起决定了滑动验证的业务场景与后端对应使用的策略模型 */
        String appKey = param.get("appKey").toString();

        /** 场景标示。它和appkey字段一起决定了滑动验证的业务场景与后端对应使用的策略模型 */
        String scene = param.get("scene").toString();

        /** 地区标示 */
        String regionId = param.get("regionId").toString();

        /** 访问秘钥ID */
        String accessKeyId = param.get("accessKeyId").toString();

        /** 访问秘钥 */
        String accessKeySecret = param.get("accessKeySecret").toString();

        AuthenticateSigRequest request = new AuthenticateSigRequest();
        String cSessionid = req.getParameter("c_sessionid");
        if(StringUtil.isEmpty(cSessionid)){
            throw new ServiceException(SystemErrorCode.E930.code(), "验证方式已更换，请刷新界面重试");
        }
        request.setSessionId(cSessionid);
        request.setSig(req.getParameter("sig"));
        request.setToken(req.getParameter("nc_token"));
        request.setScene(scene);
        request.setAppKey(appKey);
        request.setRemoteIp(getIpAddr(req));
        try {
            IAcsClient client = getClientProfile(regionId, accessKeyId, accessKeySecret);
            //response的code枚举：100验签通过，900验签失败
            AuthenticateSigResponse response = client.getAcsResponse(request);
            if (response.getCode() != 100) {
                throw new ServiceException(SystemErrorCode.E930.code(), "滑动验证参数校验失败");
            }
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            //输出日志，但是继续返回前端相同的错误信息，不直接抛出 Exception 是为了减少logger输出
            logger.error("滑动验证参数校验失败", e);
            throw new ServiceException(SystemErrorCode.E930.code(), "滑动验证参数校验失败");
        }
    }

    private static IAcsClient client;

    private static IAcsClient getClientProfile(String regionId, String accessKeyId, String accessKeySecret) throws ClientException {

        if (client != null) {
            return client;
        }

        IClientProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
        client = new DefaultAcsClient(profile);
        DefaultProfile.addEndpoint(regionId, regionId, "afs", "afs.aliyuncs.com");

        return client;

    }

    public String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
