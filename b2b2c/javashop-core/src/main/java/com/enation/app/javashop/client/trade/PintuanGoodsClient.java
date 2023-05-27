package com.enation.app.javashop.client.trade;

import java.util.List;

/**
 * @author zh
 * @version v1.0
 * @Description: 拼团索引
 * @date 2019/3/5 10:33
 * @since v7.0.0
 */
public interface PintuanGoodsClient {

    /**
     * 生成拼团索引
     *
     * @param promotionId 拼团活动ID
     * @return 生成结果
     */
    boolean createGoodsIndex(Long promotionId);


    /**
     * 删除某个商品的所有sku索引
     * @param goodsId
     */
    void deleteIndexByGoodsId(Long goodsId);

    /**
     * 同步一个商品的所有sku的索引
     * @param goodsId
     */
    void syncIndexByGoodsId(Long goodsId);

    /**
     * 删除拼团商品
     * @param goodsId
     */
    void delete(Long goodsId);

    /**
     * 删除拼团商品
     * @param delSkuIds
     */
    void deletePinTuanGoods(List<Long> delSkuIds);




}
