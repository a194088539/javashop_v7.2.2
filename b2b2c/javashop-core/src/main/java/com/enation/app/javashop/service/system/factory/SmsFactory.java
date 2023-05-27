package com.enation.app.javashop.service.system.factory;

import com.enation.app.javashop.service.base.plugin.sms.SmsPlatform;
import com.enation.app.javashop.model.system.vo.SmsPlatformVO;
import com.enation.app.javashop.service.system.SmsPlatformManager;
import com.enation.app.javashop.framework.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 短信发送插件工厂类
 * @author duanmingyu
 * @version v1.0
 * @since v7.1.6
 * 2019-12-30
 */
@Component
public class SmsFactory {

    @Autowired
    private List<SmsPlatform> smsPlatforms;

    @Autowired
    private SmsPlatformManager smsPlatformManager;

    public SmsPlatform getSmsPlatform() {
        SmsPlatformVO platformVO = smsPlatformManager.getOpen();
        return this.findByPluginid(platformVO.getBean());
    }

    /**
     * 根据插件id获取短信发送插件
     *
     * @param pluginId
     * @return
     */
    private SmsPlatform findByPluginid(String pluginId) {
        for (SmsPlatform smsPlatform : smsPlatforms) {
            if (smsPlatform.getPluginId().equals(pluginId)) {
                return smsPlatform;
            }
        }
        //如果走到这里，说明找不到可用的短信发送插件
        throw new ResourceNotFoundException("未找到可用的短信发送插件");
    }

}
