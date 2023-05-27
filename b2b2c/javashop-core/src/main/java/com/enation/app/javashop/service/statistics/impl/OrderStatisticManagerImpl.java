package com.enation.app.javashop.service.statistics.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.statistics.MemberRegisterMapper;
import com.enation.app.javashop.mapper.statistics.OrderDataMapper;
import com.enation.app.javashop.mapper.statistics.OrderStatisticsMapper;
import com.enation.app.javashop.model.base.SearchCriteria;
import com.enation.app.javashop.client.system.RegionsClient;
import com.enation.app.javashop.model.errorcode.StatisticsErrorCode;
import com.enation.app.javashop.model.statistics.dto.OrderData;
import com.enation.app.javashop.model.statistics.exception.StatisticsException;
import com.enation.app.javashop.model.statistics.enums.QueryDateType;
import com.enation.app.javashop.model.statistics.vo.*;
import com.enation.app.javashop.service.statistics.Constant.TableNameConstant;
import com.enation.app.javashop.service.statistics.OrderStatisticManager;
import com.enation.app.javashop.service.statistics.util.StatisticsResultUtil;
import com.enation.app.javashop.util.DataDisplayUtil;
import com.enation.app.javashop.service.statistics.util.SyncopateUtil;
import com.enation.app.javashop.model.system.dos.Regions;
import com.enation.app.javashop.framework.util.CurrencyUtil;
import com.enation.app.javashop.framework.util.StringUtil;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

/**
 * 订单统计实现类
 *
 * @author Chopper
 * @version v1.0
 * @Description:
 * @since v7.0
 * 2018/4/28 下午5:11
 */
