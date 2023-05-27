package com.enation.app.javashop.client.member;

import com.enation.app.javashop.model.member.dos.MemberAddress;

/**
 * 会员地址客户端
 *
 * @author zh
 * @version v7.0
 * @date 18/7/27 下午3:51
 * @since v7.0
 */

public interface MemberAddressClient {

    /**
     * 获取会员地址
     *
     * @param id 会员地址主键
     * @return MemberAddress  会员地址
     */
    MemberAddress getModel(Long id);

    /**
     * 获取会员默认地址
     *
     * @param memberId 会员id
     * @return 会员默认地址
     */
    MemberAddress getDefaultAddress(Long memberId);
}
