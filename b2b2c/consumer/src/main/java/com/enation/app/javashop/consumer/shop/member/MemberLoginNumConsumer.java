package com.enation.app.javashop.consumer.shop.member;

import com.enation.app.javashop.consumer.core.event.MemberLoginEvent;
import com.enation.app.javashop.client.member.MemberClient;
import com.enation.app.javashop.model.member.vo.MemberLoginMsg;
import com.enation.app.javashop.framework.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 会员登录后登录次数进行处理
 *
 * @author zh
 * @version v7.0
 * @date 18/4/12 下午5:38
 * @since v7.0
 */
@Service
public class MemberLoginNumConsumer implements MemberLoginEvent {

    @Autowired
    private MemberClient memberClient;

    /**
     * 会员登录后对会员某些字段进行更新 例如 上次登录时间、登录次数
     *
     * @param memberLoginMsg
     */
    @Override
    public void memberLogin(MemberLoginMsg memberLoginMsg) {
        //如果是会员登录
        if(memberLoginMsg.getMemberOrSeller().equals(1)){
            this.memberClient.updateLoginNum(memberLoginMsg.getMemberId(),DateUtil.getDateline());
        }

    }
}
