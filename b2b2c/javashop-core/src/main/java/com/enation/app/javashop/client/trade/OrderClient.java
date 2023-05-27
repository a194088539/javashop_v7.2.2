package com.enation.app.javashop.client.trade;

import com.enation.app.javashop.model.trade.cart.dos.OrderPermission;
import com.enation.app.javashop.model.trade.order.dos.OrderDO;
import com.enation.app.javashop.model.trade.order.dos.OrderItemsDO;
import com.enation.app.javashop.model.trade.order.enums.CommentStatusEnum;
import com.enation.app.javashop.model.trade.order.enums.OrderStatusEnum;
import com.enation.app.javashop.model.trade.order.vo.DeliveryVO;
import com.enation.app.javashop.model.trade.order.vo.OrderDetailVO;
import com.enation.app.javashop.model.trade.order.vo.OrderStatusNumVO;
import com.enation.app.javashop.model.trade.order.dto.OrderDetailDTO;

import java.util.List;
import java.util.Map;

/**
 * 订单查询SDK
 *
 * @author Snow create in 2018/5/28
 * @version v2.0
 * @since v7.0.0
 */
public interface OrderClient {

    /**
     * 读取一个订单详细<br/>
     *
     * @param orderSn 订单编号 必传
     * @return
     */
    OrderDetailDTO getModel(String orderSn);

    /**
     * 读取一个订单详细<br/>
     *
     * @param orderSn 订单编号 必传
     * @return
     */
    OrderDetailVO getOrderVO(String orderSn);

    /**
     * 更改订单评论状态
     *
     * @param orderSn
     * @param statusEnum
     */
    void updateOrderCommentStatus(String orderSn, String statusEnum);

    /**
     * 根据交易编号查询订单项
     *
     * @param tradeSn
     * @return
     */
    List<OrderDetailDTO> getOrderByTradeSn(String tradeSn);


    /**
     * 为某个订单的付款
     *
     * @param orderSn 订单编号
     * @param price 支付价格
     * @param returnTradeNo 第三方平台回传的支付单号
     * @param permission 权限 {@link com.enation.app.javashop.model.trade.cart.dos.OrderPermission}
     */
    void payOrder(String orderSn, Double price, String returnTradeNo,String permission);


    /**
     * 根据会员id读取我的所有订单数量
     *
     * @param memberId
     * @return
     */
    Integer getOrderNumByMemberID(Long memberId);


    /**
     * 根据会员id读取我的(评论状态)订单数量
     *
     * @param memberId
     * @param commentStatus 评论状态
     * @return
     */
    Integer getOrderCommentNumByMemberID(Long memberId, String commentStatus);

    /**
     * 读取订单状态的订单数
     *
     * @param memberId
     * @param sellerId
     * @return
     */
    OrderStatusNumVO getOrderStatusNum(Long memberId, Long sellerId);

    /**
     * 更新订单状态
     *
     * @param sn          订单号
     * @param orderStatus 订单状态
     * @return 是否更新成功
     */
    boolean updateOrderStatus(String sn, OrderStatusEnum orderStatus);

    /**
     * 更新交易状态
     *
     * @param sn          交易sn
     * @param orderStatus 交易状态
     * @return  是否更新成功
     */
    boolean updateTradeStatus(String sn, OrderStatusEnum orderStatus);

    /**
     * 更新订单项可退款金额
     * @param orderDO
     */
    void addOrderItemRefundPrice(OrderDO orderDO);

    /**
     * 更新订单项评论状态
     * @param orderSn
     * @param goodsId
     * @param commentStatus
     */
    void updateItemsCommentStatus(String orderSn, Long goodsId, CommentStatusEnum commentStatus);

    /**
     * 获取某订单的订单项
     *
     * @param orderSn
     * @return
     */
    List<OrderItemsDO> orderItems(String orderSn);

    /**
     * 根据订单编号读取一个订单详细
     * @param orderSn
     * @return
     */
    OrderDO getOrder(String orderSn);

    /**
     * 发货
     *
     * @param deliveryVO 运单</br>
     * @param permission 需要检测的订单权限
     */
    void ship(DeliveryVO deliveryVO, OrderPermission permission);

    /**
     * 获取几个月之内购买过相关商品的订单数据
     * 获取的订单数据只限已完成和已收货并且未删除的订单
     *
     * @param goodsId  商品id
     * @param memberId 会员id
     * @param month    月数 例：2代表2个月之内
     * @return
     */
    List<OrderDO> listOrderByGoods(Long goodsId, Long memberId, Integer month);

    /**
     * 获取订单商品信息
     *
     * @param orderSn 单号</br>
     */
    List<Map> getItemsPromotionTypeandNum(String  orderSn);

}
