package com.enation.app.javashop.client.trade;


import com.enation.app.javashop.model.promotion.pintuan.PintuanOrderDetailVo;
import com.enation.app.javashop.model.trade.order.dos.OrderDO;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 拼团订单操作SDK
 *
 * @author fk create in 2020/4/7
 * @version v2.0
 * @since v7.2.0
 */
public interface PintuanOrderClient {

    /**
     * 通过普通订单号查找拼团主订单
     * @param orderSn
     * @return
     */
    PintuanOrderDetailVo getMainOrderBySn(String orderSn);

    /**
     * 取消拼团订单
     * @param orderSn 订单号
     */
    void cancelOrder(String orderSn);


    /**
     * 对一个拼团订单进行支付处理
     * @param order 普通订单
     */
    void payOrder(OrderDO order);



}
