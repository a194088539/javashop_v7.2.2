package com.enation.app.javashop.service.trade.order.impl;

import com.enation.app.javashop.client.goods.GoodsClient;
import com.enation.app.javashop.client.member.MemberAddressClient;
import com.enation.app.javashop.client.member.MemberClient;
import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import com.enation.app.javashop.framework.rabbitmq.MessageSender;
import com.enation.app.javashop.framework.security.model.Buyer;
import com.enation.app.javashop.framework.sncreator.SnCreator;
import com.enation.app.javashop.framework.util.BeanUtil;
import com.enation.app.javashop.framework.util.CurrencyUtil;
import com.enation.app.javashop.framework.util.StringUtil;
import com.enation.app.javashop.model.member.dos.MemberAddress;
import com.enation.app.javashop.model.promotion.coupon.enums.CouponUseScope;
import com.enation.app.javashop.model.promotion.coupon.vo.GoodsCouponPrice;
import com.enation.app.javashop.model.trade.cart.enums.CheckedWay;
import com.enation.app.javashop.model.trade.cart.vo.CartSkuVO;
import com.enation.app.javashop.model.trade.cart.vo.CartVO;
import com.enation.app.javashop.model.trade.cart.vo.CartView;
import com.enation.app.javashop.model.trade.cart.vo.CouponVO;
import com.enation.app.javashop.model.trade.order.dto.OrderDTO;
import com.enation.app.javashop.model.trade.order.enums.OrderTypeEnum;
import com.enation.app.javashop.model.trade.order.vo.*;
import com.enation.app.javashop.service.trade.cart.CartOriginDataManager;
import com.enation.app.javashop.service.trade.cart.CartReadManager;
import com.enation.app.javashop.service.trade.cart.cartbuilder.ScriptProcess;
import com.enation.app.javashop.service.trade.order.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 交易创建业务类实现
 * @author: liuyulei
 * @create: 2020/3/19 18:05
 * @version:1.0
 * @since: 7.2.0
 **/
@Service
@Primary
public class TradeManagerImpl implements TradeManager {
    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Autowired
    protected CheckoutParamManager checkoutParamManager;

    @Autowired
    protected CartReadManager cartReadManager;

    @Autowired
    protected ShippingManager shippingManager;

    @Autowired
    protected GoodsClient goodsClient;

    @Autowired
    SnCreator snCreator;

    @Autowired
    protected ScriptProcess scriptProcess;

    @Autowired
    protected MemberAddressClient memberAddressClient;

    @Autowired
    protected MemberClient memberClient;

    @Autowired
    protected TradeIntodbManager tradeIntodbManager;

    @Autowired
    private TradeCenterManager tradeCenterManager;

    @Autowired
    private CartOriginDataManager cartOriginDataManager;



    @Autowired
    private Cache cache;

    @Autowired
    private MessageSender messageSender;


    @Autowired
    private OrderCenterManager orderCenterManager;

    private final String sellerKey = "seller_";
    private final String couponPriceKey = "total_coupon_price_";
    private final String isSite = "is_site";


    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public TradeVO createTrade(String client, CheckedWay way) {

        CheckoutParamVO param = checkoutParamManager.getParam();
        CartView cartView = this.cartReadManager.getCheckedItems(way);
        MemberAddress memberAddress = this.memberAddressClient.getModel(param.getAddressId());

        //创建交易检测者
        TradeValidator tradeValidator = new DefaultTradeValidator(param, cartView, memberAddress,param.getPaymentType()).setTradeSnCreator(snCreator).setGoodsClient(goodsClient)
                .setMemberClient(memberClient).setShippingManager(shippingManager).setScriptProcess(scriptProcess)
                .setCheckoutParamManager(checkoutParamManager);
        //检测配送范围==>检测商品的有效性==>检测促销的时效性
        tradeValidator.checkShipRange().checkGoods().checkPromotion();
        //获取交易参数
        TradeParam tradeParam = getTradeParam(client, param, cartView);
        //创建交易对象
        TradeVO tradeVO = tradeCenterManager.createTrade(tradeParam);
        //获取购物车列表
        List<CartVO> cartList = cartView.getCartList();
        //获取使用的优惠券信息
        Map sellerUseMap = getSellerCouponMap(cartView);
        //设置订单列表
        List<OrderDTO> orderList = new ArrayList<>();
        //循环购物车列表
        for (CartVO cartVO : cartList) {
            OrderParam orderParam = convertSku(tradeVO, sellerUseMap, cartVO);
            //创建订单
            OrderDTO orderDTO = this.orderCenterManager.createOrder(orderParam);
            orderList.add(orderDTO);
        }
        //设置交易订单列表
        tradeVO.setOrderList(orderList);

        //交易入库
        tradeIntodbManager.intoDB(tradeVO);

        //清除已购买的商品购物车数据
        cartOriginDataManager.cleanChecked(way);

        //清空备注信息
        this.checkoutParamManager.setRemark("");
        //清空发票信息
        this.checkoutParamManager.deleteReceipt();
        //清空客户端类型
        this.checkoutParamManager.setClientType("");


        return tradeVO;


    }

