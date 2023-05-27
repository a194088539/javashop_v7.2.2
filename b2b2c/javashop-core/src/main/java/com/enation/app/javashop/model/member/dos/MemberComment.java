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
 * 评论实体
 *
 * @author fk
 * @version v1.0
 * @since v7.0.0
 * 2018-05-03 10:19:14
 */
@TableName(value = "es_member_comment")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MemberComment implements Serializable {

    private static final long serialVersionUID = 4761968067308018L;

    /**
     * 评论主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(hidden = true)
    private Long commentId;
    /**
     * 商品id
     */
    @ApiModelProperty(name = "goods_id", value = "商品id", required = false)
    private Long goodsId;
    /**
     * skuid
     */
    @ApiModelProperty(name = "sku_id", value = "skuid", required = false)
    private Long skuId;
    /**
     * 会员id
     */
    @ApiModelProperty(name = "member_id", value = "会员id", required = false)
    private Long memberId;
    /**
     * 卖家id
     */
    @ApiModelProperty(name = "seller_id", value = "卖家id", required = false)
    private Long sellerId;
    /**
     * 会员名称
     */
    @ApiModelProperty(name = "member_name", value = "会员名称", required = false)
    private String memberName;
    /**
     * 会员头像
     */
    @ApiModelProperty(name = "member_face", value = "会员头像", required = false)
    private String memberFace;
    /**
     * 商品名称
     */
    @ApiModelProperty(name = "goods_name", value = "商品名称", required = false)
    private String goodsName;
    /**
     * 商品默认图片
     */
    @ApiModelProperty(name = "goods_img", value = "商品默认图片", required = false)
    private String goodsImg;
    /**
     * 评论内容
     */
    @ApiModelProperty(name = "content", value = "评论内容", required = false)
    private String content;
    /**
     * 评论时间
     */
    @ApiModelProperty(name = "create_time", value = "评论时间", required = false)
    private Long createTime;
    /**
     * 是否有图片 1 有 0 没有
     */
    @ApiModelProperty(name = "have_image", value = "是否有图片 1 有 0 没有", required = false)
    private Integer haveImage;
    /**
     * 状态  1 正常 0 删除
     */
    @ApiModelProperty(name = "status", value = "状态  1 正常 0 删除 ", required = false)
    private Integer status;
    /**
     * 好中差评
     */
    @ApiModelProperty(name = "grade", value = "好中差评", required = false)
    private String grade;
    /**
     * 订单编号
     */
    @ApiModelProperty(name = "order_sn", value = "订单编号", required = false)
    private String orderSn;
    /**
     * 是否回复 1 是 0 否
     */
    @ApiModelProperty(name = "reply_status", value = "是否回复 1 是 0 否", required = false)
    private Integer replyStatus;

    @ApiModelProperty(name = "audit_status",value = "初评审核:待审核(WAIT_AUDIT),审核通过(PASS_AUDIT),审核拒绝(REFUSE_AUDIT)",required = false)
    private String auditStatus;

    @ApiModelProperty(name = "comments_type",value = "评论类型：初评(INITIAL),追评(ADDITIONAL)",required = false)
    private String commentsType;

    @ApiModelProperty(name = "parent_id",value = "初评id，默认为0",required = false)
    private Long parentId;

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberFace() {
        return memberFace;
    }

    public void setMemberFace(String memberFace) {
        this.memberFace = memberFace;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Integer getHaveImage() {
        return haveImage;
    }

    public void setHaveImage(Integer haveImage) {
        this.haveImage = haveImage;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public Integer getReplyStatus() {
        return replyStatus;
    }

    public void setReplyStatus(Integer replyStatus) {
        this.replyStatus = replyStatus;
    }


    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getCommentsType() {
        return commentsType;
    }

    public void setCommentsType(String commentsType) {
        this.commentsType = commentsType;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MemberComment that = (MemberComment) o;
        return Objects.equals(commentId, that.commentId) &&
                Objects.equals(goodsId, that.goodsId) &&
                Objects.equals(skuId, that.skuId) &&
                Objects.equals(memberId, that.memberId) &&
                Objects.equals(sellerId, that.sellerId) &&
                Objects.equals(memberName, that.memberName) &&
                Objects.equals(memberFace, that.memberFace) &&
                Objects.equals(goodsName, that.goodsName) &&
                Objects.equals(goodsImg, that.goodsImg) &&
                Objects.equals(content, that.content) &&
                Objects.equals(createTime, that.createTime) &&
                Objects.equals(haveImage, that.haveImage) &&
                Objects.equals(status, that.status) &&
                Objects.equals(grade, that.grade) &&
                Objects.equals(orderSn, that.orderSn) &&
                Objects.equals(replyStatus, that.replyStatus) &&
                Objects.equals(auditStatus, that.auditStatus) &&
                Objects.equals(commentsType, that.commentsType) &&
                Objects.equals(parentId, that.parentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commentId, goodsId, skuId, memberId, sellerId, memberName, memberFace, goodsName, goodsImg, content, createTime, haveImage, status, grade, orderSn, replyStatus, auditStatus, commentsType, parentId);
    }

    @Override
    public String toString() {
        return "MemberComment{" +
                "commentId=" + commentId +
                ", goodsId=" + goodsId +
                ", skuId=" + skuId +
                ", memberId=" + memberId +
                ", sellerId=" + sellerId +
                ", memberName='" + memberName + '\'' +
                ", memberFace='" + memberFace + '\'' +
                ", goodsName='" + goodsName + '\'' +
                ", goodsImg='" + goodsImg + '\'' +
                ", content='" + content + '\'' +
                ", createTime=" + createTime +
                ", haveImage=" + haveImage +
                ", status=" + status +
                ", grade='" + grade + '\'' +
                ", orderSn='" + orderSn + '\'' +
                ", replyStatus=" + replyStatus +
                ", auditStatus='" + auditStatus + '\'' +
                ", commentsType='" + commentsType + '\'' +
                ", parentId=" + parentId +
                '}';
    }
}
