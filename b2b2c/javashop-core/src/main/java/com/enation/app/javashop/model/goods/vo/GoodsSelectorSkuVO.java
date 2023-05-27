package com.enation.app.javashop.model.goods.vo;

import com.enation.app.javashop.model.goods.SkuNameUtil;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 商品选择器，sku单位
 *
 * @author fk
 * @version v2.0
 * @since v7.2.0
 * 2020年2月6日
 */
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class GoodsSelectorSkuVO {

    /**
     * 主键
     */
    @ApiModelProperty(hidden = true)
    private Long skuId;
    /**
     * 商品id
     */
    @ApiModelProperty(name = "goods_id", value = "商品id", hidden = true)
    private Long goodsId;
    /**
     * 商品名称
     */
    @ApiModelProperty(name = "goods_name", value = "商品名称", hidden = true)
    private String goodsName;
    /**
     * 商品编号
     */
    @ApiModelProperty(name = "sn", value = "商品编号", required = false)
    private String sn;
    /**
     * 库存
     */
    @ApiModelProperty(name = "quantity", value = "库存", required = false)
    private Integer quantity;
    /**
     * 可用库存
     */
    @ApiModelProperty(name = "enable_quantity", value = "可用库存")
    private Integer enableQuantity;
    /**
     * 商品价格
     */
    @ApiModelProperty(name = "price", value = "商品价格", required = false)
    private Double price;
    /**
     * 规格信息json
     */
    @ApiModelProperty(name = "specs", value = "规格信息json", hidden = true)
    private String specs;
    /**
     * 成本价格
     */
    @ApiModelProperty(name = "cost", value = "成本价格", required = true)
    private Double cost;
    /**
     * 重量
     */
    @ApiModelProperty(name = "weight", value = "重量", required = true)
    private Double weight;
    /**
     * 卖家id
     */
    @ApiModelProperty(name = "seller_id", value = "卖家id")
    private Long sellerId;
    /**
     * 卖家名称
     */
    @ApiModelProperty(name = "seller_name", value = "卖家名称")
    private String sellerName;
    /**
     * 分类id
     */
    @ApiModelProperty(name = "category_id", value = "分类id")
    private Long categoryId;
    /**
     * 缩略图
     */
    @ApiModelProperty(name = "thumbnail", value = "缩略图", hidden = true)
    private String thumbnail;

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
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

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getEnableQuantity() {
        return enableQuantity;
    }

    public void setEnableQuantity(Integer enableQuantity) {
        this.enableQuantity = enableQuantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getSpecs() {
        return specs;
    }

    public void setSpecs(String specs) {
        this.specs = specs;
    }
}
