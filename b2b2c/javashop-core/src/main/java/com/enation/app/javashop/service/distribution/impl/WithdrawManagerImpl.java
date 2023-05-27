package com.enation.app.javashop.service.distribution.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.client.member.ConnectClient;
import com.enation.app.javashop.client.payment.WechatSmallchangeClient;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.sncreator.SnCreator;
import com.enation.app.javashop.framework.util.*;
import com.enation.app.javashop.mapper.distribution.DistributionMapper;
import com.enation.app.javashop.mapper.distribution.WithdrawApplyMapper;
import com.enation.app.javashop.mapper.distribution.WithdrawSettingMapper;
import com.enation.app.javashop.model.base.SubCode;
import com.enation.app.javashop.model.distribution.dos.DistributionDO;
import com.enation.app.javashop.model.distribution.dos.WithdrawApplyDO;
import com.enation.app.javashop.model.distribution.dos.WithdrawSettingDO;
import com.enation.app.javashop.model.distribution.enums.WithdrawStatusEnum;
import com.enation.app.javashop.model.distribution.vo.BankParamsVO;
import com.enation.app.javashop.model.distribution.vo.WithdrawApplyVO;
import com.enation.app.javashop.model.distribution.vo.WithdrawAuditPaidVO;
import com.enation.app.javashop.model.errorcode.DistributionErrorCode;
import com.enation.app.javashop.model.member.dos.ConnectDO;
import com.enation.app.javashop.model.member.enums.ConnectTypeEnum;
import com.enation.app.javashop.service.distribution.DistributionManager;
import com.enation.app.javashop.service.distribution.WithdrawManager;
import com.enation.app.javashop.service.distribution.exception.DistributionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 提现设置实现
 *
 * @author Chopper
 * @version v1.0
 * @Description:
 * @since v7.0
 * 2018/5/22 下午12:57
 */

@Service
public class WithdrawManagerImpl implements WithdrawManager {

    @Autowired
    private DistributionManager distributionManager;
    @Autowired
    private ConnectClient connectClient;
    @Autowired
    private WechatSmallchangeClient wechatSmallchangeClient;
    @Autowired
    SnCreator snCreator;

    @Autowired
    private WithdrawApplyMapper withdrawApplyMapper;
    @Autowired
    private WithdrawSettingMapper withdrawSettingMapper;
    @Autowired
    private DistributionMapper distributionMapper;


    @Override
    public WithdrawApplyDO getModel(Long id) {
        QueryWrapper<WithdrawApplyDO> wrapper = new QueryWrapper<>();
        wrapper.eq("id", id);
        return withdrawApplyMapper.selectOne(wrapper);
    }

    @Override
    public WebPage<WithdrawApplyVO> pageWithdrawApply(Long memberId, Long pageNo, Long pageSize) {
        IPage<WithdrawApplyDO> iPage = withdrawApplyMapper.pageWithdrawApply(new Page<>(pageNo, pageSize), memberId);
        WebPage<WithdrawApplyVO> result = this.convertPage(iPage);
        return result;
    }

