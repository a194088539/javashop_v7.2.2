package com.enation.app.javashop.model.goods.dos;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enation.app.javashop.framework.database.annotation.Column;
import com.enation.app.javashop.framework.database.annotation.Id;
import com.enation.app.javashop.framework.database.annotation.Table;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
 * 分类品牌关联表实体
 *
 * @author fk
 * @version v2.0
 * @since v7.0.0
 * 2018-03-19 09:34:02
 */
@TableName("es_category_brand")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CategoryBrandDO implements Serializable {

    private static final long serialVersionUID = 3315719881926878L;
    /**
     * 分类id
     */
    @TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(hidden = true)
    private Long id;

    /**
     * 分类id
     */
    @ApiModelProperty(value = "分类id", required = false)
    private Long categoryId;
    /**
     * 品牌id
     */
    @ApiModelProperty(value = "品牌id", required = false)
    private Long brandId;

    public CategoryBrandDO() {

    }

    public CategoryBrandDO(Long categoryId, Long brandId) {
        super();
        this.categoryId = categoryId;
        this.brandId = brandId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    @Override
    public String toString() {
        return "CategoryBrandDO [categoryId=" + categoryId + ", brandId=" + brandId + "]";
    }

}
