package com.enation.app.javashop.service.trade.pintuan;

import com.enation.app.javashop.model.promotion.pintuan.PintuanChildOrder;
import com.enation.app.javashop.model.promotion.pintuan.PintuanOrder;
import com.enation.app.javashop.model.promotion.pintuan.PintuanOrderDetailVo;
import com.enation.app.javashop.model.trade.order.dos.OrderDO;
import com.enation.app.javashop.model.trade.order.dto.OrderDTO;
import com.enation.app.javashop.model.trade.order.vo.TradeVO;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by kingapex on 2019-01-24.
 * 拼团订单业务类
 * @author kingapex
 * @version 1.0
 * @since 7.1.0
 * 2019-01-24
 */
public interface PintuanOrderManager {


    /**
     * 发起或参与拼团订单
     * @param tradeVO 交易对象
     * @param order 常规订单
     * @param skuId sku id
     * @param pinTuanOrderId 拼团订单id ，如果为空则为发起拼团，否则参与此拼团
     * @return 拼团订单
     */
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    PintuanOrder createOrder(TradeVO tradeVO, OrderDTO order, Long skuId, Long pinTuanOrderId);

    /**
     * 对一个拼团订单进行支付处理
     * @param order 普通订单
     */
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    void payOrder(OrderDO order);


    /**
     * 根据id获取模型
     * @param id
     * @return
     */
    PintuanOrder getModel(Long id);

    /**
     * 通过普通订单号查找拼团主订单
     * @param orderSn
     * @return
     */
    PintuanOrderDetailVo getMainOrderBySn(String orderSn);

    /**
     * 读取某个商品待成团的订单
     * @param goodsId 商品id
     * @param skuId skuId
     * @return  拼团订单
     */
    List<PintuanOrder> getWaitOrder(Long goodsId,Long skuId);

    /**
     * 读取某订单的所有子订单
     * @param orderId
     * @return
     */
    List<PintuanChildOrder> getPintuanChild(Long orderId);

    /**
     * 处理拼团订单
     * @param orderId 订单id
     */
    void handle(Long orderId);

    /**
     * 取消拼团订单
     * @param orderSn 订单号
     */
    void cancelOrder(String orderSn);

}