@Service
public class OrderStatisticManagerImpl implements OrderStatisticManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RegionsClient regionsClient;

    @Autowired
    private OrderDataMapper orderDataMapper;

    @Autowired
    private OrderStatisticsMapper orderStatisticsMapper;

    @Autowired
    private MemberRegisterMapper memberRegisterMapper;


    @Override
    public MultipleChart getOrderMoney(SearchCriteria searchCriteria, String orderStatus) {
        searchCriteria = new SearchCriteria(searchCriteria);

        try {
            long[] timestamp = DataDisplayUtil.getStartTimeAndEndTime(searchCriteria);
            long[] lastTimestamp = DataDisplayUtil.getLastStartTimeAndEndTime(searchCriteria);
            Long sellerId = searchCriteria.getSellerId();
            String circle = "";
            if (Objects.equals(searchCriteria.getCycleType(), QueryDateType.YEAR.name())) {
                circle = "%m";
            } else if (Objects.equals(searchCriteria.getCycleType(), QueryDateType.MONTH.name())) {
                circle = "%d";
            }

            //根据订单时间，订单状态，商家id查询订单
            QueryWrapper<OrderData> queryWrapper = new QueryWrapper<OrderData>()
                    //查询订单编号，金额，创建时间
                    .select("sn", "order_price AS order_money", "create_time", "FROM_UNIXTIME(create_time, '" + circle + "') AS e_create_time")
                    //创建时间大于等于开始时间
                    .ge("create_time", timestamp[0])
                    //创建时间小于等于开始时间
                    .le("create_time", timestamp[1])
                    //如果订单状态不为空，拼接订单状态查询条件
                    .eq(StringUtil.notEmpty(orderStatus), "order_status", orderStatus)
                    //如果商家id不为空且不为0，拼接商家id查询条件
                    .eq(sellerId != null && sellerId != 0, "seller_id", sellerId);
            List<Map<String, Object>> list = orderDataMapper.selectMaps(queryWrapper);
            list = StatisticsResultUtil.resultGroupSumDouble(list, "e_create_time", "order_money");

            //根据订单时间，订单状态，商家id查询上一周期订单
            QueryWrapper<OrderData> queryWrapperLast = new QueryWrapper<OrderData>()
                    //查询订单编号，金额，创建时间
                    .select("sn", "order_price AS order_money", "create_time", "FROM_UNIXTIME(create_time, '" + circle + "') AS e_create_time")
                    //创建时间大于等于开始时间
                    .ge("create_time", lastTimestamp[0])
                    //创建时间小于等于开始时间
                    .le("create_time", lastTimestamp[1])
                    //如果订单状态不为空，拼接订单状态查询条件
                    .eq(StringUtil.notEmpty(orderStatus), "order_status", orderStatus)
                    //如果商家id不为空且不为0，拼接商家id查询条件
                    .eq(sellerId != null && sellerId != 0, "seller_id", sellerId);
            List<Map<String, Object>> lastList = orderDataMapper.selectMaps(queryWrapperLast);
            lastList = StatisticsResultUtil.resultGroupSumDouble(lastList, "e_create_time", "order_money");


            String[] data = new String[DataDisplayUtil.getResultSize(searchCriteria)];
            String[] lastData = new String[DataDisplayUtil.getResultSize(searchCriteria)];

            String[] xAxis = new String[DataDisplayUtil.getResultSize(searchCriteria)];


            for (Integer i = 0; i < DataDisplayUtil.getResultSize(searchCriteria); i++) {
                xAxis[i] = (i + 1) + "";
                if (list == null || list.size() == 0) {
                    data[i] = "0";
                } else {
                    for (Map<String, Object> map : list) {
                        if ((i + 1) == StringUtil.toInt(map.get("e_create_time"), false)) {
                            data[i] = map.get("order_money").toString();
                        }
                    }
                    if (StringUtil.isEmpty(data[i])) {
                        data[i] = "0";
                    }
                }

                if (lastList == null || lastList.size() == 0) {
                    lastData[i] = "0";
                } else {
                    for (Map<String, Object> map : lastList) {
                        if ((i + 1) == StringUtil.toInt(map.get("e_create_time"), false)) {
                            lastData[i] = map.get("order_money").toString();
                        }
                    }
                    if (StringUtil.isEmpty(lastData[i])) {
                        lastData[i] = "0";
                    }
                }
            }

            ChartSeries chartSeries = new ChartSeries("本月", data, new String[0]);
            ChartSeries lastChartSeries = new ChartSeries("上月", lastData, new String[0]);

            if (searchCriteria.getCycleType().equals(QueryDateType.YEAR.name())) {
                chartSeries = new ChartSeries("今年", data, new String[0]);
                lastChartSeries = new ChartSeries("去年", lastData, new String[0]);
            }
            List<ChartSeries> chartSeriess = new ArrayList<>();
            chartSeriess.add(chartSeries);
            chartSeriess.add(lastChartSeries);
            return new MultipleChart(chartSeriess, xAxis, new String[0]);
        } catch (Exception e) {
            logger.error("下单数量异常", e);
            throw new StatisticsException(StatisticsErrorCode.E810.code(), StatisticsErrorCode.E810.des());
        }
    }

    @Override
    public WebPage getOrderPage(SearchCriteria searchCriteria, String orderStatus, Long pageNo, Long pageSize) {
        searchCriteria = new SearchCriteria(searchCriteria);

        //店铺id
        Long sellerId = searchCriteria.getSellerId();

        long[] timestamp = DataDisplayUtil.getStartTimeAndEndTime(searchCriteria);

        //开始时间
        long startTime = timestamp[0];

        //结束时间
        long endTime = timestamp[1];

        //表名后面拼接年份，如es_sss_order_data_2020
        String tableNameEsSssOrderDataYear = SyncopateUtil.handleSql(searchCriteria.getYear(), TableNameConstant.ES_SSS_ORDER_DATA);

        IPage iPage;

        try{
            //查询某一年的表
            iPage = orderStatisticsMapper.selectOrderPage(new Page(pageNo,pageSize), tableNameEsSssOrderDataYear,
                        startTime, endTime, sellerId, orderStatus);

            //如果未查到数据再查总表
            if(iPage == null || iPage.getRecords() == null || iPage.getRecords().size() == 0){
                iPage = orderStatisticsMapper.selectOrderPage(new Page(pageNo,pageSize), TableNameConstant.ES_SSS_ORDER_DATA,
                            startTime, endTime, sellerId, orderStatus);
            }

            return PageConvert.convert(iPage);
        }catch (MyBatisSystemException e){//使用Mybatisplus分页查询，如果表不存在会抛MyBatisSystemException异常，而不是BadSqlGrammarException
            //某个年份的统计表不存在，则返回空数据
            return new WebPage(pageNo,0L, pageSize, new ArrayList());
        }
    }


    @Override
    public MultipleChart getOrderNum(SearchCriteria searchCriteria, String orderStatus) {

        searchCriteria = new SearchCriteria(searchCriteria);

        try {

            //店铺id
            Long sellerId = searchCriteria.getSellerId();

            long[] timestamp = DataDisplayUtil.getStartTimeAndEndTime(searchCriteria);
            long[] lastTimestamp = DataDisplayUtil.getLastStartTimeAndEndTime(searchCriteria);

            //开始时间
            long startTime = timestamp[0];
            long lastStartTime = lastTimestamp[0];

            //结束时间
            long endTime = timestamp[1];
            long lastEndTime = lastTimestamp[1];

            String circle = "";
            if (Objects.equals(searchCriteria.getCycleType(), QueryDateType.YEAR.name())) {
                circle = "%m";
            } else if (Objects.equals(searchCriteria.getCycleType(), QueryDateType.MONTH.name())) {
                circle = "%d";
            }

            //表名后面拼接年份，如es_sss_order_data_2020
            String tableNameEsSssOrderDataYear = SyncopateUtil.handleSql(searchCriteria.getYear(), TableNameConstant.ES_SSS_ORDER_DATA);

            List<Map<String, Object>> list;
            List<Map<String, Object>> lastList;

            try{
                //查询某一年的表
                list = orderStatisticsMapper.selectOrderNumList(tableNameEsSssOrderDataYear, startTime, endTime, sellerId, orderStatus, circle);

                //如果未查到数据再查总表
                if(list == null || list.size() == 0){
                    list = orderStatisticsMapper.selectOrderNumList(TableNameConstant.ES_SSS_ORDER_DATA, startTime, endTime, sellerId, orderStatus, circle);
                }


                //查询某一年的表
                lastList = orderStatisticsMapper.selectOrderNumList(tableNameEsSssOrderDataYear, lastStartTime, lastEndTime, sellerId, orderStatus, circle);

                //如果未查到数据再查总表
                if(lastList == null || lastList.size() == 0){
                    lastList = orderStatisticsMapper.selectOrderNumList(TableNameConstant.ES_SSS_ORDER_DATA, lastStartTime, lastEndTime, sellerId, orderStatus, circle);
                }
            }catch (BadSqlGrammarException e){
                //某个年份的统计表不存在，则返回空数据
                if(e.getMessage().endsWith("doesn't exist")){
                    list = new ArrayList<>();
                    lastList = new ArrayList<>();
                }else{
                    e.printStackTrace();
                    throw new StatisticsException(StatisticsErrorCode.E810.code(), StatisticsErrorCode.E810.des());
                }
            }

            //分组计数装换
            list = StatisticsResultUtil.resultGroupCounting(list, "e_create_time", "order_num");

            //分组计数装换
            lastList = StatisticsResultUtil.resultGroupCounting(lastList, "e_create_time", "order_num");


            String[] data = new String[DataDisplayUtil.getResultSize(searchCriteria)];
            String[] lastData = new String[DataDisplayUtil.getResultSize(searchCriteria)];

            String[] xAxis = new String[DataDisplayUtil.getResultSize(searchCriteria)];


            for (int i = 0; i < DataDisplayUtil.getResultSize(searchCriteria); i++) {
                xAxis[i] = (i + 1) + "";
                if (list == null || list.size() == 0) {
                    data[i] = "0";
                } else {
                    for (Map<String, Object> map : list) {
                        if ((i + 1) == StringUtil.toInt(map.get("e_create_time"), false)) {
                            data[i] = map.get("order_num").toString();
                        }
                    }
                    if (StringUtil.isEmpty(data[i])) {
                        data[i] = "0";
                    }
                }

                if (lastList == null || lastList.size() == 0) {
                    lastData[i] = "0";
                } else {
                    for (Map<String, Object> map : lastList) {
                        if ((i + 1) == StringUtil.toInt(map.get("e_create_time"), false)) {
                            lastData[i] = map.get("order_num").toString();
                        }
                    }
                    if (StringUtil.isEmpty(lastData[i])) {
                        lastData[i] = "0";
                    }
                }
            }

            ChartSeries chartSeries = new ChartSeries("本月", data, new String[0]);
            ChartSeries lastChartSeries = new ChartSeries("上月", lastData, new String[0]);

            if (searchCriteria.getCycleType().equals(QueryDateType.YEAR.name())) {
                chartSeries = new ChartSeries("今年", data, new String[0]);
                lastChartSeries = new ChartSeries("去年", lastData, new String[0]);
            }
            List<ChartSeries> chartSeriess = new ArrayList<>();
            chartSeriess.add(chartSeries);
            chartSeriess.add(lastChartSeries);
            return new MultipleChart(chartSeriess, xAxis, new String[0]);
        } catch (Exception e) {
            logger.error("下单数量异常", e);
            throw new StatisticsException(StatisticsErrorCode.E810.code(), StatisticsErrorCode.E810.des());
        }
    }


    @Override
    public WebPage getSalesMoney(SearchCriteria searchCriteria, Long pageNo, Long pageSize) {

        searchCriteria = new SearchCriteria(searchCriteria);
        try {

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
                iPage = orderStatisticsMapper.selectSalesMoneyPage(new Page(pageNo,pageSize), tableNameEsSssOrderGoodsDataYear,
                        tableNameEsSssOrderDataYear, startTime, endTime);

                //如果未查到数据再差总表
                if(iPage == null || iPage.getRecords() == null || iPage.getRecords().size() == 0){
                    iPage = orderStatisticsMapper.selectSalesMoneyPage(new Page(pageNo,pageSize), TableNameConstant.ES_SSS_ORDER_GOODS_DATA,
                            TableNameConstant.ES_SSS_ORDER_DATA, startTime, endTime);
                }

                return PageConvert.convert(iPage);
            }catch (MyBatisSystemException e){//使用Mybatisplus分页查询，如果表不存在会抛MyBatisSystemException异常，而不是BadSqlGrammarException
                //某个年份的统计表不存在，则返回空数据
                return new WebPage(pageNo,0L, pageSize, new ArrayList());
            }
        } catch (Exception e) {
            logger.error("销售收入统计", e);
            throw new StatisticsException(StatisticsErrorCode.E810.code(), StatisticsErrorCode.E810.des());
        }
    }


    @Override
    public WebPage getAfterSalesMoney(SearchCriteria searchCriteria, Long pageNo, Long pageSize) {

        searchCriteria = new SearchCriteria(searchCriteria);

        long[] timestamp = DataDisplayUtil.getStartTimeAndEndTime(searchCriteria);

        //开始时间
        long startTime = timestamp[0];

        //结束时间
        long endTime = timestamp[1];

        //表名后面拼接年份，如es_sss_refund_data_2020
        String tableNameEsSssRefundDataYear = SyncopateUtil.handleSql(searchCriteria.getYear(), TableNameConstant.ES_SSS_REFUND_DATA);

        IPage iPage;

        try{
            //查询某一年的表
            iPage = orderStatisticsMapper.selectAfterSalesMoneyPage(new Page(pageNo,pageSize), tableNameEsSssRefundDataYear, startTime, endTime);

            //如果未查到数据再查总表
            if(iPage == null || iPage.getRecords() == null || iPage.getRecords().size() == 0){
                iPage = orderStatisticsMapper.selectAfterSalesMoneyPage(new Page(pageNo,pageSize), TableNameConstant.ES_SSS_REFUND_DATA, startTime, endTime);
            }

            return PageConvert.convert(iPage);
        }catch (MyBatisSystemException e){//使用Mybatisplus分页查询，如果表不存在会抛MyBatisSystemException异常，而不是BadSqlGrammarException
            //某个年份的统计表不存在，则返回空数据
            return new WebPage(pageNo,0L, pageSize, new ArrayList());
        }
    }

    @Override
    public SalesTotal getSalesMoneyTotal(SearchCriteria searchCriteria) {
        searchCriteria = new SearchCriteria(searchCriteria);

        try {
            long[] timestamp = DataDisplayUtil.getStartTimeAndEndTime(searchCriteria);

            //开始时间
            long startTime = timestamp[0];

            //结束时间
            long endTime = timestamp[1];

            //表名后面拼接年份，如es_sss_order_data_2020
            String tableNameEsSssOrderDataYear = SyncopateUtil.handleSql(searchCriteria.getYear(), TableNameConstant.ES_SSS_ORDER_DATA);

            //表名后面拼接年份，如es_sss_refund_data_2020
            String tableNameEsSssRefundDataYear = SyncopateUtil.handleSql(searchCriteria.getYear(), TableNameConstant.ES_SSS_REFUND_DATA);

            Map map;

            try{
                //查询某一年的表
                map = orderStatisticsMapper.selectSalesMoneyTotal(tableNameEsSssOrderDataYear, tableNameEsSssRefundDataYear, startTime, endTime);

                //如果未查到数据再查总表
                if(map == null || map.size() <= 0 || map.get("receive_money") == null || map.get("refund_money") == null){
                    map = orderStatisticsMapper.selectSalesMoneyTotal(TableNameConstant.ES_SSS_ORDER_DATA, TableNameConstant.ES_SSS_REFUND_DATA, startTime, endTime);
                }
            }catch (BadSqlGrammarException e){
                //某个年份的统计表不存在，则返回空数据
                if(e.getMessage().endsWith("doesn't exist")){
                    map = new HashMap<>();
                }else{
                    e.printStackTrace();
                    throw new StatisticsException(StatisticsErrorCode.E810.code(), StatisticsErrorCode.E810.des());
                }

            }

            SalesTotal salesTotal = new SalesTotal();
            salesTotal.setReceiveMoney(StringUtil.toDouble(map==null?null:map.get("receive_money"), false));
            salesTotal.setRefundMoney(StringUtil.toDouble(map==null?null:map.get("refund_money"), false));
            salesTotal.setRealMoney(CurrencyUtil.sub(salesTotal.getReceiveMoney(), salesTotal.getRefundMoney()));
            return salesTotal;
        } catch (Exception e) {
            logger.error("销售收入总览异常", e);
            throw new StatisticsException(StatisticsErrorCode.E810.code(), StatisticsErrorCode.E810.des());
        }
    }


    @Override
    public MapChartData getOrderRegionMember(SearchCriteria searchCriteria) {
        searchCriteria = new SearchCriteria(searchCriteria);
        try {
            long[] timestamp = DataDisplayUtil.getStartTimeAndEndTime(searchCriteria);

            //开始时间
            long startTime = timestamp[0];

            //结束时间
            long endTime = timestamp[1];

            //表名后面拼接年份，如es_sss_order_data_2020
            String tableNameEsSssOrderDataYear = SyncopateUtil.handleSql(searchCriteria.getYear(), TableNameConstant.ES_SSS_ORDER_DATA);

            //店铺id
            Long sellerId = searchCriteria.getSellerId();

            List<Map<String, Object>> list;

            try{
                //查询某一年的表
                list = orderStatisticsMapper.selectOrderRegionMemberList(tableNameEsSssOrderDataYear, startTime, endTime, sellerId);

                //如果未查到数据再查总表
                if(list == null || list.size() == 0){
                    list = orderStatisticsMapper.selectOrderRegionMemberList(TableNameConstant.ES_SSS_ORDER_DATA, startTime, endTime, sellerId);
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

            List<Regions> regionsList = this.regionsClient.getRegionsChildren(0L);

            String[] name = new String[regionsList.size()];
            String[] data = new String[regionsList.size()];


            int index = 0;
            for (Regions region : regionsList) {
                name[index] = region.getLocalName();
                for (Map<String, Object> map : list) {
                    if (region.getId().equals(map.get("region_id"))) {
                        data[index] = map.get("value").toString();
                    }
                }
                if (StringUtil.isEmpty(data[index])) {
                    data[index] = "0";
                }
                index++;
            }
            return new MapChartData(name, data);
        } catch (Exception e) {
            logger.error("下单会员数异常", e);
            throw new StatisticsException(StatisticsErrorCode.E810.code(), StatisticsErrorCode.E810.des());
        }
    }

    @Override
    public MapChartData getOrderRegionNum(SearchCriteria searchCriteria) {

        searchCriteria = new SearchCriteria(searchCriteria);
        try {
            long[] timestamp = DataDisplayUtil.getStartTimeAndEndTime(searchCriteria);

            //开始时间
            long startTime = timestamp[0];

            //结束时间
            long endTime = timestamp[1];

            //表名后面拼接年份，如es_sss_order_data_2020
            String tableNameEsSssOrderDataYear = SyncopateUtil.handleSql(searchCriteria.getYear(), TableNameConstant.ES_SSS_ORDER_DATA);

            //店铺id
            Long sellerId = searchCriteria.getSellerId();

            List<Map<String, Object>> list;

            try{
                //查询某一年的表
                list = orderStatisticsMapper.selectOrderRegionNumList(tableNameEsSssOrderDataYear, startTime, endTime, sellerId);

                //如果未查到数据再查总表
                if(list == null || list.size() == 0){
                    list = orderStatisticsMapper.selectOrderRegionNumList(TableNameConstant.ES_SSS_ORDER_DATA, startTime, endTime, sellerId);
                }

                if (list == null) {
                    list = new ArrayList<>();
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

            List<Regions> regionsList = this.regionsClient.getRegionsChildren(0L);

            String[] name = new String[regionsList.size()];
            String[] data = new String[regionsList.size()];


            int index = 0;
            for (Regions region : regionsList) {
                name[index] = region.getLocalName();
                for (Map<String, Object> map : list) {

                    if (region.getId().equals(map.get("region_id"))) {
                        data[index] = map.get("value").toString();
                    }
                }
                if (StringUtil.isEmpty(data[index])) {
                    data[index] = "0";
                }
                index++;
            }
            return new MapChartData(name, data);
        } catch (Exception e) {
            logger.error("区域分析=>下单量 异常", e);
            throw new StatisticsException(StatisticsErrorCode.E810.code(), StatisticsErrorCode.E810.des());
        }
    }

    @Override
    public MapChartData getOrderRegionMoney(SearchCriteria searchCriteria) {
        searchCriteria = new SearchCriteria(searchCriteria);


        try {
            long[] timestamp = DataDisplayUtil.getStartTimeAndEndTime(searchCriteria);

            //开始时间
            long startTime = timestamp[0];

            //结束时间
            long endTime = timestamp[1];

            //表名后面拼接年份，如es_sss_order_data_2020
            String tableNameEsSssOrderDataYear = SyncopateUtil.handleSql(searchCriteria.getYear(), TableNameConstant.ES_SSS_ORDER_DATA);

            //店铺id
            Long sellerId = searchCriteria.getSellerId();

            List<Map<String, Object>> list;

            try{
                //查询某一年的表
                list = orderStatisticsMapper.selectOrderRegionMoneyList(tableNameEsSssOrderDataYear, startTime, endTime, sellerId);

                //如果未查到数据再查总表
                if(list == null || list.size() == 0){
                    list = orderStatisticsMapper.selectOrderRegionMoneyList(TableNameConstant.ES_SSS_ORDER_DATA, startTime, endTime, sellerId);
                }

                if (list == null) {
                    list = new ArrayList<>();
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

            List<Regions> regionsList = this.regionsClient.getRegionsChildren(0L);

            String[] name = new String[regionsList.size()];
            String[] data = new String[regionsList.size()];


            int index = 0;
            for (Regions region : regionsList) {
                name[index] = region.getLocalName();
                for (Map<String, Object> map : list) {

                    if (region.getId().equals(map.get("region_id"))) {
                        data[index] = map.get("value").toString();
                    }
                }
                if (StringUtil.isEmpty(data[index])) {
                    data[index] = "0";
                }
                index++;
            }
            return new MapChartData(name, data);
        } catch (Exception e) {

            logger.error("域分析=>下单金额 异常", e);
            throw new StatisticsException(StatisticsErrorCode.E810.code(), StatisticsErrorCode.E810.des());
        }
    }

    @Override
    public WebPage getOrderRegionForm(SearchCriteria searchCriteria) {
        searchCriteria = new SearchCriteria(searchCriteria);
        try {
            long[] timestamp = DataDisplayUtil.getStartTimeAndEndTime(searchCriteria);

            //开始时间
            long startTime = timestamp[0];

            //结束时间
            long endTime = timestamp[1];

            //表名后面拼接年份，如es_sss_order_data_2020
            String tableNameEsSssOrderDataYear = SyncopateUtil.handleSql(searchCriteria.getYear(), TableNameConstant.ES_SSS_ORDER_DATA);

            //店铺id
            Long sellerId = searchCriteria.getSellerId();

            List<Map<String, Object>> list;

            try{
                //查询某一年的表
                list = orderStatisticsMapper.selectOrderRegionFormList(tableNameEsSssOrderDataYear, startTime, endTime, sellerId);

                //如果未查到数据再查总表
                if(list == null || list.size() == 0){
                    list = orderStatisticsMapper.selectOrderRegionFormList(TableNameConstant.ES_SSS_ORDER_DATA, startTime, endTime, sellerId);
                }

                if (list == null) {
                    list = new ArrayList<>();
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

            List<Regions> regionsList = this.regionsClient.getRegionsChildren(0L);

            List<Map<String, Object>> result = new ArrayList();
            for (Regions region : regionsList) {
                Map<String, Object> m = new HashMap(16);
                m.put("region_id", region.getId());
                m.put("region_name", region.getLocalName());
                result.add(m);
            }

            for (Map<String, Object> map : result) {
                boolean flag = true;
                for (Map<String, Object> map2 : list) {
                    if (map.get("region_id").equals(map2.get("region_id"))) {
                        map.put("member_num", map2.get("member_num"));
                        map.put("order_price", map2.get("order_price"));
                        map.put("sn_num", map2.get("sn_num"));
                        flag = false;
                    }
                }
                if (flag) {
                    map.put("member_num", 0);
                    map.put("order_price", 0);
                    map.put("sn_num", 0);
                }
            }

            return new WebPage(1L, Integer.valueOf(regionsList.size()).longValue(), Integer.valueOf(regionsList.size()).longValue(), result);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("区域分析表格=>page 异常", e);
            throw new StatisticsException(StatisticsErrorCode.E810.code(), StatisticsErrorCode.E810.des());
        }
    }

    @Override
    public SimpleChart getUnitPrice(SearchCriteria searchCriteria, Integer[] prices) {
        searchCriteria = new SearchCriteria(searchCriteria);

        int notAvailable = 2;
        if (prices == null || prices.length < notAvailable) {
            prices = SearchCriteria.defaultPrice();
        }
        try {

            // 店铺id
            long sellerId = searchCriteria.getSellerId();

            long[] timestamp = DataDisplayUtil.getStartTimeAndEndTime(searchCriteria);

            //开始时间
            long startTime = timestamp[0];

            //结束时间
            long endTime = timestamp[1];

            //表名后面拼接年份，如es_sss_order_goods_data_2020
            String tableNameEsSssOrderGoodsDataYear = SyncopateUtil.handleSql(searchCriteria.getYear(), TableNameConstant.ES_SSS_ORDER_GOODS_DATA);

            //表名后面拼接年份，如es_sss_order_data_2020
            String tableNameEsSssOrderDataYear = SyncopateUtil.handleSql(searchCriteria.getYear(), TableNameConstant.ES_SSS_ORDER_DATA);

            List<Map<String, Object>> dataList;;

            try{
                //查询今年的表
                dataList = orderStatisticsMapper.selectUnitPriceList(tableNameEsSssOrderGoodsDataYear, tableNameEsSssOrderDataYear,
                            startTime, endTime, sellerId);

                //如果今年的表未查到数据，再查总表
                if(dataList == null || dataList.size() == 0){
                    dataList = orderStatisticsMapper.selectUnitPriceList(TableNameConstant.ES_SSS_ORDER_GOODS_DATA, TableNameConstant.ES_SSS_ORDER_DATA,
                                startTime, endTime, sellerId);
                }
            } catch (BadSqlGrammarException e){
                //某个年份的统计表不存在，则返回空数据
                if(e.getMessage().endsWith("doesn't exist")){
                    dataList = new ArrayList<>();
                }else{
                    e.printStackTrace();
                    throw new StatisticsException(StatisticsErrorCode.E810.code(), StatisticsErrorCode.E810.des());
                }
            }

            dataList = StatisticsResultUtil.priceRangeCountResult(dataList, prices, "price_interval", "t_num");

            String[] xAxis = new String[prices.length - 1],
                    localName = new String[prices.length - 1],
                    data = new String[prices.length - 1];

            if (dataList == null) {
                dataList = new ArrayList<>();
            }
            for (int i = 0; i < prices.length - 1; i++) {
                xAxis[i] = prices[i] + "~" + prices[i + 1];
                localName[i] = xAxis[i];
                for (Map map : dataList) {
                    if ((i + 1) == (int) map.get("price_interval")) {
                        data[i] = map.get("t_num").toString();
                    }
                }
                if (StringUtil.isEmpty(data[i])) {
                    data[i] = "0";
                }

            }


            ChartSeries chartSeries = new ChartSeries("下单量", data, localName);
            return new SimpleChart(chartSeries, xAxis, new String[0]);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("客单价分布=>客单价分布 异常", e);
            throw new StatisticsException(StatisticsErrorCode.E810.code(), StatisticsErrorCode.E810.des());
        }
    }

    @Override
    public WebPage getUnitNum() {
        try {
            //查询会员总数
            Double total = new QueryChainWrapper<>(memberRegisterMapper).count().doubleValue();

            List<Map<String, Object>> result = new ArrayList<>();

            for (int i = 1; i <= 10; i++) {
                Map<String, Object> m = new HashMap<>(16);
                m.put("order_num", i);
                m.put("member_num", 0);
                result.add(m);
            }

            //查询某个买家的订单数量
            QueryWrapper<OrderData> queryWrapper = new QueryWrapper<OrderData>()
                    //查询订单数量
                    .select("COUNT(`sn`) AS sn_num")
                    //按买家id分组
                    .groupBy("buyer_id");

            List<Map<String, Object>> firstList = orderDataMapper.selectMaps(queryWrapper);

            String moreNum = firstList.stream().filter(map -> (Long)map.get("sn_num") > 10).count() + "";

            for (Map<String, Object> map : firstList) {
                for (Integer i = 1; i <= 10; i++) {
                    Object a = map.get("sn_num");
                    Integer b = new Integer(a.toString());
                    if (b.equals(i)) {
                        Map<String, Object> m = result.get(i - 1);
                        Object o = m.get("member_num");
                        Integer c = new Integer(o.toString());
                        Integer memberNum = c + 1;
                        m.put("member_num", memberNum);
                        result.set(i - 1, m);
                    }
                }
            }

            Map<String, Object> m = new HashMap<>(16);
            m.put("order_num", "10+");
            m.put("member_num", moreNum);
            result.add(m);


            DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
            for (Map<String, Object> map : result) {
                double value = Double.parseDouble(map.get("member_num").toString());
                double num = (value / total) * 100;
                String percent = df.format(num) + "%";
                map.put("percent", percent);
            }

            return new WebPage(1L, Integer.valueOf(result.size()).longValue(), 10L, result);
        } catch (NumberFormatException e) {

            logger.error("客单价分布=>购买频次分析 异常", e);
            throw new StatisticsException(StatisticsErrorCode.E810.code(), StatisticsErrorCode.E810.des());
        }
    }

    @Override
    public SimpleChart getUnitTime(SearchCriteria searchCriteria) {

        searchCriteria = new SearchCriteria(searchCriteria);
        try {

            long[] timestamp = DataDisplayUtil.getStartTimeAndEndTime(searchCriteria);

            //开始时间
            long startTime = timestamp[0];

            //结束时间
            long endTime = timestamp[1];

            //表名后面拼接年份，如es_sss_order_data_2020
            String tableNameEsSssOrderDataYear = SyncopateUtil.handleSql(searchCriteria.getYear(), TableNameConstant.ES_SSS_ORDER_DATA);

            //店铺id
            Long sellerId = searchCriteria.getSellerId();

            List<Map<String, Object>> dataList;

            try{
                //查询某一年的表
                dataList = orderStatisticsMapper.selectUnitTimeList(tableNameEsSssOrderDataYear, startTime, endTime, sellerId);

                //如果未查到数据再查总表
                if(dataList == null || dataList.size() == 0){
                    dataList = orderStatisticsMapper.selectUnitTimeList(TableNameConstant.ES_SSS_ORDER_DATA, startTime, endTime, sellerId);
                }
            }catch (BadSqlGrammarException e){
                //某个年份的统计表不存在，则返回空数据
                if(e.getMessage().endsWith("doesn't exist")){
                    dataList = new ArrayList<>();
                }else{
                    e.printStackTrace();
                    throw new StatisticsException(StatisticsErrorCode.E810.code(), StatisticsErrorCode.E810.des());
                }
            }





            String[] xAxis = new String[24],
                    data = new String[24];


            if (dataList == null) {
                dataList = new ArrayList<>();
            }

            // 小时数
            int hours = 24;
            int index = 0;
            for (int i = 0; i < hours; i++) {
                xAxis[index] = (i + 1) + "";
                for (Map map : dataList) {

                    if ((i + 1) == StringUtil.toInt(map.get("order_time"), false)) {
                        data[index] = map.get("order_num").toString();
                    }
                }
                if (StringUtil.isEmpty(data[index])) {
                    data[index] = "0";
                }
                index++;
            }

            ChartSeries chartSeries = new ChartSeries("下单量", data, new String[0]);
            return new SimpleChart(chartSeries, xAxis, new String[0]);
        } catch (Exception e) {
            logger.error("客单价分布=>购买时段分析 异常", e);
            throw new StatisticsException(StatisticsErrorCode.E810.code(), StatisticsErrorCode.E810.des());
        }
    }

    @Override
    public SimpleChart getReturnMoney(SearchCriteria searchCriteria) {
        searchCriteria = new SearchCriteria(searchCriteria);
        try {

            long[] timestamp = DataDisplayUtil.getStartTimeAndEndTime(searchCriteria);

            //开始时间
            long startTime = timestamp[0];

            //结束时间
            long endTime = timestamp[1];

            //表名后面拼接年份，如es_sss_order_data_2020
            String tableNameEsSssOrderDataYear = SyncopateUtil.handleSql(searchCriteria.getYear(), TableNameConstant.ES_SSS_ORDER_DATA);

            //表名后面拼接年份，如es_sss_refund_data_2020
            String tableNameEsSssRefundDataYear = SyncopateUtil.handleSql(searchCriteria.getYear(), TableNameConstant.ES_SSS_REFUND_DATA);

            String circle = "";
            if (Objects.equals(searchCriteria.getCycleType(), QueryDateType.YEAR.name())) {
                circle = "%m";
            } else if (Objects.equals(searchCriteria.getCycleType(), QueryDateType.MONTH.name())) {
                circle = "%d";
            }

            Long sellerId = searchCriteria.getSellerId();

            List<Map<String, Object>> dataList;

            try{
                //查询某一年的表
                dataList = orderStatisticsMapper.selectReturnMoneyList(tableNameEsSssRefundDataYear, tableNameEsSssOrderDataYear, startTime, endTime, circle, sellerId);

                //如果未查到数据再查总表
                if(dataList == null || dataList.size() <= 0 ){
                    dataList = orderStatisticsMapper.selectReturnMoneyList(TableNameConstant.ES_SSS_REFUND_DATA, TableNameConstant.ES_SSS_ORDER_DATA, startTime, endTime, circle, sellerId);
                }
            }catch (BadSqlGrammarException e){
                //某个年份的统计表不存在，则返回空数据
                if(e.getMessage().endsWith("doesn't exist")){
                    dataList = new ArrayList<>();
                }else{
                    e.printStackTrace();
                    throw new StatisticsException(StatisticsErrorCode.E810.code(), StatisticsErrorCode.E810.des());
                }

            }

            //退款统计结果集转换
            dataList = StatisticsResultUtil.resultGroupSumDouble(dataList, "refund_time", "refund_price");
            String[] xAxis = new String[DataDisplayUtil.getResultSize(searchCriteria)],
                    data = new String[DataDisplayUtil.getResultSize(searchCriteria)];


            if (dataList == null) {
                dataList = new ArrayList<>();
            }

            int index = 0;
            for (int i = 0; i < DataDisplayUtil.getResultSize(searchCriteria); i++) {
                xAxis[index] = (i + 1) + "";
                for (Map map : dataList) {
                    if ((i + 1) == StringUtil.toInt(map.get("refund_time"), false)) {
                        data[index] = map.get("refund_price").toString();
                    }
                }
                if (StringUtil.isEmpty(data[index])) {
                    data[index] = 0 + "";
                }
                index++;
            }
            ChartSeries chartSeries = new ChartSeries("退款", data, new String[0]);
            return new SimpleChart(chartSeries, xAxis, new String[0]);
        } catch (Exception e) {
            logger.error("退款统计 异常", e);
            throw new StatisticsException(StatisticsErrorCode.E810.code(), StatisticsErrorCode.E810.des());
        }
    }

}
