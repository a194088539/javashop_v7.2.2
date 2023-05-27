package com.enation.app.javashop.consumer.shop.member;

import com.enation.app.javashop.consumer.core.event.MemberInfoChangeEvent;
import com.enation.app.javashop.client.member.MemberClient;
import com.enation.app.javashop.client.member.MemberCommentClient;
import com.enation.app.javashop.model.member.dos.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 会员信息消费者
 *
 * @author zh
 * @version v7.0
 * @date 18/12/26 下午4:39
 * @since v7.0
 */
@Component
public class MemberInfoConsumer implements MemberInfoChangeEvent {

    @Autowired
    private MemberCommentClient memberCommentClient;
    @Autowired
    private MemberClient memberClient;

    @Override
    public void memberInfoChange(Long memberId) {
        //获取用户信息
        Member member = memberClient.getModel(memberId);
        //修改用户评论信息
        memberCommentClient.editComment(member.getMemberId(), member.getFace());

    }
}
