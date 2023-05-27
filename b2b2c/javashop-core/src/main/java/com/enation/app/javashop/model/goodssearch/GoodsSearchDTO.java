package com.enation.app.javashop.model.goodssearch;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @author fk
 * @version v2.0
 * @Description: 商品搜索传输对象
 * @date 2018/6/19 16:15
 * @since v7.0.0
 */
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class GoodsSearchDTO implements Serializable {

    @ApiModelProperty(name = "page_no", value = "页码")
    private Long pageNo;
    @ApiModelProperty(name = "page_size", value = "每页数量")
    private Long pageSize;
    @ApiModelProperty(name = "keyword", value = "关键字")
    private String keyword;
    @ApiModelProperty(name = "category", value = "分类")
    private Long category;
    @ApiModelProperty(name = "brand", value = "品牌")
    private Long brand;
    @ApiModelProperty(name = "price", value = "价格",example = "10_30")
    private String price;
    @ApiModelProperty(name = "sort", value = "排序:关键字_排序",allowableValues = "def_asc,def_desc,price_asc,price_desc,buynum_asc,buynum_desc,grade_asc,grade_desc")
    private String sort;
    @ApiModelProperty(name = "prop", value = "属性:参数名_参数值@参数名_参数值",example = "屏幕类型_LED@屏幕尺寸_15英寸")
    private String prop;
    @ApiModelProperty(name = "seller_id", value = "卖家id，搜索店铺商品的时候使用")
    private Long sellerId;
    @ApiModelProperty(name = "shop_cat_id", value = "商家分组id，搜索店铺商品的时候使用")
    private Long shopCatId;


    public Long getPageNo() {
        return pageNo;
    }

    public void setPageNo(Long pageNo) {
        this.pageNo = pageNo;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Long getCategory() {
        return category;
    }

    public void setCategory(Long category) {
        this.category = category;
    }

    public Long getBrand() {
        return brand;
    }

    public void setBrand(Long brand) {
        this.brand = brand;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getProp() {
        return prop;
    }

    public void setProp(String prop) {
        this.prop = prop;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public Long getShopCatId() {
        return shopCatId;
    }

    public void setShopCatId(Long shopCatId) {
        this.shopCatId = shopCatId;
    }

    @Override
    public String toString() {
        return "GoodsSearchDTO{" +
                "pageNo=" + pageNo +
                ", pageSize=" + pageSize +
                ", keyword='" + keyword + '\'' +
                ", category='" + category + '\'' +
                ", brand=" + brand +
                ", price='" + price + '\'' +
                ", sort='" + sort + '\'' +
                ", prop='" + prop + '\'' +
                '}';
    }
}
