package com.enation.app.javashop.model.base.message;

import com.enation.app.javashop.model.promotion.tool.enums.PromotionTypeEnum;
import com.enation.app.javashop.model.promotion.tool.enums.ScriptOperationTypeEnum;

import java.io.Serializable;

/**
 * 促销活动脚本消息实体
 * @author duanmingyu
 * @version v1.0
 * @since v7.2.0
 * 2020-02-14
 */
public class PromotionScriptMsg implements Serializable {

    private static final long serialVersionUID = 6805131209879111554L;

    /**
     * 促销活动ID
     */
    private Long promotionId;

    /**
     * 操作类型 CREATE：创建脚本，DELETE：删除脚本
     */
    private ScriptOperationTypeEnum operationType;

    /**
     * 促销活动类型
     */
    private PromotionTypeEnum promotionType;

    /**
     * 促销活动结束时间
     */
    private Long endTime;

    /**
     * 促销活动名称
     */
    private String promotionName;

    public Long getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Long promotionId) {
        this.promotionId = promotionId;
    }

    public ScriptOperationTypeEnum getOperationType() {
        return operationType;
    }

    public void setOperationType(ScriptOperationTypeEnum operationType) {
        this.operationType = operationType;
    }

    public PromotionTypeEnum getPromotionType() {
        return promotionType;
    }

    public void setPromotionType(PromotionTypeEnum promotionType) {
        this.promotionType = promotionType;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getPromotionName() {
        return promotionName;
    }

    public void setPromotionName(String promotionName) {
        this.promotionName = promotionName;
    }
}
