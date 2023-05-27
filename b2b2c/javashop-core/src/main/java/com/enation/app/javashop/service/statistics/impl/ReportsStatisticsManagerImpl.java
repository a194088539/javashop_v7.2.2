package com.enation.app.javashop.service.statistics.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.mapper.statistics.ReportsStatisticsMapper;
import com.enation.app.javashop.model.base.SearchCriteria;
import com.enation.app.javashop.client.system.RegionsClient;
import com.enation.app.javashop.service.statistics.Constant.TableNameConstant;
import com.enation.app.javashop.service.statistics.ReportsStatisticsManager;
import com.enation.app.javashop.model.errorcode.StatisticsErrorCode;
import com.enation.app.javashop.model.statistics.exception.StatisticsException;
import com.enation.app.javashop.model.statistics.enums.QueryDateType;
import com.enation.app.javashop.model.statistics.enums.RegionsDataType;
import com.enation.app.javashop.model.statistics.vo.ChartSeries;
import com.enation.app.javashop.model.statistics.vo.MultipleChart;
import com.enation.app.javashop.model.statistics.vo.SimpleChart;
import com.enation.app.javashop.service.statistics.util.StatisticsResultUtil;
import com.enation.app.javashop.util.DataDisplayUtil;
import com.enation.app.javashop.service.statistics.util.StatisticsUtil;
import com.enation.app.javashop.service.statistics.util.SyncopateUtil;
import com.enation.app.javashop.model.system.dos.Regions;
import com.enation.app.javashop.model.trade.order.enums.OrderStatusEnum;
import com.enation.app.javashop.model.trade.order.enums.PayStatusEnum;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.StringUtil;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 商家中心，运营报告实现类
 *
 * @author mengyuanming
 * @version 2.0
 * @since 7.0
 * 2018/5/11 20:05
 */
