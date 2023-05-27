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


/**
 * 店铺评分实体
 * @author fk
 * @version v1.0
 * @since v7.0.0
 * 2018-05-03 10:38:00
 */
@TableName(value = "es_member_shop_score")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MemberShopScore implements Serializable {

    private static final long serialVersionUID = 9224429719464458L;

    /**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(hidden=true)
    private Long scoreId;
    /**会员id*/
    @ApiModelProperty(name="member_id",value="会员id",required=false)
    private Long memberId;
    /**订单编号*/
    @ApiModelProperty(name="order_sn",value="订单编号",required=false)
    private String orderSn;
    /**发货速度评分*/
    @ApiModelProperty(name="delivery_score",value="发货速度评分",required=false)
    private Integer deliveryScore;
    /**描述相符度评分*/
    @ApiModelProperty(name="description_score",value="描述相符度评分",required=false)
    private Integer descriptionScore;
    /**服务评分*/
    @ApiModelProperty(name="service_score",value="服务评分",required=false)
    private Integer serviceScore;
    /**卖家*/
    @ApiModelProperty(name="seller_id",value="卖家",required=false)
    private Long sellerId;

    @PrimaryKeyField
    public Long getScoreId() {
        return scoreId;
    }
    public void setScoreId(Long scoreId) {
        this.scoreId = scoreId;
    }

    public Long getMemberId() {
        return memberId;
    }
    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getOrderSn() {
        return orderSn;
    }
    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public Integer getDeliveryScore() {
        return deliveryScore;
    }
    public void setDeliveryScore(Integer deliveryScore) {
        this.deliveryScore = deliveryScore;
    }

    public Integer getDescriptionScore() {
        return descriptionScore;
    }
    public void setDescriptionScore(Integer descriptionScore) {
        this.descriptionScore = descriptionScore;
    }

    public Integer getServiceScore() {
        return serviceScore;
    }
    public void setServiceScore(Integer serviceScore) {
        this.serviceScore = serviceScore;
    }

    public Long getSellerId() {
        return sellerId;
    }
    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }


	@Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        MemberShopScore that = (MemberShopScore) o;
        if (scoreId != null ? !scoreId.equals(that.scoreId) : that.scoreId != null) {return false;}
        if (memberId != null ? !memberId.equals(that.memberId) : that.memberId != null) {return false;}
        if (orderSn != null ? !orderSn.equals(that.orderSn) : that.orderSn != null) {return false;}
        if (deliveryScore != null ? !deliveryScore.equals(that.deliveryScore) : that.deliveryScore != null) {return false;}
        if (descriptionScore != null ? !descriptionScore.equals(that.descriptionScore) : that.descriptionScore != null) {return false;}
        if (serviceScore != null ? !serviceScore.equals(that.serviceScore) : that.serviceScore != null) {return false;}
        return sellerId != null ? sellerId.equals(that.sellerId) : that.sellerId == null;
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = 31 * result + (scoreId != null ? scoreId.hashCode() : 0);
        result = 31 * result + (memberId != null ? memberId.hashCode() : 0);
        result = 31 * result + (orderSn != null ? orderSn.hashCode() : 0);
        result = 31 * result + (deliveryScore != null ? deliveryScore.hashCode() : 0);
        result = 31 * result + (descriptionScore != null ? descriptionScore.hashCode() : 0);
        result = 31 * result + (serviceScore != null ? serviceScore.hashCode() : 0);
        result = 31 * result + (sellerId != null ? sellerId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MemberShopScore{" +
                "scoreId=" + scoreId +
                ", memberId=" + memberId +
                ", orderSn='" + orderSn + '\'' +
                ", deliveryScore=" + deliveryScore +
                ", descriptionScore=" + descriptionScore +
                ", serviceScore=" + serviceScore +
                ", sellerId=" + sellerId +
                '}';
    }


}
