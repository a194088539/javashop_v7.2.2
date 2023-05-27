package com.enation.app.javashop.client.member.impl;

import com.enation.app.javashop.client.member.MemberAskClient;
import com.enation.app.javashop.model.member.dos.AskMessageDO;
import com.enation.app.javashop.service.member.AskMessageManager;
import com.enation.app.javashop.service.member.MemberAskManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * @author fk
 * @version v1.0
 * @Description: 评论对外接口实现
 * @date 2018/7/26 11:30
 * @since v7.0.0
 */
@Service
@ConditionalOnProperty(value="javashop.product", havingValue="stand")
public class MemberAskClientDefaultImpl implements MemberAskClient {

    @Autowired
    private MemberAskManager memberAskManager;

    @Autowired
    private AskMessageManager askMessageManager;

    @Override
    public Integer getNoReplyCount(Long sellerId) {

        return memberAskManager.getNoReplyCount(sellerId);
    }

    @Override
    public void sendMessage(AskMessageDO askMessageDO) {
        this.askMessageManager.addAskMessage(askMessageDO);
    }
}
