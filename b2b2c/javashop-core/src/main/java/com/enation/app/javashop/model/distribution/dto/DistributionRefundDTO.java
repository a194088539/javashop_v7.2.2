package com.enation.app.javashop.model.distribution.dto;

/**
 * DistributionRefundDTO
 *
 * @author Chopper
 * @version v1.0
 * @since v7.0
 * 2019-06-04 下午7:03
 */
public class DistributionRefundDTO {

    /**
     * lv1 会员id
     */
    private Long memberIdLv1;
    /**
     * lv2 会员id
     */
    private Long memberIdLv2;
    /**
     * lv1 会员返利
     */
    private Double refundLv1;
    /**
     * lv2 会员返利
     */
    private Double RefundLv2;

    /**
     * 退款金额
     */
    private Double refundMoney;


    public Double getRefundMoney() {
        return refundMoney;
    }

    public void setRefundMoney(Double refundMoney) {
        this.refundMoney = refundMoney;
    }

    public Long getMemberIdLv1() {
        return memberIdLv1;
    }

    public void setMemberIdLv1(Long memberIdLv1) {
        this.memberIdLv1 = memberIdLv1;
    }

    public Long getMemberIdLv2() {
        return memberIdLv2;
    }

    public void setMemberIdLv2(Long memberIdLv2) {
        this.memberIdLv2 = memberIdLv2;
    }

    public Double getRefundLv1() {
        return refundLv1;
    }

    public void setRefundLv1(Double refundLv1) {
        this.refundLv1 = refundLv1;
    }

    public Double getRefundLv2() {
        return RefundLv2;
    }

    public void setRefundLv2(Double refundLv2) {
        RefundLv2 = refundLv2;
    }
}
