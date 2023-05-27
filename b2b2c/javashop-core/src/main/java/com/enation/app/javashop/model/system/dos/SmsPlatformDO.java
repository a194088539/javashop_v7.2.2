package com.enation.app.javashop.model.system.dos;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enation.app.javashop.model.system.vo.SmsPlatformVO;
import com.enation.app.javashop.framework.database.annotation.Column;
import com.enation.app.javashop.framework.database.annotation.Id;
import com.enation.app.javashop.framework.database.annotation.PrimaryKeyField;
import com.enation.app.javashop.framework.database.annotation.Table;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.gson.Gson;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Min;


/**
 * 短信网关表实体
 * @author zh
 * @version v7.0.0
 * @since v7.0.0
 * 2018-03-23 11:31:05
 */
@TableName("es_sms_platform")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SmsPlatformDO implements Serializable {

	private static final long serialVersionUID = 5431942203889125L;

	/**主键ID*/
	@TableId(type= IdType.ASSIGN_ID)
	@ApiModelProperty(hidden=true)
	private Long id;
	/**平台名称*/
	@NotEmpty(message="平台名称不能为空")
	@ApiModelProperty(name="name",value="平台名称",required=true)
	private String name;
	/**是否开启*/
	@Min(message="必须为数字", value = 0)
	@ApiModelProperty(name="open",value="是否开启",required=false)
	private Integer open;
	/**配置*/
	@ApiModelProperty(name="config",value="配置",required=false)
	private String config;
	/**编码*/
	@NotEmpty(message="插件id")
	@ApiModelProperty(name="bean",value="编码",required=true)
	private String bean;

	public SmsPlatformDO(SmsPlatformVO smsPlatformVO) {
		this.id = smsPlatformVO.getId();
		this.name = smsPlatformVO.getName();
		this.open = smsPlatformVO.getOpen();
		this.bean = smsPlatformVO.getBean();
		Gson gson = new Gson();
		this.config = gson.toJson(smsPlatformVO.getConfigItems());
	}
	public SmsPlatformDO() {

	}


	@Override
	public String toString() {
		return "PlatformDO [id=" + id + ", name=" + name + ", open=" + open + ", config=" + config + ", bean=" + bean
				+ "]";
	}
	@PrimaryKeyField
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



}
