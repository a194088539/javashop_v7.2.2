package com.enation.app.javashop.service.member;

import com.enation.app.javashop.model.member.dos.ConnectDO;
import com.enation.app.javashop.model.member.dos.ConnectSettingDO;
import com.enation.app.javashop.model.member.dos.Member;
import com.enation.app.javashop.model.member.dto.ConnectSettingDTO;
import com.enation.app.javashop.model.member.enums.ConnectTypeEnum;
import com.enation.app.javashop.model.member.vo.*;

import java.util.List;
import java.util.Map;

/**
 * @author zjp
 * @version v7.0
 * @Description 信任登录业务类
 * @ClassName ConnectManager
 * @since v7.0 下午8:54 2018/6/6
 */
public interface ConnectManager {

    /**
     * 微信授权
     */
    void wechatAuth();

    /**
     * 检测授权信息是否存在
     *
     * @param uuid
     * @return
     */
    Integer checkAuth(String uuid);

    /**
     * 微信授权回调
     */
    void wechatAuthCallBack();

    /**
     * 微信绑定登录
     *
     * @param uuid 客户端唯一标示
     * @return
     */
    Map bindLogin(String uuid);

    /**
     * 绑定账号
     *
     * @param name        用户名
     * @param password    密码
     * @param connectUuid 联合登录uuid
     * @param uuid        uuid
     * @return
     */
    Map bind(String name, String password, String connectUuid, String uuid);

    /**
     * 会员中心绑定账号
     *
     * @param uuid 唯一号
     * @param uid  用户id
     * @return
     */
    Map bind(String uuid, Long uid);

    /**
     * 发起信任登录
     *
     * @param type 登录方式 QQ，微博，微信，支付宝
     * @param port 客户端类型 PC，WAP
     * @param member 会员
     */
    void initiate(String type, String port, String member);

    /**
     * 信任登录回调
     * @param type 登录方式 QQ，微博，微信，支付宝
     * @param client 客户端类型 PC，WAP
     * @param member 会员
     * @param uuid 唯一号
     * @return
     */
    MemberVO callBack(String type, String client, String member, String uuid);

    /**
     * 注册会员并绑定
     *
     * @param uuid 唯一号
     */
    void registerBind(String uuid);

    /**
     * 会员解除绑定
     *
     * @param type 登录类型
     */
    void unbind(String type);

    /**
     * 会员绑定openid
     *
     * @param uuid 唯一号
     * @return
     */
    Map openidBind(String uuid);


    /**
     * 获取app联合登录所需参数
     *
     * @param type 登录方式 QQ，微博，微信，支付宝
     * @return
     */
    String getParam(String type);

    /**
     * 检测openid是否绑定
     *
     * @param type 登录方式 QQ，微博，微信，支付宝
     * @param openid 唯一号
     * @return
     */
    Map checkOpenid(String type, String openid);

    /**
     * 发送手机校验验证码
     *
     * @param mobile 手机号码
     */
    void sendCheckMobileSmsCode(String mobile);

    /**
     * WAP手机号绑定
     *
     * @param mobile 手机号码
     * @param uuid 唯一号
     * @return
     */
    Map mobileBind(String mobile, String uuid);


    /**
     * 获取会员绑定列表
     *
     * @return
     */
    List<ConnectVO> get();

    /**
     * 获取后台信任登录参数
     *
     * @return
     */
    List<ConnectSettingVO> list();

    /**
     * 保存信任登录信息
     *
     * @param connectSettingDTO 信任登录信息
     * @return
     */
    ConnectSettingDTO save(ConnectSettingDTO connectSettingDTO);

    /**
     * 获取授权登录参数
     *
     * @param type 授权登录类型
     * @return
     */
    ConnectSettingDO get(String type);

    /**
     * 根据type获取相应的插件类
     *
     * @param type 信任登录类型
     * @return
     */
    AbstractConnectLoginPlugin getConnectionLogin(ConnectTypeEnum type);

    /**
     * 微信退出解绑操作
     */
    void wechatOut();

    /**
     * ios APP 第三方登录获取授权url
     *
     * @return
     */
    String getAliInfo();


    /**
     * app用户绑定
     *
     * @param member 会员信息
     * @param openid openid
     * @param type 信任登录类型
     * @param uuid 唯一号
     * @return
     */
    Map appBind(Member member, String openid, String type, String uuid);

    /**
     * 初始化配置参数
     *
     * @return
     */
    Map initConnectSetting();

    /**
     * 小程序登录
<<<<<<< HEAD
     * @param content 内容
     * @param uuid 唯一号
=======
     *
     * @param content
     * @param uuid
>>>>>>> feature_7.2.1
     * @return
     */
    Map miniProgramLogin(String content, String uuid);

    /**
     * 解密
     *
     * @param code 代码
     * @param encryptedData
     * @param uuid
     * @param iv
     * @return
     */
    Map decrypt(String code, String encryptedData, String uuid, String iv);

    /**
     * 获取微信小程序码
     * @param accessTocken 访问token
     * @param goodsId 商品ID
     * @return
     */
    String getWXACodeUnlimit(String accessTocken, Long goodsId);


    /**
     * 获取 联合登录对象
     *
     * @param memberId  会员id
     * @param unionType 类型
     * @return ConnectDO
     */
    ConnectDO getConnect(Long memberId, String unionType);
}
