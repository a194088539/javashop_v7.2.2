package com.enation.app.javashop.service.statistics.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.statistics.GoodsStatisticsMapper;
import com.enation.app.javashop.model.base.SearchCriteria;
import com.enation.app.javashop.model.errorcode.StatisticsErrorCode;
import com.enation.app.javashop.model.statistics.exception.StatisticsException;
import com.enation.app.javashop.model.statistics.vo.ChartSeries;
import com.enation.app.javashop.model.statistics.vo.SimpleChart;
import com.enation.app.javashop.service.statistics.Constant.TableNameConstant;
import com.enation.app.javashop.service.statistics.GoodsStatisticManager;
import com.enation.app.javashop.service.statistics.util.StatisticsResultUtil;
import com.enation.app.javashop.service.statistics.util.SyncopateUtil;
import com.enation.app.javashop.util.DataDisplayUtil;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 商品统计
 *
 * @author Chopper
 * @version v1.0
 * @since v7.0
 * 2018-03-23 上午4:17
 */
@Service
public class GoodsStatisticManagerImpl implements GoodsStatisticManager {


    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private GoodsStatisticsMapper goodsStatisticsMapper;

    /**
     * 价格销量统计
     *
     * @param searchCriteria 搜索参数
     * @param prices         价格区间
     * @return chart
     */
    @Override
    public SimpleChart getPriceSales(SearchCriteria searchCriteria, Integer[] prices) {

        searchCriteria = new SearchCriteria(searchCriteria);

        //价格参数判定
        int notAvailable = 2;
        if (prices == null || prices.length < notAvailable) {
            prices = SearchCriteria.defaultPrice();
        }

        //分类id
        Long categoryId = searchCriteria.getCategoryId();

        //店铺id
        Long sellerId = searchCriteria.getSellerId();

        /*
         * 获取 开始/结束 时间
         */
        long[] timestamp = DataDisplayUtil.getStartTimeAndEndTime(searchCriteria);

        //开始时间
        long startTime = timestamp[0];

        //结束时间
        long endTime = timestamp[1];

        //表名后面拼接年份，如es_sss_order_goods_data_2020
        String tableNameEsSssOrderGoodsDataYear = SyncopateUtil.handleSql(searchCriteria.getYear(), TableNameConstant.ES_SSS_ORDER_GOODS_DATA);

        //表名后面拼接年份，如es_sss_order_data_2020
        String tableNameEsSssOrderDataYear = SyncopateUtil.handleSql(searchCriteria.getYear(), TableNameConstant.ES_SSS_ORDER_DATA);

        List<Map<String, Object>> queryList;

        try{
            //查询某一年的表
            queryList = goodsStatisticsMapper.selectPriceSalesList(tableNameEsSssOrderGoodsDataYear, tableNameEsSssOrderDataYear,
                            startTime, endTime, categoryId, sellerId);

            //如果未查到数据，再查总表
            if(queryList == null || queryList.size() == 0){
                queryList = goodsStatisticsMapper.selectPriceSalesList(TableNameConstant.ES_SSS_ORDER_GOODS_DATA, TableNameConstant.ES_SSS_ORDER_DATA,
                                startTime, endTime, categoryId, sellerId);
            }
        } catch (BadSqlGrammarException e){
            //某个年份的统计表不存在，则返回空数据
//            ChartSeries chartSeries = new ChartSeries("下单金额", new String[]{"0", "0", "0", "0"},
//                    new String[]{"0~100", "100~1000", "1000~10000", "10000~100000"});
//            return new SimpleChart(chartSeries, new String[]{"0~100", "100~1000", "1000~10000", "10000~100000"}, new String[0]);
            queryList = new ArrayList<>();
        }


        //商品价格统计转换
        queryList = StatisticsResultUtil.priceRangeCountResult(queryList, prices, "price_interval", "t_num");

        ArrayList<Map<String, Object>> dataList = new ArrayList<>();

        for (int i = 0; i < prices.length - 1; i++) {
            Map<String, Object> m = new HashMap<>(16);
            m.put("t_num", 0);
            m.put("price_interval", i + 1);
            m.put("prices", prices[i]);
            dataList.add(m);

            for (Map map : queryList) {
                if ((int) map.get("price_interval") == (i + 1)) {
                    m.put("t_num", map.get("t_num"));
                }
            }

        }

        String[] xAxis = new String[dataList.size()], localName = new String[dataList.size()], data = new String[dataList.size()];

        int index = 0;
        if (dataList.size() > 0) {
            for (Map map : dataList) {
                xAxis[index] = prices[index] + "~" + prices[index + 1];
                localName[index] = xAxis[index];
                data[index] = map.get("t_num").toString();

                index++;
            }
        }

        ChartSeries chartSeries = new ChartSeries("下单金额", data, localName);

        return new SimpleChart(chartSeries, xAxis, new String[0]);
    }

