package com.enation.app.javashop.model.member.dos;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enation.app.javashop.framework.database.annotation.PrimaryKeyField;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Min;


/**
 * 会员商品收藏表实体
 *
 * @author zh
 * @version v7.0.0
 * @since v7.0.0
 * 2018-04-02 10:13:41
 */
@TableName(value = "es_member_collection_goods")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MemberCollectionGoods implements Serializable {

    private static final long serialVersionUID = 7279348053700611L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(hidden = true)
    private Long id;
    /**
     * 会员ID
     */
    @Min(message = "必须为数字", value = 0)
    @ApiModelProperty(name = "member_id", value = "会员ID", required = false)
    private Long memberId;

    /**
     * 收藏商品ID
     */
    @Min(message = "必须为数字", value = 0)
    @ApiModelProperty(name = "goods_id", value = "收藏商品ID", required = true)
    private Long goodsId;
    /**
     * 收藏商品时间
     */
    @ApiModelProperty(name = "create_time", value = "收藏商品时间", required = false)
    private Long createTime;
    /**
     * 商品名称
     */
    @ApiModelProperty(name = "goods_name", value = "商品名称", required = false)
    private String goodsName;
    /**
     * 商品价格
     */
    @ApiModelProperty(name = "goods_price", value = "商品价格", required = false)
    private Double goodsPrice;
    /**
     * 商品编号
     */
    @ApiModelProperty(name = "goods_sn", value = "商品编号", required = false)
    private String goodsSn;

    /**
     * 商品图片
     */
    @ApiModelProperty(name = "goods_img", value = "商品图片", required = false)
    private String goodsImg;
    /**
     * 店铺id
     */
    @ApiModelProperty(name = "shop_id", value = "店铺id", required = false)
    private Long shopId;

    @PrimaryKeyField
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Double getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(Double goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public String getGoodsSn() {
        return goodsSn;
    }

    public void setGoodsSn(String goodsSn) {
        this.goodsSn = goodsSn;
    }

    public String getGoodsImg() {
        return goodsImg;
    }

    public void setGoodsImg(String goodsImg) {
        this.goodsImg = goodsImg;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    @Override
    public String toString() {
        return "MemberCollectionGoods{" +
                "id=" + id +
                ", memberId=" + memberId +
                ", goodsId=" + goodsId +
                ", createTime=" + createTime +
                ", goodsName='" + goodsName + '\'' +
                ", goodsPrice=" + goodsPrice +
                ", goodsSn='" + goodsSn + '\'' +
                ", goodsImg='" + goodsImg + '\'' +
                ", shopId=" + shopId +
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
        MemberCollectionGoods that = (MemberCollectionGoods) o;
        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (memberId != null ? !memberId.equals(that.memberId) : that.memberId != null) {
            return false;
        }
        if (goodsId != null ? !goodsId.equals(that.goodsId) : that.goodsId != null) {
            return false;
        }
        if (createTime != null ? !createTime.equals(that.createTime) : that.createTime != null) {
            return false;
        }
        if (goodsName != null ? !goodsName.equals(that.goodsName) : that.goodsName != null) {
            return false;
        }
        if (goodsPrice != null ? !goodsPrice.equals(that.goodsPrice) : that.goodsPrice != null) {
            return false;
        }
        if (goodsSn != null ? !goodsSn.equals(that.goodsSn) : that.goodsSn != null) {
            return false;
        }
        if (goodsImg != null ? !goodsImg.equals(that.goodsImg) : that.goodsImg != null) {
            return false;
        }
        return shopId != null ? shopId.equals(that.shopId) : that.shopId == null;
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (memberId != null ? memberId.hashCode() : 0);
        result = 31 * result + (goodsId != null ? goodsId.hashCode() : 0);
        result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
        result = 31 * result + (goodsName != null ? goodsName.hashCode() : 0);
        result = 31 * result + (goodsPrice != null ? goodsPrice.hashCode() : 0);
        result = 31 * result + (goodsSn != null ? goodsSn.hashCode() : 0);
        result = 31 * result + (goodsImg != null ? goodsImg.hashCode() : 0);
        result = 31 * result + (shopId != null ? shopId.hashCode() : 0);
        return result;
    }
}
