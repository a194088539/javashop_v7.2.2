package com.enation.coder.model.po;

import org.hibernate.validator.constraints.NotBlank;

import com.enation.coder.model.enums.Role;
import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;


/**
 * 管理员
 */

public class AdminUser   {

	private Integer userid;

	@NotBlank(message = "请输入用户名")
	private String username;


	private String password;
	private int state;

	@NotBlank(message = "请输入真实姓名")
	private String realname;
	private String remark;
	private Long dateline;

	@NotBlank(message = "请选择角色")
	private String role;

	@PrimaryKeyField
	public Integer getUserid() {
		return userid;
	}

	public void setUserid(Integer userid) {
		this.userid = userid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Long getDateline() {
		return dateline;
	}

	public void setDateline(Long dateline) {
		this.dateline = dateline;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@NotDbField
	public  String getRole_name(){
		return Role.valueOf(role).getName();
	}

	@Override
	public String toString() {
		return "AdminUser{" +
				"userid=" + userid +
				", username='" + username + '\'' +
				", password='" + password + '\'' +
				", state=" + state +
				", realname='" + realname + '\'' +
				", remark='" + remark + '\'' +
				", dateline=" + dateline +
				", role='" + role + '\'' +
				'}';
	}
}
