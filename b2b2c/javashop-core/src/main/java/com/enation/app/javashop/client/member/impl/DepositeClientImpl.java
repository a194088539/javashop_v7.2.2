package com.enation.app.javashop.client.member.impl;

import com.enation.app.javashop.client.member.DepositeClient;
import com.enation.app.javashop.model.member.dos.MemberWalletDO;
import com.enation.app.javashop.model.member.vo.MemberDepositeVO;
import com.enation.app.javashop.service.member.DepositeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * @description: 预存款客户端实现
 * @author: liuyulei
 * @create: 2019-12-30 18:41
 * @version:1.0
 * @since:7.1.4
 **/
@Service
@ConditionalOnProperty(value="javashop.product", havingValue="stand")
public class DepositeClientImpl implements DepositeClient {

    @Autowired
    private DepositeManager depositeManager;

    @Override
    public Boolean increase(Double money, Long memberId, String detail) {
        return this.depositeManager.increase(money,memberId,detail);
    }

    @Override
    public Boolean reduce(Double money, Long memberId, String detail) {
        return this.depositeManager.reduce(money,memberId,detail);
    }

    @Override
    public MemberWalletDO getModel(Long memberId) {
        return this.depositeManager.getModel(memberId);
    }

    @Override
    public MemberWalletDO add(MemberWalletDO walletDO) {
        return this.depositeManager.add(walletDO);
    }

    @Override
    public void checkPwd(Long memberId, String password) {
        this.depositeManager.checkPwd(memberId,password);
    }

    @Override
    public MemberDepositeVO getDepositeVO(Long memberId) {
        return this.depositeManager.getDepositeVO(memberId);
    }
}
