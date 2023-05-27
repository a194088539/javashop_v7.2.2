package com.enation.app.javashop.service.trade.order.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.client.payment.PaymentClient;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.mapper.trade.order.OrderItemsMapper;
import com.enation.app.javashop.mapper.trade.order.OrderMapper;
import com.enation.app.javashop.mapper.trade.order.OrderQueryMapper;
import com.enation.app.javashop.model.aftersale.dos.AfterSaleServiceDO;
import com.enation.app.javashop.model.promotion.coupon.dos.CouponDO;
import com.enation.app.javashop.service.aftersale.AfterSaleQueryManager;
import com.enation.app.javashop.model.base.DomainHelper;
import com.enation.app.javashop.model.base.SettingGroup;
import com.enation.app.javashop.client.system.SettingClient;
import com.enation.app.javashop.model.payment.dos.PaymentMethodDO;
import com.enation.app.javashop.model.promotion.coupon.vo.GoodsCouponPrice;
import com.enation.app.javashop.model.promotion.fulldiscount.dos.FullDiscountGiftDO;
import com.enation.app.javashop.model.system.vo.SiteSetting;
import com.enation.app.javashop.model.errorcode.TradeErrorCode;
import com.enation.app.javashop.model.trade.complain.enums.ComplainSkuStatusEnum;
import com.enation.app.javashop.model.trade.order.dos.OrderDO;
import com.enation.app.javashop.model.trade.order.dos.OrderItemsDO;
import com.enation.app.javashop.model.trade.order.dos.OrderMetaDO;
import com.enation.app.javashop.model.trade.order.dto.OrderDetailQueryParam;
import com.enation.app.javashop.model.trade.order.dto.OrderQueryParam;
import com.enation.app.javashop.model.trade.order.enums.*;
import com.enation.app.javashop.model.trade.order.vo.*;
import com.enation.app.javashop.service.trade.order.OrderMetaManager;
import com.enation.app.javashop.service.trade.order.OrderQueryManager;
import com.enation.app.javashop.model.trade.order.dto.OrderDetailDTO;
import com.enation.app.javashop.model.trade.order.dto.OrderSkuDTO;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.util.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单查询业务实现类
 *
 * @author Snow create in 2018/5/14
 * @version v2.0
 * @since v7.0.0
 */
@Service
public class OrderQueryManagerImpl implements OrderQueryManager {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemsMapper orderItemsMapper;

    @Autowired
    private OrderQueryMapper orderQueryMapper;

    @Autowired
    private OrderMetaManager orderMetaManager;

    @Autowired
    private SettingClient settingClient;

    @Autowired
    private PaymentClient paymentClient;

    @Autowired
    private DomainHelper domainHelper;

    @Autowired
    private AfterSaleQueryManager afterSaleQueryManager;


