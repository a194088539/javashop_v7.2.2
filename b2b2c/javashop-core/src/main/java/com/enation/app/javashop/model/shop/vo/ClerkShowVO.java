package com.enation.app.javashop.model.shop.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;


/**
 * 店员实体
 *
 * @author zh
 * @version v7.0.0
 * @since v7.0.0
 * 2018-08-04 18:48:39
 */
public class ClerkShowVO implements Serializable {
    /**
     * 店员id
     */
    @ApiModelProperty(value = "店员id")
    private Long clerkId;
    /**
     * 店员名称
     */
    @ApiModelProperty(value = "账号名称")
    private String uname;
    /**
     * 手机号码
     */
    @ApiModelProperty(value = "手机号码")
    private String mobile;
    /**
     * 权限
     */
    @ApiModelProperty(value = "权限")
    private String role;
    /**
     * 权限id
     */
    @ApiModelProperty(value = "权限id")
    private Long roleId;
    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱")
    private String email;
    /**
     * 会员id
     */
    @ApiModelProperty(value = "会员id")
    private Long memberId;
    /**
     * 店铺id
     */
    @ApiModelProperty(value = "店铺id")
    private Long shopId;
    /**
     * 角色标识
     */
    @ApiModelProperty(value = "1:超级店员 0:普通店员")
    private Integer founder;

    /**
     * 店员状态，-1为禁用，1为正常
     */
    @ApiModelProperty(name = "user_state", value = "店员状态，-1为禁用，0为正常")
    private Integer userState;

    public Long getClerkId() {
        return clerkId;
    }

    public void setClerkId(Long clerkId) {
        this.clerkId = clerkId;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public Integer getFounder() {
        return founder;
    }

    public void setFounder(Integer founder) {
        this.founder = founder;
    }

    public Integer getUserState() {
        return userState;
    }

    public void setUserState(Integer userState) {
        this.userState = userState;
    }

    @Override
    public String toString() {
        return "ClerkShowVO{" +
                "clerkId=" + clerkId +
                ", uname='" + uname + '\'' +
                ", mobile='" + mobile + '\'' +
                ", role='" + role + '\'' +
                ", roleId=" + roleId +
                ", email='" + email + '\'' +
                ", memberId=" + memberId +
                ", shopId=" + shopId +
                ", founder=" + founder +
                ", userState=" + userState +
                '}';
    }
}
