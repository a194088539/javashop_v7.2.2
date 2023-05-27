package com.enation.app.javashop.client.goods;

import com.enation.app.javashop.model.goods.dos.CategoryDO;
import com.enation.app.javashop.model.goods.dos.GoodsDO;
import com.enation.app.javashop.model.goods.dto.GoodsQueryParam;
import com.enation.app.javashop.model.goods.vo.*;
import com.enation.app.javashop.model.trade.order.vo.OrderSkuVO;
import com.enation.app.javashop.framework.database.WebPage;

import java.util.List;
import java.util.Map;

/**
 * @author fk
 * @version v1.0
 * @Description: 商品对外的接口
 * @date 2018/7/26 10:33
 * @since v7.0.0
 */
public interface GoodsClient {

    /**
     * 获取新赠商品
     *
     * @param length
     * @return
     */
    List<BackendGoodsVO> newGoods(Integer length);

    /**
     * 下架某店铺的全部商品
     *
     * @param sellerId
     */
    void underShopGoods(Long sellerId);

    /**
     * 更新商品好平率
     */
    void updateGoodsGrade();

    /**
     * 缓存中查询商品的信息
     *
     * @param goodsId
     * @return
     */
    CacheGoods getFromCache(Long goodsId);

    /**
     * 查询sku信息
     *
     * @param skuId
     * @return
     */
    GoodsSkuVO getSku(Long skuId);

    /**
     * 查询多个商品的基本信息
     *
     * @param goodsIds
     * @return
     */
    List<GoodsSelectLine> query(Long[] goodsIds);

    /**
     * 查询多个sku的基本信息
     *
     * @param skuIds
     * @return
     */
    List<GoodsSelectLine> querySkus(Long[] skuIds);

    /**
     * 查询多个商品的基本信息
     *
     * @param goodsIds
     * @return
     */
    List<GoodsDO> queryDo(Long[] goodsIds);

    /**
     * 判断商品是否都是某商家的商品
     *
     * @param goodsIds
     * @return
     */
    void checkSellerGoodsCount(Long[] goodsIds);

    /**
     * 查询很多商品的信息和参数信息
     *
     * @param goodsIds 商品id集合
     * @return
     */
    List<Map<String, Object>> getGoodsAndParams(Long[] goodsIds);

    /**
     * 查询商品信息
     *
     * @param goodsIds 商品id集合
     * @return
     */
    List<Map<String, Object>> getGoods(Long[] goodsIds);

    /**
     * 根基商家id查询商家商品的信息和参数信息
     *
     * @param sellerId 商户id
     * @return
     */
    List<Map<String, Object>> getGoodsAndParams(Long sellerId);

    /**
     * 按销量查询若干条数据
     *
     * @param sellerId
     * @return
     */
    List<GoodsDO> listGoods(Long sellerId);

    /**
     * 缓存中查询sku信息
     *
     * @param skuId
     * @return
     */
    GoodsSkuVO getSkuFromCache(Long skuId);

    /**
     * 更新商品的评论数量
     *
     * @param goodsId
     * @param num
     */
    void updateCommentCount(Long goodsId, Integer num);


    /**
     * 更新商品的购买数量
     *
     * @param list
     */
    void updateBuyCount(List<OrderSkuVO> list);


    /**
     * 查询商品总数
     *
     * @return 商品总数
     */
    Integer queryGoodsCount();

    /**
     * 根据条件查询商品总数
     *
     * @param status   商品状态
     * @param sellerId 商家id
     * @return 商品总数
     */
    Integer queryGoodsCountByParam(Integer status, Long sellerId);

    /**
     * 查询某范围的商品信息
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    List<Map<String,Object>> queryGoodsByRange(Long pageNo, Long pageSize);

    /**
     * 添加商品快照时使用的接口
     *
     * @param goodsId
     * @return
     */
    GoodsSnapshotVO queryGoodsSnapShotInfo(Long goodsId);

    /**
     * 查询商品列表
     *
     * @param goodsQueryParam
     * @return
     */
    WebPage list(GoodsQueryParam goodsQueryParam);

    /**
     * 获取商品分类
     *
     * @param id 商品分类主键
     * @return Category 商品分类
     */
    CategoryDO getCategory(Long id);

    /**
     * 校验商品模版是否使用
     *
     * @param templateId
     * @return 商品
     */
    GoodsDO checkShipTemplate(Long templateId);

    /**
     * 查询某店铺正在售卖中的商品数量
     *
     * @param sellerId
     * @return
     */
    Integer getSellerGoodsCount(Long sellerId);


    /**
     * 修改某店铺商品店铺名称
     *
     * @param sellerId
     * @param sellerName
     */
    void changeSellerName(Long sellerId, String sellerName);

    /**
     * 更新商品类型
     *
     * @param type 商品状态
     */
    void updateGoodsType(Long sellerId, String type);


    /**
     * 查询某店铺的积分商品
     *
     * @param shopId
     * @return
     */
    List<GoodsDO> listPointGoods(Long shopId);

    /**
     * 根据商品id查询所有sku
     *
     * @param goodsId 商品id
     * @return 所有sku
     */
    List<GoodsSkuVO> listByGoodsId(Long goodsId);

    /**
     * 根据商家id查询出所有的商品信息
     *
     * @param sellerId 商家id
     * @return 所有的商品信息
     */
    List<GoodsDO> getSellerGoods(Long sellerId);

    /**
     * 查询所有的分类，父子关系
     *
     * @param parentId
     * @return
     */
    List<CategoryVO> listAllChildren(Long parentId);

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
}
