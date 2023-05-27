package com.enation.app.javashop.model.member.vo;

/**
 * @author fk
 * @version v1.0
 * @Description: 商品好平率
 * @date 2018/5/4 10:45
 * @since v7.0.0
 */
public class GoodsGrade {

    private Long goodsId;

    private Double goodRate;

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Double getGoodRate() {
        return goodRate;
    }

    public void setGoodRate(Double goodRate) {
        this.goodRate = goodRate;
    }
}
