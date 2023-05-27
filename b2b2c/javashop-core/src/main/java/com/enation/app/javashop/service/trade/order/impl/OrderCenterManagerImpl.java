package com.enation.app.javashop.service.trade.order.impl;

import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import com.enation.app.javashop.framework.sncreator.SnCreator;
import com.enation.app.javashop.framework.util.CurrencyUtil;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.model.base.SubCode;
import com.enation.app.javashop.model.trade.complain.enums.ComplainSkuStatusEnum;
import com.enation.app.javashop.model.trade.order.dto.OrderDTO;
import com.enation.app.javashop.model.trade.order.enums.OrderServiceStatusEnum;
import com.enation.app.javashop.model.trade.order.vo.OrderParam;
import com.enation.app.javashop.model.trade.order.vo.OrderSkuVO;
import com.enation.app.javashop.service.trade.order.OrderCenterManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 订单中心业务实现
 * @author: liuyulei
 * @create: 2020-03-23 18:39
 * @version:1.0
 * @since:7.1.5
 **/
@Service
public class OrderCenterManagerImpl implements OrderCenterManager {


    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    SnCreator snCreator;

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public OrderDTO createOrder(OrderParam orderParam) {
        //处理订单数据
        OrderDTO order = new OrderDTO(orderParam);
        //填充订单数据
        fillOrder(order, orderParam);

        return order;
    }

    /**
     * 填充订单数据
     *
     * @param order      订单DTO
     * @param orderParam 订单参数
     */
    private void fillOrder(OrderDTO order, OrderParam orderParam) {
        //生成订单编号
        String orderSn = ""+snCreator.create(SubCode.ORDER);
        //订单创建时间
        long createTime = DateUtil.getDateline();
        //创建时间
        order.setCreateTime(createTime);

        List<OrderSkuVO> skus = new ArrayList<>();
        //获取订单中sku信息
        for (OrderSkuVO orderSkuVO : orderParam.getSkuParam()) {
            orderSkuVO.setServiceStatus(OrderServiceStatusEnum.NOT_APPLY.name());
            orderSkuVO.setComplainStatus(ComplainSkuStatusEnum.NO_APPLY.name());

            skus.add(orderSkuVO);
        }
        order.setOrderSkuList(skus);
        order.setSn(orderSn);

        logger.debug("订单[" + order.getSn() + "]的price:");
        logger.debug(order.getPrice());

        //重新计算优惠金额
        order.getPrice().reCountDiscountPrice();

        //获取订单价格
        double orderTotalPrice = order.getPrice().getTotalPrice();
        //如果使用的是站点优惠券
        if (orderParam.getIsSiteCoupon() != null && orderParam.getIsSiteCoupon()) {
            //此订单总价减去该订单可分享的站点优惠金额
            orderTotalPrice = CurrencyUtil.sub(order.getPrice().getTotalPrice(), orderParam.getCouponTotalPrice());
        }
        order.setNeedPayMoney(orderTotalPrice);
        order.setOrderPrice(orderTotalPrice);
    }
}
