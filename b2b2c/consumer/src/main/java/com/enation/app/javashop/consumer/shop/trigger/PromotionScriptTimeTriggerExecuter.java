package com.enation.app.javashop.consumer.shop.trigger;

import com.enation.app.javashop.client.promotion.PromotionGoodsClient;
import com.enation.app.javashop.model.base.CachePrefix;
import com.enation.app.javashop.model.base.message.PromotionScriptMsg;
import com.enation.app.javashop.model.base.vo.ScriptVO;
import com.enation.app.javashop.model.base.rabbitmq.TimeExecute;
import com.enation.app.javashop.model.promotion.fulldiscount.vo.FullDiscountVO;
import com.enation.app.javashop.model.promotion.halfprice.vo.HalfPriceVO;
import com.enation.app.javashop.model.promotion.minus.vo.MinusVO;
import com.enation.app.javashop.model.promotion.tool.dos.PromotionGoodsDO;
import com.enation.app.javashop.model.promotion.tool.enums.PromotionTypeEnum;
import com.enation.app.javashop.model.promotion.tool.enums.ScriptOperationTypeEnum;
import com.enation.app.javashop.model.promotion.tool.vo.PromotionScriptVO;
import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import com.enation.app.javashop.framework.trigger.Interface.TimeTrigger;
import com.enation.app.javashop.framework.trigger.Interface.TimeTriggerExecuter;
import com.enation.app.javashop.framework.util.BeanUtil;
import com.enation.app.javashop.framework.util.CurrencyUtil;
import com.enation.app.javashop.framework.util.JsonUtil;
import com.enation.app.javashop.framework.util.ScriptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商家促销活动脚本生成和删除延时任务执行器
 * 针对的是满减满赠、单品立减、第二件半价三种活动
 * 这三种活动只有商家进行操作，平台没有参与
 * @author duanmingyu
 * @version v1.0
 * @since v7.2.0
 * 2020-02-14
 */
@Component("promotionScriptTimeTriggerExecuter")
public class PromotionScriptTimeTriggerExecuter implements TimeTriggerExecuter {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TimeTrigger timeTrigger;

    @Autowired
    private PromotionGoodsClient promotionGoodsClient;

    @Autowired
    private Cache cache;

    @Override
    public void execute(Object object) {

        PromotionScriptMsg promotionScriptMsg = (PromotionScriptMsg) object;

        //如果是促销活动开始
        if (ScriptOperationTypeEnum.CREATE.equals(promotionScriptMsg.getOperationType())) {

            //创建促销活动脚本
            this.createScript(promotionScriptMsg);

            //促销活动开始后，立马设置一个促销活动结束的流程
            promotionScriptMsg.setOperationType(ScriptOperationTypeEnum.DELETE);
            String uniqueKey = "{TIME_TRIGGER_" + promotionScriptMsg.getPromotionType().name() + "}_" + promotionScriptMsg.getPromotionId();

            timeTrigger.add(TimeExecute.SELLER_PROMOTION_SCRIPT_EXECUTER, promotionScriptMsg, promotionScriptMsg.getEndTime(), uniqueKey);

            this.logger.debug("促销活动[" + promotionScriptMsg.getPromotionName() + "]开始，id=[" + promotionScriptMsg.getPromotionId() + "]");
        } else {

            //删除缓存中的促销脚本数据
            this.deleteScript(promotionScriptMsg);

            this.logger.debug("促销活动[" + promotionScriptMsg.getPromotionName() + "]结束，id=[" + promotionScriptMsg.getPromotionId() + "]");
        }
    }

    /**
     * 创建促销活动脚本
     * @param promotionScriptMsg 促销活动脚本消息信息
     */
    private void createScript(PromotionScriptMsg promotionScriptMsg) {
        //获取促销活动类型
        PromotionTypeEnum promotionType = promotionScriptMsg.getPromotionType();

        //获取促销活动ID
        Long promotionId = promotionScriptMsg.getPromotionId();

        //获取促销脚本相关数据
        ScriptVO scriptVO = this.getPromotionScript(promotionId, promotionType.name());

        //获取商家ID
        Long sellerId = scriptVO.getSellerId();
        //购物车(店铺)级别缓存key
        String cartCacheKey = CachePrefix.CART_PROMOTION.getPrefix() + sellerId;
        //获取商品参与促销活动的方式 1：全部商品参与，2：部分商品参与
        Integer rangeType = scriptVO.getRangeType();
        //构建促销脚本数据结构
        PromotionScriptVO promotionScriptVO = scriptVO.getPromotionScriptVO();

        //如果是全部商品都参与了促销活动
        if (rangeType.intValue() == 1) {

            //构建新的促销脚本数据
            List<PromotionScriptVO> scriptList = getScriptList(cartCacheKey, promotionScriptVO);

            //将促销脚本数据放入缓存中
            cache.put(cartCacheKey, scriptList);

        } else {
            //获取参与促销活动的商品集合
            List<PromotionGoodsDO> goodsList =  scriptVO.getGoodsList();

            //批量放入缓存的数据集合
            Map<String, List<PromotionScriptVO>> cacheMap = new HashMap<>();

            //循环skuID集合，将脚本放入缓存中
            for (PromotionGoodsDO goods : goodsList) {

                PromotionScriptVO newScript = new PromotionScriptVO();
                BeanUtil.copyProperties(promotionScriptVO, newScript);

                //缓存key
                String cacheKey = CachePrefix.SKU_PROMOTION.getPrefix() + goods.getSkuId();

                //脚本结构中加入商品skuID
                newScript.setSkuId(goods.getSkuId());

                //构建新的促销脚本数据
                List<PromotionScriptVO> scriptList = getScriptList(cacheKey, newScript);

                cacheMap.put(cacheKey, scriptList);
            }

            //将sku促销脚本数据批量放入缓存中
            cache.multiSet(cacheMap);

        }
    }

