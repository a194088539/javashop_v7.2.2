package com.enation.app.javashop.framework.security.message;

import com.enation.app.javashop.framework.security.model.Role;

import java.io.Serializable;

/**
 * 用户禁用消息
 * @author kingapex
 * @version 1.0
 * @since 7.2.0
 * 2019/12/27
 */
public class UserDisableMsg implements Serializable {

    private static final long serialVersionUID = 6638950830083461648L;

    private long uid;
    private Role role;
    /**
     * 操作
     */
    private Integer operation;

    /**
     * 添加操作
     */
    public final static Integer ADD = 1;

    /**
     * 删除操作
     */
    public final static Integer DELETE = 2;

    public UserDisableMsg() {
    }

    public UserDisableMsg(long uid, Role role, int operation) {
        this.uid = uid;
        this.role = role;
        this.operation = operation;
    }

    public long getUid() {
        return uid;
    }

    public Role getRole() {
        return role;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Integer getOperation() {
        return operation;
    }

    public void setOperation(Integer operation) {
        this.operation = operation;
    }

    @Override
    public String toString() {
        return "UserDisableMsg{" +
                "uid=" + uid +
                ", role=" + role +
                ", operation=" + operation +
                '}';
    }
}
