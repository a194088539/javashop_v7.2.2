package com.enation.app.javashop.service.promotion.tool.impl;

import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.mapper.promotion.groupbuy.GroupbuyActiveMapper;
import com.enation.app.javashop.mapper.promotion.tool.AbstractPromotionRuleMapper;
import com.enation.app.javashop.model.errorcode.PromotionErrorCode;
import com.enation.app.javashop.model.promotion.tool.enums.PromotionTypeEnum;
import com.enation.app.javashop.model.promotion.tool.dto.PromotionGoodsDTO;
import com.enation.app.javashop.model.system.enums.DeleteStatusEnum;
import com.enation.app.javashop.framework.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 活动规则检测
 *
 * @author Snow create in 2018/4/25
 * @version v2.0
 * @since v7.0.0
 */
@Service
public abstract class AbstractPromotionRuleManagerImpl {

    @Autowired
    private GroupbuyActiveMapper groupbuyActiveMapper;

    @Autowired
    private AbstractPromotionRuleMapper abstractPromotionRuleMapper;

    /**
     * 检测活动与活动之间的规则冲突
     *
     * @param goodsDTOList 活动商品
     */
    protected void verifyRule(List<PromotionGoodsDTO> goodsDTOList) {

        if (goodsDTOList == null || goodsDTOList.isEmpty()) {
            throw new ServiceException(PromotionErrorCode.E401.code(), "没有可用的商品");
        }
    }

    /**
     * 验证活动名称重名
     * @param name  名称
     * @param isUpdate  是否修改
     * @param activeId  修改时需要填充活动id
     */
    protected void verifyName(String name,boolean isUpdate,Long activeId) {

        if(isUpdate){
            //判断活动重名
            Integer count = new QueryChainWrapper<>(groupbuyActiveMapper)
                    //拼接活动名称查询条件
                    .eq("act_name", name)
                    //拼接活动id查询条件
                    .ne("act_id", activeId)
                    //拼接删除状态查询条件
                    .eq("delete_status", DeleteStatusEnum.NORMAL.value())
                    //查询数量
                    .count();

            if(count > 0){
                throw new ServiceException(PromotionErrorCode.E402.code(), "当前活动重名，请修正");
            }
        }else {
            Integer count = new QueryChainWrapper<>(groupbuyActiveMapper)
                    //拼接活动名称查询条件
                    .eq("act_name", name)
                    //拼接删除状态查询条件
                    .eq("delete_status", DeleteStatusEnum.NORMAL.value())
                    //查询数量
                    .count();
            //判断活动重名
            if(count > 0){
                throw new ServiceException(PromotionErrorCode.E402.code(), "当前活动重名，请修正");
            }
        }
    }

    /**
     * 验证活动时间
     * 同一时间只能有一个活动生效
     *
     * @param startTime
     * @param endTime
     */
    protected void verifyTime(long startTime, long endTime, PromotionTypeEnum typeEnum, Long activityId) {

        //（新添活动起始时间大于之前活动的起始时间小于之前活动的截止时间）or （新添活动结束时间大于之前活动的起始时间小于之前活动的截止时间）
        int num = 0;
        switch (typeEnum) {
            case HALF_PRICE:
                num = abstractPromotionRuleMapper.selectHalfPriceCount(startTime, endTime, UserContext.getSeller().getSellerId(), activityId);
                break;

            case MINUS:
                num = abstractPromotionRuleMapper.selectMinusCount(startTime, endTime, UserContext.getSeller().getSellerId(), activityId);
                break;

            case FULL_DISCOUNT:
                num = abstractPromotionRuleMapper.selectFullDiscountCount(startTime, endTime, UserContext.getSeller().getSellerId(), activityId);
                break;

            case GROUPBUY:
                num = abstractPromotionRuleMapper.selectGroupbuyActiveCount(startTime, endTime, DeleteStatusEnum.NORMAL.value(), activityId);
                break;

            case SECKILL:
                num = abstractPromotionRuleMapper.selectSeckillCount(startTime, activityId);
                break;

            case NO:
                break;

            default:
                break;
        }

        if(num > 0){
            throw new ServiceException(PromotionErrorCode.E402.code(), "当前时间内已存在此类活动");
        }

    }

}
