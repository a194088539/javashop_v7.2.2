package com.enation.app.javashop.model.system.dos;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enation.app.javashop.model.system.vo.WayBillVO;
import com.enation.app.javashop.framework.database.annotation.Column;
import com.enation.app.javashop.framework.database.annotation.Id;
import com.enation.app.javashop.framework.database.annotation.PrimaryKeyField;
import com.enation.app.javashop.framework.database.annotation.Table;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import com.google.gson.Gson;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
 * 电子面单实体
 *
 * @author zh
 * @version v7.0.0
 * @since v7.0.0
 * 2018-06-08 16:26:05
 */
@TableName("es_waybill")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class WayBillDO implements Serializable {

    private static final long serialVersionUID = 6990005251050581L;

    /**
     * 电子面单id
     */
    @TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(hidden = true)
    private Long id;
    /**
     * 名称
     */
    @ApiModelProperty(name = "name", value = "名称", required = false)
    private String name;
    /**
     * 是否开启
     */
    @ApiModelProperty(name = "open", value = "是否开启", required = false)
    private Integer open;
    /**
     * 电子面单配置
     */
    @ApiModelProperty(name = "config", value = "电子面单配置", required = false)
    private String config;
    /**
     * beanid
     */
    @ApiModelProperty(name = "bean", value = "beanid", required = false)
    private String bean;

    @PrimaryKeyField
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOpen() {
        return open;
    }

    public void setOpen(Integer open) {
        this.open = open;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getBean() {
        return bean;
    }

    public void setBean(String bean) {
        this.bean = bean;
    }

    public WayBillDO(WayBillVO wayBillVO) {
        this.id = wayBillVO.getId();
        this.name = wayBillVO.getName();
        this.open = wayBillVO.getOpen();
        this.bean = wayBillVO.getBean();
        Gson gson = new Gson();
        this.config = gson.toJson(wayBillVO.getConfigItems());
    }

    public WayBillDO() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WayBillDO that = (WayBillDO) o;
        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        if (open != null ? !open.equals(that.open) : that.open != null) {
            return false;
        }
        if (config != null ? !config.equals(that.config) : that.config != null) {
            return false;
        }
        return bean != null ? bean.equals(that.bean) : that.bean == null;
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (open != null ? open.hashCode() : 0);
        result = 31 * result + (config != null ? config.hashCode() : 0);
        result = 31 * result + (bean != null ? bean.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "WayBillDO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", open=" + open +
                ", config='" + config + '\'' +
                ", bean='" + bean + '\'' +
                '}';
    }


}
