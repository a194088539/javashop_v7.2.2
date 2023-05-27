package com.enation.app.javashop.client.member;

/**
 * (这里用一句话描述这个类的作用)
 *
 * @author zh
 * @version v7.0
 * @date 18/7/27 下午4:42
 * @since v7.0
 */

public interface MemberCollectionGoodsClient {


    /**
     * 某商品收藏数量
     *
     * @param goodsId
     * @return
     */
    Integer getGoodsCollectCount(Long goodsId);


    /**
     * 更新收藏的商品名称
     * @param goodsId
     * @param goodsName
     */
    void updateGoodsName(Long goodsId,String goodsName);

}
