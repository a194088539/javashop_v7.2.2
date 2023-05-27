package com.enation.app.javashop.service.trade.order.impl;

import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.enation.app.javashop.mapper.trade.order.OrderMetaMapper;
import com.enation.app.javashop.model.promotion.fulldiscount.dos.FullDiscountGiftDO;
import com.enation.app.javashop.model.trade.order.dos.OrderMetaDO;
import com.enation.app.javashop.model.trade.order.enums.OrderMetaKeyEnum;
import com.enation.app.javashop.service.trade.order.OrderMetaManager;
import com.enation.app.javashop.framework.util.JsonUtil;
import com.enation.app.javashop.framework.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单元信息
 *
 * @author Snow create in 2018/6/27
 * @version v2.0
 * @since v7.0.0
 */
@Service
public   class OrderMetaManagerImpl implements OrderMetaManager {

    @Autowired
    private OrderMetaMapper orderMetaMapper;

    @Override
    public void add(OrderMetaDO orderMetaDO) {

        orderMetaDO.setMetaId(null);
        orderMetaMapper.insert(orderMetaDO);
    }


    @Override
    public String getMetaValue(String orderSn,OrderMetaKeyEnum metaKey) {

        OrderMetaDO orderMetaDO = new QueryChainWrapper<>(orderMetaMapper)
                //查询扩展-值
                .select("meta_value")
                //拼接订单编号查询条件
                .eq("order_sn", orderSn)
                //拼接扩展-键查询条件
                .eq("meta_key", metaKey.name())
                //查询单个对象
                .one();

        if(orderMetaDO == null){
            return null;
        }

        return orderMetaDO.getMetaValue();
    }

    @Override
    public List<OrderMetaDO> list(String orderSn) {

        List<OrderMetaDO> list = new QueryChainWrapper<>(orderMetaMapper)
                //按订单编号查询
                .eq("order_sn", orderSn)
                //列表查询
                .list();

        return list;
    }

    @Override
    public void updateMetaValue(String orderSn, OrderMetaKeyEnum metaKey, String metaValue) {

        new UpdateChainWrapper<>(orderMetaMapper)
                //设置扩展-值
                .set("meta_value", metaValue)
                //拼接订单编号查询条件
                .eq("order_sn", orderSn)
                //拼接扩展-key查询条件
                .eq("meta_key", metaKey.name())
                //提交修改
                .update();

    }

    @Override
    public void updateMetaStatus(String orderSn, OrderMetaKeyEnum metaKey, String status) {

        new UpdateChainWrapper<>(orderMetaMapper)
                //设置售后状态
                .set("status", status)
                //拼接订单编号查询条件
                .eq("order_sn", orderSn)
                //拼接扩展-key查询条件
                .eq("meta_key", metaKey.name())
                //提交修改
                .update();

    }

    @Override
    public OrderMetaDO getModel(String orderSn, OrderMetaKeyEnum metaKey) {

        OrderMetaDO orderMetaDO = new QueryChainWrapper<>(orderMetaMapper)
                //拼接订单编号查询条件
                .eq("order_sn", orderSn)
                //拼接扩展-key查询条件
                .eq("meta_key", metaKey.name())
                //查询单个对象
                .one();

        return orderMetaDO;
    }

    @Override
    public List<FullDiscountGiftDO> getGiftList(String orderSn, String status) {

        OrderMetaDO orderMetaDO = new QueryChainWrapper<>(orderMetaMapper)
                //拼接订单编号查询条件
                .eq("order_sn", orderSn)
                //拼接扩展-key查询条件
                .eq("meta_key", OrderMetaKeyEnum.GIFT.name())
                //拼接售后状态查询条件
                .eq("status", status)
                //查询单个对象
                .one();

        if (orderMetaDO != null) {
            String giftJson = orderMetaDO.getMetaValue();
            if (!StringUtil.isEmpty(giftJson)) {
                List<FullDiscountGiftDO> giftList = JsonUtil.jsonToList(giftJson, FullDiscountGiftDO.class);
                return giftList;
            }
        }

        return null;
    }
}
