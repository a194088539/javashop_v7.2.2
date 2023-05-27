package com.enation.app.javashop.model.shop.dos;

import java.io.Serializable;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enation.app.javashop.framework.database.annotation.PrimaryKeyField;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


/**
 * 店铺分组实体
 * @author zjp
 * @version v7.0.0
 * @since v7.0.0
 * 2018-04-24 11:18:37
 */
@TableName(value = "es_shop_cat")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ShopCatDO implements Serializable {

    private static final long serialVersionUID = 9888143360348481L;

    /**店铺分组id*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(name = "shop_cat_id",value = "店铺分组id")
    private Long shopCatId;
    /**店铺分组父ID*/
    @ApiModelProperty(name="shop_cat_pid",value="店铺分组父ID",required=false)
    private Long shopCatPid;
    /**店铺id*/
    @ApiModelProperty(name="shop_id",value="店铺id",required=false)
    private Long shopId;
    /**店铺分组名称*/
    @ApiModelProperty(name="shop_cat_name",value="店铺分组名称",required=true)
    @NotEmpty(message = "店铺分组名称必填")
    private String shopCatName;
    /**店铺分组显示状态:1显示 0不显示*/
    @ApiModelProperty(name="disable",value="店铺分组显示状态:1显示 0不显示",required=true)
    @NotNull(message = "店铺分组显示状态必填")
    private Integer disable;
    /**排序*/
    @ApiModelProperty(name="sort",value="排序",required=true)
    @NotNull(message = "排序必填")
    private Integer sort;
    /**分组路径*/
    @ApiModelProperty(name="cat_path",value="分组路径",required=false)
    private String catPath;

    /**子分组*/
    @TableField(exist = false)
    @ApiModelProperty(name="children",value="分组路径",required=false)
    private List<ShopCatDO> children;

    @PrimaryKeyField
    public Long getShopCatId() {
        return shopCatId;
    }
    public void setShopCatId(Long shopCatId) {
        this.shopCatId = shopCatId;
    }

    public Long getShopCatPid() {
        return shopCatPid;
    }
    public void setShopCatPid(Long shopCatPid) {
        this.shopCatPid = shopCatPid;
    }

    public Long getShopId() {
        return shopId;
    }
    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getShopCatName() {
        return shopCatName;
    }
    public void setShopCatName(String shopCatName) {
        this.shopCatName = shopCatName;
    }

    public Integer getDisable() {
        return disable;
    }
    public void setDisable(Integer disable) {
        this.disable = disable;
    }

    public Integer getSort() {
        return sort;
    }
    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getCatPath() {
        return catPath;
    }
    public void setCatPath(String catPath) {
        this.catPath = catPath;
    }

    public List<ShopCatDO> getChildren() {
        return children;
    }

    public void setChildren(List<ShopCatDO> children) {
        this.children = children;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ShopCatDO shopCatDO = (ShopCatDO) o;

        if (!shopCatId.equals(shopCatDO.shopCatId)) {
            return false;
        }
        if (!shopCatPid.equals(shopCatDO.shopCatPid)) {
            return false;
        }
        return shopId.equals(shopCatDO.shopId);
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = 31 * result + (shopCatId != null ? shopCatId.hashCode() : 0);
        result = 31 * result + (shopCatPid != null ? shopCatPid.hashCode() : 0);
        result = 31 * result + (shopId != null ? shopId.hashCode() : 0);
        result = 31 * result + (shopCatName != null ? shopCatName.hashCode() : 0);
        result = 31 * result + (disable != null ? disable.hashCode() : 0);
        result = 31 * result + (sort != null ? sort.hashCode() : 0);
        result = 31 * result + (catPath != null ? catPath.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ShopCatDO{" +
                "shopCatId=" + shopCatId +
                ", shopCatPid=" + shopCatPid +
                ", shopId=" + shopId +
                ", shopCatName='" + shopCatName + '\'' +
                ", disable=" + disable +
                ", sort=" + sort +
                ", catPath='" + catPath + '\'' +
                '}';
    }


}