    /**
     * 热卖商品按金额统计
     *
     * @param searchCriteria 搜索参数
     * @return 热卖商品按金额统计Chart
     */
    @Override
    public SimpleChart getHotSalesMoney(SearchCriteria searchCriteria) {

        searchCriteria = new SearchCriteria(searchCriteria);

        int dataLength = 50;

        try {
            List<Map<String, Object>> dataList = this.getHotSalesMoneyPage(searchCriteria).getData();
            String[] xAxis = new String[dataLength], localName = new String[dataLength], data = new String[dataLength];

            for (int i = 0; i < dataLength; i++) {
                if (null != dataList && i < dataList.size()) {
                    Map map = dataList.get(i);
                    data[i] = map.get("price").toString();
                    localName[i] = map.get("goods_name").toString();
                } else {
                    data[i] = "0";
                    localName[i] = "无";
                }
                xAxis[i] = i + 1 + "";
            }

            ChartSeries chartSeries = new ChartSeries("下单金额", data, localName);

            return new SimpleChart(chartSeries, xAxis, new String[0]);
        } catch (MyBatisSystemException e) {

            //某个年份的统计表不存在，则返回空数据
            String[] xAxis = new String[dataLength];
            String[] data = new String[dataLength];
            String[] localName = new String[dataLength];
            for (int i = 0; i < dataLength; i++) {
                xAxis[i] = i + 1 + "";
                data[i] = null;
                localName[i] = null;
            }

            ChartSeries chartSeries = new ChartSeries("下单金额", data, localName);

            return new SimpleChart(chartSeries, xAxis, new String[0]);
        } catch (Exception e) {
            logger.error("热卖商品按金额统计异常", e);
            throw new StatisticsException(StatisticsErrorCode.E810.code(), StatisticsErrorCode.E810.des());
        }

    }

    /**
     * 热卖商品按金额统计
     *
     * @param searchCriteria 搜索参数
     * @return 热卖商品按金额统计page
     */
    @Override
    public WebPage getHotSalesMoneyPage(SearchCriteria searchCriteria) {

        searchCriteria = new SearchCriteria(searchCriteria);

        //分类id
        Long categoryId = searchCriteria.getCategoryId();

        //店铺id
        Long sellerId = searchCriteria.getSellerId();

        long[] timestamp = DataDisplayUtil.getStartTimeAndEndTime(searchCriteria);

        //开始时间
        long startTime = timestamp[0];

        //结束时间
        long endTime = timestamp[1];


        //表名后面拼接年份，如es_sss_order_goods_data_2020
        String tableNameEsSssOrderGoodsDataYear = SyncopateUtil.handleSql(searchCriteria.getYear(), TableNameConstant.ES_SSS_ORDER_GOODS_DATA);

        //表名后面拼接年份，如es_sss_order_data_2020
        String tableNameEsSssOrderDataYear = SyncopateUtil.handleSql(searchCriteria.getYear(), TableNameConstant.ES_SSS_ORDER_DATA);

        IPage iPage;

        try{
            //查询某一年的表
            iPage = goodsStatisticsMapper.selectHotSalesMoneyPage(new Page(1,50), tableNameEsSssOrderGoodsDataYear,
                            tableNameEsSssOrderDataYear, startTime, endTime, categoryId, sellerId);

            //如果未查到数据再差总表
            if(iPage == null || iPage.getRecords() == null || iPage.getRecords().size() == 0){
                iPage = goodsStatisticsMapper.selectHotSalesMoneyPage(new Page(1,50), TableNameConstant.ES_SSS_ORDER_GOODS_DATA,
                                TableNameConstant.ES_SSS_ORDER_DATA, startTime, endTime, categoryId, sellerId);
            }
        }catch (MyBatisSystemException e){//使用Mybatisplus分页查询，如果表不存在会抛MyBatisSystemException异常，而不是BadSqlGrammarException
            //某个年份的统计表不存在，则返回空数据
            return new WebPage(1L,0L, 50L, new ArrayList());
        }catch(Exception e) {
            e.printStackTrace();
            throw new StatisticsException(StatisticsErrorCode.E810.code(), StatisticsErrorCode.E810.des());
        }

        return PageConvert.convert(iPage);
    }