    /**
     * 删除促销活动脚本
     * @param promotionScriptMsg 促销活动脚本消息信息
     */
    private void deleteScript(PromotionScriptMsg promotionScriptMsg) {
        //获取促销活动类型
        PromotionTypeEnum promotionType = promotionScriptMsg.getPromotionType();

        //获取促销活动ID
        Long promotionId = promotionScriptMsg.getPromotionId();

        if (PromotionTypeEnum.FULL_DISCOUNT.equals(promotionType)) {
            //获取满减满赠促销活动详细信息
            FullDiscountVO fullDiscountVO = this.promotionGoodsClient.getFullDiscountModel(promotionId);
            //初始化缓存中的促销活动脚本信息
            initScriptCache(fullDiscountVO.getRangeType(), fullDiscountVO.getSellerId(), promotionId, promotionType.name());

        } else if (PromotionTypeEnum.MINUS.equals(promotionType)) {
            //获取单品立减促销活动信息
            MinusVO minusVO = this.promotionGoodsClient.getMinusFromDB(promotionId);
            //初始化缓存中的促销活动脚本信息
            initScriptCache(minusVO.getRangeType(), minusVO.getSellerId(), promotionId, promotionType.name());

        } else if (PromotionTypeEnum.HALF_PRICE.equals(promotionType)) {
            //获取第二件半价促销活动信息
            HalfPriceVO halfPriceVO = this.promotionGoodsClient.getHalfPriceFromDB(promotionId);
            //初始化缓存中的促销活动脚本信息
            initScriptCache(halfPriceVO.getRangeType(), halfPriceVO.getSellerId(), promotionId, promotionType.name());
        }
    }

    /**
     * 初始化缓存中的促销活动脚本信息
     * @param rangeType 商品参与促销活动类型 1：全部商品参与，2：部分商品参与
     * @param sellerId 商家ID
     * @param promotionId 促销活动ID
     * @param promotionType 促销活动类型
     */
    private void initScriptCache(Integer rangeType, Long sellerId, Long promotionId, String promotionType) {
        //如果是全部商品参与促销活动
        if (rangeType.intValue() == 1) {
            //购物车(店铺)级别缓存key
            String cartCacheKey = CachePrefix.CART_PROMOTION.getPrefix() + sellerId;

            //初始化缓存中的促销活动脚本信息数据
            deleteCache(cartCacheKey, promotionId, promotionType);

        } else {
            //获取参与促销活动的商品集合
            List<PromotionGoodsDO> goodsList = this.promotionGoodsClient.getPromotionGoods(promotionId, promotionType);

            for (PromotionGoodsDO goodsDO : goodsList) {
                //SKU级别缓存key
                String cacheKey = CachePrefix.SKU_PROMOTION.getPrefix() + goodsDO.getSkuId();

                //初始化缓存中的促销活动脚本信息数据
                deleteCache(cacheKey, promotionId, promotionType);
            }
        }
    }

    /**
     * 清除缓存中的促销活动脚本数据
     * @param cacheKey 缓存key
     * @param promotionId 促销活动ID
     * @param promotionType 促销活动类型
     */
    private void deleteCache(String cacheKey, Long promotionId, String promotionType) {
        //从缓存中读取脚本信息
        List<PromotionScriptVO> scriptList = (List<PromotionScriptVO>) cache.get(cacheKey);
        //如果缓存中的脚本信息集合长度为1，证明只有当前一个促销活动脚本信息，直接删除即可
        if (scriptList.size() == 1) {
            cache.remove(cacheKey);
        } else {
            for (PromotionScriptVO scriptVO : scriptList) {
                //如果促销活动ID相等并且促销活动类型相等
                if (promotionId.equals(scriptVO.getPromotionId()) && promotionType.equals(scriptVO.getPromotionType())) {
                    scriptList.remove(scriptVO);
                    break;
                }
            }
            //将筛选出的促销活动脚本集合重新放入缓存中
            cache.put(cacheKey, scriptList);
        }
    }

