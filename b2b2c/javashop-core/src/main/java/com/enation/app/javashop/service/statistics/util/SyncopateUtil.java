package com.enation.app.javashop.service.statistics.util;

import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.enation.app.javashop.framework.util.StringUtil;
import com.enation.app.javashop.mapper.statistics.OrderDataMapper;
import com.enation.app.javashop.mapper.statistics.SyncopateTableMapper;

import java.time.LocalDate;
import java.util.*;

/**
 * 年份查询sql工具
 *
 * @author chopper
 * @version v1.0
 * @since v7.2.2
 * 2018-05-02 下午8:23
 */
public class SyncopateUtil {

    /**
     * 需要替换年份的 表名称
     */
    private static String[] table = {
            "es_sss_order_data",
            "es_sss_order_goods_data",
            "es_sss_refund_data",
            "es_sss_shop_pv",
            "es_sss_goods_pv"
    };


    /**
     * sql 处理
     *
     * @param year 搜索年份
     * @param sql  sql
     * @return
     */
    public static String handleSql(Integer year, String sql) {

        if (StringUtil.isEmpty(sql) || year == null) {
            return "";
        }
        sql = sql.toLowerCase();
        //sql处理
        sql = replaceTable(sql, year);
        return sql;
    }


    /**
     * 替换表。
     *
     * @param sql  查询语句
     * @param year 年
     * @return
     */
    private static String replaceTable(String sql, Integer year) {
        for (int i = 0; i < table.length; i++) {
            sql = sql.replaceAll(table[i], table[i] + "_" + year);
        }
        return sql;
    }


    /**
     * 创建对应年份的表
     *
     * @param year
     * @param syncopateTableMapper
     */
    public static void createTable(Integer year, SyncopateTableMapper syncopateTableMapper) {
        Long[] time = getYearTime(year);
        long yearStartTime = time[0];
        long yearEndTime = time[1];

        for(String tableName : table){
            syncopateTableMapper.createTable(year, tableName);

            //查询这一年的数据
            List<Map<String,Object>> list = syncopateTableMapper.selectData(year, tableName, yearStartTime, yearEndTime);

            //如果有需要插入的数据，则插入
            if(list != null && list.size() > 0){
                Map<String, Object> map = parseDataList(list);

                syncopateTableMapper.insertData(year, tableName, (String) map.get("columnNames"), (List<List<Object>>)map.get("dataList"));
            }
        }
    }

    /**
     * 格式化数据
     * @param list 数据库查询出来的数据集合
     * @return Map(columnNames：所有字段，如id,name,age  dataList：格式化后的数据集合,每个元素是一条记录的字段值集合)
     */
    private static Map<String, Object> parseDataList(List<Map<String,Object>> list) {
        Map<String, Object> result = new HashMap<>();
        if(list == null || list.size() == 0){
            return null;
        }

        //获取所有字段
        Set<String> columnNameSet = new HashSet<>();
        for(Map<String,Object> map : list){
            columnNameSet.addAll(map.keySet());
        }

        //将字段放入list有序排列
        List<String> columnNameList = new ArrayList<>(columnNameSet);
        //例如  id,name,age
        String columnNames = String.join(",", columnNameList);

        //数据集合，集合中的元素是一条数据的字段集合
        List<List<Object>> dataList = new ArrayList<>();
        for(Map<String,Object> map : list){
            List<Object> columnValueList = new ArrayList();

            for(String columnName : columnNameList){
                columnValueList.add(map.get(columnName));
            }

            dataList.add(columnValueList);
        }

        result.put("columnNames", columnNames);
        result.put("dataList", dataList);
        return result;
    }

    /**
     * 切分表
     *
     * @param year
     * @param syncopateTableMapper
     */
    public static void syncopateTable(Integer year, SyncopateTableMapper syncopateTableMapper) {
        //删除表
        drop(year, syncopateTableMapper);
        //创建表
        createTable(year, syncopateTableMapper);
    }

    /**
     * 数据全局初始化
     *
     * @param syncopateTableMapper
     */
    public static void init(SyncopateTableMapper syncopateTableMapper, OrderDataMapper orderDataMapper) {
        for (int i = 2015; i < LocalDate.now().getYear(); i++) {
            drop(i, syncopateTableMapper);
            Long[] year = getYearTime(i);
            //查询这一年的数据
            Integer count = new QueryChainWrapper<>(orderDataMapper).gt("create_time", year[0]).lt("create_time", year[1]).count();
            //如果有数据则初始化
            if (count > 0) {
                SyncopateUtil.createTable(i, syncopateTableMapper);
            }
        }
    }

    /**
     * 创建当前
     *
     * @param syncopateTableMapper
     */
    public static void createCurrentTable(SyncopateTableMapper syncopateTableMapper) {
        Integer year = LocalDate.now().getYear();
        syncopateTable(year, syncopateTableMapper);
    }

    /**
     * 删除表
     *
     * @param year
     * @param syncopateTableMapper
     */
    private static void drop(Integer year, SyncopateTableMapper syncopateTableMapper) {
        for(String tableName : table){
            syncopateTableMapper.dropTable(year, tableName);
        }

    }

    /**
     * 获取某年开始结束时间
     *
     * @return
     */
    private static Long[] getYearTime(Integer year) {
        Calendar firstCal = Calendar.getInstance();
        firstCal.set(Calendar.YEAR, year - 1);
        firstCal.set(Calendar.MONTH, Calendar.DECEMBER);
        firstCal.set(Calendar.DATE, 31);

        Calendar lastCal = Calendar.getInstance();
        lastCal.set(Calendar.YEAR, year);
        lastCal.set(Calendar.MONTH, Calendar.DECEMBER);
        lastCal.set(Calendar.DATE, 31);

        Long[] yearTime = new Long[2];
        yearTime[0] = firstCal.getTime().getTime() / 1000;
        yearTime[1] = lastCal.getTime().getTime() / 1000;

        return yearTime;
    }

}
