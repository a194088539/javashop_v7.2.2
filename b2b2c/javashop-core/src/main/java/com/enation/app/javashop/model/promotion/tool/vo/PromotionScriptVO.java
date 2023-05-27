package com.enation.app.javashop.model.promotion.tool.vo;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * @description: 促销脚本VO
 * @author: liuyulei
 * @create: 2020-01-09 09:43
 * @version:1.0
 * @since:7.1.5
 **/
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PromotionScriptVO implements Serializable {
    private static final long serialVersionUID = 3566902764098210013L;

    @ApiModelProperty(value = "促销活动id")
    private Long promotionId;

    @ApiModelProperty(value = "促销活动名称")
    private String promotionName;

    @ApiModelProperty(value = "促销活动类型")
    private String promotionType;

    @ApiModelProperty(value = "是否可以被分组")
    private Boolean isGrouped;

    @ApiModelProperty(value = "促销脚本",hidden = true)
    private String promotionScript;

    @ApiModelProperty(value = "商品skuID")
    private Long skuId;


    public Long getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Long promotionId) {
        this.promotionId = promotionId;
    }

    public String getPromotionName() {
        return promotionName;
    }

    public void setPromotionName(String promotionName) {
        this.promotionName = promotionName;
    }

    public String getPromotionType() {
        return promotionType;
    }

    public void setPromotionType(String promotionType) {
        this.promotionType = promotionType;
    }

    public Boolean getIsGrouped() {
        return isGrouped;
    }

    public void setIsGrouped(Boolean grouped) {
        isGrouped = grouped;
    }

    public String getPromotionScript() {
        return promotionScript;
    }

    public void setPromotionScript(String promotionScript) {
        this.promotionScript = promotionScript;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PromotionScriptVO that = (PromotionScriptVO) o;

        return new EqualsBuilder()
                .append(promotionId, that.promotionId)
                .append(promotionName, that.promotionName)
                .append(promotionType, that.promotionType)
                .append(isGrouped, that.isGrouped)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(promotionId)
                .append(promotionName)
                .append(promotionType)
                .append(isGrouped)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "PromotionScriptVO{" +
                "promotionId=" + promotionId +
                ", promotionName='" + promotionName + '\'' +
                ", promotionType='" + promotionType + '\'' +
                ", isGrouped=" + isGrouped +
                ", promotionScript='" + promotionScript + '\'' +
                ", skuId=" + skuId +
                '}';
    }
}
