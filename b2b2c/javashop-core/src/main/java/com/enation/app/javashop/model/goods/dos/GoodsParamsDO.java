package com.enation.app.javashop.model.goods.dos;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enation.app.javashop.framework.database.annotation.Column;
import com.enation.app.javashop.framework.database.annotation.Id;
import com.enation.app.javashop.framework.database.annotation.PrimaryKeyField;
import com.enation.app.javashop.framework.database.annotation.Table;

import com.enation.app.javashop.framework.util.StringUtil;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;


/**
 * 商品关联参数值实体
 *
 * @author fk
 * @version v2.0
 * @since v7.0.0
 * 2018-03-21 11:40:23
 */
@TableName("es_goods_params")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class GoodsParamsDO implements Serializable {

    private static final long serialVersionUID = 4134870721776090L;

    /**
     * 主键
     */
    @TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(hidden = true)
    private Long id;
    /**
     * 商品id
     */
    @ApiModelProperty(name = "goods_id", value = "商品id", hidden = true)
    private Long goodsId;
    /**
     * 参数id
     */
    @ApiModelProperty(name = "param_id", value = "参数id", required = true)
    private Long paramId;
    /**
     * 参数名字
     */
    @ApiModelProperty(name = "param_name", value = "参数名字", required = true)
    private String paramName;
    /**
     * 参数值
     */
    @ApiModelProperty(name = "param_value", value = "参数值", required = true)
    @Length(max = 100, message = "参数值字符不能大于120")
    private String paramValue;

    @PrimaryKeyField
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Long getParamId() {
        return paramId;
    }

    public void setParamId(Long paramId) {
        this.paramId = paramId;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamValue() {
        if (StringUtil.isEmpty(paramValue)) {
            return "";
        }
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    @Override
    public String toString() {
        return "GoodsParamsDO [id=" + id + ", goodsId=" + goodsId + ", paramId=" + paramId + ", paramName=" + paramName
                + ", paramValue=" + paramValue + "]";
    }

}
