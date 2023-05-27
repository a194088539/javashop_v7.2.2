package com.enation.app.javashop.model.shop.enums;

/**
 * Author: gy
 * <p>
 * Date: Created in 2020/7/8 4:27 下午
 * <p>
 * Version: 0.0.1
 */
public enum ShopMessageTypeEnum {
    /**
     * 执行全部消息
     */
    All("全部"),

    /**
     * 静态页
     */
    PAGE_CREATE("静态页");

    private String description;


    ShopMessageTypeEnum(String des) {
        this.description = des;
    }

    public String description() {
        return this.description;
    }

    public String value() {
        return this.name();
    }


}
