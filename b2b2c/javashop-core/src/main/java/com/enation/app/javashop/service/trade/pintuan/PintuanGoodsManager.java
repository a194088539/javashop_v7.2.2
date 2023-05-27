package com.enation.app.javashop.service.trade.pintuan;

import com.enation.app.javashop.model.goods.vo.GoodsSkuVO;
import com.enation.app.javashop.model.promotion.tool.vo.PromotionAbnormalGoods;
import com.enation.app.javashop.model.promotion.pintuan.PinTuanGoodsVO;
import com.enation.app.javashop.model.promotion.pintuan.PintuanGoodsDO;
import com.enation.app.javashop.framework.database.WebPage;

import java.util.List;

/**
 * 拼团商品业务层
 *
 * @author admin
 * @version vv1.0.0
 * @since vv7.1.0
 * 2019-01-22 11:20:56
 */
public interface PintuanGoodsManager {

    /**
     * 查询拼团商品列表
     *
     * @param page     页码
     * @param pageSize 每页数量
     * @return WebPage
     */
    WebPage list(long page, long pageSize);


    /**
     * 添加拼团商品
     *
     * @param pintuanGoods 拼团商品
     * @return PintuanGoods 拼团商品
     */
    PintuanGoodsDO add(PintuanGoodsDO pintuanGoods);


    /**
     * 批量保存拼团商品数据
     *
     * @param pintuanId        拼团id
     * @param pintuanGoodsList 要批量添加的拼团商品
     */
    void save(Long pintuanId, List<PintuanGoodsDO> pintuanGoodsList);

    /**
     * 修改拼团商品
     *
     * @param pintuanGoods 拼团商品
     * @param id           拼团商品主键
     * @return PintuanGoods 拼团商品
     */
    PintuanGoodsDO edit(PintuanGoodsDO pintuanGoods, Long id);

    /**
     * 删除拼团商品
     *
     * @param id 拼团商品主键
     */
    void delete(Long id);

    /**
     * 获取拼团商品
     *
     * @param id 拼团商品主键
     * @return PintuanGoods  拼团商品
     */
    PintuanGoodsDO getModel(Long id);

    /**
     * 获取拼团商品
     * @param pintuanId
     * @param skuId
     * @return
     */
    PintuanGoodsDO getModel(Long pintuanId, Long skuId);

    /**
     * 获取拼团商品详细，包括拼团本身的信息
     *
     * @param skuId skuid
     * @return 商品详细vo
     */
    /**
     *  获取拼团商品详细，包括拼团本身的信息
     * @param skuId  skuid
     * @param time   时间戳
     * @return  商品详细vo
     */
    PinTuanGoodsVO getDetail(Long skuId, Long time);


    /**
     * 获取某商品参加拼团的sku
     *
     * @param goodsId
     * @return
     */
    List<GoodsSkuVO> skus(Long goodsId,Long pintuanId);

    /**
     * 更新已团数量
     *
     * @param id    拼团商品id
     * @param num   数量
     */
    void addQuantity(Long id, Integer num);

    /**
     * 获取某活动所有商品
     *
     * @param promotionId
     * @return
     */
    List<PinTuanGoodsVO> all(Long promotionId);

    /**
     * 获取参与拼团活动的所有商品集合
     * @param promotionId 拼团活动ID
     * @return
     */
    List<PintuanGoodsDO> getPintuanGoodsList(Long promotionId);

    /**
     * 关闭一个活动的促销商品索引
     *
     * @param promotionId
     */
    void delIndex(Long promotionId);

    /**
     * 开启一个活动的促销商品索引
     *
     * @param promotionId
     */
    boolean addIndex(Long promotionId);


    /**
     * 商品查询
     *
     * @param page        页码
     * @param pageSize    分页大小
     * @param promotionId 促销id
     * @param name        商品名称
     * @return
     */
    WebPage page(Long page, Long pageSize, Long promotionId, String name);

    /**
     * 查询指定时间范围，是否有参与其他活动
     *
     * @param skuIds      商品id集合
     * @param startTime   开始时间
     * @param endTime     结束时间
     * @param promotionID 促销id
     */
    List<PromotionAbnormalGoods> checkPromotion(Long[] skuIds, Long startTime, Long endTime, Long promotionID);


    /**
     * 删除拼团商品
     * @param delSkuIds
     */
    void deletePinTuanGoods(List<Long> delSkuIds);


}
