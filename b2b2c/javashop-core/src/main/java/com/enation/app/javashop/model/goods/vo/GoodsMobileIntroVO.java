package com.enation.app.javashop.model.goods.vo;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Objects;

/**
 * 商品移动端详情VO
 * @author duanmingyu
 * @version v1.0
 * @since v7.2.0
 * 2020-01-07
 */
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class GoodsMobileIntroVO implements Serializable {

    private static final long serialVersionUID = 7556885109369605084L;

    @ApiModelProperty(name = "type", value = "类型 text：文字，image：图片", allowableValues = "text,image")
    private String type;

    @ApiModelProperty(name = "content", value = "内容")
    private String content;

    @ApiModelProperty(name = "checked", value = "是否选中")
    private Boolean checked;

    @ApiModelProperty(name = "edit_checked", value = "是否选中编辑")
    private Boolean editChecked;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public Boolean getEditChecked() {
        return editChecked;
    }

    public void setEditChecked(Boolean editChecked) {
        this.editChecked = editChecked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GoodsMobileIntroVO that = (GoodsMobileIntroVO) o;
        return Objects.equals(type, that.type) &&
                Objects.equals(content, that.content) &&
                Objects.equals(checked, that.checked) &&
                Objects.equals(editChecked, that.editChecked);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, content, checked, editChecked);
    }

    @Override
    public String toString() {
        return "GoodsMobileIntroVO{" +
                "type='" + type + '\'' +
                ", content='" + content + '\'' +
                ", checked=" + checked +
                ", editChecked=" + editChecked +
                '}';
    }
}
