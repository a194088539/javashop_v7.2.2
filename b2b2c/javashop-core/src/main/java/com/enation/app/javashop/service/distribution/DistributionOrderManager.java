package com.enation.app.javashop.service.distribution;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.distribution.dos.DistributionOrderDO;
import com.enation.app.javashop.model.distribution.vo.DistributionOrderVO;
import com.enation.app.javashop.model.distribution.vo.DistributionSellbackOrderVO;
import com.enation.app.javashop.model.trade.order.dos.OrderDO;


/**
 * 分销Order Manager接口
 *
 * @author Chopper
 * @version v1.0
 * @since v6.1
 * 2016年10月2日 下午5:24:14
 */
public interface DistributionOrderManager {

    /**
     * 根据sn获得分销商订单详情
     *
     * @param orderSn 订单编号
     * @return FxOrderDO
     */
    DistributionOrderDO getModelByOrderSn(String orderSn);

    /**
     * 根据id获得分销商订单详情
     *
     * @param orderId 订单id
     * @return FxOrderDO
     */
    DistributionOrderDO getModel(Long orderId);

    /**
     * 保存一条数据
     * @param distributionOrderDO
     * @return
     */
    DistributionOrderDO add(DistributionOrderDO distributionOrderDO);

    /**
     * 通过订单id，计算出各个级别的返利金额并保存到数据库
     *
     * @param orderSn 订单编号
     * @return 计算结果 true 成功， false 失败
     */
    boolean calCommission(String orderSn);

    /**
     * 计算退款时需要退的返利金额
     *
     * @param orderSn 订单sn
     * @param price    退款金额
     * @return 计算结果 true 成功， false 失败
     */
    boolean calReturnCommission(String orderSn, double price);

    /**
     * 通过订单id，把各个级别的返利金额增加到分销商冻结金额中
     *
     * @param orderSn 订单sn
     * @return 操作结果 true 成功， false 失败
     */
    boolean addDistributorFreeze(String orderSn);

    /**
     * 分销商退货订单分页
     * @param pagesize
     * @param page
     * @param memberId
     * @param billId
     * @return
     */
    WebPage<DistributionSellbackOrderVO> pageSellBackOrder(Long pagesize, Long page, Long memberId, Long billId);


    /**
     * 结算单订单查询
     *
     * @param pageSize
     * @param page
     * @param memberId
     * @param billId
     * @return
     */
    WebPage<DistributionOrderVO> pageDistributionOrder(Long pageSize, Long page, Long memberId, Long billId);


    /**
     * 结算单订单查询
     *
     * @param pageSize
     * @param page
     * @param memberId
     * @param billId
     * @return
     */
    WebPage<DistributionOrderVO> pageDistributionTotalBillOrder(Long pageSize, Long page, Long memberId, Long billId);

    /**
     * 根据会员id获取营业额
     *
     * @param memberId 会员id
     * @return 营业额
     */
    double getTurnover(Long memberId);

    /**
     * 根据购买人增加上级人员订单数量
     *
     * @param buyMemberId 购买人会员id
     */
    void addOrderNum(Long buyMemberId);

    /**
     * 结算订单
     * @param order
     */
    void confirm(OrderDO order);
}
