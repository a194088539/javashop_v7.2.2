package com.enation.app.javashop.client.goods.impl;

import com.enation.app.javashop.client.goods.GoodsQuantityClient;
import com.enation.app.javashop.model.goods.vo.GoodsQuantityVO;
import com.enation.app.javashop.service.goods.GoodsQuantityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 库存操作实现
 *
 * @author zh
 * @version v7.0
 * @date 18/9/20 下午7:33
 * @since v7.0
 */
@Service
@ConditionalOnProperty(value = "javashop.product", havingValue = "stand")
public class GoodsQuantityClientDefaultImpl implements GoodsQuantityClient {

    @Autowired
    private GoodsQuantityManager goodsQuantityManager;


    @Override
    public boolean updateSkuQuantity( List<GoodsQuantityVO> goodsQuantityList) {

        return goodsQuantityManager.updateSkuQuantity(goodsQuantityList);
    }

}
