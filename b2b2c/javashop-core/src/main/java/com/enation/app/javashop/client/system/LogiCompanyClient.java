package com.enation.app.javashop.client.system;

import com.enation.app.javashop.model.system.dos.LogisticsCompanyDO;

import java.util.List;

/**
 * @version v7.0
 * @Description:
 * @Author: zjp
 * @Date: 2018/7/26 14:17
 */
public interface LogiCompanyClient {
    /**
     * 通过code获取物流公司
     * @param code 物流公司code
     * @return 物流公司
     */
    LogisticsCompanyDO getLogiByCode(String code);

    /**
     * 获取物流公司
     * @param id 物流公司主键
     * @return Logi  物流公司
     */
    LogisticsCompanyDO getModel(Long id);

    /**
     * 查询物流公司列表(不分页)
     * @return WebPage
     */
    List<LogisticsCompanyDO> list();

    /**
     * 查询平台添加的全部物流公司（正常使用未删除的）
     * @return
     */
    List<LogisticsCompanyDO> listAllNormal();
}