@Service
public class ReportsStatisticsManagerImpl implements ReportsStatisticsManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Autowired
    private RegionsClient regionsClient;

    @Autowired
    private ReportsStatisticsMapper reportsStatisticsMapper;


    /**
     * 销售统计 下单金额
     *
     * @param searchCriteria 统计参数，时间
     * @return MultipleChart 复杂图表数据
     */
    @Override
    public MultipleChart getSalesMoney(SearchCriteria searchCriteria) {

        String cycleType = searchCriteria.getCycleType();
        int year = searchCriteria.getYear();
        int month = null == searchCriteria.getMonth() ? 0 : searchCriteria.getMonth();
        SearchCriteria.checkDataParams(cycleType, year, month);
        Long sellerId = UserContext.getSeller().getSellerId();
        try {

            String orderStatus = OrderStatusEnum.COMPLETE.value();
            String payStatus = PayStatusEnum.PAY_YES.value();

            //判断sql日期分组条件
            String circle;
            if (Objects.equals(searchCriteria.getCycleType(), QueryDateType.YEAR.name())) {
                circle = "%m";
            } else {
                circle = "%d";
            }

            //表名后面拼接年份，如es_sss_order_data_2020
            String tableNameEsSssOrderDataYear = SyncopateUtil.handleSql(searchCriteria.getYear(), TableNameConstant.ES_SSS_ORDER_DATA);

            List<Map<String, Object>> currentList;


            /*
             * start:查询当前周期数据开始
             */
            try{
                //查询今年的表
                currentList = reportsStatisticsMapper.selectSalesMoneyList(tableNameEsSssOrderDataYear, circle, orderStatus, payStatus, sellerId);

                //如果今年的表未查到数据，再查总表
                if(currentList == null || currentList.size() == 0){
                    currentList = reportsStatisticsMapper.selectSalesMoneyList(TableNameConstant.ES_SSS_ORDER_DATA, circle, orderStatus, payStatus, sellerId);
                }
            } catch (BadSqlGrammarException e){
                //某个年份的统计表不存在，则返回空数据
                if(e.getMessage().endsWith("doesn't exist")){
                    currentList = new ArrayList<>();
                }else{
                    e.printStackTrace();
                    throw new StatisticsException(StatisticsErrorCode.E810.code(), StatisticsErrorCode.E810.des());
                }
            }
            /*
             * end:查询当前周期数据结束
             */


            /*
             * start:查询上一周期数据
             */

            //7.2.1是这么写的，暂时先这么改
            List<Map<String, Object>> lastList = currentList;

            if (QueryDateType.YEAR.value().equals(cycleType)) {
                year = year - 1;
            } else {
                month = month - 1;
            }

            /*
             * end:查询上一周期数据结束
             */

            // 判断按年查询还是按月查询，获取刻度长度
            int time;
            if (QueryDateType.YEAR.value().equals(cycleType)) {
                time = 12;
            } else {
                // 本月与上月对比，按天数多的为准，month在查询上月数据时减了1，所以要再加1
                int currentMonth = DataDisplayUtil.getMonthDayNum(month + 1, year);
                int lastMonth = DataDisplayUtil.getMonthDayNum(month, year);
                if (currentMonth > lastMonth) {
                    time = currentMonth;
                } else {
                    time = lastMonth;
                }
            }

            // 填充x轴刻度
            String[] xAxis = new String[time];
            // 数据名称
            String[] localName = new String[time];
            for (int i = 0; i < time; i++) {
                xAxis[i] = i + 1 + "";
                localName[i] = i + 1 + "";
            }

            String[] data = new String[time];
            String[] lastData = new String[time];

            // 向数组填充数据
            this.dataSet(currentList, data, time, "t_money");
            this.dataSet(lastList, lastData, time, "t_money");

            ChartSeries currentSeries = new ChartSeries((QueryDateType.YEAR.value().equals(cycleType) ? "本年" : "本月"), data, localName);
            ChartSeries lastSeries = new ChartSeries((QueryDateType.YEAR.value().equals(cycleType) ? "去年" : "上月"), lastData, localName);

            List<ChartSeries> series = new ArrayList<>();
            series.add(currentSeries);
            series.add(lastSeries);

            // 多数据复杂图表对象
            return new MultipleChart(series, xAxis, new String[0]);
        } catch (Exception e) {
            logger.error("销售统计-下单金额异常", e);
            throw new StatisticsException(StatisticsErrorCode.E810.code(), "业务异常");
        }

    }


    /**
     * 销售统计 下单量
     *
     * @param searchCriteria 统计参数，时间
     * @return MultipleChart 复杂图表数据
     */
    @Override
    public MultipleChart getSalesNum(SearchCriteria searchCriteria) {

        String cycleType = searchCriteria.getCycleType();
        Integer year = searchCriteria.getYear();
        int month = null == searchCriteria.getMonth() ? 0 : searchCriteria.getMonth();
        SearchCriteria.checkDataParams(cycleType, year, month);
        Long sellerId = UserContext.getSeller().getSellerId();
        SearchCriteria.checkDataParams(cycleType, year, month);

        try {

            //判断sql日期分组条件
            String circle;
            if (Objects.equals(searchCriteria.getCycleType(), QueryDateType.YEAR.name())) {
                circle = "%m";
            } else {
                circle = "%d";
            }

            String orderStatus = OrderStatusEnum.COMPLETE.value();
            String payStatus = PayStatusEnum.PAY_YES.value();

            //表名后面拼接年份，如es_sss_order_data_2020
            String tableNameEsSssOrderDataYear = SyncopateUtil.handleSql(searchCriteria.getYear(), TableNameConstant.ES_SSS_ORDER_DATA);

            List<Map<String, Object>> currentList;

            /*
             * start:查询当前周期数据开始
             */
            try{
                //查询今年的表
                currentList = reportsStatisticsMapper.selectSalesNumList(tableNameEsSssOrderDataYear, circle, orderStatus, payStatus, sellerId);

                //如果今年的表未查到数据，再查总表
                if(currentList == null || currentList.size() == 0){
                    currentList = reportsStatisticsMapper.selectSalesNumList(TableNameConstant.ES_SSS_ORDER_DATA, circle, orderStatus, payStatus, sellerId);
                }
            } catch (BadSqlGrammarException e){
                //某个年份的统计表不存在，则返回空数据
                if(e.getMessage().endsWith("doesn't exist")){
                    currentList = new ArrayList<>();
                }else{
                    e.printStackTrace();
                    throw new StatisticsException(StatisticsErrorCode.E810.code(), StatisticsErrorCode.E810.des());
                }
            }

            /*
             * end:当前周期数据查询结束
             */

            /*
             * start:开查询上一周期数据
             */

            //7.2.1是这么写的，暂时先这么改
            List<Map<String, Object>> lastList = currentList;

            if (QueryDateType.YEAR.value().equals(cycleType)) {
                year = year - 1;
            } else {
                month = month - 1;
            }

            /*
             * end:上一周期数据查询结束
             */

            // x轴刻度长度
            int time;
            if (QueryDateType.YEAR.value().equals(cycleType)) {
                time = 12;
            } else {
                // 本月与上月对比，按天数多的为准，month在查询上月数据时减了1，所以要再加1
                int currentMonth = DataDisplayUtil.getMonthDayNum(month + 1, year);
                int lastMonth = DataDisplayUtil.getMonthDayNum(month, year);
                if (currentMonth > lastMonth) {
                    time = currentMonth;
                } else {
                    time = lastMonth;
                }
            }

            // x轴刻度
            String[] xAxis = new String[time];
            // 数据名称
            String[] localName = new String[time];
            for (int i = 0; i < time; i++) {
                xAxis[i] = i + 1 + "";
                localName[i] = xAxis[i];
            }

            String[] data = new String[time];
            String[] lastData = new String[time];

            // 填充data和lastData数组
            this.dataSet(currentList, data, time, "t_num");
            this.dataSet(lastList, lastData, time, "t_num");

            ChartSeries currentSeries = new ChartSeries((QueryDateType.YEAR.value().equals(cycleType) ? "本年" : "本月"), data, localName);
            ChartSeries lastSeries = new ChartSeries((QueryDateType.YEAR.value().equals(cycleType) ? "去年" : "上月"), lastData, localName);

            List<ChartSeries> series = new ArrayList<>();
            series.add(currentSeries);
            series.add(lastSeries);

            // 复杂图表数据
            return new MultipleChart(series, xAxis, new String[0]);
        } catch (Exception e) {
            logger.error("销售统计-下单量 异常", e);
            throw new StatisticsException(StatisticsErrorCode.E810.code(), "业务异常");
        }
    }


    /**
     * 销售统计 分页数据
     *
     * @param searchCriteria 统计参数，时间
     * @param pageNo         查询页码
     * @param pageSize       分页数据长度
     * @return WebPage 分页数据
     */
    @Override
    public WebPage getSalesPage(SearchCriteria searchCriteria, long pageNo, long pageSize) {

        String cycleType = searchCriteria.getCycleType();
        Integer year = searchCriteria.getYear();
        Integer month = searchCriteria.getMonth();
        SearchCriteria.checkDataParams(cycleType, year, month);
        Long sellerId = UserContext.getSeller().getSellerId();

        SearchCriteria.checkDataParams(cycleType, year, month);

        try {

            long[] times = StatisticsUtil.getInstance().getStartTimeAndEndTime(cycleType, year, month);
            long startTime = times[0];
            long endTime = times[1];

            String orderStatus = OrderStatusEnum.COMPLETE.value();
            String payStatus = PayStatusEnum.PAY_YES.value();

            //表名后面拼接年份，如es_sss_order_data_2020
            String tableNameEsSssOrderDataYear = SyncopateUtil.handleSql(searchCriteria.getYear(), TableNameConstant.ES_SSS_ORDER_DATA);

            IPage iPage;

            try{
                //查询今年的表
                iPage = reportsStatisticsMapper.selectSalesPage(new Page(pageNo, pageSize), tableNameEsSssOrderDataYear, orderStatus, payStatus, startTime, endTime, sellerId);

                //如果今年的表未查到数据，再查总表
                if(iPage == null || iPage.getRecords() == null || iPage.getRecords().size() == 0){
                    iPage = reportsStatisticsMapper.selectSalesPage(new Page(pageNo, pageSize), TableNameConstant.ES_SSS_ORDER_DATA, orderStatus, payStatus, startTime, endTime, sellerId);
                }
            } catch (MyBatisSystemException e){
                //某个年份的统计表不存在，则返回空数据
                return new WebPage(pageNo, 0L, pageSize, new ArrayList());
            }


            List<Map<String, Object>> list = iPage.getRecords();

            for (Map<String, Object> map : list) {
                // 获取当前时间戳，数据库中时间戳是Long型的
                Long timestamp = (Long) map.get("create_time");
                map.replace("create_time", timestamp);

                // 获取订单状态，并将值改为文字，因为只查询已完成订单，所以直接填入已完成
                String status = "已完成";
                map.replace("order_status", status);

            }

            return new WebPage(iPage.getCurrent(), iPage.getTotal(), iPage.getSize(), list);
        } catch (Exception e) {
            logger.error("销售统计-分页数据 异常", e);
            throw new StatisticsException(StatisticsErrorCode.E810.code(), "业务异常");
        }
    }

    /**
     * 销售分析，数据小结
     *
     * @param searchCriteria 统计参数，时间
     * @return 查询时间内下单金额之和与下单量之和
     */
    @Override
    public Map getSalesSummary(SearchCriteria searchCriteria) {

        String cycleType = searchCriteria.getCycleType();
        Integer year = searchCriteria.getYear();
        Integer month = searchCriteria.getMonth();
        SearchCriteria.checkDataParams(cycleType, year, month);
        Long sellerId = UserContext.getSeller().getSellerId();

        SearchCriteria.checkDataParams(cycleType, year, month);

        try {

            long[] times = StatisticsUtil.getInstance().getStartTimeAndEndTime(cycleType, year, month);
            long startTime = times[0];
            long endTime = times[1];

            String orderStatus = OrderStatusEnum.COMPLETE.value();
            String payStatus = PayStatusEnum.PAY_YES.value();

            //表名后面拼接年份，如es_sss_order_data_2020
            String tableNameEsSssOrderDataYear = SyncopateUtil.handleSql(searchCriteria.getYear(), TableNameConstant.ES_SSS_ORDER_DATA);

            Map<String, Object> map;

            try{
                //查询今年的表
                map = reportsStatisticsMapper.selectSalesSummaryMap(tableNameEsSssOrderDataYear, orderStatus, payStatus, startTime, endTime, sellerId);

                //如果今年的表未查到数据，再查总表
                if(map == null || map.size() <= 0){
                    map = reportsStatisticsMapper.selectSalesSummaryMap(TableNameConstant.ES_SSS_ORDER_DATA, orderStatus, payStatus, startTime, endTime, sellerId);
                }
            } catch (BadSqlGrammarException e){
                //某个年份的统计表不存在，则返回空数据
                if(e.getMessage().endsWith("doesn't exist")){
                    return new HashMap();
                }else{
                    e.printStackTrace();
                    throw new StatisticsException(StatisticsErrorCode.E810.code(), StatisticsErrorCode.E810.des());
                }
            }

            if (map.get("order_price") == null) {
                map.put("order_price", 0.00);
            }
            return map;
        } catch (Exception e) {
            logger.error("销售分析-数据小结异常", e);
            //某个年份的统计表不存在，则返回空数据
            return new HashMap();

        }
    }

    /**
     * 区域分析，地图数据
     *
     * @param searchCriteria 时间相关参数
     * @param type           获取的数据类型
     * @return MapChartData 地图图表数据
     */
    @Override
    public List regionsMap(SearchCriteria searchCriteria, String type) {

        String cycleType = searchCriteria.getCycleType();
        Integer year = searchCriteria.getYear();
        Integer month = searchCriteria.getMonth();
        Long sellerId = UserContext.getSeller().getSellerId();
        SearchCriteria.checkDataParams(cycleType, year, month);

        try {

            // 获取国内各省份
            List<Regions> provinceList = this.regionsClient.getRegionsChildren(0L);

            // 区分出区域分析的三种数据，拼接不同的sql字符串，获取不同的字段名
            String[] needDataType = this.mapDataType(type);
            String sqlDifference = needDataType[0];
            String dataDifference = needDataType[1];

            long[] times = StatisticsUtil.getInstance().getStartTimeAndEndTime(cycleType, year, month);
            long startTime = times[0];
            long endTime = times[1];

            //start:获取所有地区的下单量的统计值
            String orderStatus = OrderStatusEnum.COMPLETE.value();
            String payStatus = PayStatusEnum.PAY_YES.value();

            //表名后面拼接年份，如es_sss_order_data_2020
            String tableNameEsSssOrderDataYear = SyncopateUtil.handleSql(searchCriteria.getYear(), TableNameConstant.ES_SSS_ORDER_DATA);

            List<Map<String, Object>> list;

            try{
                //查询今年的表
                list = reportsStatisticsMapper.selectRegionsMemberNumList(tableNameEsSssOrderDataYear, sqlDifference, orderStatus, payStatus, startTime, endTime, sellerId);

                //如果今年的表未查到数据，再查总表
                if(list == null || list.size() <= 0){
                    list = reportsStatisticsMapper.selectRegionsMemberNumList(TableNameConstant.ES_SSS_ORDER_DATA, sqlDifference, orderStatus, payStatus, startTime, endTime, sellerId);
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
            //**end:获取所有地区的下单量的统计值



            List<Map<String, Object>> result = new ArrayList<>();

            for (int i = 0; i < provinceList.size(); i++) {
                Regions regions = provinceList.get(i);
                Map<String, Object> map = new HashMap<>(16);
                map.put("name", regions.getLocalName());
                boolean flag = true;
                for (Map data : list) {
                    if (regions.getId().equals(data.get("ship_province_id"))) {
                        map.put("value", data.get(dataDifference).toString());
                        flag = false;
                    }
                }
                if (flag) {
                    map.put("value", 0);
                }
                result.add(map);
            }

            return result;
        } catch (Exception e) {
            logger.error("区域分析，地图数据异常", e);
            throw new StatisticsException(StatisticsErrorCode.E810.code(), "业务异常");
        }
    }

    /**
     * 购买分析，客单价分布（价格区间内下单量统计）
     *
     * @param searchCriteria 时间相关参数
     * @param ranges         价格区间，只接受整数
     * @return SimpleChart 简单图表数据
     */
    @Override
    public SimpleChart orderDistribution(SearchCriteria searchCriteria, Integer[] ranges) {

        String cycleType = searchCriteria.getCycleType();
        Integer year = searchCriteria.getYear();
        Integer month = searchCriteria.getMonth();
        Long sellerId = UserContext.getSeller().getSellerId();

        SearchCriteria.checkDataParams(cycleType, year, month);

        try {

            // 价格区间升序排序
            Arrays.sort(ranges);

            long[] times = StatisticsUtil.getInstance().getStartTimeAndEndTime(cycleType, year, month);
            long startTime = times[0];
            long endTime = times[1];

            String orderStatus = OrderStatusEnum.COMPLETE.value();
            String payStatus = PayStatusEnum.PAY_YES.value();

            //表名后面拼接年份，如es_sss_order_data_2020
            String tableNameEsSssOrderDataYear = SyncopateUtil.handleSql(searchCriteria.getYear(), TableNameConstant.ES_SSS_ORDER_DATA);

            List<Map<String, Object>> list;

            try{
                //查询今年的表
                list = reportsStatisticsMapper.selectOrderDistributionList(tableNameEsSssOrderDataYear, orderStatus, payStatus, startTime, endTime, sellerId);

                //如果今年的表未查到数据，再查总表
                if(list == null || list.size() <= 0){
                    list = reportsStatisticsMapper.selectOrderDistributionList(TableNameConstant.ES_SSS_ORDER_DATA, orderStatus, payStatus, startTime, endTime, sellerId);
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

            list = StatisticsResultUtil.priceRangeCountResult(list, ranges, "distribution", "num");

            // x轴刻度，数据名称，数据
            String[] xAxis = new String[ranges.length];
            String[] localName = new String[ranges.length];
            String[] data = new String[ranges.length];

            /*
            x轴数组下标   刻度名称   价格区间数组下标   变量变化
            0            0~100      0 1               i i+1
            1            100~200    1 2               i i+1
            2            200~300    2 3               i i+1
            3            300~400    3 4               i i+1
             */
            xAxis[xAxis.length - 1] = ranges[ranges.length - 1] + "+";
            localName[localName.length - 1] = xAxis[xAxis.length - 1];
            for (int i = 0; i < ranges.length - 1; i++) {
                xAxis[i] = ranges[i] + "~" + ranges[i + 1];
                localName[i] = xAxis[i];
                for (Map map : list) {
                    if ((i + 1) == (int) map.get("distribution")) {
                        data[i] = map.get("num").toString();
                        continue;
                    }
                    if (ranges.length == Integer.parseInt(map.get("distribution").toString())) {
                        data[ranges.length - 1] = map.get("num").toString();
                    }
                }
                if (null == (data[i])) {
                    data[i] = "0";
                }
            }
            if (StringUtil.isEmpty(data[ranges.length - 1])) {
                data[ranges.length - 1] = "0";
            }

            ChartSeries chartSeries = new ChartSeries("下单量", data, localName);

            return new SimpleChart(chartSeries, xAxis, new String[0]);

        } catch (Exception e) {
            logger.error("购买分析，客单价分布 异常", e);
            throw new StatisticsException(StatisticsErrorCode.E810.code(), "业务异常");
        }

    }

    /**
     * 购买分析，购买时段分布
     *
     * @param searchCriteria 时间相关参数
     * @return SimpleChart 简单图表数据
     */
    @Override
    public SimpleChart purchasePeriod(SearchCriteria searchCriteria) {

        String cycleType = searchCriteria.getCycleType();
        Integer year = searchCriteria.getYear();
        Integer month = searchCriteria.getMonth();
        Long sellerId = UserContext.getSeller().getSellerId();

        SearchCriteria.checkDataParams(cycleType, year, month);

        try {

            long[] times = StatisticsUtil.getInstance().getStartTimeAndEndTime(cycleType, year, month);
            long startTime = times[0];
            long endTime = times[1];

            String orderStatus = OrderStatusEnum.COMPLETE.value();
            String payStatus = PayStatusEnum.PAY_YES.value();

            //表名后面拼接年份，如es_sss_order_data_2020
            String tableNameEsSssOrderDataYear = SyncopateUtil.handleSql(searchCriteria.getYear(), TableNameConstant.ES_SSS_ORDER_DATA);

            List<Map<String, Object>> list;

            try{
                //查询今年的表
                list = reportsStatisticsMapper.selectPurchasePeriodList(tableNameEsSssOrderDataYear, orderStatus, payStatus, startTime, endTime, sellerId);

                //如果今年的表未查到数据，再查总表
                if(list == null || list.size() <= 0){
                    list = reportsStatisticsMapper.selectPurchasePeriodList(TableNameConstant.ES_SSS_ORDER_DATA, orderStatus, payStatus, startTime, endTime, sellerId);
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

            // 小时数
            int hours = 24;

            String[] xAxis = new String[hours];
            String[] localName = new String[hours];
            String[] data = new String[hours];

            for (int i = 0; i < hours; i++) {
                xAxis[i] = i + "";
                localName[i] = xAxis[i];
                for (Map map : list) {
                    if ((i) == Integer.parseInt(map.get("hour_num").toString())) {
                        data[i] = map.get("num").toString();
                    }
                }
                if (null == data[i]) {
                    data[i] = 0 + "";
                }
            }

            ChartSeries chartSeries = new ChartSeries("下单量", data, localName);

            return new SimpleChart(chartSeries, xAxis, new String[0]);
        } catch (Exception e) {
            logger.error("购买分析，购买时段分布", e);
            throw new StatisticsException(StatisticsErrorCode.E810.code(), "业务异常");
        }
    }

    /**
     * 销售统计，填充数据
     *
     * @param list  数据库数据
     * @param data  x轴数据
     * @param time  x轴刻度长度
     * @param title 获取数据的类型
     */
    private void dataSet(List<Map<String, Object>> list, String[] data, int time, String title) {

        if (null != list && list.size() != 0) {
            for (int i = 0; i < time; i++) {
                for (Map map : list) {
                    if ((i + 1) == Integer.valueOf(map.get("time").toString())) {
                        data[i] = map.get(title).toString();
                    }
                }
                if (null == data[i] || "".equals(data[i])) {
                    data[i] = 0 + "";
                }
            }
        } else {
            for (int i = 0; i < time; i++) {
                data[i] = 0 + "";
            }
        }

    }

    /**
     * 区域分析，所需数据类型判断
     *
     * @param type 所需类型
     * @return String[] sql语句和作为Map的key值的字段名
     */
    private String[] mapDataType(String type) {

        // 区分出区域分析的三种数据，拼接不同的sql字符串，获取不同的字段名
        String sqlDifference = "";
        String dataDifference = "";

        // 1.下单会员数 2.下单金额 3.下单量
        if (RegionsDataType.ORDER_MEMBER_NUM.value().equals(type)) {
            sqlDifference = " COUNT(DISTINCT buyer_id) member ";
            dataDifference = "member";
        } else if (RegionsDataType.ORDER_PRICE.value().equals(type)) {
            sqlDifference = " SUM(order_price) price ";
            dataDifference = "price";
        } else if (RegionsDataType.ORDER_NUM.value().equals(type)) {
            sqlDifference = "COUNT(DISTINCT sn) num";
            dataDifference = "num";
        }

        String[] neededData = new String[2];
        neededData[0] = sqlDifference;
        neededData[1] = dataDifference;

        return neededData;
    }


}
