package com.enation.app.javashop.service.trade.pintuan.impl;

import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import com.enation.app.javashop.framework.rabbitmq.MessageSender;
import com.enation.app.javashop.framework.security.model.Buyer;
import com.enation.app.javashop.framework.sncreator.SnCreator;
import com.enation.app.javashop.model.base.CachePrefix;
import com.enation.app.javashop.model.member.dos.MemberAddress;
import com.enation.app.javashop.model.trade.cart.vo.CartSkuVO;
import com.enation.app.javashop.model.trade.cart.vo.CartVO;
import com.enation.app.javashop.model.trade.cart.vo.CartView;
import com.enation.app.javashop.model.trade.order.dto.OrderDTO;
import com.enation.app.javashop.model.trade.order.enums.OrderTypeEnum;
import com.enation.app.javashop.model.trade.order.vo.CheckoutParamVO;
import com.enation.app.javashop.model.trade.order.vo.OrderParam;
import com.enation.app.javashop.model.trade.order.vo.TradeParam;
import com.enation.app.javashop.model.trade.order.vo.TradeVO;
import com.enation.app.javashop.service.trade.order.OrderCenterManager;
import com.enation.app.javashop.service.trade.order.TradeCenterManager;
import com.enation.app.javashop.service.trade.order.TradeValidator;
import com.enation.app.javashop.service.trade.order.impl.DefaultTradeValidator;
import com.enation.app.javashop.service.trade.order.impl.TradeManagerImpl;
import com.enation.app.javashop.service.trade.pintuan.PintuanCartManager;
import com.enation.app.javashop.service.trade.pintuan.PintuanOrderManager;
import com.enation.app.javashop.service.trade.pintuan.exception.PintuanErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kingapex on 2019-01-24.
 * 拼团交易业务类<br/>
 * 继承默认的交易业务类<br/>
 * 其中不同的是:<br/>
 * 1、使用 {@link PintuanCartManager} 获取购物车内容<br/>
 * 2、不检测优惠活动的合法性，因为拼团不存在其它活动的重叠
 * @author kingapex
 * @version 1.0
 * @since 7.1.0
 * 2019-01-24
 */
@Service
public class PintuanTradeManagerImpl extends TradeManagerImpl {

    @Autowired
    private PintuanCartManager  pintuanCartManager;

    @Autowired
    private PintuanOrderManager pintuanOrderManager;

    @Autowired
    private TradeCenterManager tradeCenterManager;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OrderCenterManager orderCenterManager;

    @Autowired
    private Cache cache;

    @Autowired
    SnCreator snCreator;

    private Integer orderCacheTimeout = 60 * 60;

    @Autowired
    private MessageSender messageSender;

    /**
     * 创建订单并创建拼团订单
     * @param client
     * @param pinTuanOrderId
     * @return
     */
    public TradeVO createTrade(String client, Long pinTuanOrderId) {

        //自己参与自己拼团的判定
        if(pinTuanOrderId!=null){
            Buyer buyer = UserContext.getBuyer();
            this.pintuanOrderManager.getModel(pinTuanOrderId).getParticipants().forEach(participant -> {
                if(participant.getId().equals(buyer.getUid())){
                    throw new ServiceException(PintuanErrorCode.E5013.code(),"不能参加自己创建的拼团");
                }
            });
        }
        //获取结算参数
        CheckoutParamVO param = checkoutParamManager.getParam();

        //获取购物车视图
        CartView cartView =pintuanCartManager.getCart();
        //获取收货地址
        MemberAddress memberAddress = this.memberAddressClient.getModel(param.getAddressId());

        logger.debug("准备创建拼团订单");
        logger.debug("param:" + param);
        logger.debug("cartView:" + cartView);
        logger.debug("memberAddress:" + memberAddress);

        //创建交易检测者
        TradeValidator tradeValidator = new DefaultTradeValidator(param,cartView,memberAddress,param.getPaymentType()).setTradeSnCreator(snCreator).setGoodsClient(goodsClient).setMemberClient(memberClient).setShippingManager(shippingManager).setCheckoutParamManager(checkoutParamManager);
        //检测配送范围==>检测商品的有效性
        tradeValidator.checkShipRange().checkGoods();

        TradeParam tradeParam = super.getTradeParam(client,param,cartView);

        //创建交易
        TradeVO tradeVO = tradeCenterManager.createTrade(tradeParam);

        logger.debug("生成交易："+ tradeVO);

        //循环购物车列表
        CartVO cartVO = cartView.getCartList().get(0);
        OrderParam orderParam = super.convertSku(tradeVO, null, cartVO);
        orderParam.setOrderType(OrderTypeEnum.PINTUAN.name());
        OrderDTO orderDTO = this.orderCenterManager.createOrder(orderParam);

        List<OrderDTO> list = new ArrayList<>();
        list.add(orderDTO);
        tradeVO.setOrderList(list);

        logger.debug("生成交易："+ tradeVO);

        CartSkuVO skuVO = cartVO.getSkuList().get(0);

        pintuanOrderManager.createOrder(tradeVO,orderDTO,skuVO.getSkuId(), pinTuanOrderId);
        //清空备注信息
        this.checkoutParamManager.setRemark("");
        //清空发票信息
        this.checkoutParamManager.deleteReceipt();
        //清空客户端类型
        this.checkoutParamManager.setClientType("");


        return tradeVO;
    }

    /**
     * 获取交易缓存key
     *
     * @param tradeSn
     * @return
     */
    public String getTradeCacheKey(String tradeSn) {
        //重新压入缓存
        String cacheKey = CachePrefix.TRADE_SESSION_ID_PREFIX.getPrefix() + tradeSn;
        return cacheKey;
    }
}
