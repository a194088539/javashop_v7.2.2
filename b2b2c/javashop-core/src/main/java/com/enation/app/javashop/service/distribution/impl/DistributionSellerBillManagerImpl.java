package com.enation.app.javashop.service.distribution.impl;

import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.mapper.distribution.DistributionSellerBillMapper;
import com.enation.app.javashop.model.distribution.dos.DistributionSellerBillDO;
import com.enation.app.javashop.model.distribution.dos.DistributionOrderDO;
import com.enation.app.javashop.model.distribution.dto.DistributionSellerBillDTO;
import com.enation.app.javashop.service.distribution.DistributionSellerBillManager;
import com.enation.app.javashop.framework.util.CurrencyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 实现
 *
 * @author Chopper
 * @version v1.0
 * @since v7.0
 * 2018-09-05 下午4:12
 */
@Service
public class DistributionSellerBillManagerImpl implements DistributionSellerBillManager {

    @Autowired
    private DistributionSellerBillMapper distributionSellerBillMapper;

    /**
     * 新增记录
     *
     * @param distributionOrderDO
     */
    @Override
    public void add(DistributionOrderDO distributionOrderDO) {
        DistributionSellerBillDO distributionSellerBillDO = new DistributionSellerBillDO();
        distributionSellerBillDO.setSellerId(distributionOrderDO.getSellerId());
        distributionSellerBillDO.setCreateTime(DateUtil.getDateline());
        if(distributionOrderDO.getGrade1Rebate()==null){
            distributionOrderDO.setGrade1Rebate(0D);
        }
        if(distributionOrderDO.getGrade2Rebate()==null){
            distributionOrderDO.setGrade2Rebate(0D);
        }
        distributionSellerBillDO.setExpenditure(CurrencyUtil.add(distributionOrderDO.getGrade1Rebate(), distributionOrderDO.getGrade2Rebate()));
        distributionSellerBillDO.setReturnExpenditure(0D);
        distributionSellerBillDO.setOrderSn(distributionOrderDO.getOrderSn());
        distributionSellerBillMapper.insert(distributionSellerBillDO);
    }

    /**
     * 新增退款记录
     *
     * @param distributionOrderDO
     */
    @Override
    public void addRefund(DistributionOrderDO distributionOrderDO) {
        DistributionSellerBillDO distributionSellerBillDO = new DistributionSellerBillDO();
        distributionSellerBillDO.setSellerId(distributionOrderDO.getSellerId());
        distributionSellerBillDO.setCreateTime(DateUtil.getDateline());
        if(distributionOrderDO.getGrade1SellbackPrice()==null){
            distributionOrderDO.setGrade1SellbackPrice(0D);
        }
        if(distributionOrderDO.getGrade2SellbackPrice()==null){
            distributionOrderDO.setGrade2SellbackPrice(0D);
        }
        distributionSellerBillDO.setReturnExpenditure(CurrencyUtil.add(distributionOrderDO.getGrade1SellbackPrice(), distributionOrderDO.getGrade2SellbackPrice()));
        distributionSellerBillDO.setExpenditure(0D);
        distributionSellerBillDO.setOrderSn(distributionOrderDO.getOrderSn());
        distributionSellerBillMapper.insert(distributionSellerBillDO);
    }

    @Override
    public List<DistributionSellerBillDTO> countSeller(Integer startTime, Integer endTime) {
        List<DistributionSellerBillDTO> dtos = distributionSellerBillMapper.queryCountSeller(startTime,endTime);
        return dtos;
    }

}
