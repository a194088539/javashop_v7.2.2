package com.enation.app.javashop.model.statistics.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enation.app.javashop.model.trade.order.dos.OrderDO;
import com.enation.app.javashop.model.trade.order.dos.OrderItemsDO;
import com.enation.app.javashop.framework.util.CurrencyUtil;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Map;

/**
 * 订单商品数据
 *
 * @author chopper
 * @version v1.0
 * @Description:
 * @since v7.0
 * 2018/3/22 下午11:51
 */

@TableName("es_sss_order_goods_data")
public class OrderGoodsData implements Serializable {

    @ApiModelProperty(value = "主键id")
    @TableId(type= IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty(value = "订单编号")
    private String orderSn;

    @ApiModelProperty(value = "商品id")
    private Long goodsId;

    @ApiModelProperty(value = "购买数量")
    private Integer goodsNum;


    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @ApiModelProperty(value = "商品价格")
    private Double price;


    @ApiModelProperty(value = "小记")
    private Double subTotal;


    @ApiModelProperty(value = "分类id")
    private Long categoryId;

    @ApiModelProperty(value = "分类path")
    private String categoryPath;

    @ApiModelProperty(value = "行业id")
    private Long industryId;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    public OrderGoodsData(OrderItemsDO orderItem, OrderDO order) {

        this.setCategoryId(orderItem.getCatId());
        this.setCreateTime(order.getCreateTime());
        this.setGoodsId(orderItem.getGoodsId());
        this.setGoodsName(orderItem.getName());
        this.setGoodsNum(orderItem.getNum());
        this.setOrderSn(order.getSn());
        this.setPrice(orderItem.getPrice());
        this.setSubTotal(CurrencyUtil.mul(orderItem.getPrice(), orderItem.getNum()));

    }

    public OrderGoodsData(Map<String, Object> map) {
        this.setCategoryId((Long) map.get("category_id"));
        this.setCreateTime((Long) map.get("create_time"));
        this.setGoodsId((Long) map.get("goods_id"));
        this.setGoodsName((String) map.get("goods_name"));
        this.setGoodsNum((Integer) map.get("goods_num"));
        this.setOrderSn((String) map.get("order_sn"));
        this.setPrice((Double) map.get("price"));
        this.setSubTotal((Double) map.get("sub_total"));
        this.setCategoryPath((String) map.get("category_path"));
        this.setIndustryId((Long) map.get("industry_id"));
    }

    public OrderGoodsData() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCategoryPath() {
        return categoryPath;
    }

    public void setCategoryPath(String categoryPath) {
        this.categoryPath = categoryPath;
    }

    public Long getIndustryId() {
        return industryId;
    }

    public void setIndustryId(Long industryId) {
        this.industryId = industryId;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(Double subTotal) {
        this.subTotal = subTotal;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public String toString() {
        return "OrderGoodsData{" +
                "id=" + id +
                ", orderSn='" + orderSn + '\'' +
                ", goodsId=" + goodsId +
                ", goodsNum=" + goodsNum +
                ", goodsName='" + goodsName + '\'' +
                ", price=" + price +
                ", subTotal=" + subTotal +
                ", categoryId=" + categoryId +
                ", categoryPath='" + categoryPath + '\'' +
                ", industryId=" + industryId +
                ", createTime=" + createTime +
                '}';
    }
}
