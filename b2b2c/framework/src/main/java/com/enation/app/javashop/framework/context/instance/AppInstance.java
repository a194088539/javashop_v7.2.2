package com.enation.app.javashop.framework.context.instance;

import java.io.Serializable;
import java.util.UUID;

/**
 * App 实例
 * @author kingapex
 * @version 1.0
 * @since 7.2.1
 * 2020/4/14
 */

public class AppInstance implements Serializable {

    private static final long serialVersionUID = 4597928439838933735L;

    /**
     * 全局唯一实例
     */
    private static AppInstance instance;

    /**
     * 单例的工厂方法
     * @return
     */
    public static AppInstance getInstance() {
        if (instance == null) {
            instance = new AppInstance();
            instance.setUuid(UUID.randomUUID().toString());
        }
        return instance;
    }

    /**
     * 保护起来，只允许工厂实例化
     */
    private AppInstance() {

    }


    /**
     * work id 是唯一的一个int值，如1,2,3
     * 不仅是在app 多实例内唯一，是全局唯一的
     */
    private int workId;

    /**
     * 实例标识自己用的，比如注册到中心时标识自己
     * 以便在中心中确定是自己注册的
     */
    private String uuid;


    /**
     * app的名字，如base-api buyer-api，用来显示的
     */
    private String appName;

    /**
     * instance 的有效期，如果无效，应该被剔除
     */
    private Long expirationTime;


    public int getWorkId() {
        return workId;
    }

    public void setWorkId(int workId) {
        this.workId = workId;
    }

    public String getUuid() {
        return uuid;
    }

    protected void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Long getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Long expirationTime) {
        this.expirationTime = expirationTime;
    }

    @Override
    public String toString() {
        return "AppInstance{" +
                "workId=" + workId +
                ", uuid='" + uuid + '\'' +
                ", appName='" + appName + '\'' +
                ", expirationTime=" + expirationTime +
                '}';
    }
}
