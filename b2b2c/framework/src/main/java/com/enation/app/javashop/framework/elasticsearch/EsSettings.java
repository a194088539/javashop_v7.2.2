package com.enation.app.javashop.framework.elasticsearch;

/**
 *
 * es索引设置
 * @author kingapex
 * @version 1.0
 * @since 7.1.0
 * 2019-03-21
 */

public class EsSettings {

    /**
     * 商品索引后缀
     */
    public static final String GOODS_INDEX_NAME="goods";

    /**
     * 商品type名字
     */
    public static final String GOODS_TYPE_NAME="goods";

    /**
     * 拼团索引后缀
     */
    public static final String PINTUAN_INDEX_NAME="pt";


    /**
     * 拼团类型名字
     */
    public static final String PINTUAN_TYPE_NAME="pintuan_goods";


    /**
     * 日志索引前缀
     */
    public static  final String LOG_INDEX_NAME = "log-index-";

    /**
     * 日志type
     */
    public static  final String LOG_TYPE_NAME = "log";


}
