package com.enation.app.javashop.client.member.impl;

import com.enation.app.javashop.client.member.MemberNoticeLogClient;
import com.enation.app.javashop.model.member.dos.MemberNoticeLog;
import com.enation.app.javashop.service.member.MemberNoticeLogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author fk
 * @version v2.0
 * @Description: 会员站内短消息
 * @date 2018/8/14 10:18
 * @since v7.0.0
 */
@Service
@ConditionalOnProperty(value = "javashop.product", havingValue = "stand")
public class MemberNoticeLogClientDefaultImpl implements MemberNoticeLogClient {

    @Autowired
    private MemberNoticeLogManager memberNociceLogManager;

    @Override
    public MemberNoticeLog add(String content, long sendTime, Long memberId, String title) {

        return memberNociceLogManager.add(content, sendTime, memberId, title);
    }

    @Override
    public int adds(String content, long sendTime, List<String> memberIds, String title) {
        return memberNociceLogManager.adds(content, sendTime, memberIds, title);
    }
}
