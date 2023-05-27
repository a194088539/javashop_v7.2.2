package com.enation.app.javashop.client.system.impl;

import com.enation.app.javashop.client.system.MessageTemplateClient;
import com.enation.app.javashop.model.system.enums.MessageCodeEnum;
import com.enation.app.javashop.model.system.dos.MessageTemplateDO;
import com.enation.app.javashop.model.system.enums.WechatMsgTemplateTypeEnum;
import com.enation.app.javashop.service.system.MessageTemplateManager;
import com.enation.app.javashop.service.system.WechatMsgTemplateManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @version v7.0
 * @Description:
 * @Author: zjp
 * @Date: 2018/7/27 09:44
 */
@Service
@ConditionalOnProperty(value="javashop.product", havingValue="stand")
public class MessageTemplateClientDefaultImpl implements MessageTemplateClient {

    @Autowired
    private MessageTemplateManager messageTemplateManager;

    @Autowired
    private WechatMsgTemplateManager wechatMsgTemplateManager;

    @Override
    public MessageTemplateDO getModel(MessageCodeEnum messageCodeEnum) {
        return messageTemplateManager.getModel(messageCodeEnum);
    }

    @Override
    public void sendWechatMsg(String openId, WechatMsgTemplateTypeEnum messageType, List<Object> keywords) {

        wechatMsgTemplateManager.send(openId, messageType, keywords);
    }
}
