package com.enation.app.javashop.model.goods.vo;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;

/**
 * 商品sku变化
 *
 * @author fk
 * @version v2.0
 * @since v7.2.0
 * 2020年2月7日 上午11:50:42
 */
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class GoodsSkuChangeVO {

    private Long goodsId;

    private Long skuId;

    private Integer status;

    /**
     * 修改
     */
    public final static int UPDATE_OPERATION = 2;

    /**
     * 删除
     */
    public final static int DEL_OPERATION = 3;

    public GoodsSkuChangeVO(Long goodsId, Long skuId, Integer status) {
        this.goodsId = goodsId;
        this.skuId = skuId;
        this.status = status;
    }

    public GoodsSkuChangeVO() {
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
