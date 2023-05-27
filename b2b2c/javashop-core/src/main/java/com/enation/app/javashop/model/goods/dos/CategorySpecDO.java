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
 * 分类规格关联表实体
 *
 * @author fk
 * @version v2.0
 * @since v7.0.0
 * 2018-03-20 10:04:26
 */
@TableName("es_category_spec")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CategorySpecDO implements Serializable {

    private static final long serialVersionUID = 3127259064985991L;

    /**
     * 分类id
     */
    @ApiModelProperty(value = "分类id", required = false)
    private Long categoryId;
    /**
     * 规格id
     */
    @ApiModelProperty(value = "规格id", required = false)
    private Long specId;

    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(hidden = true)
    private Long id;

    public CategorySpecDO() {

    }

    public CategorySpecDO(Long categoryId, Long specId) {
        super();
        this.categoryId = categoryId;
        this.specId = specId;
    }


    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getSpecId() {
        return specId;
    }

    public void setSpecId(Long specId) {
        this.specId = specId;
    }

    @PrimaryKeyField
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "CategorySpecDO [categoryId=" + categoryId + ", specId=" + specId + "]";
    }


}
