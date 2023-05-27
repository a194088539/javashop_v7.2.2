package com.enation.app.javashop.model.promotion.tool.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Objects;

/**
 * 活动商品DTO
 * @author Snow create in 2018/3/30
 * @version v2.0
 * @since v7.0.0
 */
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PromotionGoodsDTO implements Serializable {

    private static final long serialVersionUID = 698984607262185052L;

    @ApiModelProperty(name="goods_id",value="商品id")
    private Long goodsId;

    @ApiModelProperty(name="sku_id",value="skuid")
    private Long skuId;

    @ApiModelProperty(value="规格信息")
    private String specs;

    @ApiModelProperty(name="goods_name",value="商品名称")
    private String goodsName;

    @ApiModelProperty(value="商品缩略图")
    private String thumbnail;

    @ApiModelProperty(value="商品编号")
    private String sn;

    @ApiModelProperty(value="商品价格")
    private Double price ;

    @ApiModelProperty(value="库存")
    private Integer quantity;

    @ApiModelProperty(value="商家id")
    private Long sellerId;

    @ApiModelProperty(value="可用库存")
    private Integer enableQuantity;

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public Integer getEnableQuantity() {
        return enableQuantity;
    }

    public void setEnableQuantity(Integer enableQuantity) {
        this.enableQuantity = enableQuantity;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public String getSpecs() {
        return specs;
    }

    public void setSpecs(String specs) {
        this.specs = specs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PromotionGoodsDTO that = (PromotionGoodsDTO) o;
        return Objects.equals(goodsId, that.goodsId) &&
                Objects.equals(goodsName, that.goodsName) &&
                Objects.equals(thumbnail, that.thumbnail) &&
                Objects.equals(sn, that.sn) &&
                Objects.equals(price, that.price) &&
                Objects.equals(quantity, that.quantity) &&
                Objects.equals(sellerId, that.sellerId) &&
                Objects.equals(enableQuantity, that.enableQuantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(goodsId, goodsName, thumbnail, sn, price, quantity, sellerId, enableQuantity);
    }

    @Override
    public String toString() {
        return "PromotionGoodsDTO{" +
                "goodsId=" + goodsId +
                ", goodsName='" + goodsName + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", sn='" + sn + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", sellerId=" + sellerId +
                ", enableQuantity=" + enableQuantity +
                '}';
    }
}
