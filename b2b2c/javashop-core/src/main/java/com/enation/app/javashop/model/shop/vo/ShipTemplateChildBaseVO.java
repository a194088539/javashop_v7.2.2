package com.enation.app.javashop.model.shop.vo;

import com.enation.app.javashop.framework.database.annotation.Column;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;

import java.io.Serializable;

/**
 * @author fk
 * @version v2.0
 * @Description: ${todo}
 * @date 2018/10/2616:24
 * @since v7.0.0
 */
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ShipTemplateChildBaseVO implements Serializable {

    @ApiParam("首重／首件")
    private Double firstCompany;

    @ApiParam("运费")
    private Double firstPrice;

    @ApiParam("续重／需件")
    private Double continuedCompany;

    @ApiParam("续费")
   private Double continuedPrice;

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

}
