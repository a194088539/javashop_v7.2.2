package com.enation.app.javashop.model.system.dos;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enation.app.javashop.framework.database.annotation.Column;
import com.enation.app.javashop.framework.database.annotation.Id;
import com.enation.app.javashop.framework.database.annotation.PrimaryKeyField;
import com.enation.app.javashop.framework.database.annotation.Table;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;


/**
 * 角色表实体
 *
 * @author zh
 * @version v7.0.0
 * @since v7.0.0
 * 2018-06-26 20:19:36
 */
@TableName("es_role")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RoleDO implements Serializable {

    private static final long serialVersionUID = 7874065238889473L;

    /**
     * 主键ID
     */
    @TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(hidden = true)
    private Long roleId;
    /**
     * 角色名称
     */
    @NotEmpty(message = "角色名称不能为空")
    @ApiModelProperty(name = "role_name", value = "角色名称", required = true)
    private String roleName;
    /**
     * 角色介绍
     */
    @ApiModelProperty(name = "auth_ids", value = "角色介绍", required = false)
    private String authIds;
    /**
     * 角色描述
     */
    @ApiModelProperty(name = "role_describe", value = "角色描述", required = false)
    private String roleDescribe;

    @PrimaryKeyField
    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getAuthIds() {
        return authIds;
    }

    public void setAuthIds(String authIds) {
        this.authIds = authIds;
    }

    public String getRoleDescribe() {
        return roleDescribe;
    }

    public void setRoleDescribe(String roleDescribe) {
        this.roleDescribe = roleDescribe;
    }


    @Override
    public String toString() {
        return "Role{" +
                "roleId=" + roleId +
                ", roleName='" + roleName + '\'' +
                ", authIds='" + authIds + '\'' +
                ", roleDescribe='" + roleDescribe + '\'' +
                '}';
    }


}
