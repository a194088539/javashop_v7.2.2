package com.enation.app.javashop.service.system.factory;

import com.enation.app.javashop.service.base.plugin.validator.ValidatorPlugin;
import com.enation.app.javashop.model.system.vo.ValidatorPlatformVO;
import com.enation.app.javashop.service.system.ValidatorPlatformManager;
import com.enation.app.javashop.framework.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 验证插件工厂类
 * @author duanmingyu
 * @version v1.0
 * @since v7.1.6
 * 2019-12-30
 */
@Component
public class ValidatorFactory {

    @Autowired
    private List<ValidatorPlugin> validatorPlugins;

    @Autowired
    private ValidatorPlatformManager validatorPlatformManager;

    public ValidatorPlugin getValidatorPlugin() {
        ValidatorPlatformVO validatorPlatformVO = this.validatorPlatformManager.getOpen();

        return this.findByPluginid(validatorPlatformVO.getPluginId());
    }

    public ValidatorPlugin getCaptchaValidatorPlugin() {
        String pluginId = "captchaValidatorPlugin";
        return this.findByPluginid(pluginId);
    }

    /**
     * 根据插件id获取验证插件
     *
     * @param pluginId
     * @return
     */
    private ValidatorPlugin findByPluginid(String pluginId) {
        for (ValidatorPlugin validatorPlugin : validatorPlugins) {
            if (validatorPlugin.getPluginId().equals(pluginId)) {
                return validatorPlugin;
            }
        }
        //如果走到这里，说明找不到可用的验证插件
        throw new ResourceNotFoundException("未找到可用的验证插件");
    }
}
