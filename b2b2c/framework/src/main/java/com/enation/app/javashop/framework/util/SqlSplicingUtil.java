package com.enation.app.javashop.framework.util;

import java.lang.reflect.Field;
import java.util.List;

import com.enation.app.javashop.framework.database.ColumnMeta;
import com.enation.app.javashop.framework.sncreator.SnowflakeSnCreator;


/**
 * sql语句拼接工具类
 *
 * @author zjp
 * @version v1.0
 * @since v6.4.0
 * 2017年12月1日 下午4:51:28
 */
public class SqlSplicingUtil {
    /**
     * sql拼接
     *
     * @param list
     * @return
     */
    public static String sqlSplicing(List<String> list) {
        StringBuffer sql = new StringBuffer("");
        if (list.size() > 0) {
            sql.append(" where ");
            for (int i = 0; i < list.size(); i++) {
                if (i == list.size() - 1) {
                    sql.append(list.get(i) + " ");
                } else {
                    sql.append(list.get(i) + " and ");
                }
            }
        }
        return sql.toString();
    }

    /**
     * 批量新增sql语句拼接
     *
     * @param table 表名
     * @param list  需要新增的集合
     * @return
     */
    public static String listToString(String table, List list) {

        //StringBuffer 增加性能
        StringBuffer sql = new StringBuffer("insert into ");
        //雪花算法发号器
        SnowflakeSnCreator creator = new SnowflakeSnCreator();
//        增加表名
        sql.append(table);
        sql.append(" ( ");
//        非空验证
        if (!list.isEmpty()) {
//            遍历集合
            for (int i = 0; i < list.size(); i++) {
//                获取集合对象
                Object o = list.get(i);
                //        非空验证
                if (o != null) {
//                    获取有值的所有属性
                    ColumnMeta columnMeta = ReflectionUtil.getColumnMeta(o);
//                    获取所有属性
                    Field[] fields = o.getClass().getDeclaredFields();
                    //第一个次进
                    if (i == 0) {
//                        记录当前循环的多少
                        int x = 0;
//                        遍历循环
                        for (Field s : fields) {
                            //设置获取所有属性
                            //s.setAccessible(true);
//                            如果字段值为序列化则跳过
                            if ("serialVersionUID".equals(s.getName())) {
                                x++;
                                continue;
                            }
                            // 拼接
                            sql.append(StringUtil.humpToLine(s.getName()));
                            //如果是最后一个，那么添加尾VALUES
                            if (x < fields.length - 1) {
                                sql.append(" , ");
                            } else {
                                sql.append(" ) VALUES ");
                            }
                            x++;
                        }
                    }
                    sql.append(" (");
                    //记录当前循环的多少
                    int x = 0;
                    for (Field field : fields) {
                        //设置获取所有属性
                        field.setAccessible(true);
                        Object value = null;
                        try {
                            value = field.get(o);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        if ("serialVersionUID".equals(field.getName())) {
                            x++;
                            continue;
                        }
                        if (columnMeta.getPrimaryKeyName().equals(StringUtil.humpToLine(field.getName()))) {
                            if (value == null || "null".equals(value)) {
                                value = creator.create(12);
                            }
                        }

                        if (value != null) {
                            if (value instanceof String || value instanceof Long || value instanceof Character) {
                                sql.append("'");
                                sql.append(value);
                                sql.append("'");
                            } else {
                                sql.append(value);
                            }
                        } else {
                            sql.append(value);
                        }
                        if (x < fields.length - 1) {
                            sql.append(" , ");
                        } else {
                            sql.append(" ) ");
                        }
                        x++;
                    }
                    if (i != list.size() - 1) {
                        sql.append(" , ");
                    } else {
                        sql.append(" ; ");
                    }
                }
            }
        }
        return sql.toString();
    }


}