    /**
     * 热卖商品按数量统计
     *
     * @param searchCriteria 搜索参数
     * @return SimpleChart   热卖商品按数量统计chart
     */
    @Override
    public SimpleChart getHotSalesNum(SearchCriteria searchCriteria) {
        searchCriteria = new SearchCriteria(searchCriteria);


        try {

            List<Map<String, Object>> dataList = this.getHotSalesNumPage(searchCriteria).getData();

            int dataLength = 50;
            String[] xAxis = new String[dataLength], localName = new String[dataLength], data = new String[dataLength];

            for (int i = 0; i < dataLength; i++) {
                if (null != dataList && i < dataList.size()) {
                    Map map = dataList.get(i);
                    data[i] = map.get("order_num").toString();
                    localName[i] = map.get("goods_name").toString();
                } else {
                    data[i] = "0";
                    localName[i] = "无";
                }
                xAxis[i] = i + 1 + "";
            }

            ChartSeries chartSeries = new ChartSeries("下单数量", data, localName);

            return new SimpleChart(chartSeries, xAxis, new String[0]);
        } catch (Exception e) {
            logger.error("热卖商品按数量统计 异常", e);
            throw new StatisticsException(StatisticsErrorCode.E810.code(), StatisticsErrorCode.E810.des());
        }
    }

    /**
     * 热卖商品按数量统计
     *
     * @param searchCriteria 搜索参数
     * @return 按热卖商品数量统计 page
     */
    @Override
    public WebPage getHotSalesNumPage(SearchCriteria searchCriteria) {
        searchCriteria = new SearchCriteria(searchCriteria);


        //分类id
        Long categoryId = searchCriteria.getCategoryId();

        //店铺id
        Long sellerId = searchCriteria.getSellerId();

        long[] timestamp = DataDisplayUtil.getStartTimeAndEndTime(searchCriteria);

        //开始时间
        long startTime = timestamp[0];

        //结束时间
        long endTime = timestamp[1];


        //表名后面拼接年份，如es_sss_order_goods_data_2020
        String tableNameEsSssOrderGoodsDataYear = SyncopateUtil.handleSql(searchCriteria.getYear(), TableNameConstant.ES_SSS_ORDER_GOODS_DATA);

        //表名后面拼接年份，如es_sss_order_data_2020
        String tableNameEsSssOrderDataYear = SyncopateUtil.handleSql(searchCriteria.getYear(), TableNameConstant.ES_SSS_ORDER_DATA);

        IPage iPage;

        try{
            //查询某一年的表
            iPage = goodsStatisticsMapper.selectHotSalesNumPage(new Page(1,50), tableNameEsSssOrderGoodsDataYear,
                            tableNameEsSssOrderDataYear, startTime, endTime, categoryId, sellerId);

            //如果未查到数据，再查总表
            if(iPage == null || iPage.getRecords() == null || iPage.getRecords().size() == 0){
                iPage = goodsStatisticsMapper.selectHotSalesNumPage(new Page(1,50), TableNameConstant.ES_SSS_ORDER_GOODS_DATA,
                                TableNameConstant.ES_SSS_ORDER_DATA, startTime, endTime, categoryId, sellerId);
            }
        }catch (MyBatisSystemException e){//使用Mybatisplus分页查询，如果表不存在会抛MyBatisSystemException异常，而不是BadSqlGrammarException
            //某个年份的统计表不存在，则返回空数据
            return new WebPage(1L,0L, 50L, new ArrayList());
        }catch(Exception e) {
            e.printStackTrace();
            throw new StatisticsException(StatisticsErrorCode.E810.code(), StatisticsErrorCode.E810.des());
        }

        return PageConvert.convert(iPage);
    }

