package com.enation.app.javashop.service.statistics;

import com.enation.app.javashop.model.member.dos.Member;

/**
 * 会员信息收集manager
 *
 * @author chopper
 * @version v1.0
 * @Description:
 * @since v7.0
 * 2018/5/8 下午4:10
 */

public interface MemberDataManager {


//	/**
//	 * 会员登录
//	 * @param msg
//	 */
//	void putMember(MemberLoginMsg msg);

    /**
     * 会员注册
     *
     * @param member 会员
     */
    void register(Member member);


}
