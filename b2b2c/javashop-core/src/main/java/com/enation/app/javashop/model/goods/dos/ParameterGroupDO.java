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
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


/**
 * 参数组实体
 *
 * @author fk
 * @version v2.0
 * @since v7.0.0
 * 2018-03-20 16:14:17
 */
@TableName("es_parameter_group")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ParameterGroupDO implements Serializable {

    private static final long serialVersionUID = 2394621849767871L;

    /**
     * 主键
     */
    @TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(hidden = true)
    private Long groupId;
    /**
     * 参数组名称
     */
    @ApiModelProperty(name = "group_name", value = "参数组名称", required = true)
    @NotEmpty(message = "参数组名称不能为空")
    @Length(max = 50, message = "参数组名称不能超过50字")
    private String groupName;
    /**
     * 关联分类id
     */
    @ApiModelProperty(name = "category_id", value = "关联分类id", required = true)
    private Long categoryId;
    /**
     * 排序
     */
    @ApiModelProperty(hidden = true)
    private Integer sort;

    @PrimaryKeyField
    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    @Override
    public String toString() {
        return "ParameterGroupDO [groupId=" + groupId + ", groupName=" + groupName + ", categoryId=" + categoryId
                + ", sort=" + sort + "]";
    }

}
