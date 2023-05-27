package com.enation.app.javashop.client.trade.impl;

import com.enation.app.javashop.client.trade.PintuanGoodsClient;
import com.enation.app.javashop.service.trade.pintuan.PinTuanSearchManager;
import com.enation.app.javashop.service.trade.pintuan.PintuanGoodsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 拼团默认实现类
 *
 * @author zh
 * @version v7.0
 * @date 19/3/5 下午2:22
 * @since v7.0
 */
@Service
@ConditionalOnProperty(value = "javashop.product", havingValue = "stand")
public class PintuanGoodsClientImpl implements PintuanGoodsClient {

    @Autowired
    private PintuanGoodsManager pintuanGoodsManager;

    @Autowired
    private PinTuanSearchManager pinTuanSearchManager;

    @Override
    public boolean createGoodsIndex(Long promotionId) {
        return  pintuanGoodsManager.addIndex(promotionId);
    }

    @Override
    public void deleteIndexByGoodsId(Long goodsId) {
        pinTuanSearchManager.deleteByGoodsId(goodsId);
    }

    @Override
    public void syncIndexByGoodsId(Long goodsId) {
        pinTuanSearchManager.syncIndexByGoodsId(goodsId);
    }

    @Override
    public void delete(Long goodsId) {
        this.pintuanGoodsManager.delete(goodsId);
    }

    @Override
    public void deletePinTuanGoods(List<Long> delSkuIds) {
        pintuanGoodsManager.deletePinTuanGoods(delSkuIds);
    }
}
