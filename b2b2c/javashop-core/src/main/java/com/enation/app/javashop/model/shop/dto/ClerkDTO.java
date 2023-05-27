package com.enation.app.javashop.model.shop.dto;

import com.enation.app.javashop.framework.validation.annotation.Mobile;
import io.swagger.annotations.ApiModelProperty;


/**
 * 店员DTO
 *
 * @author zh
 * @version v7.0.0
 * @since v7.0.0
 * 2018-08-04 18:48:39
 */
public class ClerkDTO {
    /**
     * 手机号码
     */
    @Mobile(message = "手机格式不正确")
    @ApiModelProperty(value = "手机号码", required = true)
    private String mobile;
    /**
     * 权限id
     */
    @ApiModelProperty(name = "role_id", value = "角色id,如果是店主则传0", required = true)
    private Long roleId;


    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}
