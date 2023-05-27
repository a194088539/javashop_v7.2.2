package com.enation.app.javashop.service.shop;

import com.enation.app.javashop.model.shop.dos.ShopNoticeLogDO;
import com.enation.app.javashop.framework.database.WebPage;

/**
 * 店铺站内消息业务层
 *
 * @author zjp
 * @version v7.0.0
 * @since v7.0.0
 * 2018-07-10 10:21:45
 */
public interface ShopNoticeLogManager {

    /**
     * 查询店铺站内消息列表
     *
     * @param page     页码
     * @param pageSize 每页数量
     * @param type     类型
     * @param isRead   1 已读，0 未读
     * @return WebPage
     */
    WebPage list(long page, long pageSize, String type, Integer isRead);

    /**
     * 添加店铺站内消息
     *
     * @param shopNoticeLog 店铺站内消息
     * @return ShopNoticeLog 店铺站内消息
     */
    ShopNoticeLogDO add(ShopNoticeLogDO shopNoticeLog);

    /**
     * 删除历史消息
     *
     * @param ids 消息ID组
     */
    void delete(Long[] ids);

    /**
     * 设置已读
     *
     * @param ids 消息id组
     */
    void read(Long[] ids);

}
