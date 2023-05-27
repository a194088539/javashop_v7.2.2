package com.enation.app.javashop.model.promotion.tool.dos;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enation.app.javashop.framework.database.annotation.Column;
import com.enation.app.javashop.framework.database.annotation.PrimaryKeyField;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
 * 有效活动商品对照表实体
 *
 * @author Snow
 * @version v7.0.0
 * @since v7.0.0
 * 2018-03-27 15:17:47
 */
@TableName("es_promotion_goods")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PromotionGoodsDO implements Serializable {

    private static final long serialVersionUID = 686823101861419L;

    /**
     * 商品对照ID
     */
    @TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(hidden = true)
    private Long pgId;

    /**
     * 商品id
     */
    @ApiModelProperty(name = "goods_id", value = "商品id", required = false)
    private Long goodsId;

    /**
     * 货品id
     */
    @ApiModelProperty(name = "sku_id", value = "货品id", required = false)
    private Long skuId;

    /**
     * 活动开始时间
     */
    @ApiModelProperty(name = "start_time", value = "活动开始时间", required = false)
    private Long startTime;

    /**
     * 活动结束时间
     */
    @Column(name = "end_time")
    @ApiModelProperty(name = "end_time", value = "活动结束时间", required = false)
    private Long endTime;

    /**
     * 活动id
     */
    @ApiModelProperty(name = "activity_id", value = "活动id", required = false)
    private Long activityId;

    /**
     * 促销工具类型
     */
    @ApiModelProperty(name = "promotion_type", value = "促销工具类型", required = false)
    private String promotionType;

    /**
     * 活动标题
     */
    @ApiModelProperty(name = "title", value = "活动标题", required = false)
    private String title;

    /**
     * 参与活动的商品数量
     */
    @ApiModelProperty(name = "num", value = "参与活动的商品数量", required = false)
    private Integer num;

    /**
     * 活动时商品的价格
     */
    @ApiModelProperty(name = "price", value = "活动时商品的价格", required = false)
    private Double price;

    @ApiModelProperty(name = "seller_id", value = "商家ID", required = false)
    private Long sellerId;

    @PrimaryKeyField
    public Long getPgId() {
        return pgId;
    }

    public void setPgId(Long pgId) {
        this.pgId = pgId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
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

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public String getPromotionType() {
        return promotionType;
    }

    public void setPromotionType(String promotionType) {
        this.promotionType = promotionType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    @Override
    public String toString() {
        return "PromotionGoodsDO{" +
                "pgId=" + pgId +
                ", goodsId=" + goodsId +
                ", skuId=" + skuId +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", activityId=" + activityId +
                ", promotionType='" + promotionType + '\'' +
                ", title='" + title + '\'' +
                ", num=" + num +
                ", price=" + price +
                ", sellerId=" + sellerId +
                '}';
    }
}
