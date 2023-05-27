package com.enation.app.javashop.service.statistics.impl;

import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.enation.app.javashop.mapper.statistics.GoodsDataMapper;
import com.enation.app.javashop.mapper.statistics.IndustryStatisticsMapper;
import com.enation.app.javashop.model.base.SearchCriteria;
import com.enation.app.javashop.client.goods.GoodsClient;
import com.enation.app.javashop.model.goods.vo.CategoryVO;
import com.enation.app.javashop.model.errorcode.StatisticsErrorCode;
import com.enation.app.javashop.model.statistics.exception.StatisticsException;
import com.enation.app.javashop.model.statistics.vo.ChartSeries;
import com.enation.app.javashop.model.statistics.vo.SimpleChart;
import com.enation.app.javashop.service.statistics.Constant.TableNameConstant;
import com.enation.app.javashop.service.statistics.IndustryStatisticManager;
import com.enation.app.javashop.util.DataDisplayUtil;
import com.enation.app.javashop.service.statistics.util.SyncopateUtil;
import com.enation.app.javashop.model.trade.order.enums.OrderStatusEnum;
import com.enation.app.javashop.model.trade.order.enums.PayStatusEnum;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.StringUtil;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 行业分析实现类
 *
 * @author Chopper
 * @version v1.0
 * @Description:
 * @since v7.0
 * 2018/4/28 下午5:11
 */
@Service
public class IndustryStatisticManagerImpl implements IndustryStatisticManager {

    @Autowired
    private GoodsClient goodsClient;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IndustryStatisticsMapper industryStatisticsMapper;
    
    @Autowired
    private GoodsDataMapper goodsDataMapper;


    @Override
    public SimpleChart getOrderQuantity(SearchCriteria searchCriteria) {
        try {
            searchCriteria = new SearchCriteria(searchCriteria);

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

            List<Map<String, Object>> list;

            try{
                //先查询某一年的表
                list = industryStatisticsMapper.selectOrderQuantityList(tableNameEsSssOrderGoodsDataYear,tableNameEsSssOrderDataYear,
                        startTime, endTime, sellerId);

                //未查到数据，再查总表
                if(list == null || list.size() == 0){
                    list = industryStatisticsMapper.selectOrderQuantityList(TableNameConstant.ES_SSS_ORDER_GOODS_DATA,TableNameConstant.ES_SSS_ORDER_DATA,
                            startTime, endTime, sellerId);
                }
            }catch (BadSqlGrammarException e){
                //某个年份的统计表不存在，则返回空数据
                if (e.getMessage().endsWith("doesn't exist")) {
                    list = new ArrayList<>();
                }else{
                    e.printStackTrace();
                    throw new StatisticsException(StatisticsErrorCode.E810.code(), StatisticsErrorCode.E810.des());
                }

            }

            List<CategoryVO> categoryList = this.goodsClient.listAllChildren(0L);

            String[] data = new String[categoryList.size()];
            String[] name = new String[categoryList.size()];
            int index = 0;

            for (CategoryVO category : categoryList) {
                name[index] = category.getName();
                for (Map<String, Object> map : list) {
                    if (category.getCategoryId().toString().equals(map.get("industry_id").toString())) {
                        data[index] = map.get("order_num").toString();
                    }
                }

                if (StringUtil.isEmpty(data[index])) {
                    data[index] = "0";
                }
                index++;
            }

            ChartSeries chartSeries = new ChartSeries("行业下单统计", data, name);
            return new SimpleChart(chartSeries, new String[0], name);

        } catch (Exception e) {
            logger.error("行业下单统计异常",e);
            throw new StatisticsException(StatisticsErrorCode.E810.code(), StatisticsErrorCode.E810.des());
        }
    }

    @Override
    public SimpleChart getGoodsNum(SearchCriteria searchCriteria) {

        try {

            searchCriteria = new SearchCriteria(searchCriteria);

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

            List<Map<String, Object>> list;

            try{
                //先查询某一年的表
                list = industryStatisticsMapper.selectGoodsNumList(tableNameEsSssOrderGoodsDataYear,tableNameEsSssOrderDataYear,
                                startTime, endTime, sellerId);

                //未查到数据，再查总表
                if(list == null || list.size() == 0){
                    list = industryStatisticsMapper.selectGoodsNumList(TableNameConstant.ES_SSS_ORDER_GOODS_DATA,TableNameConstant.ES_SSS_ORDER_DATA,
                                    startTime, endTime, sellerId);
                }
            }catch (BadSqlGrammarException e){
                //某个年份的统计表不存在，则返回空数据
                if (e.getMessage().endsWith("doesn't exist")) {
                    list = new ArrayList<>();
                }else{
                    e.printStackTrace();
                    throw new StatisticsException(StatisticsErrorCode.E810.code(), StatisticsErrorCode.E810.des());
                }

            }

            List<CategoryVO> categoryList = this.goodsClient.listAllChildren(0L);


            String[] data = new String[categoryList.size()];
            String[] name = new String[categoryList.size()];
            int index = 0;
            for (CategoryVO category : categoryList) {
                name[index] = category.getName();
                for (Map<String, Object> map : list) {
                    if (map.get("industry_id").toString().equals(category.getCategoryId().toString())) {
                        data[index] = map.get("goods_num").toString();
                    }
                }
                if (StringUtil.isEmpty(data[index])) {
                    data[index] = "0";
                }
                index++;
            }

            ChartSeries chartSeries = new ChartSeries("行业下单商品数", data, name);
            return new SimpleChart(chartSeries, new String[0], name);
        } catch (Exception e) {
            logger.error("按分类统计下单商品数量",e);
            throw new StatisticsException(StatisticsErrorCode.E810.code(), StatisticsErrorCode.E810.des());
        }
    }

