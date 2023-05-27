package com.enation.app.javashop.model.statistics.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enation.app.javashop.model.shop.vo.ShopVO;
import com.enation.app.javashop.framework.database.annotation.Column;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 店铺统计数据
 *
 * @author chopper
 * @version v1.0
 * @since v7.0
 * 2018-03-29 下午4:41
 */
@TableName("es_sss_shop_data")
public class ShopData implements Serializable {


    @ApiModelProperty(value = "主键id")
    @TableId(type= IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty(value = "商家id")
    @Column(name = "seller_id")
    private Long sellerId;

    @ApiModelProperty(value = "商家名称")
    @Column(name = "seller_name")
    private String sellerName;

    @ApiModelProperty(value = "收藏数量")
    @Column(name = "favorite_num")
    private Integer favoriteNum;

    @ApiModelProperty(value = "OPEN/CLOSED/APPLY/REFUSED/APPLYING")
    @Column(name = "shop_disable")
    private String shopDisable;

    public ShopData() {
    }

    public ShopData(ShopVO shopVO) {
        this.setSellerId(shopVO.getShopId());
        this.setSellerName(shopVO.getShopName());
        this.setShopDisable(shopVO.getShopDisable());
        this.setFavoriteNum(0);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getFavoriteNum() {
        return favoriteNum;
    }

    public void setFavoriteNum(Integer favoriteNum) {
        this.favoriteNum = favoriteNum;
    }

    public String getShopDisable() {
        return shopDisable;
    }

    public void setShopDisable(String shopDisable) {
        this.shopDisable = shopDisable;
    }

    @Override
    public String toString() {
        return "ShopData{" +
                "sellerId=" + sellerId +
                ", sellerName='" + sellerName + '\'' +
                ", favoriteNum=" + favoriteNum +
                ", shopDisable='" + shopDisable + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ShopData shopData = (ShopData) o;

        if (sellerId != null ? !sellerId.equals(shopData.sellerId) : shopData.sellerId != null) {
            return false;
        }
        if (sellerName != null ? !sellerName.equals(shopData.sellerName) : shopData.sellerName != null) {
            return false;
        }
        if (favoriteNum != null ? !favoriteNum.equals(shopData.favoriteNum) : shopData.favoriteNum != null) {
            return false;
        }
        return shopDisable != null ? shopDisable.equals(shopData.shopDisable) : shopData.shopDisable == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (sellerId != null ? sellerId.hashCode() : 0);
        result = 31 * result + (sellerName != null ? sellerName.hashCode() : 0);
        result = 31 * result + (favoriteNum != null ? favoriteNum.hashCode() : 0);
        result = 31 * result + (shopDisable != null ? shopDisable.hashCode() : 0);
        return result;
    }
}
