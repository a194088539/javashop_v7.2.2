package com.enation.app.javashop.service.statistics.util;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

/**
 * @Author gy
 * @Description 统计返回值专用工具类
 * @Date: Created in 22:20 2020/6/18
 */
public class StatisticsResultUtil {


    /**
     * 价格销量统计结果集转换
     *
     * @param list        价格区间结果集
     * @param ranges      价格集合
     * @param groupColumn 分组字段
     * @param sumColumn   求和字段
     * @return
     */
    public static List<Map<String, Object>> priceRangeSumInt(List<Map<String, Object>> list, List<Integer> ranges, String groupColumn, String sumColumn) {

        return list.stream()
                .map(map -> {
                    map.put(groupColumn, priceRange(ranges, Double.valueOf(map.get(groupColumn).toString())));
                    return map;
                }).collect(collectingAndThen(groupingBy(map -> map.get(groupColumn), summingInt(map -> Integer.parseInt(map.get(sumColumn).toString()))),
                        map -> convertMap(map, groupColumn, sumColumn)
                ));
    }


    /**
     * 价格销量统计结果集转换
     *
     * @param list        价格区间结果集
     * @param ranges      价格集合
     * @param groupColumn 分组字段
     * @param countColumn 计数字段
     * @return
     */
    public static List<Map<String, Object>> priceRangeCountResult(List<Map<String, Object>> list, Integer[] ranges, String groupColumn, String countColumn) {
        Map<Integer, Long> collect = list.stream()
                .map(map -> {
                    String price = map.get(groupColumn).toString();
                    return priceRange(ranges, Double.valueOf(price));
                }).collect(groupingBy(Function.identity(), Collectors.counting()));
        return convertMap(collect, groupColumn, countColumn);
    }


    /**
     * 返回规定价格区间的值
     *
     * @param ranges 区间数组
     * @param price  价格
     * @return
     */
    private static String priceRange(List<Integer> ranges, Double price) {
        //从大到小排序
        ranges.sort(Comparator.comparing(Integer::intValue).reversed());
        if (price > ranges.get(0)) {
            return ranges.get(0) + "+";
        }
        for (int i = 0; i < ranges.size(); i++) {
            if (ranges.get(i) >= price && price >= ranges.get(i + 1)) {
                return ranges.get(i + 1) + "~" + ranges.get(i);
            }
            i += 2;
        }
        return "0";
    }


    /**
     * 返回规定价格区间的值
     *
     * @param ranges 区间数组
     * @param price  价格
     * @param <T>
     * @return
     */
    private static <T> int priceRange(Integer[] ranges, Double price) {


        if (ranges.length % 2 == 0) {
            for (int i = 1; i < ranges.length; i++) {
                if (price >= ranges[i - 1] && price <= ranges[i]) {
                    return i;
                }
            }
        } else {
            if (price > Arrays.stream(ranges).max(Integer::compareTo).get()) {
                return ranges.length;
            }
            for (int i = 1; i < ranges.length; i++) {
                if (price >= ranges[i - 1] && price <= ranges[i]) {
                    return i;
                }
            }
        }

        return 0;
    }


    /**
     * 分组累加结果转换
     *
     * @param list        结果集合
     * @param groupColumn 分组的列名
     * @param sumColumn   求和的列名
     * @return
     */
    public static List<Map<String, Object>> resultGroupSumDouble(List<Map<String, Object>> list, String groupColumn, String sumColumn) {

        return list.stream().collect(
                collectingAndThen(
                        groupingBy(map -> map.get(groupColumn).toString(), summingDouble(map -> Double.valueOf(map.get(sumColumn).toString()))),
                        map -> convertMap(map, groupColumn, sumColumn)
                ));

    }


    /**
     * 结果分组计数
     *
     * @param list        结果集
     * @param groupColumn 分组字段
     * @param countColumn 计数字段
     * @return
     */
    public static List<Map<String, Object>> resultGroupCounting(List<Map<String, Object>> list, String groupColumn, String countColumn) {

        List<Map<String, Object>> collect = list.stream()
                .collect(
                        collectingAndThen(
                                groupingBy(map -> map.get(groupColumn), counting()),
                                map -> convertMap(map, groupColumn, countColumn)
                        ));
        return collect;
    }

    private static <K, V> List<Map<String, Object>> convertMap(Map<K, V> map, String groupColumn, String column) {
        return map.keySet().stream().map(key -> {
            Map<String, Object> newMap = new HashMap<String, Object>(2);
            newMap.put(groupColumn, key);
            newMap.put(column, map.get(key));
            return newMap;
        }).collect(Collectors.toList());


    }

    public static void main(String[] args) {
        Stream.of("1", "2", "3");
        List<Integer> list = Arrays.asList(1, 2, 4, 0);
        list.sort(Comparator.comparing(Integer::intValue).reversed());
        System.out.println(list);

    }
}