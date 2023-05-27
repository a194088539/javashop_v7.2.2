package com.enation.app.javashop.consumer.shop.sss;

import com.enation.app.javashop.consumer.core.event.GoodsChangeEvent;
import com.enation.app.javashop.model.base.message.GoodsChangeMsg;
import com.enation.app.javashop.client.goods.GoodsWordsClient;
import com.enation.app.javashop.client.statistics.GoodsDataClient;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 商品数据收集消费者
 * @author chopper
 * @version v1.0
 * @since v7.0
 * 2018/6/20 下午2:19
 * @Description:
 *
 */
@Component
public class DataGoodsConsumer implements GoodsChangeEvent {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private GoodsDataClient goodsDataClient;

	@Autowired
	private GoodsWordsClient goodsWordsClient;

	@Override
    public void goodsChange(GoodsChangeMsg goodsChangeMsg){

		try {
			if (goodsChangeMsg.getGoodsIds() == null || goodsChangeMsg.getGoodsIds().length == 0) {
                return;
            }
			// 添加商品
			if (goodsChangeMsg.getOperationType().equals(GoodsChangeMsg.ADD_OPERATION)) {
                this.goodsDataClient.addGoods(goodsChangeMsg.getGoodsIds());
            }
			// 修改商品
			if (goodsChangeMsg.getOperationType().equals(GoodsChangeMsg.UPDATE_OPERATION)) {
                this.goodsDataClient.updateGoods(goodsChangeMsg.getGoodsIds());
            }
			// 删除商品 暂时不删除统计表重的商品
//			if (goodsChangeMsg.getOperationType().equals(GoodsChangeMsg.DEL_OPERATION)) {
//                this.goodsDataClient.deleteGoods(goodsChangeMsg.getGoodsIds());
//            }
			this.goodsWordsClient.batchUpdateGoodsNum();
		} catch (Exception e) {
			logger.error("商品消息监听异常：",e);
			e.printStackTrace();
		}
	}

}
