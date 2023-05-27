package com.enation.app.javashop.model.promotion.coupon.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enation.app.javashop.framework.database.annotation.Column;
import com.enation.app.javashop.framework.database.annotation.Id;
import com.enation.app.javashop.framework.database.annotation.PrimaryKeyField;
import com.enation.app.javashop.framework.database.annotation.Table;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.expression.spel.ast.Assign;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


/**
 * 优惠券实体
 *
 * @author Snow
 * @version v2.0
 * @since v7.0.0
 * 2018-04-17 23:19:39
 */
@TableName("es_coupon")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CouponDO implements Serializable {

    private static final long serialVersionUID = 8587456467004980L;

    /**
     * 主键
     */
    @TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(hidden = true)
    private Long couponId;

    /**
     * 优惠券名称
     */
    @ApiModelProperty(name = "title", value = "优惠券名称", required = true)
    @NotEmpty(message = "请填写优惠券名称")
    private String title;

    /**
     * 优惠券面额
     */
    @ApiModelProperty(name = "coupon_price", value = "优惠券面额", required = false)
    @NotNull(message = "请填写优惠券面额")
    @Max(value = 99999999, message = "优惠券面额不能超过99999999")
    private Double couponPrice;

    /**
     * 优惠券门槛价格
     */
    @ApiModelProperty(name = "coupon_threshold_price", value = "优惠券门槛价格", required = false)
    @NotNull(message = "请填写优惠券门槛价格")
    @Max(value = 99999999, message = "优惠券门槛价格不能超过99999999")
    private Double couponThresholdPrice;

    /**
     * 使用起始时间
     */
    @ApiModelProperty(name = "start_time", value = "使用起始时间", required = false)
    @NotNull(message = "请填写起始时间")
    private Long startTime;

    /**
     * 使用截止时间
     */
    @ApiModelProperty(name = "end_time", value = "使用截止时间", required = false)
    @NotNull(message = "请填写截止时间")
    private Long endTime;

    /**
     * 发行量
     */
    @ApiModelProperty(name = "create_num", value = "发行量", required = false)
    @NotNull(message = "请正确填写发行量")
    private Integer createNum;

    /**
     * 每人限领数量
     */
    @ApiModelProperty(name = "limit_num", value = "每人限领数量", required = false)
    @NotNull(message = "请正确填写每人限领数量")
    private Integer limitNum;

    /**
     * 已被使用的数量
     */
    @ApiModelProperty(name = "used_num", value = "已被使用的数量", required = false)
    private Integer usedNum;

    /**
     * 已被领取的数量
     */
    @ApiModelProperty(name = "received_num", value = "已被领取的数量", required = false)
    private Integer receivedNum;

    /**
     * 店铺ID
     */
    @ApiModelProperty(name = "seller_id", value = "商家id", required = false)
    private Long sellerId;

    @ApiModelProperty(name = "seller_name", value = "店铺名称")
    private String sellerName;

    /**
     * 优惠券类型，分为免费领取和活动赠送
     */
    @ApiModelProperty(name = "type", value = "优惠券类型，分为免费领取和活动赠送")
    private String type;

    /**
     * 使用范围，全品，分类，部分商品
     */
    @ApiModelProperty(name = "use_scope", value = "使用范围，全品，分类，部分商品")
    private String useScope;


    /**
     * 范围关联的id
     * 全品或者商家优惠券时为0
     * 分类时为分类id
     * 部分商品时为商品ID集合
     */
    @ApiModelProperty(name = "scope_id", value = "范围关联的id")
    private String scopeId;


    /**
     * 店铺承担比例
     */
    @ApiModelProperty(name = "shop_commission", value = "店铺承担比例")
    private Integer shopCommission;

    /**
     * 范围描述
     */
    @ApiModelProperty(name = "scope_description", value = "范围描述")
    private String scopeDescription;

    /**
     * 活动说明
     */
    @ApiModelProperty(name = "activity_description", value = "活动说明")
    private String activityDescription;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @PrimaryKeyField
    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
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

    public Integer getCreateNum() {
        return createNum;
    }

    public void setCreateNum(Integer createNum) {
        this.createNum = createNum;
    }

    public Integer getLimitNum() {
        return limitNum;
    }

    public void setLimitNum(Integer limitNum) {
        this.limitNum = limitNum;
    }

    public Integer getUsedNum() {
        return usedNum;
    }

    public void setUsedNum(Integer usedNum) {
        this.usedNum = usedNum;
    }

    public Integer getReceivedNum() {
        if (receivedNum == null) {
            receivedNum = 0;
        }
        return receivedNum;
    }

    public void setReceivedNum(Integer receivedNum) {
        this.receivedNum = receivedNum;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Integer getShopCommission() {
        return shopCommission;
    }

    public void setShopCommission(Integer shopCommission) {
        this.shopCommission = shopCommission;
    }

    public String getScopeDescription() {
        return scopeDescription;
    }

    public void setScopeDescription(String scopeDescription) {
        this.scopeDescription = scopeDescription;
    }

    public String getActivityDescription() {
        return activityDescription;
    }

    public void setActivityDescription(String activityDescription) {
        this.activityDescription = activityDescription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CouponDO couponDO = (CouponDO) o;

        return new EqualsBuilder()
                .append(couponId, couponDO.couponId)
                .append(title, couponDO.title)
                .append(couponPrice, couponDO.couponPrice)
                .append(couponThresholdPrice, couponDO.couponThresholdPrice)
                .append(startTime, couponDO.startTime)
                .append(endTime, couponDO.endTime)
                .append(createNum, couponDO.createNum)
                .append(limitNum, couponDO.limitNum)
                .append(usedNum, couponDO.usedNum)
                .append(receivedNum, couponDO.receivedNum)
                .append(sellerId, couponDO.sellerId)
                .append(sellerName, couponDO.sellerName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(couponId)
                .append(title)
                .append(couponPrice)
                .append(couponThresholdPrice)
                .append(startTime)
                .append(endTime)
                .append(createNum)
                .append(limitNum)
                .append(usedNum)
                .append(receivedNum)
                .append(sellerId)
                .append(sellerName)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "CouponDO{" +
                "couponId=" + couponId +
                ", title='" + title + '\'' +
                ", couponPrice=" + couponPrice +
                ", couponThresholdPrice=" + couponThresholdPrice +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", createNum=" + createNum +
                ", limitNum=" + limitNum +
                ", usedNum=" + usedNum +
                ", receivedNum=" + receivedNum +
                ", sellerId=" + sellerId +
                ", sellerName=" + sellerName +
                '}';
    }
}
