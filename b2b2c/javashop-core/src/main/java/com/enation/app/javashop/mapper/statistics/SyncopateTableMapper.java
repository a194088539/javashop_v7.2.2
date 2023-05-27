package com.enation.app.javashop.mapper.statistics;

import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 统计模块分表 mapper
 * @author zs
 * @version v1.0
 * @since v7.2.2
 * 2020-08-04
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface SyncopateTableMapper {

    /**
     * 创建年份表
     * @param year 年份
     * @param tableName 主表名
     */
    void createTable(@Param("year") Integer year, @Param("tableName") String tableName);

    /**
     * 删除年份表
     * @param year 年份
     * @param tableName 主表名
     */
    void dropTable(@Param("year") Integer year, @Param("tableName") String tableName);

    /**
     * 查询主表某一年的数据
     * @param year 年份
     * @param tableName 表名
     * @param yearStartTime 一年的开始时间
     * @param yearEndTime 一年的结束时间
     * @return
     */
    List<Map<String, Object>> selectData(@Param("year") Integer year,
                                         @Param("tableName") String tableName,
                                         @Param("yearStartTime") Long yearStartTime,
                                         @Param("yearEndTime") Long yearEndTime);

    /**
     * 插入数据到年份表
     * @param year 年份
     * @param tableName 主表名
     * @param columnNames 要插入的列名
     * @param dataList 要插入的数据
     */
    void insertData(@Param("year") Integer year,
                    @Param("tableName") String tableName,
                    @Param("columnNames") String columnNames,
                    @Param("dataList") List<List<Object>> dataList);


}
