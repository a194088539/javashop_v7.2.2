package com.enation.app.javashop.service.trade.pintuan.impl;

import com.enation.app.javashop.model.base.CachePrefix;
import com.enation.app.javashop.model.promotion.pintuan.Pintuan;
import com.enation.app.javashop.model.promotion.pintuan.PintuanGoodsDO;
import com.enation.app.javashop.service.trade.pintuan.PintuanManager;
import com.enation.app.javashop.service.trade.pintuan.PintuanScriptManager;
import com.enation.app.javashop.model.promotion.tool.enums.PromotionTypeEnum;
import com.enation.app.javashop.model.promotion.tool.vo.PromotionScriptVO;
import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import com.enation.app.javashop.framework.util.ScriptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 拼团促销活动脚本业务接口实现
 * @author duanmingyu
 * @version v1.0
 * @since v7.1.5
 * 2020-02-17
 */
@Service
public class PintuanScriptManagerImpl implements PintuanScriptManager {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Cache cache;

    @Autowired
    private PintuanManager pintuanManager;

    @Override
    public void createCacheScript(Long promotionId, List<PintuanGoodsDO> goodsList) {
        //如果参与拼团促销活动的商品集合不为空并且集合长度不为0
        if (goodsList != null && goodsList.size() != 0) {
            //获取拼团活动详情
            Pintuan pintuan = this.pintuanManager.getModel(promotionId);
            //批量放入缓存的脚本数据集合
            Map<String, List<PromotionScriptVO>> cacheMap = new HashMap<>();

            //循环参与拼团促销活动的商品集合，创建商品的拼团促销活动脚本数据结构
            for (PintuanGoodsDO goods : goodsList) {

                //缓存key
                String cacheKey = CachePrefix.SKU_PROMOTION.getPrefix() + goods.getSkuId();

                //获取拼团活动脚本信息
                PromotionScriptVO scriptVO = new PromotionScriptVO();

                //渲染并读取满减满赠促销活动脚本信息
                String script = renderScript(pintuan.getStartTime().toString(), pintuan.getEndTime().toString(), goods.getSalesPrice());

                scriptVO.setPromotionScript(script);
                scriptVO.setPromotionId(promotionId);
                scriptVO.setPromotionType(PromotionTypeEnum.PINTUAN.name());
                scriptVO.setIsGrouped(false);
                scriptVO.setPromotionName("拼团活动");
                scriptVO.setSkuId(goods.getSkuId());

                //从缓存中读取脚本信息
                List<PromotionScriptVO> scriptList = (List<PromotionScriptVO>) cache.get(cacheKey);
                if (scriptList == null) {
                    scriptList = new ArrayList<>();
                }

                scriptList.add(scriptVO);

                cacheMap.put(cacheKey, scriptList);
            }

            //将参与拼团活动商品的促销脚本数据批量放入缓存中
            cache.multiSet(cacheMap);
        }
    }

    @Override
    public void deleteCacheScript(Long promotionId, List<PintuanGoodsDO> goodsList) {
        //如果参与拼团促销活动的商品集合不为空并且集合长度不为0
        if (goodsList != null && goodsList.size() != 0) {

            //需要批量更新的缓存数据集合
            Map<String, List<PromotionScriptVO>> updateCacheMap = new HashMap<>();

            //需要批量删除的缓存key集合
            List<String> delKeyList = new ArrayList<>();

            for (PintuanGoodsDO goods : goodsList) {
                //缓存key
                String cacheKey = CachePrefix.SKU_PROMOTION.getPrefix() + goods.getSkuId();

                //从缓存中读取促销脚本缓存
                List<PromotionScriptVO> scriptCacheList = (List<PromotionScriptVO>) cache.get(cacheKey);

                if (scriptCacheList != null && scriptCacheList.size() != 0) {

                    //循环促销脚本缓存数据集合
                    for (PromotionScriptVO script : scriptCacheList) {

                        //如果脚本数据的促销活动信息与当前修改的促销活动信息一致，那么就将此信息删除
                        if (PromotionTypeEnum.PINTUAN.name().equals(script.getPromotionType())
                                && script.getPromotionId().intValue() == promotionId.intValue()) {
                            scriptCacheList.remove(script);
                            break;
                        }
                    }

                    if (scriptCacheList.size() == 0) {
                        delKeyList.add(cacheKey);
                    } else {
                        updateCacheMap.put(cacheKey, scriptCacheList);
                    }
                }
            }

            cache.multiDel(delKeyList);
            cache.multiSet(updateCacheMap);
        }

    }

    /**
     * 渲染并读取拼团促销活动脚本信息
     * @param startTime 活动开始时间
     * @param endTime 活动结束时间
     * @param price 拼团商品价格
     * @return
     */
    private String renderScript(String startTime, String endTime, Double price) {
        Map<String, Object> model = new HashMap<>();

        Map<String, Object> params = new HashMap<>();
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        params.put("price", price);

        model.put("promotionActive", params);

        String path = "single_promotion.ftl";
        String script = ScriptUtil.renderScript(path, model);

        logger.debug("生成拼团促销活动脚本：" + script);

        return script;
    }

}
