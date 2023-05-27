package com.enation.app.javashop.model.member.vo;

/**
 * 商家登录后返回信息
 *
 * @author zh
 * @version v7.0
 * @date 18/8/15 下午1:54
 * @since v7.0
 */
public class SellerInfoVO extends MemberVO {

    /**
     * 角色id
     */
    private Long roleId;
    /**
     * 是否是店主
     */
    private Integer founder;

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Integer getFounder() {
        return founder;
    }

    public void setFounder(Integer founder) {
        this.founder = founder;
    }

    @Override
    public String toString() {
        return "SellerInfoVO{" +
                "roleId=" + roleId +
                ", founder=" + founder +
                '}';
    }
}
