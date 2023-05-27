package com.enation.app.javashop.model.base.dos;

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
import javax.validation.constraints.Min;
import javax.validation.constraints.Email;


/**
 * 邮件记录实体
 * @author zh
 * @version v7.0.0
 * @since v7.0.0
 * 2018-03-26 16:22:11
 */
@TableName("es_email")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class EmailDO implements Serializable {

    private static final long serialVersionUID = 5515821199866313L;

    /**邮件记录id*/
    @TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(hidden=true)
    private Long id;
    /**邮件标题*/
    @ApiModelProperty(name="title",value="邮件标题",required=false)
    private String title;
    /**邮件类型*/
    @ApiModelProperty(name="type",value="邮件类型",required=false)
    private String type;
    /**是否成功*/
    @Min(message="必须为数字", value = 0)
    @ApiModelProperty(name="success",value="是否成功",required=false)
    private Integer success;
    /**邮件接收者*/
    @Email(message="格式不正确")
    @ApiModelProperty(name="email",value="邮件接收者",required=false)
    private String email;
    /**邮件内容*/
    @ApiModelProperty(name="content",value="邮件内容",required=false)
    private String content;
    /**错误次数*/
    @Min(message="必须为数字", value = 0)
    @ApiModelProperty(name="error_num",value="错误次数",required=false)
    private Integer errorNum;
    /**最后发送时间*/
    @ApiModelProperty(name="last_send",value="最后发送时间",required=false)
    private Long lastSend;


    @Override
	public String toString() {
		return "EmailDO [id=" + id + ", title=" + title + ", type=" + type + ", success=" + success + ", email=" + email
				+ ", context=" + content + ", errorNum=" + errorNum + ", lastSend=" + lastSend + "]";
	}
	@PrimaryKeyField
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public Integer getSuccess() {
        return success;
    }
    public void setSuccess(Integer success) {
        this.success = success;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public Integer getErrorNum() {
        return errorNum;
    }
    public void setErrorNum(Integer errorNum) {
        this.errorNum = errorNum;
    }

    public Long getLastSend() {
        return lastSend;
    }
    public void setLastSend(Long lastSend) {
        this.lastSend = lastSend;
    }



}
