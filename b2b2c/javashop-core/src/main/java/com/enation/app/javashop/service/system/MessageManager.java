package com.enation.app.javashop.service.system;

import com.enation.app.javashop.model.system.dos.Message;
import com.enation.app.javashop.model.system.dto.MessageQueryParam;
import com.enation.app.javashop.model.system.vo.MessageVO;
import com.enation.app.javashop.framework.database.WebPage;

/**
 * 站内消息业务层
 *
 * @author zh
 * @version v7.0.0
 * @since v7.0.0
 * 2018-07-04 21:50:52
 */
public interface MessageManager {

    /**
     * 查询站内消息列表
     *
     * @param param 搜索条件
     * @return WebPage
     */
    WebPage list(MessageQueryParam param);

    /**
     * 添加站内消息
     *
     * @param messageVO 站内消息
     * @return Message 站内消息
     */
    Message add(MessageVO messageVO);

    /**
     * 通过id查询站内消息
     *
     * @param id 消息id
     * @return 站内消息对象
     */
    Message get(Long id);
}
