package com.enation.app.javashop.client.member;

import com.enation.app.javashop.model.member.dos.MemberNoticeLog;

import java.util.List;

/**
 * @author fk
 * @version v2.0
 * @Description: 会员短消息
 * @date 2018/8/14 10:17
 * @since v7.0.0
 */
public interface MemberNoticeLogClient {

    /**
     * 添加会员站内消息历史
     *
     * @param content  消息内容
     * @param sendTime 发送时间
     * @param memberId 会员id
     * @param title    标题
     * @return 历史消息
     */
    MemberNoticeLog add(String content, long sendTime, Long memberId, String title);

    /**
     * 添加会员站内消息历史
     *
     * @param content   消息内容
     * @param sendTime  发送时间
     * @param memberIds 会员id集合
     * @param title     标题
     * @return 历史消息
     */
    int adds(String content, long sendTime, List<String> memberIds, String title);
}
