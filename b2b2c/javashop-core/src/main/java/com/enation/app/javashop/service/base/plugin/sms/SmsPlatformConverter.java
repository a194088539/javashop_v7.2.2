package com.enation.app.javashop.service.base.plugin.sms;

import com.enation.app.javashop.model.system.vo.SmsPlatformVO;

/**
 * 短信网关vo转换器
 * @author kingapex
 * @version 1.0
 * @since 7.3.0
 * 2020/4/2
 */

public class SmsPlatformConverter {

    /**
     * 通过sms platofrm插件转换成vo
     * @param smsPlatform
     * @return
     */
    public static SmsPlatformVO toSmsPlatformVO(SmsPlatform smsPlatform) {
        SmsPlatformVO smsPlatformVO = new SmsPlatformVO();
        smsPlatformVO.setId(0L);
        smsPlatformVO.setName(smsPlatform.getPluginName());
        smsPlatformVO.setOpen(smsPlatform.getIsOpen());
        smsPlatformVO.setBean(smsPlatform.getPluginId());
        smsPlatformVO.setConfigItems( smsPlatform.definitionConfigItem());
        return smsPlatformVO;
    }
}
