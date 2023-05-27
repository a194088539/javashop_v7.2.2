package com.enation.app.javashop.framework.context.instance;

import java.util.List;
import java.util.Set;

/**
 * @author kingapex
 * @version 1.0
 * @since 7.1.0
 * 2020/4/15
 */

public interface InstanceContext {

    /**
     * 读取app列表
     * @return
     */
    Set<String> getApps();

    /**
     * 获取实例列表
     * @return
     */
    List<AppInstance> getInstances();

    /**
     * 注册本实例
     */
    void register();


}