    @Override
    public WebPage list(OrderQueryParam paramDTO) {

        // 时间转换
        Long startTime = paramDTO.getStartTime();
        Long endTime = paramDTO.getEndTime();
        if (startTime != null) {
            String startDay = DateUtil.toString(startTime, "yyyy-MM-dd");
            startTime = DateUtil.getDateline(startDay + " 00:00:00", "yyyy-MM-dd HH:mm:ss");
            paramDTO.setStartTime(startTime);
        }

        if (endTime != null) {
            String endDay = DateUtil.toString(endTime, "yyyy-MM-dd");
            endTime = DateUtil.getDateline(endDay + " 23:59:59", "yyyy-MM-dd HH:mm:ss");
            paramDTO.setEndTime(endTime);
        }

        IPage iPage = orderQueryMapper.selectOrderQueryPage(new Page(paramDTO.getPageNo(), paramDTO.getPageSize()), paramDTO);

        //订单自动取消天数
        int cancelLeftDay = getCancelLeftDay();

        // 转为VO
        List<OrderDO> orderList = iPage.getRecords();
        List<OrderLineVO> lineList = new ArrayList();
        for (OrderDO orderDO : orderList) {
            OrderLineVO line = new OrderLineVO(orderDO);

            //如果未付款并且是在线支付则显示取消时间
            if (!PayStatusEnum.PAY_YES.value().equals(orderDO.getPayStatus())
                    && PaymentTypeEnum.ONLINE.value().equals(orderDO.getPaymentType())
                    && !OrderStatusEnum.CANCELLED.value().equals(orderDO.getOrderStatus())) {
                //计算自动取消剩余时间
                Long leftTime = getCancelLeftTime(line.getCreateTime(), cancelLeftDay);
                line.setCancelLeftTime(leftTime);


            } else {
                line.setCancelLeftTime(0L);
            }

            //默认订单是不支持原路退款操作的
            line.setIsRetrace(false);

            if (orderDO.getBalance() > 0) {
                line.setIsRetraceBalance(true);
            } else {
                line.setIsRetraceBalance(false);
            }

            //如果订单已付款并且是在线支付的，那么需要获取订单的支付方式判断是否支持原路退款操作
            if (PayStatusEnum.PAY_YES.value().equals(orderDO.getPayStatus())
                    && PaymentTypeEnum.ONLINE.value().equals(orderDO.getPaymentType())
                    && orderDO.getPaymentPluginId() != null) {
                //获取订单的支付方式
                PaymentMethodDO paymentMethodDO = this.paymentClient.getByPluginId(orderDO.getPaymentPluginId());
                if (paymentMethodDO != null && paymentMethodDO.getIsRetrace() == 1) {
                    line.setIsRetrace(true);
                }
            }

            if (OrderTypeEnum.PINTUAN.name().equals(line.getOrderType())) {

                //如果订单
                int waitNums = convertOwesNums(orderDO);
                line.setWaitingGroupNums(waitNums);
                if (waitNums == 0 && PayStatusEnum.PAY_YES.value().equals(line.getPayStatus())) {
                    line.setPingTuanStatus("已成团");
                } else if (OrderStatusEnum.CANCELLED.value().equals(line.getOrderStatus())) {
                    line.setPingTuanStatus("未成团");
                } else {
                    line.setPingTuanStatus("待成团");
                }
            }

            lineList.add(line);
        }


        // 生成新的Page
        WebPage<OrderLineVO> linePage = new WebPage(paramDTO.getPageNo(), iPage.getTotal(), paramDTO.getPageSize(), lineList);

        return linePage;
    }

    @Override
    public List<OrderLineVO> export(OrderQueryParam paramDTO) {

        // 时间转换
        Long startTime = paramDTO.getStartTime();
        Long endTime = paramDTO.getEndTime();
        if (startTime != null) {
            String startDay = DateUtil.toString(startTime, "yyyy-MM-dd");
            startTime = DateUtil.getDateline(startDay + " 00:00:00", "yyyy-MM-dd HH:mm:ss");
            paramDTO.setStartTime(startTime);
        }

        if (endTime != null) {
            String endDay = DateUtil.toString(endTime, "yyyy-MM-dd");
            endTime = DateUtil.getDateline(endDay + " 23:59:59", "yyyy-MM-dd HH:mm:ss");
            paramDTO.setEndTime(endTime);
        }

        List<OrderDO> orderDOList = orderQueryMapper.selectExportList(paramDTO);


        // 转为VO
        List<OrderLineVO> lineList = new ArrayList();
        for (OrderDO orderDO : orderDOList) {
            OrderLineVO line = new OrderLineVO(orderDO);
            lineList.add(line);
        }

        return lineList;
    }

    /**
     * 读取订单自动取消天数
     *
     * @return
     */
    private int getCancelLeftDay() {
        String settingVOJson = this.settingClient.get(SettingGroup.TRADE);
        OrderSettingVO settingVO = JsonUtil.jsonToObject(settingVOJson, OrderSettingVO.class);
        int day = settingVO.getCancelOrderDay();
        return day;
    }


    private Long getCancelLeftTime(Long createTime, int cancelLeftDay) {

        Long cancelTime = createTime + cancelLeftDay * 24 * 60 * 60;
        Long now = DateUtil.getDateline();
        Long leftTime = cancelTime - now;
        if (leftTime < 0) {
            leftTime = 0L;
        }
        return leftTime;

    }


