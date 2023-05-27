package com.enation.app.javashop.client.member.impl;

import com.enation.app.javashop.client.member.ConnectClient;
import com.enation.app.javashop.model.member.dos.ConnectDO;
import com.enation.app.javashop.service.member.ConnectManager;
import com.enation.app.javashop.service.passport.signaturer.WechatSignaturer;
import com.enation.app.javashop.model.payment.enums.WechatTypeEnmu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * 第三方连接client
 *
 * @author zh
 * @version v7.0
 * @date 18/7/27 下午3:51
 * @since v7.0
 */
@Service
@ConditionalOnProperty(value="javashop.product", havingValue="stand")
public class ConnectClientDefaultImpl implements ConnectClient {

    @Autowired
    private ConnectManager connectManager;

    @Autowired
    private WechatSignaturer wechatSignaturer;

    @Override
    public ConnectDO getConnect(Long memberId, String unionType) {

        return connectManager.getConnect(memberId, unionType);
    }

    @Override
    public String getMemberOpenid(Long memberId) {

        return wechatSignaturer.getMemberOpenid(memberId);
    }

    @Override
    public String getCgiAccessToken(WechatTypeEnmu wechatTypeEnmu) {

        return wechatSignaturer.getCgiAccessToken(wechatTypeEnmu);
    }
}
