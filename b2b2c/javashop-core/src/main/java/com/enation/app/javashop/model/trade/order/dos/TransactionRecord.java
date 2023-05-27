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
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;


/**
 * 交易记录表实体
 * @author Snow
 * @version v7.0.0
 * @since v7.0.0
 * 2018-05-25 15:37:56
 */
@TableName("es_transaction_record")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TransactionRecord implements Serializable {

    private static final long serialVersionUID = 6751804777335135L;

    /**主键ID*/
    @TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(hidden=true)
    private Long recordId;
    /**订单编号*/
    @ApiModelProperty(name="order_sn",value="订单编号",required=false)
    private String orderSn;
    /**商品ID*/
    @ApiModelProperty(name="goods_id",value="商品ID",required=false)
    private Long goodsId;
    /**商品数量*/
    @ApiModelProperty(name="goods_num",value="商品数量",required=false)
    private Integer goodsNum;
    /**确认收货时间*/
    @ApiModelProperty(name="rog_time",value="确认收货时间",required=false)
    private Long rogTime;
    /**用户名*/
    @ApiModelProperty(name="uname",value="用户名",required=false)
    private String uname;
    /**交易价格*/
    @ApiModelProperty(name="price",value="交易价格",required=false)
    private Double price;
    /**会员ID*/
    @ApiModelProperty(name="member_id",value="会员ID",required=false)
    private Long memberId;

    /**确认收货时间*/
    @ApiModelProperty(name="create_time",value="订单创建时间",required=false)
    private Long createTime;

    @PrimaryKeyField
    public Long getRecordId() {
        return recordId;
    }
    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public String getOrderSn() {
        return orderSn;
    }
    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public Long getGoodsId() {
        return goodsId;
    }
    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getGoodsNum() {
        return goodsNum;
    }
    public void setGoodsNum(Integer goodsNum) {
        this.goodsNum = goodsNum;
    }

    public Long getRogTime() {
        return rogTime;
    }
    public void setRogTime(Long rogTime) {
        this.rogTime = rogTime;
    }

    public String getUname() {
        return uname;
    }
    public void setUname(String uname) {
        this.uname = uname;
    }

    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getMemberId() {
        return memberId;
    }
    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }

        if (o == null || getClass() != o.getClass()){
            return false;
        }

        TransactionRecord that = (TransactionRecord) o;

        return new EqualsBuilder()
                .append(recordId, that.recordId)
                .append(orderSn, that.orderSn)
                .append(goodsId, that.goodsId)
                .append(goodsNum, that.goodsNum)
                .append(rogTime, that.rogTime)
                .append(uname, that.uname)
                .append(price, that.price)
                .append(memberId, that.memberId)
                .append(createTime, that.createTime)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(recordId)
                .append(orderSn)
                .append(goodsId)
                .append(goodsNum)
                .append(rogTime)
                .append(uname)
                .append(price)
                .append(memberId)
                .append(createTime)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "TransactionRecord{" +
                "recordId=" + recordId +
                ", orderSn='" + orderSn + '\'' +
                ", goodsId=" + goodsId +
                ", goodsNum=" + goodsNum +
                ", rogTime=" + rogTime +
                ", uname='" + uname + '\'' +
                ", price=" + price +
                ", memberId=" + memberId +
                ", createTime=" + createTime +
                '}';
    }


}
