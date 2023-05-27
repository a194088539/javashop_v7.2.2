package com.enation.app.javashop.client.member;

/**
 * 会员收藏店铺client
 *
 * @author fk
 * @version v.2.0
 * @date 20/2/23 下午4:42
 * @since v7.2.0
 */
public interface MemberCollectionShopClient {

    /**
     * 修改某店铺商品店铺名称
     *
     * @param sellerId
     * @param sellerName
     */
    void changeSellerName(Long sellerId, String sellerName);





}
