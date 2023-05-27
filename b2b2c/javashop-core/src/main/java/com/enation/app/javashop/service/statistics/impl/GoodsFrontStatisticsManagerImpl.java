package com.enation.app.javashop.service.statistics.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.statistics.GoodsDataMapper;
import com.enation.app.javashop.mapper.statistics.GoodsFrontStatisticsMapper;
import com.enation.app.javashop.model.base.SearchCriteria;
import com.enation.app.javashop.model.statistics.dto.GoodsData;
import com.enation.app.javashop.service.statistics.Constant.TableNameConstant;
import com.enation.app.javashop.service.statistics.GoodsFrontStatisticsManager;
import com.enation.app.javashop.model.errorcode.StatisticsErrorCode;
import com.enation.app.javashop.model.statistics.exception.StatisticsException;
import com.enation.app.javashop.model.statistics.vo.ChartSeries;
import com.enation.app.javashop.model.statistics.vo.SimpleChart;
import com.enation.app.javashop.service.statistics.util.StatisticsResultUtil;
import com.enation.app.javashop.service.statistics.util.StatisticsUtil;
import com.enation.app.javashop.model.trade.order.enums.OrderStatusEnum;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import com.enation.app.javashop.service.statistics.util.SyncopateUtil;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 商家中心，商品分析统计 接口实现体
 *
 * @author Chopper
 * @version v1.0
 * @since v7.0 2018/3/28 上午9:49
 */
