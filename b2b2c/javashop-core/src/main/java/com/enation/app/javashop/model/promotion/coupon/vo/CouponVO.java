package com.enation.app.javashop.model.promotion.coupon.vo;

import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.model.promotion.coupon.dos.CouponDO;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @description: 优惠券VO
 * @author: liuyulei
 * @create: 2020-02-10 09:59
 * @version:1.0
 * @since:7.1.4
 **/
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CouponVO extends CouponDO implements Serializable {

    @ApiModelProperty(value="优惠券是否失效,false:已失效，true:正常",name = "disabled")
    private Boolean disabled;


    public Boolean getDisabled() {
        Long currentTime = DateUtil.getDateline();
        if(super.getStartTime() <= currentTime && currentTime <=super.getEndTime()){
            return true;
        }
        return false;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public String toString() {
        return "CouponVO{" +
                "disabled=" + disabled +
                "} " + super.toString();
    }
}
