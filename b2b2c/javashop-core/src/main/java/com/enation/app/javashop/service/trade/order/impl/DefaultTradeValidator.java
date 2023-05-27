package com.enation.app.javashop.service.trade.order.impl;

import com.enation.app.javashop.client.goods.GoodsClient;
import com.enation.app.javashop.client.member.MemberClient;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import com.enation.app.javashop.framework.security.model.Buyer;
import com.enation.app.javashop.framework.sncreator.SnCreator;
import com.enation.app.javashop.framework.util.JsonUtil;
import com.enation.app.javashop.model.errorcode.TradeErrorCode;
import com.enation.app.javashop.model.goods.vo.CacheGoods;
import com.enation.app.javashop.model.goods.vo.GoodsSkuVO;
import com.enation.app.javashop.model.member.dos.Member;
import com.enation.app.javashop.model.member.dos.MemberAddress;
import com.enation.app.javashop.model.promotion.tool.enums.PromotionTypeEnum;
import com.enation.app.javashop.model.trade.cart.vo.*;
import com.enation.app.javashop.model.trade.order.enums.PaymentTypeEnum;
import com.enation.app.javashop.model.trade.order.vo.CheckoutParamVO;
import com.enation.app.javashop.service.trade.cart.cartbuilder.ScriptProcess;
import com.enation.app.javashop.service.trade.order.CheckoutParamManager;
import com.enation.app.javashop.service.trade.order.ShippingManager;
import com.enation.app.javashop.service.trade.order.TradeValidator;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 默认交易检测者
 * @author: liuyulei
 * @create: 2020-03-20 12:01
 * @version:1.0
 * @since:7.2.0
 **/
@Service
public class DefaultTradeValidator implements TradeValidator {

    protected CheckoutParamVO param;
    protected CartView cartView;
    protected MemberAddress memberAddress;
    protected PaymentTypeEnum paymentTypeEnum;
    protected ShippingManager shippingManager;
    protected GoodsClient goodsClient;
    protected MemberClient memberClient;
    SnCreator snCreator;
    protected ScriptProcess scriptProcess;
    protected CheckoutParamManager checkoutParamManager;


    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final String sellerKey = "seller_";
    private final String couponPriceKey = "total_coupon_price_";
    private final String isSite = "is_site";


    public DefaultTradeValidator() {
    }

    /**
     * 通过构造器设置构建交易所需要的原料
     *
     * @param param         结算参数
     * @param cartView      购物车视图
     * @param memberAddress 收货地址
     */
    public DefaultTradeValidator(CheckoutParamVO param, CartView cartView, MemberAddress memberAddress,PaymentTypeEnum paymentTypeEnum) {

        this.param = param;
        this.cartView = cartView;
        this.memberAddress = memberAddress;
        this.paymentTypeEnum = paymentTypeEnum;
    }


    public DefaultTradeValidator setShippingManager(ShippingManager shippingManager) {
        this.shippingManager = shippingManager;
        return this;
    }

    public DefaultTradeValidator setGoodsClient(GoodsClient goodsClient) {
        this.goodsClient = goodsClient;
        return this;
    }

    public DefaultTradeValidator setMemberClient(MemberClient memberClient) {
        this.memberClient = memberClient;
        return this;
    }

    public DefaultTradeValidator setScriptProcess(ScriptProcess scriptProcess) {
        this.scriptProcess = scriptProcess;
        return this;
    }

    public DefaultTradeValidator setTradeSnCreator(SnCreator tradeSnCreator) {
        snCreator = tradeSnCreator;
        return this;
    }

    public DefaultTradeValidator setCheckoutParamManager(CheckoutParamManager checkoutParamManager) {
        this.checkoutParamManager= checkoutParamManager;
        return this;
    }

    @Override
    public TradeValidator checkShipRange() {
        Assert.notNull(shippingManager, "shippingManager为空，请先调用setShippingManager设置正确的交配送管理业务类");
        if (memberAddress == null) {
            throw new ServiceException(TradeErrorCode.E452.code(), "请填写收货地址");
        }

        //已选中结算的商品
        List<CartVO> cartList = cartView.getCartList();

        Long areaId = memberAddress.getCountyId();

        Long addrId = memberAddress.getAddrId();
        //验证地区是否支持货到付款
        this.checkoutParamManager.checkCod(paymentTypeEnum,addrId);

        //2、筛选不在配送区域的商品
        List<CacheGoods> list = this.shippingManager.checkArea(cartList, areaId);

        //验证后存在商品问题的集合
        List<Map> goodsErrorList = new ArrayList();

        if (list.size() > 0) {
            for (CacheGoods goods : list) {
                Map errorMap = new HashMap(16);
                errorMap.put("name", goods.getGoodsName());
                errorMap.put("image", goods.getThumbnail());
                goodsErrorList.add(errorMap);
            }
            throw new ServiceException(TradeErrorCode.E461.code(), "商品不在配送区域", goodsErrorList);
        }
        return this;
    }

