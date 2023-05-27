package com.enation.app.javashop.service.system;

import com.enation.app.javashop.framework.database.WebPage;

import java.util.List;
import java.util.Map;

/**
 * @Description : 日志分析业务层
 * @Author snow
 * @Date: 2020-02-03 14:52
 * @Version v1.0
 */

public interface LogManager {

    /**
     * 读取服务名列表
     * @return
     */
    List<Map> appNameList();


    /**
     * 根据服务名查询实例列表
     * @param appName
     * @return
     */
    List<Map> instancesList(String appName);


    /**
     * 读取日志
     * @param appName   服务名
     * @param instances 实例UUID
     * @param date      日期，格式 yyyy-MM-dd
     * @param page
     * @param pageSize
     * @return
     */
    WebPage<String> getLogs(String appName, String instances, String date, int page, int pageSize) throws RuntimeException;


}
