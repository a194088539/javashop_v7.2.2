package com.enation.app.javashop.model.promotion.exchange.dos;

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

import java.io.Serializable;


/**
 * 积分兑换实体
 * @author Snow
 * @version v7.0.0
 * @since v7.0.0
 * 2018-03-21 11:47:18
 */
@TableName(value = "es_exchange")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ExchangeDO implements Serializable {

    private static final long serialVersionUID = 2173041743628504L;

    /**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(hidden=true)
    private Long exchangeId;

    /**商品id*/
    @ApiModelProperty(name="goodsId",value="商品id",required=false)
    private Long goodsId;

    /**商品所属积分分类*/
    @ApiModelProperty(name="categoryId",value="商品所属积分分类",required=false)
    private Long categoryId;

    /**是否允许兑换*/
    @ApiModelProperty(name="enable_exchange",value="是否允许兑换。0:否，1:是",required=false,allowableValues = "0,1")
    private Integer enableExchange;

    /**兑换所需金额*/
    @ApiModelProperty(name="exchange_money",value="兑换所需金额")
    private Double exchangeMoney;

    /**兑换所需积分*/
    @ApiModelProperty(name="exchange_point",value="兑换所需积分")
    private Integer exchangePoint;

    /**积分商品名称*/
    @ApiModelProperty(name="goods_name",value="商品名称")
    private String goodsName;

    /**积分商品原价*/
    @ApiModelProperty(name="goods_price",value="商品原价")
    private Double goodsPrice;

    /**积分商品图片*/
    @ApiModelProperty(name="goods_img",value="商品图片")
    private String goodsImg;

    @PrimaryKeyField
    public Long getExchangeId() {
        return exchangeId;
    }
    public void setExchangeId(Long exchangeId) {
        this.exchangeId = exchangeId;
    }

    public Long getGoodsId() {
        return goodsId;
    }
    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Long getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getEnableExchange() {
        return enableExchange;
    }
    public void setEnableExchange(Integer enableExchange) {
        this.enableExchange = enableExchange;
    }

    public Double getExchangeMoney() {
        return exchangeMoney;
    }
    public void setExchangeMoney(Double exchangeMoney) {
        this.exchangeMoney = exchangeMoney;
    }

    public Integer getExchangePoint() {
        return exchangePoint;
    }
    public void setExchangePoint(Integer exchangePoint) {
        this.exchangePoint = exchangePoint;
    }

    public String getGoodsName() {
        return goodsName;
    }
    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Double getGoodsPrice() {
        return goodsPrice;
    }
    public void setGoodsPrice(Double goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public String getGoodsImg() {
        return goodsImg;
    }
    public void setGoodsImg(String goodsImg) {
        this.goodsImg = goodsImg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }

        if (o == null || getClass() != o.getClass()){
            return false;
        }

        ExchangeDO that = (ExchangeDO) o;

        return new EqualsBuilder()
                .append(exchangeId, that.exchangeId)
                .append(goodsId, that.goodsId)
                .append(categoryId, that.categoryId)
                .append(enableExchange, that.enableExchange)
                .append(exchangeMoney, that.exchangeMoney)
                .append(exchangePoint, that.exchangePoint)
                .append(goodsName, that.goodsName)
                .append(goodsPrice, that.goodsPrice)
                .append(goodsImg, that.goodsImg)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(exchangeId)
                .append(goodsId)
                .append(categoryId)
                .append(enableExchange)
                .append(exchangeMoney)
                .append(exchangePoint)
                .append(goodsName)
                .append(goodsPrice)
                .append(goodsImg)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "ExchangeDO{" +
                "settingId=" + exchangeId +
                ", goodsId=" + goodsId +
                ", categoryId=" + categoryId +
                ", enableExchange=" + enableExchange +
                ", exchangeMoney=" + exchangeMoney +
                ", exchangePoint=" + exchangePoint +
                ", goodsName='" + goodsName + '\'' +
                ", goodsPrice=" + goodsPrice +
                ", goodsImg='" + goodsImg + '\'' +
                '}';
    }
}
