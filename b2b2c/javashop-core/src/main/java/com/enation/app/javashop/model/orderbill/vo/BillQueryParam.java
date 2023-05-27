package com.enation.app.javashop.model.orderbill.vo;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author fk
 * @version v2.0
 * @Description: 结算单查询条件
 * @date 2018/4/2810:49
 * @since v7.0.0
 */
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class BillQueryParam {

    @ApiModelProperty(name = "page_no", value = "页码", required = false)
    private Long pageNo;
    @ApiModelProperty(name = "page_size", value = "分页数", required = false)
    private Long pageSize;
    @ApiModelProperty(name = "seller_id", value = "商家ID", required = false)
    private Long sellerId;
    @ApiModelProperty(name = "sn", value = "账单号", required = false)
    private String sn;
    @ApiModelProperty(name = "bill_sn", value = "结算单号", required = false)
    private String billSn;

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

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getBillSn() {
        return billSn;
    }

    public void setBillSn(String billSn) {
        this.billSn = billSn;
    }

    @Override
    public String toString() {
        return "BillQueryParam{" +
                "pageNo=" + pageNo +
                ", pageSize=" + pageSize +
                ", sellerId=" + sellerId +
                ", sn='" + sn + '\'' +
                ", billSn='" + billSn + '\'' +
                '}';
    }
}
