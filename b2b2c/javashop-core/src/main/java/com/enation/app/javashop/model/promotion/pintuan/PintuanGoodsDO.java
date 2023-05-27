package com.enation.app.javashop.model.promotion.pintuan;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enation.app.javashop.framework.database.annotation.Column;
import com.enation.app.javashop.framework.database.annotation.Id;
import com.enation.app.javashop.framework.database.annotation.PrimaryKeyField;
import com.enation.app.javashop.framework.database.annotation.Table;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.validation.constraints.Min;
import java.io.Serializable;


/**
 * 拼团商品实体
 *
 * @author admin
 * @version vv1.0.0
 * @since vv7.1.0
 * 2019-01-22 11:20:56
 */
@TableName("es_pintuan_goods")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PintuanGoodsDO implements Serializable {

    /**
     * id
     */
    @TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(hidden = true)
    private Long id;
    /**
     * sku_id
     */
    @ApiModelProperty(name = "sku_id", value = "sku_id")
    private Long skuId;

    @ApiModelProperty(name = "goods_id", value = "goods_id")
    private Long goodsId;


    @ApiModelProperty(name = "seller_id", value = "卖家id")
    private Long sellerId;

    /**
     * 卖家名称
     */
    @ApiModelProperty(name = "seller_name", value = "卖家名称", hidden = true)
    private String sellerName;


    /**
     * 商品名称
     */
    @ApiModelProperty(name = "goods_name", value = "商品名称", required = true)
    private String goodsName;
    /**
     * 原价
     */
    @Min(message = "必须为数字", value = 0)
    @ApiModelProperty(name = "origin_price", value = "原价")
    private Double originPrice;
    /**
     * 活动价
     */
    @Min(message = "必须为数字", value = 0)
    @ApiModelProperty(name = "sales_price", value = "活动价")
    private Double salesPrice;
    /**
     * sn
     */
    @ApiModelProperty(name = "sn", value = "sn", required = true)
    private String sn;
    /**
     * 已售数量
     */
    @ApiModelProperty(name = "sold_quantity", value = "已售数量")
    private Integer soldQuantity;
    /**
     * 待发货数量
     */
    @ApiModelProperty(name = "locked_quantity", value = "待发货数量")
    private Integer lockedQuantity;
    /**
     * 拼团活动id
     */
    @ApiModelProperty(name = "pintuan_id", value = "拼团活动id")
    private Long pintuanId;


    @ApiModelProperty(name = "specs", value = "规格信息json")
    @JsonRawValue
    private String specs;

    @ApiModelProperty(name = "thumbnail", value = "商品图片")
    private String thumbnail;

    @PrimaryKeyField
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Double getPrice() {
        return originPrice;
    }

    public Double getOriginPrice() {
        return originPrice;
    }

    public void setOriginPrice(Double originPrice) {
        this.originPrice = originPrice;
    }

    public Double getSalesPrice() {
        return salesPrice;
    }

    public void setSalesPrice(Double salesPrice) {
        this.salesPrice = salesPrice;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public Integer getSoldQuantity() {
        return soldQuantity;
    }

    public void setSoldQuantity(Integer soldQuantity) {
        this.soldQuantity = soldQuantity;
    }

    public Integer getLockedQuantity() {
        return lockedQuantity;
    }

    public void setLockedQuantity(Integer lockedQuantity) {
        this.lockedQuantity = lockedQuantity;
    }

    public Long getPintuanId() {
        return pintuanId;
    }

    public void setPintuanId(Long pintuanId) {
        this.pintuanId = pintuanId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSpecs() {
        return specs;
    }

    public void setSpecs(String specs) {
        this.specs = specs;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PintuanGoodsDO that = (PintuanGoodsDO) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(skuId, that.skuId)
                .append(goodsId, that.goodsId)
                .append(goodsName, that.goodsName)
                .append(originPrice, that.originPrice)
                .append(salesPrice, that.salesPrice)
                .append(sn, that.sn)
                .append(soldQuantity, that.soldQuantity)
                .append(lockedQuantity, that.lockedQuantity)
                .append(pintuanId, that.pintuanId)
                .append(sellerId, that.sellerId)
                .append(sellerName, that.sellerName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(skuId)
                .append(goodsId)
                .append(goodsName)
                .append(originPrice)
                .append(salesPrice)
                .append(sn)
                .append(soldQuantity)
                .append(lockedQuantity)
                .append(pintuanId)
                .append(sellerId)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "PintuanGoodsDO{" +
                "id=" + id +
                ", skuId=" + skuId +
                ", goodsId=" + goodsId +
                ", goodsName='" + goodsName + '\'' +
                ", originPrice=" + originPrice +
                ", salesPrice=" + salesPrice +
                ", sn='" + sn + '\'' +
                ", soldQuantity=" + soldQuantity +
                ", lockedQuantity=" + lockedQuantity +
                ", pintuanId=" + pintuanId +
                ", sellerId=" +sellerId +
                ", sellerName=" +sellerName +
                '}';
    }


}
