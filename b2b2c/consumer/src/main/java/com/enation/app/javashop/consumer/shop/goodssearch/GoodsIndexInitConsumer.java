package com.enation.app.javashop.consumer.shop.goodssearch;

import com.enation.app.javashop.consumer.core.event.GoodsIndexInitEvent;
import com.enation.app.javashop.client.goods.GoodsClient;
import com.enation.app.javashop.client.goods.GoodsIndexClient;
import com.enation.app.javashop.client.trade.PintuanGoodsClient;
import com.enation.app.javashop.client.trade.PintuanClient;
import com.enation.app.javashop.model.promotion.pintuan.Pintuan;
import com.enation.app.javashop.model.promotion.tool.enums.PromotionStatusEnum;
import com.enation.app.javashop.model.system.vo.TaskProgressConstant;
import com.enation.app.javashop.model.util.progress.ProgressManager;
import com.enation.app.javashop.framework.logs.Debugger;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author fk
 * @version v1.0
 * @Description: 商品索引初始化
 * @date 2018/6/25 11:38
 * @since v7.0.0
 */
@Service
public class GoodsIndexInitConsumer implements GoodsIndexInitEvent {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ProgressManager progressManager;
    @Autowired
    private GoodsIndexClient goodsIndexClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private PintuanClient pintuanClient;
    @Autowired
    private PintuanGoodsClient pintuanGoodsClient;

    @Autowired
    private Debugger debugger;

    @Override
    public void createGoodsIndex() {

        debugger.log("开始生成索引");

        String key = TaskProgressConstant.GOODS_INDEX;
        try {
            List<Pintuan> list = pintuanClient.get(PromotionStatusEnum.UNDERWAY.name());

            /** 获取商品数 */
            int goodsCount = this.goodsClient.queryGoodsCount();

            /** 生成任务进度 */
            progressManager.taskBegin(key, goodsCount + list.size());

            //生成普通商品商品索引
            boolean goodsResult = createOrdinaryGoods(goodsCount);

            //生成拼团活动商品索引
            boolean ptResult  = createPintuanGoods(list, key);

            //任务结束
            progressManager.taskEnd(key, "索引生成完成");

            debugger.log("索引生成完成");

            if (goodsResult | ptResult) {
                debugger.log("索引生成出现错误");
            }

        } catch (Exception e) {
            debugger.log("索引生成异常");
            progressManager.taskError(key, "生成索引异常，请联系运维人员");
            this.logger.error("生成索引异常：", e);

        }

    }

    /**
     * 生成普通商品的索引
     *
     * @param goodsCount 商品数
     */
    private boolean createOrdinaryGoods(Integer goodsCount) {

        //用来标记是否有错误
        boolean hasError = false;

        long pageSize = 100;
        long pageCount;
        pageCount = goodsCount / pageSize;
        pageCount = goodsCount % pageSize > 0 ? pageCount + 1 : pageCount;
        for (int i = 1; i <= pageCount; i++) {
            List<Map<String,Object>> goodsList = this.goodsClient.queryGoodsByRange(Integer.valueOf(i).longValue(), pageSize);
            Long[] goodsIds = new Long[goodsList.size()];
            int j = 0;
            for (Map map : goodsList) {
                goodsIds[j] = Long.valueOf(map.get("goods_id").toString());
                j++;
            }
            List<Map<String, Object>> list = goodsClient.getGoodsAndParams(goodsIds);
            boolean result = goodsIndexClient.addAll(list, i);

            //有过错误就是有错误
            hasError = result && hasError;
        }

        return hasError;

    }

    /**
     * 生成拼团活动商品索引
     *
     * @param list 拼团活动集合
     */
    private boolean createPintuanGoods(List<Pintuan> list, String key) {
        boolean hasError = false;
        for (Pintuan pintuan : list) {
            boolean result  = pintuanGoodsClient.createGoodsIndex(pintuan.getPromotionId());
            progressManager.taskUpdate(key, "为拼团名称为[" + pintuan.getPromotionName() + "]的活动生成索引");
            hasError = result && hasError;
        }

        return hasError;
    }

}
