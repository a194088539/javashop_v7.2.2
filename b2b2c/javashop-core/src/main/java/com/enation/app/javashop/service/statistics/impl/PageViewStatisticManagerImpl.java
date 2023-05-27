package com.enation.app.javashop.service.statistics.impl;

import com.enation.app.javashop.mapper.statistics.PageViewStatisticMapper;
import com.enation.app.javashop.model.base.SearchCriteria;
import com.enation.app.javashop.model.errorcode.StatisticsErrorCode;
import com.enation.app.javashop.model.statistics.exception.StatisticsException;
import com.enation.app.javashop.model.statistics.enums.QueryDateType;
import com.enation.app.javashop.model.statistics.vo.ChartSeries;
import com.enation.app.javashop.model.statistics.vo.SimpleChart;
import com.enation.app.javashop.service.statistics.Constant.TableNameConstant;
import com.enation.app.javashop.service.statistics.PageViewStatisticManager;
import com.enation.app.javashop.service.statistics.util.ChartUtil;
import com.enation.app.javashop.service.statistics.util.SyncopateUtil;
import com.enation.app.javashop.util.DataDisplayUtil;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 平台后台 流量分析
 *
 * @author mengyuanming
 * @version 2.0
 * @since 7.0 2018年3月19日上午9:35:06
 */
@Service
public class PageViewStatisticManagerImpl implements PageViewStatisticManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private PageViewStatisticMapper pageViewStatisticMapper;

    /**
     * 统计店铺访问量
     *
     * @param searchCriteria 店铺流量参数
     * @return SimpleChart 简单图表数据
     */
    @Override
    public SimpleChart countShop(SearchCriteria searchCriteria) {

        searchCriteria = new SearchCriteria(searchCriteria);
        try {

            // 获取参数，便于使用，年份和月份有默认值
            String type = searchCriteria.getCycleType();
            searchCriteria = new SearchCriteria(searchCriteria);
            Integer year = searchCriteria.getYear();
            Integer month = searchCriteria.getMonth();
            Long sellerId = searchCriteria.getSellerId();

            // 获取结果大小
            Integer resultSize = DataDisplayUtil.getResultSize(type, year, month);

            //表名后面拼接年份，如es_sss_shop_pv_2020
            String tableNameEsSssShopPvYear = SyncopateUtil.handleSql(searchCriteria.getYear(), TableNameConstant.ES_SSS_SHOP_PV);

            List<Map<String, Object>> list;

            try{
                //查询今年的表
                list = pageViewStatisticMapper.selectVsNum(tableNameEsSssShopPvYear, type, year, month, sellerId);

                //如果今年的表未查到数据，再查总表
                if(list == null || list.size() == 0){
                    list = pageViewStatisticMapper.selectVsNum(TableNameConstant.ES_SSS_SHOP_PV, type, year, month, sellerId);
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


            // 获取x轴数据各区段数据
            String[] data = new String[resultSize];

            String[] localName = new String[resultSize];

            // 长度为日期的循环
            for (int i = 0; i < resultSize; i++) {
                // 遍历list
                for (Map map : list) {
                    // 如果按年查询，获取vs_month字段，如果按月查询，获取vs_day字段
                    if (QueryDateType.YEAR.value().equals(type)) {
                        // 判断月份是否与下标相等，是插入数据库数据，否则插入0
                        if ((i + 1 + "").equals(map.get("vs_month").toString())) {
                            data[i] = map.get("num").toString();
                        }
                    } else {
                        // 判断日期是否与下标相等，是插入数据库数据，否则插入0
                        if ((i + 1 + "").equals(map.get("vs_day").toString())) {
                            data[i] = map.get("num").toString();
                        }
                    }
                }
                // 如果未找到合适数据，则填入0
                if (null == data[i]) {
                    data[i] = "0";
                }
                localName[i] = i + 1 + "";
            }


            ChartSeries series = new ChartSeries("访问量", data, localName);

            return new SimpleChart(series, ChartUtil.structureXAxis(type, year, month), new String[0]);

        } catch (Exception e) {
            logger.error("统计店铺访问量",e);
            throw new StatisticsException(StatisticsErrorCode.E810.code(), "业务处理异常");
        }

    }

    /**
     * 统计商品访问量
     *
     * @param searchCriteria 时间参数
     * @return SimpleChart 简单图表数据
     */
    @Override
    public SimpleChart countGoods(SearchCriteria searchCriteria) {

        searchCriteria = new SearchCriteria(searchCriteria);
        // 获取参数，便于使用
        String type = searchCriteria.getCycleType();
        Long sellerId = searchCriteria.getSellerId();
        Integer year = searchCriteria.getYear();
        Integer month = searchCriteria.getMonth();

        //表名后面拼接年份，如es_sss_goods_pv_2020
        String tableNameEsSssGoodsPvYear = SyncopateUtil.handleSql(searchCriteria.getYear(), TableNameConstant.ES_SSS_GOODS_PV);

        List<Map<String, Object>> list;

        try{
            //查询今年的表
            list = pageViewStatisticMapper.countGoods(tableNameEsSssGoodsPvYear, type, year, month, sellerId);

            //如果今年的表未查到数据，再查总表
            if(list == null || list.size() == 0){
                list = pageViewStatisticMapper.countGoods(TableNameConstant.ES_SSS_GOODS_PV, type, year, month, sellerId);
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

        int dataLength = 30;

        // 获取x轴数据，包括商品名和访问量
        String[] data = new String[dataLength];
        String[] goodsName = new String[dataLength];
        // 获取x轴刻度
        String[] xAxis = new String[dataLength];
        // 赋值
        for (int i = 0; i < dataLength; i++) {
            if (null != list && i < list.size()) {
                Map map = list.get(i);
                data[i] = map.get("num").toString();
                goodsName[i] = map.get("goods_name").toString();
            } else {
                data[i] = "无";
                goodsName[i] = "无";
            }
            xAxis[i] = i + 1 + "";
        }

        ChartSeries series = new ChartSeries("访问量", data, goodsName);

        return new SimpleChart(series, xAxis, new String[0]);

    }
}