    /**
     * 转换订单sku数据
     *
     * @param tradeVO      交易VO
     * @param sellerUseMap 使用的优惠券信息
     * @param cartVO       购物车
     * @return 订单参数
     */
    public OrderParam convertSku(TradeVO tradeVO, Map sellerUseMap, CartVO cartVO) {
        OrderParam orderParam = new OrderParam();
        //计算该商家可以分享的优惠券的总金额
        orderParam.setIsSiteCoupon(false);
        if (sellerUseMap != null) {
            Double couponTotalPrice = (Double) sellerUseMap.get(couponPriceKey + cartVO.getSellerId());
            List<GoodsCouponPrice> couponGoodsList = (List<GoodsCouponPrice>) sellerUseMap.get(sellerKey + cartVO.getSellerId());
            //获取是否为平台优惠券
            Boolean site = (Boolean) sellerUseMap.get(isSite);
            orderParam.setIsSiteCoupon(site);
            //设置订单可分享的优惠券金额
            orderParam.setCouponTotalPrice(couponTotalPrice);
            orderParam.setCouponGoodsList(couponGoodsList);
        }

        //转换sku数据格式
        List<OrderSkuVO> skuParamVOS = new ArrayList<>();
        for (CartSkuVO cartSkuVO : cartVO.getSkuList()) {
            OrderSkuVO orderSkuVO = new OrderSkuVO();
            BeanUtil.copyProperties(cartSkuVO, orderSkuVO);
            skuParamVOS.add(orderSkuVO);
        }
        //填充订单参数
        orderParam.setSkuParam(skuParamVOS);
        orderParam.setTradeSn(tradeVO.getTradeSn());
        orderParam.setSellerId(cartVO.getSellerId());
        orderParam.setSellerName(cartVO.getSellerName());
        orderParam.setGiftPoint(cartVO.getGiftPoint());
        orderParam.setGiftJson(cartVO.getGiftJson());
        orderParam.setGoodsNum(cartVO.getSkuList().size());
        orderParam.setRemark(tradeVO.getRemark());
        orderParam.setReceiveTime(tradeVO.getReceiveTime());
        orderParam.setMemberId(tradeVO.getMemberId());
        orderParam.setMemberName(tradeVO.getMemberName());
        orderParam.setPrice(cartVO.getPrice());
        orderParam.setConsignee(tradeVO.getConsignee());
        orderParam.setPaymentType(tradeVO.getPaymentType());
        orderParam.setReceiptHistory(tradeVO.getReceipt());
        orderParam.setClientType(tradeVO.getClientType());
        orderParam.setNeedReceipt(0);
        if (tradeVO.getReceipt() != null && !StringUtil.isEmpty(tradeVO.getReceipt().getReceiptTitle())) {
            orderParam.setNeedReceipt(1);
        }
        orderParam.setShippingId(cartVO.getShippingTypeId());
        orderParam.setShippingType(cartVO.getShippingTypeName());
        //默认为普通商品订单
        orderParam.setOrderType(OrderTypeEnum.NORMAL.name());
        return orderParam;
    }

