package com.enation.app.javashop.service.distribution;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.distribution.dos.WithdrawApplyDO;
import com.enation.app.javashop.model.distribution.vo.BankParamsVO;
import com.enation.app.javashop.model.distribution.vo.WithdrawApplyVO;
import com.enation.app.javashop.model.distribution.vo.WithdrawAuditPaidVO;

import java.util.List;
import java.util.Map;


/**
 * 提现接口
 *
 * @author Chopper
 * @version v1.0
 * @Description:
 * @since v7.0
 * 2018/5/22 下午1:18
 */
public interface WithdrawManager {

    /**
     * 根据ID提现申请详细记录
     *
     * @param id
     * @return
     */
    WithdrawApplyDO getModel(Long id);

    /**
     * 申请提现
     *
     * @param memberId    会员id
     * @param applyMoney  申请金额
     * @param applyRemark 备注
     */
    void applyWithdraw(Long memberId, Double applyMoney, String applyRemark);

    /**
     * 批量审核提现申请
     *
     * @param withdrawAuditPaidVO
     * @param auditResult 审核结果
     */
    void batchAuditing(WithdrawAuditPaidVO withdrawAuditPaidVO, String auditResult);

    /**
     * 批量设置已转账
     * @param withdrawAuditPaidVO
     */
    void batchAccountPaid(WithdrawAuditPaidVO withdrawAuditPaidVO);

    /**
     * 根据member_id查询提现记录
     * @param memeberId
     * @param pageNo
     * @param pageSize
     * @return
     */
    WebPage<WithdrawApplyVO> pageWithdrawApply(Long memeberId, Long pageNo, Long pageSize);

    /**
     * 保存提现设置
     * @param bankParams
     */
    void saveWithdrawWay(BankParamsVO bankParams);

    /**
     * 获取提现设置
     *
     * @param memberId
     * @return
     */
    BankParamsVO getWithdrawSetting(Long memberId);

    /**
     * 分页会员提现查询
     *
     * @param pageNo
     * @param pageSize
     * @param map
     * @return
     */
    WebPage<WithdrawApplyVO> pageApply(Long pageNo, Long pageSize, Map<String, String> map);

    /**
     * 导出提现申请
     * @param map 筛选参数
     * @return
     */
    List<WithdrawApplyDO> exportApply(Map<String, String> map);

    /**
     * 分页会员提现查询
     *
     * @param memberId
     * @return
     */
    Double getRebate(Long memberId);

}