    @Override
    public SimpleChart getOrderMoney(SearchCriteria searchCriteria) {

        searchCriteria = new SearchCriteria(searchCriteria);
        try {

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

            List<Map<String, Object>> list;

            try{
                //先查询某一年的表
                list = industryStatisticsMapper.selectOrderMoneyList(tableNameEsSssOrderGoodsDataYear,tableNameEsSssOrderDataYear,
                        startTime, endTime, sellerId);

                //未查到数据，再查总表
                if(list == null || list.size() == 0){
                    list = industryStatisticsMapper.selectOrderMoneyList(TableNameConstant.ES_SSS_ORDER_GOODS_DATA,TableNameConstant.ES_SSS_ORDER_DATA,
                            startTime, endTime, sellerId);
                }
            }catch (BadSqlGrammarException e){
                //某个年份的统计表不存在，则返回空数据
                if (e.getMessage().endsWith("doesn't exist")) {
                    list = new ArrayList<>();
                }else{
                    e.printStackTrace();
                    throw new StatisticsException(StatisticsErrorCode.E810.code(), StatisticsErrorCode.E810.des());
                }

            }

            List<CategoryVO> categoryList = this.goodsClient.listAllChildren(0L);


            String[] data = new String[categoryList.size()];
            String[] name = new String[categoryList.size()];
            int index = 0;
            for (CategoryVO category : categoryList) {
                name[index] = category.getName();
                for (Map<String, Object> map : list) {
                    if (map.get("industry_id").toString().equals(category.getCategoryId().toString())) {
                        data[index] = map.get("order_money").toString();
                    }
                }
                if (StringUtil.isEmpty(data[index])) {
                    data[index] = "0";
                }
                index++;
            }
            ChartSeries chartSeries = new ChartSeries("行业下单金额", data, name);
            return new SimpleChart(chartSeries, new String[0], name);
        } catch (Exception e) {
            logger.error("按分类统计下单金额异常",e);
            throw new StatisticsException(StatisticsErrorCode.E810.code(), StatisticsErrorCode.E810.des());
        }
    }

    @Override
    public WebPage getGeneralOverview(SearchCriteria searchCriteria) {

        searchCriteria = new SearchCriteria(searchCriteria);

        try {
            List<CategoryVO> categoryList = this.goodsClient.listAllChildren(searchCriteria.getCategoryId());
            List<Map<String, Object>> result = new ArrayList<>();

            for (CategoryVO category : categoryList) {
                Map<String, Object> m = new HashMap<>(16);
                m.put("category_name", category.getName());
                m.put("industry_id", category.getCategoryId());
                //平均价格
                String avgPrice = industryStatisticsMapper.selectAvgPrice(category.getCategoryId(), searchCriteria.getSellerId());

                m.put("avg_price", StringUtil.toDouble(avgPrice, false));

                //有销量商品数
                String tableNameEsSssOrderGoodsDataYear = SyncopateUtil.handleSql(searchCriteria.getYear(), TableNameConstant.ES_SSS_ORDER_GOODS_DATA);

                String tableNameEsSssOrderDataYear = SyncopateUtil.handleSql(searchCriteria.getYear(), TableNameConstant.ES_SSS_ORDER_DATA);

                Integer soldGoodsNum = industryStatisticsMapper.selectSoldGoodsNum(tableNameEsSssOrderGoodsDataYear, tableNameEsSssOrderDataYear,
                        category.getCategoryId(), searchCriteria.getSellerId(), OrderStatusEnum.COMPLETE.name(), PayStatusEnum.PAY_YES.name());

                m.put("sold_goods_num", soldGoodsNum);

                //总览
                //根据分类路径和商家id查询总数
                Integer totalGoodsNum = new QueryChainWrapper<>(goodsDataMapper)
                        .like("category_path", category.getCategoryId())
                        .eq(searchCriteria.getSellerId() != 0, "seller_id", searchCriteria.getSellerId())
                        .count();

                m.put("goods_total_num", totalGoodsNum);
                m.put("nosales_goods_num", totalGoodsNum - soldGoodsNum);

                //未销售
                Map map = industryStatisticsMapper.selectSoldNum(tableNameEsSssOrderGoodsDataYear, tableNameEsSssOrderDataYear,
                        category.getCategoryId(), searchCriteria.getSellerId());

                m.put("sold_num", map.get("num"));
                try {
                    if (!StringUtil.isEmpty(map.get("price").toString())) {
                        m.put("sales_money", map.get("price"));
                    }
                } catch (Exception e) {
                    m.put("sales_money", 0);
                }
                result.add(m);
            }

            return new WebPage(1L, Integer.valueOf(result.size()).longValue(), 10L, result);
        } catch(BadSqlGrammarException e) {
            //某个年份的统计表不存在，则返回空数据
            if (e.getMessage().endsWith("doesn't exist")) {
                return new WebPage(1L, 0L, 10L, new ArrayList());
            }
            logger.error("概括总览 异常",e);
            throw new StatisticsException(StatisticsErrorCode.E810.code(), StatisticsErrorCode.E810.des());
        }catch (Exception e) {
            logger.error("概括总览 异常",e);
            throw new StatisticsException(StatisticsErrorCode.E810.code(), StatisticsErrorCode.E810.des());
        }
    }

}
