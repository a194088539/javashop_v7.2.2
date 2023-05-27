package com.enation.app.javashop.consumer.shop.goods;

import com.enation.app.javashop.consumer.core.event.GoodsChangeEvent;
import com.enation.app.javashop.model.base.message.GoodsChangeMsg;
import com.enation.app.javashop.client.goods.GoodsClient;
import com.enation.app.javashop.client.promotion.ExchangeGoodsClient;
import com.enation.app.javashop.model.goods.dos.GoodsDO;
import com.enation.app.javashop.model.goods.enums.GoodsType;
import com.enation.app.javashop.model.goods.vo.GoodsSkuVO;
import com.enation.app.javashop.model.promotion.exchange.dos.ExchangeDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 积分兑换商品脚本信息生成和删除
 *
 * @author duanmingyu
 * @version v1.0
 * @since v7.2.0
 * 2020-02-20
 */
@Service
public class ExchangeGoodsScriptConsumer implements GoodsChangeEvent {

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private ExchangeGoodsClient exchangeGoodsClient;

    @Override
    public void goodsChange(GoodsChangeMsg goodsChangeMsg) {
        //获取商品ID数组
        Long[] goodsIds = goodsChangeMsg.getGoodsIds();
        //获取商品信息集合
        List<GoodsDO> goodsList = goodsClient.queryDo(goodsIds);
        //获取操作类型
        int operationType = goodsChangeMsg.getOperationType();

        //如果商品集合不为空并且集合长度不为0
        if (goodsList != null && goodsList.size() != 0) {
            for (GoodsDO goodsDO : goodsList) {
                //如果商品为积分兑换商品
                if (GoodsType.POINT.name().equals(goodsDO.getGoodsType())) {
                    //获取商品ID
                    Long goodsId = goodsDO.getGoodsId();
                    //获取积分兑换商品信息
                    ExchangeDO exchangeDO = this.exchangeGoodsClient.getModelByGoods(goodsId);
                    //获取商品的SKU信息
                    List<GoodsSkuVO> skuList = this.goodsClient.listByGoodsId(goodsId);

                    //如果为true 证明商品正在上架销售
                    boolean normal = goodsDO.getDisabled() == 1 && goodsDO.getMarketEnable() == 1 && goodsDO.getIsAuth() == 1;
                    //如果为true 证明商品已下架但是没有删除
                    boolean under = goodsDO.getDisabled() == 1 && goodsDO.getMarketEnable() == 0 && goodsDO.getIsAuth() == 1;
                    //新增脚本条件：（操作类型为添加或者审核成功操作） && 商品未删除 && 商品未下架 && 商品审核状态为审核通过
                    boolean add = (GoodsChangeMsg.ADD_OPERATION == operationType || GoodsChangeMsg.GOODS_VERIFY_SUCCESS == operationType) && normal;
                    //修改脚本条件：操作类型为修改 && 商品未删除 && 商品未下架 && 商品审核状态为审核通过
                    boolean edit = GoodsChangeMsg.UPDATE_OPERATION == operationType && normal;
                    //删除脚本条件：操作类型为修改 && 商品未删除 && 商品已下架 && 商品审核状态为审核通过
                    boolean delete = GoodsChangeMsg.UNDER_OPERATION == operationType && under;

                    if (add || edit) {
                        for (GoodsSkuVO goodsSkuVO : skuList) {
                            //先删除已有的脚本信息
                            this.goodsClient.deleteSkuExchangeScript(goodsSkuVO.getSkuId());
                            //生成脚本信息
                            this.goodsClient.createSkuExchangeScript(goodsSkuVO.getSkuId(), exchangeDO.getExchangeId(), exchangeDO.getExchangeMoney(), exchangeDO.getExchangePoint());
                        }
                    }

                    if (delete) {
                        for (GoodsSkuVO goodsSkuVO : skuList) {
                            //删除已有的脚本信息
                            this.goodsClient.deleteSkuExchangeScript(goodsSkuVO.getSkuId());
                        }
                    }
                }
            }
        }
    }
}
