package com.enation.app.javashop.model.goodssearch;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enation.app.javashop.framework.database.annotation.Column;
import com.enation.app.javashop.framework.database.annotation.Id;
import com.enation.app.javashop.framework.database.annotation.Table;
import com.enation.app.javashop.framework.util.DateUtil;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
* @author liuyulei
 * @version 1.0
 * @Description: 搜索历史
 * @date 2019/5/27 10:24
 * @since v7.0
 */
@TableName("es_keyword_search_history")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SearchKeywordDO implements Serializable {


    /**
     * 主键
     */
    @TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(hidden = true)
    private Long id;

    /**
     * 名称
     */
    @ApiModelProperty(name = "keyword", value = "名称", required = true)
    @NotEmpty(message = "关键字名称必填")
    private String keyword;

    @ApiModelProperty(name = "count", value = "搜索次数", required = true)
    @NotEmpty(message = "关键字名称必填")
    private Integer count ;

    @ApiModelProperty(name = "add_time", value = "添加时间", required = true)
    @NotEmpty(message = "关键字名称必填")
    private Long addTime;

    @ApiModelProperty(name = "modify_time", value = "更新时间", required = true)
    @NotEmpty(message = "更新时间")
    private Long modifyTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Long getAddTime() {
        return addTime;
    }

    public void setAddTime(Long addTime) {
        this.addTime = addTime;
    }

    public Long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public SearchKeywordDO() {
    }

    public SearchKeywordDO(String keyword) {
        this.keyword = keyword;
        this.addTime = DateUtil.getDateline();
        this.modifyTime = DateUtil.getDateline();
        this.count = 1;
    }

    @Override
    public String toString() {
        return "SearchKeywordDO{" +
                "id=" + id +
                ", keyword='" + keyword + '\'' +
                ", count=" + count +
                ", addTime=" + addTime +
                ", modifyTime=" + modifyTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SearchKeywordDO that = (SearchKeywordDO) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(keyword, that.keyword)
                .append(count, that.count)
                .append(addTime, that.addTime)
                .append(modifyTime, that.modifyTime)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(keyword)
                .append(count)
                .append(addTime)
                .append(modifyTime)
                .toHashCode();
    }
}