    /**
     * 转换拼团的 待成团人数
     *
     * @param orderDO 订单do
     * @return 待成团人数
     */
    private int convertOwesNums(OrderDO orderDO) {
        //取出个性化数据
        String orderData = orderDO.getOrderData();
        Gson gson = new GsonBuilder().create();
        if (!StringUtil.isEmpty(orderData)) {

            //将个性化数据转为map
            Map map = gson.fromJson(orderData, HashMap.class);

            //转换拼团个性化数据
            String json = (String) map.get(OrderDataKey.pintuan.name());
            if (!StringUtil.isEmpty(json)) {
                Map pintuanMap = gson.fromJson(json, HashMap.class);
                Double nums = (Double) pintuanMap.get("owesPersonNums");
                return nums.intValue();
            }
        }

        return 0;


    }


    @Override
    public OrderDetailVO getModel(String orderSn, OrderDetailQueryParam queryParam) {

        OrderDO orderDO = new QueryChainWrapper<>(orderMapper)
                //拼接订单编号查询条件
                .eq("sn", orderSn)
                //如果商家id不为空，拼接商家id查询条件
                .eq(queryParam != null && queryParam.getSellerId() != null, "seller_id", queryParam == null ? null : queryParam.getSellerId())
                //如果买家id不为空，拼接买家id查询条件
                .eq(queryParam != null && queryParam.getBuyerId() != null, "member_id", queryParam == null ? null : queryParam.getBuyerId())
                //查询单个对象
                .one();

        if (orderDO == null) {
            throw new ServiceException(TradeErrorCode.E453.code(), "订单不存在");
        }

        OrderDetailVO detailVO = new OrderDetailVO();
        BeanUtils.copyProperties(orderDO, detailVO);
        //初始化sku信息
        List<OrderSkuVO> skuList = JsonUtil.jsonToList(orderDO.getItemsJson(), OrderSkuVO.class);

        //订单商品原价
        double goodsOriginalPrice = 0.00;

        for (OrderSkuVO skuVO : skuList) {
            //设置商品的可操作状态
            skuVO.setGoodsOperateAllowableVO(new GoodsOperateAllowable(ShipStatusEnum.valueOf(orderDO.getShipStatus()),
                    OrderServiceStatusEnum.valueOf(skuVO.getServiceStatus()), PayStatusEnum.valueOf(orderDO.getPayStatus()),
                    skuVO.getComplainStatus() == null ? ComplainSkuStatusEnum.EXPIRED : ComplainSkuStatusEnum.valueOf(skuVO.getComplainStatus())));

            //计算订单商品原价总和
            goodsOriginalPrice = CurrencyUtil.add(goodsOriginalPrice, CurrencyUtil.mul(skuVO.getOriginalPrice(), skuVO.getNum()));
        }

        detailVO.setOrderSkuList(skuList);

        // 初始化订单允许状态
        OrderOperateAllowable operateAllowableVO = new OrderOperateAllowable(detailVO);

        detailVO.setOrderOperateAllowableVO(operateAllowableVO);


        List<OrderMetaDO> metalList = this.orderMetaManager.list(orderSn);

        Double couponPrice = 0D;
        Double orderMetaCashBack = 0D;
        for (OrderMetaDO metaDO : metalList) {

            //满减金额
            if (OrderMetaKeyEnum.CASH_BACK.name().equals(metaDO.getMetaKey())) {
                if (!StringUtil.isEmpty(metaDO.getMetaValue())) {
                    orderMetaCashBack = Double.valueOf(metaDO.getMetaValue());
                }
            }

            //订单的赠品信息
            if (OrderMetaKeyEnum.GIFT.name().equals(metaDO.getMetaKey())) {
                String giftJson = metaDO.getMetaValue();
                if (!StringUtil.isEmpty(giftJson)) {
                    List<FullDiscountGiftDO> giftList = JsonUtil.jsonToList(giftJson, FullDiscountGiftDO.class);
                    detailVO.setGiftList(giftList);
                }
            }

            //使用的积分
            if (OrderMetaKeyEnum.POINT.name().equals(metaDO.getMetaKey())) {
                String pointStr = metaDO.getMetaValue();
                int point = 0;
                if (!StringUtil.isEmpty(pointStr)) {
                    point = Integer.valueOf(pointStr);
                }

                detailVO.setUsePoint(point);

            }


            //赠送的积分
            if (OrderMetaKeyEnum.GIFT_POINT.name().equals(metaDO.getMetaKey())) {
                String giftPointStr = metaDO.getMetaValue();
                int giftPoint = 0;
                if (!StringUtil.isEmpty(giftPointStr)) {
                    giftPoint = Integer.valueOf(giftPoint);
                }

                detailVO.setGiftPoint(giftPoint);

            }


            //满减金额
            if (OrderMetaKeyEnum.FULL_MINUS.name().equals(metaDO.getMetaKey())) {
                Double fullMinus = 0D;
                if (!StringUtil.isEmpty(metaDO.getMetaValue())) {
                    fullMinus = Double.valueOf(metaDO.getMetaValue());
                }
                detailVO.setFullMinus(fullMinus);
            }


            if (OrderMetaKeyEnum.COUPON.name().equals(metaDO.getMetaKey())) {

                String couponJson = metaDO.getMetaValue();
                if (!StringUtil.isEmpty(couponJson)) {
                    List<CouponDO> couponList = JsonUtil.jsonToList(couponJson, CouponDO.class);
                    if (couponList != null && !couponList.isEmpty()) {
                        CouponDO couponVO = couponList.get(0);
                        detailVO.setGiftCoupon(couponVO);
                    }

                }

            }

            //优惠券抵扣金额
            if (OrderMetaKeyEnum.COUPON_PRICE.name().equals(metaDO.getMetaKey())) {
                String couponPriceStr = metaDO.getMetaValue();

                if (!StringUtil.isEmpty(couponPriceStr) && !"null".equals(couponPriceStr)
                        && !"0.0".equals(couponPriceStr)) {
                    List<GoodsCouponPrice> couponList = JsonUtil.jsonToList(couponPriceStr, GoodsCouponPrice.class);
                    for (GoodsCouponPrice goodsCouponPrice : couponList) {
                        couponPrice += goodsCouponPrice.getCouponPrice();
                    }
                }
                //设置优惠券抵扣金额
                detailVO.setCouponPrice(couponPrice);

            }


        }

        //订单商品原总价 = 订单实际支付价格 - 运费 + 返现金额 + 优惠券优惠金额
        Double goodsPrice = CurrencyUtil.add(CurrencyUtil.add(CurrencyUtil.sub(detailVO.getOrderPrice(), detailVO.getShippingPrice()), orderMetaCashBack), couponPrice);
        detailVO.setGoodsOriginalTotalPrice(goodsPrice);

        //设置订单的返现金额
        Double cashBack = CurrencyUtil.sub(detailVO.getDiscountPrice(), couponPrice);
        detailVO.setCashBack(cashBack);

        //当商品总价(优惠后的商品单价*数量+总优惠金额)超过商品原价总价
        if (detailVO.getGoodsPrice().doubleValue() > goodsOriginalPrice) {
            detailVO.setGoodsPrice(CurrencyUtil.sub(CurrencyUtil.sub(goodsOriginalPrice, cashBack), couponPrice));
        }
        if (OrderTypeEnum.PINTUAN.name().equals(detailVO.getOrderType())) {

            //如果订单
            int waitNums = convertOwesNums(orderDO);
            if (waitNums == 0 && PayStatusEnum.PAY_YES.value().equals(detailVO.getPayStatus())) {
                detailVO.setPingTuanStatus("已成团");
            } else if (OrderStatusEnum.CANCELLED.value().equals(detailVO.getOrderStatus())) {
                detailVO.setPingTuanStatus("未成团");
            } else {
                detailVO.setPingTuanStatus("待成团");
            }
        }

        //默认订单是不支持原路退款操作的
        detailVO.setIsRetrace(false);

        //如果订单已付款并且是在线支付的，那么需要获取订单的支付方式判断是否支持原路退款操作
        if (PayStatusEnum.PAY_YES.value().equals(orderDO.getPayStatus())
                && PaymentTypeEnum.ONLINE.value().equals(orderDO.getPaymentType())
                && orderDO.getPaymentPluginId() != null) {
            //获取订单的支付方式
            PaymentMethodDO paymentMethodDO = this.paymentClient.getByPluginId(orderDO.getPaymentPluginId());
            if (paymentMethodDO != null && paymentMethodDO.getIsRetrace() == 1) {
                detailVO.setIsRetrace(true);
            }
        }

        return detailVO;
    }

