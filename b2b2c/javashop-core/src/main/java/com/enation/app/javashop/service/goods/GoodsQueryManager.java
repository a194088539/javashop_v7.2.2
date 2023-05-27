package com.enation.app.javashop.service.goods;

import com.enation.app.javashop.model.goods.dos.GoodsDO;
import com.enation.app.javashop.model.goods.dto.GoodsQueryParam;
import com.enation.app.javashop.model.goods.enums.GoodsType;
import com.enation.app.javashop.model.goods.vo.BuyCountVO;
import com.enation.app.javashop.model.goods.vo.CacheGoods;
import com.enation.app.javashop.model.goods.vo.GoodsSelectLine;
import com.enation.app.javashop.model.goods.vo.GoodsVO;
import com.enation.app.javashop.framework.database.WebPage;

import java.util.List;
import java.util.Map;

/**
 * 商品业务层
 *
 * @author fk
 * @version v2.0
 * @since v7.0.0 2018-03-21 11:23:10
 */
public interface GoodsQueryManager {

    /**
     * 查询商品列表
     *
     * @param goodsQueryParam 查询条件
     * @return
     */
    WebPage list(GoodsQueryParam goodsQueryParam);

    /**
     * 查询商品排序列表
     *
     * @param goodsQueryParam 查询条件
     * @return
     */
    WebPage goodsPriorityList(GoodsQueryParam goodsQueryParam);

    /**
     * 获取商品
     *
     * @param id 商品主键
     * @return Goods 商品
     */
    GoodsDO getModel(Long id);

    /**
     * 商家查询商品,编辑查看使用
     *
     * @param goodsId 商品主键
     * @return
     */
    GoodsVO sellerQueryGoods(Long goodsId);

    /**
     * 缓存中查询商品的信息
     *
     * @param goodsId 商品主键
     * @return
     */
    CacheGoods getFromCache(Long goodsId);

    /**
     * 查询预警货品的商品
     *
     * @param param 查询条件
     * @return
     */
    WebPage warningGoodsList(GoodsQueryParam param);

    /**
     * 查询多个商品的信息
     *
     * @param goodsIds 商品id数组
     * @return
     */
    List<GoodsDO> queryDo(Long[] goodsIds);

    /**
     * 查询商品的好平率
     *
     * @param goodsId 商品id
     * @return
     */
    Double getGoodsGrade(Long goodsId);

    /**
     * 判断商品是否都是某商家的商品
     *
     * @param goodsIds 商品id数组
     * @return
     */
    void checkSellerGoodsCount(Long[] goodsIds);

    /**
     * 查询很多商品的信息和参数信息
     *
     * @param goodsIds 商品id数组
     * @return
     */
    List<Map<String, Object>> getGoodsAndParams(Long[] goodsIds);

    /**
     * 查询一个商家的所有在售商品
     *
     * @param sellerId 商家id
     * @return
     */
    List<Map<String, Object>> getGoodsAndParams(Long sellerId);

    /**
     * 按销量查询若干条数据
     *
     * @param sellerId 商家id
     * @return
     */
    List<GoodsDO> listGoods(Long sellerId);

    /**
     * 查询购买量
     *
     * @param goodsIds 商品id数组
     * @return
     */
    List<BuyCountVO> queryBuyCount(Long[] goodsIds);


    /**
     * 查询某店铺正在售卖中的商品数量
     *
     * @param sellerId 商家id
     * @return
     */
    Integer getSellerGoodsCount(Long sellerId);

    /**
     * 查看某商品是否在配送范围
     * @param goodsId 商品id
     * @param areaId 地区id
     * @return
     */
    Integer checkArea(Long goodsId, Long areaId);

    /**
     * 根据条件查询商品数
     *
     * @param status   商品状态
     * @param sellerId 商家id
     * @param disabled 商品是否已删除
     * @return 商品数
     */
    Integer getGoodsCountByParam(Integer status, Long sellerId, Integer disabled);

    /**
     * 根据商品id集合查询商品信息
     *
     * @param goodsIds 商品ids
     * @return  商品信息
     */
    List<Map<String, Object>> getGoods(Long[] goodsIds);

    /**
     * 查询商家所有的商品
     * @param sellerId  商家id
     * @param goodsType  商品类型
     * @return  所有商品
     *
     */
    List<GoodsDO> getSellerGoods(Long sellerId, GoodsType goodsType);

    /**
     * 查询多个sku信息
     * @param skuIds skuid数组
     * @return
     */
    List<GoodsSelectLine> querySkus(Long[] skuIds);

    /**
     * 根据条件查询多个商品信息
     * 多用于前端商品选择器
     * @param goodsIds 商品ID组
     * @param param 查询条件
     * @return
     */
    List<GoodsSelectLine> queryGoodsLines(Long[] goodsIds, GoodsQueryParam param);

    /**
     * 查询某范围的商品信息
     * @param pageNo 每页
     * @param pageSize 每页数量
     * @return
     */
    List<Map<String,Object>> queryGoodsByRange(Long pageNo, Long pageSize);
}
