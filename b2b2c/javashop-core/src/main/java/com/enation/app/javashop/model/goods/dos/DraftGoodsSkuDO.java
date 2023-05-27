package com.enation.app.javashop.model.goods.dos;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enation.app.javashop.model.goods.vo.GoodsSkuVO;
import com.enation.app.javashop.framework.database.annotation.Column;
import com.enation.app.javashop.framework.database.annotation.Id;
import com.enation.app.javashop.framework.database.annotation.PrimaryKeyField;
import com.enation.app.javashop.framework.database.annotation.Table;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
 * 草稿商品sku实体
 *
 * @author fk
 * @version v2.0
 * @since v7.0.0
 * 2018-03-26 11:38:06
 */
@TableName("es_draft_goods_sku")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DraftGoodsSkuDO implements Serializable {

    private static final long serialVersionUID = 5684194304207265L;

    /**
     * 主键ID
     */
    @TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(hidden = true)
    private Long draftSkuId;
    /**
     * 草稿id
     */
    @ApiModelProperty(name = "draft_goods_id", value = "草稿id", required = false)
    private Long draftGoodsId;
    /**
     * 货号
     */
    @ApiModelProperty(name = "sn", value = "货号", required = false)
    private String sn;
    /**
     * 总库存
     */
    @ApiModelProperty(name = "quantity", value = "总库存", required = false)
    private Integer quantity;
    /**
     * 价格
     */
    @ApiModelProperty(name = "price", value = "价格", required = false)
    private Double price;
    /**
     * 规格
     */
    @ApiModelProperty(name = "specs", value = "规格", required = false)
    private String specs;
    /**
     * 成本
     */
    @ApiModelProperty(name = "cost", value = "成本", required = false)
    private Double cost;
    /**
     * 重量
     */
    @ApiModelProperty(name = "weight", value = "重量", required = false)
    private Double weight;

    public DraftGoodsSkuDO() {
    }

    public DraftGoodsSkuDO(GoodsSkuVO skuVO) {
        this.setDraftGoodsId(skuVO.getGoodsId());
        this.setSn(skuVO.getSn());
        this.setPrice(skuVO.getPrice());
        this.setCost(skuVO.getCost());
        this.setWeight(skuVO.getWeight());
        this.setQuantity(skuVO.getQuantity());
        this.setSpecs(skuVO.getSpecs());
    }

    @PrimaryKeyField
    public Long getDraftSkuId() {
        return draftSkuId;
    }

    public void setDraftSkuId(Long draftSkuId) {
        this.draftSkuId = draftSkuId;
    }

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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getSpecs() {
        return specs;
    }

    public void setSpecs(String specs) {
        this.specs = specs;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "DraftGoodsSkuDO [draftSkuId=" + draftSkuId + ", draftGoodsId=" + draftGoodsId + ", sn=" + sn
                + ", quantity=" + quantity + ", price=" + price + ", specs=" + specs + ", cost=" + cost + ", weight="
                + weight + "]";
    }


}