    /**
     * 读取一个订单详细
     *
     * @param orderId
     * @return
     */
    @Override
    public OrderDO getModel(Long orderId) {
        return orderMapper.selectById(orderId);
    }

    @Override
    public OrderDO getOrder(String orderSn) {

        OrderDO orderDO = new QueryChainWrapper<>(orderMapper)
                //按订单编号查询
                .eq("sn", orderSn)
                //查询单个对象
                .one();

        return orderDO;
    }

    @Override
    public OrderStatusNumVO getOrderStatusNum(Long memberId, Long sellerId) {

        QueryWrapper<OrderDO> queryWrapper = new QueryWrapper<OrderDO>()
                //查询字段
                .select("order_type", "order_status", "pay_status", "ship_status", "payment_type", "comment_status", "count(order_id) as count")
                //如果商家id不为空，拼接商家id查询条件
                .eq(sellerId != null, "seller_id", sellerId)
                //如果买家id不为空，拼接买家id查询条件
                .eq(memberId != null, "member_id", memberId)
                //分组
                .groupBy("order_status,pay_status,ship_status,comment_status,payment_type,order_type");

        List<Map<String, Object>> list = orderMapper.selectMaps(queryWrapper);

        OrderStatusNumVO numVO = new OrderStatusNumVO();
        numVO.setWaitShipNum(0);
        numVO.setWaitPayNum(0);
        numVO.setWaitRogNum(0);
        numVO.setCompleteNum(0);
        numVO.setCancelNum(0);
        numVO.setWaitCommentNum(0);
        numVO.setAllNum(orderMapper.selectCount(new QueryWrapper<OrderDO>()
                //如果商家id不为空，拼接商家id查询条件
                .eq(sellerId != null, "seller_id", sellerId)
                //如果买家id不为空，拼接买家id查询条件
                .eq(memberId != null, "member_id", memberId)));

        // 支付状态未支付，订单状态已确认，为待付款订单
        for (Map map : list) {
            boolean flag = (OrderStatusEnum.CONFIRM.value().equals(map.get("order_status").toString()) && !"COD".equals(map.get("payment_type").toString()))
                    || (OrderStatusEnum.ROG.value().equals(map.get("order_status").toString()) && "COD".equals(map.get("payment_type").toString()));
            if (flag) {
                numVO.setWaitPayNum(numVO.getWaitPayNum() + (null == map.get("count") ? 0 : Integer.parseInt(map.get("count").toString())));
            }

            // 物流状态为未发货，订单状态为已收款，为待发货订单
            flag = (OrderStatusEnum.CONFIRM.value().equals(map.get("order_status").toString()) && "COD".equals(map.get("payment_type").toString()) && OrderTypeEnum.NORMAL.name().equals(map.get("order_type").toString()))
                    || (OrderStatusEnum.PAID_OFF.value().equals(map.get("order_status").toString()) && !"COD".equals(map.get("payment_type").toString()) && OrderTypeEnum.NORMAL.name().equals(map.get("order_type").toString()))
                    || (OrderTypeEnum.PINTUAN.name().equals(map.get("order_type").toString()) && OrderStatusEnum.FORMED.value().equals(map.get("order_status").toString()));
            if (flag) {
                numVO.setWaitShipNum(numVO.getWaitShipNum() + (null == map.get("count") ? 0 : Integer.parseInt(map.get("count").toString())));
            }

            // 订单状态为已发货，为待收货订单
            if (OrderStatusEnum.SHIPPED.value().equals(map.get("order_status").toString())) {
                numVO.setWaitRogNum(numVO.getWaitRogNum() + (null == map.get("count") ? 0 : Integer.parseInt(map.get("count").toString())));
            }

            // 订单状态为已取消，为已取消订单
            if (OrderStatusEnum.CANCELLED.value().equals(map.get("order_status").toString())) {
                numVO.setCancelNum(numVO.getCancelNum() + (null == map.get("count") ? 0 : Integer.parseInt(map.get("count").toString())));
            }

            // 订单状态为已完成，为已完成订单
            if (OrderStatusEnum.COMPLETE.value().equals(map.get("order_status").toString())) {
                numVO.setCompleteNum(numVO.getCompleteNum() + (null == map.get("count") ? 0 : Integer.parseInt(map.get("count").toString())));
            }

            // 评论状态为未评论，订单状态为已收货，为待评论订单
            if (CommentStatusEnum.UNFINISHED.value().equals(map.get("comment_status").toString()) && OrderStatusEnum.ROG.value().equals(map.get("order_status").toString())) {
                numVO.setWaitCommentNum(numVO.getWaitCommentNum() + (null == map.get("count") ? 0 : Integer.parseInt(map.get("count").toString())));
            }
        }

        // 申请售后，但未完成售后的订单
        numVO.setRefundNum(this.afterSaleQueryManager.getAfterSaleCount(memberId, sellerId));

        return numVO;

    }

