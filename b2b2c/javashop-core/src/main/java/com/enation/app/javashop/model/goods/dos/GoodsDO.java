package com.enation.app.javashop.model.goods.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enation.app.javashop.model.base.CharacterConstant;
import com.enation.app.javashop.model.goods.dto.GoodsDTO;
import com.enation.app.javashop.model.goods.vo.GoodsMobileIntroVO;
import com.enation.app.javashop.framework.database.annotation.Column;
import com.enation.app.javashop.framework.database.annotation.Id;
import com.enation.app.javashop.framework.database.annotation.PrimaryKeyField;
import com.enation.app.javashop.framework.database.annotation.Table;
import com.enation.app.javashop.framework.util.JsonUtil;
import com.enation.app.javashop.framework.util.StringUtil;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * 商品实体
 *
 * @author fk
 * @version v2.0
 * @since v7.0.0 2018-03-21 11:23:10
 */
@TableName("es_goods")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class GoodsDO implements Serializable {

    private static final long serialVersionUID = 9115135430405642L;

    /**
     * 主键
     */
    @TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(hidden = true)
    private Long goodsId;
    /**
     * 商品名称
     */
    @ApiModelProperty(name = "goods_name", value = "商品名称", required = false)
    private String goodsName;
    /**
     * 商品编号
     */
    @ApiModelProperty(name = "sn", value = "商品编号", required = false)
    private String sn;
    /**
     * 品牌id
     */
    @ApiModelProperty(name = "brand_id", value = "品牌id", required = false)
    private Long brandId;
    /**
     * 分类id
     */
    @ApiModelProperty(name = "category_id", value = "分类id", required = false)
    private Long categoryId;
    /**
     * 商品类型normal普通point积分
     */
    @ApiModelProperty(name = "goods_type", value = "商品类型NORMAL普通POINT积分", required = false)
    private String goodsType;
    /**
     * 重量
     */
    @ApiModelProperty(name = "weight", value = "重量", required = false)
    private Double weight;
    /**
     * 上架状态 1上架 0下架
     */
    @ApiModelProperty(name = "market_enable", value = "上架状态 1上架  0下架", required = false)
    private Integer marketEnable;
    /**
     * 详情
     */
    @ApiModelProperty(name = "intro", value = "详情", required = false)
    private String intro;
    /**
     * 商品价格
     */
    @ApiModelProperty(name = "price", value = "商品价格", required = false)
    private Double price;
    /**
     * 成本价格
     */
    @ApiModelProperty(name = "cost", value = "成本价格", required = false)
    private Double cost;
    /**
     * 市场价格
     */
    @ApiModelProperty(name = "mktprice", value = "市场价格", required = false)
    private Double mktprice;
    /**
     * 是否有规格0没有 1有
     */
    @ApiModelProperty(name = "have_spec", value = "是否有规格0没有 1有", required = false)
    private Integer haveSpec;
    /**
     * 创建时间
     */
    @ApiModelProperty(name = "create_time", value = "创建时间", required = false)
    private Long createTime;
    /**
     * 最后修改时间
     */
    @ApiModelProperty(name = "last_modify", value = "最后修改时间", required = false)
    private Long lastModify;
    /**
     * 浏览数量
     */
    @ApiModelProperty(name = "view_count", value = "浏览数量", required = false)
    private Integer viewCount;
    /**
     * 购买数量
     */
    @ApiModelProperty(name = "buy_count", value = "购买数量", required = false)
    private Integer buyCount;
    /**
     * 是否被删除0 删除 1未删除
     */
    @ApiModelProperty(name = "disabled", value = "是否被删除0 删除 1未删除", required = false)
    private Integer disabled;
    /**
     * 库存
     */
    @ApiModelProperty(name = "quantity", value = "库存", required = false)
    private Integer quantity;
    /**
     * 可用库存
     */
    @ApiModelProperty(name = "enable_quantity", value = "可用库存", required = false)
    private Integer enableQuantity;
    /**
     * 如果是积分商品需要使用的积分
     */
    @ApiModelProperty(name = "point", value = "如果是积分商品需要使用的积分", required = false)
    @TableField("`point`")
    private Integer point;
    /**
     * seo标题
     */
    @ApiModelProperty(name = "page_title", value = "seo标题", required = false)
    private String pageTitle;
    /**
     * seo关键字
     */
    @ApiModelProperty(name = "meta_keywords", value = "seo关键字", required = false)
    private String metaKeywords;
    /**
     * seo描述
     */
    @ApiModelProperty(name = "meta_description", value = "seo描述", required = false)
    private String metaDescription;
    /**
     * 商品好评率
     */
    @ApiModelProperty(name = "grade", value = "商品好评率", required = false)
    private Double grade;
    /**
     * 缩略图路径
     */
    @ApiModelProperty(name = "thumbnail", value = "缩略图路径", required = false)
    private String thumbnail;
    /**
     * 大图路径
     */
    @ApiModelProperty(name = "big", value = "大图路径", required = false)
    private String big;
    /**
     * 小图路径
     */
    @ApiModelProperty(name = "small", value = "小图路径", required = false)
    private String small;
    /**
     * 原图路径
     */
    @ApiModelProperty(name = "original", value = "原图路径", required = false)
    private String original;
    /**
     * 卖家id
     */
    @ApiModelProperty(name = "seller_id", value = "卖家id", required = false)
    private Long sellerId;
    /**
     * 店铺分类id
     */
    @ApiModelProperty(name = "shop_cat_id", value = "店铺分类id", required = false)
    private Long shopCatId;
    /**
     * 评论数量
     */
    @ApiModelProperty(name = "comment_num", value = "评论数量", required = false)
    private Integer commentNum;
    /**
     * 运费模板id
     */
    @ApiModelProperty(name = "template_id", value = "运费模板id", required = false)
    private Long templateId;
    /**
     * 谁承担运费0：买家承担，1：卖家承担
     */
    @ApiModelProperty(name = "goods_transfee_charge", value = "谁承担运费0：买家承担，1：卖家承担", required = false)
    private Integer goodsTransfeeCharge;
    /**
     * 卖家名字
     */
    @ApiModelProperty(name = "seller_name", value = "卖家名字", required = false)
    private String sellerName;
    /**
     * 0 需要审核 并且待审核，1 不需要审核 2需要审核 且审核通过 3 需要审核 且审核未通过
     */
    @ApiModelProperty(name = "is_auth", value = "0 待审核，1 审核通过 2 未通过", required = false)
    private Integer isAuth;
    /**
     * 审核信息
     */
    @ApiModelProperty(name = "auth_message", value = "审核信息", required = false)
    private String authMessage;

    /**
     * 下架原因
     */
    @ApiModelProperty(name = "under_message", value = "下架原因", required = false)
    private String underMessage;

    @ApiModelProperty(name = "self_operated", value = "是否自营0否 1是", required = false)
    private Integer selfOperated;

    /**
     * 商品移动端详情
     */
    @ApiModelProperty(name = "mobile_intro", value = "商品移动端详情", required = false)
    private String mobileIntro;

    /**
     * 商品视频
     */
    @ApiModelProperty(name = "goods_video", value = "商品视频", required = false)
    private String goodsVideo;


    public GoodsDO() {
    }

    public GoodsDO(GoodsDTO goodsVO) {

        this.goodsId = goodsVO.getGoodsId();
        this.categoryId = goodsVO.getCategoryId();
        this.shopCatId = goodsVO.getShopCatId();
        this.brandId = goodsVO.getBrandId();
        this.goodsName = goodsVO.getGoodsName();
        this.sn = goodsVO.getSn();
        this.price = goodsVO.getPrice();
        this.cost = goodsVO.getCost();
        this.mktprice = goodsVO.getMktprice();
        this.weight = goodsVO.getWeight();
        this.goodsTransfeeCharge = goodsVO.getGoodsTransfeeCharge();
        this.intro = goodsVO.getIntro();
        this.haveSpec = goodsVO.getHaveSpec();
        this.templateId = goodsVO.getTemplateId();
        this.pageTitle = goodsVO.getPageTitle();
        this.metaKeywords = goodsVO.getMetaKeywords();
        this.metaDescription = goodsVO.getMetaDescription();
        this.marketEnable = goodsVO.getMarketEnable() != 1 ? 0 : 1;

        if (StringUtil.isEmpty(this.pageTitle)) {
            this.pageTitle = goodsVO.getGoodsName();
        }
        if (StringUtil.isEmpty(this.metaKeywords)) {
            this.metaKeywords = goodsVO.getGoodsName();
        }
        if (StringUtil.isEmpty(this.metaDescription)) {
            this.metaDescription = goodsVO.getGoodsName();
        }

        List<GoodsMobileIntroVO> introList = goodsVO.getIntroList();
        if (introList != null && introList.size() != 0) {
            this.mobileIntro = JsonUtil.objectToJson(introList);
        } else {
            this.mobileIntro = "";
        }

        this.goodsVideo = goodsVO.getGoodsVideo();
    }

    @PrimaryKeyField
    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(String goodsType) {
        this.goodsType = goodsType;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Integer getMarketEnable() {
        return marketEnable;
    }

    public void setMarketEnable(Integer marketEnable) {
        this.marketEnable = marketEnable;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Double getMktprice() {
        return mktprice;
    }

    public void setMktprice(Double mktprice) {
        this.mktprice = mktprice;
    }

    public Integer getHaveSpec() {
        return haveSpec;
    }

    public void setHaveSpec(Integer haveSpec) {
        this.haveSpec = haveSpec;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getLastModify() {
        return lastModify;
    }

    public void setLastModify(Long lastModify) {
        this.lastModify = lastModify;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getBuyCount() {
        return buyCount;
    }

    public void setBuyCount(Integer buyCount) {
        this.buyCount = buyCount;
    }

    public Integer getDisabled() {
        return disabled;
    }

    public void setDisabled(Integer disabled) {
        this.disabled = disabled;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getEnableQuantity() {
        return enableQuantity;
    }

    public void setEnableQuantity(Integer enableQuantity) {
        this.enableQuantity = enableQuantity;
    }

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getMetaKeywords() {
        return metaKeywords;
    }

    public void setMetaKeywords(String metaKeywords) {
        this.metaKeywords = metaKeywords;
    }

    public String getMetaDescription() {
        return metaDescription;
    }

    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }

    public Double getGrade() {
        return grade;
    }

    public void setGrade(Double grade) {
        this.grade = grade;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getBig() {
        return big;
    }

    public void setBig(String big) {
        this.big = big;
    }

    public String getSmall() {
        return small;
    }

    public void setSmall(String small) {
        this.small = small;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public Long getShopCatId() {
        return shopCatId;
    }

    public void setShopCatId(Long shopCatId) {
        this.shopCatId = shopCatId;
    }

    public Integer getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(Integer commentNum) {
        this.commentNum = commentNum;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Integer getGoodsTransfeeCharge() {
        return goodsTransfeeCharge;
    }

    public void setGoodsTransfeeCharge(Integer goodsTransfeeCharge) {
        this.goodsTransfeeCharge = goodsTransfeeCharge;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public Integer getIsAuth() {
        return isAuth;
    }

    public void setIsAuth(Integer isAuth) {
        this.isAuth = isAuth;
    }

    public String getAuthMessage() {
        return authMessage;
    }

    public void setAuthMessage(String authMessage) {
        this.authMessage = authMessage;
    }

    public Integer getSelfOperated() {
        return selfOperated;
    }

    public void setSelfOperated(Integer selfOperated) {
        this.selfOperated = selfOperated;
    }

    public String getUnderMessage() {
        return underMessage;
    }

    public void setUnderMessage(String underMessage) {
        this.underMessage = underMessage;
    }

    public String getMobileIntro() {
        return mobileIntro;
    }

    public void setMobileIntro(String mobileIntro) {
        this.mobileIntro = mobileIntro;
    }

    public String getGoodsVideo() {
        return goodsVideo;
    }

    public void setGoodsVideo(String goodsVideo) {
        this.goodsVideo = goodsVideo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GoodsDO goodsDO = (GoodsDO) o;
        return Objects.equals(goodsId, goodsDO.goodsId) &&
                Objects.equals(goodsName, goodsDO.goodsName) &&
                Objects.equals(sn, goodsDO.sn) &&
                Objects.equals(brandId, goodsDO.brandId) &&
                Objects.equals(categoryId, goodsDO.categoryId) &&
                Objects.equals(goodsType, goodsDO.goodsType) &&
                Objects.equals(weight, goodsDO.weight) &&
                Objects.equals(marketEnable, goodsDO.marketEnable) &&
                Objects.equals(intro, goodsDO.intro) &&
                Objects.equals(price, goodsDO.price) &&
                Objects.equals(cost, goodsDO.cost) &&
                Objects.equals(mktprice, goodsDO.mktprice) &&
                Objects.equals(haveSpec, goodsDO.haveSpec) &&
                Objects.equals(createTime, goodsDO.createTime) &&
                Objects.equals(lastModify, goodsDO.lastModify) &&
                Objects.equals(viewCount, goodsDO.viewCount) &&
                Objects.equals(buyCount, goodsDO.buyCount) &&
                Objects.equals(disabled, goodsDO.disabled) &&
                Objects.equals(quantity, goodsDO.quantity) &&
                Objects.equals(enableQuantity, goodsDO.enableQuantity) &&
                Objects.equals(point, goodsDO.point) &&
                Objects.equals(pageTitle, goodsDO.pageTitle) &&
                Objects.equals(metaKeywords, goodsDO.metaKeywords) &&
                Objects.equals(metaDescription, goodsDO.metaDescription) &&
                Objects.equals(grade, goodsDO.grade) &&
                Objects.equals(thumbnail, goodsDO.thumbnail) &&
                Objects.equals(big, goodsDO.big) &&
                Objects.equals(small, goodsDO.small) &&
                Objects.equals(original, goodsDO.original) &&
                Objects.equals(sellerId, goodsDO.sellerId) &&
                Objects.equals(shopCatId, goodsDO.shopCatId) &&
                Objects.equals(commentNum, goodsDO.commentNum) &&
                Objects.equals(templateId, goodsDO.templateId) &&
                Objects.equals(goodsTransfeeCharge, goodsDO.goodsTransfeeCharge) &&
                Objects.equals(sellerName, goodsDO.sellerName) &&
                Objects.equals(isAuth, goodsDO.isAuth) &&
                Objects.equals(authMessage, goodsDO.authMessage) &&
                Objects.equals(underMessage, goodsDO.underMessage) &&
                Objects.equals(selfOperated, goodsDO.selfOperated) &&
                Objects.equals(mobileIntro, goodsDO.mobileIntro) &&
                Objects.equals(goodsVideo, goodsDO.goodsVideo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(goodsId, goodsName, sn, brandId, categoryId, goodsType, weight, marketEnable, intro, price, cost, mktprice, haveSpec, createTime, lastModify, viewCount, buyCount, disabled, quantity, enableQuantity, point, pageTitle, metaKeywords, metaDescription, grade, thumbnail, big, small, original, sellerId, shopCatId, commentNum, templateId, goodsTransfeeCharge, sellerName, isAuth, authMessage, underMessage, selfOperated, mobileIntro, goodsVideo);
    }

    @Override
    public String toString() {
        return "GoodsDO{" +
                "goodsId=" + goodsId +
                ", goodsName='" + goodsName + '\'' +
                ", sn='" + sn + '\'' +
                ", brandId=" + brandId +
                ", categoryId=" + categoryId +
                ", goodsType='" + goodsType + '\'' +
                ", weight=" + weight +
                ", marketEnable=" + marketEnable +
                ", intro='" + intro + '\'' +
                ", price=" + price +
                ", cost=" + cost +
                ", mktprice=" + mktprice +
                ", haveSpec=" + haveSpec +
                ", createTime=" + createTime +
                ", lastModify=" + lastModify +
                ", viewCount=" + viewCount +
                ", buyCount=" + buyCount +
                ", disabled=" + disabled +
                ", quantity=" + quantity +
                ", enableQuantity=" + enableQuantity +
                ", point=" + point +
                ", pageTitle='" + pageTitle + '\'' +
                ", metaKeywords='" + metaKeywords + '\'' +
                ", metaDescription='" + metaDescription + '\'' +
                ", grade=" + grade +
                ", thumbnail='" + thumbnail + '\'' +
                ", big='" + big + '\'' +
                ", small='" + small + '\'' +
                ", original='" + original + '\'' +
                ", sellerId=" + sellerId +
                ", shopCatId=" + shopCatId +
                ", commentNum=" + commentNum +
                ", templateId=" + templateId +
                ", goodsTransfeeCharge=" + goodsTransfeeCharge +
                ", sellerName='" + sellerName + '\'' +
                ", isAuth=" + isAuth +
                ", authMessage='" + authMessage + '\'' +
                ", underMessage='" + underMessage + '\'' +
                ", selfOperated=" + selfOperated +
                ", mobileIntro='" + mobileIntro + '\'' +
                ", goodsVideo='" + goodsVideo + '\'' +
                '}';
    }
}
