package com.enation.app.javashop.service.distribution.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.enation.app.javashop.mapper.distribution.DistributionMapper;
import com.enation.app.javashop.mapper.distribution.DistributionOrderMapper;
import com.enation.app.javashop.model.distribution.dos.DistributionOrderDO;
import com.enation.app.javashop.service.distribution.WithdrawCountManager;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * WithdrawCountManagerImpl
 *
 * @author Chopper
 * @version v1.0
 * @since v7.0
 * 2018-08-15 上午8:43
 */
@Service
public class WithdrawCountManagerImpl implements WithdrawCountManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DistributionOrderMapper distributionOrderMapper;
    @Autowired
    private DistributionMapper distributionMapper;

    /**
     * 整理解冻金额
     */
    @Override
    public void withdrawCount() {
        try {
            Long currentData = DateUtil.getDateline();
            distributionMapper.updateCanRebate(currentData);
//            String sql = "UPDATE es_distribution distribution SET can_rebate = can_rebate + \n" +
//                    "IFNULL(( SELECT ( IFNULL( ( SELECT sum(disorder.grade1_rebate) FROM es_distribution_order disorder WHERE member_id_lv1 = distribution.member_id AND disorder.is_withdraw = 0 and disorder.settle_cycle <? ), 0 )+ IFNULL( ( SELECT sum(disorder.grade2_rebate) FROM es_distribution_order disorder  WHERE member_id_lv2 = distribution.member_id AND disorder.is_withdraw = 0 and disorder.settle_cycle <? ), 0 ) ) ), 0 )\n" +
//                    ",commission_frozen = commission_frozen-IFNULL(( SELECT ( IFNULL( ( SELECT sum(disorder.grade1_rebate) FROM es_distribution_order disorder WHERE member_id_lv1 = distribution.member_id AND disorder.is_withdraw = 0 and disorder.settle_cycle <? ), 0 )+ IFNULL( ( SELECT sum(disorder.grade2_rebate) FROM es_distribution_order disorder  WHERE member_id_lv2 = distribution.member_id AND disorder.is_withdraw = 0 and disorder.settle_cycle <? ), 0 ) ) ), 0 )\n";
//
//            this.daoSupport.execute(sql, currentData, currentData, currentData, currentData);


            DistributionOrderDO distributionOrderDo = new DistributionOrderDO();
            UpdateWrapper<DistributionOrderDO> wrapper = new UpdateWrapper<>();
            wrapper.lt("settle_cycle",currentData);
            distributionOrderDo.setIsWithdraw(1);
            distributionOrderMapper.update(distributionOrderDo,wrapper);
        } catch (Exception e) {
            logger.error("每日将解锁金额自动添加到可提现金额异常：", e);
        }
    }
}