    /**
     * 根据促销活动ID和促销活动类型获取促销脚本数据
     * @param promotionId 促销活动id
     * @param promotionType 促销活动类型
     * @return
     */
    private ScriptVO getPromotionScript(Long promotionId, String promotionType) {

        ScriptVO scriptVO = new ScriptVO();

        if (PromotionTypeEnum.FULL_DISCOUNT.name().equals(promotionType)) {
            //获取满减满赠促销活动详细信息
            FullDiscountVO fullDiscountVO = this.promotionGoodsClient.getFullDiscountModel(promotionId);

            //渲染并读取满减满赠促销活动脚本信息
            String script = renderFullDiscountScript(fullDiscountVO);

            //构建促销脚本数据结构
            PromotionScriptVO promotionScriptVO = getScript(script, promotionId, true, promotionType, getFullDiscountTips(fullDiscountVO));

            scriptVO.setSellerId(fullDiscountVO.getSellerId());
            scriptVO.setRangeType(fullDiscountVO.getRangeType());
            scriptVO.setPromotionScriptVO(promotionScriptVO);

        } else if (PromotionTypeEnum.MINUS.name().equals(promotionType)) {

            //获取单品立减促销活动信息
            MinusVO minusVO = this.promotionGoodsClient.getMinusFromDB(promotionId);

            //渲染并读取单品立减促销活动脚本信息
            String script = renderMinusScript(minusVO);

            String tips = "单品立减";

            //构建促销脚本数据结构
            PromotionScriptVO promotionScriptVO = getScript(script, promotionId, false, promotionType, tips);

            scriptVO.setSellerId(minusVO.getSellerId());
            scriptVO.setRangeType(minusVO.getRangeType());
            scriptVO.setPromotionScriptVO(promotionScriptVO);

        } else if (PromotionTypeEnum.HALF_PRICE.name().equals(promotionType)) {

            //获取第二件半价促销活动信息
            HalfPriceVO halfPriceVO = this.promotionGoodsClient.getHalfPriceFromDB(promotionId);

            //渲染并读取第二件半价促销活动脚本信息
            String script = renderHalfPriceScript(halfPriceVO);

            String tips = "第二件半价";

            //构建促销脚本数据结构
            PromotionScriptVO promotionScriptVO = getScript(script, promotionId, false, promotionType, tips);

            scriptVO.setSellerId(halfPriceVO.getSellerId());
            scriptVO.setRangeType(halfPriceVO.getRangeType());
            scriptVO.setPromotionScriptVO(promotionScriptVO);

        }

        //如果是部分商品参与活动，需要查询出参与促销活动的商品skuID集合
        if (scriptVO.getRangeType().intValue() == 2) {
            List<PromotionGoodsDO> goodsList = this.promotionGoodsClient.getPromotionGoods(promotionId, promotionType);
            scriptVO.setGoodsList(goodsList);
        }

        return scriptVO;
    }

    /**
     * 获取促销脚本数据公共方法
     * @param cacheKey 缓存key
     * @param promotionScriptVO 新生成的促销脚本数据
     * @return
     */
    private List<PromotionScriptVO> getScriptList(String cacheKey, PromotionScriptVO promotionScriptVO) {
        //从缓存中读取脚本信息
        List<PromotionScriptVO> scriptList = (List<PromotionScriptVO>) cache.get(cacheKey);
        if (scriptList == null) {
            scriptList = new ArrayList<>();
        }

        scriptList.add(promotionScriptVO);

        return scriptList;
    }

    /**
     * 获取脚本实体公共方法
     * @param script 脚本信息
     * @param promotionId 促销活动ID
     * @param isGroup 是否可以被组合
     * @param promotionType 促销活动类型
     * @param promotionName 促销活动名称
     * @return
     */
    private PromotionScriptVO getScript(String script, Long promotionId, boolean isGroup, String promotionType, String promotionName) {
        //构建促销脚本数据结构
        PromotionScriptVO promotionScriptVO = new PromotionScriptVO();
        promotionScriptVO.setPromotionScript(script);
        promotionScriptVO.setPromotionId(promotionId);
        promotionScriptVO.setIsGrouped(isGroup);
        promotionScriptVO.setPromotionType(promotionType);
        promotionScriptVO.setPromotionName(promotionName);
        return promotionScriptVO;
    }

