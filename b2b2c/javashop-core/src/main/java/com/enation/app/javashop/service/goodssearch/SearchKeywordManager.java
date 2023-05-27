package com.enation.app.javashop.service.goodssearch;

import com.enation.app.javashop.framework.database.WebPage;

/**
* @author liuyulei
 * @version 1.0
 * @Description:  搜索关键字历史业务类
 * @date 2019/5/27 11:11
 * @since v7.0
 */
public interface SearchKeywordManager {

    /**
     * 添加搜索关键字
     * @param keyword 关键字
     */
    void add(String keyword);

    /**
     * 关键字历史列表
     * @param pageNo 每页
     * @param pageSize 每页数量
     * @param keyword 关键字
     * @return
     */
    WebPage list(Long pageNo, Long pageSize, String keyword);


    /**
     * 更新关键字数据
     * @param keyword 关键字
     */
    void update(String keyword);

    /**
     * 判断关键字是否存在
     * @param keyword 关键字
     * @return
     */
    boolean isExist(String keyword);

}
