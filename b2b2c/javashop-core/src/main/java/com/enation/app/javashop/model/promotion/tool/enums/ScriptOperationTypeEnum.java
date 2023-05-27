package com.enation.app.javashop.model.promotion.tool.enums;

/**
 * 脚本操作状态枚举类
 * @author duanmingyu
 * @version v1.0
 * @since v7.2.0
 * 2020-02-27
 */
public enum ScriptOperationTypeEnum {

    /**
     * 创建
     */
    CREATE("创建脚本"),

    /**
     * 删除
     */
    DELETE("删除脚本");

    private String description;

    ScriptOperationTypeEnum(String des) {
        this.description = des;
    }

    public String description() {
        return this.description;
    }

    public String value() {
        return this.name();
    }
}
