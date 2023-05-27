package com.enation.app.javashop.client.promotion.impl;

import com.enation.app.javashop.client.promotion.PromotionGoodsClient;
import com.enation.app.javashop.model.promotion.fulldiscount.vo.FullDiscountVO;
import com.enation.app.javashop.model.promotion.halfprice.vo.HalfPriceVO;
import com.enation.app.javashop.model.promotion.minus.vo.MinusVO;
import com.enation.app.javashop.model.promotion.seckill.dos.SeckillApplyDO;
import com.enation.app.javashop.model.promotion.tool.dos.PromotionGoodsDO;
import com.enation.app.javashop.model.promotion.tool.dto.PromotionDTO;
import com.enation.app.javashop.service.promotion.fulldiscount.FullDiscountManager;
import com.enation.app.javashop.service.promotion.groupbuy.GroupbuyGoodsManager;
import com.enation.app.javashop.service.promotion.halfprice.HalfPriceManager;
import com.enation.app.javashop.service.promotion.minus.MinusManager;
import com.enation.app.javashop.service.promotion.seckill.SeckillGoodsManager;
import com.enation.app.javashop.model.promotion.tool.vo.PromotionVO;
import com.enation.app.javashop.service.promotion.tool.PromotionGoodsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 促销商品客户端实现
 *
 * @author zh
 * @version v7.0
 * @date 19/3/28 上午11:30
 * @since v7.0
 */
@Service
@ConditionalOnProperty(value = "javashop.product", havingValue = "stand")
public class PromotionGoodsClientImpl implements PromotionGoodsClient {

    @Autowired
    private PromotionGoodsManager promotionGoodsManager;

    @Autowired
    private SeckillGoodsManager seckillGoodsManager;

    @Autowired
    private GroupbuyGoodsManager groupbuyGoodsManager;

    @Autowired
    private FullDiscountManager fullDiscountManager;

    @Autowired
    private MinusManager minusManager;

    @Autowired
    private HalfPriceManager halfPriceManager;


    @Override
    public void delPromotionGoods(Long goodsId, String type, Long activityId) {
        promotionGoodsManager.delete(goodsId, activityId, type);
    }


    @Override
    public void delPromotionGoods(List<Long> delSkuIds) {
        //删除促销商品
        promotionGoodsManager.delete(delSkuIds);
    }

    @Override
    public List<PromotionVO> getPromotion(Long goodsId) {
        return promotionGoodsManager.getPromotion(goodsId);
    }

    @Override
    public void rollbackSeckillStock(List<PromotionDTO> promotionDTOList) {
        seckillGoodsManager.rollbackStock(promotionDTOList);
    }

    @Override
    public boolean addSeckillSoldNum(List<PromotionDTO> promotionDTOList) {
        return seckillGoodsManager.addSoldNum(promotionDTOList);
    }

    @Override
    public List<SeckillApplyDO> getSeckillGoodsList(Long seckillId, String status) {
        return seckillGoodsManager.getGoodsList(seckillId, status);
    }

    @Override
    public void rollbackGroupbuyStock(List<PromotionDTO> promotionDTOList, String orderSn) {
        groupbuyGoodsManager.rollbackStock(promotionDTOList, orderSn);
    }

    @Override
    public boolean cutGroupbuyQuantity(String orderSn, List<PromotionDTO> promotionDTOList) {
        return groupbuyGoodsManager.cutQuantity(orderSn, promotionDTOList);
    }

    @Override
    public void updateGroupbuyGoodsInfo(Long[] goodsIds) {
        groupbuyGoodsManager.updateGoodsInfo(goodsIds);
    }

    @Override
    public void addGroupbuyQuantity(String orderSn) {
        groupbuyGoodsManager.addQuantity(orderSn);
    }

    @Override
    public boolean renewGroupbuyBuyNum(Long goodid, Integer num, Long productId) {
        return groupbuyGoodsManager.renewBuyNum(goodid, num, productId);
    }

    @Override
    public List<PromotionGoodsDO> getPromotionGoods(Long activityId, String promotionType) {
        return promotionGoodsManager.getPromotionGoods(activityId, promotionType);
    }

    @Override
    public FullDiscountVO getFullDiscountModel(Long fdId) {
        return fullDiscountManager.getModel(fdId);
    }

    @Override
    public MinusVO getMinusFromDB(Long minusId) {
        return minusManager.getFromDB(minusId);
    }

    @Override
    public HalfPriceVO getHalfPriceFromDB(Long id) {
        return halfPriceManager.getFromDB(id);
    }

    @Override
    public List<PromotionGoodsDO> getPromotionGoodsList(String promotionType, Long nowTime) {
        return promotionGoodsManager.getPromotionGoodsList(promotionType, nowTime);
    }

    @Override
    public void updatePromotionEndTime(Long endTime, Long pgId) {
        promotionGoodsManager.updatePromotionEndTime(endTime, pgId);
    }
}
