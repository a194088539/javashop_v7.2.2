package com.enation.app.javashop.model.system.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.enation.app.javashop.model.base.vo.ConfigItem;
import com.enation.app.javashop.model.system.dos.ValidatorPlatformDO;
import com.enation.app.javashop.framework.database.annotation.Column;
import com.enation.app.javashop.framework.database.annotation.Id;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * 验证平台实体VO
 * @author duanmingyu
 * @version v1.0
 * @since v7.1.6
 * 2019-12-18
 */
public class ValidatorPlatformVO implements Serializable {

    private static final long serialVersionUID = 7430241246638433536L;

    /**
     * 验证平台id
     */
    @TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(hidden = true)
    private Long id;
    /**
     * 验证平台名称
     */
    @Column(name = "name")
    @ApiModelProperty(name = "name", value = "验证平台名称", required = false)
    private String name;
    /**
     * 是否开启验证平台,1开启，0未开启
     */
    @Column(name = "open")
    @ApiModelProperty(name = "open", value = "是否开启验证平台,1开启，0未开启", required = false)
    private Integer open;
    /**
     * 验证平台配置
     */
    @Column(name = "config")
    @ApiModelProperty(name = "config", value = "验证平台配置", required = false)
    private String config;
    /**
     * 验证平台插件ID
     */
    @Column(name = "plugin_id")
    @ApiModelProperty(name = "plugin_id", value = "验证平台插件ID", required = false)
    private String pluginId;

    /**
     * 滑块验证平台配置项
     */
    @ApiModelProperty(name = "configItems", value = "滑块验证配置项", required = true)
    private List<ConfigItem> configItems;

    public ValidatorPlatformVO() {

    }

    public ValidatorPlatformVO(ValidatorPlatformDO validatorPlatformDO) {
        this.id = validatorPlatformDO.getId();
        this.name = validatorPlatformDO.getName();
        this.open = validatorPlatformDO.getOpen();
        this.config = validatorPlatformDO.getConfig();
        this.pluginId = validatorPlatformDO.getPluginId();
        Gson gson = new Gson();
        this.configItems = gson.fromJson(validatorPlatformDO.getConfig(), new TypeToken<List<ConfigItem>>() {
        }.getType());
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOpen() {
        return open;
    }

    public void setOpen(Integer open) {
        this.open = open;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getPluginId() {
        return pluginId;
    }

    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }

    public List<ConfigItem> getConfigItems() {
        return configItems;
    }

    public void setConfigItems(List<ConfigItem> configItems) {
        this.configItems = configItems;
    }

    @Override
    public String toString() {
        return "ValidatorPlatformVO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", open=" + open +
                ", config='" + config + '\'' +
                ", pluginId='" + pluginId + '\'' +
                ", configItems=" + configItems +
                '}';
    }
}
