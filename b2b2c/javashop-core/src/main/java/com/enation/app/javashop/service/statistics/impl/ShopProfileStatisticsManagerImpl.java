package com.enation.app.javashop.service.statistics.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.mapper.statistics.OrderDataMapper;
import com.enation.app.javashop.mapper.statistics.ShopProfileStatisticsMapper;
import com.enation.app.javashop.model.statistics.dto.OrderData;
import com.enation.app.javashop.service.statistics.ShopProfileStatisticsManager;
import com.enation.app.javashop.model.errorcode.StatisticsErrorCode;
import com.enation.app.javashop.model.statistics.exception.StatisticsException;
import com.enation.app.javashop.model.statistics.vo.ChartSeries;
import com.enation.app.javashop.model.statistics.vo.ShopProfileVO;
import com.enation.app.javashop.model.statistics.vo.SimpleChart;
import com.enation.app.javashop.model.trade.order.enums.OrderStatusEnum;
import com.enation.app.javashop.model.trade.order.enums.PayStatusEnum;
import com.enation.app.javashop.framework.util.CurrencyUtil;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * 店铺概况管理实现类
 *
 * @author Chopper
 * @version v1.0
 * @since v7.0
 * 2018/3/28 上午9:50
 */
@Service
public class ShopProfileStatisticsManagerImpl implements ShopProfileStatisticsManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ShopProfileStatisticsMapper shopProfileStatisticsMapper;

    @Autowired
    private OrderDataMapper orderDataMapper;

    /**
     * 获取店铺概况
     *
     * @return ShopProfileVO 店铺概况展示VO
     */
    @Override
    public ShopProfileVO data() {
        Long sellerId = UserContext.getSeller().getSellerId();
        try {
            // 近30天起始时间
            long startTime = startOfSomeDay(30);
            long endTime = DateUtil.endOfTodDay();

            String orderStatus = OrderStatusEnum.COMPLETE.value();
            String payStatus = PayStatusEnum.PAY_YES.value();

            // 获取下单金额，下单会员数，下单量，下单商品数
            Map<String, Object> map = shopProfileStatisticsMapper.selectDataList(orderStatus, payStatus, startTime, endTime, sellerId);

            ShopProfileVO shopProfileVO = new ShopProfileVO();
            // 下单金额
            String orderMoney = null == map.get("order_money") ? "0.0" : map.get("order_money").toString();
            shopProfileVO.setOrderMoney(orderMoney);
            // 下单会员数
            String orderMember = null == map.get("order_member") ? "0" : map.get("order_member").toString();
            shopProfileVO.setOrderMember(orderMember);
            // 下单量
            String orderNum = null == map.get("order_num") ? "0" : map.get("order_num").toString();
            shopProfileVO.setOrderNum(orderNum);
            // 下单商品数
            String orderGoods = null == map.get("order_good") ? "0" : map.get("order_good").toString();
            shopProfileVO.setOrderGoods(orderGoods);
            // 平均客单价
            Double averageMemberMoney = 0.0;
            if (!"0".equals(orderMember)) {
                double orderMoneyNum = new Double(orderMoney);
                double orderMemberNum = new Double(orderMember);
                averageMemberMoney = CurrencyUtil.div(orderMoneyNum, orderMemberNum, 2);
            }
            shopProfileVO.setAverageMemberMoney(averageMemberMoney.toString());
            // 商品平均价格
            Double averageGoodsMoney = 0.0;
            if (!"0".equals(orderGoods)) {
                double orderMoneyNum = new Double(orderMoney);
                double orderGoodsNum = new Double(orderGoods);
                averageGoodsMoney = CurrencyUtil.div(orderMoneyNum, orderGoodsNum, 2);
            }
            shopProfileVO.setAverageGoodsMoney(averageGoodsMoney.toString());

            map = shopProfileStatisticsMapper.selectGoodsDataList(sellerId);

            // 店铺商品总数
            String totalGoods = null == map.get("total_goods") ? "0" : map.get("total_goods").toString();
            shopProfileVO.setTotalGoods(totalGoods);
            // 商品收藏总数
            String goodsCollect = null == map.get("goods_collect") ? "0" : map.get("goods_collect").toString();
            shopProfileVO.setGoodsCollect(goodsCollect);


            List<Map<String, Object>> list = shopProfileStatisticsMapper.selectShopDataList(sellerId);

            // 店铺收藏数
            String shopCollect = "0";
            if (null != list && list.size() > 0) {
                map = list.get(0);
                shopCollect = map.get("shop_collect").toString();
            }

            shopProfileVO.setShopCollect(shopCollect);

            ZonedDateTime nowDate = getNowZoneDateTime();

            long startDate = nowDate.plusDays(-29).toEpochSecond();
            long endDate = nowDate.plusHours(23).plusMinutes(59).plusSeconds(59).toEpochSecond();

            //根据订单状态，支付状态，商家id和订单时间查询订单列表
            List<OrderData> orderList = new QueryChainWrapper<>(orderDataMapper)
                    //拼接订单状态查询条件
                    .eq("order_status", OrderStatusEnum.COMPLETE.value())
                    //拼接支付状态查询条件
                    .eq("pay_status", PayStatusEnum.PAY_YES.value())
                    //拼接商家id查询条件
                    .eq("seller_id", sellerId)
                    //拼接订单时间查询条件
                    .between("create_time", startDate, endDate)
                    //列表查询
                    .list();

            //分库分表ShardingSphere无法使用CASE语句，只能全部查出来，再统计出订单最多的那天
            String orderFastigium = getOrderFastigium(orderList);

            shopProfileVO.setOrderFastigium(orderFastigium);

            return shopProfileVO;

        } catch (Exception e) {
            logger.error("获取30天店铺概况展示数据异常", e);
            e.printStackTrace();
            throw new StatisticsException(StatisticsErrorCode.E810.code(), "业务异常");
        }
    }

    /**
     * 获取订单数量最多的那一天
     * @param orderList
     * @return
     */
    private String getOrderFastigium(List<OrderData> orderList) {
        if(orderList == null || orderList.size() == 0){
            return  "暂无";
        }

        Map<String, Integer> dateNumMap = new HashMap<>();//key:日期 value:这一天的订单数
        for(OrderData orderData : orderList){
            Long createTime = orderData.getCreateTime();

            //获取日期
            String dateStr = getDateStr(createTime);

            //该日期订单数+1
            putDateNum(dateNumMap, dateStr);
        }

        //获取订单数最多的日期
        List<Map.Entry<String,Integer>> list = new ArrayList(dateNumMap.entrySet());
        Collections.sort(list, (o1, o2) -> (o2.getValue() - o1.getValue()));//按value降序排列

        return list.get(0).getKey();


    }

    private void putDateNum(Map<String, Integer> dateNumMap, String dateStr) {
        Integer integer = dateNumMap.get(dateStr);
        if(integer == null){
            dateNumMap.put(dateStr, 1);
        }else{
            dateNumMap.put(dateStr, integer + 1);
        }
    }

    /**
     * 获取月-日期格式  如7-15
     * @param createTime
     * @return
     */
    private String getDateStr(Long createTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(createTime * 1000);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return month+"-"+day;
    }

    /**
     * 店铺概况，获取近30天下单金额
     *
     * @return SimpleChart 简单图表数据
     */
    @Override
    public SimpleChart chart() {
        Long sellerId = UserContext.getSeller().getSellerId();

        //查询map集合
        try {

            // 数据名称，与x轴刻度名相同
            String[] localName = new String[30];

            int limitDays = 30;
            for (int i = 0; i < limitDays; i++) {
                Map<String, Object> map = DateUtil.getYearMonthAndDay(i);
                String month = map.get("month").toString();
                String day = map.get("day").toString();
                localName[i] = month + "-" + day;
            }

            //根据订单状态，支付状态，商家id查询每天的订单金额
            QueryWrapper<OrderData> queryWrapper = new QueryWrapper<OrderData>()
                    //查询订单金额总数
                    .select("SUM(order_price) AS money", "FROM_UNIXTIME( create_time, '%c-%e' ) AS time")
                    //拼接订单状态查询条件
                    .eq("order_status", OrderStatusEnum.COMPLETE.value())
                    //拼接支付状态查询条件
                    .eq("pay_status", PayStatusEnum.PAY_YES.value())
                    //拼接商家id查询条件
                    .eq("seller_id", sellerId)
                    //按每天分组
                    .groupBy("time");

            List<Map<String, Object>> list = orderDataMapper.selectMaps(queryWrapper);

            String[] data = new String[30];

            // 循环xAxis，如果与time相同，则将money放入data数组，无数据的数组元素填入0
            for (int i = 0; i < limitDays; i++) {
                for (Map map : list) {
                    if (localName[i].equals(map.get("time").toString())) {
                        data[i] = map.get("money").toString();
                    }
                }
                if (null == data[i]) {
                    data[i] = 0 + "";
                }
            }

            ChartSeries chartSeries = new ChartSeries("下单金额", data, localName);

            return new SimpleChart(chartSeries, localName, new String[0]);
        } catch (Exception e) {
            logger.error("获取30天店铺下单金额统计图数据", e);
            throw new StatisticsException(StatisticsErrorCode.E810.code(), "业务异常");
        }
    }

    private StringBuilder getConditionSql() {

        // 时间分组，同时获取数据名称
        StringBuilder conditionSql = new StringBuilder();
        // 取近30天数据
        int limitDays = 30;
        for (int i = 0; i < limitDays; i++) {
            Map<String, Object> map = DateUtil.getYearMonthAndDay(i);
            String year = map.get("year").toString();
            String month = map.get("month").toString();
            String day = map.get("day").toString();
            String dayDate = year + "-" + month + "-" + day;
            long start = DateUtil.getDateline(dayDate + " 00:00:00", "yyyy-MM-dd HH:mm:ss");
            long end = DateUtil.getDateline(dayDate + " 23:59:59", "yyyy-MM-dd HH:mm:ss");
            conditionSql.append(" when create_time >= ").append(start).append(" and   create_time <=").append(end).append(" then '").append(month).append("-").append(day).append("'");
        }
        conditionSql.append(" else '0' end");

        return conditionSql;
    }

    /**
     * 某天的开始时间
     *
     * @param dayUntilNow 距今多少天以前
     * @return 时间戳
     */
    private static long startOfSomeDay(int dayUntilNow) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DATE, -dayUntilNow);
        Date date = calendar.getTime();
        return date.getTime() / 1000;
    }

    /**
     * 获取当前时区的时间
     *
     * @return
     */
    public static ZonedDateTime getNowZoneDateTime() {
        Instant now = Instant.now();
        ZoneId zoneId = ZoneId.of("Asia/Chongqing");
        ZonedDateTime zdt = ZonedDateTime.ofInstant(now, zoneId);
        return zdt.toLocalDate().atStartOfDay(zoneId);
    }


    public static void main(String[] args) {
        long startTime = startOfSomeDay(30);
        long endTime = DateUtil.startOfTodDay();
        System.out.println(startTime);
        System.out.println(endTime);
    }
}
