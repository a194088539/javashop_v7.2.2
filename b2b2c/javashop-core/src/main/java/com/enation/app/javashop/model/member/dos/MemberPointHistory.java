package com.enation.app.javashop.model.member.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enation.app.javashop.framework.database.annotation.PrimaryKeyField;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * 会员积分表实体
 *
 * @author zh
 * @version v7.0.0
 * @since v7.0.0
 * 2018-04-03 15:44:12
 */
@TableName(value = "es_member_point_history")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MemberPointHistory implements Serializable {

    private static final long serialVersionUID = 5222393178191730L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(hidden = true)
    private Long id;
    /**
     * 会员ID
     */
    @ApiModelProperty(name = "member_id", value = "会员ID", required = false)
    private Long memberId;
    /**
     * 等级积分
     */
    @Min(message = "必须为数字", value = 0)
    @ApiModelProperty(name = "gade_point", value = "等级积分", required = false)
    private Integer gradePoint;
    /**
     * 操作时间
     */
    @ApiModelProperty(name = "time", value = "操作时间", required = false)
    private Long time;
    /**
     * 操作理由
     */
    @ApiModelProperty(name = "reason", value = "操作理由", required = false)
    private String reason;
    /**
     * 等级积分类型
     */
    @Min(message = "最小值为0", value = 0)
    @Max(message = "最大值为1", value = 1)
    @ApiModelProperty(name = "grade_point_type", value = "等级积分类型 1为增加，0为消费", required = false)
    private Integer gradePointType;
    /**
     * 操作者
     */
    @ApiModelProperty(name = "operator", value = "操作者", required = false)
    private String operator;
    /**
     * 消费积分
     */
    @Min(message = "必须为数字", value = 0)
    @ApiModelProperty(name = "consum_point", value = "消费积分", required = false)
    private Long consumPoint;
    /**
     * 消费积分类型
     */
    @Min(message = "最小值为0", value = 0)
    @Max(message = "最大值为1", value = 1)
    @ApiModelProperty(name = "consum_point_type", value = "消费积分类型，1为增加，0为消费", required = false)
    private Integer consumPointType;

    @PrimaryKeyField
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

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Long getConsumPoint() {
        return consumPoint;
    }

    public void setConsumPoint(Long consumPoint) {
        this.consumPoint = consumPoint;
    }

    public Integer getConsumPointType() {
        return consumPointType;
    }

    public void setConsumPointType(Integer consumPointType) {
        this.consumPointType = consumPointType;
    }

    public Integer getGradePoint() {
        return gradePoint;
    }

    public void setGradePoint(Integer gradePoint) {
        this.gradePoint = gradePoint;
    }

    public Integer getGradePointType() {
        return gradePointType;
    }

    public void setGradePointType(Integer gradePointType) {
        this.gradePointType = gradePointType;
    }

    @Override
    public String toString() {
        return "MemberPointHistory{" +
                "id=" + id +
                ", memberId=" + memberId +
                ", gradePoint=" + gradePoint +
                ", time=" + time +
                ", reason='" + reason + '\'' +
                ", gradePointType=" + gradePointType +
                ", operator='" + operator + '\'' +
                ", consumPoint=" + consumPoint +
                ", consumPointType=" + consumPointType +
                '}';
    }
}
