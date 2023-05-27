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
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.validation.constraints.NotEmpty;


/**
 * 会员钱包实体
 * @author liuyulei
 * @version v1.0
 * @since v7.2.0
 * 2019-12-30 16:24:51
 */
@TableName(value = "es_member_wallet")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MemberWalletDO implements Serializable {

    private static final long serialVersionUID = 3311432786588985L;

    /**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(hidden=true)
    private Long id;

    /**会员id*/
    @ApiModelProperty(name="member_id",value="会员id",required=false)
    private Long memberId;

    /**会员名称*/
    @NotEmpty(message="会员名称不能为空")
    @ApiModelProperty(name="member_name",value="会员名称",required=true)
    private String memberName;

    /**会员预存款，默认为0*/
    @NotEmpty(message="会员预存款，默认为0不能为空")
    @ApiModelProperty(name="pre_deposite",value="会员预存款，默认为0",required=true)
    private Double preDeposite;

    /**预存款密码，默认为-1*/
    @NotEmpty(message="预存款密码不能为空")
    @ApiModelProperty(name="deposite_password",value="预存款密码，默认为-1",required=false,hidden=true)
    private String depositePassword;

    public MemberWalletDO() {
    }

    public MemberWalletDO(Long memberId, @NotEmpty(message = "会员名称不能为空") String memberName) {
        this.memberId = memberId;
        this.memberName = memberName;
        this.preDeposite = 0D;
        this.depositePassword = "-1";
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

    public Double getPreDeposite() {
        return preDeposite;
    }

    public void setPreDeposite(Double preDeposite) {
        this.preDeposite = preDeposite;
    }

    public String getDepositePassword() {
        return depositePassword;
    }

    public void setDepositePassword(String depositePassword) {
        this.depositePassword = depositePassword;
    }

    @Override
    public String toString() {
        return "MemberDeposite{" +
                "id=" + id +
                ", memberId=" + memberId +
                ", memnerName='" + memberName + '\'' +
                ", preDeposite=" + preDeposite +
                ", depositePassword='" + depositePassword + '\'' +
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
        MemberWalletDO that = (MemberWalletDO) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(memberId, that.memberId)
                .append(memberName, that.memberName)
                .append(preDeposite, that.preDeposite)
                .append(depositePassword, that.depositePassword)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(memberId)
                .append(memberName)
                .append(preDeposite)
                .append(depositePassword)
                .toHashCode();
    }
}
