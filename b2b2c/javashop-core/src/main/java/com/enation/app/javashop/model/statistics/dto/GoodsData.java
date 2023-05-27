package com.enation.app.javashop.model.statistics.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enation.app.javashop.model.goods.vo.CacheGoods;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 统计库商品数据
 *
 * @author chopper
 * @version v1.0
 * @since v7.0
 * 2018/3/22 下午11:49
 */
@TableName("es_sss_goods_data")
public class GoodsData implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -6108469000506420173L;

    @ApiModelProperty(value = "主键id")
    @TableId(type= IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty(value = "商品id")
    private Long goodsId;

    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @ApiModelProperty(value = "品牌id")
    private Long brandId;

    @ApiModelProperty(value = "分类id")
    private Long categoryId;

    @ApiModelProperty("分类路径")
    private String categoryPath;

    @ApiModelProperty(value = "商品价格")
    private Double price;

    @ApiModelProperty(value = "商家id")
    private Long sellerId;

    @ApiModelProperty("收藏数量")
    private Integer favoriteNum;

    @ApiModelProperty("是否上架 1 上架 0下架")
    private Integer marketEnable;


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryPath() {
        return categoryPath;
    }

    public void setCategoryPath(String categoryPath) {
        this.categoryPath = categoryPath;
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

    public Integer getFavoriteNum() {
        return favoriteNum;
    }

    public void setFavoriteNum(Integer favoriteNum) {
        this.favoriteNum = favoriteNum;
    }

    public Integer getMarketEnable() {
        return marketEnable;
    }

    public void setMarketEnable(Integer marketEnable) {
        this.marketEnable = marketEnable;
    }

    public GoodsData() {

    }
//    public GoodsData(Map<String,Object> goods) {
//        this.setGoodsId((Integer)goods.get("goods_id"));
//        this.setName((String)goods.get("goods_name"));
//        this.setBrandId((Integer)goods.get("brand_id"));
//        this.setCategoryId((Integer)goods.get("category_id"));
//        this.setCategoryPath("");
//        this.setFavoriteNum(0);
//        this.setPrice((Double)goods.get("price"));
//        this.setSellerId((Integer)goods.get("seller_id"));
//        this.setMarketEnable((Integer)goods.get("market_enable"));
//    }

    public GoodsData(CacheGoods goods) {
        this.setGoodsId(goods.getGoodsId());
        this.setGoodsName(goods.getGoodsName());
        this.setCategoryId(goods.getCategoryId());
        this.setCategoryPath("");
        this.setFavoriteNum(0);
        this.setPrice(goods.getPrice());
        this.setSellerId(goods.getSellerId());
        this.setMarketEnable(goods.getMarketEnable());
    }

    public GoodsData(CacheGoods goods,GoodsData gd) {
        this.setGoodsId(goods.getGoodsId());
        this.setGoodsName(goods.getGoodsName());
        this.setCategoryId(goods.getCategoryId());
        this.setPrice(goods.getPrice());
        this.setSellerId(goods.getSellerId());
        this.setMarketEnable(goods.getMarketEnable());

        this.setCategoryPath(gd.getCategoryPath());
        this.setFavoriteNum(gd.getFavoriteNum());
        this.setId(gd.getId());
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GoodsData goodsData = (GoodsData) o;

        if (id != null ? !id.equals(goodsData.id) : goodsData.id != null) {
            return false;
        }
        if (goodsId != null ? !goodsId.equals(goodsData.goodsId) : goodsData.goodsId != null) {
            return false;
        }
        if (goodsName != null ? !goodsName.equals(goodsData.goodsName) : goodsData.goodsName != null) {
            return false;
        }
        if (brandId != null ? !brandId.equals(goodsData.brandId) : goodsData.brandId != null) {
            return false;
        }
        if (categoryId != null ? !categoryId.equals(goodsData.categoryId) : goodsData.categoryId != null) {
            return false;
        }
        if (categoryPath != null ? !categoryPath.equals(goodsData.categoryPath) : goodsData.categoryPath != null) {
            return false;
        }
        if (price != null ? !price.equals(goodsData.price) : goodsData.price != null) {
            return false;
        }
        if (sellerId != null ? !sellerId.equals(goodsData.sellerId) : goodsData.sellerId != null) {
            return false;
        }
        if (favoriteNum != null ? !favoriteNum.equals(goodsData.favoriteNum) : goodsData.favoriteNum != null) {
            return false;
        }
        return marketEnable != null ? marketEnable.equals(goodsData.marketEnable) : goodsData.marketEnable == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (goodsId != null ? goodsId.hashCode() : 0);
        result = 31 * result + (goodsName != null ? goodsName.hashCode() : 0);
        result = 31 * result + (brandId != null ? brandId.hashCode() : 0);
        result = 31 * result + (categoryId != null ? categoryId.hashCode() : 0);
        result = 31 * result + (categoryPath != null ? categoryPath.hashCode() : 0);
        result = 31 * result + (price != null ? price.hashCode() : 0);
        result = 31 * result + (sellerId != null ? sellerId.hashCode() : 0);
        result = 31 * result + (favoriteNum != null ? favoriteNum.hashCode() : 0);
        result = 31 * result + (marketEnable != null ? marketEnable.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "GoodsData{"+
                "goodsId=" + goodsId +
                ", goodsName='" + goodsName + '\'' +
                ", brandId=" + brandId +
                ", categoryId=" + categoryId +
                ", categoryPath='" + categoryPath + '\'' +
                ", price=" + price +
                ", sellerId=" + sellerId +
                ", favoriteNum=" + favoriteNum +
                ", marketEnable=" + marketEnable +
                '}';
    }
}
