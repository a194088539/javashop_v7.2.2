package com.enation.app.javashop.service.base.plugin.validator;

import com.enation.app.javashop.model.base.vo.ConfigItem;

import java.util.List;
import java.util.Map;

/**
 * 验证插件接口
 * @author duanmingyu
 * @version v1.0
 * @since v7.1.6
 * 2019-12-18
 */
public interface ValidatorPlugin {

    /**
     * 配置各个存储方案的参数
     *
     * @return 参数列表
     */
    List<ConfigItem> definitionConfigItem();

    /**
     * 获取插件ID
     *
     * @return 插件beanId
     */
    String getPluginId();

    /**
     * 获取插件名称
     *
     * @return 插件名称
     */
    String getPluginName();

    /**
     * 短信网关是否开启
     *
     * @return 0 不开启  1 开启
     */
    Integer getIsOpen();

    /**
     * 验证方法
     * @param param 要验证的参数
     */
    void onValidate(Map param);

}
