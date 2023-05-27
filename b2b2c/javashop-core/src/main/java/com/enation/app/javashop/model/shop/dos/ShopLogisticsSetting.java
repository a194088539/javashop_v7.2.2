package com.enation.app.javashop.model.shop.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enation.app.javashop.model.system.dto.KDNParams;
import com.enation.app.javashop.framework.util.JsonUtil;
import com.enation.app.javashop.framework.util.StringUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 店铺电子面单设置
 *
 * @author Chopper
 * @version v1.0
 * @since v7.0
 * 2019-05-23 上午11:10
 */
@TableName(value = "es_shop_logistics_setting")
@ApiModel
public class ShopLogisticsSetting implements Serializable {

    private static final long serialVersionUID = -8308524136843282998L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty(name = "shop_id", value = "店铺id", hidden = true)
    private Long shopId;

    @ApiModelProperty(name = "logistics_id", value = "物流id")
    private Long logisticsId;

    @ApiModelProperty(name = "config", value = "配置项", hidden = true)
    private String config;


    public void setParams(KDNParams kdnParams) {
        if (kdnParams != null) {
            this.setConfig(JsonUtil.objectToJson(kdnParams));
        }
    }

    public KDNParams getParams() {
        if (!StringUtil.isEmpty(config)) {
            return JsonUtil.jsonToObject(config, KDNParams.class);
        }
        return null;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public Long getLogisticsId() {
        return logisticsId;
    }

    public void setLogisticsId(Long logisticsId) {
        this.logisticsId = logisticsId;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }
}
