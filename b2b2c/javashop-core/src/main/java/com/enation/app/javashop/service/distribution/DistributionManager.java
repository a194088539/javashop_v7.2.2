package com.enation.app.javashop.service.distribution;

import com.enation.app.javashop.model.distribution.dos.DistributionDO;
import com.enation.app.javashop.model.distribution.vo.DistributionVO;
import com.enation.app.javashop.framework.database.WebPage;

import java.util.List;

/**
 * 分销商Manager接口
 *
 * @author Chopper
 * @version v1.0
 * @Description:
 * @since v7.0
 * 2018/5/21 下午3:19
 */
public interface DistributionManager {

    /**
     * 新增分销商
     * @param distributor
     * @return
     */
    DistributionDO add(DistributionDO distributor);

    /**
     * 所有下线
     * @param memberId
     * @return
     */
    List<DistributionVO> allDown(Long memberId);


    /**
     * 分页分销商
     * @param pageNo 页码
     * @param pageSize 分页大小
     * @param memberName 会员名字
     * @return PAGE
     */
    WebPage page(Long pageNo, Long pageSize, String memberName);

    /**
     * 根据会员id获得分销商的信息
     *
     * @param memberId 会员id
     * @return 分销商对象 Distributor,没有就返回null
     */
    DistributionDO getDistributorByMemberId(Long memberId);

    /**
     * 根据会员id获得分销商的信息
     *
     * @param id 分销商id
     * @return 分销商对象 Distributor,没有就返回null
     */
    DistributionDO getDistributor(Long id);


    /**
     * 更新Distributor信息
     * @param distributor
     * @return
     */
    DistributionDO edit(DistributionDO distributor);


    /**
     * 根据会员id设置其上级分销商（两级）
     *
     * @param memberId 会员id
     * @param parentId 上级会员的id
     * @return 设置结果， trun=成功 false=失败
     */
    boolean setParentDistributorId(Long memberId, Long parentId);

    /**
     * 获取可提现金额
     *
     * @param memberId
     * @return
     */
    Double getCanRebate(Long memberId);

    /**
     * 增加冻结返利金额
     *
     * @param price     返利金额金额
     * @param memberId 会员id
     */
    void addFrozenCommission(Double price, Long memberId);


    /**
     * 增加总销售额、总的返利金额金额
     *
     * @param orderPrice 订单金额
     * @param rebate     返利金额
     * @param memberId  会员id
     */
    void addTotalPrice(Double orderPrice, Double rebate, Long memberId);

    /**
     * 减去总销售额、总的返利金额金额
     *
     * @param orderPrice 订单金额
     * @param rebate     返利金额
     * @param memberId  会员id
     */
    void subTotalPrice(Double orderPrice, Double rebate, Long memberId);

    /**
     * 获取当前会员 的上级
     *
     * @return 返回的字符串
     */
    String getUpMember();

    /**
     * 获取下级 分销商集合
     *
     * @param memberId
     * @return
     */
    List<DistributionVO> getLowerDistributorTree(Long memberId);

    /**
     * 修改模版
     * @param memberId
     * @param tplId
     */
    void changeTpl(Long memberId, Integer tplId);

    /**
     * 统计下线人数
     * @param memberId
     */
    void countDown(Long memberId);

}