    @Override
    public List<OrderFlowNode> getOrderFlow(String orderSn) {

        OrderDetailVO orderDetailVO = this.getModel(orderSn, null);
        String orderStatus = orderDetailVO.getOrderStatus();
        String serviceStatus = orderDetailVO.getServiceStatus();
        String paymentType = orderDetailVO.getPaymentType();

        //如果订单售后状态是已申请取消订单
        if (serviceStatus.equals(OrderServiceStatusEnum.APPLY.value())) {
            AfterSaleServiceDO serviceDO = this.afterSaleQueryManager.getCancelService(orderSn);
            List<OrderFlowNode> resultFlow = getFlow(OrderFlow.getCancelOrderFlow(), serviceDO.getServiceStatus());
            return resultFlow;
        }

        //如果订单状态是已取消
        if (orderStatus.equals(OrderStatusEnum.CANCELLED.name())) {
            return OrderFlow.getCancelFlow();
        }

        //如果订单状态是出库失败
        if (orderStatus.equals(OrderStatusEnum.INTODB_ERROR.name())) {
            return OrderFlow.getIntodbErrorFlow();
        }

        List<OrderFlowNode> resultFlow = getFlow(OrderFlow.getFlow(orderDetailVO.getOrderType(), paymentType), orderStatus);
        return resultFlow;
    }


