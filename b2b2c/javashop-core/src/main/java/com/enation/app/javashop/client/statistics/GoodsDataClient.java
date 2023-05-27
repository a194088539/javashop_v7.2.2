package com.enation.app.javashop.client.statistics;

import com.enation.app.javashop.model.statistics.dto.GoodsData;

/**
 * 商品收集manager
 *
 * @author chopper
 * @version v1.0
 * @Description:
 * @since v7.0
 * 2018/5/8 下午4:11
 */
public interface GoodsDataClient {
    /**
     * 新增商品
     *
     * @param goodsIds 商品id
     */
    void addGoods(Long[] goodsIds);

    /**
     * 修改商品
     *
     * @param goodsIds 商品id
     */
    void updateGoods(Long[] goodsIds);

    /**
     * 删除商品
     *
     * @param goodsIds 商品id
     */
    void deleteGoods(Long[] goodsIds);

    /**
     * 修改商品收藏数量
     * @param goodsData
     */
    void updateCollection(GoodsData goodsData);

    /**
     * 下架所有商品
     * @param sellerId
     */
    void underAllGoods(Long sellerId);


}
