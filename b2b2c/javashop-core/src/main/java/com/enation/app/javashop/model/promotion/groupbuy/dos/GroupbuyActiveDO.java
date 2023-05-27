package com.enation.app.javashop.model.promotion.groupbuy.dos;

import java.io.Serializable;
import java.util.Objects;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enation.app.javashop.framework.database.annotation.PrimaryKeyField;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
 * 团购活动表实体
 * @author Snow
 * @version v7.0.0
 * @since v7.0.0
 * 2018-03-21 11:52:14
 */
@TableName(value = "es_groupbuy_active")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class GroupbuyActiveDO implements Serializable {

    private static final long serialVersionUID = 8396241558782003L;

    /**活动Id*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(hidden=true)
    private Long actId;

    /**活动名称*/
    @ApiModelProperty(name="act_name",value="活动名称",required=false)
    private String actName;

    /**活动开启时间*/
    @ApiModelProperty(name="start_time",value="活动开启时间",required=false)
    private Long startTime;

    /**团购结束时间*/
    @ApiModelProperty(name="end_time",value="团购结束时间",required=false)
    private Long endTime;

    /**团购报名截止时间*/
    @ApiModelProperty(name="join_end_time",value="团购报名截止时间",required=false)
    private Long joinEndTime;

    /**团购添加时间*/
    @ApiModelProperty(name="add_time",value="团购添加时间",required=false)
    private Long addTime;

    /**团购活动标签Id*/
    @ApiModelProperty(name="act_tag_id",value="团购活动标签Id",required=false)
    private Integer actTagId;

    /**参与团购商品数量*/
    @ApiModelProperty(name="goods_num",value="参与团购商品数量",required=false)
    private Integer goodsNum;

    /**是否删除 DELETED：已删除，NORMAL：正常*/
    @ApiModelProperty(name="delete_status",value="是否删除 DELETED：已删除，NORMAL：正常",required=false)
    private String deleteStatus;

    /**删除原因*/
    @ApiModelProperty(name="delete_reason",value="删除原因",required=false)
    private String deleteReason;

    /**删除日期*/
    @ApiModelProperty(name="delete_time",value="删除日期",required=false)
    private Long deleteTime;

    /**删除操作人*/
    @ApiModelProperty(name="delete_name",value="删除操作人",required=false)
    private String deleteName;

    @PrimaryKeyField
    public Long getActId() {
        return actId;
    }
    public void setActId(Long actId) {
        this.actId = actId;
    }

    public String getActName() {
        return actName;
    }
    public void setActName(String actName) {
        this.actName = actName;
    }

    public Long getStartTime() {
        return startTime;
    }
    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }
    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Long getJoinEndTime() {
        return joinEndTime;
    }
    public void setJoinEndTime(Long joinEndTime) {
        this.joinEndTime = joinEndTime;
    }

    public Long getAddTime() {
        return addTime;
    }
    public void setAddTime(Long addTime) {
        this.addTime = addTime;
    }

    public Integer getActTagId() {
        return actTagId;
    }
    public void setActTagId(Integer actTagId) {
        this.actTagId = actTagId;
    }

    public Integer getGoodsNum() {
        return goodsNum;
    }
    public void setGoodsNum(Integer goodsNum) {
        this.goodsNum = goodsNum;
    }

    public String getDeleteStatus() {
        return deleteStatus;
    }

    public void setDeleteStatus(String deleteStatus) {
        this.deleteStatus = deleteStatus;
    }

    public String getDeleteReason() {
        return deleteReason;
    }

    public void setDeleteReason(String deleteReason) {
        this.deleteReason = deleteReason;
    }

    public Long getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(Long deleteTime) {
        this.deleteTime = deleteTime;
    }

    public String getDeleteName() {
        return deleteName;
    }

    public void setDeleteName(String deleteName) {
        this.deleteName = deleteName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GroupbuyActiveDO that = (GroupbuyActiveDO) o;
        return Objects.equals(actId, that.actId) &&
                Objects.equals(actName, that.actName) &&
                Objects.equals(startTime, that.startTime) &&
                Objects.equals(endTime, that.endTime) &&
                Objects.equals(joinEndTime, that.joinEndTime) &&
                Objects.equals(addTime, that.addTime) &&
                Objects.equals(actTagId, that.actTagId) &&
                Objects.equals(goodsNum, that.goodsNum) &&
                Objects.equals(deleteStatus, that.deleteStatus) &&
                Objects.equals(deleteReason, that.deleteReason) &&
                Objects.equals(deleteTime, that.deleteTime) &&
                Objects.equals(deleteName, that.deleteName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(actId, actName, startTime, endTime, joinEndTime, addTime, actTagId, goodsNum, deleteStatus, deleteReason, deleteTime, deleteName);
    }
}
