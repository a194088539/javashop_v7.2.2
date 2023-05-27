package com.enation.app.javashop.model.goods.dos;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enation.app.javashop.framework.database.annotation.Column;
import com.enation.app.javashop.framework.database.annotation.Id;
import com.enation.app.javashop.framework.database.annotation.Table;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
 * 标签商品关联实体
 *
 * @author fk
 * @version v2.0
 * @since v7.0.0
 * 2018-03-28 17:02:59
 */
@TableName("es_tag_goods")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TagGoodsDO implements Serializable {

    private static final long serialVersionUID = 9467335201085494L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 标签id
     */
    @ApiModelProperty(name = "tag_id", value = "标签id", required = false)
    private Long tagId;
    /**
     * 商品id
     */
    @ApiModelProperty(name = "goods_id", value = "商品id", required = false)
    private Long goodsId;

    public TagGoodsDO() {
    }


    public TagGoodsDO(Long tagId, Long goodsId) {
        super();
        this.tagId = tagId;
        this.goodsId = goodsId;
    }


    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "TagGoodsDO{" +
                "tagId=" + tagId +
                ", goodsId=" + goodsId +
                '}';
    }
}
