package com.enation.app.javashop.service.trade.order.impl;

import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.sncreator.SnCreator;
import com.enation.app.javashop.framework.util.StringUtil;
import com.enation.app.javashop.model.base.SubCode;
import com.enation.app.javashop.model.errorcode.TradeErrorCode;
import com.enation.app.javashop.model.trade.order.vo.ConsigneeVO;
import com.enation.app.javashop.model.trade.order.vo.TradeParam;
import com.enation.app.javashop.model.trade.order.vo.TradeVO;
import com.enation.app.javashop.service.trade.order.TradeCenterManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @description: 交易中心业务实现
 * @author: liuyulei
 * @create: 2020-03-23 18:20
 * @version:1.0
 * @since:7.1.5
 **/
@Service
public class TradeCenterManagerImpl implements TradeCenterManager {

    @Autowired
    SnCreator snCreator;

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public TradeVO createTrade(TradeParam tradeParam) {
        TradeVO tradeVO = new TradeVO();
        /**
         * 两种设置联系人的方式
         * 方式一：选择用户设置的收货地址信息（包含联系人、联系方式、收货地址等信息）
         * 方式二：设置联系人姓名和联系方式（仅包含联系人和联系方式）
         * 二者选其一
         */
        if (tradeParam.getAddrId() == null && StringUtil.isEmpty(tradeParam.getMobile())) {
            throw new ServiceException(TradeErrorCode.E452.code(), "请填写联系人相关信息");
        }

        //如果存在收货地址id，则获取用户收货地址信息
        if (tradeParam.getConsignee() != null) {
            tradeVO.setConsignee(tradeParam.getConsignee());
        } else {
            //根据传入的联系人和联系方式设置交易信息
            ConsigneeVO consigneeVO = new ConsigneeVO(tradeParam.getShipName(), tradeParam.getMobile());
            tradeVO.setConsignee(consigneeVO);
        }

        //设置交易价格信息
        tradeVO.setPriceDetail(tradeParam.getPrice());
        tradeVO.setPaymentType(tradeParam.getPaymentType());
        tradeVO.setMemberId(tradeParam.getMemberId());
        tradeVO.setMemberName(tradeParam.getMemberName());
        tradeVO.setTradeSn(""+snCreator.create(SubCode.TRADE));
        tradeVO.setClientType(tradeParam.getClient());
        tradeVO.setReceiveTime(tradeParam.getReceiveTime());
        tradeVO.setReceipt(tradeParam.getReceipt());
        tradeVO.setRemark(tradeParam.getRemark());

        return tradeVO;
    }

}
