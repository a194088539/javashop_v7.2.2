package com.enation.app.javashop.model.system.enums;

/**
 * 验证平台类型枚举类
 * @author duanmingyu
 * @version v1.0
 * @since v7.1.6
 * 2019-12-18
 */
public enum ValidatorPlatformEnum {
    /**
     * 图片验证码
     */
    IMAGE("图片验证码"),
    /**
     * 阿里云滑动验证
     */
    ALIYUN("阿里云滑动验证");

    private String description;

    ValidatorPlatformEnum(String des) {
        this.description = des;
    }

    public String description() {
        return this.description;
    }

    public String value() {
        return this.name();
    }
}
