package com.enation.app.javashop.model.system.dos;

import java.io.Serializable;
import java.util.ArrayList;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enation.app.javashop.model.member.vo.RegionVO;
import com.enation.app.javashop.framework.database.annotation.Column;
import com.enation.app.javashop.framework.database.annotation.Id;
import com.enation.app.javashop.framework.database.annotation.PrimaryKeyField;
import com.enation.app.javashop.framework.database.annotation.Table;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
 * 地区实体
 *
 * @author zh
 * @version v7.0.0
 * @since v7.0.0
 * 2018-04-28 13:49:38
 */
@TableName("es_regions")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Regions implements Serializable {

    private static final long serialVersionUID = 8051779001011335L;

    /**
     * 地区id
     */
    @TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(hidden = true)
    private Long id;
    /**
     * 父地区id
     */
    @ApiModelProperty(name = "parent_id", value = "父地区id", required = false)
    private Long parentId;
    /**
     * 路径
     */
    @ApiModelProperty(name = "region_path", value = "路径", required = false)
    private String regionPath;
    /**
     * 级别
     */
    @ApiModelProperty(name = "region_grade", value = "级别", required = false)
    private Integer regionGrade;
    /**
     * 名称
     */
    @ApiModelProperty(name = "local_name", value = "名称", required = false)
    private String localName;
    /**
     * 邮编
     */
    @ApiModelProperty(name = "zipcode", value = "邮编", required = false)
    private String zipcode;
    /**
     * 是否支持货到付款
     */
    @ApiModelProperty(name = "cod", value = "是否支持货到付款,1支持，0不支持", required = false)
    private Integer cod;

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

    public String getRegionPath() {
        return regionPath;
    }

    public void setRegionPath(String regionPath) {
        this.regionPath = regionPath;
    }

    public Integer getRegionGrade() {
        return regionGrade;
    }

    public void setRegionGrade(Integer regionGrade) {
        this.regionGrade = regionGrade;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public Integer getCod() {
        return cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Regions that = (Regions) o;
        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (parentId != null ? !parentId.equals(that.parentId) : that.parentId != null) {
            return false;
        }
        if (regionPath != null ? !regionPath.equals(that.regionPath) : that.regionPath != null) {
            return false;
        }
        if (regionGrade != null ? !regionGrade.equals(that.regionGrade) : that.regionGrade != null) {
            return false;
        }
        if (localName != null ? !localName.equals(that.localName) : that.localName != null) {
            return false;
        }
        if (zipcode != null ? !zipcode.equals(that.zipcode) : that.zipcode != null) {
            return false;
        }
        return cod != null ? cod.equals(that.cod) : that.cod == null;
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (parentId != null ? parentId.hashCode() : 0);
        result = 31 * result + (regionPath != null ? regionPath.hashCode() : 0);
        result = 31 * result + (regionGrade != null ? regionGrade.hashCode() : 0);
        result = 31 * result + (localName != null ? localName.hashCode() : 0);
        result = 31 * result + (zipcode != null ? zipcode.hashCode() : 0);
        result = 31 * result + (cod != null ? cod.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Regions{" +
                "id=" + id +
                ", parentId=" + parentId +
                ", regionPath='" + regionPath + '\'' +
                ", regionGrade=" + regionGrade +
                ", localName='" + localName + '\'' +
                ", zipcode='" + zipcode + '\'' +
                ", cod=" + cod +
                '}';
    }

    /**
     * region转vo
     *
     * @return
     */
    public RegionVO toVo() {
        RegionVO vo = new RegionVO();
        vo.setLocalName(this.getLocalName());
        vo.setParentId(this.getParentId());
        vo.setId(this.getId());
        vo.setChildren(new ArrayList<RegionVO>());
        vo.setLevel(this.getRegionGrade());
        return vo;
    }


}
