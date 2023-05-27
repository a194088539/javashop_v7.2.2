package com.enation.app.javashop.model.member.dos;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enation.app.javashop.framework.database.annotation.PrimaryKeyField;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
 * 会员站内消息历史实体
 *
 * @author zh
 * @version v7.0.0
 * @since v7.0.0
 * 2018-07-05 14:10:16
 */

/**
 * 会员站内消息历史实体
 *
 * @author zh
 * @version v7.0.0
 * @since v7.0.0
 * 2018-07-23 18:11:21
 */
@TableName(value = "es_member_notice_log")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MemberNoticeLog implements Serializable {

    private static final long serialVersionUID = 2703003634632585L;

    /**
     * 会员历史消息id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(hidden = true)
    private Long id;
    /**
     * 会员id
     */
    @ApiModelProperty(name = "member_id", value = "会员id", required = false)
    private Long memberId;
    /**
     * 站内信内容
     */
    @ApiModelProperty(name = "content", value = "站内信内容", required = false)
    private String content;
    /**
     * 发送时间
     */
    @ApiModelProperty(name = "send_time", value = "发送时间", required = false)
    private Long sendTime;
    /**
     * 是否删除，0删除，1没有删除
     */
    @ApiModelProperty(name = "is_del", value = "是否删除，0删除，1没有删除", required = false)
    private Integer isDel;
    /**
     * 是否已读，0未读，1已读
     */
    @ApiModelProperty(name = "is_read", value = "是否已读，0未读，1已读", required = false)
    private Integer isRead;
    /**
     * 接收时间
     */
    @ApiModelProperty(name = "receive_time", value = "接收时间", required = false)
    private Long receiveTime;
    /**
     * 标题
     */
    @ApiModelProperty(name = "title", value = "标题", required = false)
    private String title;

    @PrimaryKeyField
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getSendTime() {
        return sendTime;
    }

    public void setSendTime(Long sendTime) {
        this.sendTime = sendTime;
    }

    public Integer getIsDel() {
        return isDel;
    }

    public void setIsDel(Integer isDel) {
        this.isDel = isDel;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }

    public Long getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Long receiveTime) {
        this.receiveTime = receiveTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MemberNoticeLog that = (MemberNoticeLog) o;
        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (memberId != null ? !memberId.equals(that.memberId) : that.memberId != null) {
            return false;
        }
        if (content != null ? !content.equals(that.content) : that.content != null) {
            return false;
        }
        if (sendTime != null ? !sendTime.equals(that.sendTime) : that.sendTime != null) {
            return false;
        }
        if (isDel != null ? !isDel.equals(that.isDel) : that.isDel != null) {
            return false;
        }
        if (isRead != null ? !isRead.equals(that.isRead) : that.isRead != null) {
            return false;
        }
        if (receiveTime != null ? !receiveTime.equals(that.receiveTime) : that.receiveTime != null) {
            return false;
        }
        return title != null ? title.equals(that.title) : that.title == null;
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (memberId != null ? memberId.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (sendTime != null ? sendTime.hashCode() : 0);
        result = 31 * result + (isDel != null ? isDel.hashCode() : 0);
        result = 31 * result + (isRead != null ? isRead.hashCode() : 0);
        result = 31 * result + (receiveTime != null ? receiveTime.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MemberNoticeLog{" +
                "id=" + id +
                ", memberId=" + memberId +
                ", content='" + content + '\'' +
                ", sendTime=" + sendTime +
                ", isDel=" + isDel +
                ", isRead=" + isRead +
                ", receiveTime=" + receiveTime +
                ", title='" + title + '\'' +
                '}';
    }


}
