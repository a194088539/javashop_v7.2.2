package com.enation.app.javashop.model.member.vo;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import	java.io.Serializable;

/**
 * @description:
 * @author: liuyulei
 * @create: 2019-12-31 20:39
 * @version:1.0
 * @since:7.1.4
 **/
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MemberDepositeVO implements Serializable {


    private static final long serialVersionUID = 7330113963321364215L;

    @ApiModelProperty(name = "is_used",value = "是否使用预存款抵扣")
    private Boolean isUsed;

    @ApiModelProperty(name = "is_pwd",value = "是否设置过密码,false：没有设置过密码，true：设置过密码")
    private Boolean isPwd;

    @ApiModelProperty(name = "balance",value = "预存款余额")
    private Double balance;



    public Boolean getIsUsed() {
        return isUsed;
    }

    public void setIsUsed(Boolean used) {
        isUsed = used;
    }

    public Boolean getIsPwd() {
        return isPwd;
    }

    public void setIsPwd(Boolean pwd) {
        isPwd = pwd;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "MemberDepositeVO{" +
                "isUsed=" + isUsed +
                ", isPwd=" + isPwd +
                ", balance=" + balance +
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
        MemberDepositeVO that = (MemberDepositeVO) o;

        return new EqualsBuilder()
                .append(isUsed, that.isUsed)
                .append(isPwd, that.isPwd)
                .append(balance, that.balance)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(isUsed)
                .append(isPwd)
                .append(balance)
                .toHashCode();
    }
}
