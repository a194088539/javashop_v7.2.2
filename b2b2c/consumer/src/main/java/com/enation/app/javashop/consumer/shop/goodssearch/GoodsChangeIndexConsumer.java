package com.enation.app.javashop.consumer.shop.goodssearch;

import java.util.List;
import java.util.Map;

import com.enation.app.javashop.client.goods.GoodsIndexClient;
import com.enation.app.javashop.consumer.core.event.GoodsChangeEvent;
import com.enation.app.javashop.model.base.message.GoodsChangeMsg;
import com.enation.app.javashop.client.goods.GoodsClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 消费者
 *
 * @author zh
 * @version v1.0
 * @since v1.0
 * 2017年4月12日 下午4:33:14
 */
@Service
public class GoodsChangeIndexConsumer implements GoodsChangeEvent {

    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private GoodsIndexClient goodsIndexClient;

    @Override
    public void goodsChange(GoodsChangeMsg goodsChangeMsg) {

        Long[] goodsIds = goodsChangeMsg.getGoodsIds();
        int operationType = goodsChangeMsg.getOperationType();
        List<Map<String, Object>> list = goodsClient.getGoodsAndParams(goodsIds);

        //添加
        if (GoodsChangeMsg.ADD_OPERATION == operationType) {

            if (list != null && list.size() > 0) {
                goodsIndexClient.addIndex(list.get(0));
            }

        } else if (GoodsChangeMsg.UPDATE_OPERATION == operationType
                || GoodsChangeMsg.INRECYCLE_OPERATION == operationType
                || GoodsChangeMsg.REVERT_OPERATION == operationType
                || GoodsChangeMsg.GOODS_VERIFY_SUCCESS == operationType
                || GoodsChangeMsg.GOODS_VERIFY_FAIL == operationType) {//修改(修改，还原，放入购物车，审核)

            if (list != null) {
                for (Map<String, Object> map : list) {
                    goodsIndexClient.updateIndex(map);
                }
            }

        } else if (GoodsChangeMsg.DEL_OPERATION == operationType || GoodsChangeMsg.UNDER_OPERATION == operationType) {
            //删除
            if (list != null) {
                for (Map<String, Object> map : list) {
                    goodsIndexClient.deleteIndex(map);
                }
            }

        }
    }
}
