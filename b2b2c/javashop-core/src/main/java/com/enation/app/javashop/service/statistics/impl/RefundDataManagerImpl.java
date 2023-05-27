package com.enation.app.javashop.service.statistics.impl;

import com.enation.app.javashop.mapper.statistics.RefundDataMapper;
import com.enation.app.javashop.service.statistics.RefundDataManager;
import org.springframework.stereotype.Service;
import com.enation.app.javashop.model.statistics.dto.RefundData;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 退款变化业务实现类
 *
 * @author mengyuanming
 * @version 2.0
 * @since 7.0
 * 2018/6/4 9:59
 */
@Service
public class RefundDataManagerImpl implements RefundDataManager {

    @Autowired
    private RefundDataMapper refundDataMapper;

    @Override
    public void put(RefundData refundData) {
        //审核通过
        refundDataMapper.insert(refundData);
    }

}
