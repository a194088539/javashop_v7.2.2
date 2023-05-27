package com.enation.app.javashop.model.member.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Objects;

/**
 * 会员电子发票附件实体
 *
 * @author duanmingyu
 * @version v7.1.4
 * @since v7.0.0
 * 2019-06-24
 */
@TableName(value = "es_receipt_file")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ReceiptFileDO implements Serializable {

    private static final long serialVersionUID = 5669929332569564985L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(hidden = true)
    private Long id;

    /**
     * 会员开票历史记录ID
     */
    @ApiModelProperty(name = "history_id", value = "会员开票历史记录ID", required = false)
    private Long historyId;

    /**
     * 电子发票附件
     */
    @ApiModelProperty(name = "elec_file", value = "电子发票附件", required = false)
    private String elecFile;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHistoryId() {
        return historyId;
    }

    public void setHistoryId(Long historyId) {
        this.historyId = historyId;
    }

    public String getElecFile() {
        return elecFile;
    }

    public void setElecFile(String elecFile) {
        this.elecFile = elecFile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ReceiptFileDO that = (ReceiptFileDO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(historyId, that.historyId) &&
                Objects.equals(elecFile, that.elecFile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, historyId, elecFile);
    }

    @Override
    public String toString() {
        return "ReceiptFileDO{" +
                "id=" + id +
                ", historyId=" + historyId +
                ", elecFile='" + elecFile + '\'' +
                '}';
    }
}
