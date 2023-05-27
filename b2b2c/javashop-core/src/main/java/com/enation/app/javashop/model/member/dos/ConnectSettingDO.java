package com.enation.app.javashop.model.member.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enation.app.javashop.framework.database.annotation.Column;
import com.enation.app.javashop.framework.database.annotation.Id;
import com.enation.app.javashop.framework.database.annotation.Table;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * @author zjp
 * @version v7.0
 * @Description 信任登录后台参数DO
 * @ClassName ConnectSettingDO
 * @since v7.0 下午2:43 2018/6/20
 */
@TableName(value = "es_connect_setting")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ConnectSettingDO  implements Serializable {

    /**
     * Id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(name = "id",value = "id")
    private Long id;

    /**
     * 参数配置名称
     */
    @ApiModelProperty(name = "name", value = "参数配置名称")
    @NotEmpty(message="参数配置名称必填")
    private String name;
    /**
     * 信任登录类型
     */
    @ApiModelProperty(name = "type", value = "授权类型",allowableValues = "QQ,WEIBO,WECHAT,ALIPAY")
    private String type;
    /**
     * 信任登录配置参数
     */
    @ApiModelProperty(name = "config", value = "信任登录配置参数")
    private String config;


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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getConfig() {
        return config;
    }

    @Override
    public String toString() {
        return "ConnectSettingDO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", config='" + config + '\'' +
                '}';
    }
}
