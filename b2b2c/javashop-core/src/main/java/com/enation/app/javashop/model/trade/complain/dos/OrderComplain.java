package com.enation.app.javashop.model.trade.complain.dos;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enation.app.javashop.framework.database.annotation.Column;
import com.enation.app.javashop.framework.database.annotation.Id;
import com.enation.app.javashop.framework.database.annotation.PrimaryKeyField;
import com.enation.app.javashop.framework.database.annotation.Table;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
 * 交易投诉表实体
 *
 * @author fk
 * @version v2.0
 * @since v2.0
 * 2019-11-27 16:48:27
 */
@TableName("es_order_complain")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class OrderComplain implements Serializable {

    private static final long serialVersionUID = 2519567680196080L;

    /**
     * 主键
     */
    @TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(hidden = true)
    private Long complainId;
    /**
     * 投诉主题
     */
    @ApiModelProperty(name = "complain_topic", value = "投诉主题", required = false)
    private String complainTopic;
    /**
     * 投诉内容
     */
    @ApiModelProperty(name = "content", value = "投诉内容", required = false)
    private String content;
    /**
     * 投诉时间
     */
    @ApiModelProperty(name = "create_time", value = "投诉时间", required = false)
    private Long createTime;
    /**
     * 投诉凭证图片
     */
    @ApiModelProperty(name = "images", value = "投诉凭证图片", required = false)
    private String images;
    /**
     * 状态，见ComplainStatusEnum.java
     */
    @ApiModelProperty(name = "status", value = "状态，见ComplainStatusEnum.java", required = false)
    private String status;
    /**
     * 商家申诉内容
     */
    @ApiModelProperty(name = "appeal_content", value = "商家申诉内容", required = false)
    private String appealContent;
    /**
     * 商家申诉时间
     */
    @ApiModelProperty(name = "appeal_time", value = "商家申诉时间", required = false)
    private Long appealTime;
    /**
     * 商家申诉上传的图片
     */
    @ApiModelProperty(name = "appeal_images", value = "商家申诉上传的图片", required = false)
    private String appealImages;
    /**
     * 订单号
     */
    @ApiModelProperty(name = "order_sn", value = "订单号", required = false)
    private String orderSn;
    /**
     * 下单时间
     */
    @ApiModelProperty(name = "order_time", value = "下单时间", required = false)
    private Long orderTime;
    /**
     * 商品名称
     */
    @ApiModelProperty(name = "goods_name", value = "商品名称", required = false)
    private String goodsName;
    /**
     * 商品id
     */
    @ApiModelProperty(name = "goods_id", value = "商品id", required = false)
    private Long goodsId;

    /**
     * skuid
     */
    @ApiModelProperty(name = "sku_id", value = "sku主键", required = false)
    private Long skuId;
    /**
     * 商品价格
     */
    @ApiModelProperty(name = "goods_price", value = "商品价格", required = false)
    private Double goodsPrice;

    /**
     * 商品图片
     */
    @ApiModelProperty(name = "goods_image", value = "商品图片", required = false)
    private String goodsImage;
    /**
     * 购买的商品数量
     */
    @ApiModelProperty(name = "num", value = "购买的商品数量", required = false)
    private Integer num;
    /**
     * 运费
     */
    @ApiModelProperty(name = "shipping_price", value = "运费", required = false)
    private Double shippingPrice;
    /**
     * 订单金额
     */
    @ApiModelProperty(name = "order_price", value = "订单金额", required = false)
    private Double orderPrice;
    /**
     * 物流单号
     */
    @ApiModelProperty(name = "ship_no", value = "物流单号", required = false)
    private String shipNo;
    /**
     * 商家id
     */
    @ApiModelProperty(name = "seller_id", value = "商家id", required = false)
    private Long sellerId;
    /**
     * 商家名称
     */
    @ApiModelProperty(name = "seller_name", value = "商家名称", required = false)
    private String sellerName;
    /**
     * 会员id
     */
    @ApiModelProperty(name = "member_id", value = "会员id", required = false)
    private Long memberId;
    /**
     * 会员名称
     */
    @ApiModelProperty(name = "member_name", value = "会员名称", required = false)
    private String memberName;
    /**
     * 收货人
     */
    @ApiModelProperty(name = "ship_name", value = "收货人", required = false)
    private String shipName;
    /**
     * 收货地址
     */
    @ApiModelProperty(name = "ship_addr", value = "收货地址", required = false)
    private String shipAddr;
    /**
     * 收货人手机
     */
    @ApiModelProperty(name = "ship_mobile", value = "收货人手机", required = false)
    private String shipMobile;
    /**
     * 仲裁结果
     */
    @ApiModelProperty(name = "arbitration_result", value = "仲裁结果", required = false)
    private String arbitrationResult;

    @PrimaryKeyField
    public Long getComplainId() {
        return complainId;
    }

    public void setComplainId(Long complainId) {
        this.complainId = complainId;
    }

    public String getComplainTopic() {
        return complainTopic;
    }

    public void setComplainTopic(String complainTopic) {
        this.complainTopic = complainTopic;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAppealContent() {
        return appealContent;
    }

    public void setAppealContent(String appealContent) {
        this.appealContent = appealContent;
    }

    public Long getAppealTime() {
        return appealTime;
    }

    public void setAppealTime(Long appealTime) {
        this.appealTime = appealTime;
    }

    public String getAppealImages() {
        return appealImages;
    }

    public void setAppealImages(String appealImages) {
        this.appealImages = appealImages;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public Long getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Long orderTime) {
        this.orderTime = orderTime;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Double getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(Double goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Double getShippingPrice() {
        return shippingPrice;
    }

    public void setShippingPrice(Double shippingPrice) {
        this.shippingPrice = shippingPrice;
    }

    public Double getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(Double orderPrice) {
        this.orderPrice = orderPrice;
    }

    public String getShipNo() {
        return shipNo;
    }

    public void setShipNo(String shipNo) {
        this.shipNo = shipNo;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
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

    public String getShipName() {
        return shipName;
    }

    public void setShipName(String shipName) {
        this.shipName = shipName;
    }

    public String getShipAddr() {
        return shipAddr;
    }

    public void setShipAddr(String shipAddr) {
        this.shipAddr = shipAddr;
    }

    public String getShipMobile() {
        return shipMobile;
    }

    public void setShipMobile(String shipMobile) {
        this.shipMobile = shipMobile;
    }

    public String getArbitrationResult() {
        return arbitrationResult;
    }

    public void setArbitrationResult(String arbitrationResult) {
        this.arbitrationResult = arbitrationResult;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public String getGoodsImage() {
        return goodsImage;
    }

    public void setGoodsImage(String goodsImage) {
        this.goodsImage = goodsImage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OrderComplain that = (OrderComplain) o;
        if (complainId != null ? !complainId.equals(that.complainId) : that.complainId != null) {
            return false;
        }
        if (complainTopic != null ? !complainTopic.equals(that.complainTopic) : that.complainTopic != null) {
            return false;
        }
        if (content != null ? !content.equals(that.content) : that.content != null) {
            return false;
        }
        if (createTime != null ? !createTime.equals(that.createTime) : that.createTime != null) {
            return false;
        }
        if (images != null ? !images.equals(that.images) : that.images != null) {
            return false;
        }
        if (status != null ? !status.equals(that.status) : that.status != null) {
            return false;
        }
        if (appealContent != null ? !appealContent.equals(that.appealContent) : that.appealContent != null) {
            return false;
        }
        if (appealTime != null ? !appealTime.equals(that.appealTime) : that.appealTime != null) {
            return false;
        }
        if (appealImages != null ? !appealImages.equals(that.appealImages) : that.appealImages != null) {
            return false;
        }
        if (orderSn != null ? !orderSn.equals(that.orderSn) : that.orderSn != null) {
            return false;
        }
        if (orderTime != null ? !orderTime.equals(that.orderTime) : that.orderTime != null) {
            return false;
        }
        if (goodsName != null ? !goodsName.equals(that.goodsName) : that.goodsName != null) {
            return false;
        }
        if (goodsId != null ? !goodsId.equals(that.goodsId) : that.goodsId != null) {
            return false;
        }
        if (goodsPrice != null ? !goodsPrice.equals(that.goodsPrice) : that.goodsPrice != null) {
            return false;
        }
        if (num != null ? !num.equals(that.num) : that.num != null) {
            return false;
        }
        if (shippingPrice != null ? !shippingPrice.equals(that.shippingPrice) : that.shippingPrice != null) {
            return false;
        }
        if (orderPrice != null ? !orderPrice.equals(that.orderPrice) : that.orderPrice != null) {
            return false;
        }
        if (shipNo != null ? !shipNo.equals(that.shipNo) : that.shipNo != null) {
            return false;
        }
        if (sellerId != null ? !sellerId.equals(that.sellerId) : that.sellerId != null) {
            return false;
        }
        if (sellerName != null ? !sellerName.equals(that.sellerName) : that.sellerName != null) {
            return false;
        }
        if (memberId != null ? !memberId.equals(that.memberId) : that.memberId != null) {
            return false;
        }
        if (memberName != null ? !memberName.equals(that.memberName) : that.memberName != null) {
            return false;
        }
        if (shipName != null ? !shipName.equals(that.shipName) : that.shipName != null) {
            return false;
        }
        if (shipAddr != null ? !shipAddr.equals(that.shipAddr) : that.shipAddr != null) {
            return false;
        }
        if (shipMobile != null ? !shipMobile.equals(that.shipMobile) : that.shipMobile != null) {
            return false;
        }
        if (arbitrationResult != null ? !arbitrationResult.equals(that.arbitrationResult) : that.arbitrationResult != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = 31 * result + (complainId != null ? complainId.hashCode() : 0);
        result = 31 * result + (complainTopic != null ? complainTopic.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
        result = 31 * result + (images != null ? images.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (appealContent != null ? appealContent.hashCode() : 0);
        result = 31 * result + (appealTime != null ? appealTime.hashCode() : 0);
        result = 31 * result + (appealImages != null ? appealImages.hashCode() : 0);
        result = 31 * result + (orderSn != null ? orderSn.hashCode() : 0);
        result = 31 * result + (orderTime != null ? orderTime.hashCode() : 0);
        result = 31 * result + (goodsName != null ? goodsName.hashCode() : 0);
        result = 31 * result + (goodsId != null ? goodsId.hashCode() : 0);
        result = 31 * result + (goodsPrice != null ? goodsPrice.hashCode() : 0);
        result = 31 * result + (num != null ? num.hashCode() : 0);
        result = 31 * result + (shippingPrice != null ? shippingPrice.hashCode() : 0);
        result = 31 * result + (orderPrice != null ? orderPrice.hashCode() : 0);
        result = 31 * result + (shipNo != null ? shipNo.hashCode() : 0);
        result = 31 * result + (sellerId != null ? sellerId.hashCode() : 0);
        result = 31 * result + (sellerName != null ? sellerName.hashCode() : 0);
        result = 31 * result + (memberId != null ? memberId.hashCode() : 0);
        result = 31 * result + (memberName != null ? memberName.hashCode() : 0);
        result = 31 * result + (shipName != null ? shipName.hashCode() : 0);
        result = 31 * result + (shipAddr != null ? shipAddr.hashCode() : 0);
        result = 31 * result + (shipMobile != null ? shipMobile.hashCode() : 0);
        result = 31 * result + (arbitrationResult != null ? arbitrationResult.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "OrderComplain{" +
                "complainId=" + complainId +
                ", complainTopic='" + complainTopic + '\'' +
                ", content='" + content + '\'' +
                ", createTime=" + createTime +
                ", images='" + images + '\'' +
                ", status='" + status + '\'' +
                ", appealContent='" + appealContent + '\'' +
                ", appealTime=" + appealTime +
                ", appealImages='" + appealImages + '\'' +
                ", orderSn='" + orderSn + '\'' +
                ", orderTime=" + orderTime +
                ", goodsName='" + goodsName + '\'' +
                ", goodsId=" + goodsId +
                ", goodsPrice=" + goodsPrice +
                ", num=" + num +
                ", shippingPrice=" + shippingPrice +
                ", orderPrice=" + orderPrice +
                ", shipNo='" + shipNo + '\'' +
                ", sellerId=" + sellerId +
                ", sellerName='" + sellerName + '\'' +
                ", memberId=" + memberId +
                ", memberName='" + memberName + '\'' +
                ", shipName='" + shipName + '\'' +
                ", shipAddr='" + shipAddr + '\'' +
                ", shipMobile='" + shipMobile + '\'' +
                ", arbitrationResult='" + arbitrationResult + '\'' +
                '}';
    }


}
