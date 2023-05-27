package com.enation.app.javashop.model.trade.cart.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * 活动的Vo
 * @author Snow
 * @since v6.4
 * @version v1.0
 * 2017年08月24日14:48:36
 */
@ApiModel(description = "购物车中活动Vo")
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CartPromotionVo implements Serializable {


	private static final long serialVersionUID = 1867982008597357312L;


	@ApiModelProperty(value = "货品id")
	private Long skuId;

	@ApiModelProperty(value = "活动id")
	private Long promotionId;

	@ApiModelProperty(value = "活动工具类型")
	private String promotionType;

	@ApiModelProperty(value = "活动名称")
	private String promotionName;

	@ApiModelProperty(value = "是否选中参与这个活动,1为是 0为否")
	private Integer isCheck;

    @ApiModelProperty(value = "促销脚本",hidden = true)
	@JsonIgnore
    private String promotionScript;



	public Long getPromotionId() {
		return promotionId;
	}

	public void setPromotionId(Long promotionId) {
		this.promotionId = promotionId;
	}

	public String getPromotionType() {
		return promotionType;
	}

	public void setPromotionType(String promotionType) {
		this.promotionType = promotionType;
	}

	public String getPromotionName() {
		return promotionName;
	}

	public void setPromotionName(String promotionName) {
		this.promotionName = promotionName;
	}

	public Integer getIsCheck() {
		isCheck = isCheck == null ? 0 : isCheck;
		return isCheck;
	}

	public void setIsCheck(Integer isCheck) {
		this.isCheck = isCheck;
	}

	public Long getSkuId() {
		return skuId;
	}

	public void setSkuId(Long skuId) {
		this.skuId = skuId;
	}

    public String getPromotionScript() {
        return promotionScript;
    }

    public void setPromotionScript(String promotionScript) {
        this.promotionScript = promotionScript;
    }

    @Override
    public String toString() {
        return "CartPromotionVo{" +
                "skuId=" + skuId +
                ", promotionId=" + promotionId +
                ", promotionType='" + promotionType + '\'' +
                ", promotionName='" + promotionName + '\'' +
                ", isCheck=" + isCheck +
                ", promotionScript='" + promotionScript + '\'' +
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
        CartPromotionVo that = (CartPromotionVo) o;

        return new EqualsBuilder()
                .append(skuId, that.skuId)
                .append(promotionId, that.promotionId)
                .append(promotionType, that.promotionType)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(skuId)
                .append(promotionId)
                .append(promotionType)
                .append(promotionName)
                .append(isCheck)
                .append(promotionScript)
                .toHashCode();
    }
}
