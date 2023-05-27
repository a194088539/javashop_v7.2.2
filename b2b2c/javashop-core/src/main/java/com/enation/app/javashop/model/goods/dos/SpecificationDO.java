package com.enation.app.javashop.model.goods.dos;

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
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;


/**
 * 规格项实体
 *
 * @author fk
 * @version v2.0
 * @since v7.0.0
 * 2018-03-20 09:31:27
 */
@TableName("es_specification")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SpecificationDO implements Serializable {

    private static final long serialVersionUID = 5111769180376075L;

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(hidden = true)
    private Long specId;
    /**
     * 规格项名称
     */
    @NotEmpty(message = "规格项名称不能为空")
    @ApiModelProperty(name = "spec_name", value = "规格项名称", required = true)
    private String specName;
    /**
     * 是否被删除0 删除   1  没有删除
     */
    @ApiModelProperty(hidden = true)
    private Integer disabled;
    /**
     * 规格描述
     */
    @ApiModelProperty(name = "spec_memo", value = "规格描述", required = false)
    @Length(max = 50, message = "规格备注为0到50个字符之间")
    private String specMemo;
    /**
     * 所属卖家 0属于平台
     */
    @ApiModelProperty(hidden = true)
    private Long sellerId;

    public SpecificationDO() {
    }


    public SpecificationDO(String specName, Integer disabled, String specMemo, Long sellerId) {
        super();
        this.specName = specName;
        this.disabled = disabled;
        this.specMemo = specMemo;
        this.sellerId = sellerId;
    }

    @PrimaryKeyField
    public Long getSpecId() {
        return specId;
    }

    public void setSpecId(Long specId) {
        this.specId = specId;
    }

    public String getSpecName() {
        return specName;
    }

    public void setSpecName(String specName) {
        this.specName = specName;
    }

    public Integer getDisabled() {
        return disabled;
    }

    public void setDisabled(Integer disabled) {
        this.disabled = disabled;
    }

    public String getSpecMemo() {
        return specMemo;
    }

    public void setSpecMemo(String specMemo) {
        this.specMemo = specMemo;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    @Override
    public String toString() {
        return "SpecificationDO [specId=" + specId + ", specName=" + specName + ", disabled=" + disabled + ", specMemo="
                + specMemo + ", sellerId=" + sellerId + "]";
    }

}
