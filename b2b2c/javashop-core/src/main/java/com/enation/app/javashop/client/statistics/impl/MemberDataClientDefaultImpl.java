package com.enation.app.javashop.client.statistics.impl;

import com.enation.app.javashop.client.statistics.MemberDataClient;
import com.enation.app.javashop.model.member.dos.Member;
import com.enation.app.javashop.service.statistics.MemberDataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * MemberDataClientDefaultImpl
 *
 * @author Chopper
 * @version v1.0
 * @since v7.0
 * 2018-08-14 下午2:40
 */
@Service
@ConditionalOnProperty(value="javashop.product", havingValue="stand")
public class MemberDataClientDefaultImpl implements MemberDataClient {

    @Autowired
    private MemberDataManager memberDataManager;
    /**
     * 会员注册
     * @param member 会员
     */
    @Override
    public void register(Member member) {
        memberDataManager.register(member);
    }
}
