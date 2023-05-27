package com.enation.app.javashop.service.trade.cart.impl;

import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.model.base.CachePrefix;
import com.enation.app.javashop.client.member.MemberCouponClient;
import com.enation.app.javashop.model.member.dos.MemberCoupon;
import com.enation.app.javashop.model.promotion.coupon.vo.CouponValidateResult;
import com.enation.app.javashop.model.promotion.tool.enums.PromotionTypeEnum;
import com.enation.app.javashop.model.promotion.tool.vo.PromotionScriptVO;
import com.enation.app.javashop.model.errorcode.TradeErrorCode;
import com.enation.app.javashop.model.trade.cart.vo.*;
import com.enation.app.javashop.service.trade.cart.CartPromotionManager;
import com.enation.app.javashop.service.trade.cart.cartbuilder.ScriptProcess;
import com.enation.app.javashop.service.trade.cart.util.CartUtil;
import com.enation.app.javashop.service.trade.cart.util.CouponValidateUtil;
import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.database.DaoSupport;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.security.model.Buyer;
import com.enation.app.javashop.framework.util.BeanUtil;
import org.apache.commons.lang.ArrayUtils;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;

/**
 * 购物车促销信息处理实现类
 *
 * @author kingapex
 * @version 1.0
 * @since 7.0.0
 * 2018/12/1
 */
