package com.enation.app.javashop.model.base.vo;

import com.enation.app.javashop.model.promotion.tool.dos.PromotionGoodsDO;
import com.enation.app.javashop.model.promotion.tool.vo.PromotionScriptVO;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;

import java.io.Serializable;
import java.util.List;

/**
 * 促销脚本VO
 * @author duanmingyu
 * @version v1.0
 * @since v7.2.0
 * 2020-01-14
 */
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ScriptVO implements Serializable {

    private static final long serialVersionUID = 8127789064747729418L;

    /**
     * 商家ID
     */
    private Long sellerId;

    /**
     * 商品参与促销活动类型 1：全部参与，2：部分参与
     */
    private Integer rangeType;

    /**
     * 参与促销活动的商品skuID集合
     */
    private List<PromotionGoodsDO> goodsList;

    /**
     * 促销脚本数据
     */
    private PromotionScriptVO promotionScriptVO;

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public Integer getRangeType() {
        return rangeType;
    }

    public void setRangeType(Integer rangeType) {
        this.rangeType = rangeType;
    }

    public List<PromotionGoodsDO> getGoodsList() {
        return goodsList;
    }

    public void setGoodsList(List<PromotionGoodsDO> goodsList) {
        this.goodsList = goodsList;
    }

    public PromotionScriptVO getPromotionScriptVO() {
        return promotionScriptVO;
    }

    public void setPromotionScriptVO(PromotionScriptVO promotionScriptVO) {
        this.promotionScriptVO = promotionScriptVO;
    }

    @Override
    public String toString() {
        return "ScriptVO{" +
                "sellerId=" + sellerId +
                ", rangeType=" + rangeType +
                ", goodsList=" + goodsList +
                ", promotionScriptVO=" + promotionScriptVO +
                '}';
    }
}
