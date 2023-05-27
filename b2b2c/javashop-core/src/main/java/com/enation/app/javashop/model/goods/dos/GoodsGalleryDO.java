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

import java.io.Serializable;


/**
 * 商品相册实体
 *
 * @author fk
 * @version v2.0
 * @since v7.0.0
 * 2018-03-21 11:39:54
 */
@TableName("es_goods_gallery")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class GoodsGalleryDO implements Serializable {

    private static final long serialVersionUID = 8150217189133447L;

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(name = "img_id", value = "图片的主键，添加时-1", required = true)
    private Long imgId;
    /**
     * 商品主键
     */
    @ApiModelProperty(name = "goods_id", value = "商品主键", hidden = true)
    private Long goodsId;
    /**
     * 缩略图路径
     */
    @ApiModelProperty(name = "thumbnail", value = "缩略图路径", hidden = true)
    private String thumbnail;
    /**
     * 小图路径
     */
    @ApiModelProperty(name = "small", value = "小图路径", hidden = true)
    private String small;
    /**
     * 大图路径
     */
    @ApiModelProperty(name = "big", value = "大图路径", hidden = true)
    private String big;
    /**
     * 原图路径
     */
    @ApiModelProperty(name = "original", value = "原图路径", required = true)
    private String original;
    /**
     * 极小图路径
     */
    @ApiModelProperty(name = "tiny", value = "极小图路径", hidden = true)
    private String tiny;
    /**
     * 是否是默认图片1   0没有默认
     */
    @ApiModelProperty(name = "isdefault", value = "是否是默认图片1   0没有默认", hidden = true)
    private Integer isdefault;
    /**
     * 排序
     */
    @ApiModelProperty(name = "sort", value = "排序", required = true)
    private Integer sort;

    @PrimaryKeyField
    public Long getImgId() {
        return imgId;
    }

    public void setImgId(Long imgId) {
        this.imgId = imgId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getSmall() {
        return small;
    }

    public void setSmall(String small) {
        this.small = small;
    }

    public String getBig() {
        return big;
    }

    public void setBig(String big) {
        this.big = big;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getTiny() {
        return tiny;
    }

    public void setTiny(String tiny) {
        this.tiny = tiny;
    }

    public Integer getIsdefault() {
        return isdefault;
    }

    public void setIsdefault(Integer isdefault) {
        this.isdefault = isdefault;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    @Override
    public String toString() {
        return "GoodsGalleryDO [imgId=" + imgId + ", goodsId=" + goodsId + ", thumbnail=" + thumbnail + ", small="
                + small + ", big=" + big + ", original=" + original + ", tiny=" + tiny + ", isdefault=" + isdefault
                + ", sort=" + sort + "]";
    }


}