@Service
public class CartPromotionManagerImpl implements CartPromotionManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Autowired
    private Cache cache;
    @Autowired
    private MemberCouponClient memberCouponClient;
    @Autowired
    private ScriptProcess scriptProcess;


    private String getOriginKey() {
        String cacheKey = "";
        //如果会员登录了，则要以会员id为key
        Buyer buyer = UserContext.getBuyer();
        if (buyer != null) {
            cacheKey = CachePrefix.CART_PROMOTION_PREFIX.getPrefix() + buyer.getUid();
        }
        return cacheKey;
    }

    /**
     * 由缓存中读取出用户选择的促销信息
     *
     * @return 用户选择的促销信息
     */
    @Override
    public SelectedPromotionVo getSelectedPromotion() {
        String cacheKey = this.getOriginKey();
        SelectedPromotionVo selectedPromotionVo = (SelectedPromotionVo) cache.get(cacheKey);
        if (selectedPromotionVo == null) {
            selectedPromotionVo = new SelectedPromotionVo();
            cache.put(cacheKey, selectedPromotionVo);
        }

        return selectedPromotionVo;
    }

    @Override
    public void usePromotion(Long sellerId, Long skuId, CartPromotionVo promotionVo) {
        Assert.notNull(PromotionTypeEnum.myValueOf(promotionVo.getPromotionType()), "未知的促销类型");

        try {

            SelectedPromotionVo selectedPromotionVo = this.getSelectedPromotion();
            List<PromotionScriptVO> promotionScriptVO = getSkuScript(skuId);
            //获取店铺级别别的活动
            this.getCartScript(sellerId, promotionScriptVO);

            Boolean bool = false;
            for (PromotionScriptVO scriptVO : promotionScriptVO) {
                if (promotionVo.getPromotionId().equals(scriptVO.getPromotionId())
                        && promotionVo.getPromotionType().equals(scriptVO.getPromotionType())) {
                    bool = this.scriptProcess.validTime(scriptVO.getPromotionScript());
                    BeanUtil.copyProperties(scriptVO, promotionVo);
                    promotionVo.setSkuId(skuId);
                    promotionVo.setIsCheck(1);
                    break;
                } else {
                    promotionVo.setIsCheck(0);
                }
            }
            //如果促销活动有效 则正常使用
            if (bool) {
                selectedPromotionVo.putPromotion(sellerId, promotionVo);
                String cacheKey = this.getOriginKey();
                cache.put(cacheKey, selectedPromotionVo);
                logger.debug("使用促销：" + promotionVo);
                logger.debug("促销信息为:" + selectedPromotionVo);
            }


        } catch (Exception e) {
            logger.error("使用促销出错", e);
            throw new ServiceException(TradeErrorCode.E462.code(), e.getMessage());
        }
    }


    @Override
    public void useCoupon(Long sellerId, Long mcId, List<CartVO> cartList, MemberCoupon memberCoupon) {
        if (memberCoupon != null) {
            //查询选中的促销
            SelectedPromotionVo selectedPromotionVo = getSelectedPromotion();
            CouponVO couponVO = new CouponVO(memberCoupon);
            SelectedPromotionVo selectedPromotion = getSelectedPromotion();
            selectedPromotion.putCooupon(sellerId, couponVO);
            logger.debug("使用优惠券：" + couponVO);
            logger.debug("促销信息为:" + selectedPromotionVo);
            String cacheKey = this.getOriginKey();
            cache.put(cacheKey, selectedPromotion);
        }
    }

    @Override
    public MemberCoupon detectCoupon(Long sellerId, Long mcId, List<CartVO> cartList) {
        Buyer buyer = UserContext.getBuyer();
        MemberCoupon memberCoupon = this.memberCouponClient.getModel(buyer.getUid(), mcId);
        //如果优惠券Id为0并且优惠券为空则取消优惠券使用
        if (memberCoupon == null && mcId == 0) {
            this.deleteCoupon(sellerId);
            //取消优惠券返回为空
            return null;
        }
        //如果优惠券为空则抛出异常
        if (memberCoupon == null) {
            throw new ServiceException(TradeErrorCode.E455.code(), "当前优惠券不存在");
        }
        if (sellerId != null && !sellerId.equals(memberCoupon.getSellerId())) {
            throw new ServiceException(TradeErrorCode.E455.code(), "当前优惠券不满足使用条件");
        }
        //如果优惠券状态未已使用
        if (memberCoupon.getUsedStatus() != null && memberCoupon.getUsedStatus() == 1) {
            throw new ServiceException(TradeErrorCode.E455.code(), "当前优惠券已使用");
        }

        //如果优惠券时间未达标则抛出异常
        long time = DateUtil.getDateline();
        if (memberCoupon.getEndTime() < time || memberCoupon.getStartTime() > time) {
            throw new ServiceException(TradeErrorCode.E455.code(), "您选择优惠券不可使用！ ");
        }
        //查询选中的促销
        SelectedPromotionVo selectedPromotionVo = getSelectedPromotion();
        //查看购物车中是否包含积分商品
        for (CartVO cartVO : cartList) {
            if (CouponValidateUtil.validateCoupon(selectedPromotionVo, sellerId, cartVO.getSkuList())) {
                throw new ServiceException(TradeErrorCode.E455.code(), "您选择的商品包含积分兑换的商品不能使用优惠券！");
            }
        }

        //判断是平台优惠券，还是商家优惠券
        if (memberCoupon.getSellerId() == 0) {
            //平台优惠券
            CouponValidateResult isUseRes = CouponValidateUtil.isEnable(memberCoupon, cartList);
            if (isUseRes.isEnable()) {
                //平台优惠券和店铺优惠券不能同时使用，所以选中了平台优惠券，先将优惠券清空，再设置选中的优惠券
                this.cleanCoupon();
            } else {
                throw new ServiceException(TradeErrorCode.E455.code(), "您选择优惠券不可使用！");
            }
        } else {
            //平台优惠券和店铺优惠券不能同时使用
            this.deleteCoupon(0L);
            //商家优惠券
            CartVO cart = CartUtil.findCart(sellerId, cartList);
            if (cart == null) {
                throw new ServiceException(TradeErrorCode.E455.code(), "您选择优惠券不可使用！");
            } else {
                double goodsPrice = cart.getPrice().getOriginalPrice();
                //校验优惠券的限额
                if (goodsPrice < memberCoupon.getCouponThresholdPrice()) {
                    throw new ServiceException(TradeErrorCode.E455.code(), "未达到优惠券使用最低限额");
                }
            }
        }
        return memberCoupon;
    }


    @Override
    public void deleteCoupon(Long sellerId) {
        SelectedPromotionVo selectedPromotionVo = getSelectedPromotion();
        selectedPromotionVo.getCouponMap().remove(sellerId);
        String cacheKey = this.getOriginKey();
        cache.put(cacheKey, selectedPromotionVo);
    }

    @Override
    public void cleanCoupon() {
        SelectedPromotionVo selectedPromotionVo = getSelectedPromotion();
        selectedPromotionVo.getCouponMap().clear();
        String cacheKey = this.getOriginKey();
        cache.put(cacheKey, selectedPromotionVo);
    }


    /**
     * 获取sku级别活动
     *
     * @param skuId
     * @return
     */
    private List<PromotionScriptVO> getSkuScript(Long skuId) {
        //获取sku级别参与的活动
        List<PromotionScriptVO> promotionScriptVO = scriptProcess.readSkuScript(skuId);
        if (promotionScriptVO == null || promotionScriptVO.isEmpty()) {
            promotionScriptVO = new ArrayList<>();
        }
        return promotionScriptVO;
    }

    /**
     * 删除一组sku的促销，
     *
     * @param skuids
     */
    @Override
    public void delete(Long[] skuids) {
        SelectedPromotionVo selectedPromotionVo = this.getSelectedPromotion();
        Map<Long, List<CartPromotionVo>> promotionMap = selectedPromotionVo.getSinglePromotionMap();

        //用来记录要删除的店铺
        List<Long> needRemoveSellerIds = new ArrayList<>();

        Iterator<Long> sellerIdIter = promotionMap.keySet().iterator();

        while (sellerIdIter.hasNext()) {
            Long sellerId = sellerIdIter.next();
            List<CartPromotionVo> skuPromotionVoList = promotionMap.get(sellerId);

            if (skuPromotionVoList == null) {
                continue;
            }

            List<CartPromotionVo> newList = deleteBySkus(skuids, skuPromotionVoList);

            //如果新list是空的，表明这个店铺已经没有促销活动了
            if (newList.isEmpty()) {
                needRemoveSellerIds.add(sellerId);
            } else {
                //将清理后的
                promotionMap.put(sellerId, newList);
            }

        }

        //经过上述的处理，list中已经有了要删除的店铺id
        for (Long sellerid : needRemoveSellerIds) {
            promotionMap.remove(sellerid);
        }

        //重新压入缓存
        String cacheKey = this.getOriginKey();
        cache.put(cacheKey, selectedPromotionVo);

    }

    @Override
    public boolean checkPromotionInvalid(Long skuId, Long promotionId, Long sellerId, String promotionType) {
        //默认活动全部有效
        Boolean invalid = true;
        SelectedPromotionVo selectedPromotionVo = this.getSelectedPromotion();

        //获取之前选中的单品活动
        Map<Long, List<CartPromotionVo>> promotionMap = selectedPromotionVo.getSinglePromotionMap();
        //复制一份之前选中的单品活动
        Map<Long, List<CartPromotionVo>> newPromotionMap = new HashMap<>(promotionMap);
        //检测默认参与的促销活动是否存在
        CartPromotionVo promotionVo = getCartPromotionVo(skuId, promotionId, sellerId, promotionType);
        //获取当前商品所在店铺(用户选择参与当前店铺的所有活动)选中的促销活动
        List<CartPromotionVo> promotionVos = promotionMap.get(sellerId);
        //检测用户是否选择了该店铺的促销活动
        if (promotionMap != null && promotionMap.size() > 0 && promotionVos != null) {
            invalid = isInvalid(skuId, sellerId, promotionMap, newPromotionMap, promotionVo);
        } else if (promotionVo != null) {
            //默认参与的促销活动存在
            invalid = this.scriptProcess.validTime(promotionVo.getPromotionScript());
            //如果没有设置过促销活动，则默认将促销活动添加到用户选中
            if (invalid != null && invalid) {
                List<CartPromotionVo> newList = new ArrayList<>();
                newList.add(promotionVo);
                newPromotionMap.put(sellerId, newList);
            }
        }

        selectedPromotionVo.setSinglePromotionMap(newPromotionMap);
        String cacheKey = this.getOriginKey();
        cache.put(cacheKey, selectedPromotionVo);
        return invalid == null ? false : invalid;
    }


    @Override
    public void clean() {
        String cacheKey = this.getOriginKey();
        cache.remove(cacheKey);
    }

    @Override
    public void checkPromotionInvalid() {
        SelectedPromotionVo selectedPromotionVo = this.getSelectedPromotion();
        Map<Long, List<CartPromotionVo>> promotionMap = selectedPromotionVo.getSinglePromotionMap();
        Map<Long, CartPromotionVo> groupPromotionMap = selectedPromotionVo.getGroupPromotionMap();
        //检测单品活动
        Map<Long, List<CartPromotionVo>> newPromotionMap = new HashMap<>(promotionMap);
        for (Long key : promotionMap.keySet()) {
            List<CartPromotionVo> promotions = promotionMap.get(key);
            if (promotions == null) {
                continue;
            }
            List<CartPromotionVo> newList = new ArrayList<>();

            for (CartPromotionVo promotionVO : promotions) {
                if (promotionVO == null) {
                    continue;
                }
                Boolean bool = this.scriptProcess.validTime(promotionVO.getPromotionScript());
                if (bool == null || !bool) {
                    promotionVO = null;
                }
                if (promotionVO != null) {
                    newList.add(promotionVO);
                }

            }
            newPromotionMap.remove(key);
            if (newList.size() > 0) {
                newPromotionMap.put(key, newList);
            }

        }
        //检测组合活动
        Map<Long, CartPromotionVo> newGroupPromotionMap = new HashMap<>(groupPromotionMap);
        for (Long key : groupPromotionMap.keySet()) {
            CartPromotionVo cartPromotionVo = groupPromotionMap.get(key);
            Boolean bool = this.scriptProcess.validTime(cartPromotionVo.getPromotionScript());
            if (bool == null || !bool) {
                newGroupPromotionMap.remove(key);
            }
        }
        if (newGroupPromotionMap.size() > 0) {
            selectedPromotionVo.setGroupPromotionMap(newGroupPromotionMap);
        }

        selectedPromotionVo.setSinglePromotionMap(newPromotionMap);
        String cacheKey = this.getOriginKey();
        cache.put(cacheKey, selectedPromotionVo);
    }


    /**
     * 从促销活动列表中删除一批sku的活动
     *
     * @param skuids             skuid数组
     * @param skuPromotionVoList 要清理的活动列表
     * @return 清理后的活动列表
     */
    private List<CartPromotionVo> deleteBySkus(Long[] skuids, List<CartPromotionVo> skuPromotionVoList) {
        List<CartPromotionVo> newList = new ArrayList<>();
        for (CartPromotionVo promotionVO : skuPromotionVoList) {
            //如果skuid数组中不包含，则不压入新list中
            if (!ArrayUtils.contains(skuids, promotionVO.getSkuId())) {
                newList.add(promotionVO);
            }
        }
        return newList;
    }

    /**
     * 获取店铺级别的活动
     *
     * @param sellerId
     * @param promotions
     */
    private void getCartScript(Long sellerId, List<PromotionScriptVO> promotions) {
        List<PromotionScriptVO> cartPromotions = this.scriptProcess.readCartScript(sellerId);
        if (cartPromotions != null && !cartPromotions.isEmpty()) {
            promotions.addAll(cartPromotions);
        }
    }


    /**
     * 检测默认参与的促销活动是否存在
     *
     * @param skuId
     * @param promotionId   促销活动id
     * @param sellerId      店铺id
     * @param promotionType 促销活动类型
     * @return
     */
    private CartPromotionVo getCartPromotionVo(Long skuId, Long promotionId, Long sellerId, String promotionType) {
        CartPromotionVo promotionVo = null;
        if (promotionId != null) {
            //获取商品参与的所有活动
            List<PromotionScriptVO> promotions = this.getSkuScript(skuId);
            getCartScript(sellerId, promotions);
            for (PromotionScriptVO scriptVO : promotions) {
                if (scriptVO == null) {
                    continue;
                }
                if (scriptVO.getPromotionId().equals(promotionId) && scriptVO.getPromotionType().equals(promotionType)) {
                    promotionVo = new CartPromotionVo();
                    BeanUtil.copyProperties(scriptVO, promotionVo);
                    promotionVo.setIsCheck(1);
                    promotionVo.setSkuId(skuId);
                }
            }
        }
        return promotionVo;
    }


    /**
     * 检测活动是否失效
     *
     * @param skuId           skuid
     * @param sellerId        店铺id
     * @param promotionMap    之前选中的促销活动
     * @param newPromotionMap 有效促销活动
     * @param promotionVo     当前选中的促销活动
     * @return
     */
    private Boolean isInvalid(Long skuId, Long sellerId, Map<Long, List<CartPromotionVo>> promotionMap, Map<Long, List<CartPromotionVo>> newPromotionMap, CartPromotionVo promotionVo) {
        Boolean invalid = true;
        List<CartPromotionVo> promotionVos;
        for (Long key : promotionMap.keySet()) {
            List<CartPromotionVo> newList = new ArrayList<>();
            promotionVos = promotionMap.get(sellerId);
            if (key.equals(sellerId) && !promotionVos.contains(promotionVo)) {
                promotionVos.add(promotionVo);
            }
            for (CartPromotionVo promotionVO : promotionVos) {
                if (promotionVO == null) {
                    continue;
                }
                //遍历所有优惠活动验证是否超过其有效时间
                if (skuId.equals(promotionVO.getSkuId())) {
                    Boolean bool = this.scriptProcess.validTime(promotionVO.getPromotionScript());
                    if (bool == null || !bool) {
                        promotionVO = null;
                        invalid = false;
                    }
                }
                if (promotionVO != null) {
                    newList.add(promotionVO);
                }

            }
            newPromotionMap.remove(key);
            if (newList.size() > 0) {
                newPromotionMap.put(key, newList);
            }
        }
        return invalid;
    }


}
