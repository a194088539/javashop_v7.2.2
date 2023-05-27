package com.enation.app.javashop.service.trade.pintuan;

import com.enation.app.javashop.model.promotion.pintuan.PinTuanGoodsVO;
import com.enation.app.javashop.model.promotion.pintuan.PtGoodsDoc;

import java.util.List;

/**
 * Created by kingapex on 2019-01-21.
 * 拼团搜索业务接口
 * @author kingapex
 * @version 1.0
 * @since 7.1.0
 * 2019-01-21
 */
public interface PinTuanSearchManager {

    /**
     * 搜索拼团商品
     * @param categoryId
     * @param pageNo
     * @param pageSize
     * @return
     */
    List<PtGoodsDoc> search(Long categoryId, Integer pageNo, Integer pageSize);


    /**
     * 向es写入索引
     * @param goodsDoc
     */
    void addIndex(PtGoodsDoc goodsDoc );

    /**
     * 向es写入索引
     * @param pintuanGoods
     * @return  是否生成成功
     */
    boolean addIndex(PinTuanGoodsVO pintuanGoods);

    /**
     * 删除一个sku的索引
     * @param skuId
     */
    void delIndex(Long skuId);


    /**
     * 删除某个商品的所有的索引
     * @param goodsId
     */
    void deleteByGoodsId(Long goodsId);


    /**
     * 删除某个拼团的所有索引
     * @param pinTuanId 拼团id
     */
    void deleteByPintuanId(Long pinTuanId);

    /**
     * 根据拼团id同步es中的拼团商品<br/>
     * 当拼团活动商品发生变化时调用此方法
     * @param pinTuanId
     */
    void syncIndexByPinTuanId(Long pinTuanId);

    /**
     * 根据商品id同步es中的拼团商品<br>
     * @param goodsId 商品id
     */
    void syncIndexByGoodsId(Long goodsId);
}
