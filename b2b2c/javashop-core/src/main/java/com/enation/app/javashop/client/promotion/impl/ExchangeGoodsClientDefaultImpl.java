package com.enation.app.javashop.client.promotion.impl;

import com.enation.app.javashop.client.promotion.ExchangeGoodsClient;
import com.enation.app.javashop.model.goods.dto.ExchangeClientDTO;
import com.enation.app.javashop.model.promotion.exchange.dos.ExchangeDO;
import com.enation.app.javashop.service.promotion.exchange.ExchangeGoodsManager;
import com.enation.app.javashop.model.promotion.tool.dto.PromotionGoodsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * @author fk
 * @version v2.0
 * @Description:
 * @date 2018/8/21 16:14
 * @since v7.0.0
 */
@Service
@ConditionalOnProperty(value="javashop.product", havingValue="stand")
public class ExchangeGoodsClientDefaultImpl implements ExchangeGoodsClient {

    @Autowired
    private ExchangeGoodsManager exchangeGoodsManager;

    @Override
    public ExchangeDO add(ExchangeClientDTO dto) {
        ExchangeDO exchange = dto.getExchangeSetting();
        PromotionGoodsDTO goodsDTO = dto.getGoodsDTO();

        return exchangeGoodsManager.add(exchange, goodsDTO);
    }

    @Override
    public ExchangeDO edit(ExchangeClientDTO dto) {
        ExchangeDO exchange = dto.getExchangeSetting();
        PromotionGoodsDTO goodsDTO = dto.getGoodsDTO();

        return exchangeGoodsManager.edit(exchange, goodsDTO);
    }

    @Override
    public ExchangeDO getModelByGoods(Long goodsId) {

        return exchangeGoodsManager.getModelByGoods(goodsId);
    }

    /**
     * 删除某个商品的积分兑换信息
     *
     * @param goodsId
     * @return
     */
    @Override
    public void del(Long goodsId) {
        exchangeGoodsManager.deleteByGoods(goodsId);
    }
}
