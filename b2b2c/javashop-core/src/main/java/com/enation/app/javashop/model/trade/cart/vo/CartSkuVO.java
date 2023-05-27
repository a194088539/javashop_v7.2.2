package com.enation.app.javashop.model.trade.cart.vo;

import com.enation.app.javashop.model.goods.vo.SpecValueVO;
import com.enation.app.javashop.model.goods.vo.TemplateScript;
import com.enation.app.javashop.model.trade.order.enums.OrderServiceStatusEnum;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.core.annotation.Order;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 购物车中的产品
 *
 * @author Snow
 * @version v2.0
 * 2018年03月19日21:54:35
 * @since v7.0.0
 */
@ApiModel(value = "sku", description = "产品")
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@Order
public class CartSkuVO implements Serializable {


    private static final long serialVersionUID = -2761425455060777922L;

    /**
     * 在构造器里初始化促销列表，规格列表
     */
    public CartSkuVO() {
        this.checked = 1;
        this.invalid = 0;
        this.purchaseNum = 0;
        specList = new ArrayList<SpecValueVO>();
        promotionTags = new ArrayList<>();
    }

    @ApiModelProperty(value = "卖家id")
    private Long sellerId;

    @ApiModelProperty(value = "卖家姓名")
    private String sellerName;

    @ApiModelProperty(value = "商品id")
    private Long goodsId;

    @ApiModelProperty(value = "产品id")
    private Long skuId;

    @ApiModelProperty(value = "产品sn")
    private String skuSn;

    @ApiModelProperty(value = "商品所属的分类id")
    private Long catId;

    @ApiModelProperty(value = "购买数量")
    private Integer num;

    @ApiModelProperty(value = "优惠数量数量")
    private Integer purchaseNum;

    @ApiModelProperty(value = "商品重量")
    private Double goodsWeight;

    @ApiModelProperty(value = "商品原价")
    private Double originalPrice;

    @ApiModelProperty(value = "购买时的成交价")
    private Double purchasePrice;

    @ApiModelProperty(value = "小计")
    private Double subtotal;

    @ApiModelProperty(value = "商品名称")
    private String name;

    @ApiModelProperty(value = "商品图片")
    private String goodsImage;

    @ApiModelProperty(value = "模板脚本")
    private TemplateScript templateScript;

    /**
     * 是否选中，要去结算 0:未选中 1:已选中，默认
     */
    @ApiModelProperty(value = "是否选中，要去结算")
    private Integer checked;


    @ApiModelProperty(value = "是否免运费,1：商家承担运费（免运费） 0：买家承担运费")
    private Integer isFreeFreight;

    @ApiModelProperty(value = "已参与的单品活动工具列表")
    private List<CartPromotionVo> singleList;

    @ApiModelProperty(value = "已参与的组合活动工具列表")
    private List<CartPromotionVo> groupList;

    @ApiModelProperty(value = "此商品需要提示给顾客的优惠标签")
    private List<String> promotionTags;

    @ApiModelProperty(value = "不参与活动")
    private Integer notJoinPromotion;

    @ApiModelProperty(value = "运费模板id")
    private Long templateId;

    /**
     * 商品规格列表
     */
    @ApiModelProperty(value = "规格列表")
    private List<SpecValueVO> specList;

    /**
     * 积分换购活动中，购买这个商品需要消费的积分。
     */
    @ApiModelProperty(value = "使用积分")
    private Integer point;

    @ApiModelProperty(value = "快照ID")
    private Integer snapshotId;

    @ApiModelProperty(value = "售后状态")
    private String serviceStatus;

    @ApiModelProperty(name = "last_modify", value = "最后修改时间")
    private Long lastModify;

    @ApiModelProperty(name = "enable_quantity", value = "可用库存")
    private Integer enableQuantity;

    @ApiModelProperty(value = "是否失效：0:正常 1:已失效")
    private Integer invalid;

    @ApiModelProperty(value = "购物车商品错误消息")
    private String errorMessage;

