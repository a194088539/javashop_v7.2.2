package com.enation.app.javashop.service.trade.cart.cartbuilder.impl;
import	java.util.HashMap;
import java.util.*;

import com.enation.app.javashop.model.base.CachePrefix;
import com.enation.app.javashop.model.promotion.tool.vo.PromotionScriptVO;
import com.enation.app.javashop.model.trade.cart.vo.*;
import com.enation.app.javashop.service.trade.cart.cartbuilder.ScriptProcess;
import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.framework.util.JsonUtil;
import com.enation.app.javashop.framework.util.ScriptUtil;
import com.enation.app.javashop.framework.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * @description:
 * @author: liuyulei
 * @create: 2020-01-10 16:29
 * @version:1.0
 * @since:7.1.4
 **/
@Service
public class ScriptProcessImpl implements ScriptProcess {

    @Autowired
    private Cache cache;

    private static final Logger log = LoggerFactory.getLogger(ScriptProcessImpl.class);

    @Override
    public List<PromotionScriptVO> readSkuScript(List<CartSkuVO> skuList){
        List<String> skuKeys = new ArrayList<>();
        for (CartSkuVO cartSkuVO :skuList){
            skuKeys.add(CachePrefix.SKU_PROMOTION.getPrefix() +cartSkuVO.getSkuId());
        }
        List<List<PromotionScriptVO>> skuScripts = cache.multiGet(skuKeys);
        List<PromotionScriptVO> result  = new ArrayList<>();
        for (List<PromotionScriptVO> scriptVO:skuScripts) {
            if(scriptVO != null && !scriptVO.isEmpty()){
                result.addAll(scriptVO);
            }
        }
        return result;
    }

    @Override
    public List<PromotionScriptVO> readSkuScript(Long skuId) {
        return (List<PromotionScriptVO>) cache.get(CachePrefix.SKU_PROMOTION.getPrefix() + skuId);
    }

    @Override
    public Double getShipPrice(String script, Map<String, Object> params) {
        try {
            //计算运费  返回运费金额
            Object price = ScriptUtil.executeScript("getShipPrice",params,script);
            //设置返回值
            Double result = Double.parseDouble(price.toString());
            log.debug("入参" + JsonUtil.objectToJson(params));
            log.debug("script 脚本：" + script);
            log.debug("根据脚本计算运费：");
            log.debug(result.toString());
            return result;
        } catch (Exception e) {
            log.error("根据脚本计算运费异常:");
            log.error(e.getMessage());
        }
        return 0D;
    }

    @Override
    public String readCouponScript(Long couponId) {
        //优惠券级别缓存key
        String cacheKey = CachePrefix.COUPON_PROMOTION.getPrefix() + couponId;
        return (String) this.cache.get(cacheKey);
    }


    @Override
    public List<PromotionScriptVO> readCartScript(Long sellerId){
        List<PromotionScriptVO> scriptVO = (List<PromotionScriptVO>) cache.get(CachePrefix.CART_PROMOTION.getPrefix() + sellerId);
        return scriptVO;
    }

    /**
     * 获取活动是否有效
     * @param script
     * @return  true：活动有效，false:活动无效
     */
    @Override
    public Boolean validTime(String script){
        try {
            Map params = new HashMap();
            params.put("$currentTime", DateUtil.getDateline());
            Boolean result = (Boolean) ScriptUtil.executeScript("validTime",params,script);
            log.debug("script 脚本：" + script);
            log.debug("根据促销脚本判断活动是否有效：");
            log.debug(result);
            return result;
        } catch (Exception e) {
            log.error("根据促销脚本计算价格异常:",e);
        }
        return false;
    }



    @Override
    public Double countPrice(String script,Map<String,Object> params){
        try {
            //计算促销优惠  返回优惠后的金额
            Object price = ScriptUtil.executeScript("countPrice",params,script);
            //设置返回值
            Double result = Double.parseDouble(price.toString());
            log.debug("入参" + JsonUtil.objectToJson(params));
            log.debug("script 脚本：" + script);
            log.debug("根据促销脚本计算价格：");
            log.debug(result.toString());
            return result;
        } catch (Exception e) {
            log.error("根据促销脚本计算价格异常:");
            log.error(e.getMessage());
        }
        return 0D;
    }


    @Override
    public String giveGift(String script,Map<String,Object> params){
        try {
            String result =  ScriptUtil.executeScript("giveGift",params,script).toString();
            log.debug("script 脚本：" + script);
            log.debug("赠送赠品信息：");
            log.debug(result);
            return result;
        } catch (Exception e) {
            log.error("从促销脚本中获取赠品信息失败!");
        }
        return "";
    }

    @Override
    public Integer countPoint(String script,Map<String,Object> params) {
        try {
            if(StringUtil.isEmpty(script)){
                return 0;
            }
            //获取消费的积分
            Object obj = ScriptUtil.executeScript("countPoint",params,script);
            Integer result = Integer.parseInt(obj.toString());
            log.debug("script 脚本：" + script);
            log.debug("积分兑换：");
            log.debug(result + "");
            return result;
        } catch (Exception e) {
            log.error("从促销脚本中获取积分商品信息失败!",e);
        }
        return 0;
    }


}
