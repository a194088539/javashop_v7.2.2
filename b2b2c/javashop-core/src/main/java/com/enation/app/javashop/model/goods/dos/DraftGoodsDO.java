package com.enation.app.javashop.model.goods.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enation.app.javashop.model.goods.dto.GoodsDTO;
import com.enation.app.javashop.model.goods.enums.GoodsType;
import com.enation.app.javashop.model.goods.vo.GoodsMobileIntroVO;
import com.enation.app.javashop.framework.database.annotation.Column;
import com.enation.app.javashop.framework.database.annotation.Id;
import com.enation.app.javashop.framework.database.annotation.PrimaryKeyField;
import com.enation.app.javashop.framework.database.annotation.Table;
import com.enation.app.javashop.framework.util.JsonUtil;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jodd.util.StringUtil;

import java.io.Serializable;
import java.util.List;

/**
 * 草稿商品实体
 *
 * @author fk
 * @version v2.0
 * @since v7.0.0 2018-03-26 11:01:27
 */
@TableName("es_draft_goods")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DraftGoodsDO implements Serializable {

    private static final long serialVersionUID = 7646662730625878L;

    /**
     * 草稿商品id
     */
    @TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(hidden = true)
    private Long draftGoodsId;
    /**
     * 商品编号
     */
    @ApiModelProperty(name = "sn", value = "商品编号", required = false)
    private String sn;
    /**
     * 商品名称
     */
    @ApiModelProperty(name = "goods_name", value = "商品名称", required = false)
    private String goodsName;
    /**
     * 商品品牌ID
     */
    @ApiModelProperty(name = "brand_id", value = "商品品牌ID", required = false)
    private Long brandId;
    /**
     * 商品分类ID
     */
    @ApiModelProperty(name = "category_id", value = "商品分类ID", required = false)
    private Long categoryId;
    /**
     * 商品重量
     */
    @ApiModelProperty(name = "weight", value = "商品重量", required = false)
    private Double weight;
    /**
     * 商品详情
     */
    @ApiModelProperty(name = "intro", value = "商品详情", required = false)
    private String intro;
    /**
     * 商品价格
     */
    @ApiModelProperty(name = "price", value = "商品价格", required = false)
    private Double price;
    /**
     * 商品成本价
     */
    @ApiModelProperty(name = "cost", value = "商品成本价", required = false)
    private Double cost;
    /**
     * 商品市场价
     */
    @ApiModelProperty(name = "mktprice", value = "商品市场价", required = false)
    private Double mktprice;
    /**
     * 商品类型
     */
    @ApiModelProperty(name = "goods_type", value = "商品类型", required = false)
    private String goodsType;
    /**
     * 是否开启规格
     */
    @ApiModelProperty(name = "have_spec", value = "是否开启规格", required = false)
    private Integer haveSpec;
    /**
     * 商品添加时间
     */
    @ApiModelProperty(name = "create_time", value = "商品添加时间", required = false)
    private Long createTime;
    /**
     * 商品总库存
     */
    @ApiModelProperty(name = "quantity", value = "商品总库存", required = false)
    private Integer quantity;
    /**
     * 商品原始图片
     */
    @ApiModelProperty(name = "original", value = "商品原始图片", required = false)
    private String original;
    /**
     * 商品所属卖家ID
     */
    @ApiModelProperty(name = "seller_id", value = "商品所属卖家ID", required = false)
    private Long sellerId;
    /**
     * 商品所属店铺类目ID
     */
    @ApiModelProperty(name = "shop_cat_id", value = "商品所属店铺类目ID", required = false)
    private Long shopCatId;
    /**
     * 商品运费模板ID
     */
    @ApiModelProperty(name = "template_id", value = "商品运费模板ID", required = false)
    private Long templateId;
    /**
     * 是否为买家承担运费
     */
    @ApiModelProperty(name = "goods_transfee_charge", value = "是否为买家承担运费", required = false)
    private Integer goodsTransfeeCharge;
    /**
     * 商品所属店铺名称
     */
    @ApiModelProperty(name = "seller_name", value = "商品所属店铺名称", required = false)
    private String sellerName;
    /**
     * seo 标题
     */
    @ApiModelProperty(name = "page_title", value = "seo 标题", required = false)
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
     * 积分商品需要的金额
     */
    @ApiModelProperty(name = "exchange_money", value = "积分商品需要的金额", required = false)
    private Double exchangeMoney;
    /**
     * 积分商品需要的积分
     */
    @ApiModelProperty(name = "exchange_point", value = "积分商品需要的积分", required = false)
    private Integer exchangePoint;
    /**
     * 积分商品的分类id
     */
    @ApiModelProperty(name = "exchange_category_id", value = "积分商品的分类id", required = false)
    private Long exchangeCategoryId;

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

    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    private List<String> galleryList;

    public DraftGoodsDO() {
    }

    public DraftGoodsDO(GoodsDTO goodsVO) {

        this.draftGoodsId = goodsVO.getGoodsId();
        this.categoryId = goodsVO.getCategoryId();
        this.shopCatId = goodsVO.getShopCatId();
        this.brandId = goodsVO.getBrandId();
        this.goodsName = goodsVO.getGoodsName();
        this.sn = goodsVO.getSn();
        this.price = goodsVO.getPrice() == null ? 0 : goodsVO.getPrice();
        this.cost = goodsVO.getCost() == null ? 0 : goodsVO.getCost();
        this.mktprice = goodsVO.getMktprice() == null ? 0 : goodsVO.getMktprice();
        this.weight = goodsVO.getWeight() == null ? 0 : goodsVO.getWeight();
        this.goodsTransfeeCharge = goodsVO.getGoodsTransfeeCharge();
        this.intro = goodsVO.getIntro();
        this.haveSpec = goodsVO.getHaveSpec();
        if (goodsVO.getExchange() != null && goodsVO.getExchange().getEnableExchange() == 1) {
            this.goodsType = GoodsType.POINT.name();
            this.exchangeMoney = goodsVO.getExchange().getExchangeMoney();
            this.exchangePoint = goodsVO.getExchange().getExchangePoint();
            this.exchangeCategoryId = goodsVO.getExchange().getCategoryId();
        } else {
            this.goodsType = GoodsType.NORMAL.name();
            this.exchangeMoney = 0.00;
            this.exchangePoint = 0;
            this.exchangeCategoryId = 1L;
        }
        this.pageTitle = goodsVO.getPageTitle();
        this.metaKeywords = goodsVO.getMetaKeywords();
        this.metaDescription = goodsVO.getMetaDescription();
        this.templateId = goodsVO.getTemplateId();

        List<GoodsMobileIntroVO> introList = goodsVO.getIntroList();
        if (introList != null && introList.size() != 0) {
            this.mobileIntro = JsonUtil.objectToJson(introList);
        }

        this.goodsVideo = goodsVO.getGoodsVideo();
    }

    @PrimaryKeyField
    public Long getDraftGoodsId() {
        return draftGoodsId;
    }

    public void setDraftGoodsId(Long draftGoodsId) {
        this.draftGoodsId = draftGoodsId;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
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

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
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

    public String getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(String goodsType) {
        this.goodsType = goodsType;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
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

    public Long getExchangeCategoryId() {
        return exchangeCategoryId;
    }

    public void setExchangeCategoryId(Long exchangeCategoryId) {
        this.exchangeCategoryId = exchangeCategoryId;
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

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public List<String> getGalleryList() {

        if (!StringUtil.isEmpty(this.getOriginal())) {
            return JsonUtil.jsonToList(this.getOriginal(), String.class);
        }
        return galleryList;
    }

    public void setGalleryList(List<String> galleryList) {
        this.galleryList = galleryList;
    }

    @Override
    public String toString() {
        return "DraftGoodsDO{" +
                "draftGoodsId=" + draftGoodsId +
                ", sn='" + sn + '\'' +
                ", goodsName='" + goodsName + '\'' +
                ", brandId=" + brandId +
                ", categoryId=" + categoryId +
                ", weight=" + weight +
                ", intro='" + intro + '\'' +
                ", price=" + price +
                ", cost=" + cost +
                ", mktprice=" + mktprice +
                ", goodsType='" + goodsType + '\'' +
                ", haveSpec=" + haveSpec +
                ", createTime=" + createTime +
                ", quantity=" + quantity +
                ", original='" + original + '\'' +
                ", sellerId=" + sellerId +
                ", shopCatId=" + shopCatId +
                ", templateId=" + templateId +
                ", goodsTransfeeCharge=" + goodsTransfeeCharge +
                ", sellerName='" + sellerName + '\'' +
                ", pageTitle='" + pageTitle + '\'' +
                ", metaKeywords='" + metaKeywords + '\'' +
                ", metaDescription='" + metaDescription + '\'' +
                ", exchangeMoney=" + exchangeMoney +
                ", exchangePoint=" + exchangePoint +
                ", exchangeCategoryId=" + exchangeCategoryId +
                ", mobileIntro='" + mobileIntro + '\'' +
                ", goodsVideo='" + goodsVideo + '\'' +
                ", galleryList=" + galleryList +
                '}';
    }
}
