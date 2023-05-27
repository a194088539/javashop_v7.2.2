package com.enation.app.javashop.model.member.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enation.app.javashop.model.promotion.coupon.dos.CouponDO;
import com.enation.app.javashop.framework.database.annotation.PrimaryKeyField;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;


/**
 * 会员优惠券实体
 *
 * @author Snow
 * @version vv7.0.0
 * @since v7.0.0
 * 2018-06-12 21:48:46
 */
@TableName(value = "es_member_coupon")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MemberCoupon implements Serializable {

    private static final long serialVersionUID = 5545788652245350L;

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(hidden = true)
    private Long mcId;

    /**
     * 优惠券表主键
     */
    @ApiModelProperty(name = "coupon_id", value = "优惠券表主键", required = false)
    private Long couponId;

    /**
     * 会员表主键
     */
    @ApiModelProperty(name = "member_id", value = "会员表主键", required = false)
    private Long memberId;

    /**
     * 使用时间
     */
    @ApiModelProperty(name = "used_time", value = "使用时间", required = false)
    private Long usedTime;

    /**
     * 领取时间
     */
    @ApiModelProperty(name = "create_time", value = "领取时间", required = false)
    private Long createTime;

    /**
     * 订单表主键
     */
    @ApiModelProperty(name = "order_id", value = "订单表主键", required = false)
    private Long orderId;

    /**
     * 订单编号
     */
    @ApiModelProperty(name = "order_sn", value = "订单编号", required = false)
    private String orderSn;

    /**
     * 会员用户名
     */
    @ApiModelProperty(name = "member_name", value = "会员用户名", required = false)
    private String memberName;

    /**
     * 优惠券名称
     */
    @ApiModelProperty(name = "title", value = "优惠券名称", required = false)
    private String title;

    /**
     * 优惠券面额
     */
    @ApiModelProperty(name = "coupon_price", value = "优惠券面额", required = false)
    private Double couponPrice;

    /**
     * 优惠券门槛金额
     */
    @ApiModelProperty(name = "coupon_threshold_price", value = "优惠券门槛金额", required = false)
    private Double couponThresholdPrice;

    /**
     * 有效期--起始时间
     */
    @ApiModelProperty(name = "start_time", value = "有效期--起始时间", required = false)
    private Long startTime;

    /**
     * 有效期--截止时间
     */
    @ApiModelProperty(name = "end_time", value = "有效期--截止时间", required = false)
    private Long endTime;

    /**
     * 使用状态 0:未使用,1:已使用,2:已过期,3:已作废
     */
    @ApiModelProperty(name = "used_status", value = "使用状态", example = "0:未使用,1:已使用,2:已过期,3:已作废")
    private Integer usedStatus;

    /**
     * 商家ID
     */
    @ApiModelProperty(name = "seller_id", value = "商家ID")
    private Long sellerId;

    /**
     * 商家名称
     */
    @ApiModelProperty(name = "seller_name", value = "商家名称")
    private String sellerName;

    /**
     * 使用状态文字（非数据库字段）
     */
    @ApiModelProperty(value = "使用状态文字")
    @TableField(exist = false)
    private String usedStatusText;

    /**
     * 	使用范围 ALL:全品,CATEGORY:分类,SOME_GOODS:部分商品
     */
    @ApiModelProperty(name = "use_scope", value = "使用范围 ALL:全品,CATEGORY:分类,SOME_GOODS:部分商品")
    private String useScope;

    /**
     * 范围关联的id
     * 全品或者商家优惠券时为0
     * 分类时为分类id
     * 部分商品时为商品ID集合
     */
    @ApiModelProperty(name = "scope_id", value = "范围关联的id")
    private String scopeId;


    public MemberCoupon() {
    }

    public MemberCoupon(CouponDO couponDO) {
        this.setCouponId(couponDO.getCouponId());
        this.setTitle(couponDO.getTitle());
        this.setStartTime(couponDO.getStartTime());
        this.setEndTime(couponDO.getEndTime());
        this.setCouponPrice(couponDO.getCouponPrice());
        this.setCouponThresholdPrice(couponDO.getCouponThresholdPrice());
        this.setSellerId(couponDO.getSellerId());
        this.setSellerName(couponDO.getSellerName());
        this.setUseScope(couponDO.getUseScope());
        this.setScopeId(couponDO.getScopeId());
    }

    @PrimaryKeyField
    public Long getMcId() {
        return mcId;
    }

    public void setMcId(Long mcId) {
        this.mcId = mcId;
    }

    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getUsedTime() {
        return usedTime;
    }

    public void setUsedTime(Long usedTime) {
        this.usedTime = usedTime;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getCouponPrice() {
        return couponPrice;
    }

    public void setCouponPrice(Double couponPrice) {
        this.couponPrice = couponPrice;
    }

    public Double getCouponThresholdPrice() {
        return couponThresholdPrice;
    }

    public void setCouponThresholdPrice(Double couponThresholdPrice) {
        this.couponThresholdPrice = couponThresholdPrice;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Integer getUsedStatus() {
        return usedStatus;
    }

    public void setUsedStatus(Integer usedStatus) {
        this.usedStatus = usedStatus;
    }

    public String getUsedStatusText() {
        if (usedStatus == 0) {
            usedStatusText = "未使用";
        } else if(usedStatus == 2){
            usedStatusText = "已过期";
        }else{
            usedStatusText = "已使用";
        }

        return usedStatusText;
    }

    public void setUsedStatusText(String usedStatusText) {
        this.usedStatusText = usedStatusText;
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


    public String getUseScope() {
        return useScope;
    }

    public void setUseScope(String useScope) {
        this.useScope = useScope;
    }

    public String getScopeId() {
        return scopeId;
    }

    public void setScopeId(String scopeId) {
        this.scopeId = scopeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MemberCoupon that = (MemberCoupon) o;

        return new EqualsBuilder()
                .append(mcId, that.mcId)
                .append(couponId, that.couponId)
                .append(memberId, that.memberId)
                .append(usedTime, that.usedTime)
                .append(createTime, that.createTime)
                .append(orderId, that.orderId)
                .append(orderSn, that.orderSn)
                .append(memberName, that.memberName)
                .append(title, that.title)
                .append(couponPrice, that.couponPrice)
                .append(couponThresholdPrice, that.couponThresholdPrice)
                .append(startTime, that.startTime)
                .append(endTime, that.endTime)
                .append(usedStatus, that.usedStatus)
                .append(sellerId, that.sellerId)
                .append(sellerName, that.sellerName)
                .append(usedStatusText, that.usedStatusText)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(mcId)
                .append(couponId)
                .append(memberId)
                .append(usedTime)
                .append(createTime)
                .append(orderId)
                .append(orderSn)
                .append(memberName)
                .append(title)
                .append(couponPrice)
                .append(couponThresholdPrice)
                .append(startTime)
                .append(endTime)
                .append(usedStatus)
                .append(sellerId)
                .append(sellerName)
                .append(usedStatusText)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "MemberCoupon{" +
                "mcId=" + mcId +
                ", couponId=" + couponId +
                ", memberId=" + memberId +
                ", usedTime=" + usedTime +
                ", createTime=" + createTime +
                ", orderId=" + orderId +
                ", orderSn='" + orderSn + '\'' +
                ", memberName='" + memberName + '\'' +
                ", title='" + title + '\'' +
                ", couponPrice=" + couponPrice +
                ", couponThresholdPrice=" + couponThresholdPrice +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", usedStatus=" + usedStatus +
                ", sellerId=" + sellerId +
                ", sellerName='" + sellerName + '\'' +
                ", usedStatusText='" + usedStatusText + '\'' +
                '}';
    }
}
