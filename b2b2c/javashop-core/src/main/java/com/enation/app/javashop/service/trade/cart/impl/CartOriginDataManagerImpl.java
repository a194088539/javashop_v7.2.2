package com.enation.app.javashop.service.trade.cart.impl;

import com.enation.app.javashop.model.base.CachePrefix;
import com.enation.app.javashop.client.goods.GoodsClient;
import com.enation.app.javashop.model.goods.vo.GoodsSkuVO;
import com.enation.app.javashop.model.errorcode.TradeErrorCode;
import com.enation.app.javashop.model.trade.cart.enums.CheckedWay;
import com.enation.app.javashop.model.trade.cart.vo.CartSkuOriginVo;
import com.enation.app.javashop.service.trade.cart.CartPromotionManager;
import com.enation.app.javashop.service.trade.cart.CartOriginDataManager;
import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.database.DaoSupport;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.security.model.Buyer;
import org.apache.commons.lang.ArrayUtils;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 购物车原始数据业务类实现<br>
 * 文档请参考：<br>
 * <a href="http://doc.javamall.com.cn/current/achitecture/jia-gou/ding-dan/cart-and-checkout.html" >购物车架构</a>
 * @author kingapex
 * @version 1.0
 * @since 7.0.0
 * 2018/12/11
 */
