package com.enation.app.javashop.service.aftersale.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.enation.app.javashop.mapper.trade.aftersale.AfterSaleChangeMapper;
import com.enation.app.javashop.model.aftersale.dos.AfterSaleChangeDO;
import com.enation.app.javashop.service.aftersale.AfterSaleChangeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 售后服务退货地址业务接口实现
 * @author duanmingyu
 * @version v1.0
 * @since v7.1.5
 * 2019-12-03
 */
@Service
public class AfterSaleChangeManagerImpl implements AfterSaleChangeManager {

    @Autowired
    private AfterSaleChangeMapper afterSaleChangeMapper;

    @Override
    public void add(AfterSaleChangeDO afterSaleChangeDO) {
        afterSaleChangeMapper.insert(afterSaleChangeDO);
    }

    @Override
    public AfterSaleChangeDO fillChange(String serviceSn, AfterSaleChangeDO afterSaleChangeDO) {
        //设置售后服务单号
        afterSaleChangeDO.setServiceSn(serviceSn);
        //新增售后服务退货地址信息
        this.add(afterSaleChangeDO);
        return afterSaleChangeDO;
    }

    @Override
    public AfterSaleChangeDO getModel(String serviceSn) {
        //新建查询条件包装器
        QueryWrapper<AfterSaleChangeDO> wrapper = new QueryWrapper<>();
        //以售后服务单号为查询条件
        wrapper.eq("service_sn", serviceSn);
        //返回查询到的售后服务退货地址信息
        return afterSaleChangeMapper.selectOne(wrapper);
    }
}
