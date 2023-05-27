package com.enation.app.javashop.model.system.vo;

import com.enation.app.javashop.model.base.vo.ConfigItem;
import com.enation.app.javashop.model.system.dos.ExpressPlatformDO;
import com.enation.app.javashop.framework.database.annotation.Column;
import com.enation.app.javashop.framework.database.annotation.Id;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;


/**
 * 快递平台实体
 *
 * @author zh
 * @version v7.0.0
 * @since v7.0.0
 * 2018-07-11 14:42:50
 */

public class ExpressPlatformVO implements Serializable {


    private static final long serialVersionUID = -6909967652948921476L;
    /**
     * 快递平台id
     */
    @Id(name = "id")
    @ApiModelProperty(hidden = true)
    private Long id;
    /**
     * 快递平台名称
     */
    @Column(name = "name")
    @ApiModelProperty(name = "name", value = "快递平台名称", required = false)
    private String name;
    /**
     * 是否开启快递平台,1开启，0未开启
     */
    @Column(name = "open")
    @ApiModelProperty(name = "open", value = "是否开启快递平台,1开启，0未开启", required = false)
    private Integer open;
    /**
     * 快递平台配置
     */
    @Column(name = "config")
    @ApiModelProperty(name = "config", value = "快递平台配置", required = false)
    private String config;
    /**
     * 快递平台beanid
     */
    @Column(name = "bean")
    @ApiModelProperty(name = "bean", value = "快递平台beanid", required = false)
    private String bean;
    /**
     * 快递平台配置项
     */
    @ApiModelProperty(name = "configItems", value = "快递平台配置项", required = true)
    private List<ConfigItem> configItems;

    public ExpressPlatformVO(ExpressPlatformDO expressPlatformDO) {
        this.id = expressPlatformDO.getId();
        this.name = expressPlatformDO.getName();
        this.open = expressPlatformDO.getOpen();
        this.config = expressPlatformDO.getConfig();
        this.bean = expressPlatformDO.getBean();
        Gson gson = new Gson();
        this.configItems = gson.fromJson(expressPlatformDO.getConfig(), new TypeToken<List<ConfigItem>>() {
        }.getType());
    }

    public ExpressPlatformVO() {

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

    public String getBean() {
        return bean;
    }

    public void setBean(String bean) {
        this.bean = bean;
    }

    public List<ConfigItem> getConfigItems() {
        return configItems;
    }

    public void setConfigItems(List<ConfigItem> configItems) {
        this.configItems = configItems;
    }

    @Override
    public String toString() {
        return "ExpressPlatformVO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", open=" + open +
                ", config='" + config + '\'' +
                ", bean='" + bean + '\'' +
                ", configItems=" + configItems +
                '}';
    }
}
