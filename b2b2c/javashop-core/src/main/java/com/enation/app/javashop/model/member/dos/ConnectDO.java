package com.enation.app.javashop.model.member.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @author zjp
 * @version v7.0
 * @Description 信任登录DO
 * @ClassName ConnectDO
 * @since v7.0 下午2:43 2018/6/20
 */
@TableName(value = "es_connect")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ConnectDO  implements Serializable {

    /**Id*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(hidden=true)
    private Long id;

    /**
     * 会员ID
     */
    @ApiModelProperty(name = "member_id",value = "会员id")
    private Long memberId;
    /**
     * 唯一标示union_id
     */
    @ApiModelProperty(name = "union_id", value = "唯一标示")
    private String unionId;
    /**
     * 信任登录类型
     */
    @ApiModelProperty(name = "union_type", value = "信任登录类型")
    private String unionType;
    /**
     * 解绑时间
     */
    @ApiModelProperty(name = "unbound_time",value = "解绑时间")
    private Long unboundTime;

    public ConnectDO(){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    public String getUnionType() {
        return unionType;
    }

    public void setUnionType(String unionType) {
        this.unionType = unionType;
    }

    public Long getUnboundTime() {
        return unboundTime;
    }

    public void setUnboundTime(Long unboundTime) {
        this.unboundTime = unboundTime;
    }

    @Override
    public String toString() {
        return "ConnectDO{" +
                "id=" + id +
                ", memberId=" + memberId +
                ", unionId='" + unionId + '\'' +
                ", unionType='" + unionType + '\'' +
                ", unboundTime=" + unboundTime +
                '}';
    }
}
