package com.enation.app.javashop.model.goodssearch;

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
 * 商品分词表实体
 *
 * @author fk
 * @version v2.0
 * @since v2.0
 * 2020-08-06 10:18:29
 */
@TableName("es_goods_words")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class GoodsWordsDO implements Serializable {

    private static final long serialVersionUID = 2525253328866549L;

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(hidden = true)
    private Long id;
    /**
     * 分词
     */
    @ApiModelProperty(name = "words", value = "分词", required = false)
    private String words;
    /**
     * 数量
     */
    @ApiModelProperty(name = "goods_num", value = "数量", required = false)
    private Long goodsNum;
    /**
     * 全拼字母
     */
    @ApiModelProperty(name = "quanpin", value = "全拼字母", required = false)
    private String quanpin;
    /**
     * 首字母
     */
    @ApiModelProperty(name = "szm", value = "首字母", required = false)
    private String szm;
    /**
     * 类型
     */
    @ApiModelProperty(name = "type", value = "类型", required = false)
    private String type;
    /**
     * 排序字段
     */
    @ApiModelProperty(name = "sort", value = "排序字段", required = false)
    private Integer sort;


    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words;
    }

    public Long getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(Long goodsNum) {
        this.goodsNum = goodsNum;
    }

    public String getQuanpin() {
        return quanpin;
    }

    public void setQuanpin(String quanpin) {
        this.quanpin = quanpin;
    }

    public String getSzm() {
        return szm;
    }

    public void setSzm(String szm) {
        this.szm = szm;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    @PrimaryKeyField
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GoodsWordsDO that = (GoodsWordsDO) o;
        if (words != null ? !words.equals(that.words) : that.words != null) {
            return false;
        }
        if (goodsNum != null ? !goodsNum.equals(that.goodsNum) : that.goodsNum != null) {
            return false;
        }
        if (quanpin != null ? !quanpin.equals(that.quanpin) : that.quanpin != null) {
            return false;
        }
        if (szm != null ? !szm.equals(that.szm) : that.szm != null) {
            return false;
        }
        if (type != null ? !type.equals(that.type) : that.type != null) {
            return false;
        }
        if (sort != null ? !sort.equals(that.sort) : that.sort != null) {
            return false;
        }
        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = 31 * result + (words != null ? words.hashCode() : 0);
        result = 31 * result + (goodsNum != null ? goodsNum.hashCode() : 0);
        result = 31 * result + (quanpin != null ? quanpin.hashCode() : 0);
        result = 31 * result + (szm != null ? szm.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (sort != null ? sort.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "GoodsWordsDO{" +
                "id=" + id +
                ", words='" + words + '\'' +
                ", goodsNum=" + goodsNum +
                ", quanpin='" + quanpin + '\'' +
                ", szm='" + szm + '\'' +
                ", type='" + type + '\'' +
                ", sort=" + sort +
                '}';
    }
}