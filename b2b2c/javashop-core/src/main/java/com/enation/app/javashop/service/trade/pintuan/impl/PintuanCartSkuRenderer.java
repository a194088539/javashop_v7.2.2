package com.enation.app.javashop.service.trade.pintuan.impl;

import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.exception.ResourceNotFoundException;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.security.model.Buyer;
import com.enation.app.javashop.framework.util.CurrencyUtil;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.mapper.trade.pintuan.PintuanGoodsMapper;
import com.enation.app.javashop.model.base.CachePrefix;
import com.enation.app.javashop.model.promotion.pintuan.PintuanGoodsDO;
import com.enation.app.javashop.model.trade.cart.enums.CartType;
import com.enation.app.javashop.model.trade.cart.enums.CheckedWay;
import com.enation.app.javashop.model.trade.cart.vo.CartSkuOriginVo;
import com.enation.app.javashop.model.trade.cart.vo.CartSkuVO;
import com.enation.app.javashop.model.trade.cart.vo.CartVO;
import com.enation.app.javashop.service.trade.cart.cartbuilder.CartSkuRenderer;
import com.enation.app.javashop.service.trade.cart.cartbuilder.impl.CartSkuFilter;
import com.enation.app.javashop.service.trade.pintuan.PintuanManager;
import com.enation.app.javashop.service.trade.pintuan.exception.PintuanErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kingapex on 2019-01-23.
 * 拼团的购物车渲染器
 *
 * @author kingapex
 * @version 1.0
 * @since 7.0.0
 * 2019-01-23
 */
@Service
public class PintuanCartSkuRenderer implements CartSkuRenderer {

    @Autowired
    private PintuanGoodsMapper pintuanGoodsMapper;
    @Autowired
    private PintuanManager pintuanManager;
    @Autowired
    private Cache cache;

    @SuppressWarnings("Duplicates")
    @Override
    public void renderSku(List<CartVO> cartList, CartType cartType, CheckedWay way) {

        String originKey = this.getOriginKey();
        CartSkuOriginVo goodsSkuVO = (CartSkuOriginVo) cache.get(originKey);

        //查询当前时间内的拼团商品
        PintuanGoodsDO pintuanGoodsDO = pintuanGoodsMapper.selectPintuanGoods(goodsSkuVO.getSkuId(), DateUtil.getDateline(), DateUtil.getDateline());

        if (pintuanGoodsDO == null) {
            throw new ResourceNotFoundException("此拼团活动已经取消，不能发起拼团");
        }
        CartSkuVO skuVO = new CartSkuVO();

        skuVO.setSellerId(pintuanGoodsDO.getSellerId());
        skuVO.setSellerName(pintuanGoodsDO.getSellerName());
        skuVO.setGoodsId(pintuanGoodsDO.getGoodsId());
        skuVO.setSkuId(pintuanGoodsDO.getSkuId());
        skuVO.setCatId(goodsSkuVO.getCategoryId());
        skuVO.setGoodsImage(goodsSkuVO.getThumbnail());
        skuVO.setName(pintuanGoodsDO.getGoodsName());
        skuVO.setSkuSn(pintuanGoodsDO.getSn());


        //拼团成交价
        skuVO.setPurchasePrice(pintuanGoodsDO.getSalesPrice());

        //拼团商品原始价格
        skuVO.setOriginalPrice(pintuanGoodsDO.getPrice());

        skuVO.setSpecList(goodsSkuVO.getSpecList());
        skuVO.setIsFreeFreight(goodsSkuVO.getGoodsTransfeeCharge());
        skuVO.setGoodsWeight(goodsSkuVO.getWeight());
        skuVO.setTemplateId(goodsSkuVO.getTemplateId());
        skuVO.setEnableQuantity(goodsSkuVO.getEnableQuantity());
        skuVO.setLastModify(goodsSkuVO.getLastModify());
        skuVO.setNum(goodsSkuVO.getNum());
        skuVO.setChecked(1);
        skuVO.setGoodsType(goodsSkuVO.getGoodsType());

        //计算小计
        double subTotal = CurrencyUtil.mul(skuVO.getNum(), skuVO.getPurchasePrice());
        skuVO.setSubtotal(subTotal);

        List<CartSkuVO> skuList = new ArrayList<>();
        skuList.add(skuVO);

        Long sellerId = goodsSkuVO.getSellerId();
        String sellerName = goodsSkuVO.getSellerName();

        CartVO cartVO = new CartVO(sellerId, sellerName, cartType);
        cartVO.setSkuList(skuList);

        //如果超出限购数量 如果限购数量为空，则不验证限购数量
        Integer pintuan = pintuanManager.getModel(pintuanGoodsDO.getPintuanId()).getLimitNum();
        if (pintuan != null && pintuan!=0 && pintuan < skuVO.getNum()){
            throw new ServiceException(PintuanErrorCode.E5018.code(), PintuanErrorCode.E5018.describe());
        }

        cartList.add(cartVO);

    }

    @Override
    public void renderSku(List<CartVO> cartList, CartSkuFilter cartFilter, CartType cartType, CheckedWay way) {

        //创建一个临时的list
        List<CartVO> tempList = new ArrayList<>();

        //将临时的list渲染好
        renderSku(tempList, cartType, way);

        //进行过滤
        tempList.forEach(cartVO -> {

            cartVO.getSkuList().forEach(cartSkuVO -> {
                //如果过滤成功才继续
                if (!cartFilter.accept(cartSkuVO)) {
                    cartList.add(cartVO);
                }
            });

        });
    }


    /**
     * 读取当前会员购物车原始数据key
     *
     * @return
     */
    @SuppressWarnings("Duplicates")
    protected String getOriginKey() {

        String cacheKey = "";
        //如果会员登录了，则要以会员id为key
        Buyer buyer = UserContext.getBuyer();
        if (buyer != null) {
            cacheKey = CachePrefix.CART_SKU_PREFIX.getPrefix() + buyer.getUid();
        }

        return cacheKey;
    }
}
