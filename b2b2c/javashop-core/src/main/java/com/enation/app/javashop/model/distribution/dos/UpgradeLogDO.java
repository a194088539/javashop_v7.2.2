package com.enation.app.javashop.model.distribution.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enation.app.javashop.framework.database.annotation.Column;
import com.enation.app.javashop.framework.database.annotation.Id;
import com.enation.app.javashop.framework.database.annotation.Table;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Objects;

/**
 *  模版升级日志
 * @author Chopper
 * @version v1.0
 * @since v7.0
 * 2018/5/21 上午10:35
 * @Description:
 *
 */

@TableName("es_upgrade_log")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UpgradeLogDO implements Serializable {

	/**模版升级日志id*/
	@TableId(type= IdType.ASSIGN_ID)
	@ApiModelProperty(hidden=true)
	private Long id;

	/**会员id*/
	@ApiModelProperty(value="会员id")
	private Long memberId;

	/**会员名称*/
	@ApiModelProperty(value="会员名称")
	private String memberName;

	/**切换类型（1=手动，2=自动）*/
	@ApiModelProperty(value="切换类型 ")
	private String type;

	/**旧的模板id*/
	@ApiModelProperty(value="旧的模板id")
	private Long oldTplId;

	/**旧的模板名称*/
	@ApiModelProperty(value="旧的模板名字")
	private String oldTplName;

	/**新的模板id*/
	@ApiModelProperty(value="新的模板id")
	private Integer newTplId;

	/**新的模板名*/
	@ApiModelProperty(value="新的模板名字")
	private String newTplName;

	/**升级时间*/
	@ApiModelProperty(value="创建日期")
	private Long createTime;


	@Override
	public boolean equals(Object o) {
		if (this == o) {
            return true;
        }
		if (o == null || getClass() != o.getClass()) {
            return false;
        }

		UpgradeLogDO that = (UpgradeLogDO) o;

		if (!Objects.equals(id, that.id)) {
            return false;
        }
		if (!Objects.equals(memberId, that.memberId)) {
            return false;
        }
		if (type != that.type) {
            return false;
        }
		if (!Objects.equals(oldTplId, that.oldTplId)) {
            return false;
        }
		if (!Objects.equals(newTplId, that.newTplId)) {
            return false;
        }
		if (!Objects.equals(createTime, that.createTime)) {
            return false;
        }
		if (memberName != null ? !memberName.equals(that.memberName) : that.memberName != null) {
            return false;
        }
		if (oldTplName != null ? !oldTplName.equals(that.oldTplName) : that.oldTplName != null) {
            return false;
        }
		return newTplName != null ? newTplName.equals(that.newTplName) : that.newTplName == null;
	}


	@Override
	public String toString() {
		return "UpgradeLog{" +
				"id=" + id +
				", memberId=" + memberId +
				", memberName='" + memberName + '\'' +
				", type=" + type +
				", oldTplId=" + oldTplId +
				", oldTplName='" + oldTplName + '\'' +
				", newTplId=" + newTplId +
				", newTplName='" + newTplName + '\'' +
				", createTime=" + createTime +
				'}';
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

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getOldTplId() {
		return oldTplId;
	}

	public void setOldTplId(Long oldTplId) {
		this.oldTplId = oldTplId;
	}

	public String getOldTplName() {
		return oldTplName;
	}

	public void setOldTplName(String oldTplName) {
		this.oldTplName = oldTplName;
	}

	public int getNewTplId() {
		return newTplId;
	}

	public void setNewTplId(int newTplId) {
		this.newTplId = newTplId;
	}

	public String getNewTplName() {
		return newTplName;
	}

	public void setNewTplName(String newTplName) {
		this.newTplName = newTplName;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
}
