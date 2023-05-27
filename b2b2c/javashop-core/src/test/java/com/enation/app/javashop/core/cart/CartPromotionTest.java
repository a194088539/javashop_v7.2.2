package com.enation.app.javashop.core.cart;

import com.enation.app.javashop.model.promotion.exchange.dos.ExchangeDO;
import com.enation.app.javashop.service.promotion.exchange.ExchangeGoodsManager;
import com.enation.app.javashop.model.promotion.groupbuy.vo.GroupbuyGoodsVO;
import com.enation.app.javashop.service.promotion.groupbuy.GroupbuyGoodsManager;
import com.enation.app.javashop.model.promotion.halfprice.vo.HalfPriceVO;
import com.enation.app.javashop.service.promotion.halfprice.HalfPriceManager;
import com.enation.app.javashop.model.promotion.minus.vo.MinusVO;
import com.enation.app.javashop.service.promotion.minus.MinusManager;
import com.enation.app.javashop.service.trade.cart.CartPromotionManager;
import com.enation.app.javashop.model.trade.cart.vo.SelectedPromotionVo;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.security.model.Buyer;
import com.enation.app.javashop.framework.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.when;

/**
 * Created by kingapex on 2018/12/17.
 * 购物车促销测试
 * @author kingapex
 * @version 1.0
 * @since 7.0.0
 * 2018/12/17
 */
public class CartPromotionTest extends BaseTest {

    @Autowired
    private CartPromotionManager cartPromotionManager;

    @MockBean
    private ExchangeGoodsManager exchangeGoodsManager;

    @MockBean
    private GroupbuyGoodsManager groupbuyGoodsManager;

    @MockBean
    private MinusManager minusManager;

    @MockBean
    private HalfPriceManager halfPriceManager;

    @Test
    public void test() {

        //模拟当前用户
        Buyer buyer = new Buyer();
        buyer.setUid(1L);
        UserContext.mockBuyer = buyer;

        //模拟一个积分兑换活动
        ExchangeDO exchangeDO = SkuMocker.mockExchangeDO();

        //模拟一个团购活动
        GroupbuyGoodsVO groupbuyGoodsVO = SkuMocker.mockGroupBuyVO();

        //模拟一个第二件半价活动
        HalfPriceVO halfPriceVO =  SkuMocker.mockHalfPriceVO();

        //模拟一个单品立即减活动
        MinusVO minusVO = SkuMocker.createMinusVO();


        when(exchangeGoodsManager.getModel(1L)).thenReturn(exchangeDO);
        when(groupbuyGoodsManager.getModel(2L)).thenReturn(groupbuyGoodsVO);
        when(halfPriceManager.getFromDB(3L)).thenReturn(halfPriceVO);
        when(minusManager.getFromDB(4L)).thenReturn(minusVO);


        long sellerId1 = 1;
        long sellerId2 = 2;

        //给这个店铺来两个带活动的商品
//        cartPromotionManager.usePromotion(sellerid1, 1, 1, PromotionTypeEnum.EXCHANGE);
//        cartPromotionManager.usePromotion(sellerid1, 2, 1, PromotionTypeEnum.EXCHANGE);
//
//        //第二个店铺有一个商品
//        cartPromotionManager.usePromotion(sellerid2, 3, 1, PromotionTypeEnum.EXCHANGE);


        //看看现在的活动情况
        System.out.println("看看现在的活动情况:");
        SelectedPromotionVo selectedPromotionVo = cartPromotionManager.getSelectedPromotion();
        System.out.println(selectedPromotionVo);

        //删除一个看看
        System.out.println("删除一个看看:");
        cartPromotionManager.delete(new Long[]{1L});
        selectedPromotionVo = cartPromotionManager.getSelectedPromotion();
        System.out.println(selectedPromotionVo);

        //再删除一个看看
        System.out.println("再删除一个看看:");
        cartPromotionManager.delete(new Long[]{3L});
        selectedPromotionVo = cartPromotionManager.getSelectedPromotion();
        System.out.println(selectedPromotionVo);


        //再加上sku1,然后变换活动
        System.out.println("再加上sku1,然后变换活动,看看:");
//        cartPromotionManager.usePromotion(sellerid1, 1, 4, PromotionTypeEnum.MINUS);
//        cartPromotionManager.usePromotion(sellerid1, 1, 2,PromotionTypeEnum.GROUPBUY );
        selectedPromotionVo = cartPromotionManager.getSelectedPromotion();
        System.out.println(selectedPromotionVo);

        System.out.println("clean,看看:");
        cartPromotionManager.clean();
        selectedPromotionVo = cartPromotionManager.getSelectedPromotion();
        System.out.println(selectedPromotionVo);

    }

}
