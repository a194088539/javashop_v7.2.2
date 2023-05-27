package com.enation.app.javashop.model.trade.order.dos;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enation.app.javashop.framework.database.annotation.Column;
import com.enation.app.javashop.framework.database.annotation.PrimaryKeyField;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
 * 订单出库状态实体
 * @author xlp
 * @version v2.0
 * @since v7.0.0
 * 2018-07-10 14:06:38
 */
@TableName("es_order_out_status")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class OrderOutStatus implements Serializable {

    private static final long serialVersionUID = 582319689134779L;

    /**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(hidden=true)
    private Long outId;

    /**订单编号*/
    @ApiModelProperty(name="order_sn",value="订单编号",required=false)
    private String orderSn;

    /**出库类型*/
    @ApiModelProperty(name="out_type",value="出库类型",required=false)
    private String outType;

    /**出库状态*/
    @ApiModelProperty(name="out_status",value="出库状态",required=false)
    private String outStatus;

    @PrimaryKeyField
    public Long getOutId() {
        return outId;
    }
    public void setOutId(Long outId) {
        this.outId = outId;
    }

    public String getOrderSn() {
        return orderSn;
    }
    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public String getOutType() {
        return outType;
    }
    public void setOutType(String outType) {
        this.outType = outType;
    }

    public String getOutStatus() {
        return outStatus;
    }
    public void setOutStatus(String outStatus) {
        this.outStatus = outStatus;
    }


	@Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        OrderOutStatus that = (OrderOutStatus) o;
        if (outId != null ? !outId.equals(that.outId) : that.outId != null) {return false;}
        if (orderSn != null ? !orderSn.equals(that.orderSn) : that.orderSn != null) {return false;}
        if (outType != null ? !outType.equals(that.outType) : that.outType != null) {return false;}
        return outStatus != null ? outStatus.equals(that.outStatus) : that.outStatus == null;
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = 31 * result + (outId != null ? outId.hashCode() : 0);
        result = 31 * result + (orderSn != null ? orderSn.hashCode() : 0);
        result = 31 * result + (outType != null ? outType.hashCode() : 0);
        result = 31 * result + (outStatus != null ? outStatus.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "OrderOutStatus{" +
                "outId=" + outId +
                ", orderSn='" + orderSn + '\'' +
                ", outType='" + outType + '\'' +
                ", outStatus='" + outStatus + '\'' +
                '}';
    }


}