    /**
     * 将计算商品中可以使用优惠券的组装成map
     *
     * @param cartView
     * @return
     */
    private Map getSellerCouponMap(CartView cartView) {

        //seller_1:List<GoodsCouponPrice>,total_coupon_price_1:Double
        Map sellerUseMap = new HashMap<>();
        sellerUseMap.put(isSite, false);
        if (cartView.getCouponList() != null && cartView.getCouponList().size() > 0) {
            for (CouponVO couponVO : cartView.getCouponList()) {
                if (couponVO.getSelected() == 1) {

                    Map map = getEnableGoodsPrice(couponVO, cartView.getCartList());

                    //1 获取能使用该优惠券的商品的总金额
                    Double goodsTotalPrice = (Double) map.get("totalPrice");
                    //可以使用该优惠券的商品
                    List<GoodsCouponPrice> goodsList = (List<GoodsCouponPrice>) map.get("goods");
                    Double caculateCouponPrice = 0D;
                    int i = 0;
                    for (GoodsCouponPrice goodsCouponPrice : goodsList) {
                        Double couponTotalPrice = (Double) sellerUseMap.get(couponPriceKey + goodsCouponPrice.getSellerId());
                        List<GoodsCouponPrice> goodsCouponPriceList = (List<GoodsCouponPrice>) sellerUseMap.get(sellerKey + goodsCouponPrice.getSellerId());
                        if (goodsCouponPriceList == null || couponTotalPrice == null) {
                            goodsCouponPriceList = new ArrayList<>();
                            couponTotalPrice = 0D;
                        }
                        Double couponPrice;
                        if (i == goodsList.size() - 1) {
                            //最后一个可以使用该优惠券的商品，用优惠券金额-使用的金额
                            couponPrice = CurrencyUtil.sub(couponVO.getAmount(), caculateCouponPrice);
                        } else {
                            //计算该商品拆单后能使用的优惠券金额
                            couponPrice = CurrencyUtil.mul(CurrencyUtil.div(goodsCouponPrice.getGoodsOriginPrice(), goodsTotalPrice, 4), couponVO.getAmount());
                            caculateCouponPrice = CurrencyUtil.add(caculateCouponPrice, couponPrice);
                        }
                        goodsCouponPrice.setCouponPrice(couponPrice);
                        goodsCouponPrice.setCouponId(couponVO.getCouponId());
                        goodsCouponPrice.setMemberCouponId(couponVO.getMemberCouponId());

                        goodsCouponPriceList.add(goodsCouponPrice);
                        //塞进map中
                        sellerUseMap.put(sellerKey + goodsCouponPrice.getSellerId(), goodsCouponPriceList);
                        sellerUseMap.put(couponPriceKey + goodsCouponPrice.getSellerId(), CurrencyUtil.add(couponTotalPrice, couponPrice));
                        i++;
                    }
                    //是否是平台优惠券
                    if (couponVO.getSellerId()==0) {
                        sellerUseMap.put(isSite, true);
                    }
                }
            }
        }
        return sellerUseMap;
    }

    /**
     * 获取使用优惠券的商品
     *
     * @param couponVO 优惠券
     * @param cartList 购物车列表
     * @return
     */
    private Map getEnableGoodsPrice(CouponVO couponVO, List<CartVO> cartList) {

        Map resMap = new HashMap();
        //可以使用该优惠券的商品的总金额
        Double totalPrice = 0d;
        List<GoodsCouponPrice> list = new ArrayList<>();
        for (CartVO orderDTO : cartList) {
            //商家优惠券
            if (couponVO.getSellerId()!=0 && orderDTO.getSellerId().equals(couponVO.getSellerId())) {
                totalPrice = orderDTO.getPrice().getOriginalPrice();
                for (CartSkuVO skuVO : orderDTO.getSkuList()) {
                    GoodsCouponPrice goodsCouponPrice = new GoodsCouponPrice(skuVO);
                    list.add(goodsCouponPrice);
                }
                break;
            }
            //平台优惠券
            if (couponVO.getSellerId()==0) {
                List<Long> skuIdList = couponVO.getEnableSkuList();
                List<CartSkuVO> skuList = orderDTO.getSkuList();
                for (CartSkuVO skuVO : skuList) {
                    //查看商品是否在优惠券可使用的skuid中
                    if (skuIdList.indexOf(skuVO.getSkuId()) > -1 || CouponUseScope.ALL.name().equals(couponVO.getUseScope())) {
                        totalPrice += CurrencyUtil.mul(skuVO.getOriginalPrice(), skuVO.getNum());
                        GoodsCouponPrice goodsCouponPrice = new GoodsCouponPrice(skuVO);
                        list.add(goodsCouponPrice);
                    }
                }
            }

        }

        resMap.put("totalPrice", totalPrice);
        resMap.put("goods", list);

        return resMap;

    }


    /**
     * 整理交易阐述
     *
     * @param client   客户端类型
     * @param param    结算参数
     * @param cartView 购物车视图
     * @return TradeParam 交易参数
     */
    public TradeParam getTradeParam(String client, CheckoutParamVO param, CartView cartView) {
        TradeParam tradeParam = new TradeParam();
        //b2b2c 创建订单，选择了会员收货地址id，此处需要设置下
        MemberAddress memberAddress = memberAddressClient.getModel(param.getAddressId());
        ConsigneeVO consignee = new ConsigneeVO(memberAddress);
        tradeParam.setConsignee(consignee);
        tradeParam.setAddrId(param.getAddressId());
        //如果客户端类型为空，那么需要判断是否是wap访问
        if (StringUtil.isEmpty(client) && StringUtil.isWap()) {
            client = "WAP";
        }
        Buyer buyer = UserContext.getBuyer();
        tradeParam.setMemberId(buyer.getUid());
        tradeParam.setMemberName(buyer.getUsername());
        tradeParam.setClient(client);
        tradeParam.setPaymentType(param.getPaymentType().name());
        tradeParam.setRemark(param.getRemark());
        tradeParam.setPrice(cartView.getTotalPrice());
        tradeParam.setReceipt(param.getReceipt());
        tradeParam.setReceiveTime(param.getReceiveTime());
        return tradeParam;
    }



}
