package com.enation.app.javashop.model.promotion.tool.vo;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * @description: 赠品VO
 * @author: liuyulei
 * @create: 2020-02-22 19:32
 * @version:1.0
 * @since:7.1.5
 **/
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class GiveGiftVO implements Serializable {

    @ApiModelProperty(value = "赠品类型")
    private String type;
    @ApiModelProperty(value = "赠品值")
    private Object value;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GiveGiftVO that = (GiveGiftVO) o;

        return new EqualsBuilder()
                .append(type, that.type)
                .append(value, that.value)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(type)
                .append(value)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "GiveGiftVO{" +
                "type='" + type + '\'' +
                ", value=" + value +
                '}';
    }
}
