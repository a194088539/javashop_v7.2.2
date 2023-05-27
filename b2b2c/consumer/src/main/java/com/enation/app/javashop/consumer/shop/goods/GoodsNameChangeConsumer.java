package com.enation.app.javashop.consumer.shop.goods;

import com.enation.app.javashop.consumer.core.event.GoodsChangeEvent;
import com.enation.app.javashop.model.base.message.GoodsChangeMsg;
import com.enation.app.javashop.client.goods.GoodsClient;
import com.enation.app.javashop.client.member.MemberCollectionGoodsClient;
import com.enation.app.javashop.model.goods.vo.CacheGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author fk
 * @version v1.0
 * @Description: 商品名称变化相关消费
 * @date 2020/2/27 10:13
 * @since v7.2.0
 */
@Service
public class GoodsNameChangeConsumer implements GoodsChangeEvent {

    @Autowired
    private MemberCollectionGoodsClient memberCollectionGoodsClient;
    @Autowired
    private GoodsClient goodsClient;

    @Override
    public void goodsChange(GoodsChangeMsg goodsChangeMsg) {

        Long[] goodsIds = goodsChangeMsg.getGoodsIds();
        int operationType = goodsChangeMsg.getOperationType();
        //更改商品操作 && 商品名称有变化
        if (GoodsChangeMsg.UPDATE_OPERATION == operationType && goodsChangeMsg.isNameChange()) {
            Long goodsId = goodsIds[0];
            CacheGoods goods = goodsClient.getFromCache(goodsId);
            if (goods == null) {
                return;
            }
            //更新商品收藏的商品名称
            memberCollectionGoodsClient.updateGoodsName(goods.getGoodsId(), goods.getGoodsName());
        }
    }
}
