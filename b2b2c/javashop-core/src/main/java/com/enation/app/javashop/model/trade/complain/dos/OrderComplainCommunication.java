package com.enation.app.javashop.model.trade.complain.dos;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enation.app.javashop.framework.database.annotation.Column;
import com.enation.app.javashop.framework.database.annotation.Id;
import com.enation.app.javashop.framework.database.annotation.PrimaryKeyField;
import com.enation.app.javashop.framework.database.annotation.Table;

import com.enation.app.javashop.framework.util.DateUtil;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
 * 交易投诉对话表实体
 *
 * @author fk
 * @version v2.0
 * @since v2.0
 * 2019-11-29 10:46:34
 */
@TableName("es_order_complain_communication")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class OrderComplainCommunication implements Serializable {

    private static final long serialVersionUID = 1817299973924924L;

    /**
     * 主键
     */
    @TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(hidden = true)
    private Long communicationId;
    /**
     * 投诉id
     */
    @ApiModelProperty(name = "complain_id", value = "投诉id", required = false)
    private Long complainId;
    /**
     * 对话内容
     */
    @ApiModelProperty(name = "content", value = "对话内容", required = false)
    private String content;
    /**
     * 对话时间
     */
    @ApiModelProperty(name = "create_time", value = "对话时间", required = false)
    private Long createTime;
    /**
     * 所属，买家/卖家
     */
    @ApiModelProperty(name = "owner", value = "所属，买家/卖家", required = false)
    private String owner;
    /**
     * 对话所属名称
     */
    @ApiModelProperty(name = "owner_name", value = "对话所属名称", required = false)
    private String ownerName;
    /**
     * 对话所属id,卖家id/买家id
     */
    @ApiModelProperty(name = "owner_id", value = "对话所属id,卖家id/买家id", required = false)
    private Long ownerId;

    public OrderComplainCommunication(Long complainId, String content, String owner, String ownerName, Long ownerId) {
        this.complainId = complainId;
        this.content = content;
        this.createTime = DateUtil.getDateline();
        this.owner = owner;
        this.ownerName = ownerName;
        this.ownerId = ownerId;
    }

    public OrderComplainCommunication() {
    }

    @PrimaryKeyField
    public Long getCommunicationId() {
        return communicationId;
    }

    public void setCommunicationId(Long communicationId) {
        this.communicationId = communicationId;
    }

    public Long getComplainId() {
        return complainId;
    }

    public void setComplainId(Long complainId) {
        this.complainId = complainId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OrderComplainCommunication that = (OrderComplainCommunication) o;
        if (communicationId != null ? !communicationId.equals(that.communicationId) : that.communicationId != null) {
            return false;
        }
        if (complainId != null ? !complainId.equals(that.complainId) : that.complainId != null) {
            return false;
        }
        if (content != null ? !content.equals(that.content) : that.content != null) {
            return false;
        }
        if (createTime != null ? !createTime.equals(that.createTime) : that.createTime != null) {
            return false;
        }
        if (owner != null ? !owner.equals(that.owner) : that.owner != null) {
            return false;
        }
        if (ownerName != null ? !ownerName.equals(that.ownerName) : that.ownerName != null) {
            return false;
        }
        if (ownerId != null ? !ownerId.equals(that.ownerId) : that.ownerId != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = 31 * result + (communicationId != null ? communicationId.hashCode() : 0);
        result = 31 * result + (complainId != null ? complainId.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = 31 * result + (ownerName != null ? ownerName.hashCode() : 0);
        result = 31 * result + (ownerId != null ? ownerId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "OrderComplainCommunication{" +
                "communicationId=" + communicationId +
                ", complainId=" + complainId +
                ", content='" + content + '\'' +
                ", createTime=" + createTime +
                ", owner='" + owner + '\'' +
                ", ownerName='" + ownerName + '\'' +
                ", ownerId=" + ownerId +
                '}';
    }


}
