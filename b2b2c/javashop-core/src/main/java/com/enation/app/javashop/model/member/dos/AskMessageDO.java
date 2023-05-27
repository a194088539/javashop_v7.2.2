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
 * 会员问题咨询消息实体
 *
 * @author duanmingyu
 * @version v2.0
 * @since v7.1.5
 * 2019-09-16
 */
@TableName(value = "es_ask_message")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AskMessageDO implements Serializable {

    private static final long serialVersionUID = 8000767425453427506L;

    /**
     * 主键
     */
    @TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(hidden = true)
    private Long id;
    /**
     * 会员id
     */
    @ApiModelProperty(name = "member_id", value = "会员id", required = false)
    private Long memberId;
    /**
     * 商品id
     */
    @ApiModelProperty(name = "goods_id", value = "商品id", required = false)
    private Long goodsId;
    /**
     * 商品名称
     */
    @ApiModelProperty(name = "goods_name", value = "商品名称", required = false)
    private String goodsName;
    /**
     * 商品图片
     */
    @ApiModelProperty(name = "goods_img", value = "商品图片", required = false)
    private String goodsImg;
    /**
     * 会员咨询id
     */
    @ApiModelProperty(name = "ask_id", value = "会员咨询id", required = false)
    private Long askId;
    /**
     * 咨询内容
     */
    @ApiModelProperty(name = "ask", value = "咨询内容", required = false)
    private String ask;
    /**
     * 咨询会员
     */
    @ApiModelProperty(name = "ask_member", value = "咨询会员", required = false)
    private String askMember;
    /**
     * 会员回复咨询id
     */
    @ApiModelProperty(name = "reply_id", value = "会员回复咨询id", required = false)
    private Long replyId;
    /**
     * 回复内容
     */
    @ApiModelProperty(name = "reply", value = "回复内容", required = false)
    private String reply;
    /**
     * 回复会员
     */
    @ApiModelProperty(name = "reply_member", value = "回复会员", required = false)
    private String replyMember;
    /**
     * 消息发送时间
     */
    @ApiModelProperty(name = "send_time", value = "消息发送时间", required = false)
    private Long sendTime;
    /**
     * 删除状态 DELETED：已删除 NORMAL：正常
     */
    @ApiModelProperty(name = "is_del", value = "删除状态 DELETED：已删除 NORMAL：正常", required = false)
    private String isDel;
    /**
     * 是否已读 YES：是 NO：否
     */
    @ApiModelProperty(name = "is_read", value = "回复会员", required = false)
    private String isRead;
    /**
     * 消息接收时间
     */
    @ApiModelProperty(name = "receive_time", value = "消息接收时间", required = false)
    private Long receiveTime;
    /**
     * 咨询消息类型 ASK：提问消息 REPLY：回复消息
     */
    @ApiModelProperty(name = "msg_type", value = "咨询消息类型 ASK：提问消息 REPLY：回复消息", required = false)
    private String msgType;

    /**
     * 咨询人是否匿名 YES:是，NO:否
     */
    @ApiModelProperty(name = "ask_anonymous", value = "咨询人是否匿名 YES:是，NO:否", required = false)
    private String askAnonymous;

    /**
     * 回复咨询人是否匿名 YES:是，NO:否
     */
    @ApiModelProperty(name = "reply_anonymous", value = "回复咨询人是否匿名 YES:是，NO:否", required = false)
    private String replyAnonymous;

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

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsImg() {
        return goodsImg;
    }

    public void setGoodsImg(String goodsImg) {
        this.goodsImg = goodsImg;
    }

    public Long getAskId() {
        return askId;
    }

    public void setAskId(Long askId) {
        this.askId = askId;
    }

    public String getAsk() {
        return ask;
    }

    public void setAsk(String ask) {
        this.ask = ask;
    }

    public String getAskMember() {
        return askMember;
    }

    public void setAskMember(String askMember) {
        this.askMember = askMember;
    }

    public Long getReplyId() {
        return replyId;
    }

    public void setReplyId(Long replyId) {
        this.replyId = replyId;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getReplyMember() {
        return replyMember;
    }

    public void setReplyMember(String replyMember) {
        this.replyMember = replyMember;
    }

    public Long getSendTime() {
        return sendTime;
    }

    public void setSendTime(Long sendTime) {
        this.sendTime = sendTime;
    }

    public String getIsDel() {
        return isDel;
    }

    public void setIsDel(String isDel) {
        this.isDel = isDel;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    public Long getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Long receiveTime) {
        this.receiveTime = receiveTime;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getAskAnonymous() {
        return askAnonymous;
    }

    public void setAskAnonymous(String askAnonymous) {
        this.askAnonymous = askAnonymous;
    }

    public String getReplyAnonymous() {
        return replyAnonymous;
    }

    public void setReplyAnonymous(String replyAnonymous) {
        this.replyAnonymous = replyAnonymous;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AskMessageDO that = (AskMessageDO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(memberId, that.memberId) &&
                Objects.equals(goodsId, that.goodsId) &&
                Objects.equals(goodsName, that.goodsName) &&
                Objects.equals(goodsImg, that.goodsImg) &&
                Objects.equals(askId, that.askId) &&
                Objects.equals(ask, that.ask) &&
                Objects.equals(askMember, that.askMember) &&
                Objects.equals(replyId, that.replyId) &&
                Objects.equals(reply, that.reply) &&
                Objects.equals(replyMember, that.replyMember) &&
                Objects.equals(sendTime, that.sendTime) &&
                Objects.equals(isDel, that.isDel) &&
                Objects.equals(isRead, that.isRead) &&
                Objects.equals(receiveTime, that.receiveTime) &&
                Objects.equals(msgType, that.msgType) &&
                Objects.equals(askAnonymous, that.askAnonymous) &&
                Objects.equals(replyAnonymous, that.replyAnonymous);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, memberId, goodsId, goodsName, goodsImg, askId, ask, askMember, replyId, reply, replyMember, sendTime, isDel, isRead, receiveTime, msgType, askAnonymous, replyAnonymous);
    }

    @Override
    public String toString() {
        return "AskMessageDO{" +
                "id=" + id +
                ", memberId=" + memberId +
                ", goodsId=" + goodsId +
                ", goodsName='" + goodsName + '\'' +
                ", goodsImg='" + goodsImg + '\'' +
                ", askId=" + askId +
                ", ask='" + ask + '\'' +
                ", askMember='" + askMember + '\'' +
                ", replyId=" + replyId +
                ", reply='" + reply + '\'' +
                ", replyMember='" + replyMember + '\'' +
                ", sendTime=" + sendTime +
                ", isDel='" + isDel + '\'' +
                ", isRead='" + isRead + '\'' +
                ", receiveTime=" + receiveTime +
                ", msgType='" + msgType + '\'' +
                ", askAnonymous='" + askAnonymous + '\'' +
                ", replyAnonymous='" + replyAnonymous + '\'' +
                '}';
    }
}
