package com.enation.app.javashop.consumer.shop.message;

import com.enation.app.javashop.consumer.core.event.SmsSendMessageEvent;
import com.enation.app.javashop.model.base.vo.SmsSendVO;
import com.enation.app.javashop.client.system.SmsClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 发送短信
 *
 * @author zjp
 * @version v7.0
 * @since v7.0
 * 2018年3月25日 下午3:15:01
 */
@Component
public class SmsMessageConsumer implements SmsSendMessageEvent {

    @Autowired
    private SmsClient smsClient;

    @Override
    public void send(SmsSendVO smsSendVO) {
        smsClient.send(smsSendVO);
    }
}
