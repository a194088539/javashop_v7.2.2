package com.enation.app.javashop.model.aftersale.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enation.app.javashop.model.trade.order.dos.OrderDO;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Objects;

/**
 * 售后收货地址信息实体
 * 主要用于换货和补发，退货退款对应的是订单收货地址信息
 * @author duanmingyu
 * @version v1.0
 * @since v7.1.5
 * 2019-10-15
 */
@TableName(value = "es_as_change")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AfterSaleChangeDO implements Serializable {

    private static final long serialVersionUID = -8031221768812955983L;

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
     * 收货人姓名
     */
    @ApiModelProperty(name = "ship_name", value = "收货人姓名", required = false)
    private String shipName;
    /**
     * 收货地址省份ID
     */
    @ApiModelProperty(name = "province_id", value = "收货地址省份ID", required = false, hidden = true)
    private Long provinceId;
    /**
     * 收货地址城市ID
     */
    @ApiModelProperty(name = "city_id", value = "收货地址城市ID", required = false, hidden = true)
    private Long cityId;
    /**
     * 收货地址县(区)ID
     */
    @ApiModelProperty(name = "county_id", value = "收货地址县(区)ID", required = false, hidden = true)
    private Long countyId;
    /**
     * 收货地址乡(镇)ID
     */
    @ApiModelProperty(name = "town_id", value = "收货地址乡(镇)ID", required = false, hidden = true)
    private Long townId;
    /**
     * 收货地址省份名称
     */
    @ApiModelProperty(name = "province", value = "收货地址省份名称", required = false, hidden = true)
    private String province;
    /**
     * 收货地址城市名称
     */
    @ApiModelProperty(name = "city", value = "收货地址城市名称", required = false, hidden = true)
    private String city;
    /**
     * 收货地址县(区)名称
     */
    @ApiModelProperty(name = "county", value = "收货地址县(区)名称", required = false, hidden = true)
    private String county;
    /**
     * 收货地址城镇名称
     */
    @ApiModelProperty(name = "town", value = "收货地址城镇名称", required = false, hidden = true)
    private String town;
    /**
     * 收货地址详细
     */
    @ApiModelProperty(name = "ship_addr", value = "收货地址详细", required = false)
    private String shipAddr;
    /**
     * 收货人手机号
     */
    @ApiModelProperty(name = "ship_mobile", value = "收货人手机号", required = false)
    private String shipMobile;

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

    public String getShipName() {
        return shipName;
    }

    public void setShipName(String shipName) {
        this.shipName = shipName;
    }

    public Long getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Long provinceId) {
        this.provinceId = provinceId;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public Long getCountyId() {
        return countyId;
    }

    public void setCountyId(Long countyId) {
        this.countyId = countyId;
    }

    public Long getTownId() {
        return townId;
    }

    public void setTownId(Long townId) {
        this.townId = townId;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getShipAddr() {
        return shipAddr;
    }

    public void setShipAddr(String shipAddr) {
        this.shipAddr = shipAddr;
    }

    public String getShipMobile() {
        return shipMobile;
    }

    public void setShipMobile(String shipMobile) {
        this.shipMobile = shipMobile;
    }

    public AfterSaleChangeDO () {

    }

    public AfterSaleChangeDO (OrderDO order) {
        this.setProvinceId(order.getShipProvinceId());
        this.setCityId(order.getShipCityId());
        this.setCountyId(order.getShipCountyId());
        this.setTownId(order.getShipTownId());
        this.setProvince(order.getShipProvince());
        this.setCity(order.getShipCity());
        this.setCounty(order.getShipCounty());
        this.setTown(order.getShipTown());
        this.setShipName(order.getShipName());
        this.setShipMobile(order.getShipMobile());
        this.setShipAddr(order.getShipAddr());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AfterSaleChangeDO that = (AfterSaleChangeDO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(serviceSn, that.serviceSn) &&
                Objects.equals(shipName, that.shipName) &&
                Objects.equals(provinceId, that.provinceId) &&
                Objects.equals(cityId, that.cityId) &&
                Objects.equals(countyId, that.countyId) &&
                Objects.equals(townId, that.townId) &&
                Objects.equals(province, that.province) &&
                Objects.equals(city, that.city) &&
                Objects.equals(county, that.county) &&
                Objects.equals(town, that.town) &&
                Objects.equals(shipAddr, that.shipAddr) &&
                Objects.equals(shipMobile, that.shipMobile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, serviceSn, shipName, provinceId, cityId, countyId, townId, province, city, county, town, shipAddr, shipMobile);
    }

    @Override
    public String toString() {
        return "AfterSaleChangeDO{" +
                "id=" + id +
                ", serviceSn='" + serviceSn + '\'' +
                ", shipName='" + shipName + '\'' +
                ", provinceId=" + provinceId +
                ", cityId=" + cityId +
                ", countyId=" + countyId +
                ", townId=" + townId +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", county='" + county + '\'' +
                ", town='" + town + '\'' +
                ", shipAddr='" + shipAddr + '\'' +
                ", shipMobile='" + shipMobile + '\'' +
                '}';
    }
}
