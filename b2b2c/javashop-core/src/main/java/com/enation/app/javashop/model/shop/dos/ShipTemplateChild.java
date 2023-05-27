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
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;


/**
 * 模版详细配置
 *
 * @author fk
 * @version v1.0
 * @since v7.0.0
 * 2018-08-22 15:10:51
 */
@TableName(value = "es_ship_template_child")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ShipTemplateChild implements Serializable {

    /**
     * <p>
     * Title:
     * </p>
     * <p>
     * Description:
     * </p>
     */
    private static final long serialVersionUID = -2310849247997108107L;

    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(hidden = true)
    private Long id;

    @ApiModelProperty(hidden = true)
    private Long templateId;

    @ApiParam("首重／首件")
    private Double firstCompany;

    @ApiParam("运费")
    private Double firstPrice;

    @ApiParam("续重／需件")
    private Double continuedCompany;

    @ApiParam("续费")
    private Double continuedPrice;

    @ApiParam("地区‘，‘分隔   示例参数：北京，山西，天津，上海")
    private String area;

    @ApiModelProperty(value = "地区id‘，‘分隔  示例参数：1，2，3，4 ", hidden = true)
    private String areaId;

    @ApiModelProperty(value = "是否免运费  true：免运费，false：不免运费 ", hidden = true)
    private boolean isFree;

    public Double getFirstCompany() {
        return firstCompany;
    }

    public void setFirstCompany(Double firstCompany) {
        this.firstCompany = firstCompany;
    }

    public Double getFirstPrice() {
        return firstPrice;
    }

    public void setFirstPrice(Double firstPrice) {
        this.firstPrice = firstPrice;
    }

    public Double getContinuedCompany() {
        return continuedCompany;
    }

    public void setContinuedCompany(Double continuedCompany) {
        this.continuedCompany = continuedCompany;
    }

    public Double getContinuedPrice() {
        return continuedPrice;
    }

    public void setContinuedPrice(Double continuedPrice) {
        this.continuedPrice = continuedPrice;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    @PrimaryKeyField
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public boolean getIsFree() {
        return isFree;
    }

    public void setIsFree(boolean free) {
        isFree = free;
    }

    @Override
    public String toString() {
        return "ShipTemplateChild{" +
                "id=" + id +
                ", templateId=" + templateId +
                ", firstCompany=" + firstCompany +
                ", firstPrice=" + firstPrice +
                ", continuedCompany=" + continuedCompany +
                ", continuedPrice=" + continuedPrice +
                ", area='" + area + '\'' +
                ", areaId='" + areaId + '\'' +
                ", isFree=" + isFree +
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
        ShipTemplateChild that = (ShipTemplateChild) o;

        return new EqualsBuilder()
                .append(isFree, that.isFree)
                .append(id, that.id)
                .append(templateId, that.templateId)
                .append(firstCompany, that.firstCompany)
                .append(firstPrice, that.firstPrice)
                .append(continuedCompany, that.continuedCompany)
                .append(continuedPrice, that.continuedPrice)
                .append(area, that.area)
                .append(areaId, that.areaId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(templateId)
                .append(firstCompany)
                .append(firstPrice)
                .append(continuedCompany)
                .append(continuedPrice)
                .append(area)
                .append(areaId)
                .append(isFree)
                .toHashCode();
    }

}
