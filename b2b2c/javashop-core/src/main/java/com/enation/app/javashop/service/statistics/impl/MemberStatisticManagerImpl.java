package com.enation.app.javashop.service.statistics.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.statistics.MemberRegisterMapper;
import com.enation.app.javashop.mapper.statistics.MemberStatisticMapper;
import com.enation.app.javashop.model.base.SearchCriteria;
import com.enation.app.javashop.model.statistics.dto.MemberRegisterData;
import com.enation.app.javashop.service.statistics.MemberStatisticManager;
import com.enation.app.javashop.model.errorcode.StatisticsErrorCode;
import com.enation.app.javashop.model.statistics.exception.StatisticsException;
import com.enation.app.javashop.model.statistics.enums.QueryDateType;
import com.enation.app.javashop.model.statistics.vo.ChartSeries;
import com.enation.app.javashop.model.statistics.vo.SimpleChart;
import com.enation.app.javashop.service.statistics.util.ChartUtil;
import com.enation.app.javashop.service.statistics.util.StatisticsResultUtil;
import com.enation.app.javashop.util.DataDisplayUtil;
import com.enation.app.javashop.model.trade.order.enums.OrderStatusEnum;
import com.enation.app.javashop.model.trade.order.enums.PayStatusEnum;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

/**
 * 会员统计 实现类
 *
 * @author Chopper
 * @version v1.0
 * @Description:
 * @since v7.0
 * 2018/4/28 下午5:11
 */