    @Override
    public Integer getOrderNumByMemberId(Long memberId) {

        Integer num = new QueryChainWrapper<>(orderMapper)
                //按买家id查询
                .eq("member_id", memberId)
                //查询数量
                .count();

        return num;
    }


    @Override
    public Integer getOrderCommentNumByMemberId(Long memberId, String commentStatus) {

        Integer num = new QueryChainWrapper<>(orderMapper)
                //拼接买家id查询条件
                .eq("member_id", memberId)
                //查询发货状态为已发货
                .eq("ship_status", ShipStatusEnum.SHIP_ROG)
                //拼接评论是否完成查询条件
                .eq("comment_status", commentStatus)
                //查询数量
                .count();

        return num;
    }


    @Override
    public List<OrderDetailDTO> getOrderByTradeSn(String tradeSn) {
        List<OrderDetailVO> orderDetailVOList = this.getOrderByTradeSn(tradeSn, null);

        List<OrderDetailDTO> orderDetailDTOList = new ArrayList<>();
        for (OrderDetailVO orderDetailVO : orderDetailVOList) {
            OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
            BeanUtils.copyProperties(orderDetailVO, orderDetailDTO);
            orderDetailDTOList.add(orderDetailDTO);
        }
        return orderDetailDTOList;
    }

    @Override
    public List<OrderDetailVO> getOrderByTradeSn(String tradeSn, Long memberId) {

        List<OrderDetailVO> orderDetailVOList = orderQueryMapper.getOrderByTradeSn(tradeSn);

        if (orderDetailVOList == null) {
            return new ArrayList<>();
        }
        return orderDetailVOList;
    }

