package com.enation.app.javashop.model.goods.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enation.app.javashop.framework.database.annotation.PrimaryKeyField;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;


/**
 * 品牌实体
 *
 * @author fk
 * @version v2.0
 * @since v7.0.0
 * 2018-03-16 16:32:45
 */
@TableName("es_brand")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class BrandDO implements Serializable {

    private static final long serialVersionUID = 9122931201151887L;

    /**
     * 主键
     */
    @TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(hidden = true)
    private Long brandId;
    /**
     * 品牌名称
     */
    @NotEmpty(message = "品牌名称不能为空")
    @ApiModelProperty(value = "品牌名称", required = true)
    private String name;
    /**
     * 品牌图标
     */
    @NotEmpty(message = "品牌图标不能为空")
    @ApiModelProperty(value = "品牌图标", required = true)
    private String logo;
    /**
     * 是否删除，0删除1未删除
     */
    @ApiModelProperty(hidden = true)
    private Integer disabled;

    @PrimaryKeyField
    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Integer getDisabled() {
        return disabled;
    }

    public void setDisabled(Integer disabled) {
        this.disabled = disabled;
    }

    @Override
    public String toString() {
        return "[brandId=" + brandId + ", name=" + name + ", logo=" + logo + ", disabled=" + disabled + "]";
    }

}