@Service
public class GoodsFrontStatisticsManagerImpl implements GoodsFrontStatisticsManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private GoodsDataMapper goodsDataMapper;
    @Autowired
    private GoodsFrontStatisticsMapper goodsFrontStatisticsMapper;


    /**
     * 获取商品详情
     *
     * @param pageNo    当前页码
     * @param pageSize  每页数据量
     * @param catId     商品分类id
     * @param goodsName 商品名称
     * @return WebPage 分页数据
     */
    @Override
    public WebPage getGoodsDetail(Long pageNo, Long pageSize, Long catId, String goodsName) {

        try {
            // 获取当前登录的会员店铺id
            Long sellerId = UserContext.getSeller().getSellerId();

            // 获得今天23:59:59的时间戳
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;
            int day = cal.get(Calendar.DATE);
            long endTime = DateUtil.toDate(year + "-" + month + "-" + day + " 23:59:59", "yyyy-MM-dd HH:mm:ss")
                    .getTime() / 1000;

            // 获取30天前23:59:59的时间戳
            long startTime = endTime - 2592000;

            // 商品分类路径
            String catPath;

            // 如果分类id为0，则查询全部分类，不为0，则于数据库中查询路径，如果没有此分类，或有重复数据，则返回空数据
            if (catId == 0) {
                catPath = "0";
            } else {
                //根据分类id查询分类路径
                GoodsData goodsData = new QueryChainWrapper<>(goodsDataMapper)
                        //查询分类路径
                        .select("DISTINCT category_path")
                        //按分类id查询
                        .eq("category_id", catId)
                        //查询单个对象
                        .one();

                //没有此分类，返回空数据
                if(goodsData == null){
                    return new WebPage(pageNo,0L, pageSize, new ArrayList());
                }

                catPath = goodsData.getCategoryPath();
            }


            //表名后面拼接年份，如es_sss_order_goods_data_2020
            String tableNameEsSssOrderGoodsDataYear = SyncopateUtil.handleSql(year, TableNameConstant.ES_SSS_ORDER_GOODS_DATA);

            //表名后面拼接年份，如es_sss_order_data_2020
            String tableNameEsSssOrderDataYear = SyncopateUtil.handleSql(year, TableNameConstant.ES_SSS_ORDER_DATA);

            IPage iPage;

            //查询今年的表
            iPage = goodsFrontStatisticsMapper.selectGoodsStatisticsPage(new Page(pageNo, pageSize), tableNameEsSssOrderGoodsDataYear,
                            tableNameEsSssOrderDataYear, sellerId, startTime, endTime, goodsName, catPath);

//            //如果今年的表未查到数据，再查总表
            if(iPage == null || iPage.getRecords() == null || iPage.getRecords().size() == 0){
                iPage = goodsFrontStatisticsMapper.selectGoodsStatisticsPage(new Page(pageNo,pageSize), TableNameConstant.ES_SSS_ORDER_GOODS_DATA,
                                TableNameConstant.ES_SSS_ORDER_DATA, sellerId, startTime, endTime, goodsName, catPath);
            }

            return PageConvert.convert(iPage);
        } catch (MyBatisSystemException e){//使用Mybatisplus分页查询，如果表不存在会抛MyBatisSystemException异常，而不是BadSqlGrammarException
            //某个年份的统计表不存在，则返回空数据
            return new WebPage(pageNo,0L, pageSize, new ArrayList());
        } catch (Exception e) {
            logger.error("获取商品详情异常", e);
            throw new StatisticsException(StatisticsErrorCode.E810.code(), "业务异常");
        }
    }


    /**
     * 价格区间
     *
     * @param sections       区间List 格式：0 100 200
     * @param searchCriteria 时间，分类id，店铺id
     * @return SimpleChart 简单图表数据
     */
    @Override
    public SimpleChart getGoodsPriceSales(List<Integer> sections, SearchCriteria searchCriteria) {

        // 验证参数
        SearchCriteria.checkDataParams(searchCriteria, true, false, false);

        String cycleType = searchCriteria.getCycleType();
        Integer year = searchCriteria.getYear();
        Integer month = searchCriteria.getMonth();
        Long catId = searchCriteria.getCategoryId();

        // 获取当前登录的会员店铺id
        Long sellerId = UserContext.getSeller().getSellerId();

        try {
            // 获取开始时间和结束时间
            long[] times = StatisticsUtil.getInstance().getStartTimeAndEndTime(cycleType, year, month);

            long startTime = times[0];
            long endTime = times[1];

            // 商品分类
            String catPath;
            if (null == catId || catId == 0) {
                catPath = "0";
            } else {
                //根据分类id查询分类路径
                GoodsData goodsData = new QueryChainWrapper<>(goodsDataMapper)
                        //查询分类路径
                        .select("DISTINCT category_path")
                        //按分类id查询
                        .eq("category_id", catId)
                        //查询单个对象
                        .one();

                if(goodsData == null){
                    catPath = null;
                }else{
                    catPath = goodsData.getCategoryPath();
                }
            }

            List<Map<String, Object>> data;

            if(catPath == null){
                data = new ArrayList<>();
            }else{
                data = goodsFrontStatisticsMapper.selectBySellerIdAndCatPath(sellerId, catPath, startTime, endTime);
            }

            data = StatisticsResultUtil.priceRangeSumInt(data, sections, "elt_data", "goods_num");

            // 将数据重新排序
            data = StatisticsUtil.getInstance().fitOrderPriceData(data, sections);

            // 图表数据
            String[] chartData = new String[data.size()];

            // 数据名称，就是区间
            String[] localName = new String[data.size()];

            int i = 0;
            for (Map map : data) {
                chartData[i] = null == map.get("goods_num") ? "0" : map.get("goods_num").toString();
                localName[i] = map.get("elt_data").toString();
                i++;
            }

            ChartSeries chartSeries = new ChartSeries("价格销量分析", chartData, localName);

            return new SimpleChart(chartSeries, localName, new String[0]);
        } catch (Exception e) {
            logger.error("价格区间异常", e);
            throw new StatisticsException(StatisticsErrorCode.E810.code(), "业务异常");
        }
    }

    /**
     * 商品获取下单金额前30
     *
     * @param topNum         top数
     * @param searchCriteria 时间相关参数
     * @return WebPage 分页对象
     */
    @Override
    public WebPage getGoodsOrderPriceTopPage(int topNum, SearchCriteria searchCriteria) {

        SearchCriteria.checkDataParams(searchCriteria, true, false, false);
        try {
            // 获取当前登录的会员店铺id
            Long sellerId = UserContext.getSeller().getSellerId();

            // 如果排名没有值，默认为30
            if (topNum == 0) {
                topNum = 30;
            }
            // 获取时间戳
            long[] times = StatisticsUtil.getInstance().getStartTimeAndEndTime(searchCriteria.getCycleType(),
                    searchCriteria.getYear(), searchCriteria.getMonth());

            long startTime = times[0];
            long endTime = times[1];


            //表名后面拼接年份，如es_sss_order_goods_data_2020
            String tableNameEsSssOrderGoodsDataYear = SyncopateUtil.handleSql(searchCriteria.getYear(), TableNameConstant.ES_SSS_ORDER_GOODS_DATA);

            //表名后面拼接年份，如es_sss_order_data_2020
            String tableNameEsSssOrderDataYear = SyncopateUtil.handleSql(searchCriteria.getYear(), TableNameConstant.ES_SSS_ORDER_DATA);

            IPage iPage;

            //查询今年的表
            iPage = goodsFrontStatisticsMapper.selectGoodsOrderPriceTopPage(new Page(1,Integer.valueOf(topNum).longValue()),
                            tableNameEsSssOrderGoodsDataYear, tableNameEsSssOrderDataYear,
                            sellerId, OrderStatusEnum.COMPLETE.name(), startTime, endTime);

            //如果今年的表未查到数据，再查总表
            if(iPage == null || iPage.getRecords() == null || iPage.getRecords().size() == 0){
                iPage = goodsFrontStatisticsMapper.selectGoodsOrderPriceTopPage(new Page(1,Integer.valueOf(topNum).longValue()),
                        TableNameConstant.ES_SSS_ORDER_GOODS_DATA, TableNameConstant.ES_SSS_ORDER_DATA,
                                sellerId, OrderStatusEnum.COMPLETE.name(), startTime, endTime);
            }

            return PageConvert.convert(iPage);
        } catch (MyBatisSystemException e){//使用Mybatisplus分页查询，如果表不存在会抛MyBatisSystemException异常，而不是BadSqlGrammarException
            //某个年份的统计表不存在，则返回空数据
            return new WebPage(1L,0L, Long.valueOf(topNum).longValue(), new ArrayList());
        } catch (Exception e){
            logger.error("商品获取下单金额异常", e);
            throw new StatisticsException(StatisticsErrorCode.E810.code(), "业务异常");
        }
    }

    /**
     * 下单商品数量前30，分页数据
     *
     * @param topNum         名次 默认为30
     * @param searchCriteria 时间相关参数
     * @return WebPage 分页对象
     */
    @Override
    public WebPage getGoodsNumTopPage(int topNum, SearchCriteria searchCriteria) {

        SearchCriteria.checkDataParams(searchCriteria, true, false, false);

        String cycleType = searchCriteria.getCycleType();
        Integer year = searchCriteria.getYear();
        Integer month = searchCriteria.getMonth();

        try {
            // 获取当前登录的会员店铺id
            Long sellerId = UserContext.getSeller().getSellerId();

            if (topNum == 0) {
                topNum = 30;
            }

            long[] times = StatisticsUtil.getInstance().getStartTimeAndEndTime(cycleType, year, month);

            long startTime = times[0];
            long endTime = times[1];


            //表名后面拼接年份，如es_sss_order_goods_data_2020
            String tableNameEsSssOrderGoodsDataYear = SyncopateUtil.handleSql(searchCriteria.getYear(), TableNameConstant.ES_SSS_ORDER_GOODS_DATA);

            //表名后面拼接年份，如es_sss_order_data_2020
            String tableNameEsSssOrderDataYear = SyncopateUtil.handleSql(searchCriteria.getYear(), TableNameConstant.ES_SSS_ORDER_DATA);

            IPage iPage;

            //查询今年的表
            iPage = goodsFrontStatisticsMapper.selectGoodsNumTopPage(new Page(1,Integer.valueOf(topNum).longValue()),
                            tableNameEsSssOrderGoodsDataYear, tableNameEsSssOrderDataYear,
                            sellerId, OrderStatusEnum.COMPLETE.name(), startTime, endTime);

            //如果今年的表未查到数据，再查总表
            if(iPage == null || iPage.getRecords() == null || iPage.getRecords().size() == 0){
                iPage = goodsFrontStatisticsMapper.selectGoodsNumTopPage(new Page(1,Integer.valueOf(topNum).longValue()),
                                TableNameConstant.ES_SSS_ORDER_GOODS_DATA, TableNameConstant.ES_SSS_ORDER_DATA,
                                sellerId, OrderStatusEnum.COMPLETE.name(), startTime, endTime);
            }

            return PageConvert.convert(iPage);
        } catch (MyBatisSystemException e){//使用Mybatisplus分页查询，如果表不存在会抛MyBatisSystemException异常，而不是BadSqlGrammarException
            //某个年份的统计表不存在，则返回空数据
            return new WebPage(1L,0L, Long.valueOf(topNum).longValue(), new ArrayList());
        } catch (Exception e) {
            logger.error("下单商品数量 异常", e);
            throw new StatisticsException(StatisticsErrorCode.E810.code(), "业务异常");
        }
    }

    /**
     * 获取商品下单金额前30，图表数据
     *
     * @param topNum         top数
     * @param searchCriteria 时间相关参数
     * @return SimpleChart 简单图表数据
     */
    @Override
    public SimpleChart getGoodsOrderPriceTop(Integer topNum, SearchCriteria searchCriteria) {

        SearchCriteria.checkDataParams(searchCriteria, true, false, false);
        try {
            // 获取当前登录的会员店铺id
            Long sellerId = UserContext.getSeller().getSellerId();

            // 如果排名没有值
            if (topNum == 0) {
                topNum = 30;
            }
            long[] times = StatisticsUtil.getInstance().getStartTimeAndEndTime(searchCriteria.getCycleType(),
                    searchCriteria.getYear(), searchCriteria.getMonth());

            long startTime = times[0];
            long endTime = times[1];

            //表名后面拼接年份，如es_sss_order_goods_data_2020
            String tableNameEsSssOrderGoodsDataYear = SyncopateUtil.handleSql(searchCriteria.getYear(), TableNameConstant.ES_SSS_ORDER_GOODS_DATA);

            //表名后面拼接年份，如es_sss_order_data_2020
            String tableNameEsSssOrderDataYear = SyncopateUtil.handleSql(searchCriteria.getYear(), TableNameConstant.ES_SSS_ORDER_DATA);

            List<Map<String, Object>> list;;

            try{
                //查询今年的表
                list = goodsFrontStatisticsMapper.selectGoodsOrderPriceTopList(tableNameEsSssOrderGoodsDataYear, tableNameEsSssOrderDataYear,
                            sellerId, OrderStatusEnum.COMPLETE.name(), startTime, endTime, topNum);

                //如果今年的表未查到数据，再查总表
                if(list == null || list.size() == 0){
                    list = goodsFrontStatisticsMapper.selectGoodsOrderPriceTopList(TableNameConstant.ES_SSS_ORDER_GOODS_DATA, TableNameConstant.ES_SSS_ORDER_DATA,
                                sellerId, OrderStatusEnum.COMPLETE.name(), startTime, endTime, topNum);
                }
            } catch (BadSqlGrammarException e){
                //某个年份的统计表不存在，则返回空数据
                if(e.getMessage().endsWith("doesn't exist")){
                    list = new ArrayList<>();
                }else{
                    e.printStackTrace();
                    throw new StatisticsException(StatisticsErrorCode.E810.code(), StatisticsErrorCode.E810.des());
                }
            }


            // 图表数据，下单金额
            String[] data = new String[list.size()];

            // 数据名称，即商品名称
            String[] localName = new String[list.size()];

            // x轴刻度
            String[] xAxis = new String[list.size()];

            // 如果数据大于0，则遍历
            int dataNum = list.size();
            if (list.size() > 0) {
                for (int i = 0; i < dataNum; i++) {
                    Map map = list.get(i);
                    data[i] = map.get("sum").toString();
                    localName[i] = map.get("goods_name").toString();
                    xAxis[i] = i + 1 + "";
                }
            }

            ChartSeries chartSeries = new ChartSeries("总金额", data, localName);

            return new SimpleChart(chartSeries, xAxis, new String[0]);
        } catch (Exception e) {
            logger.error("获取商品下单金额异常", e);
            throw new StatisticsException(StatisticsErrorCode.E810.code(), "业务异常");
        }

    }

    /**
     * 获取商品购买数量前30
     *
     * @param topNum         top数
     * @param searchCriteria 时间相关参数
     * @return SimpleChart 简单图表数据
     */
    @Override
    public SimpleChart getGoodsNumTop(Integer topNum, SearchCriteria searchCriteria) {

        SearchCriteria.checkDataParams(searchCriteria, true, false, false);

        String cycleType = searchCriteria.getCycleType();
        Integer year = searchCriteria.getYear();
        Integer month = searchCriteria.getMonth();

        try {
            // 获取当前登录的会员店铺id
            Long sellerId = UserContext.getSeller().getSellerId();

            long[] times = StatisticsUtil.getInstance().getStartTimeAndEndTime(cycleType, year, month);

            long startTime = times[0];
            long endTime = times[1];

            // 如果排名没有值
            if (topNum == 0) {
                topNum = 30;
            }


            //表名后面拼接年份，如es_sss_order_goods_data_2020
            String tableNameEsSssOrderGoodsDataYear = SyncopateUtil.handleSql(searchCriteria.getYear(), TableNameConstant.ES_SSS_ORDER_GOODS_DATA);

            //表名后面拼接年份，如es_sss_order_data_2020
            String tableNameEsSssOrderDataYear = SyncopateUtil.handleSql(searchCriteria.getYear(), TableNameConstant.ES_SSS_ORDER_DATA);

            List<Map<String, Object>> list;;

            try{
                //查询今年的表
                list = goodsFrontStatisticsMapper.selectGoodsNumTopList(tableNameEsSssOrderGoodsDataYear, tableNameEsSssOrderDataYear,
                        sellerId, OrderStatusEnum.COMPLETE.name(), startTime, endTime, topNum);

                //如果今年的表未查到数据，再查总表
                if(list == null || list.size() == 0){
                    list = goodsFrontStatisticsMapper.selectGoodsNumTopList(TableNameConstant.ES_SSS_ORDER_GOODS_DATA, TableNameConstant.ES_SSS_ORDER_DATA,
                            sellerId, OrderStatusEnum.COMPLETE.name(), startTime, endTime, topNum);
                }
            }catch (BadSqlGrammarException e){
                //某个年份的统计表不存在，则返回空数据
                if(e.getMessage().endsWith("doesn't exist")){
                    list = new ArrayList<>();
                }else{
                    e.printStackTrace();
                    throw new StatisticsException(StatisticsErrorCode.E810.code(), StatisticsErrorCode.E810.des());
                }
            }


            // 图表数据，即商品被购买的数量
            String[] chartData = new String[list.size()];

            // 数据名称，即商品名称
            String[] localName = new String[list.size()];

            //x轴刻度
            String[] xAxis = new String[list.size()];

            // 如果list有数据，则遍历
            if (list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    Map map = list.get(i);
                    chartData[i] = map.get("all_num").toString();
                    localName[i] = map.get("goods_name").toString();
                    xAxis[i] = i + 1 + "";
                }
            }

            ChartSeries chartSeries = new ChartSeries("下单量", chartData, localName);

            return new SimpleChart(chartSeries, xAxis, new String[0]);
        } catch (Exception e) {
            logger.error("获取商品购买数量异常", e);
            throw new StatisticsException(StatisticsErrorCode.E810.code(), "业务异常");
        }
    }

    /**
     * 生成价格销量统计的SQL CASE语句
     *
     * @param ranges 整数集合
     * @return SQL CASE Statement
     */
    private static String getGoodsPriceSqlCaseStatement(List<Integer> ranges) {
        if (null == ranges || ranges.size() == 0) {
            return "0";
        }
        // 由大到小排序
        StatisticsUtil.sortRanges(ranges);

        StringBuilder sb = new StringBuilder("(case ");
        sb.append("when oi.price > ").append(ranges.get(0)).append(" then '").append(ranges.get(0)).append("+' ");
        for (int i = 0; i < ranges.size(); ) {
            Integer num = ranges.get(i);
            Integer nextNum;
            if (i < ranges.size() - 1) {
                nextNum = ranges.get(i + 1);
                sb.append("when oi.price >= ").append(nextNum).append(" and oi.price <= ").append(num).append(" then '")
                        .append(nextNum).append("~").append(num).append("' ");
                i += 2;
            }
        }
        sb.append("else '0' end ) ");
        return sb.toString();
    }

}
