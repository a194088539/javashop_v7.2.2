package com.enation.app.javashop.model.promotion.tool.vo;

import com.enation.app.javashop.model.promotion.fulldiscount.vo.FullDiscountVO;
import com.enation.app.javashop.framework.database.annotation.Column;

/**
 * Created by kingapex on 2018/12/18.
 *
 * @author kingapex
 * @version 1.0
 * @since 7.0.0
 * 2018/12/18
 */
public class FullDiscountWithGoodsId extends FullDiscountVO {
    public FullDiscountWithGoodsId() {
    }

    @Column(name = "goods_id")
    private long goodsId;

    public long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }
}
