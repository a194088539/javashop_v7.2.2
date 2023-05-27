package com.enation.app.javashop.service.payment.impl;

import com.enation.app.javashop.model.payment.vo.PaymentPluginVO;
import com.enation.app.javashop.service.payment.PaymentPluginManager;

/**
 * Payment vo转换器
 * @author kingapex
 * @version 1.0
 * @since 7.1.0
 * 2020/4/2
 */

public class PaymentVoConverter {

    /**
     * 通过插件转换vo
     * @param plugin
     * @return
     */
    public static PaymentPluginVO toValidatorPlatformVO(PaymentPluginManager plugin) {
        PaymentPluginVO vo = new PaymentPluginVO();
        vo.setMethodName(plugin.getPluginName());
        vo.setPluginId(plugin.getPluginId());
        vo.setIsRetrace(plugin.getIsRetrace());
        return vo;
    }


}