@Service
public class MemberStatisticManagerImpl implements MemberStatisticManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MemberRegisterMapper memberRegisterMapper;

    @Autowired
    private MemberStatisticMapper memberStatisticMapper;

    @Override
    public SimpleChart getIncreaseMember(SearchCriteria searchCriteria) {
        //参数校验
        searchCriteria = new SearchCriteria(searchCriteria);
        try {
            //获取结果数量
            Integer resultSize = DataDisplayUtil.getResultSize(searchCriteria);

            WebPage page = this.getIncreaseMemberPage(searchCriteria);
            List<Map<String, Object>> result = page.getData();

            String[] nowData = new String[resultSize];

            //生成图标数据
            int i = 0;
            for (Map<String, Object> mrd : result) {
                nowData[i] = mrd.get("num").toString();
                i++;
            }
            ChartSeries chartSeries = new ChartSeries("新增会员数量", nowData);
            return new SimpleChart(chartSeries, ChartUtil.structureXAxis(searchCriteria.getCycleType(), searchCriteria.getYear(), searchCriteria.getMonth()), new String[0]);
        } catch (Exception e) {
            logger.error("新增会员统计异常", e);
            throw new StatisticsException(StatisticsErrorCode.E810.code(), StatisticsErrorCode.E810.des());
        }
    }


    @Override
    public WebPage getIncreaseMemberPage(SearchCriteria searchCriteria) {
        //参数校验
        searchCriteria = new SearchCriteria(searchCriteria);
        try {

            //获取结果数量
            Integer resultSize = DataDisplayUtil.getResultSize(searchCriteria);
            //获取结果数量
            long[] timestamp = DataDisplayUtil.getStartTimeAndEndTime(searchCriteria);
            long[] lasttimestamp = DataDisplayUtil.getLastStartTimeAndEndTime(searchCriteria);

            //判断sql日期分组条件
            String circle;
            if (Objects.equals(searchCriteria.getCycleType(), QueryDateType.YEAR.name())) {
                circle = "%m";
            } else {
                circle = "%d";
            }

            //根据会员注册时间查询会员
            QueryWrapper<MemberRegisterData> queryWrapper = new QueryWrapper<MemberRegisterData>()
                    //查询会员id，注册时间
                    .select("member_id AS num", "create_time", "FROM_UNIXTIME(create_time, '" + circle + "') AS time")
                    //拼接注册时间大于开始时间查询条件
                    .ge("create_time", timestamp[0])
                    //拼接注册时间小于结束时间查询条件
                    .le("create_time", timestamp[1]);

            List<Map<String, Object>> list = memberRegisterMapper.selectMaps(queryWrapper);

            list = StatisticsResultUtil.resultGroupCounting(list, "time", "num");

            //根据会员注册时间查询会员
            QueryWrapper<MemberRegisterData> queryWrapperLast = new QueryWrapper<MemberRegisterData>()
                    //查询会员id，注册时间
                    .select("member_id AS num", "create_time", "FROM_UNIXTIME(create_time, '" + circle + "') AS time")
                    //拼接注册时间大于开始时间查询条件
                    .gt("create_time", lasttimestamp[0])
                    //拼接注册时间小于结束时间查询条件
                    .lt("create_time", lasttimestamp[1]);

            List<Map<String, Object>> lastList = memberRegisterMapper.selectMaps(queryWrapperLast);

            lastList = StatisticsResultUtil.resultGroupCounting(lastList, "time", "num");

            DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
            //将上周期的数据放到map中，以time为key
            Map lastMap = new HashMap();
            for (Map<String, Object> mrd2 : lastList) {

                lastMap.put(Integer.parseInt(mrd2.get("time").toString()), mrd2.get("num"));
            }

            //生成对比数据
            Map<Integer, Map<String, Object>> map = new HashMap();
            for (Map<String, Object> mrd : list) {
                Integer key = Integer.parseInt(mrd.get("time").toString());
                if (lastMap.get(key) != null) {
                    double value = Double.parseDouble(mrd.get("num").toString());
                    double lastValue = Double.parseDouble(lastMap.get(key).toString());
                    double num = ((value - lastValue) / lastValue) * 100;
                    String result = df.format(num) + "%";
                    mrd.put("last_num", lastMap.get(key) == null ? 0 : lastMap.get(key));
                    mrd.put("growth", result);
                } else {
                    mrd.put("last_num", lastMap.get(key) == null ? 0 : lastMap.get(key));
                    mrd.put("growth", 0);
                }
                mrd.put("time", Integer.parseInt(mrd.get("time").toString()));
                map.put(key, mrd);
            }
            //最终结果 数据填充
            List<Map<String, Object>> result = new ArrayList<>();

            for (int i = 1; i <= resultSize; i++) {
                Map<String, Object> mrd = null;

                if (map.get(i) == null) {
                    mrd = new HashMap<>();
                    mrd.put("time", i);
                    mrd.put("num", 0);
                    mrd.put("last_num", lastMap.get(i) == null ? 0 : lastMap.get(i));
                    if (lastMap.get(i) == null) {

                        mrd.put("growth", 0);
                    } else {
                        double num = ((0 - Double.parseDouble(lastMap.get(i).toString())) / Double.parseDouble(lastMap.get(i).toString())) * 100;
                        mrd.put("growth", df.format(num) + "%");
                    }
                } else {
                    mrd = map.get(i);
                }
                result.add(mrd);
            }

            return new WebPage(1L, resultSize.longValue(), resultSize.longValue(), result);
        } catch (Exception e) {
            logger.error("新增会员统计 page 异常", e);
            throw new StatisticsException(StatisticsErrorCode.E810.code(), StatisticsErrorCode.E810.des());
        }
    }

    @Override
    public SimpleChart getMemberOrderQuantity(SearchCriteria searchCriteria) {

        searchCriteria = new SearchCriteria(searchCriteria);

        try {
            WebPage page = this.getMemberOrderQuantityPage(searchCriteria);
            List<Map<String, Object>> list = page.getData();
            int dataLength = 10;
            String[] xAxis = new String[dataLength], localName = new String[dataLength], data = new String[dataLength];

            for (int i = 0; i < dataLength; i++) {
                if (null != list && i < list.size()) {
                    Map map = list.get(i);
                    data[i] = map.get("order_num").toString();
                    localName[i] = map.get("member_name").toString();
                } else {
                    data[i] = "0";
                    localName[i] = "无";
                }
                xAxis[i] = i + 1 + "";
            }

            ChartSeries chartSeries = new ChartSeries("会员下单量", data, localName);

            return new SimpleChart(chartSeries, xAxis, new String[0]);
        } catch (Exception e) {
            logger.error("下单量异常", e);
            throw new StatisticsException(StatisticsErrorCode.E810.code(), StatisticsErrorCode.E810.des());
        }
    }


    @Override
    public WebPage getMemberOrderQuantityPage(SearchCriteria searchCriteria) {

        searchCriteria = new SearchCriteria(searchCriteria);

        try {
            long[] timestamp = DataDisplayUtil.getStartTimeAndEndTime(searchCriteria);

            //开始时间
            long startTime = timestamp[0];

            //结束时间
            long endTime = timestamp[1];

            //订单状态：已完成
            String orderStatus = OrderStatusEnum.COMPLETE.name();

            //订单状态：已付款
            String payStatus = PayStatusEnum.PAY_YES.name();

            //商家id
            long sellerId = searchCriteria.getSellerId();

            IPage iPage = memberStatisticMapper.selectMemberOrderQuantityPage(new Page(1,10), startTime, endTime, orderStatus, payStatus, sellerId);

            return PageConvert.convert(iPage);
        } catch (Exception e) {
            logger.error("下单量 page异常", e);
            throw new StatisticsException(StatisticsErrorCode.E810.code(), StatisticsErrorCode.E810.des());
        }
    }

    @Override
    public SimpleChart getMemberGoodsNum(SearchCriteria searchCriteria) {
        searchCriteria = new SearchCriteria(searchCriteria);
        try {

            WebPage page = this.getMemberGoodsNumPage(searchCriteria);
            List<Map<String, Object>> list = page.getData();

            int dataLength = 10;
            String[] xAxis = new String[dataLength], localName = new String[dataLength], data = new String[dataLength];

            for (int i = 0; i < dataLength; i++) {
                if (null != list && i < list.size()) {
                    Map map = list.get(i);
                    data[i] = map.get("goods_num").toString();
                    localName[i] = map.get("member_name").toString();
                } else {
                    data[i] = "0";
                    localName[i] = "无";
                }
                xAxis[i] = i + 1 + "";
            }

            ChartSeries chartSeries = new ChartSeries("会员下单商品数", data, localName);

            return new SimpleChart(chartSeries, xAxis, new String[0]);
        } catch (Exception e) {
            logger.error("下单商品数异常", e);
            throw new StatisticsException(StatisticsErrorCode.E810.code(), StatisticsErrorCode.E810.des());
        }
    }


    @Override
    public WebPage getMemberGoodsNumPage(SearchCriteria searchCriteria) {
        searchCriteria = new SearchCriteria(searchCriteria);
        try {
            long[] timestamp = DataDisplayUtil.getStartTimeAndEndTime(searchCriteria);

            //开始时间
            long startTime = timestamp[0];

            //结束时间
            long endTime = timestamp[1];

            //订单状态：已完成
            String orderStatus = OrderStatusEnum.COMPLETE.name();

            //订单状态：已付款
            String payStatus = PayStatusEnum.PAY_YES.name();

            //商家id
            long sellerId = searchCriteria.getSellerId();

            IPage iPage = memberStatisticMapper.selectMemberGoodsNumPage(new Page(1,10), startTime, endTime, orderStatus, payStatus, sellerId);

            return PageConvert.convert(iPage);
        } catch (Exception e) {
            logger.error("下单商品数 page异常", e);
            throw new StatisticsException(StatisticsErrorCode.E810.code(), StatisticsErrorCode.E810.des());
        }
    }

    @Override
    public SimpleChart getMemberMoney(SearchCriteria searchCriteria) {
        searchCriteria = new SearchCriteria(searchCriteria);
        try {
            WebPage page = this.getMemberMoneyPage(searchCriteria);
            List<Map<String, Object>> list = page.getData();
            int dataLength = 10;
            String[] xAxis = new String[dataLength], localName = new String[dataLength], data = new String[dataLength];

            for (int i = 0; i < dataLength; i++) {
                if (null != list && i < list.size()) {
                    Map map = list.get(i);
                    data[i] = map.get("total_money").toString();
                    localName[i] = map.get("member_name").toString();
                } else {
                    data[i] = "0";
                    localName[i] = "无";
                }
                xAxis[i] = i + 1 + "";
            }

            ChartSeries chartSeries = new ChartSeries("会员下单金额", data, localName);

            return new SimpleChart(chartSeries, xAxis, new String[0]);
        } catch (StatisticsException e) {
            throw e;
        } catch (Exception e) {
            logger.error("下单金额异常", e);
            throw new StatisticsException(StatisticsErrorCode.E810.code(), StatisticsErrorCode.E810.des());
        }
    }

    @Override
    public WebPage getMemberMoneyPage(SearchCriteria searchCriteria) {
        searchCriteria = new SearchCriteria(searchCriteria);
        try {

            long[] timestamp = DataDisplayUtil.getStartTimeAndEndTime(searchCriteria);

            //开始时间
            long startTime = timestamp[0];

            //结束时间
            long endTime = timestamp[1];

            //订单状态：已完成
            String orderStatus = OrderStatusEnum.COMPLETE.name();

            //订单状态：已付款
            String payStatus = PayStatusEnum.PAY_YES.name();

            //商家id
            long sellerId = searchCriteria.getSellerId();

            IPage iPage = memberStatisticMapper.selectMemberMoneyPage(new Page(1,10), startTime, endTime, orderStatus, payStatus, sellerId);

            return PageConvert.convert(iPage);
        } catch (Exception e) {
            logger.error("下单金额 page异常", e);
            throw new StatisticsException(StatisticsErrorCode.E810.code(), StatisticsErrorCode.E810.des());
        }

    }
}
