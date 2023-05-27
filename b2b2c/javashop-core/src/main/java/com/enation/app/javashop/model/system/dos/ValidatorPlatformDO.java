package com.enation.app.javashop.model.system.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enation.app.javashop.model.system.vo.ValidatorPlatformVO;
import com.enation.app.javashop.framework.database.annotation.Column;
import com.enation.app.javashop.framework.database.annotation.Id;
import com.enation.app.javashop.framework.database.annotation.Table;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.gson.Gson;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Objects;

/**
 * 验证平台实体
 * @author duanmingyu
 * @version v1.0
 * @since v7.1.6
 * 2019-12-18
 */
@TableName("es_validator_platform")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ValidatorPlatformDO implements Serializable {

    private static final long serialVersionUID = -2450741404998439310L;

    /**
     * 主键ID
     */
    @TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(hidden = true)
    private Long id;
    /**
     * 验证平台名称
     */
    @ApiModelProperty(name = "name", value = "验证平台名称", required = false)
    private String name;
    /**
     * 是否开启验证平台,1开启，0未开启
     */
    @ApiModelProperty(name = "open", value = "是否开启验证平台,1开启，0未开启", required = false)
    private Integer open;
    /**
     * 验证平台配置
     */
    @ApiModelProperty(name = "config", value = "验证平台配置", required = false)
    private String config;
    /**
     * 验证平台插件ID
     */
    @ApiModelProperty(name = "plugin_id", value = "验证平台插件ID", required = false)
    private String pluginId;

    public ValidatorPlatformDO() {

    }

    public ValidatorPlatformDO(ValidatorPlatformVO validatorPlatformVO) {
        this.id = validatorPlatformVO.getId();
        this.name = validatorPlatformVO.getName();
        this.open = validatorPlatformVO.getOpen();
        this.pluginId = validatorPlatformVO.getPluginId();
        Gson gson = new Gson();
        this.config = gson.toJson(validatorPlatformVO.getConfigItems());
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ValidatorPlatformDO that = (ValidatorPlatformDO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(open, that.open) &&
                Objects.equals(config, that.config) &&
                Objects.equals(pluginId, that.pluginId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, open, config, pluginId);
    }

    @Override
    public String toString() {
        return "ValidatorPlatformDO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", open=" + open +
                ", config='" + config + '\'' +
                ", pluginId='" + pluginId + '\'' +
                '}';
    }
}
