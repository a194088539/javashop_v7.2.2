package com.enation.app.javashop.model.promotion.seckill.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enation.app.javashop.framework.database.annotation.Column;
import com.enation.app.javashop.framework.database.annotation.PrimaryKeyField;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;


/**
 * 限时抢购申请实体
 *
 * @author Snow
 * @version v7.0.0
 * @since v7.0.0
 * 2018-04-02 17:30:09
 */
@TableName("es_seckill_apply")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SeckillApplyDO implements Serializable {

    private static final long serialVersionUID = 2980175459354215L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(hidden = true)
    private Long applyId;

    /**
     * 活动id
     */
    @Column(name = "seckill_id")
    @ApiModelProperty(name = "seckill_id", value = "活动id", required = true)
    private Long seckillId;

    /**
     * 时刻
     */
    @Column(name = "time_line")
    @ApiModelProperty(name = "time_line", value = "时刻")
    private Integer timeLine;

    /**
     * 活动开始日期
     */
    @Column(name = "start_day")
    @ApiModelProperty(name = "start_day", value = "活动开始日期")
    private Long startDay;

    /**
     * 商品ID
     */
    @Column(name = "goods_id")
    @ApiModelProperty(name = "goods_id", value = "商品ID")
    private Long goodsId;

    /**
     * skuID
     */
    @Column(name = "sku_id")
    @ApiModelProperty(name = "sku_id", value = "skuID")
    private Long skuId;

    /**
     * 规格组合
     */
    @Column(name = "specs")
    @ApiModelProperty(name = "specs", value = "规格组合")
    @JsonRawValue
    private String specs;

    /**
     * 商品名称
     */
    @Column(name = "goods_name")
    @ApiModelProperty(name = "goods_name", value = "商品名称")
    private String goodsName;

    /**
     * 商家ID
     */
    @Column(name = "seller_id")
    @ApiModelProperty(name = "seller_id", value = "商家id")
    private Long sellerId;

    /**
     * 商家名称
     */
    @Column(name = "shop_name")
    @ApiModelProperty(name = "shop_name", value = "商家名称")
    private String shopName;

    /**
     * 价格
     */
    @Column(name = "price")
    @ApiModelProperty(name = "price", value = "价格")
    private Double price;

    /**
     * 售空数量
     */
    @Column(name = "sold_quantity")
    @ApiModelProperty(name = "sold_quantity", value = "售空数量")
    private Integer soldQuantity;

    /**
     * 申请状态
     */
    @Column(name = "status")
    @ApiModelProperty(name = "status", value = "申请状态,APPLY:申请中,PASS:已通过,FAIL:已驳回")
    private String status;

    /**
     * 驳回原因
     */
    @Column(name = "fail_reason")
    @ApiModelProperty(name = "fail_reason", value = "驳回原因")
    private String failReason;

    @Column(name = "sales_num")
    @ApiModelProperty(name = "sales_num", value = "已售数量")
    private Integer salesNum;

    @Column(name = "original_price")
    @ApiModelProperty(name = "original_price", value = "商品原始价格")
    private Double originalPrice;


    @PrimaryKeyField
    public Long getApplyId() {
        return applyId;
    }

    public void setApplyId(Long applyId) {
        this.applyId = applyId;
    }

    public Long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(Long seckillId) {
        this.seckillId = seckillId;
    }

    public Integer getTimeLine() {
        return timeLine;
    }

    public void setTimeLine(Integer timeLine) {
        this.timeLine = timeLine;
    }

    public Long getStartDay() {
        return startDay;
    }

    public void setStartDay(Long startDay) {
        this.startDay = startDay;
    }

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

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getSoldQuantity() {
        return soldQuantity;
    }

    public void setSoldQuantity(Integer soldQuantity) {
        this.soldQuantity = soldQuantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFailReason() {
        return failReason;
    }

    public String getSpecs() {
        return specs;
    }

    public void setSpecs(String specs) {
        this.specs = specs;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public Double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Integer getSalesNum() {
        if (salesNum == null) {
            salesNum = 0;
        }
        return salesNum;
    }

    public void setSalesNum(Integer salesNum) {
        this.salesNum = salesNum;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SeckillApplyDO applyDO = (SeckillApplyDO) o;

        return new EqualsBuilder()
                .append(applyId, applyDO.applyId)
                .append(seckillId, applyDO.seckillId)
                .append(timeLine, applyDO.timeLine)
                .append(startDay, applyDO.startDay)
                .append(goodsId, applyDO.goodsId)
                .append(goodsName, applyDO.goodsName)
                .append(skuId, applyDO.skuId)
                .append(sellerId, applyDO.sellerId)
                .append(shopName, applyDO.shopName)
                .append(price, applyDO.price)
                .append(soldQuantity, applyDO.soldQuantity)
                .append(status, applyDO.status)
                .append(failReason, applyDO.failReason)
                .append(salesNum, applyDO.salesNum)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(applyId)
                .append(seckillId)
                .append(timeLine)
                .append(startDay)
                .append(goodsId)
                .append(goodsName)
                .append(skuId)
                .append(sellerId)
                .append(shopName)
                .append(price)
                .append(soldQuantity)
                .append(status)
                .append(failReason)
                .append(salesNum)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "SeckillApplyDO{" +
                "applyId=" + applyId +
                ", seckillId=" + seckillId +
                ", timeLine=" + timeLine +
                ", startDay=" + startDay +
                ", goodsId=" + goodsId +
                ", goodsName='" + goodsName + '\'' +
                ", skuId=" + skuId +
                ", sellerId=" + sellerId +
                ", shopName='" + shopName + '\'' +
                ", price=" + price +
                ", soldQuantity=" + soldQuantity +
                ", status='" + status + '\'' +
                ", failReason='" + failReason + '\'' +
                ", salesNum=" + salesNum +
                '}';
    }
}
