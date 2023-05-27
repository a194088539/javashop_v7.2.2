package com.enation.app.javashop.consumer.shop.distribution;

import com.enation.app.javashop.consumer.core.event.MemberRegisterEvent;
import com.enation.app.javashop.model.base.CachePrefix;
import com.enation.app.javashop.model.base.message.MemberRegisterMsg;
import com.enation.app.javashop.client.distribution.CommissionTplClient;
import com.enation.app.javashop.client.distribution.DistributionClient;
import com.enation.app.javashop.model.distribution.dos.CommissionTpl;
import com.enation.app.javashop.model.distribution.dos.DistributionDO;
import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 注册后添加分销商
 *
 * @author Chopper
 * @version v1.0
 * @Description:
 * @since v7.0
 * 2018/6/13 下午11:33
 */
@Component
public class DistributionRegisterConsumer implements MemberRegisterEvent{
    @Autowired
    private DistributionClient distributionClient;

    @Autowired
    private CommissionTplClient commissionTplClient;

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private Cache cache;


    @Override
    public void memberRegister(MemberRegisterMsg memberRegisterMsg) {
        // 注册完毕后 在分销商表中添加会员信息
        DistributionDO distributor = new DistributionDO();
        try {
            distributor.setMemberId(memberRegisterMsg.getMember().getMemberId());
            //默认模版设置
            CommissionTpl commissionTpl= commissionTplClient.getDefaultCommission();
            distributor.setCurrentTplId(commissionTpl.getId());
            distributor.setCurrentTplName(commissionTpl.getTplName());
            distributor.setMemberName(memberRegisterMsg.getMember().getUname());
            distributor = this.distributionClient.add(distributor);
            distributor.setPath("0|" + distributor.getMemberId() + "|");
        } catch (RuntimeException e) {
            logger.error("会员注册异常",e);
        }

        //注册完毕后，给注册会员添加他的上级分销商id
        Object upMemberId = cache.get(CachePrefix.DISTRIBUTION_UP.getPrefix() + memberRegisterMsg.getUuid());

        try {
            //如果推广的会员id存在
            if (upMemberId != null) {
                long lv1MemberId = Long.parseLong(upMemberId.toString());
                logger.debug("上级会员："+lv1MemberId);
                DistributionDO parentDistributor = this.distributionClient.getDistributorByMemberId(lv1MemberId);
                distributor.setPath(parentDistributor.getPath() + distributor.getMemberId() + "|");

                //先更新 path
                this.distributionClient.edit(distributor);
                // 再更新parentId
                this.distributionClient.setParentDistributorId(memberRegisterMsg.getMember().getMemberId(), lv1MemberId);

            } else {
                logger.debug("没有上级会员");
                this.distributionClient.edit(distributor);
            }
            cache.remove(CachePrefix.DISTRIBUTION_UP.getPrefix() + memberRegisterMsg.getUuid());
        } catch (Exception e) {
            logger.error("会员注册异常",e);
        }
    }


}
