package com.enation.app.javashop.service.goods;

import com.enation.app.javashop.model.goods.dos.GoodsDO;
import com.enation.app.javashop.model.goods.dos.GoodsSkuDO;
import com.enation.app.javashop.model.goods.dto.GoodsQueryParam;
import com.enation.app.javashop.model.goods.vo.GoodsSkuVO;
import com.enation.app.javashop.framework.database.WebPage;

import java.util.List;

/**
 * 商品sku业务层
 *
 * @author fk
 * @version v2.0
 * @since v7.0.0 2018-03-21 11:48:40
 */
public interface GoodsSkuManager {
    /**
     * 查询SKU列表
     * @param param 查询条件
     * @return
     */
    WebPage list(GoodsQueryParam param);

    /**
     * 添加商品sku
     *
     * @param skuList sku集合
     * @param goods 商品do对象
     */
    void add(List<GoodsSkuVO> skuList, GoodsDO goods);

    /**
     * 修改商品sku
     *
     * @param skuList sku集合
     * @param goods 商品do对象
     */
    void edit(List<GoodsSkuVO> skuList, GoodsDO goods);

    /**
     * 删除商品关联的sku
     *
     * @param goodsIds 商品id数组
     */
    void delete(Long[] goodsIds);

    /**
     * 查询某商品的sku
     *
     * @param goodsId 商品id
     * @return
     */
    List<GoodsSkuVO> listByGoodsId(Long goodsId);

    /**
     * 缓存中查询sku信息
     *
     * @param skuId skuid
     * @return
     */
    GoodsSkuVO getSkuFromCache(Long skuId);

    /**
     * 查询sku信息
     *
     * @param skuId skuid
     * @return
     */
    GoodsSkuVO getSku(Long skuId);

    /**
     * 查询某商家的可售卖的商品的sku集合
     *
     * @return
     */
    List<GoodsSkuDO> querySellerAllSku();

    /**
     * 判断商品是否都是某商家的商品
     *
     * @param skuIds
     * @return
     */
    void checkSellerGoodsCount(Long[] skuIds);

    /**
     * 查询单个sku
     *
     * @param id skuid
     * @return
     */
    GoodsSkuDO getModel(Long id);


    /**
     * 根据商品sku主键id集合获取商品信息
     * @param skuIds sku数组
     * @return
     */
    List<GoodsSkuVO> query(Long[] skuIds);

    /**
     * 根据商品信息更新sku的图片等信息
     * @param goods 商品do对象
     */
    void updateSkuByGoods(GoodsDO goods);

    /**
     * 更新商品的sku，可能是添加，可能是修改
     * @param skuList sku集合
     * @param goodsId 商品id
     */
    void editSkus(List<GoodsSkuVO> skuList, Long goodsId);

    /**
     * 生成积分兑换商品的脚本信息
     * @param skuId 商品skuID
     * @param exchangeId 积分兑换商品ID
     * @param price 兑换积分商品所需的价钱
     * @param point 兑换积分商品所需的积分
     */
    void createSkuExchangeScript(Long skuId, Long exchangeId, Double price, Integer point);

    /**
     * 删除积分兑换商品的脚本信息
     * @param skuId 商品skuID
     */
    void deleteSkuExchangeScript(Long skuId);

    /**
     * 删除积分兑换商品的脚本信息
     * 此方法用于商家修改商品时，修改之前商品类型为积分商品，修改后为普通商品的情况
     * @param goodsId 商品id
     * @param oldType 原商品类型
     * @param newType 现商品类型
     */
    void deleteSkuExchangeScript(Long goodsId, String oldType, String newType);
}
