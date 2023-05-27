package com.enation.app.javashop.client.goods.impl;

import com.enation.app.javashop.client.goods.GoodsWordsClient;
import com.enation.app.javashop.model.goodssearch.enums.GoodsWordsType;
import com.enation.app.javashop.service.goodssearch.GoodsWordsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author fk
 * @version v2.0
 * @Description:
 * @date 2018/8/21 16:11
 * @since v7.0.0
 */
@Service
@ConditionalOnProperty(value = "javashop.product", havingValue = "stand")
public class GoodsWordsClientDefaultImpl implements GoodsWordsClient {

    @Autowired
    private GoodsWordsManager goodsWordsManager;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(String words) {
        goodsWordsManager.delete(words);
    }


    @Override
    public void addWords(String words) {
        goodsWordsManager.addWords(words);
    }

    @Override
    public void delete(GoodsWordsType goodsWordsType, Long id) {
        goodsWordsManager.delete(goodsWordsType,id);
    }

    @Override
    public void updateGoodsNum(String words) {
        goodsWordsManager.updateGoodsNum(words);
    }

    @Override
    public void batchUpdateGoodsNum() {
        goodsWordsManager.batchUpdateGoodsNum();
    }


}
