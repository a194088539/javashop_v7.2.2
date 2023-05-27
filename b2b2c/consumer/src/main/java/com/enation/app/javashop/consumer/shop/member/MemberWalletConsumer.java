package com.enation.app.javashop.consumer.shop.member;

import com.enation.app.javashop.consumer.core.event.MemberRegisterEvent;
import com.enation.app.javashop.model.base.message.MemberRegisterMsg;
import com.enation.app.javashop.client.member.DepositeClient;
import com.enation.app.javashop.model.member.dos.Member;
import com.enation.app.javashop.model.member.dos.MemberWalletDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description: 会员钱包消费者
 * @author: liuyulei
 * @create: 2019-12-31 11:08
 * @version:1.0
 * @since:7.1.4
 **/
@Service
public class MemberWalletConsumer implements MemberRegisterEvent {

    @Autowired
    private DepositeClient depositeClient;

    /**
     * 新注册会员，默认添加会员钱包信息
     * @param memberRegisterMsg
     */
    @Override
    public void memberRegister(MemberRegisterMsg memberRegisterMsg) {
        this.checkWallet(memberRegisterMsg.getMember());
    }

    /**
     * 检测会员钱包是否存在，如果不存在，则添加会员钱包信息
     * @param member
     */
    private void checkWallet(Member member) {
        //检测会员钱包是否存在
        MemberWalletDO walletDO = this.depositeClient.getModel(member.getMemberId());
        if(walletDO == null){
            //如果不存在则添加会员钱包信息
            walletDO = new MemberWalletDO(member.getMemberId(),member.getUname());
            this.depositeClient.add(walletDO);
        }
    }
}
