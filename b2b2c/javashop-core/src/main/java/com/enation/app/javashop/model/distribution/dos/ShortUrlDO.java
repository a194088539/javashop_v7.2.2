package com.enation.app.javashop.model.distribution.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 短链接
 *
 * @author Chopper
 * @version v1.0
 * @since v7.0
 * 2018-06-04 上午8:59
 */
@TableName("es_short_url")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ShortUrlDO implements Serializable {

    @TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(hidden=true)
    private Long id;

    @ApiModelProperty(value="跳转地址")
    private String url;

    @ApiModelProperty(value="短链接key")
    private String su;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSu() {
        return su;
    }

    public void setSu(String su) {
        this.su = su;
    }

    @Override
    public String toString() {
        return "ShortUrlDO{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", su='" + su + '\'' +
                '}';
    }
}
