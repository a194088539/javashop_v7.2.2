package com.enation.app.javashop.model.promotion.coupon.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * 查询优惠券参数
 *
 * @author fk
 * @version v2.0
 * @since v7.1.5
 * 2019-09-10 23:19:39
 */
public class CouponParams {

    /**
     * 分页
     */
    @ApiModelProperty(name = "page_no", value = "分页")
    private Long pageNo;

    /**
     * 每页显示数量
     */
    @ApiModelProperty(name = "page_size", value = "每页显示数量")
    private Long pageSize;

    /**
     * 开始时间
     */
    @ApiModelProperty(name = "start_time", value = "开始时间")
    private Long startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(name = "end_time", value = "结束时间")
    private Long endTime;

    /**
     * 关键字
     */
    @ApiModelProperty(name = "keyword", value = "关键字")
    private String keyword;

    /**
     * 卖家
     */
    @ApiModelProperty(name = "seller_id", value = "卖家")
    private Long sellerId;


    public Long getPageNo() {
        return pageNo;
    }

    public void setPageNo(Long pageNo) {
        this.pageNo = pageNo;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
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

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }
}
