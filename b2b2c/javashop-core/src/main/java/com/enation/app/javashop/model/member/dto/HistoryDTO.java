package com.enation.app.javashop.model.member.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;


/**
 * 会员足迹DTO
 *
 * @author zh
 * @version v7.1.4
 * @since vv7.1
 * 2019-06-18 15:18:56
 */
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class HistoryDTO implements Serializable {

    public HistoryDTO() {

    }

    public HistoryDTO(Long goodsId, Long memberId) {
        this.goodsId = goodsId;
        this.memberId = memberId;
    }

    private static final long serialVersionUID = 5890914765373676945L;
    /**
     * 商品id
     */
    @ApiModelProperty(name = "goods_id", value = "商品id")
    private Long goodsId;
    /**
     * 会员id
     */
    @ApiModelProperty(name = "member_id", value = "商品名称", required = true)
    private Long memberId;

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    @Override
    public String toString() {
        return "HistoryDTO{" +
                "goodsId=" + goodsId +
                ", memberId=" + memberId +
                '}';
    }
}
