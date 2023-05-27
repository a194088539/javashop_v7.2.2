package com.enation.app.javashop.framework.database.impl;

import com.enation.app.javashop.framework.database.ColumnMeta;
import com.enation.app.javashop.framework.database.DataMeta;
import com.enation.app.javashop.framework.database.SqlMetaBuilder;
import com.enation.app.javashop.framework.sncreator.SnCreator;
import com.enation.app.javashop.framework.util.ReflectionUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 基于Mysql的基本增删改查操作实现类
 *
 * @author Snow create in 2018/3/21
 * @version v2.0
 * @since v7.0.0
 */
@Service
public class MySqlMetaBuilderImpl implements SqlMetaBuilder {

    @Autowired
    SnCreator snCreator;


    @Override
    public <T> DataMeta insert(T model) {

        ColumnMeta columnMeta = ReflectionUtil.getColumnMeta(model);
        String primaryKeyName = columnMeta.getPrimaryKeyName();
        Long idValue = snCreator.create(1);

        Object[] columnName = columnMeta.getNames();
        Object[] columnValue = columnMeta.getValues();

        //拼接主键
        columnName = arrayInsert(columnName, primaryKeyName);
        columnValue = arrayInsert(columnValue, idValue);

        StringBuffer questionMarkStr = new StringBuffer();
        for (int i = 0; i < columnValue.length; i++) {
            questionMarkStr.append("?");
            if ((i + 1) != columnValue.length) {
                questionMarkStr.append(",");
            }
        }


        //修改使其能获取父类Table注解的表名 add by liuyulei 2019-12-12
        String tableName = ReflectionUtil.getTableName(model.getClass());
        String columnNameStr = StringUtils.join(columnName, ",");

        String addSql = "INSERT INTO " + tableName + " (" + columnNameStr + ") VALUES (" + questionMarkStr.toString() + ")";

        DataMeta dataMeta = new DataMeta();
        dataMeta.setPrimaryValue(idValue);

        dataMeta.setSql(addSql);
        dataMeta.setParamter(columnValue);
        return dataMeta;
    }

    @Override
    public <T> DataMeta update(T model, Long id) {
        ColumnMeta columnMeta = ReflectionUtil.getColumnMeta(model);
        Object[] columnName = columnMeta.getNames();
        Object[] columnValue = columnMeta.getValues();

        String columnId = ReflectionUtil.getPrimaryKey(model.getClass());
        //修改使其能获取父类Table注解的表名 add by liuyulei 2019-12-12
        String tableName = ReflectionUtil.getTableName(model.getClass());

        List valueList = new ArrayList();
        StringBuffer setStr = new StringBuffer();
        for (int i = 0; i < columnName.length; i++) {
            setStr.append(columnName[i] + "=?");
            valueList.add(columnValue[i]);
            if ((i + 1) != columnName.length) {
                setStr.append(",");
            }
        }
        String editSql = "UPDATE " + tableName + " SET " + setStr.toString() + " WHERE " + columnId + "=?";
        valueList.add(id);

        DataMeta dataMeta = new DataMeta();
        dataMeta.setSql(editSql);
        dataMeta.setParamter(valueList.toArray());
        return dataMeta;
    }


    @Override
    public String queryForModel(Class clazz) {
        //修改使其能获取父类Table注解的表名 add by liuyulei 2019-12-12
        String tableName = ReflectionUtil.getTableName(clazz);
        String columnId = ReflectionUtil.getPrimaryKey(clazz);
        String queryOneSql = "SELECT * FROM " + tableName + " WHERE " + columnId + "=?";
        return queryOneSql;
    }

    @Override
    public String delete(Class clazz) {
        //修改使其能获取父类Table注解的表名 add by liuyulei 2019-12-12
        String tableName = ReflectionUtil.getTableName(clazz);
        String columnId = ReflectionUtil.getPrimaryKey(clazz);
        String deleteSql = "DELETE FROM " + tableName + " WHERE " + columnId + "=?";
        return deleteSql;
    }

    /**
     * 给数组的最后添加元素
     *
     * @param ar
     * @param value
     * @return
     */
    Object[] arrayInsert(Object[] ar, Object value) {
        ar = Arrays.copyOf(ar, ar.length + 1);
        ar[ar.length - 1] = value;
        return ar;
    }
}
