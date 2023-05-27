package com.enation.app.javashop.model.errorcode;

/**
 * 会员异常码
 * Created by kingapex on 2018/3/13.
 *
 * @author kingapex
 * @version 1.0
 * @since 7.0.0
 * 2018/3/13
 */
public enum MemberErrorCode {

    //会员error code
    E100("会员地址已达上限！"),
    E101("无法更改当前默认地址为非默认地址！"),
    E102("无法收藏自己的店铺！"),
    E103("此店铺已经添加为收藏！"),
    E104("无法收藏自己店铺的商品！"),
    E105("此商品已经添加为收藏！"),
    E106("参数不完整！"),
    E107("参数不正确！"),
    E108("当前用户名已经被使用"),
    E109("当前token已经失效"),
    E110("会员已经退出"),
    E111("当前手机号已经绑定其他用户"),
    E112("密码长度为4-20个字符"),
    E113("两次密码不一致"),
    E114("当前会员未绑定手机号"),
    E115("请先对手机进行验证"),
    E116("用户名称已经被占用"),
    E117("邮箱已经被占用"),
    E118("当前手机号已经被使用"),
    E119("请先对用户进行身份验证"),
    E120("税号不能为空"),
    E121("发票数已达上限"),
    E122("请指定发送会员"),
    E123("当前会员不存在"),
    E124("当前会员已禁用"),
    E130("不支持的登录方式"),
    E131("联合登录失败"),
    E132("当前会员已绑定其他账号"),
    E133("授权超时"),
    E134("会员未绑定相关账号"),
    E135("30天内不可重复解绑"),
    E200("评论无权限"),
    E201("评论传参错误"),
    E202("咨询参数错误"),
    E203("优惠券超领"),
    E136("权限操作错误"),
    E137("当前会员已经失效"),
    E138("无法删除超级管理员"),
    E139("无法添加超级管理员"),
    E140("登录失败"),
    E141("地区不合法"),
    E142("您还没有为成为店主"),
    E143("当前账号已经绑定其他用户"),
    E144("此账号已经拥有店铺"),
    E145("此会员已经成为其他店铺店员"),
    E146("此会员已经是本店店员"),
    E147("参数错误"),
    E148("会员增票资质信息错误"),
    E149("会员收票地址信息错误"),
    E150("会员开票历史记录信息错误"),
    E151("会员收货地址信息不正确"),
    E152("发送短信频繁，请稍后再试");

    private String describe;

    MemberErrorCode(String des) {
        this.describe = des;
    }

    /**
     * 获取异常码
     *
     * @return
     */
    public String code() {
        return this.name().replaceAll("E", "");
    }


}