    @ApiModelProperty(value = "是否可配送  1可配送（有货）0  不可配送（无货）")
    private Integer isShip;

    @ApiModelProperty(name = "goods_type", value = "商品类型NORMAL普通POINT积分")
    private String goodsType;

    @ApiModelProperty(name = "give_gift", value = "促销活动，赠送赠品信息")
    private String giveGift;


    public Integer getPurchaseNum() {
        return purchaseNum;
    }

    public void setPurchaseNum(Integer purchaseNum) {
        this.purchaseNum = purchaseNum;
    }

    public Integer getIsShip() {
        if (isShip == null) {
            return 1;
        }
        return isShip;
    }

    public void setIsShip(Integer isShip) {
        this.isShip = isShip;
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

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public String getSkuSn() {
        return skuSn;
    }

    public void setSkuSn(String skuSn) {
        this.skuSn = skuSn;
    }

    public Long getCatId() {
        return catId;
    }

    public void setCatId(Long catId) {
        this.catId = catId;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }


    public Integer getInvalid() {
        return invalid;
    }

    public void setInvalid(Integer invalid) {
        this.invalid = invalid;
    }

    public Double getGoodsWeight() {
        if (this.goodsWeight == null) {
            return 0.0;
        }
        return goodsWeight;
    }

    public void setGoodsWeight(Double goodsWeight) {
        this.goodsWeight = goodsWeight;
    }

    public Double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(Double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGoodsImage() {
        return goodsImage;
    }

    public void setGoodsImage(String goodsImage) {
        this.goodsImage = goodsImage;
    }


    public Integer getIsFreeFreight() {
        return isFreeFreight;
    }

    public void setIsFreeFreight(Integer isFreeFreight) {
        this.isFreeFreight = isFreeFreight;
    }

    public List<CartPromotionVo> getSingleList() {
        return singleList;
    }

    public void setSingleList(List<CartPromotionVo> singleList) {
        this.singleList = singleList;
    }

    public List<CartPromotionVo> getGroupList() {
        if (groupList == null) {
            return new ArrayList<>();
        }
        return groupList;
    }

    public void setGroupList(List<CartPromotionVo> groupList) {
        this.groupList = groupList;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public List<SpecValueVO> getSpecList() {
        return specList;
    }

    public void setSpecList(List<SpecValueVO> specList) {
        this.specList = specList;
    }

    public Integer getPoint() {
        if(point == null ){
            point = 0;
        }
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    public Integer getSnapshotId() {
        return snapshotId;
    }

    public void setSnapshotId(Integer snapshotId) {
        this.snapshotId = snapshotId;
    }

    public Long getLastModify() {
        return lastModify;
    }

    public void setLastModify(Long lastModify) {
        this.lastModify = lastModify;
    }

    public String getServiceStatus() {
        if (serviceStatus == null) {
            serviceStatus = OrderServiceStatusEnum.NOT_APPLY.value();
        }
        return serviceStatus;
    }

    public void setServiceStatus(String serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    public Integer getChecked() {
        return checked;
    }

    public void setChecked(Integer checked) {
        this.checked = checked;
    }

    public Integer getEnableQuantity() {
        return enableQuantity;
    }

    public void setEnableQuantity(Integer enableQuantity) {
        this.enableQuantity = enableQuantity;
    }

    public List<String> getPromotionTags() {
        return promotionTags;
    }

    public void setPromotionTags(List<String> promotionTags) {
        this.promotionTags = promotionTags;
    }



    public String getErrorMessage() {
        if (errorMessage == null) {
            return "";
        }
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getNotJoinPromotion() {

        if (notJoinPromotion == null) {
            return 0;
        }
        return notJoinPromotion;
    }

    public void setNotJoinPromotion(Integer notJoinPromotion) {
        this.notJoinPromotion = notJoinPromotion;
    }

    public String getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(String goodsType) {
        this.goodsType = goodsType;
    }

    public TemplateScript getTemplateScript() {
        return templateScript;
    }

    public void setTemplateScript(TemplateScript templateScript) {
        this.templateScript = templateScript;
    }

    public String getGiveGift() {
        return giveGift;
    }

    public void setGiveGift(String giveGift) {
        this.giveGift = giveGift;
    }

    @Override
    public String toString() {
        return "CartSkuVO{" +
                "sellerId=" + sellerId +
                ", sellerName='" + sellerName + '\'' +
                ", goodsId=" + goodsId +
                ", skuId=" + skuId +
                ", skuSn='" + skuSn + '\'' +
                ", catId=" + catId +
                ", num=" + num +
                ", purchaseNum=" + purchaseNum +
                ", goodsWeight=" + goodsWeight +
                ", originalPrice=" + originalPrice +
                ", purchasePrice=" + purchasePrice +
                ", subtotal=" + subtotal +
                ", name='" + name + '\'' +
                ", goodsImage='" + goodsImage + '\'' +
                ", templateScript=" + templateScript +
                ", checked=" + checked +
                ", isFreeFreight=" + isFreeFreight +
                ", singleList=" + singleList +
                ", groupList=" + groupList +
                ", promotionTags=" + promotionTags +
                ", notJoinPromotion=" + notJoinPromotion +
                ", templateId=" + templateId +
                ", specList=" + specList +
                ", point=" + point +
                ", snapshotId=" + snapshotId +
                ", serviceStatus='" + serviceStatus + '\'' +
                ", lastModify=" + lastModify +
                ", enableQuantity=" + enableQuantity +
                ", invalid=" + invalid +
                ", errorMessage='" + errorMessage + '\'' +
                ", isShip=" + isShip +
                ", goodsType='" + goodsType + '\'' +
                ", giveGift='" + giveGift + '\'' +
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
        CartSkuVO that = (CartSkuVO) o;

        return new EqualsBuilder()
                .append(sellerId, that.sellerId)
                .append(sellerName, that.sellerName)
                .append(goodsId, that.goodsId)
                .append(skuId, that.skuId)
                .append(skuSn, that.skuSn)
                .append(catId, that.catId)
                .append(num, that.num)
                .append(purchaseNum, that.purchaseNum)
                .append(goodsWeight, that.goodsWeight)
                .append(originalPrice, that.originalPrice)
                .append(purchasePrice, that.purchasePrice)
                .append(subtotal, that.subtotal)
                .append(name, that.name)
                .append(goodsImage, that.goodsImage)
                .append(templateScript, that.templateScript)
                .append(checked, that.checked)
                .append(isFreeFreight, that.isFreeFreight)
                .append(singleList, that.singleList)
                .append(groupList, that.groupList)
                .append(promotionTags, that.promotionTags)
                .append(notJoinPromotion, that.notJoinPromotion)
                .append(templateId, that.templateId)
                .append(specList, that.specList)
                .append(point, that.point)
                .append(snapshotId, that.snapshotId)
                .append(serviceStatus, that.serviceStatus)
                .append(lastModify, that.lastModify)
                .append(enableQuantity, that.enableQuantity)
                .append(invalid, that.invalid)
                .append(errorMessage, that.errorMessage)
                .append(isShip, that.isShip)
                .append(goodsType, that.goodsType)
                .append(giveGift, that.giveGift)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(sellerId)
                .append(sellerName)
                .append(goodsId)
                .append(skuId)
                .append(skuSn)
                .append(catId)
                .append(num)
                .append(purchaseNum)
                .append(goodsWeight)
                .append(originalPrice)
                .append(purchasePrice)
                .append(subtotal)
                .append(name)
                .append(goodsImage)
                .append(templateScript)
                .append(checked)
                .append(isFreeFreight)
                .append(singleList)
                .append(groupList)
                .append(promotionTags)
                .append(notJoinPromotion)
                .append(templateId)
                .append(specList)
                .append(point)
                .append(snapshotId)
                .append(serviceStatus)
                .append(lastModify)
                .append(enableQuantity)
                .append(invalid)
                .append(errorMessage)
                .append(isShip)
                .append(goodsType)
                .append(giveGift)
                .toHashCode();
    }
}
