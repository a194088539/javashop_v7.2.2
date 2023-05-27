package com.enation.app.javashop.model.member.dos;

import java.io.Serializable;
import java.util.Objects;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enation.app.javashop.framework.database.annotation.PrimaryKeyField;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;


/**
 * 会员发票信息缓存实体
 *
 * @author duanmingyu
 * @version v7.1.4
 * @since v7.0.0
 * 2019-06-19
 */
@TableName(value = "es_member_receipt")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MemberReceipt implements Serializable {

    private static final long serialVersionUID = 4743090485786622L;

    /**
     * 会员发票id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(hidden = true)
    private Long receiptId;
    /**
     * 会员id
     */
    @ApiModelProperty(name = "member_id", value = "会员id", required = false)
    private Long memberId;
    /**
     * 发票类型 ELECTRO：电子普通发票，VATORDINARY：增值税普通发票
     */
    @ApiModelProperty(name = "receipt_type", value = "发票类型 ELECTRO：电子普通发票，VATORDINARY：增值税普通发票", required = false)
    private String receiptType;
    /**
     * 发票抬头
     */
    @ApiModelProperty(name = "receipt_title", value = "发票抬头", required = false)
    @Length(min=1,max = 60,message = "发票抬头在1到60之间")
    private String receiptTitle;
    /**
     * 发票内容
     */
    @ApiModelProperty(name = "receipt_content", value = "发票内容", required = false)
    private String receiptContent;
    /**
     * 发票税号
     */
    @ApiModelProperty(name = "tax_no", value = "发票税号", required = false)
    private String taxNo;
    /**
     * 收票人手机号
     */
    @ApiModelProperty(name = "member_mobile", value = "收票人手机号", required = false)
    private String memberMobile;
    /**
     * 收票人邮箱
     */
    @ApiModelProperty(name = "member_email", value = "收票人邮箱", required = false)
    private String memberEmail;
    /**
     * 是否为默认选项 0：否，1：是
     */
    @ApiModelProperty(name = "is_default", value = "是否为默认选项 0：否，1：是", required = false)
    private Integer isDefault;

    @PrimaryKeyField
    public Long getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(Long receiptId) {
        this.receiptId = receiptId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getReceiptType() {
        return receiptType;
    }

    public void setReceiptType(String receiptType) {
        this.receiptType = receiptType;
    }

    public String getReceiptTitle() {
        return receiptTitle;
    }

    public void setReceiptTitle(String receiptTitle) {
        this.receiptTitle = receiptTitle;
    }

    public String getReceiptContent() {
        return receiptContent;
    }

    public void setReceiptContent(String receiptContent) {
        this.receiptContent = receiptContent;
    }

    public String getTaxNo() {
        return taxNo;
    }

    public void setTaxNo(String taxNo) {
        this.taxNo = taxNo;
    }

    public String getMemberMobile() {
        return memberMobile;
    }

    public void setMemberMobile(String memberMobile) {
        this.memberMobile = memberMobile;
    }

    public String getMemberEmail() {
        return memberEmail;
    }

    public void setMemberEmail(String memberEmail) {
        this.memberEmail = memberEmail;
    }

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MemberReceipt that = (MemberReceipt) o;
        return Objects.equals(receiptId, that.receiptId) &&
                Objects.equals(memberId, that.memberId) &&
                Objects.equals(receiptType, that.receiptType) &&
                Objects.equals(receiptTitle, that.receiptTitle) &&
                Objects.equals(receiptContent, that.receiptContent) &&
                Objects.equals(taxNo, that.taxNo) &&
                Objects.equals(memberMobile, that.memberMobile) &&
                Objects.equals(memberEmail, that.memberEmail) &&
                Objects.equals(isDefault, that.isDefault);
    }

    @Override
    public int hashCode() {
        return Objects.hash(receiptId, memberId, receiptType, receiptTitle, receiptContent, taxNo, memberMobile, memberEmail, isDefault);
    }

    @Override
    public String toString() {
        return "MemberReceipt{" +
                "receiptId=" + receiptId +
                ", memberId=" + memberId +
                ", receiptType='" + receiptType + '\'' +
                ", receiptTitle='" + receiptTitle + '\'' +
                ", receiptContent='" + receiptContent + '\'' +
                ", taxNo='" + taxNo + '\'' +
                ", memberMobile='" + memberMobile + '\'' +
                ", memberEmail='" + memberEmail + '\'' +
                ", isDefault=" + isDefault +
                '}';
    }
}
