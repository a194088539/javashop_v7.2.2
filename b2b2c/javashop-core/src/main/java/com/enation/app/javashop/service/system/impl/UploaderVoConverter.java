package com.enation.app.javashop.service.system.impl;

import com.enation.app.javashop.service.base.plugin.upload.Uploader;
import com.enation.app.javashop.model.system.vo.UploaderVO;

/**
 * Uploader vo转换器
 * @author kingapex
 * @version 1.0
 * @since 7.1.0
 * 2020/4/2
 */

public class UploaderVoConverter {

    /**
     * 通过插件转换vo
     * @param plugin
     * @return
     */
    public static UploaderVO toValidatorPlatformVO(Uploader plugin) {
        UploaderVO vo = new UploaderVO();
        vo.setId(0L);
        vo.setName(plugin.getPluginName());
        vo.setOpen(plugin.getIsOpen());
        vo.setBean(plugin.getPluginId());
        vo.setConfigItems( plugin.definitionConfigItem());
        return vo;
    }


}
