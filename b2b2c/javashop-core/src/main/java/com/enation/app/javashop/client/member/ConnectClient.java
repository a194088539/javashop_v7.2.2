package com.enation.app.javashop.client.member;

import com.enation.app.javashop.model.member.dos.ConnectDO;
import com.enation.app.javashop.model.payment.enums.WechatTypeEnmu;

/**
 * 第三方连接client
 *
 * @author zh
 * @version v7.0
 * @date 18/7/27 下午3:51
 * @since v7.0
 */
public interface ConnectClient {

    /**
     * 获取 联合登录对象
     * @param memberId 会员id
     * @param unionType 类型
     * @return ConnectDO
     */
    ConnectDO getConnect(Long memberId, String unionType);

    /**
     * 获取会员的openId
     * @param memberId
     * @return
     */
    String getMemberOpenid(Long memberId);

    /**
     * 生成 CGI 接口 access token ，服务器与微信的接口token
     *
     * @param wechatTypeEnmu
     * @return
     */
    String getCgiAccessToken(WechatTypeEnmu wechatTypeEnmu);


}
