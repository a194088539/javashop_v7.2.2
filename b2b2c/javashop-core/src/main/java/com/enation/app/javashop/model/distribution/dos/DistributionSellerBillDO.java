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
 * 分销商家结算单
 *
 * @author Chopper
 * @version v1.0
 * @since v7.0
 * 2018-09-04 上午10:03
 */
@TableName("es_seller_bill")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DistributionSellerBillDO implements Serializable {



    @TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(value = "结算单id", name = "id")
    private Long id;


    /** 周期开始时间*/
    @ApiModelProperty(value="开始时间")
    private Long createTime;

    @ApiModelProperty(value = "商家id", name = "seller_id")
    private Long sellerId;

    @ApiModelProperty(value = "order_sn", name = "order_sn")
    private String orderSn;

    @ApiModelProperty(value = "商品反现支出", name = "expenditure")
    private Double expenditure;

    @ApiModelProperty(value = "商品反现退换", name = "return_expenditure")
    private Double returnExpenditure;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public Double getExpenditure() {
        return expenditure;
    }

    public void setExpenditure(Double expenditure) {
        this.expenditure = expenditure;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public Double getReturnExpenditure() {
        return returnExpenditure;
    }

    public void setReturnExpenditure(Double returnExpenditure) {
        this.returnExpenditure = returnExpenditure;
    }

    @Override
    public String toString() {
        return "DistributionSellerBillDO{" +
                "id=" + id +
                ", createTime=" + createTime +
                ", sellerId=" + sellerId +
                ", orderSn='" + orderSn + '\'' +
                ", expenditure=" + expenditure +
                ", returnExpenditure=" + returnExpenditure +
                '}';
    }
}
