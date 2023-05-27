package com.enation.app.javashop.client.system.impl;

import com.enation.app.javashop.client.system.MessageClient;
import com.enation.app.javashop.model.system.dos.Message;
import com.enation.app.javashop.service.system.MessageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * @author fk
 * @version v2.0
 * @Description: 站内消息
 * @date 2018/8/14 10:15
 * @since v7.0.0
 */
@Service
@ConditionalOnProperty(value="javashop.product", havingValue="stand")
public class MessageClientDefaultImpl implements MessageClient {

    @Autowired
    private MessageManager messageManager;

    @Override
    public Message get(Long id) {

        return messageManager.get(id);
    }
}
