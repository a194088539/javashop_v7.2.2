package com.enation.app.javashop.service.system.impl;

import com.enation.app.javashop.service.base.plugin.validator.ValidatorPlugin;
import com.enation.app.javashop.model.system.vo.ValidatorPlatformVO;

/**
 * 验证码vo转换器
 * @author kingapex
 * @version 1.0
 * @since 7.1.0
 * 2020/4/2
 */

public class ValidatorPlatformConverter {

    /**
     * 通过插件转换vo
     * @param plugin
     * @return
     */
    public static ValidatorPlatformVO toValidatorPlatformVO(ValidatorPlugin plugin) {
        ValidatorPlatformVO vo = new ValidatorPlatformVO();
        vo.setId(0L);
        vo.setName(plugin.getPluginName());
        vo.setOpen(plugin.getIsOpen());
        vo.setPluginId(plugin.getPluginId());
        vo.setConfigItems( plugin.definitionConfigItem());
        return vo;
    }


}
