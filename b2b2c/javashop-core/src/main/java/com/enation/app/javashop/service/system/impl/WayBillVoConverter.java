package com.enation.app.javashop.service.system.impl;

import com.enation.app.javashop.service.base.plugin.waybill.WayBillPlugin;
import com.enation.app.javashop.model.system.vo.WayBillVO;

/**
 * WayBill vo转换器
 * @author kingapex
 * @version 1.0
 * @since 7.1.0
 * 2020/4/2
 */

public class WayBillVoConverter {

    /**
     * 通过插件转换vo
     * @param plugin
     * @return
     */
    public static WayBillVO toValidatorPlatformVO(WayBillPlugin plugin) {
        WayBillVO vo = new WayBillVO();
        vo.setId(0L);
        vo.setName(plugin.getPluginName());
        vo.setOpen(plugin.getOpen());
        vo.setBean(plugin.getPluginId());
        vo.setConfigItems( plugin.definitionConfigItem());
        return vo;
    }


}
