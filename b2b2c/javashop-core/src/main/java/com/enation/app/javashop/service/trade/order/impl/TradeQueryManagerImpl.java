package com.enation.app.javashop.service.trade.order.impl;

import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.enation.app.javashop.mapper.trade.order.OrderMapper;
import com.enation.app.javashop.mapper.trade.order.TradeMapper;
import com.enation.app.javashop.model.errorcode.TradeErrorCode;
import com.enation.app.javashop.model.trade.order.dos.TradeDO;
import com.enation.app.javashop.service.trade.order.TradeQueryManager;
import com.enation.app.javashop.framework.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 交易查询
 * @author Snow create in 2018/5/21
 * @version v2.0
 * @since v7.0.0
 */
@Service
public class TradeQueryManagerImpl implements TradeQueryManager {

    @Autowired
    private TradeMapper tradeMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public TradeDO getModel(String tradeSn) {

        TradeDO tradeDO = new QueryChainWrapper<>(tradeMapper)
                //按订单编号查询
                .eq("trade_sn", tradeSn)
                //查询单个对象
                .one();

        return tradeDO;
    }

    @Override
    public void checkIsOwner(String sn, Long memberId) {

        /**
         * 为了适配分库分表做出多次查询的改造
         * 原因： order表和order_log、item、meta等表分在一片， trade独立的分片规则，所以无法联合查询
         */
        Integer tradeCount = new QueryChainWrapper<>(tradeMapper)
                //拼接订单编号查询条件
                .eq("trade_sn", sn)
                //拼接买家id查询条件
                .eq("member_id", memberId)
                //查询数量
                .count();

        Integer orderCount = new QueryChainWrapper<>(orderMapper)
                //拼接订单编号查询条件
                .eq("sn", sn)
                //拼接买家id查询条件
                .eq("member_id", memberId)
                //查询数量
                .count();

        if(tradeCount == 0 && orderCount==0){
            throw new ServiceException(TradeErrorCode.E458.name(),"订单不存在！");
        }
    }
}
