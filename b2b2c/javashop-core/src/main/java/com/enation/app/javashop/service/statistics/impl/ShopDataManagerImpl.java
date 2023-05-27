package com.enation.app.javashop.service.statistics.impl;

import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.enation.app.javashop.mapper.statistics.ShopDataMapper;
import com.enation.app.javashop.model.statistics.dto.ShopData;
import com.enation.app.javashop.service.statistics.ShopDataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * ShopDataManagerImpl
 *
 * @author Chopper
 * @version v1.0
 * @since v7.0
 * 2018-07-24 上午6:05
 */
@Service
public class ShopDataManagerImpl implements ShopDataManager {

    @Autowired
    private ShopDataMapper shopDataMapper;

    /**
     * 修改店铺收藏数量
     *
     * @param shopData
     */
    @Override
    public void updateCollection(ShopData shopData) {
        //根据商家id修改收藏数量
        new UpdateChainWrapper<>(shopDataMapper)
                //设置收藏数量
                .set("favorite_num", shopData.getFavoriteNum())
                //按商家id修改
                .eq("seller_id", shopData.getSellerId())
                //提交修改
                .update();
    }

    @Override
    public void updateShopData(ShopData shopData) {
        //按商家id修改店铺统计数据
        new UpdateChainWrapper<>(shopDataMapper)
                //按商家id修改
                .eq("seller_id", shopData.getSellerId())
                //提交修改
                .update(shopData);
    }

    /**
     * 添加店铺数据
     *
     * @param shopData
     */
    @Override
    public void add(ShopData shopData) {
        shopDataMapper.insert(shopData);
    }

    /**
     * 获取商品数据
     *
     * @param shopId
     * @return
     */
    @Override
    public ShopData get(Long shopId) {
        //根据商家id查询商家统计数据
        return new QueryChainWrapper<>(shopDataMapper).eq("seller_id", shopId).one();
    }
}
