package com.enation.app.javashop.client.member;

import com.enation.app.javashop.model.shop.dos.ShopDO;
import com.enation.app.javashop.model.shop.dto.ShopBankDTO;
import com.enation.app.javashop.model.shop.vo.ShopVO;

import java.util.List;
import java.util.Map;

/**
 * @author zjp
 * @version v7.0
 * @Description 店铺Client默认实现
 * @ClassName ShopClient
 * @since v7.0 下午9:26 2018/7/12
 */
public interface ShopClient {

    /**
     * 获取店铺详细
     * @param shopId 店铺id
     * @return ShopVO  店铺详细
     */
    ShopVO getShop(Long shopId);

    /**
     * 获取所有店铺的银行信息佣金比例
     * @return
     */
    List<ShopBankDTO> listShopBankInfo();

    /**
     * 增加收藏数量
     * @param shopId 店铺id
     */
    void addCollectNum(Long shopId);
    /**
     * 减少收藏数量
     * @param shopId
     */
    void reduceCollectNum(Long shopId);

    /**
     * 计算店铺评分
     */
    void calculateShopScore();

    /**
     * 更新店铺的商品数量
     * @param sellerId
     * @param sellerGoodsCount
     */
    void updateShopGoodsNum(Long sellerId, Integer sellerGoodsCount);


    /**
     * 获取店铺总数
     * @return
     */
    int queryShopCount();

    /**
     * 根据范围查询店铺信息
     * @param i
     * @param pageSize
     * @return
     */
    List<ShopDO> queryShopByRange(Long i, Long pageSize);
}
