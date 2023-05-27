package com.enation.app.javashop.service.goods.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.enation.app.javashop.mapper.goods.GoodsMapper;
import com.enation.app.javashop.model.goods.dos.GoodsDO;
import com.enation.app.javashop.service.goods.StaticsPageGoodsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * StaticsPageGoodsManager
 *
 * @author Chopper
 * @version v1.0
 * @since v7.0
 * 2018-07-17 下午3:21
 */
@Service
public class StaticsPageGoodsManagerImpl implements StaticsPageGoodsManager {

    @Autowired
    private GoodsMapper goodsMapper;

    /**
     * 商品总数
     *
     * @return
     */
    @Override
    public Integer count() {

        return this.goodsMapper.selectCount(new QueryWrapper<>());
    }

    /**
     * 商品数据获取
     *
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public List goodsList(Long page, Long pageSize) {

        return this.goodsMapper.selectMaps(new QueryWrapper<GoodsDO>()
                .select("goods_id","goods_name")
                .orderByDesc("goods_id")
                .last("limit "+(page-1)*pageSize+","+pageSize));
    }
}
