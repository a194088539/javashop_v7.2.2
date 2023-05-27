package com.enation.app.javashop.model.goods.dos;

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


/**
 * 商品标签实体
 *
 * @author fk
 * @version v2.0
 * @since v7.0.0
 * 2018-03-28 14:49:36
 */
@TableName("es_tags")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TagsDO implements Serializable {

    private static final long serialVersionUID = 1899720595535600L;

    /**
     * 主键
     */
    @TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(hidden = true)
    private Long tagId;
    /**
     * 标签名字
     */
    @ApiModelProperty(name = "tag_name", value = "标签名字", required = false)
    private String tagName;
    /**
     * 所属卖家
     */
    @ApiModelProperty(name = "seller_id", value = "所属卖家", required = false)
    private Long sellerId;
    /**
     * 关键字
     */
    @ApiModelProperty(name = "mark", value = "关键字", required = false)
    private String mark;


    public TagsDO() {
    }

    public TagsDO(String tagName, Long sellerId, String mark) {
        super();
        this.tagName = tagName;
        this.sellerId = sellerId;
        this.mark = mark;
    }


    @PrimaryKeyField
    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    @Override
    public String toString() {
        return "TagsDO{" +
                "tagId=" + tagId +
                ", tagName='" + tagName + '\'' +
                ", sellerId=" + sellerId +
                ", mark='" + mark + '\'' +
                '}';
    }
}
