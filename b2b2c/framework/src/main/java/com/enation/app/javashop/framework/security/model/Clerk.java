package com.enation.app.javashop.framework.security.model;

/**
 * 店员
 *
 * @author zh
 * @version v7.0
 * @date 18/8/4 下午3:12
 * @since v7.0
 */

public class Clerk extends Seller {

    /**
     * 店员id
     */
    private Integer clerkId;
    /**
     * 店员名称
     */
    private String clerkName;
    /**
     * 是否是超级店员
     */
    private Integer founder;


    public Clerk() {
        //clerk有 买家的角色和卖宾角色
        add(Role.CLERK.name());
    }

    public Integer getClerkId() {
        return clerkId;
    }

    public void setClerkId(Integer clerkId) {
        this.clerkId = clerkId;
    }

    public String getClerkName() {
        return clerkName;
    }

    public void setClerkName(String clerkName) {
        this.clerkName = clerkName;
    }

    public Integer getFounder() {
        return founder;
    }

    public void setFounder(Integer founder) {
        this.founder = founder;
    }


    @Override
    public String toString() {
        return "Clerk{" +
                "clerkId=" + clerkId +
                ", clerkName='" + clerkName + '\'' +
                ", founder=" + founder +
                '}';
    }
}
