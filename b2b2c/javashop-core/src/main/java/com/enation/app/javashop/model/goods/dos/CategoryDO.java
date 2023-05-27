package com.enation.app.javashop.model.goods.dos;

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

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;


/**
 * 商品分类实体
 *
 * @author fk
 * @version v2.0
 * @since v7.0.0
 * 2018-03-15 17:22:06
 */
@TableName("es_category")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CategoryDO implements Serializable {

    private static final long serialVersionUID = 1964321416223565L;

    /**
     * 主键
     */
	@TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(hidden = true)
    private Long categoryId;

    /**
     * 分类名称
     */
    @ApiModelProperty(value = "分类名称", required = true)
    @NotEmpty(message = "分类名称不能为空")
    private String name;

    /**
     * 分类父id
     */
    @ApiModelProperty(name = "parent_id", value = "分类父id，顶 0", required = true)
    private Long parentId;

    /**
     * 分类父子路径
     */
    @ApiModelProperty(hidden = true)
    private String categoryPath;

    /**
     * 该分类下商品数量
     */
    @ApiModelProperty(hidden = true)
    private Integer goodsCount;

    /**
     * 分类排序
     */
    @ApiModelProperty(name = "category_order", value = "分类排序", required = false)
    private Integer categoryOrder;

    /**
     * 分类图标
     */
    @ApiModelProperty(value = "分类图标", required = false)
    private String image;

    /**
     * 是否显示 YES：是，NO：否
     */
    @ApiModelProperty(value = "是否显示 YES：是，NO：否", required = true, allowableValues = "YES,NO")
    private String isShow;

    /**
     * 分类广告图片
     */
    @ApiModelProperty(value = "分类广告图片", required = false)
    private String advImage;

    /**
     * 分类广告图片链接
     */
    @ApiModelProperty(value = "分类广告图片链接", required = false)
    private String advImageLink;

    @PrimaryKeyField
    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getCategoryPath() {
        return categoryPath;
    }

    public void setCategoryPath(String categoryPath) {
        this.categoryPath = categoryPath;
    }

    public Integer getGoodsCount() {
        return goodsCount;
    }

    public void setGoodsCount(Integer goodsCount) {
        this.goodsCount = goodsCount;
    }

    public Integer getCategoryOrder() {
        return categoryOrder;
    }

    public void setCategoryOrder(Integer categoryOrder) {
        this.categoryOrder = categoryOrder;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public String getIsShow() {
        return isShow;
    }

    public void setIsShow(String isShow) {
        this.isShow = isShow;
    }

    public String getAdvImage() {
        return advImage;
    }

    public void setAdvImage(String advImage) {
        this.advImage = advImage;
    }

    public String getAdvImageLink() {
        return advImageLink;
    }

    public void setAdvImageLink(String advImageLink) {
        this.advImageLink = advImageLink;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CategoryDO that = (CategoryDO) o;
        return Objects.equals(categoryId, that.categoryId) &&
                Objects.equals(name, that.name) &&
                Objects.equals(parentId, that.parentId) &&
                Objects.equals(categoryPath, that.categoryPath) &&
                Objects.equals(goodsCount, that.goodsCount) &&
                Objects.equals(categoryOrder, that.categoryOrder) &&
                Objects.equals(image, that.image) &&
                Objects.equals(isShow, that.isShow) &&
                Objects.equals(advImage, that.advImage) &&
                Objects.equals(advImageLink, that.advImageLink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryId, name, parentId, categoryPath, goodsCount, categoryOrder, image, isShow, advImage, advImageLink);
    }

    @Override
    public String toString() {
        return "CategoryDO{" +
                "categoryId=" + categoryId +
                ", name='" + name + '\'' +
                ", parentId=" + parentId +
                ", categoryPath='" + categoryPath + '\'' +
                ", goodsCount=" + goodsCount +
                ", categoryOrder=" + categoryOrder +
                ", image='" + image + '\'' +
                ", isShow='" + isShow + '\'' +
                ", advImage='" + advImage + '\'' +
                ", advImageLink='" + advImageLink + '\'' +
                '}';
    }
}
