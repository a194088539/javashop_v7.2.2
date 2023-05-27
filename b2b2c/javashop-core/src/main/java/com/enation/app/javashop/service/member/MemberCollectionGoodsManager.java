package com.enation.app.javashop.service.member;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.member.dos.MemberCollectionGoods;

/**
 * 会员商品收藏表业务层
 *
 * @author zh
 * @version v7.0.0
 * @since v7.0.0
 * 2018-04-02 10:13:41
 */
public interface MemberCollectionGoodsManager {

    /**
     * 查询会员商品收藏表列表
     *
     * @param page     页码
     * @param pageSize 每页数量
     * @return WebPage
     */
    WebPage list(long page, long pageSize);

    /**
     * 添加会员商品收藏表
     *
     * @param memberCollectionGoods 收藏商品对象
     * @return
     */
    MemberCollectionGoods add(MemberCollectionGoods memberCollectionGoods);

    /**
     * 删除会员商品收藏
     *
     * @param goodsId 商品id
     */
    void delete(Long goodsId);

    /**
     * 获取会员商品收藏
     *
     * @param id 会员商品收藏表主键
     * @return MemberCollectionGoods  会员商品收藏表
     */
    MemberCollectionGoods getModel(Long id);

    /**
     * 获取会员收藏商品数
     *
     * @return 收藏商品数
     */
    Integer getMemberCollectCount();

    /**
     * 某商品收藏数量
     *
     * @param goodsId 商品ID
     * @return
     */
    Integer getGoodsCollectCount(Long goodsId);

    /**
     * 是否收藏某商品
     *
     * @param id 商品id
     * @return
     */
    boolean isCollection(Long id);

    /**
     * 更新收藏的商品名称
     * @param goodsId 商品ID
     * @param goodsName 商品名称
     */
    void updateGoodsName(Long goodsId, String goodsName);
}
