package com.enation.app.javashop.model.member.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;


/**
 * 评论图片实体
 * @author fk
 * @version v1.0
 * @since v7.0.0
 * 2018-05-03 14:11:46
 */
@TableName(value = "es_comment_gallery")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CommentGallery implements Serializable {

    private static final long serialVersionUID = 8048552718399969L;

    /**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(hidden=true)
    private Long imgId;
    /**主键*/
    @ApiModelProperty(name="comment_id",value="主键",required=false)
    private Long commentId;
    /**图片路径*/
    @ApiModelProperty(name="original",value="图片路径",required=false)
    private String original;
    /**排序*/
    @ApiModelProperty(name="sort",value="排序",required=false)
    private Integer sort;

    public Long getImgId() {
        return imgId;
    }
    public void setImgId(Long imgId) {
        this.imgId = imgId;
    }

    public Long getCommentId() {
        return commentId;
    }
    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public String getOriginal() {
        return original;
    }
    public void setOriginal(String original) {
        this.original = original;
    }

    public Integer getSort() {
        return sort;
    }
    public void setSort(Integer sort) {
        this.sort = sort;
    }


    @Override
    public String toString() {
        return "CommentGallery{" +
                "imgId=" + imgId +
                ", commentId=" + commentId +
                ", original='" + original + '\'' +
                ", sort=" + sort +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CommentGallery that = (CommentGallery) o;

        return new EqualsBuilder()
                .append(imgId, that.imgId)
                .append(commentId, that.commentId)
                .append(original, that.original)
                .append(sort, that.sort)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(imgId)
                .append(commentId)
                .append(original)
                .append(sort)
                .toHashCode();
    }
}
