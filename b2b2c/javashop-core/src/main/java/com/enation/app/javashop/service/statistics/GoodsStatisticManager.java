package com.enation.app.javashop.service.statistics;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.base.SearchCriteria;
import com.enation.app.javashop.model.statistics.vo.SimpleChart;

/**
 * 商品相关统计
 *
 * @author chopper
 * @version v1.0
 * @since v7.0
 * 2018-03-23 上午12:16
 */
public interface GoodsStatisticManager {


    /**
     * 价格销量统计
     *
     * @param searchCriteria 搜索参数
     * @param prices         价格区间
     * @return chart
     */
    SimpleChart getPriceSales(SearchCriteria searchCriteria, Integer[] prices);


    /**
     * 热卖商品按金额统计
     *
     * @param searchCriteria 搜索参数
     * @return chart
     */
    SimpleChart getHotSalesMoney(SearchCriteria searchCriteria);

    /**
     * 热卖商品按金额统计
     *
     * @param searchCriteria 搜索参数
     * @return 热卖商品金额page
     */
    WebPage getHotSalesMoneyPage(SearchCriteria searchCriteria);

    /**
     * 热卖商品按数量统计
     *
     * @param searchCriteria 搜索参数
     * @return chart
     */
    SimpleChart getHotSalesNum(SearchCriteria searchCriteria);

    /**
     * 热卖商品按数量统计
     *
     * @param searchCriteria 搜索参数
     * @return 热卖商品数量Page
     */
    WebPage getHotSalesNumPage(SearchCriteria searchCriteria);

    /**
     * 商品销售明细
     *
     * @param searchCriteria 搜索参数
     * @param goodsName      商品名称
     * @param pageSize       分页大小
     * @param pageNo         页码
     * @return 商品销售page
     */
    WebPage getSaleDetails(SearchCriteria searchCriteria, String goodsName, Long pageSize, Long pageNo);

    /**
     * 商品收藏排行
     *
     * @param searchCriteria 搜索参数
     * @return chart
     */
    SimpleChart getGoodsCollect(SearchCriteria searchCriteria);


    /**
     * 商品收藏排行 PAGE
     *
     * @param searchCriteria 搜索参数
     * @return 收藏page
     */
    WebPage getGoodsCollectPage(SearchCriteria searchCriteria);


}
