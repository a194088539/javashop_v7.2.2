package com.enation.app.javashop.service.distribution.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.client.member.MemberClient;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.distribution.DistributionMapper;
import com.enation.app.javashop.model.errorcode.DistributionErrorCode;
import com.enation.app.javashop.service.distribution.exception.DistributionException;
import com.enation.app.javashop.model.distribution.dos.CommissionTpl;
import com.enation.app.javashop.model.distribution.dos.DistributionDO;
import com.enation.app.javashop.model.distribution.enums.UpgradeTypeEnum;
import com.enation.app.javashop.model.distribution.vo.DistributionVO;
import com.enation.app.javashop.service.distribution.UpgradeLogManager;
import com.enation.app.javashop.service.distribution.CommissionTplManager;
import com.enation.app.javashop.service.distribution.DistributionManager;
import com.enation.app.javashop.model.member.dos.Member;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.framework.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 分销管理实现类
 *
 * @author Chopper
 * @version v1.0
 * @Description:
 * @since v7.0
 * 2018/5/21 下午3:27
 */

@Component
public class DistributionManagerImpl implements DistributionManager {

    @Autowired
    private CommissionTplManager commissionTplManager;
    @Autowired
    private UpgradeLogManager upgradeLogManager;
    @Autowired
    private MemberClient memberClient;
    @Autowired
    private DistributionMapper distributionMapper;

    @Override
    @Transactional(value = "distributionTransactionManager",rollbackFor=Exception.class)
    public DistributionDO add(DistributionDO distributor) {
        // 如果分销商值有效
        if (distributor != null) {
            distributor.setCreateTime(DateUtil.getDateline());
            distributionMapper.insert(distributor);
            distributor.setId(distributor.getId());
        }
        return distributor;
    }

    /**
     * 所有下线
     *
     * @param memberId
     * @return
     */
    @Override
    public List<DistributionVO> allDown(Long memberId) {

        QueryWrapper<DistributionDO> wrapper = new QueryWrapper<>();
        wrapper.eq("member_id_lv1", memberId).eq("member_id_lv2", memberId);
        List<DistributionDO> dos = distributionMapper.selectList(wrapper);

        List<DistributionVO> vos = new ArrayList<>();
        //填充一级
        for (DistributionDO ddo : dos) {
            if (ddo.getMemberIdLv1().equals(memberId)) {
                vos.add(new DistributionVO(ddo));
            }
        }
        //填充二级

        if (!vos.isEmpty()) {
            for (DistributionDO ddo : dos) {
                for (DistributionVO vo : vos) {
                    if (ddo.getMemberIdLv1().equals(vo.getId())) {
                        List<DistributionVO> item = vo.getItem();
                        if (item == null) {
                            item = new ArrayList<>();
                        }
                        item.add(new DistributionVO(ddo));
                        vo.setItem(item);
                    }
                }
            }
        }

        return vos;
    }

    @Override
    public WebPage page(Long pageNo, Long pageSize, String memberName) {

        QueryWrapper<DistributionDO> wrapper = new QueryWrapper<>();
        wrapper.like(!StringUtil.isEmpty(memberName),"member_name",memberName);
        wrapper.orderByDesc("id");
        IPage<DistributionDO> iPage = distributionMapper.selectPage(new Page<>(pageNo,pageSize), wrapper);
        WebPage<DistributionDO> page =  PageConvert.convert(iPage);

        WebPage result = new WebPage();
        result.setPageNo(page.getPageNo());
        result.setPageSize(page.getPageSize());
        result.setDataTotal(page.getDataTotal());

        List<DistributionVO> vos = new ArrayList<>();
        for (DistributionDO ddo : page.getData()) {
            vos.add(new DistributionVO(ddo));
        }
        result.setData(vos);
        return result;
    }

    @Override
    public DistributionDO getDistributorByMemberId(Long memberId) {
        QueryWrapper<DistributionDO> wrapper = new QueryWrapper<>();
        wrapper.eq("member_id", memberId);
        DistributionDO distributor = distributionMapper.selectOne(wrapper);
        return distributor;
    }

    @Override
    public DistributionDO getDistributor(Long id) {
        DistributionDO distributor = distributionMapper.selectById(id);
        return distributor;
    }

    @Override
    @Transactional(value = "distributionTransactionManager",rollbackFor=Exception.class)
    public DistributionDO edit(DistributionDO distributor) {
        distributionMapper.updateById(distributor);
        return distributor;
    }


    @Override
    public boolean setParentDistributorId(Long memberId, Long parentId) {

        // 如果会员id有效
        if (memberId != 0) {
            // 1.得到父级会员信息 就是当前会员的lv1
            DistributionDO lv1Distributor = this.getDistributorByMemberId(parentId);

            // 2.得到 他的lv1级 （当前会员的lv2级 是他的lv1级的lv1级）
            Long lv2MemberId = lv1Distributor.getMemberIdLv1();

            // 3.准备拼接sql
            DistributionDO distributionDo = new DistributionDO();
            UpdateWrapper<DistributionDO> wrapper = new UpdateWrapper<>();
            distributionDo.setMemberIdLv1(lv1Distributor.getMemberId());
            // 如果lv2会员id存在
            if (null != lv2MemberId) {
                distributionDo.setMemberIdLv2(lv2MemberId);
            }
            // 4.添加where 并执行
            wrapper.eq("member_id",memberId);
            distributionMapper.update(distributionDo,wrapper);

            this.countDown(lv1Distributor.getMemberId());
            // 如果lv2会员id存在
            if (null != lv2MemberId) {
                this.countDown(lv2MemberId);
            }
            return true;
        }
        return false;
    }

