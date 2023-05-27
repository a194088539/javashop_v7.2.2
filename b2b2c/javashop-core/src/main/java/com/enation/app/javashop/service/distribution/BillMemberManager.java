package com.enation.app.javashop.service.distribution;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.distribution.dos.BillMemberDO;
import com.enation.app.javashop.model.distribution.dos.DistributionOrderDO;
import com.enation.app.javashop.model.distribution.dto.DistributionRefundDTO;
import com.enation.app.javashop.model.distribution.vo.BillMemberVO;
import com.enation.app.javashop.model.distribution.vo.DistributionOrderVO;
import com.enation.app.javashop.model.distribution.vo.DistributionSellbackOrderVO;

import java.util.List;

/**
 * 用户结算单
 *
 * @author Chopper
 * @version v1.0
 * @Description:
 * @since v7.0
 * 2018/5/22 上午8:51
 */

public interface BillMemberManager {

    /**
     * 获取某个会员的下线业绩
     * @param memberId
     * @param billId
     * @return
     */
    List<BillMemberVO> allDown(Long memberId, Long billId);


    /**
     * 获取结算单
     *
     * @param totalSn
     * @param memberId
     * @return
     */
    BillMemberDO getBillByTotalSn(String totalSn, Long memberId);

    /**
     * 分页获取会员历史业绩
     * @param memberId
     * @param pageNo
     * @param pageSize
     * @return
     */
    WebPage<BillMemberVO> getAllByMemberId(Long memberId, Long pageNo, Long pageSize);

    /**
     * 新增一个结算单
     *
     * @param billMember
     * @return
     */
    BillMemberDO add(BillMemberDO billMember);

    /**
     * 查询一个总结算单的会员结算单
     *
     * @param page
     * @param pageSize
     * @param id
     * @param uname
     * @return page
     */
    WebPage<BillMemberVO> page(Long page, Long pageSize, Long id, String uname);


    /**
     * 获取分销商结算单
     *
     * @param billId 总结算单id
     * @return do
     */
    BillMemberDO getBillMember(Long billId);


    /**
     * 购买商品产生的结算
     *
     * @param order
     */
    void buyShop(DistributionOrderDO order);

    /**
     * 退货商品产生的结算
     *
     * @param order
     */
    void returnShop(DistributionOrderDO order, DistributionRefundDTO distributionRefundDTO);

    /**
     * 获取分销账单
     *
     * @param page
     * @param pageSize
     * @param id
     * @param memberId
     * @return page
     */
    WebPage<DistributionOrderVO> listOrder(Long page, Long pageSize, Long id, Long memberId);


    /**
     * 获取分销退款单
     *
     * @param page
     * @param pagesize
     * @param id
     * @param memberId
     * @return page
     */
    WebPage<DistributionSellbackOrderVO> listSellback(Long page, Long pagesize, Long id, Long memberId);

    /**
     * 获取当前分销商当月结算单
     *
     * @param memberId 会员id
     * @return model
     */
    BillMemberVO getCurrentBillMember(Long memberId, Long billId);

    /**
     * 获取指定sn的分销商结算单
     *
     * @param memberId
     * @param sn
     * @return do
     */
    BillMemberDO getHistoryBillMember(Long memberId, String sn);


    /**
     * 结算单会员分页查询
     *
     * @param pageNo
     * @param pageSize
     * @param memberId
     * @return page
     */
    WebPage<BillMemberVO> billMemberPage(Long pageNo, Long pageSize, Long memberId);

}
