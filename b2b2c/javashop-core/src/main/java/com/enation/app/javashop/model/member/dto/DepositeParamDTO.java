package com.enation.app.javashop.model.member.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @description: 会员预存款搜索参数DTO
 * @author: liuyulei
 * @create: 2019-12-31 09:17
 * @version:1.0
 * @since:7.1.5
 **/
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DepositeParamDTO implements Serializable {
    private static final long serialVersionUID = -477285823153620967L;

    @ApiModelProperty(name = "page_no",value = "页码" ,required = true)
    private Long pageNo;

    @ApiModelProperty(name = "page_size",value = "页面显示大小",required = true)
    private Long pageSize;

    @ApiModelProperty(name = "member_id",value = "会员id",hidden = true)
    private Long memberId;

    @ApiModelProperty(name = "member_name",value = "会员名称",hidden = true)
    private String memberName;

    @ApiModelProperty(name = "sn",value = "充值编号",hidden = true)
    private String sn;

    @ApiModelProperty(name = "start_time",value = "开始时间",hidden = true)
    private Long startTime;

    @ApiModelProperty(name = "end_time",value = "结束时间",hidden = true)
    private Long endTime;

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

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }


    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
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


    @Override
    public String toString() {
        return "DepositeParamDTO{" +
                "pageNo=" + pageNo +
                ", pageSize=" + pageSize +
                ", memberId=" + memberId +
                ", memberName='" + memberName + '\'' +
                ", sn='" + sn + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