    /**
     * 商品销售明细
     *
     * @param searchCriteria 搜索参数
     * @param goodsName      商品名称
     * @return 销售page
     */
    @Override
    public WebPage getSaleDetails(SearchCriteria searchCriteria, String goodsName, Long pageSize, Long pageNo) {
        searchCriteria = new SearchCriteria(searchCriteria);

        //分类id
        Long categoryId = searchCriteria.getCategoryId();

        //店铺id
        Long sellerId = searchCriteria.getSellerId();

        long[] timestamp = DataDisplayUtil.getStartTimeAndEndTime(searchCriteria);

        //开始时间
        long startTime = timestamp[0];

        //结束时间
        long endTime = timestamp[1];

        //表名后面拼接年份，如es_sss_order_goods_data_2020
        String tableNameEsSssOrderGoodsDataYear = SyncopateUtil.handleSql(searchCriteria.getYear(), TableNameConstant.ES_SSS_ORDER_GOODS_DATA);

        //表名后面拼接年份，如es_sss_order_data_2020
        String tableNameEsSssOrderDataYear = SyncopateUtil.handleSql(searchCriteria.getYear(), TableNameConstant.ES_SSS_ORDER_DATA);


        IPage iPage;

        try{
            //先查某一年的表
            iPage = goodsStatisticsMapper.selectSaleDetailsPage(new Page(1,50), tableNameEsSssOrderGoodsDataYear,
                            tableNameEsSssOrderDataYear, startTime, endTime, goodsName, categoryId, sellerId);

            //如果未查到数据再查总表
            if(iPage == null || iPage.getRecords() == null || iPage.getRecords().size() == 0){
                iPage = goodsStatisticsMapper.selectSaleDetailsPage(new Page(1,50), TableNameConstant.ES_SSS_ORDER_GOODS_DATA,
                                TableNameConstant.ES_SSS_ORDER_DATA, startTime, endTime, goodsName, categoryId, sellerId);
            }
        }catch (MyBatisSystemException e){//使用Mybatisplus分页查询，如果表不存在会抛MyBatisSystemException异常，而不是BadSqlGrammarException
            //某个年份的统计表不存在，则返回空数据
            return new WebPage(1L,0L, 50L, new ArrayList());
        }catch(Exception e) {
            e.printStackTrace();
            throw new StatisticsException(StatisticsErrorCode.E810.code(), StatisticsErrorCode.E810.des());
        }

        return PageConvert.convert(iPage);
    }

    /**
     * 商品收藏排行
     *
     * @param searchCriteria 搜索参数
     * @return 排行page
     */
    @Override
    public WebPage getGoodsCollectPage(SearchCriteria searchCriteria) {

        searchCriteria = new SearchCriteria(searchCriteria);

        //分类id
        Long categoryId = searchCriteria.getCategoryId();

        //店铺id
        Long sellerId = searchCriteria.getSellerId();

        //表名后面拼接年份，如es_sss_goods_data_2020
        String tableNameEsSssGoodsDataYear = SyncopateUtil.handleSql(searchCriteria.getYear(), TableNameConstant.ES_SSS_GOODS_DATA);

        //表名后面拼接年份，如es_sss_shop_data_2020
        String tableNameEsSssShopDataYear = SyncopateUtil.handleSql(searchCriteria.getYear(), TableNameConstant.ES_SSS_SHOP_DATA);

        IPage iPage;

        try{
            //先查某一年的表
            iPage = goodsStatisticsMapper.selectGoodsCollectPage(new Page(1,50), tableNameEsSssGoodsDataYear,
                    tableNameEsSssShopDataYear, categoryId, sellerId);

            //如果未查到数据再查总表
            if(iPage == null || iPage.getRecords() == null || iPage.getRecords().size() == 0){
                iPage = goodsStatisticsMapper.selectGoodsCollectPage(new Page(1,50), TableNameConstant.ES_SSS_GOODS_DATA,
                        TableNameConstant.ES_SSS_SHOP_DATA, categoryId, sellerId);
            }
        }catch (MyBatisSystemException e){
            //某个年份的统计表不存在，则返回空数据
            return new WebPage(1L,0L, 50L, new ArrayList());
        }catch(Exception e) {
            e.printStackTrace();
            throw new StatisticsException(StatisticsErrorCode.E810.code(), StatisticsErrorCode.E810.des());
        }

        return PageConvert.convert(iPage);
    }

    /**
     * 商品收藏排行
     *
     * @param searchCriteria 搜索参数
     * @return SimpleChart   商品收藏chart
     */
    @Override
    public SimpleChart getGoodsCollect(SearchCriteria searchCriteria) {

        searchCriteria = new SearchCriteria(searchCriteria);
        try {
            WebPage page = this.getGoodsCollectPage(searchCriteria);
            List<Map<String, Object>> dataList = page.getData();

            int dataLength = 50;
            String[] xAxis = new String[dataLength], localName = new String[dataLength], data = new String[dataLength];

            for (int i = 0; i < dataLength; i++) {
                if (null != dataList && i < dataList.size()) {
                    Map map = dataList.get(i);
                    data[i] = map.get("favorite_num").toString();
                    localName[i] = map.get("goods_name").toString();
                } else {
                    data[i] = "0";
                    localName[i] = "无";
                }
                xAxis[i] = i + 1 + "";
            }

            ChartSeries chartSeries = new ChartSeries("商品收藏TOP50", data, localName);

            return new SimpleChart(chartSeries, xAxis, new String[0]);
        } catch (Exception e) {
            logger.error("商品收藏排行异常", e);
            throw new StatisticsException(StatisticsErrorCode.E810.code(), StatisticsErrorCode.E810.des());
        }
    }

}