    @Override
    public void saveWithdrawWay(BankParamsVO bankParams) {
        Long userId = UserContext.getBuyer().getUid();
        QueryWrapper<WithdrawSettingDO> wrapper = new QueryWrapper<>();
        wrapper.eq("member_id", userId);
        WithdrawSettingDO withdrawSetting = withdrawSettingMapper.selectOne(wrapper);

        if (withdrawSetting != null) {
            withdrawSetting.setMemberId(userId);
            withdrawSetting.setParam(JsonUtil.objectToJson(bankParams));
            withdrawSettingMapper.updateById(withdrawSetting);
//            Map where = new HashMap(16);
//            where.put("id", withdrawSetting.getId());
//            this.daoSupport.update("es_withdraw_setting", withdrawSetting, where);
        } else {
            withdrawSetting = new WithdrawSettingDO();
            withdrawSetting.setMemberId(userId);
            withdrawSetting.setParam(JsonUtil.objectToJson(bankParams));
            withdrawSettingMapper.insert(withdrawSetting);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void applyWithdraw(Long memberId, Double applyMoney, String applyRemark) {
        WithdrawApplyDO apply = new WithdrawApplyDO();
        apply.setApplyTime(DateUtil.getDateline());
        apply.setApplyMoney(applyMoney);
        apply.setApplyRemark(applyRemark);
        apply.setStatus(WithdrawStatusEnum.APPLY.name());
        apply.setMemberId(memberId);
        apply.setMemberName(UserContext.getBuyer().getUsername());
        apply.setSn("" + snCreator.create(SubCode.PAY_BILL));
        apply.setIp(IPUtil.getIpAdrress());
        withdrawApplyMapper.insert(apply);
        // 修改可提现金额
        UpdateWrapper wrapper = new UpdateWrapper();
        DistributionDO distributionDO = new DistributionDO();
        wrapper.eq("member_id", memberId);
        wrapper.setSql("can_rebate=can_rebate-" + applyMoney);
        wrapper.setSql("withdraw_frozen_price=withdraw_frozen_price+" + applyMoney);
        distributionMapper.update(distributionDO, wrapper);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void batchAuditing(WithdrawAuditPaidVO withdrawAuditPaidVO, String auditResult) {
        //判断是否选择了提现申请
        if (withdrawAuditPaidVO.getApplyIds() == null || withdrawAuditPaidVO.getApplyIds().length == 0) {
            throw new DistributionException(DistributionErrorCode.E1000.code(), "请选择要审核的提现申请");
        }

        //判断审核状态值是否正确
        if (StringUtil.isEmpty(auditResult) || (!WithdrawStatusEnum.VIA_AUDITING.value().equals(auditResult) && !WithdrawStatusEnum.FAIL_AUDITING.value().equals(auditResult))) {
            throw new DistributionException(DistributionErrorCode.E1005.code(), "审核状态值不正确");
        }

        for (Long applyId : withdrawAuditPaidVO.getApplyIds()) {
            WithdrawApplyDO wdo = this.getModel(applyId);
            //判断提现申请是否存在
            if (wdo == null) {
                throw new DistributionException(DistributionErrorCode.E1004.code(), "ID为" + wdo.getId() + "的提现申请不存在");
            }

            //除状态为申请中的提现申请，其它状态的提现申请都不允许审核
            if (!WithdrawStatusEnum.APPLY.value().equals(wdo.getStatus())) {
                throw new DistributionException(DistributionErrorCode.E1002.code(), "ID为" + wdo.getId() + "的提现申请已经审核，不能重复审核");
            }

            //判断审核状态值是否正确
            if (StringUtil.isEmpty(auditResult) || (!WithdrawStatusEnum.VIA_AUDITING.value().equals(auditResult) && !WithdrawStatusEnum.FAIL_AUDITING.value().equals(auditResult))) {
                throw new DistributionException(DistributionErrorCode.E1005.code(), "审核状态值不正确");
            }

            //更改提现申请审核状态数据
            WithdrawApplyDO withdrawApplyDo = new WithdrawApplyDO();
            UpdateWrapper<WithdrawApplyDO> wrapper = new UpdateWrapper<>();
            wrapper.eq("id", applyId);
            withdrawApplyDo.setStatus(auditResult);
            withdrawApplyDo.setInspectTime(DateUtil.getDateline());
            withdrawApplyDo.setInspectRemark(withdrawAuditPaidVO.getRemark());
            withdrawApplyMapper.update(withdrawApplyDo, wrapper);

            //如果审核未通过，要将提现的金额返还
            if (WithdrawStatusEnum.FAIL_AUDITING.name().equals(auditResult)) {
                // 获取分销商信息
                DistributionDO distributionDO = this.distributionManager.getDistributorByMemberId(wdo.getMemberId());

                double rebate = CurrencyUtil.add(distributionDO.getCanRebate(), wdo.getApplyMoney());
                double frozen = CurrencyUtil.sub(distributionDO.getWithdrawFrozenPrice(), wdo.getApplyMoney());

                DistributionDO distributionDo = new DistributionDO();
                UpdateWrapper<DistributionDO> wrapperr = new UpdateWrapper<>();
                wrapperr.eq("member_id", wdo.getMemberId());
                distributionDo.setCanRebate(rebate);
                distributionDo.setWithdrawFrozenPrice(frozen);
                distributionMapper.update(distributionDo, wrapperr);
            } else {
                autoSend(applyId);
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void batchAccountPaid(WithdrawAuditPaidVO withdrawAuditPaidVO) {
        //判断是否选择了提现申请
        if (withdrawAuditPaidVO.getApplyIds() == null || withdrawAuditPaidVO.getApplyIds().length == 0) {
            throw new DistributionException(DistributionErrorCode.E1000.code(), "请选择要设置为已转账的提现申请");
        }

        for (Long applyId : withdrawAuditPaidVO.getApplyIds()) {
            WithdrawApplyDO wdo = this.getModel(applyId);
            //判断提现申请是否存在
            if (wdo == null) {
                throw new DistributionException(DistributionErrorCode.E1004.code(), "ID为" + wdo.getId() + "的提现申请不存在");
            }
            //除状态为审核通过的提现申请，其它状态的提现申请都不允许设置已转账
            if (!WithdrawStatusEnum.VIA_AUDITING.value().equals(wdo.getStatus())) {
                throw new DistributionException(DistributionErrorCode.E1002.code(), "ID为" + wdo.getId() + "的提现申请审核未通过，不能设置已转账");
            }

            WithdrawApplyDO withdrawApplyDo = new WithdrawApplyDO();
            UpdateWrapper<WithdrawApplyDO> wrapper = new UpdateWrapper<>();
            wrapper.eq("id", applyId);
            withdrawApplyDo.setStatus(WithdrawStatusEnum.TRANSFER_ACCOUNTS.name());
            withdrawApplyDo.setTransferTime(DateUtil.getDateline());
            withdrawApplyDo.setTransferRemark(withdrawAuditPaidVO.getRemark());
            withdrawApplyMapper.update(withdrawApplyDo, wrapper);
        }
    }

    @Override
    public BankParamsVO getWithdrawSetting(Long memberId) {
        QueryWrapper<WithdrawSettingDO> wrapper = new QueryWrapper<>();
        wrapper.eq("member_id", memberId);
        WithdrawSettingDO withdrawSetting = withdrawSettingMapper.selectOne(wrapper);
        if (withdrawSetting == null) {
            return new BankParamsVO();
        }
        return JsonUtil.jsonToObject(withdrawSetting.getParam(), BankParamsVO.class);
    }

    @Override
    public WebPage<WithdrawApplyVO> pageApply(Long pageNo, Long pageSize, Map<String, String> map) {
        QueryWrapper<WithdrawApplyDO> wrapper = new QueryWrapper<>();
        wrapper.like(!StringUtil.isEmpty(map.get("uname")), "member_name", map.get("uname"));
        wrapper.gt(!StringUtil.isEmpty(map.get("start_time")), "apply_time", map.get("start_time"));
        wrapper.lt(!StringUtil.isEmpty(map.get("end_time")), "apply_time", map.get("end_time"));
        wrapper.eq(!StringUtil.isEmpty(map.get("status")), "status", map.get("status"));
        wrapper.orderByDesc("id");

        IPage<WithdrawApplyDO> iPage = withdrawApplyMapper.selectPage(new Page<>(pageNo, pageSize), wrapper);
        WebPage<WithdrawApplyVO> result = this.convertPage(iPage);

        return result;
    }

    @Override
    public List<WithdrawApplyDO> exportApply(Map<String, String> map) {

        QueryWrapper<WithdrawApplyDO> wrapper = new QueryWrapper<>();
        wrapper.like(!StringUtil.isEmpty(map.get("uname")), "member_name", map.get("uname"));
        wrapper.gt(!StringUtil.isEmpty(map.get("start_time")), "apply_time", map.get("start_time"));
        wrapper.lt(!StringUtil.isEmpty(map.get("end_time")), "apply_time", map.get("end_time"));
        wrapper.eq(!StringUtil.isEmpty(map.get("status")), "status", map.get("status"));
        wrapper.orderByDesc("apply_time");
        List<WithdrawApplyDO> applyList = withdrawApplyMapper.selectList(wrapper);

        return applyList;
    }

    @Override
    public Double getRebate(Long memberId) {

        QueryWrapper<DistributionDO> wrapper = new QueryWrapper<>();
        wrapper.select("can_rebate");
        wrapper.eq("member_id", memberId);
        Double rebate = distributionMapper.selectOne(wrapper).getCanRebate();
        return rebate <= 0 ? 0 : rebate;
    }

    /**
     * 转换page
     *
     * @param page
     * @return
     */
    private WebPage convertPage(IPage<WithdrawApplyDO> page) {
        List<WithdrawApplyVO> vos = new ArrayList<>();
        for (WithdrawApplyDO withdrawApplyDO : page.getRecords()) {
            WithdrawApplyVO applyVO = new WithdrawApplyVO(withdrawApplyDO);
            BankParamsVO paramsVO = this.getWithdrawSetting(withdrawApplyDO.getMemberId());
            applyVO.setBankParamsVO(paramsVO);
            vos.add(applyVO);
        }
        WebPage result = new WebPage(page.getPages(), page.getTotal(), page.getSize(), vos);
        return result;
    }

    /**
     * 自动发送红包
     *
     * @param applyId
     */
    private void autoSend(Long applyId) {
        WithdrawApplyDO withdrawApplyDO = this.getModel(applyId);
        ConnectDO connectDO = connectClient.getConnect(withdrawApplyDO.getMemberId(), ConnectTypeEnum.WECHAT_OPENID.value());
        if (connectDO == null) {
            return;
        }
        boolean success = wechatSmallchangeClient.autoSend(connectDO.getUnionId(), withdrawApplyDO.getApplyMoney(), withdrawApplyDO.getIp(), withdrawApplyDO.getSn());
        if (success) {
            WithdrawApplyDO withdrawApplyDo = new WithdrawApplyDO();
            UpdateWrapper<WithdrawApplyDO> wrapper = new UpdateWrapper<>();
            wrapper.eq("id", applyId);
            withdrawApplyDo.setStatus(WithdrawStatusEnum.TRANSFER_ACCOUNTS.name());
            withdrawApplyDo.setTransferTime(DateUtil.getDateline());
            withdrawApplyDo.setTransferRemark("零钱转账");
            withdrawApplyMapper.update(withdrawApplyDo, wrapper);
        }
    }
}
