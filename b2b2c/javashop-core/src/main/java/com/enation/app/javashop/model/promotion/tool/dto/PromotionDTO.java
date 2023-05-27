package com.enation.app.javashop.model.promotion.tool.dto;

import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * 活动DTO
 *
 * @author Snow create in 2018/7/9
 * @version v2.0
 * @since v7.0.0
 */
public class PromotionDTO {

    @ApiModelProperty(value="参与的活动ID")
    private Long actId;

    @ApiModelProperty(value="商品商品")
    private Long goodsId;

    @ApiModelProperty(value="购买的数量")
    private Integer num;

    public Long getActId() {
        return actId;
    }

    public void setActId(Long actId) {
        this.actId = actId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }

        if (o == null || getClass() != o.getClass()){
            return false;
        }

        PromotionDTO that = (PromotionDTO) o;

        return new EqualsBuilder()
                .append(actId, that.actId)
                .append(goodsId, that.goodsId)
                .append(num, that.num)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(actId)
                .append(goodsId)
                .append(num)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "PromotionDTO{" +
                "actId=" + actId +
                ", goodsId=" + goodsId +
                ", num=" + num +
                '}';
    }
}
