package com.enation.app.javashop.service.distribution.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.client.member.MemberClient;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.distribution.UpgradeLogMapper;
import com.enation.app.javashop.model.distribution.dos.CommissionTpl;
import com.enation.app.javashop.model.distribution.dos.UpgradeLogDO;
import com.enation.app.javashop.model.distribution.enums.UpgradeTypeEnum;
import com.enation.app.javashop.service.distribution.CommissionTplManager;
import com.enation.app.javashop.service.distribution.DistributionManager;
import com.enation.app.javashop.service.distribution.UpgradeLogManager;
import com.enation.app.javashop.model.member.dos.Member;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.framework.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 升级日志 实现
 *
 * @author Chopper
 * @version v1.0
 * @Description:
 * @since v7.0
 * 2018/5/22 下午12:58
 */

@Component
public class UpgradeLogManagerImpl implements UpgradeLogManager {

    @Autowired
    private DistributionManager distributionManager;
    @Autowired
    private CommissionTplManager commissionTplManager;
    @Autowired
    private MemberClient memberClient;
    @Autowired
    private UpgradeLogMapper upgradeLogMapper;

    @Override
    public WebPage page(long page, long pageSize, String memberName) {
        QueryWrapper<UpgradeLogDO> wrapper = new QueryWrapper<>();
        wrapper.like(!StringUtil.isEmpty(memberName),"member_name",memberName);
        wrapper.orderByDesc("create_time");
        IPage<UpgradeLogDO> iPage = upgradeLogMapper.selectPage(new Page<>(page,pageSize), wrapper);
        return PageConvert.convert(iPage);
    }

    @Override
    public UpgradeLogDO add(UpgradeLogDO upgradeLog) {
        // 非空
        if (upgradeLog != null) {
            upgradeLogMapper.insert(upgradeLog);
        }
        return upgradeLog;
    }

    @Override
    public void addUpgradeLog(Long memberId, int newTplId, UpgradeTypeEnum upgradeType) {
        UpgradeLogDO upgradelog = new UpgradeLogDO();
        Member member = this.memberClient.getModel(memberId);
        long oldTplId = this.distributionManager.getDistributorByMemberId(memberId).getCurrentTplId();
        CommissionTpl oldTpl = this.commissionTplManager.getModel(oldTplId);
        CommissionTpl newTpl = this.commissionTplManager.getModel(newTplId);

        //set数据
        upgradelog.setMemberId(memberId);
        if(member!=null) {
            upgradelog.setMemberName(member.getUname());
        }else{
            upgradelog.setMemberName("无名");
        }

        // 如果有 就记录
        if (oldTpl != null) {
            upgradelog.setOldTplId(oldTplId);
            upgradelog.setOldTplName(oldTpl.getTplName());
        }

        upgradelog.setNewTplId(newTplId);
        upgradelog.setNewTplName(newTpl.getTplName());
        upgradelog.setType(upgradeType.getName());
        upgradelog.setCreateTime(DateUtil.getDateline());
        this.add(upgradelog);
    }
}
