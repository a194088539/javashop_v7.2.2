package com.enation.app.javashop.client.member;

import com.enation.app.javashop.model.member.dos.MemberWalletDO;
import com.enation.app.javashop.model.member.vo.MemberDepositeVO;

/**
*
* @description: 预存款客户端
* @author: liuyulei
* @create: 2019/12/30 18:40
* @version:1.0
* @since:7.1.5
**/
public interface DepositeClient {


    /**
     * 增加会员预存款余额
     * @param money 金额
     * @param memberId 会员id
     * @param detail   操作描述
     * @return
     */
    Boolean increase(Double money, Long memberId, String detail);

    /**
     * 扣减会员预存款余额
     * @param money 金额
     * @param memberId 会员id
     * @param detail   操作描述
     * @return
     */
    Boolean	reduce(Double money, Long memberId, String detail);

    /**
     * 获取会员钱包信息
     * @param memberId
     * @return
     */
    MemberWalletDO getModel(Long memberId);


    /**
     * 添加会员钱包信息
     * @param walletDO
     * @return
     */
    MemberWalletDO add(MemberWalletDO walletDO);

    /**
     * 校验支付面
     * @param memberId  会员id
     * @param password  支付密码
     * @return
     */
    void checkPwd(Long memberId,String password);


    /**
     * 获取预存款支付相关
     * @param memberId
     * @return
     */
    MemberDepositeVO getDepositeVO(Long memberId);
}
