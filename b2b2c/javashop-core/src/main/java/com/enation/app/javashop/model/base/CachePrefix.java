package com.enation.app.javashop.model.base;

/**
 * 缓存前缀
 * Created by kingapex on 2018/3/19.
 *
 * @author kingapex
 * @version 1.0
 * @since 7.0.0
 * 2018/3/19
 */
public enum CachePrefix {


    /**
     * nonce
     */
    NONCE,

    /**
     * token
     */
    TOKEN,

    /**
     * 系统设置
     */
    SETTING,

    /**
     * 快递平台
     */
    EXPRESS,

    /**
     * 图片验证码
     */
    CAPTCHA,

    /**
     * 商品
     */
    GOODS,
    /**
     * 运费模板脚本
     */
    SHIP_SCRIPT,

    /**
     * 商品sku
     */
    SKU,

    /**
     * sku库存
     */
    SKU_STOCK,

    /**
     * 商品库存
     */
    GOODS_STOCK,

    /**
     * 商品分类
     */
    GOODS_CAT,
    /**
     * 浏览次数
     */
    VISIT_COUNT,
    /**
     * 存储方案
     */
    UPLOADER,
    /**
     * 地区
     */
    REGION,

    /**
     * 短信网关
     */
    SPlATFORM,
    /**
     * 短信验证码前缀
     */
    _CODE_PREFIX,
    /**
     * smtp
     */
    SMTP,
    /**
     * 系统设置
     */
    SETTINGS,
    /**
     * 电子面单
     */
    WAYBILL,
    /**
     * 短信验证码
     */
    SMS_CODE,
    /**
     * 邮箱验证码
     */
    EMAIL_CODE,
    /**
     * 管理员角色权限对照表
     */
    ADMIN_URL_ROLE,

    /**
     * 店铺管理员角色权限对照表
     */
    SHOP_URL_ROLE,

    /**
     * 手机验证标识
     */
    MOBILE_VALIDATE,

    /**
     * 邮箱验证标识
     */
    EMAIL_VALIDATE,

    /**
     * 店铺运费模版列表
     */
    SHIP_TEMPLATE,

    /**
     * 店铺中某个运费模版
     */
    SHIP_TEMPLATE_ONE,

    //================促销=================
    /**
     * 促销活动
     */
    PROMOTION_KEY,

    /*** 单品立减 */
    STORE_ID_MINUS_KEY,

    /*** 第二件半价 */
    STORE_ID_HALF_PRICE_KEY,

    /*** 满优惠 */
    STORE_ID_FULL_DISCOUNT_KEY,

    /**
     * 限时抢购活动缓存key前缀
     */
    STORE_ID_SECKILL_KEY,

    /**
     * 团购活动缓存key前缀
     */
    STORE_ID_GROUP_BUY_KEY,

    /**
     * 积分商品缓存key前缀
     */
    STORE_ID_EXCHANGE_KEY,


    //================交易=================

    /**
     * 购物车原始数据
     */
    CART_ORIGIN_DATA_PREFIX,

    /**
     * 立即购买原始数据
     */
    BUY_NOW_ORIGIN_DATA_PREFIX,

    /**
     * 交易原始数据
     */
    TRADE_ORIGIN_DATA_PREFIX,

    /**
     * 立即购买sku
     */
    CART_SKU_PREFIX,

    /**
     * 购物车视图
     */
    CART_MEMBER_ID_PREFIX,

    /**
     * 购物车，用户选择的促销信息
     */
    CART_PROMOTION_PREFIX,


    /**
     * 交易_交易价格的前缀
     */
    PRICE_SESSION_ID_PREFIX,

    /**
     * 交易_交易单
     */
    TRADE_SESSION_ID_PREFIX,


    /**
     * 结算参数
     */
    CHECKOUT_PARAM_ID_PREFIX,

    /**
     * 交易单号前缀
     */
    TRADE_SN_CACHE_PREFIX,

    /**
     * 订单编号前缀
     */
    ORDER_SN_CACHE_PREFIX,
    /**
     * 订单编号标记
     */
    ORDER_SN_SIGN_CACHE_PREFIX,
    /**
     * 订单编号前缀
     */
    PAY_LOG_SN_CACHE_PREFIX,

    /**
     * 零钱
     */
    SMALL_CHANGE_CACHE_PREFIX,

    /**
     * 预存款余额
     */
    BALANCE_CACHE_PREFIX,

    /**
     * 售后服务单号前缀
     */
    AFTER_SALE_SERVICE_PREFIX,

    /**
     * 支付账单编号前缀
     */
    PAY_BILL_CACHE_PREFIX,

    /**
     * 充值订单编号前缀
     */
    RECHARGE_CACHE_PREFIX,

    /**
     * 交易
     */
    TRADE,

    /**
     * 商品好评率
     */
    GOODS_GRADE,
    /**
     * 所有地区
     */
    REGIONALL,
    /**
     * 分级别地区缓存
     */
    REGIONLIDEPTH,

    /**
     * 站点导航栏
     */
    SITE_NAVIGATION,
    /**
     * 信任登录
     */
    CONNECT_LOGIN,
    /**
     * session_key
     */
    SESSION_KEY,
    /**
     * 支付参数
     */
    PAYMENT_CONFIG,

    /**
     * 流程控制
     */
    FLOW,

    /**
     * 热门关键字
     */
    HOT_KEYWORD,

    /**
     * 验证平台
     */
    VALIDATOR_PLATFORM,


    /**
     * 购物车级别促销信息
     */
    CART_PROMOTION,

    /**
     * 商品SKU级别促销信息
     */
    SKU_PROMOTION,

    /**
     * 手机号码发送短信次数
     */
    PHONE_SMS_NUMBER,

    /**
     * 优惠券级别促销信息
     */
    COUPON_PROMOTION,
    /**
     * 上线人员
     */
    DISTRIBUTION_UP;

    public String getPrefix() {
        return "{" + this.name() + "}_";
    }
}