    /**
     * 渲染并读取满减满赠促销活动脚本信息
     * @param fullDiscountVO 满减满赠促销活动信息
     * @return
     */
    private String renderFullDiscountScript(FullDiscountVO fullDiscountVO) {
        Map<String, Object> model = new HashMap<>();

        Map<String, Object> params = new HashMap<>();
        params.put("startTime", fullDiscountVO.getStartTime().toString());
        params.put("endTime", fullDiscountVO.getEndTime().toString());
        params.put("isFullMinus", fullDiscountVO.getIsFullMinus());
        params.put("isDiscount", fullDiscountVO.getIsDiscount());
        params.put("fullMoney", fullDiscountVO.getFullMoney());
        params.put("minusValue", fullDiscountVO.getMinusValue() == null ? 0.00 : fullDiscountVO.getMinusValue());
        params.put("discountValue", fullDiscountVO.getDiscountValue() == null ? 0.00 : CurrencyUtil.div(fullDiscountVO.getDiscountValue(), 10, 2));

        List<Map<String, Object>> giftList = new ArrayList<>();

        //判断是否免邮费
        if (fullDiscountVO.getIsFreeShip().intValue() == 1) {
            Map<String, Object> free = new HashMap<>();
            free.put("type", "freeShip");
            free.put("value", true);
            giftList.add(free);
        }

        //判断是否送积分
        if (fullDiscountVO.getIsSendPoint().intValue() == 1) {
            Map<String, Object> point = new HashMap<>();
            point.put("type", "point");
            point.put("value", fullDiscountVO.getPointValue());
            giftList.add(point);
        }

        //判断是否送赠品
        if (fullDiscountVO.getIsSendGift().intValue() == 1 ) {
            Map<String, Object> gift = new HashMap<>();
            gift.put("type", "gift");
            gift.put("value", fullDiscountVO.getGiftId().toString());
            giftList.add(gift);
        }

        //判断是否送优惠券
        if (fullDiscountVO.getIsSendBonus().intValue() == 1) {
            Map<String, Object> coupon = new HashMap<>();
            coupon.put("type", "coupon");
            coupon.put("value", fullDiscountVO.getBonusId().toString());
            giftList.add(coupon);
        }

        params.put("gift", JsonUtil.objectToJson(giftList));

        model.put("promotionActive", params);

        String path = "full_discount.ftl";
        String script = ScriptUtil.renderScript(path, model);

        logger.debug("生成满减满赠促销活动脚本：" + script);

        return script;
    }

    /**
     * 渲染并读取单品立减促销活动脚本信息
     * @param minusVO 单品立减促销活动信息
     * @return
     */
    private String renderMinusScript(MinusVO minusVO) {
        Map<String, Object> model = new HashMap<>();

        Map<String, Object> params = new HashMap<>();
        params.put("startTime", minusVO.getStartTime().toString());
        params.put("endTime", minusVO.getEndTime().toString());
        params.put("singleReductionValue", minusVO.getSingleReductionValue());

        model.put("promotionActive", params);

        String path = "minus.ftl";
        String script = ScriptUtil.renderScript(path, model);

        logger.debug("生成单品立减促销活动脚本：" + script);

        return script;
    }

    /**
     * 渲染并读取第二件半价促销活动脚本信息
     * @param halfPriceVO 第二件半价促销活动信息
     * @return
     */
    private String renderHalfPriceScript(HalfPriceVO halfPriceVO) {
        Map<String, Object> model = new HashMap<>();

        Map<String, Object> params = new HashMap<>();
        params.put("startTime", halfPriceVO.getStartTime().toString());
        params.put("endTime", halfPriceVO.getEndTime().toString());

        model.put("promotionActive", params);

        String path = "half_price.ftl";
        String script = ScriptUtil.renderScript(path, model);

        logger.debug("生成第二件半价促销活动脚本：" + script);

        return script;
    }

    /**
     * 获取满减满赠促销活动提示
     * @param fullDiscountVO 满减满赠促销活动信息
     * @return
     */
    private String getFullDiscountTips(FullDiscountVO fullDiscountVO) {
        String tips = "满" + fullDiscountVO.getFullMoney() + "元";

        if (fullDiscountVO.getIsFullMinus().intValue() == 1) {
            tips += "减" + fullDiscountVO.getMinusValue() + "元";
        }

        if (fullDiscountVO.getIsDiscount().intValue() == 1) {
            tips += "打" + fullDiscountVO.getDiscountValue() + "折";
        }

        if (fullDiscountVO.getIsSendPoint().intValue() == 1) {
            tips += "送" + fullDiscountVO.getPointValue() + "积分";
        }

        if (fullDiscountVO.getIsFreeShip().intValue() == 1) {
            tips += "免邮费";
        }

        if (fullDiscountVO.getIsSendGift().intValue() == 1) {
            tips += "送赠品";
        }

        if (fullDiscountVO.getIsSendBonus().intValue() == 1) {
            tips += "送优惠券";
        }

        return tips;
    }
}
