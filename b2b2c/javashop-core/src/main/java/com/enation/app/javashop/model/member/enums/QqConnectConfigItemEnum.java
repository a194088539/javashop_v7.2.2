package com.enation.app.javashop.model.member.enums;

/**
 * @author zjp
 * @version v7.0
 * @Description qq信任登录参数项枚举类
 * @ClassName QqConnectConfigItemEnum
 * @since v7.0 下午6:12 2018/6/28
 */
public enum QqConnectConfigItemEnum {
    /**
     * AppId
     */
    app_id("AppId"),
    /**
     * AppKey
     */
    app_key("AppKey");
    private String text;

    QqConnectConfigItemEnum(String text) {
        this.text = text;

    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String value() {
        return this.name();
    }
}
