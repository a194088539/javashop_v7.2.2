package com.enation.app.javashop.model.trade.deposite;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.model.trade.order.enums.PayStatusEnum;
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

import javax.validation.constraints.NotEmpty;


/**
 * 充值记录实体
 *
 * @author liuyulei
 * @version v1.0
 * @since v7.1.5
 * 2019-12-30 16:38:45
 */
@TableName("es_deposite_recharge")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RechargeDO implements Serializable {

    private static final long serialVersionUID = 381986132572775L;

    /***/
    @TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(hidden = true)
    private Long id;
    /**
     * 充值订单编号
     */
    @ApiModelProperty(name = "recharge_sn", value = "充值订单编号", required = false)
    private String rechargeSn;
    /**
     * 会员id
     */
    @ApiModelProperty(name = "member_id", value = "会员id", required = false)
    private Long memberId;
    /**
     * 会员名称
     */
    @ApiModelProperty(name = "member_name", value = "会员名称", required = false)
    private String memberName;
    /**
     * 充值金额
     */
    @NotEmpty(message = "充值金额不能为空")
    @ApiModelProperty(name = "recharge_money", value = "充值金额", required = true)
    private Double rechargeMoney;
    /**
     * 充值时间戳
     */
    @ApiModelProperty(name = "recharge_time", value = "充值时间戳", required = false)
    private Long rechargeTime;
    /**
     * 充值方式，如：支付宝，微信
     */
    @NotEmpty(message = "充值方式，如：支付宝，微信不能为空")
    @ApiModelProperty(name = "recharge_way", value = "充值方式，如：支付宝，微信", required = true)
    private String rechargeWay;
    /**
     * 支付状态
     */
    @NotEmpty(message = "支付状态不能为空")
    @ApiModelProperty(name = "pay_status", value = "支付状态", required = true)
    private String payStatus;

    /**
     * 支付插件id
     */
    @ApiModelProperty(name = "payment_plugin_id", value = "支付插件id", required = true)
    private String paymentPluginId;

    public RechargeDO() {
    }

    public RechargeDO(String rechargeSn, Long memberId, String memberName, @NotEmpty(message = "充值金额不能为空") Double rechargeMoney) {
        this.rechargeSn = rechargeSn;
        this.memberId = memberId;
        this.memberName = memberName;
        this.rechargeMoney = rechargeMoney;
        this.payStatus = PayStatusEnum.PAY_NO.name();
        this.rechargeTime = DateUtil.getDateline();

    }

    @PrimaryKeyField
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRechargeSn() {
        return rechargeSn;
    }

    public void setRechargeSn(String rechargeSn) {
        this.rechargeSn = rechargeSn;
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

    public Double getRechargeMoney() {
        return rechargeMoney;
    }

    public void setRechargeMoney(Double rechargeMoney) {
        this.rechargeMoney = rechargeMoney;
    }

    public Long getRechargeTime() {
        return rechargeTime;
    }

    public void setRechargeTime(Long rechargeTime) {
        this.rechargeTime = rechargeTime;
    }


    public String getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }

    public String getRechargeWay() {
        return rechargeWay;
    }

    public void setRechargeWay(String rechargeWay) {
        this.rechargeWay = rechargeWay;
    }

    public String getPaymentPluginId() {
        return paymentPluginId;
    }

    public void setPaymentPluginId(String paymentPluginId) {
        this.paymentPluginId = paymentPluginId;
    }

    @Override
    public String toString() {
        return "RechargeDO{" +
                "id=" + id +
                ", rechargeSn='" + rechargeSn + '\'' +
                ", memberId=" + memberId +
                ", memberName='" + memberName + '\'' +
                ", rechargeMoney=" + rechargeMoney +
                ", rechargeTime=" + rechargeTime +
                ", rechargeWay='" + rechargeWay + '\'' +
                ", payStatus='" + payStatus + '\'' +
                ", paymentPluginId='" + paymentPluginId + '\'' +
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
        RechargeDO that = (RechargeDO) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(rechargeSn, that.rechargeSn)
                .append(memberId, that.memberId)
                .append(memberName, that.memberName)
                .append(rechargeMoney, that.rechargeMoney)
                .append(rechargeTime, that.rechargeTime)
                .append(rechargeWay, that.rechargeWay)
                .append(payStatus, that.payStatus)
                .append(paymentPluginId, that.paymentPluginId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(rechargeSn)
                .append(memberId)
                .append(memberName)
                .append(rechargeMoney)
                .append(rechargeTime)
                .append(rechargeWay)
                .append(payStatus)
                .append(paymentPluginId)
                .toHashCode();
    }


}