    @Override
    public void addFrozenCommission(Double price, Long memberId) {
        UpdateWrapper wrapper = new UpdateWrapper();
        DistributionDO distributionDO = new DistributionDO();
        wrapper.eq("member_id",memberId);
        //增加冻结返利金额
        wrapper.setSql("commission_frozen = commission_frozen + " + price);
        distributionMapper.update(distributionDO,wrapper);
    }

    @Override
    public void addTotalPrice(Double orderPrice, Double rebate, Long memberId) {
        UpdateWrapper wrapper = new UpdateWrapper();
        DistributionDO distributionDO = new DistributionDO();
        wrapper.eq("member_id",memberId);
        //增加总销售额、总的返利金额金额
        wrapper.setSql("turnover_price = turnover_price +" + orderPrice);
        wrapper.setSql("rebate_total = rebate_total +" + rebate);
        distributionMapper.update(distributionDO,wrapper);
    }

    @Override
    public void subTotalPrice(Double orderPrice, Double rebate, Long memberId) {
        UpdateWrapper wrapper = new UpdateWrapper();
        DistributionDO distributionDO = new DistributionDO();
        wrapper.eq("member_id",memberId);
        //减去总销售额、总的返利金额金额
        wrapper.setSql("turnover_price = turnover_price -" + orderPrice);
        wrapper.setSql("rebate_total = rebate_total -" + rebate);
        distributionMapper.update(distributionDO,wrapper);
    }

    @Override
    public Double getCanRebate(Long memberId) {
        try {
            QueryWrapper<DistributionDO> wrapper = new QueryWrapper<>();
            wrapper.eq("member_id", memberId);
            return distributionMapper.selectOne(wrapper).getCanRebate();
        } catch (Exception e) {
            // 如果用户没有提现金额 返回0
            return 0d;
        }
    }

    @Override
    public String getUpMember() {

        String path = this.getDistributorByMemberId(
                UserContext.getBuyer().getUid())
                .getPath();
        String[] up = path.split("\\|");
        Long upMember = Long.parseLong(up[up.length - 2]);
        if (upMember == 0) {
            return "没有推荐人";
        }
        try {
            DistributionDO distributor = this.getDistributorByMemberId(upMember);
            Member member = memberClient.getModel(distributor.getMemberId());
            if (member == null) {
                return "没有推荐人";
            }
            return member.getUname();
        } catch (Exception e) {
            return "没有推荐人";
        }
    }

    @Override
    public List<DistributionVO> getLowerDistributorTree(Long memberId) {

        QueryWrapper<DistributionDO> wrapper = new QueryWrapper<>();
        wrapper.eq("member_id_lv2", memberId).or().eq("member_id_lv1",memberId);
        List<DistributionDO> list = distributionMapper.selectList(wrapper);

        List<DistributionVO> vos = new ArrayList<DistributionVO>();
        for (DistributionDO ddo : list) {
            vos.add(new DistributionVO(ddo));
        }
        List<DistributionVO> result = new ArrayList<>();
        //第一层关系构造
        for (DistributionVO vo : vos) {
            if (vo.getLv1Id().equals(memberId)) {
                result.add(vo);
            }
        }
        //第二层关系构造 循环第一层构造
        for (DistributionVO rs : result) {
            List<DistributionVO> items = new ArrayList<>();
            for (DistributionVO vo : vos) {
                if (rs.getId().equals(vo.getLv1Id())) {
                    items.add(vo);
                }
            }
            rs.setItem(items);
        }
        return result;
    }


    @Override
    public void changeTpl(Long memberId, Integer tplId) {
        CommissionTpl tpl = commissionTplManager.getModel(tplId);
        DistributionDO ddo = this.getDistributorByMemberId(memberId);
        if (tpl == null || ddo == null) {
            throw new DistributionException(DistributionErrorCode.E1000.code(), DistributionErrorCode.E1000.des());
        }
        this.upgradeLogManager.addUpgradeLog(memberId, tplId, UpgradeTypeEnum.MANUAL);

        UpdateWrapper<DistributionDO> wrapper = new UpdateWrapper<>();
        wrapper.eq("member_id",memberId);
        wrapper.set("current_tpl_id",tplId.longValue());
        wrapper.set("current_tpl_name",commissionTplManager.getModel(tplId).getTplName());
        distributionMapper.update(null,wrapper);
    }

    /**
     * 统计下线人数
     */
    @Override
    public void countDown(Long memberId) {
        distributionMapper.updateDownline(memberId);
    }

}
