package com.enation.app.javashop.model.promotion.fulldiscount.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enation.app.javashop.framework.database.annotation.PrimaryKeyField;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


/**
 * 满优惠赠品实体
 * @author Snow
 * @version v7.0.0
 * @since v7.0.0
 * 2018-03-30 17:34:46
 */
@TableName(value = "es_full_discount_gift")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FullDiscountGiftDO implements Serializable {

    private static final long serialVersionUID = 953152013470095L;

    /**赠品id*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(hidden=true)
    private Long giftId;

    /**赠品名称*/
    @ApiModelProperty(name="gift_name",value="赠品名称",required=false)
    private String giftName;

    /**赠品金额*/
    @ApiModelProperty(name="gift_price",value="赠品金额",required=false)
    private Double giftPrice;

    /**赠品图片*/
    @NotEmpty(message = "请上传赠品图片")
    @ApiModelProperty(name="gift_img",value="赠品图片",required=false)
    private String giftImg;

    /**库存*/
    @NotNull(message = "请填写库存")
    @ApiModelProperty(name="actual_store",value="库存",required=false)
    private Integer actualStore;

    /**赠品类型*/
    @ApiModelProperty(name="gift_type",value="赠品类型",required=false)
    private Integer giftType;

    /**可用库存*/
    @ApiModelProperty(name="enable_store",value="可用库存",required=false)
    private Integer enableStore;

    /**活动时间*/
    @ApiModelProperty(name="create_time",value="活动时间",required=false)
    private Long createTime;

    /**活动商品id*/
    @ApiModelProperty(name="goods_id",value="活动商品id",required=false)
    private Long goodsId;

    /**是否禁用*/
    @ApiModelProperty(name="disabled",value="是否禁用",required=false)
    private Integer disabled;

    /**店铺id*/
    @ApiModelProperty(name="seller_id",value="商家id",required=false)
    private Long sellerId;

    @PrimaryKeyField
    public Long getGiftId() {
        return giftId;
    }
    public void setGiftId(Long giftId) {
        this.giftId = giftId;
    }

    public String getGiftName() {
        return giftName;
    }
    public void setGiftName(String giftName) {
        this.giftName = giftName;
    }

    public Double getGiftPrice() {
        return giftPrice;
    }
    public void setGiftPrice(Double giftPrice) {
        this.giftPrice = giftPrice;
    }

    public String getGiftImg() {
        return giftImg;
    }
    public void setGiftImg(String giftImg) {
        this.giftImg = giftImg;
    }

    public Integer getGiftType() {
        return giftType;
    }
    public void setGiftType(Integer giftType) {
        this.giftType = giftType;
    }

    public Integer getActualStore() {
        return actualStore;
    }
    public void setActualStore(Integer actualStore) {
        this.actualStore = actualStore;
    }

    public Integer getEnableStore() {
        return enableStore;
    }
    public void setEnableStore(Integer enableStore) {
        this.enableStore = enableStore;
    }

    public Long getCreateTime() {
        return createTime;
    }
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getGoodsId() {
        return goodsId;
    }
    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getDisabled() {
        return disabled;
    }
    public void setDisabled(Integer disabled) {
        this.disabled = disabled;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    @Override
    public String toString() {
        return "FullDiscountGiftDO{" +
                "giftId=" + giftId +
                ", giftName='" + giftName + '\'' +
                ", giftPrice=" + giftPrice +
                ", giftImg='" + giftImg + '\'' +
                ", actualStore=" + actualStore +
                ", giftType=" + giftType +
                ", enableStore=" + enableStore +
                ", createTime=" + createTime +
                ", goodsId=" + goodsId +
                ", disabled=" + disabled +
                ", sellerId=" + sellerId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }

        if (o == null || getClass() != o.getClass()){
            return false;
        }

        FullDiscountGiftDO giftDO = (FullDiscountGiftDO) o;

        return new EqualsBuilder()
                .append(giftId, giftDO.giftId)
                .append(giftName, giftDO.giftName)
                .append(giftPrice, giftDO.giftPrice)
                .append(giftImg, giftDO.giftImg)
                .append(actualStore, giftDO.actualStore)
                .append(giftType, giftDO.giftType)
                .append(enableStore, giftDO.enableStore)
                .append(createTime, giftDO.createTime)
                .append(goodsId, giftDO.goodsId)
                .append(disabled, giftDO.disabled)
                .append(sellerId, giftDO.sellerId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(giftId)
                .append(giftName)
                .append(giftPrice)
                .append(giftImg)
                .append(actualStore)
                .append(giftType)
                .append(enableStore)
                .append(createTime)
                .append(goodsId)
                .append(disabled)
                .append(sellerId)
                .toHashCode();
    }
}
