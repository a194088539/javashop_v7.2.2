package com.enation.app.javashop.service.member;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.member.dos.MemberCollectionShop;

/**
 * 会员收藏店铺表业务层
 *
 * @author zh
 * @version v7.0.0
 * @since v7.0.0
 * 2018-03-30 20:34:23
 */
public interface MemberCollectionShopManager {

    /**
     * 查询会员收藏店铺表列表
     *
     * @param page     页码
     * @param pageSize 每页数量
     * @return WebPage
     */
    WebPage list(long page, long pageSize);

    /**
     * 添加会员收藏店铺
     *
     * @param memberCollectionShop 收藏店铺对象
     * @return
     */
    MemberCollectionShop add(MemberCollectionShop memberCollectionShop);

    /**
     * 删除会员收藏店铺
     *
     * @param shopId 要删除收藏店铺的店铺id
     */
    void delete(Long shopId);

    /**
     * 获取会员收藏店铺
     *
     * @param id 会员收藏店铺表主键
     * @return MemberCollectionShop  会员收藏店铺表
     */
    MemberCollectionShop getModel(Long id);

    /**
     * 获取会员收藏店铺
     *
     * @param id 店铺id
     * @return MemberCollectionShop  会员收藏店铺表
     */
    boolean isCollection(Long id);

    /**
     * 获取会员收藏店铺数
     *
     * @return 收藏店铺数
     */
    Integer getMemberCollectCount();


    /**
     * 获取店铺有多少收藏量
     * @param sellerId 商家ID
     * @return 收藏量
     */
    Integer getCollectionBySeller(Long sellerId);

    /**
     * 修改某店铺商品店铺名称
     * @param sellerId 商家ID
     * @param sellerName 商家名称
     */
    void changeSellerName(Long sellerId, String sellerName);
}
