package com.enation.app.javashop.model.aftersale.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Objects;

/**
 * 售后物流信息实体
 * 主要用于退货、换货审核通过后，用户邮寄货品时使用
 * @author duanmingyu
 * @version v1.0
 * @since v7.1.5
 * 2019-10-15
 */
@TableName(value = "es_as_express")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AfterSaleExpressDO implements Serializable {

    private static final long serialVersionUID = -3412149499413072796L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(hidden=true)
    private Long id;
    /**
     * 售后服务单号
     */
    @ApiModelProperty(name = "service_sn", value = "售后服务单号", required = false)
    private String serviceSn;
    /**
     * 物流单号
     */
    @ApiModelProperty(name = "courier_number", value = "物流单号", required = false)
    private String courierNumber;
    /**
     * 物流公司id
     */
    @ApiModelProperty(name = "courier_company_id", value = "物流公司id", required = false)
    private Long courierCompanyId;
    /**
     * 物流公司名称
     */
    @ApiModelProperty(name = "courier_company", value = "物流公司名称", required = false)
    private String courierCompany;
    /**
     * 发货时间
     */
    @ApiModelProperty(name = "ship_time", value = "发货时间", required = false)
    private Long shipTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceSn() {
        return serviceSn;
    }

    public void setServiceSn(String serviceSn) {
        this.serviceSn = serviceSn;
    }

    public String getCourierNumber() {
        return courierNumber;
    }

    public void setCourierNumber(String courierNumber) {
        this.courierNumber = courierNumber;
    }

    public Long getCourierCompanyId() {
        return courierCompanyId;
    }

    public void setCourierCompanyId(Long courierCompanyId) {
        this.courierCompanyId = courierCompanyId;
    }

    public String getCourierCompany() {
        return courierCompany;
    }

    public void setCourierCompany(String courierCompany) {
        this.courierCompany = courierCompany;
    }

    public Long getShipTime() {
        return shipTime;
    }

    public void setShipTime(Long shipTime) {
        this.shipTime = shipTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AfterSaleExpressDO that = (AfterSaleExpressDO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(serviceSn, that.serviceSn) &&
                Objects.equals(courierNumber, that.courierNumber) &&
                Objects.equals(courierCompanyId, that.courierCompanyId) &&
                Objects.equals(courierCompany, that.courierCompany) &&
                Objects.equals(shipTime, that.shipTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, serviceSn, courierNumber, courierCompanyId, courierCompany, shipTime);
    }

    @Override
    public String toString() {
        return "AfterSaleExpressDO{" +
                "id=" + id +
                ", serviceSn='" + serviceSn + '\'' +
                ", courierNumber='" + courierNumber + '\'' +
                ", courierCompanyId=" + courierCompanyId +
                ", courierCompany='" + courierCompany + '\'' +
                ", shipTime=" + shipTime +
                '}';
    }
}
