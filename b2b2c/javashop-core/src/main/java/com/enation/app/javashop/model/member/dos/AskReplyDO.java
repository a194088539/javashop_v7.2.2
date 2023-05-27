package com.enation.app.javashop.model.member.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Objects;

/**
 * 会员商品咨询回复实体
 *
 * @author duanmingyu
 * @version v1.0
 * @since v7.1.5
 * 2019-09-16
 */
@TableName(value = "es_ask_reply")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AskReplyDO implements Serializable {

    private static final long serialVersionUID = 5652460062027483880L;

    /**
     * 主键
     */
    @TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(hidden = true)
    private Long id;
    /**
     * 会员咨询id
     */
    @ApiModelProperty(name = "ask_id", value = "会员咨询id", required = false)
    private Long askId;
    /**
     * 会员id
     */
    @ApiModelProperty(name = "member_id", value = "会员id", required = false)
    private Long memberId;
    /**
     * 会员名称
     */
    @ApiModelProperty(name = "member_name", value = "会员名称", required = false)
    private String memberName;
    /**
     * 回复内容
     */
    @ApiModelProperty(name = "content", value = "回复内容", required = false)
    private String content;
    /**
     * 回复时间
     */
    @ApiModelProperty(name = "reply_time", value = "回复时间", required = false)
    private Long replyTime;
    /**
     * 是否匿名 YES:是，NO:否
     */
    @ApiModelProperty(name = "anonymous", value = "是否匿名 YES:是，NO:否", required = false)
    private String anonymous;
    /**
     * 审核状态 WAIT_AUDIT:待审核,PASS_AUDIT:审核通过,REFUSE_AUDIT:审核未通过
     */
    @ApiModelProperty(name = "auth_status", value = "审核状态 WAIT_AUDIT:待审核,PASS_AUDIT:审核通过,REFUSE_AUDIT:审核未通过", required = false)
    private String authStatus;
    /**
     * 删除状态 DELETED：已删除 NORMAL：正常
     */
    @ApiModelProperty(name = "is_del", value = "删除状态 DELETED：已删除 NORMAL：正常", required = false)
    private String isDel;
    /**
     * 是否已回复 YES：是，NO：否
     */
    @ApiModelProperty(name = "reply_status", value = "是否已回复 YES：是，NO：否", required = false)
    private String replyStatus;
    /**
     * 创建时间
     */
    @ApiModelProperty(name = "create_time", value = "创建时间", required = false)
    private Long createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAskId() {
        return askId;
    }

    public void setAskId(Long askId) {
        this.askId = askId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(Long replyTime) {
        this.replyTime = replyTime;
    }

    public String getAnonymous() {
        return anonymous;
    }

    public void setAnonymous(String anonymous) {
        this.anonymous = anonymous;
    }

    public String getAuthStatus() {
        return authStatus;
    }

    public void setAuthStatus(String authStatus) {
        this.authStatus = authStatus;
    }

    public String getIsDel() {
        return isDel;
    }

    public void setIsDel(String isDel) {
        this.isDel = isDel;
    }

    public String getReplyStatus() {
        return replyStatus;
    }

    public void setReplyStatus(String replyStatus) {
        this.replyStatus = replyStatus;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AskReplyDO that = (AskReplyDO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(askId, that.askId) &&
                Objects.equals(memberId, that.memberId) &&
                Objects.equals(memberName, that.memberName) &&
                Objects.equals(content, that.content) &&
                Objects.equals(replyTime, that.replyTime) &&
                Objects.equals(anonymous, that.anonymous) &&
                Objects.equals(authStatus, that.authStatus) &&
                Objects.equals(isDel, that.isDel) &&
                Objects.equals(replyStatus, that.replyStatus) &&
                Objects.equals(createTime, that.createTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, askId, memberId, memberName, content, replyTime, anonymous, authStatus, isDel, replyStatus, createTime);
    }

    @Override
    public String toString() {
        return "AskReplyDO{" +
                "id=" + id +
                ", askId=" + askId +
                ", memberId=" + memberId +
                ", memberName='" + memberName + '\'' +
                ", content='" + content + '\'' +
                ", replyTime=" + replyTime +
                ", anonymous='" + anonymous + '\'' +
                ", authStatus='" + authStatus + '\'' +
                ", isDel='" + isDel + '\'' +
                ", replyStatus='" + replyStatus + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
