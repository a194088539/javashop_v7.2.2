package com.enation.app.javashop.model.promotion.groupbuy.dos;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enation.app.javashop.framework.database.annotation.PrimaryKeyField;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.validation.constraints.NotEmpty;


/**
 * 团购分类实体
 * @author Snow
 * @version v7.0.0
 * @since v7.0.0
 * 2018-04-02 16:08:03
 */
@TableName(value = "es_groupbuy_cat")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class GroupbuyCatDO implements Serializable {

    private static final long serialVersionUID = 9105318699704423L;

    /**分类id*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(hidden=true)
    private Long catId;

    /**父类id*/
    @ApiModelProperty(name="parent_id",value="父类id",required=false)
    private Long parentId;

    /**分类名称*/
    @NotEmpty(message = "请填写分类名称")
    @ApiModelProperty(name="cat_name",value="分类名称",required=false)
    private String catName;

    /**分类结构目录*/
    @ApiModelProperty(name="cat_path",value="分类结构目录",required=false)
    private String catPath;

    /**分类排序*/
    @ApiModelProperty(name="cat_order",value="分类排序",required=false)
    private Integer catOrder;

    @PrimaryKeyField
    public Long getCatId() {
        return catId;
    }

    public void setCatId(Long catId) {
        this.catId = catId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public String getCatPath() {
        return catPath;
    }

    public void setCatPath(String catPath) {
        this.catPath = catPath;
    }

    public Integer getCatOrder() {
        return catOrder;
    }

    public void setCatOrder(Integer catOrder) {
        this.catOrder = catOrder;
    }

    @Override
    public String toString() {
        return "GroupbuyCatDO{" +
                "catId=" + catId +
                ", parentId=" + parentId +
                ", catName='" + catName + '\'' +
                ", catPath='" + catPath + '\'' +
                ", catOrder=" + catOrder +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }

        if (o == null || getClass() != o.getClass()){
            return false;
        }

        GroupbuyCatDO catDO = (GroupbuyCatDO) o;

        return new EqualsBuilder()
                .append(catId, catDO.catId)
                .append(parentId, catDO.parentId)
                .append(catName, catDO.catName)
                .append(catPath, catDO.catPath)
                .append(catOrder, catDO.catOrder)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(catId)
                .append(parentId)
                .append(catName)
                .append(catPath)
                .append(catOrder)
                .toHashCode();
    }
}
