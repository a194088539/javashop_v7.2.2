package com.enation.app.javashop.model.member.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enation.app.javashop.model.base.context.Region;
import com.enation.app.javashop.model.base.context.RegionFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Objects;

/**
 * 会员收票地址实体
 *
 * @author duanmingyu
 * @version v7.1.4
 * @since v7.0.0
 * 2019-06-19
 */
@TableName(value = "es_receipt_address")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ReceiptAddressDO implements Serializable {

    private static final long serialVersionUID = -6723850607492854901L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(hidden = true)
    private Long id;

    /**
     * 会员ID
     */
    @ApiModelProperty(name = "member_id", value = "会员ID", required = false, hidden = true)
    private Long memberId;

    /**
     * 收票人姓名
     */
    @ApiModelProperty(name = "member_name", value = "收票人姓名", required = false)
    private String memberName;

    /**
     * 收票人手机号
     */
    @ApiModelProperty(name = "member_mobile", value = "收票人手机号", required = false)
    private String memberMobile;

    /**
     * 所属省份ID
     */
    @ApiModelProperty(name = "province_id", value = "所属省份ID", required = false, hidden = true)
    private Long provinceId;

    /**
     * 所属城市ID
     */
    @ApiModelProperty(name = "city_id", value = "所属城市ID", required = false, hidden = true)
    private Long cityId;

    /**
     * 所属区县ID
     */
    @ApiModelProperty(name = "county_id", value = "所属区县ID", required = false, hidden = true)
    private Long countyId;

    /**
     * 所属乡镇ID
     */
    @ApiModelProperty(name = "town_id", value = "所属乡镇ID", required = false, hidden = true)
    private Long townId;

    /**
     * 所属省份
     */
    @ApiModelProperty(name = "province", value = "所属省份", required = false, hidden = true)
    private String province;

    /**
     * 所属城市
     */
    @ApiModelProperty(name = "city", value = "所属城市", required = false, hidden = true)
    private String city;

    /**
     * 所属区县
     */
    @ApiModelProperty(name = "county", value = "所属区县", required = false, hidden = true)
    private String county;

    /**
     * 所属乡镇
     */
    @ApiModelProperty(name = "town", value = "所属乡镇", required = false, hidden = true)
    private String town;

    /**
     * 详细地址
     */
    @ApiModelProperty(name = "detail_addr", value = "详细地址", required = false)
    private String detailAddr;

    @TableField(exist = false)
    @RegionFormat
    @ApiModelProperty(name = "region", value = "地区")
    private Region region;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberMobile() {
        return memberMobile;
    }

    public void setMemberMobile(String memberMobile) {
        this.memberMobile = memberMobile;
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

    public String getDetailAddr() {
        return detailAddr;
    }

    public void setDetailAddr(String detailAddr) {
        this.detailAddr = detailAddr;
    }

    @JsonIgnore
    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ReceiptAddressDO addressDO = (ReceiptAddressDO) o;
        return Objects.equals(id, addressDO.id) &&
                Objects.equals(memberId, addressDO.memberId) &&
                Objects.equals(memberName, addressDO.memberName) &&
                Objects.equals(memberMobile, addressDO.memberMobile) &&
                Objects.equals(provinceId, addressDO.provinceId) &&
                Objects.equals(cityId, addressDO.cityId) &&
                Objects.equals(countyId, addressDO.countyId) &&
                Objects.equals(townId, addressDO.townId) &&
                Objects.equals(province, addressDO.province) &&
                Objects.equals(city, addressDO.city) &&
                Objects.equals(county, addressDO.county) &&
                Objects.equals(town, addressDO.town) &&
                Objects.equals(detailAddr, addressDO.detailAddr) &&
                Objects.equals(region, addressDO.region);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, memberId, memberName, memberMobile, provinceId, cityId, countyId, townId, province, city, county, town, detailAddr, region);
    }

    @Override
    public String toString() {
        return "ReceiptAddressDO{" +
                "id=" + id +
                ", memberId=" + memberId +
                ", memberName='" + memberName + '\'' +
                ", memberMobile='" + memberMobile + '\'' +
                ", provinceId=" + provinceId +
                ", cityId=" + cityId +
                ", countyId=" + countyId +
                ", townId=" + townId +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", county='" + county + '\'' +
                ", town='" + town + '\'' +
                ", detailAddr='" + detailAddr + '\'' +
                ", region=" + region +
                '}';
    }
}
