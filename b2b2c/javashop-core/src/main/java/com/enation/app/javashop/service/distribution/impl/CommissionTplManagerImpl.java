package com.enation.app.javashop.service.distribution.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.distribution.CommissionTplMapper;
import com.enation.app.javashop.mapper.distribution.DistributionMapper;
import com.enation.app.javashop.model.distribution.dos.DistributionDO;
import com.enation.app.javashop.model.errorcode.DistributionErrorCode;
import com.enation.app.javashop.service.distribution.exception.DistributionException;
import com.enation.app.javashop.model.distribution.dos.CommissionTpl;
import com.enation.app.javashop.service.distribution.CommissionTplManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 模版管理实现类
 *
 * @author Chopper
 * @version v1.0
 * @Description:
 * @since v7.0
 * 2018/5/22 上午11:00
 */

@Service("commissionTplManager")
public class CommissionTplManagerImpl implements CommissionTplManager {

    @Autowired
    private CommissionTplMapper commissionTplMapper;
    @Autowired
    private DistributionMapper distributionMapper;

    @Override
    public CommissionTpl getModel(long tplId) {
        QueryWrapper<CommissionTpl> wrapper = new QueryWrapper<>();
        wrapper.eq("id",tplId);
        CommissionTpl commissionTpl = commissionTplMapper.selectOne(wrapper);
        return commissionTpl;
    }

    @Override
    public CommissionTpl getCommissionTplByMember(int memberid) {
        // 如果是个正确的模板id
        QueryWrapper<DistributionDO> wrapper = new QueryWrapper<>();
        wrapper.eq("member_id",memberid);
        wrapper.select("current_id");
        List currentId = distributionMapper.selectList(wrapper);

        QueryWrapper<CommissionTpl> wrapperr = new QueryWrapper<>();
        wrapperr.in("id",currentId.toArray());
        CommissionTpl commissionTpl = commissionTplMapper.selectOne(wrapperr);
        return commissionTpl;
    }

    @Override
    public WebPage page(long page, long pageSize) {
        QueryWrapper<CommissionTpl> wrapper = new QueryWrapper<>();
        IPage<CommissionTpl> iPage = commissionTplMapper.selectPage(new Page<>(page,pageSize), wrapper);
        return PageConvert.convert(iPage);
    }

    @Override
    public CommissionTpl add(CommissionTpl commissionTpl) {
        commissionTplMapper.insert(commissionTpl);
        Long id = commissionTpl.getId();
        if (commissionTpl.getIsDefault() == 1) {
            CommissionTpl commissionTpll = new CommissionTpl();
            UpdateWrapper<CommissionTpl> wrapper = new UpdateWrapper<>();
            wrapper.ne("id",id);
            commissionTpll.setIsDefault(0);
            commissionTplMapper.update(commissionTpll,wrapper);
        }
        return commissionTpl;
    }


    @Override
    public CommissionTpl edit(CommissionTpl commissionTpl) {
        commissionTplMapper.updateById(commissionTpl);
        if (commissionTpl.getIsDefault() == 1) {
            CommissionTpl commissionTpll = new CommissionTpl();
            UpdateWrapper<CommissionTpl> wrapper = new UpdateWrapper<>();
            wrapper.ne("id",commissionTpl.getId());
            commissionTpll.setIsDefault(0);
            commissionTplMapper.update(commissionTpll,wrapper);
        }
        return commissionTpl;
    }


    @Override
    public void delete(Long tplId) {

        CommissionTpl commissionTpl = this.getModel(tplId);
        if (commissionTpl.getIsDefault() == 1) {
            throw new DistributionException(DistributionErrorCode.E1010.code(), DistributionErrorCode.E1010.des());
        }

        QueryWrapper<DistributionDO> wrapper = new QueryWrapper<>();
        wrapper.eq("current_tpl_id",tplId);
        if( distributionMapper.selectCount(wrapper)>0 ){
            throw new DistributionException(DistributionErrorCode.E1012.code(), DistributionErrorCode.E1012.des());
        }
        commissionTplMapper.deleteById(tplId);
    }


    @Override
    public CommissionTpl getDefaultCommission() {
        QueryWrapper<CommissionTpl> wrapper = new QueryWrapper<>();
        wrapper.eq("is_default", 1);
        return commissionTplMapper.selectOne(wrapper);
    }

}