    @Override
    public TradeValidator checkGoods() {
        Assert.notNull(goodsClient, "goodsClient为空，请先调用setGoodsClient设置正确的商品业务Client");

        //已选中结算的商品
        List<CartVO> cartList = cartView.getCartList();
        //1、检测购物车是否为空
        if (cartList == null || cartList.isEmpty()) {
            throw new ServiceException(TradeErrorCode.E452.code(), "购物车为空");
        }
        //验证后存在商品问题的集合
        List<Map> goodsErrorList = new ArrayList();

        boolean flag = true;
        //遍历购物车集合
        for (CartVO cartVO : cartList) {

            List<CartSkuVO> skuList = cartVO.getSkuList();

            for (CartSkuVO cartSkuVO : skuList) {
                Map errorMap = new HashMap(16);
                errorMap.put("name", cartSkuVO.getName());
                errorMap.put("image", cartSkuVO.getGoodsImage());

                Long skuId = cartSkuVO.getSkuId();
                GoodsSkuVO skuVO = this.goodsClient.getSkuFromCache(skuId);

                //检测商品是否存在
                if (skuVO == null) {
                    goodsErrorList.add(errorMap);
                    continue;
                }

                //检测商品的上下架状态
                if (skuVO.getMarketEnable() != null && skuVO.getMarketEnable().intValue() != 1) {
                    goodsErrorList.add(errorMap);
                    continue;
                }

                //检测商品的删除状态
                if (skuVO.getDisabled() != null && skuVO.getDisabled().intValue() != 1) {
                    goodsErrorList.add(errorMap);
                    continue;
                }

                Long goodsId = skuVO.getGoodsId();
                CacheGoods goodsVo = this.goodsClient.getFromCache(goodsId);
                if (goodsVo.getIsAuth() == 0 || goodsVo.getIsAuth() == 3) {
                    goodsErrorList.add(errorMap);
                    continue;
                }

                //读取此产品的可用库存数量
                int enableQuantity = skuVO.getEnableQuantity();
                //此产品将要购买的数量
                int num = cartSkuVO.getNum();

                //如果将要购买的产品数量大于redis中的数量，则此产品不能下单
                if (num > enableQuantity) {
                    flag = false;
                    goodsErrorList.add(errorMap);
                    continue;
                }
            }
        }

        if (!goodsErrorList.isEmpty()) {
            throw new ServiceException(TradeErrorCode.E452.code(), "抱歉，您以下商品所在地区无货", JsonUtil.objectToJson(goodsErrorList));
        }
        return this;
    }

    @Override
    public TradeValidator checkPromotion() {
        List<CartVO> cartList = cartView.getCartList();

        for (CartVO cartVO : cartList) {

            List<CartSkuVO> skuList = cartVO.getSkuList();

            for (CartSkuVO cartSkuVO : skuList) {
                innerCheckPromotion(cartSkuVO);
            }
        }

        //读取订单的总交易价格信息
        PriceDetailVO detailVO = cartView.getTotalPrice();

        //此交易需要扣除用户的积分
        Long point = detailVO.getExchangePoint();

        if (point > 0) {
            Buyer buyer = UserContext.getBuyer();
            Member member = this.memberClient.getModel(buyer.getUid());
            Long consumPoint = member.getConsumPoint();

            //如果用户可使用的消费积分小于 交易需要扣除的积分时，则不能下单
            if (consumPoint < point) {
                //update by liuyulei 2019-07-17  修改bug,立即购买不经过购物车，错误信息返回:"您可使用的消费积分不足"
                throw new ServiceException(TradeErrorCode.E452.code(), "您可使用的消费积分不足");
            }
        }
        return this;
    }



    /**
     * 检测sku参与的促销活动是否有效
     * @param cartSkuVO
     */
    private void innerCheckPromotion(CartSkuVO cartSkuVO) {

        Map errorMap = new HashMap(16);
        errorMap.put("name", cartSkuVO.getName());
        errorMap.put("image", cartSkuVO.getGoodsImage());

        //验证后存在促销活动问题的集合
        List<Map> promotionErrorList = new ArrayList();
        //此商品参与的单品活动
        List<CartPromotionVo> singlePromotionList = cartSkuVO.getSingleList();
        if (!singlePromotionList.isEmpty()) {
            for (CartPromotionVo promotionGoodsVO : singlePromotionList) {
                // 默认参与的活动 && 非不参与活动的状态
                if (promotionGoodsVO.getIsCheck().intValue() == 1 && !promotionGoodsVO.getPromotionType().equals(PromotionTypeEnum.NO.name())) {
                    Boolean bool = this.scriptProcess.validTime(promotionGoodsVO.getPromotionScript());
                    if (!bool) {
                        //此活动已经失效了，不能下单
                        promotionErrorList.add(errorMap);
                        continue;
                    }
                }

            }
        }

        //此商品参与的组合活动
        List<CartPromotionVo> groupPromotionList = cartSkuVO.getGroupList();
        if (!groupPromotionList.isEmpty()) {
            for (CartPromotionVo cartPromotionGoodsVo : groupPromotionList) {
                Boolean bool = this.scriptProcess.validTime(cartPromotionGoodsVo.getPromotionScript());

                if (!bool) {
                    //此活动已经失效了，不能下单
                    promotionErrorList.add(errorMap);
                    continue;
                }
            }
        }
    }
}
