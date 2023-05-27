package com.enation.app.javashop.model.goods.vo;

import com.enation.app.javashop.framework.database.annotation.Column;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 用于select标签使用
 *
 * @author fk
 * @version v1.0
 * 2017年5月10日 下午7:37:32
 */
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SelectVO {

    @ApiModelProperty("选择器id")
    private Long id;

    @ApiModelProperty("选择器文本")
    private String text;

    @ApiModelProperty("是否选中，选中true，未选中false")
    private Boolean selected;

    @ApiModelProperty("选择器文本的ID")
    private String falgid;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFalgid() {
        return falgid;
    }

    public void setFalgid(String falgid) {
        this.falgid = falgid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }


    @Override
    public String toString() {
        return "SelectVO{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", selected=" + selected +
                '}';
    }
}
