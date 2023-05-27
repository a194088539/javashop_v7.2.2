package com.enation.app.javashop.model.promotion.tool.vo;

/**
 * 反常促销商品
 * 目前用于检查商品参与活动，返回商品与活动关联的对象
 *
 * @author Chopper
 * @version v1.0
 * @since v7.0
 * 2019-02-28 上午5:43
 */
public class PromotionAbnormalGoods {


    /**
     * 商品id
     */
    private Long goodsId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 开始时间
     */
    private Long startTime;


    /**
     * 结束时间
     */
    private Long endTime;

    /**
     * 活动类型
     */
    private String promotionType;

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getPromotionType() {
        return promotionType;
    }

    public void setPromotionType(String promotionType) {
        this.promotionType = promotionType;
    }

    @Override
    public String toString() {
        return "PromotionGoodsDTO{" +
                "goodsId=" + goodsId +
                ", goodsName='" + goodsName + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", promotionType='" + promotionType + '\'' +
                '}';
    }
}
