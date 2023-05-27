package com.enation.app.javashop.model.goods.dos;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enation.app.javashop.framework.database.annotation.Column;
import com.enation.app.javashop.framework.database.annotation.Id;
import com.enation.app.javashop.framework.database.annotation.PrimaryKeyField;
import com.enation.app.javashop.framework.database.annotation.Table;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
 * 草稿商品参数表实体
 *
 * @author fk
 * @version v2.0
 * @since v7.0.0
 * 2018-03-26 11:31:20
 */
@TableName("es_draft_goods_params")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DraftGoodsParamsDO implements Serializable {

    private static final long serialVersionUID = 1137617128769441L;

    /**
     * ID
     */
    @TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(hidden = true)
    private Long id;
    /**
     * 草稿ID
     */
    @ApiModelProperty(name = "draft_goods_id", value = "草稿ID", required = false)
    private Long draftGoodsId;
    /**
     * 参数ID
     */
    @ApiModelProperty(name = "param_id", value = "参数ID", required = false)
    private Long paramId;
    /**
     * 参数名
     */
    @ApiModelProperty(name = "param_name", value = "参数名", required = false)
    private String paramName;
    /**
     * 参数值
     */
    @ApiModelProperty(name = "param_value", value = "参数值", required = false)
    private String paramValue;

    public DraftGoodsParamsDO() {
    }

    public DraftGoodsParamsDO(GoodsParamsDO param) {
        this.paramId = param.getParamId();
        this.paramName = param.getParamName();
        this.paramValue = param.getParamValue();
    }

    @PrimaryKeyField
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDraftGoodsId() {
        return draftGoodsId;
    }

    public void setDraftGoodsId(Long draftGoodsId) {
        this.draftGoodsId = draftGoodsId;
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
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    @Override
    public String toString() {
        return "DraftGoodsParamsDO [id=" + id + ", draftGoodsId=" + draftGoodsId + ", paramId=" + paramId
                + ", paramName=" + paramName + ", paramValue=" + paramValue + "]";
    }


}
