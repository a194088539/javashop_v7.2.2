package com.enation.app.javashop.service.distribution.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.distribution.DistributionOrderMapper;
import com.enation.app.javashop.model.base.SearchCriteria;
import com.enation.app.javashop.model.distribution.vo.SellerPushVO;
import com.enation.app.javashop.service.distribution.DistributionStatisticManager;
import com.enation.app.javashop.model.statistics.enums.QueryDateType;
import com.enation.app.javashop.model.statistics.vo.ChartSeries;
import com.enation.app.javashop.model.statistics.vo.SimpleChart;
import com.enation.app.javashop.util.DataDisplayUtil;
import com.enation.app.javashop.framework.util.CurrencyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * DistributionStatisticManagerImpl
 *
 * @author Chopper
 * @version v1.0
 * @since v7.0
 * 2018-06-13 上午8:37
 */
@Service
public class DistributionStatisticManagerImpl implements DistributionStatisticManager {

    @Autowired
    private DistributionOrderMapper distributionOrderMapper;

    @Override
    public SimpleChart getOrderMoney(String circle, Long memberId, Integer year, Integer month) {


        SearchCriteria searchCriteria = new SearchCriteria();
        searchCriteria.setMonth(month);
        searchCriteria.setYear(year);
        searchCriteria.setCycleType(circle);

        searchCriteria = new SearchCriteria(searchCriteria);

        long[] timesTramp = DataDisplayUtil.getStartTimeAndEndTime(searchCriteria);

        Integer resultSize = DataDisplayUtil.getResultSize(searchCriteria);

        String circleWhere = "";
        if (Objects.equals(searchCriteria.getCycleType(), QueryDateType.YEAR.name())) {
            circleWhere = "%m";
        } else {
            circleWhere = "%d";
        }

        List<Map<String, Object>> list = distributionOrderMapper.querySumOrderPrice(circleWhere, timesTramp[0], timesTramp[1], memberId);

        String[] xAxis = new String[resultSize],
                data = new String[resultSize];

        for (int i = 0; i < resultSize; i++) {

            data[i] = 0 + "";
            for (Map<String, Object> map : list) {
                try {
                    if (Integer.parseInt(map.get("date").toString()) == (i + 1)) {
                        data[i] = map.get("order_price").toString();
                    }
                } catch (NullPointerException e) {
                }
            }
            xAxis[i] = i + 1 + "";
        }

        ChartSeries chartSeries = new ChartSeries("订单金额统计", data, new String[0]);

        SimpleChart simpleChart = new SimpleChart(chartSeries, xAxis, new String[0]);

        return simpleChart;
    }

    @Override
    public SimpleChart getPushMoney(String circle, Long memberId, Integer year, Integer month) {


        SearchCriteria searchCriteria = new SearchCriteria();
        searchCriteria.setMonth(month);
        searchCriteria.setYear(year);
        searchCriteria.setCycleType(circle);

        searchCriteria = new SearchCriteria(searchCriteria);

        long[] timesTramp = DataDisplayUtil.getStartTimeAndEndTime(searchCriteria);

        Integer resultSize = DataDisplayUtil.getResultSize(searchCriteria);

        String circleWhere = "";
        if (Objects.equals(searchCriteria.getCycleType(), QueryDateType.YEAR.name())) {
            circleWhere = "%m";
        } else {
            circleWhere = "%d";
        }

        List<Map<String, Object>> list = distributionOrderMapper.querySumGrade1Rebate(circleWhere, timesTramp[0], timesTramp[1], memberId);
        List<Map<String, Object>> list2 = distributionOrderMapper.querySumGrade2Rebate(circleWhere, timesTramp[0], timesTramp[1], memberId);

        List<Map<String, Object>> result = new ArrayList<>();

        for (int i = 0; i < resultSize; i++) {
            double finalRebate = 0;
            for (Map<String, Object> map : list) {
                try {
                    if (Integer.parseInt(map.get("date").toString()) == (i + 1)) {
                        finalRebate = CurrencyUtil.add(finalRebate, Double.parseDouble(map.get("grade_rebate").toString()));
                    }
                } catch (NullPointerException e) {
                }
            }
            for (Map<String, Object> map : list2) {
                try {
                    if (Integer.parseInt(map.get("date").toString()) == (i + 1)) {
                        finalRebate = CurrencyUtil.add(finalRebate, Double.parseDouble(map.get("grade_rebate").toString()));
                    }
                } catch (NullPointerException e) {
                }
            }
            Map<String, Object> map = new HashMap<>(16);
            map.put("date", i + 1);
            map.put("grade_rebate", finalRebate);
            result.add(map);
        }

        String[] xAxis = new String[resultSize],
                data = new String[resultSize];

        for (int i = 0; i < resultSize; i++) {

            data[i] = 0 + "";
            for (Map<String, Object> map : result) {
                if (Integer.parseInt(map.get("date").toString()) == (i + 1)) {
                    data[i] = map.get("grade_rebate").toString();
                }
            }
            xAxis[i] = i + 1 + "";
        }

        ChartSeries chartSeries = new ChartSeries("订单提成统计", data, new String[0]);

        SimpleChart simpleChart = new SimpleChart(chartSeries, xAxis, new String[0]);
        return simpleChart;
    }

    @Override
    public SimpleChart getOrderCount(String circle, Long memberId, Integer year, Integer month) {
        SearchCriteria searchCriteria = new SearchCriteria();
        searchCriteria.setMonth(month);
        searchCriteria.setYear(year);
        searchCriteria.setCycleType(circle);

        searchCriteria = new SearchCriteria(searchCriteria);


        long[] timesTramp = DataDisplayUtil.getStartTimeAndEndTime(searchCriteria);

        Integer resultSize = DataDisplayUtil.getResultSize(searchCriteria);

        String circleWhere = "";
        if (Objects.equals(searchCriteria.getCycleType(), QueryDateType.YEAR.name())) {
            circleWhere = "%m";
        } else {
            circleWhere = "%d";
        }

        List<Map<String, Object>> list = distributionOrderMapper.queryCount(circleWhere, timesTramp[0], timesTramp[1], memberId);

        String[] xAxis = new String[resultSize],
                data = new String[resultSize];

        for (int i = 0; i < resultSize; i++) {

            data[i] = 0 + "";
            for (Map<String, Object> map : list) {
                try {
                    if (Integer.parseInt(map.get("date").toString()) == (i + 1)) {
                        data[i] = map.get("count").toString();
                    }
                } catch (NullPointerException e) {
                }
            }
            xAxis[i] = i + 1 + "";
        }

        ChartSeries chartSeries = new ChartSeries("订单数量统计", data, new String[0]);

        SimpleChart simpleChart = new SimpleChart(chartSeries, xAxis, new String[0]);

        return simpleChart;
    }


    @Override
    public WebPage getShopPush(String circle, Integer year, Integer month, Long pageSize, Long pageNo) {

        SearchCriteria searchCriteria = new SearchCriteria();
        searchCriteria.setMonth(month);
        searchCriteria.setYear(year);
        searchCriteria.setCycleType(circle);

        searchCriteria = new SearchCriteria(searchCriteria);
        long[] timesTramp = DataDisplayUtil.getStartTimeAndEndTime(searchCriteria);

        IPage<SellerPushVO> iPage = distributionOrderMapper.queryGradeRebateForPage(new Page<>(pageNo, pageSize),timesTramp[0], timesTramp[1]);
        return PageConvert.convert(iPage);
    }
}
