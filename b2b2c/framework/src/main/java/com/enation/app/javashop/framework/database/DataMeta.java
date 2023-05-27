package com.enation.app.javashop.framework.database;

import java.util.Arrays;

/**
 * 元数据
 * @author Snow create in 2018/3/28
 * @version v2.0
 * @since v7.0.0
 */
public class DataMeta {

    /**
     * sql 语句
     */
    private String sql;

    /**
     * sql 语句中需要的参数
     */
    private Object[] paramter;

    private Long primaryValue;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Object[] getParamter() {
        return paramter;
    }

    public void setParamter(Object[] paramter) {
        this.paramter = paramter;
    }

    public Long getPrimaryValue() {
        return primaryValue;
    }

    public void setPrimaryValue(Long primaryValue) {
        this.primaryValue = primaryValue;
    }

    @Override
    public String toString() {
        return "DataMeta{" +
                "sql='" + sql + '\'' +
                ", paramter=" + Arrays.toString(paramter) +
                '}';
    }
}