    @Override
    public List<OrderItemsDO> orderItems(String orderSn) {

        List<OrderItemsDO> list = new QueryChainWrapper<>(orderItemsMapper)
                //按订单编号查询
                .eq("order_sn", orderSn)
                //列表查询
                .list();

        return list;
    }

    @Override
    public OrderDetailDTO getModel(String orderSn) {
        OrderDetailVO orderDetailVO = this.getModel(orderSn, null);
        OrderDetailDTO detailDTO = new OrderDetailDTO();
        BeanUtils.copyProperties(orderDetailVO, detailDTO);
        detailDTO.setOrderSkuList(new ArrayList<>());

        for (OrderSkuVO skuVO : orderDetailVO.getOrderSkuList()) {
            OrderSkuDTO skuDTO = new OrderSkuDTO();
            BeanUtil.copyProperties(skuVO, skuDTO);
            detailDTO.getOrderSkuList().add(skuDTO);
        }

        String json = this.orderMetaManager.getMetaValue(detailDTO.getSn(), OrderMetaKeyEnum.GIFT);
        List<FullDiscountGiftDO> giftList = JsonUtil.jsonToList(json, FullDiscountGiftDO.class);
        detailDTO.setGiftList(giftList);

        return detailDTO;
    }

    @Override
    public double getOrderRefundPrice(String orderSn) {
        double refundPrice = 0.00;
        List<OrderItemsDO> itemsDOList = this.orderItems(orderSn);
        if (itemsDOList != null && itemsDOList.size() != 0) {
            for (OrderItemsDO itemsDO : itemsDOList) {
                refundPrice = CurrencyUtil.add(refundPrice, itemsDO.getRefundPrice());
            }
        }
        return refundPrice;
    }

    @Override
    public List<OrderDO> listOrderByGoods(Long goodsId, Long memberId, Integer month) {

        return orderQueryMapper.listOrderByGoods(DateUtil.getBeforeMonthDateline(month), goodsId, memberId);
    }

    @Override
    public InvoiceVO getInvoice(Long orderId) {
        //获取订单信息
        OrderDO orderDO = this.getModel(orderId);
        //获取sku信息
        List<OrderSkuVO> skuList = JsonUtil.jsonToList(orderDO.getItemsJson(), OrderSkuVO.class);
        //获取站点信息
        String siteJson = settingClient.get(SettingGroup.SITE);
        SiteSetting siteSetting = JsonUtil.jsonToObject(siteJson, SiteSetting.class);
        //组织返回数据
        InvoiceVO invoiceVO = new InvoiceVO();
        invoiceVO.setOrderSkuList(skuList);
        invoiceVO.setLogo(siteSetting.getLogo());
        invoiceVO.setSiteName(siteSetting.getSiteName());
        invoiceVO.setSiteAddress(domainHelper.getBuyerDomain());
        invoiceVO.setAddress(orderDO.getShipAddr());
        invoiceVO.setConsignee(orderDO.getShipName());
        invoiceVO.setRegion(orderDO.getShipProvince() + orderDO.getShipCity() + orderDO.getShipCounty() + orderDO.getShipTown());
        invoiceVO.setOrderCreateTime(orderDO.getCreateTime());
        invoiceVO.setCreateTime(DateUtil.getDateline());
        invoiceVO.setMemberName(orderDO.getMemberName());
        invoiceVO.setSn(orderDO.getSn());
        invoiceVO.setPhone(orderDO.getShipMobile());
        return invoiceVO;
    }

    @Override
    public List<Map> getItemsPromotionTypeandNum(String orderSn) {

        List<Map> list = orderQueryMapper.getItemsPromotionTypeandNum(orderSn);

        return list;
    }

    /**
     * 获取订单相关流程公共方法
     *
     * @param resultFlow 流程结构集合
     * @param status     状态
     * @return
     */
    private List<OrderFlowNode> getFlow(List<OrderFlowNode> resultFlow, String status) {
        boolean isEnd = false;
        for (OrderFlowNode flow : resultFlow) {

            flow.setShowStatus(1);
            if (isEnd) {
                flow.setShowStatus(0);
            }

            if (flow.getOrderStatus().equals(status)) {
                isEnd = true;
            }

        }
        return resultFlow;
    }

}
