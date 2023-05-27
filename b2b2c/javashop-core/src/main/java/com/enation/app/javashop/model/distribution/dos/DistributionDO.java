package com.enation.app.javashop.model.distribution.dos;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enation.app.javashop.framework.database.annotation.Column;
import com.enation.app.javashop.framework.database.annotation.Id;
import com.enation.app.javashop.framework.database.annotation.Table;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 分销商
 *
 * @author Chopper
 * @version v1.0
 * @Description:
 * @since v7.0
 * 2018/5/21 上午11:25
 */

@TableName("es_distribution")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DistributionDO implements Serializable {

    /**
     * 主键id
     */
    @TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(hidden = true)
    private Long id;

    /**
     * 关联会员的id
     */
    @ApiModelProperty(value = "会员id", required = true)
    private Long memberId;


    /**
     * 会员名称
     */
    @ApiModelProperty(value = "会员名称", required = true)
    private String memberName;


    /**
     * 关系路径
     */
    @ApiModelProperty(value = "关系路径", required = true)
    private String path;

    /**
     * 2级分销商id（上上级）
     */
    @ApiModelProperty(name = "member_id_lv2", value = "2级分销商id（上上级）", required = true)
    private Long memberIdLv2;

    /**
     * 1级分销商id（上级）
     */
    @ApiModelProperty(name = "member_id_lv1", value = "1级分销商id（上级）", required = true)
    private Long memberIdLv1;

    /**
     * 下线人数
     */
    @ApiModelProperty(value = "下线人数", required = true)
    private Integer downline = 0;

    /**
     * 提成相关订单数
     */
    @ApiModelProperty(name = "order_num", value = "提成相关订单数", required = true)
    private Integer orderNum = 0;

    /**
     * 返利总额
     */
    @ApiModelProperty(name = "rebate_total", value = "返利总额", required = true)
    private Double rebateTotal = 0D;

    /**
     * 营业额总额
     */
    @ApiModelProperty(name = "turnover_price", value = "营业额总额", required = true)
    private Double turnoverPrice = 0D;

    /**
     * 可提现金额
     */
    @ApiModelProperty(name = "can_rebate", value = "可提现金额", required = true)
    private Double canRebate = 0D;

    /**
     * 返利金额冻结
     */
    @ApiModelProperty(name = "commission_frozen", value = "冻结金额", required = true)
    private Double commissionFrozen = 0D;


    /**
     * 提现冻结
     */
    @ApiModelProperty(name = "withdraw_frozen_price", value = "冻结金额", required = true)
    private Double withdrawFrozenPrice = 0D;

    /**
     * 使用模板id
     */
    @ApiModelProperty(name = "current_tpl_id", value = "使用模板id", required = true)
    private Long currentTplId;


    /**
     * 使用模板id
     */
    @ApiModelProperty(name = "current_tpl_name", value = "使用模板名称", required = true)
    private String currentTplName;

    /**
     * 创建时间
     */
    @ApiModelProperty(name = "create_time", value = "创建时间", required = false)
    private Long createTime;


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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getMemberIdLv2() {
        return memberIdLv2;
    }

    public void setMemberIdLv2(Long memberIdLv2) {
        this.memberIdLv2 = memberIdLv2;
    }

    public Long getMemberIdLv1() {
        return memberIdLv1;
    }

    public void setMemberIdLv1(Long memberIdLv1) {
        this.memberIdLv1 = memberIdLv1;
    }

    public Integer getDownline() {
        return downline;
    }

    public void setDownline(Integer downline) {
        this.downline = downline;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public Double getRebateTotal() {
        return rebateTotal;
    }

    public void setRebateTotal(Double rebateTotal) {
        this.rebateTotal = rebateTotal;
    }

    public Double getTurnoverPrice() {
        return turnoverPrice;
    }

    public void setTurnoverPrice(Double turnoverPrice) {
        this.turnoverPrice = turnoverPrice;
    }

    public Double getCanRebate() {
        return canRebate;
    }

    public void setCanRebate(Double canRebate) {
        this.canRebate = canRebate;
    }

    public Double getCommissionFrozen() {
        return commissionFrozen;
    }

    public void setCommissionFrozen(Double commissionFrozen) {
        this.commissionFrozen = commissionFrozen;
    }

    public Long getCurrentTplId() {
        return currentTplId;
    }

    public void setCurrentTplId(Long currentTplId) {
        this.currentTplId = currentTplId;
    }

    public String getCurrentTplName() {
        return currentTplName;
    }

    public void setCurrentTplName(String currentTplName) {
        this.currentTplName = currentTplName;
    }

    public Double getWithdrawFrozenPrice() {
        return withdrawFrozenPrice;
    }

    public void setWithdrawFrozenPrice(Double withdrawFrozenPrice) {
        this.withdrawFrozenPrice = withdrawFrozenPrice;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "DistributionDO{" +
                " memberId=" + memberId +
                ", memberName='" + memberName + '\'' +
                ", path='" + path + '\'' +
                ", memberIdLv2=" + memberIdLv2 +
                ", memberIdLv1=" + memberIdLv1 +
                ", downline=" + downline +
                ", orderNum=" + orderNum +
                ", rebateTotal=" + rebateTotal +
                ", turnoverPrice=" + turnoverPrice +
                ", canRebate=" + canRebate +
                ", commissionFrozen=" + commissionFrozen +
                ", withdrawFrozenPrice=" + withdrawFrozenPrice +
                ", currentTplId=" + currentTplId +
                ", currentTplName='" + currentTplName + '\'' +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DistributionDO that = (DistributionDO) o;
        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (memberId != null ? !memberId.equals(that.memberId) : that.memberId != null) {
            return false;
        }
        if (memberName != null ? !memberName.equals(that.memberName) : that.memberName != null) {
            return false;
        }
        if (path != null ? !path.equals(that.path) : that.path != null) {
            return false;
        }
        if (memberIdLv2 != null ? !memberIdLv2.equals(that.memberIdLv2) : that.memberIdLv2 != null) {
            return false;
        }
        if (memberIdLv1 != null ? !memberIdLv1.equals(that.memberIdLv1) : that.memberIdLv1 != null) {
            return false;
        }
        if (downline != null ? !downline.equals(that.downline) : that.downline != null) {
            return false;
        }
        if (orderNum != null ? !orderNum.equals(that.orderNum) : that.orderNum != null) {
            return false;
        }
        if (currentTplId != null ? !currentTplId.equals(that.currentTplId) : that.currentTplId != null) {
            return false;
        }
        if (currentTplName != null ? !currentTplName.equals(that.currentTplName) : that.currentTplName != null) {
            return false;
        }
        if (rebateTotal != null ? !rebateTotal.equals(that.rebateTotal) : that.rebateTotal != null) {
            return false;
        }
        if (turnoverPrice != null ? !turnoverPrice.equals(that.turnoverPrice) : that.turnoverPrice != null) {
            return false;
        }
        if (canRebate != null ? !canRebate.equals(that.canRebate) : that.canRebate != null) {
            return false;
        }
        if (commissionFrozen != null ? !commissionFrozen.equals(that.commissionFrozen) : that.commissionFrozen != null) {
            return false;
        }
        if (withdrawFrozenPrice != null ? !withdrawFrozenPrice.equals(that.withdrawFrozenPrice) : that.withdrawFrozenPrice != null) {
            return false;
        }
        return createTime != null ? createTime.equals(that.createTime) : that.createTime == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (memberId != null ? memberId.hashCode() : 0);
        result = 31 * result + (memberName != null ? memberName.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (memberIdLv2 != null ? memberIdLv2.hashCode() : 0);
        result = 31 * result + (memberIdLv1 != null ? memberIdLv1.hashCode() : 0);
        result = 31 * result + (downline != null ? downline.hashCode() : 0);
        result = 31 * result + (orderNum != null ? orderNum.hashCode() : 0);
        result = 31 * result + (rebateTotal != null ? rebateTotal.hashCode() : 0);
        result = 31 * result + (turnoverPrice != null ? turnoverPrice.hashCode() : 0);
        result = 31 * result + (canRebate != null ? canRebate.hashCode() : 0);
        result = 31 * result + (commissionFrozen != null ? commissionFrozen.hashCode() : 0);
        result = 31 * result + (withdrawFrozenPrice != null ? withdrawFrozenPrice.hashCode() : 0);
        result = 31 * result + (currentTplId != null ? currentTplId.hashCode() : 0);
        result = 31 * result + (currentTplName != null ? currentTplName.hashCode() : 0);
        result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
        return result;
    }

}
