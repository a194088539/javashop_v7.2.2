package com.enation.app.javashop.client.system;

import com.enation.app.javashop.model.system.enums.MessageCodeEnum;
import com.enation.app.javashop.model.system.dos.MessageTemplateDO;
import com.enation.app.javashop.model.system.enums.WechatMsgTemplateTypeEnum;

import java.util.List;

/**
 * @version v7.0
 * @Description: 消息模版client
 * @Author: zjp
 * @Date: 2018/7/27 09:42
 */
public interface MessageTemplateClient {
    /**
     * 获取消息模版
     * @param messageCodeEnum 消息模版编码
     * @return MessageTemplateDO  消息模版
     */
    MessageTemplateDO getModel(MessageCodeEnum messageCodeEnum);

    /**
     * 发送消息
     * @param openId
     * @param keywords
     * @param messageType
     */
    void sendWechatMsg(String openId, WechatMsgTemplateTypeEnum messageType, List<Object> keywords);

}
