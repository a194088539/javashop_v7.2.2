package com.enation.app.javashop.model.system.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 阿里云滑动验证参数VO
 * @author duanmingyu
 * @version v1.0
 * @since v7.1.6
 * 2019-12-18
 */
public class AliyunAfsVO implements Serializable {

    private static final long serialVersionUID = 4627877287190473083L;

    /**
     * 应用程序秘钥
     */
    @ApiModelProperty(name = "app_key", value = "应用程序秘钥")
    private String appKey;
    /**
     * 场景标识
     */
    @ApiModelProperty(name = "scene", value = "场景标识")
    private String scene;

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    @Override
    public String toString() {
        return "AliyunAfsVO{" +
                "appKey='" + appKey + '\'' +
                ", scene=" + scene + '\'' +
                '}';
    }
}
