package com.enation.app.javashop.model.trade.deposite;

import com.baomidou.mybatisplus.annotation.IdType;
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

import java.io.Serializable;

/**
 * @description: 预存款日志
 * @author: liuyulei
 * @create: 2019-12-30 17:36
 * @version:1.0
 * @since:7.1.5
 **/
@TableName("es_deposite_log")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DepositeLogDO implements Serializable {
    private static final long serialVersionUID = 8252160385375284313L;

    /**
     * 主键
     */
    @TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(hidden = true)
    private Long id;

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
     * 金额
     */
    @ApiModelProperty(name = "money", value = "金额", required = false)
    private Double money;

    /**
     * 记录时间
     */
    @ApiModelProperty(name = "time", value = "记录时间", required = false)
    private Long time;
    /**
     * 日志明细
     */
    @ApiModelProperty(name = "detail", value = "日志明细", required = false)
    private String detail;


    public DepositeLogDO() {
    }

    public DepositeLogDO(Long memberId, String memberName, Double money, String detail) {
        this.memberId = memberId;
        this.memberName = memberName;
        this.money = money;
        this.detail = detail;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

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

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }


    @Override
    public String toString() {
        return "DepositeLogDO{" +
                "id=" + id +
                ", memberId=" + memberId +
                ", memberName='" + memberName + '\'' +
                ", money=" + money +
                ", detail='" + detail + '\'' +
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
        DepositeLogDO that = (DepositeLogDO) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(memberId, that.memberId)
                .append(memberName, that.memberName)
                .append(money, that.money)
                .append(detail, that.detail)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(memberId)
                .append(memberName)
                .append(money)
                .append(detail)
                .toHashCode();
    }
}
