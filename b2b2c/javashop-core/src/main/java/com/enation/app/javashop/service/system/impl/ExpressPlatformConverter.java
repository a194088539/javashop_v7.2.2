package com.enation.app.javashop.service.system.impl;

import com.enation.app.javashop.service.base.plugin.express.ExpressPlatform;
import com.enation.app.javashop.model.system.vo.ExpressPlatformVO;

/**
 * Express platform 转换器
 * @author kingapex
 * @version 1.0
 * @since 7.3.0
 * 2020/4/2
 */

public class ExpressPlatformConverter {

    /**
     * 使用一个插件转换vo
     * @param expressPlatform
     * @return
     */
    public static ExpressPlatformVO toExpressPlatformVO (ExpressPlatform expressPlatform) {

        ExpressPlatformVO vo = new ExpressPlatformVO();
        vo.setId(0L);
        vo.setName(expressPlatform.getPluginName());
        vo.setOpen(expressPlatform.getIsOpen());
        vo.setBean(expressPlatform.getPluginId());
        vo.setConfigItems( expressPlatform.definitionConfigItem());
        return vo;
    }
}
