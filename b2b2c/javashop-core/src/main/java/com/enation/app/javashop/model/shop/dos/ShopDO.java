package com.enation.app.javashop.model.shop.dos;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enation.app.javashop.framework.database.annotation.PrimaryKeyField;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
 * 店铺实体
 * @author zjp
 * @version v7.0.0
 * @since v7.0.0
 * 2018-03-20 10:33:40
 */
@TableName(value = "es_shop")
@ApiModel
public class  ShopDO implements Serializable {

    private static final long serialVersionUID = 8432303547724758L;

    /**店铺Id*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(hidden=true)
    private Long shopId;
    /**会员Id*/
    @ApiModelProperty(name="member_id",value="会员Id",required=false)
    private Long memberId;
    /**会员名称*/
    @ApiModelProperty(name="member_name",value="会员名称",required=false)
    private String memberName;
    /**店铺名称*/
    @ApiModelProperty(name="shop_name",value="店铺名称",required=false)
    private String shopName;
    /**店铺状态*/
    @ApiModelProperty(name="shop_disable",value="店铺状态",required=false)
    private String shopDisable;
    /**店铺创建时间*/
    @ApiModelProperty(name="shop_createtime",value="店铺创建时间",required=false)
    private Long shopCreatetime;
    /**店铺关闭时间*/
    @ApiModelProperty(name="shop_endtime",value="店铺关闭时间",required=false)
    private Long shopEndtime;

    @PrimaryKeyField
    public Long getShopId() {
        return shopId;
    }
    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public Long getMemberId() {
        return memberId;
    }
    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }
    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getShopName() {
        return shopName;
    }
    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopDisable() {
        return shopDisable;
    }
    public void setShopDisable(String shopDisable) {
        this.shopDisable = shopDisable;
    }

    public Long getShopCreatetime() {
        return shopCreatetime;
    }
    public void setShopCreatetime(Long shopCreatetime) {
        this.shopCreatetime = shopCreatetime;
    }

    public Long getShopEndtime() {
        return shopEndtime;
    }
    public void setShopEndtime(Long shopEndtime) {
        this.shopEndtime = shopEndtime;
    }
	@Override
	public String toString() {
		return "ShopDO [shopId=" + shopId + ", memberId=" + memberId + ", memberName=" + memberName + ", shopName="
				+ shopName + ", shopDisable=" + shopDisable + ", shopCreatetime=" + shopCreatetime + ", shopEndtime="
				+ shopEndtime + "]";
	}



}