@Service
public class CartOriginDataManagerImpl implements CartOriginDataManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private Cache cache;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private CartPromotionManager cartPromotionManager;

    /**
     * 由缓存中读取购物原始数据
     * @return
     */
    @Override
    public List<CartSkuOriginVo> read(CheckedWay checkedWay) {

        List<CartSkuOriginVo> originList = (List<CartSkuOriginVo> )cache.get(getOriginKey(checkedWay));

        if (originList == null) {
            return new ArrayList<>();
        }
        return originList;
    }

    @Override
    public CartSkuOriginVo add(Long skuId, int num, Long activityId, String promotionType) {
        GoodsSkuVO sku = this.goodsClient.getSkuFromCache(skuId);
        if(sku == null ){
            throw new ServiceException(TradeErrorCode.E451.code(),"商品已失效，请刷新购物车");
        }

        //读取sku的可用库存
        Integer enableQuantity =   sku.getEnableQuantity();

        //如果sku的可用库存小于等于0或者小于用户购买的数量，则不允许购买
        if (enableQuantity.intValue() <= 0 || enableQuantity.intValue() < num) {
            throw new ServiceException(TradeErrorCode.E451.code(),"商品库存已不足，不能购买。");
        }
        List<CartSkuOriginVo> originList =  this.read(CheckedWay.CART);

        //先看看购物车中是否存在此sku
        CartSkuOriginVo skuVo = this.findSku(skuId, originList);

        //购物车中已经存在，试着更新数量
        if (skuVo != null && sku.getLastModify().equals(skuVo.getLastModify())) {

            //判断是商品否被修改
            int oldNum = skuVo.getNum();
            int newNum = oldNum + num;

            //检查购物车优惠活动有效期,如有过期活动则删除过期活动,并重置数量
            if (!cartPromotionManager.checkPromotionInvalid(skuId,activityId,skuVo.getSellerId(),promotionType)) {
                newNum = num;
            }
            //增加数量的话库存已经不足，则最多为可用库存
            if (newNum > enableQuantity) {
                newNum = enableQuantity;
            }
            skuVo.setNum(newNum);

        } else {
            //先清理一下 如果商品无效的话
            originList.remove(skuVo);
            //购物车中不存在此商品，则新建立一个
            skuVo = new CartSkuOriginVo();
            //将相同的属性copy
            BeanUtils.copyProperties(sku, skuVo);
            //再设置加入购物车的数量
            skuVo.setNum(num);
            originList.add(skuVo);
            //设置默认选中的促销活动
            cartPromotionManager.checkPromotionInvalid(skuId,activityId,skuVo.getSellerId(),promotionType);
        }

        //新加入的商品都是选中的
        skuVo.setChecked(1);


        //重新压入缓存
        String originKey = this.getOriginKey(CheckedWay.CART);
        cache.put(originKey,originList);

        return skuVo;
    }

    @Override
    public CartSkuOriginVo addBuyNow(Long skuId, int num, Long activityId, String promotionType) {
        GoodsSkuVO sku = goodsClient.getSku(skuId);
        if(sku == null ){
            throw new ServiceException(TradeErrorCode.E451.code(),"商品已失效，请刷新购物车");
        }
        //如果sku的可用库存小于等于0或者小于用户购买的数量，则不允许购买
        if (sku.getEnableQuantity().intValue() <= 0 || sku.getEnableQuantity().intValue() < num) {
            throw new ServiceException(TradeErrorCode.E451.code(),"商品库存已不足，不能购买。");
        }

        List<CartSkuOriginVo> originList = new ArrayList<>();
        CartSkuOriginVo skuVo = new CartSkuOriginVo() ;
        BeanUtils.copyProperties(sku, skuVo);
        //再设置加入购物车的数量
        skuVo.setNum(num);
        skuVo.setChecked(1);
        originList.add(skuVo);
        //设置默认选中的促销活动
        cartPromotionManager.checkPromotionInvalid(skuId,activityId,skuVo.getSellerId(),promotionType);
        String originKey = this.getOriginKey(CheckedWay.BUY_NOW);
        cache.put(originKey,originList);
        return skuVo;
    }

    @Override
    public void buy(Long skuId, Integer num, Long activityId, String promotionType) {
        cartPromotionManager.cleanCoupon();
        delete(new Long[]{skuId}, CheckedWay.BUY_NOW);
        checkedAll(0, CheckedWay.BUY_NOW);
        addBuyNow(skuId, num, activityId,promotionType);


    }


    @Override
    public CartSkuOriginVo updateNum(Long skuId, int num) {

        Assert.notNull(skuId, "参数skuId不能为空");
        Assert.notNull(num, "参数num不能为空");

        GoodsSkuVO sku = this.goodsClient.getSkuFromCache(skuId);
        if(sku == null ){
            throw new ServiceException(TradeErrorCode.E451.code(),"商品已失效，请刷新购物车");
        }

        //读取sku的可用库存
        Integer enableQuantity =   sku.getEnableQuantity();
        List<CartSkuOriginVo> originList =  this.read(CheckedWay.CART);

        //先看看购物车中是否存在此sku
        CartSkuOriginVo skuVo = this.findSku(skuId, originList);

        if (skuVo != null) {
            //话库存已经不足
            if (num > enableQuantity) {
                throw new ServiceException(TradeErrorCode.E451.code(), "此商品已经超出库存，库存为[" + enableQuantity + "]");
            } else {
                skuVo.setNum(num);
            }
        }

        String originKey = this.getOriginKey(CheckedWay.CART);

        cache.put(originKey,originList);

        return   skuVo;

    }

    @Override
    public CartSkuOriginVo checked(long skuId, int checked) {
        //不合法的参数，忽略掉
        if (checked != 1 && checked != 0) {
            return new CartSkuOriginVo();
        }


        Assert.notNull(skuId, "参数skuId不能为空");
        String originKey = this.getOriginKey(CheckedWay.CART);

        //这是本次要返回的sku
        AtomicReference<CartSkuOriginVo> skuOriginVo = new AtomicReference<>();
        List<CartSkuOriginVo> originList = this.read(CheckedWay.CART);
        originList.forEach(cartSku -> {
            if (cartSku.getSkuId() == skuId) {
                cartSku.setChecked(checked);
                skuOriginVo.set(cartSku);
                return;
            }
        });

        cache.put(originKey,originList);

        return   skuOriginVo.get();
    }

    @Override
    public void checkedSeller(long sellerId, int checked) {
        //不合法的参数，忽略掉
        if (checked != 1 && checked != 0) {
            return ;
        }

        Assert.notNull(sellerId, "参数sellerId不能为空");
        String originKey = this.getOriginKey(CheckedWay.CART);

        List<CartSkuOriginVo> originList = this.read(CheckedWay.CART);
        originList.forEach(cartSku -> {
            if (cartSku.getSellerId() == sellerId) {
                cartSku.setChecked(checked);
            }
        });

        cache.put(originKey,originList);

    }

    @Override
    public void checkedAll(int checked, CheckedWay way) {
        //不合法的参数，忽略掉
        if (checked != 1 && checked != 0) {
            return ;
        }

        String originKey = this.getOriginKey(way);

        //这是本次要返回的sku
        List<CartSkuOriginVo> originList = this.read(way);
        originList.forEach(cartSku -> {
                cartSku.setChecked(checked);
        });

        cache.put(originKey,originList);

    }




    @Override
    public void delete(Long[] skuIds, CheckedWay way) {
        Assert.notNull(skuIds, "参数skuIds不能为空");
        String originKey = this.getOriginKey(way);

        //删除相关促销活动
        cartPromotionManager.delete(skuIds);
        //这是本次要返回的sku
        List<CartSkuOriginVo> originList = this.read(way);
        List<CartSkuOriginVo> newList = new ArrayList<>();
        originList.forEach(cartSku -> {

            //查找是否要删除
            //如果不删除压入到新的list中
            if ( !ArrayUtils.contains(skuIds, cartSku.getSkuId()) ){
                newList.add(cartSku);
            }

        });

        cache.put(originKey,newList);

    }

    @Override
    public void clean() {
        //清空此用户所有选择的促销活动
        cartPromotionManager.clean();
        String originKey = this.getOriginKey(CheckedWay.CART);
        cache.remove(originKey);
    }

    @Override
    public void cleanChecked(CheckedWay way) {
        String originKey = this.getOriginKey(way);

        //这是本次要返回的sku
        List<CartSkuOriginVo> originList = this.read(way);
        List<CartSkuOriginVo> newList = new ArrayList<>();
        List<Long> skuIds = new ArrayList<Long>();

        originList.forEach(cartSku -> {

            //如果是选中的则要删除（也就是未选中的才压入list）
            if (cartSku.getChecked() == 0) {
                newList.add(cartSku);
            } else {
                skuIds.add(cartSku.getSkuId());
            }

        });

        //清除相关sku的优惠活动
        cartPromotionManager.delete( skuIds.toArray(new Long[skuIds.size()]));

        //清除用户选择的所有的优惠券
        cartPromotionManager.cleanCoupon();
        cache.put(originKey,newList);

    }


    /**
     * 读取当前会员购物原始数据key
     * @param checkedWay 获取方式
     * @return
     */
    private String getOriginKey(CheckedWay checkedWay) {

        String cacheKey = "";
        //如果会员登录了，则要以会员id为key
        Buyer buyer = UserContext.getBuyer();
        if (buyer != null) {
            if (checkedWay.equals(CheckedWay.CART)){
                cacheKey = CachePrefix.CART_ORIGIN_DATA_PREFIX.getPrefix()+buyer.getUid();
            }else if (checkedWay.equals(CheckedWay.BUY_NOW)){
                cacheKey = CachePrefix.BUY_NOW_ORIGIN_DATA_PREFIX.getPrefix()+buyer.getUid();
            }

        }

        return cacheKey;
    }

    /**
     * 根据skuid 查找某个sku
     * @param skuId 要查找的skuid
     * @param originList  sku list
     * @return 找到返回sku，找不到返回Null
     */
    private  CartSkuOriginVo findSku(Long skuId, List<CartSkuOriginVo> originList){
        for (CartSkuOriginVo cartSkuOriginVo : originList) {
            if (cartSkuOriginVo.getSkuId().equals(skuId)) {
                return cartSkuOriginVo;
            }
        }
        return null;

    }



}
