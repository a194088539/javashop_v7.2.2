package com.enation.app.javashop.model.shop.vo;

import com.enation.app.javashop.model.shop.dos.ShipTemplateDO;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * @author fk
 * @version v2.0
 * @Description: 运费模板VO
 * @date 2018/8/22 15:16
 * @since v7.0.0
 */
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ShipTemplateSellerVO extends ShipTemplateDO implements Serializable {

    private static final long serialVersionUID = 9133643638891738530L;

    @ApiModelProperty(name = "items", value = "指定配送区域", required = true)
    private List<ShipTemplateChildSellerVO>  items;

    @ApiModelProperty(name = "free_items", value = "指定配送区域", required = true)
    private List<ShipTemplateChildSellerVO>  freeItems;

    public List<ShipTemplateChildSellerVO> getItems() {
        return items;
    }

    public void setItems(List<ShipTemplateChildSellerVO> items) {
        this.items = items;
    }

    public List<ShipTemplateChildSellerVO> getFreeItems() {
        return freeItems;
    }

    public void setFreeItems(List<ShipTemplateChildSellerVO> freeItems) {
        this.freeItems = freeItems;
    }



    @Override
    public String toString() {
        return "ShipTemplateVO{" +
                "items=" + items +
                '}';
    }
}
