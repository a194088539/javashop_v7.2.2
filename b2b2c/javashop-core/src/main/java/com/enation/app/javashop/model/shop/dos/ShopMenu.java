package com.enation.app.javashop.model.shop.dos;

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
 * 菜单管理店铺实体
 *
 * @author zh
 * @version v7.0.0
 * @since v7.0.0
 * 2018-08-02 15:38:40
 */
@TableName(value = "es_shop_menu")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ShopMenu implements Serializable {

    private static final long serialVersionUID = 7395447372130268L;
    /**店铺菜单id*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(hidden=true)
    private Long id;
    /**菜单父id*/
    @ApiModelProperty(name="parent_id",value="菜单父id",required=false)
    private Long parentId;
    /**菜单标题*/
    @ApiModelProperty(name="title",value="菜单标题",required=false)
    private String title;
    /**菜单唯一标识*/
    @ApiModelProperty(name="identifier",value="菜单唯一标识",required=false)
    private String identifier;
    /**权限表达式*/
    @ApiModelProperty(name="auth_regular",value="权限表达式",required=false)
    private String authRegular;
    /**删除标记*/
    @ApiModelProperty(name="delete_flag",value="删除标记",required=false)
    private Integer deleteFlag;
    /**菜单级别标识*/
    @ApiModelProperty(name="path",value="菜单级别标识",required=false)
    private String path;
    /**菜单级别*/
    @ApiModelProperty(name="grade",value="菜单级别",required=false)
    private Integer grade;

    @PrimaryKeyField
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getIdentifier() {
        return identifier;
    }
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getAuthRegular() {
        return authRegular;
    }
    public void setAuthRegular(String authRegular) {
        this.authRegular = authRegular;
    }

    public Integer getDeleteFlag() {
        return deleteFlag;
    }
    public void setDeleteFlag(Integer deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }

    public Integer getGrade() {
        return grade;
    }
    public void setGrade(Integer grade) {
        this.grade = grade;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        ShopMenu that = (ShopMenu) o;
        if (id != null ? !id.equals(that.id) : that.id != null) {return false;}
        if (parentId != null ? !parentId.equals(that.parentId) : that.parentId != null) {return false;}
        if (title != null ? !title.equals(that.title) : that.title != null) {return false;}
        if (identifier != null ? !identifier.equals(that.identifier) : that.identifier != null) {return false;}
        if (authRegular != null ? !authRegular.equals(that.authRegular) : that.authRegular != null) {return false;}
        if (deleteFlag != null ? !deleteFlag.equals(that.deleteFlag) : that.deleteFlag != null) {return false;}
        if (path != null ? !path.equals(that.path) : that.path != null) {return false;}
        return grade != null ? grade.equals(that.grade) : that.grade == null;
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (parentId != null ? parentId.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (identifier != null ? identifier.hashCode() : 0);
        result = 31 * result + (authRegular != null ? authRegular.hashCode() : 0);
        result = 31 * result + (deleteFlag != null ? deleteFlag.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (grade != null ? grade.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ShopMenu{" +
                "id=" + id +
                ", parentId=" + parentId +
                ", title='" + title + '\'' +
                ", identifier='" + identifier + '\'' +
                ", authRegular='" + authRegular + '\'' +
                ", deleteFlag=" + deleteFlag +
                ", path='" + path + '\'' +
                ", grade=" + grade +
                '}';
    }


}
