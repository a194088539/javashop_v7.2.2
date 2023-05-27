package com.enation.app.javashop.service.distribution.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.client.member.MemberClient;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.distribution.BillMemberMapper;
import com.enation.app.javashop.mapper.distribution.BillTotalMapper;
import com.enation.app.javashop.mapper.distribution.DistributionOrderMapper;
import com.enation.app.javashop.model.distribution.dos.BillMemberDO;
import com.enation.app.javashop.model.distribution.dos.BillTotalDO;
import com.enation.app.javashop.model.distribution.dos.DistributionOrderDO;
import com.enation.app.javashop.model.distribution.dto.DistributionRefundDTO;
import com.enation.app.javashop.model.distribution.vo.BillMemberVO;
import com.enation.app.javashop.model.distribution.vo.DistributionOrderVO;
import com.enation.app.javashop.model.distribution.vo.DistributionSellbackOrderVO;
import com.enation.app.javashop.model.distribution.vo.DistributionVO;
import com.enation.app.javashop.service.distribution.BillMemberManager;
import com.enation.app.javashop.service.distribution.BillTotalManager;
import com.enation.app.javashop.service.distribution.DistributionManager;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.framework.util.StringUtil;
import net.minidev.json.writer.UpdaterMapper;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * 用户结算单 实现
 *
 * @author Chopper
 * @version v1.0
 * @Description:
 * @since v7.0
 * 2018/5/22 上午8:52
 */

@SuppressWarnings("ALL")
@Service("billMemberManager")
public class BillMemberManagerImpl implements BillMemberManager {

    @Autowired
    private DistributionManager distributionManager;
    @Autowired
    private BillTotalManager billTotalManager;
    @Autowired
    private MemberClient memberClient;
    @Autowired
    private BillMemberMapper billMemberMapper;
    @Autowired
    private BillTotalMapper billTotalMapper;
    @Autowired
    private DistributionOrderMapper distributionOrderMapper;

    @Override
    public BillMemberDO getBillMember(Long billId) {
        QueryWrapper<BillMemberDO> wrapper = new QueryWrapper<>();
        wrapper.eq("id",billId);
        return billMemberMapper.selectOne(wrapper);
    }

    @Override
    public BillMemberDO add(BillMemberDO billMember) {
        billMemberMapper.insert(billMember);
        return billMember;
    }

    @Override
    public WebPage<DistributionOrderVO> listOrder(Long page, Long pageSize, Long totalId, Long memberId) {
        QueryWrapper<DistributionOrderDO> wrapper = new QueryWrapper<>();
        wrapper.eq("bill_id",totalId).eq("member_id",memberId);
        IPage<DistributionOrderDO> iPage = distributionOrderMapper.selectPage(new Page<>(page,pageSize), wrapper);
        WebPage<DistributionOrderDO> dos =  PageConvert.convert(iPage);

        List<DistributionOrderVO> data = new ArrayList<>();
        for (DistributionOrderDO fxdo : dos.getData()) {
            data.add(new DistributionOrderVO(fxdo, memberId));
        }
        WebPage<DistributionOrderVO> result = new WebPage<>();
        result.setData(data);
        result.setDataTotal(dos.getDataTotal());
        result.setPageNo(dos.getPageNo());
        result.setPageSize(dos.getPageSize());
        return result;
    }

    @Override
    public WebPage<DistributionSellbackOrderVO> listSellback(Long page, Long pagesize, Long totalId, Long memberId) {
        IPage<DistributionOrderDO> iPage = distributionOrderMapper.queryIdForPage(new Page<>(page,pagesize), totalId, memberId);
        WebPage<DistributionOrderDO> dos = PageConvert.convert(iPage);

        List<DistributionSellbackOrderVO> data = new ArrayList<>();
        for (DistributionOrderDO fxdo : dos.getData()) {
        data.add(new DistributionSellbackOrderVO(fxdo, memberId));
        }
        WebPage<DistributionSellbackOrderVO> result = new WebPage<>();
        result.setData(data);
        result.setDataTotal(dos.getDataTotal());
        result.setPageNo(dos.getPageNo());
        result.setPageSize(dos.getPageSize());
        return result;
    }

    @Override
    public WebPage<BillMemberVO> page(Long page, Long pageSize, Long totalid, String uname) {

        WebPage<BillMemberDO> data = null;
        if (StringUtil.isEmpty(uname)) {
            QueryWrapper<BillMemberDO> wrapper = new QueryWrapper<>();
            wrapper.eq("total_id",totalid);
            IPage<BillMemberDO> iPage = billMemberMapper.selectPage(new Page<>(page,pageSize), wrapper);
            data =  PageConvert.convert(iPage);
        } else {
            QueryWrapper<BillMemberDO> wrapper = new QueryWrapper<>();
            wrapper.eq("total_id",totalid).like("member_name",uname);
            IPage<BillMemberDO> iPage = billMemberMapper.selectPage(new Page<>(page,pageSize), wrapper);
            data =  PageConvert.convert(iPage);
        }

        List<BillMemberVO> vos = new ArrayList<>();
        for (BillMemberDO bdo : data.getData()) {
            vos.add(new BillMemberVO(bdo));
        }

        WebPage<BillMemberVO> result = new WebPage<>(data.getPageNo(), data.getDataTotal(), data.getPageSize(), vos);
        return result;
    }


    @Override
    public BillMemberVO getCurrentBillMember(Long memberId, Long billId) {
        Long[] time = getCurrentMonth();
        BillTotalDO billTotal = billTotalManager.getTotalByStart(time[0]);
        // 如果没有创建 总结算单
        if (null == billTotal) {
            billTotal = this.createTotal();
        }
        BillMemberDO bm = this.getBillByStart(time[0], memberId, billId);
        // 如果没有创建 总结算单
        if (null == bm) {
            bm = this.createBill(memberId,
                    billTotal.getId());
        }
        return new BillMemberVO(bm);
    }

    @Override
    public void buyShop(DistributionOrderDO order) {

        Long[] time = getCurrentMonth();
        BillTotalDO billTotal = billTotalManager.getTotalByStart(time[0]);
        // 如果没有创建 总结算单
        if (null == billTotal) {
            billTotal = this.createTotal();
        }
        // 修改总结算单
        BillTotalDO billTotalDO = new BillTotalDO();
        UpdateWrapper wrapper1 = new UpdateWrapper();
        wrapper1.eq("id",billTotal.getId());
        wrapper1.setSql("order_count = order_count + 1");
        wrapper1.setSql("order_money = order_money + " + order.getOrderPrice());
        billTotalMapper.update(billTotalDO,wrapper1);

//        DistributionOrderDO distributionOrderDo = new DistributionOrderDO();
//        UpdateWrapper<DistributionOrderDO> wrapper = new UpdateWrapper<>();
//        wrapper.eq("order_id",order.getOrderId());

        DistributionOrderDO distributionOrderDo = new DistributionOrderDO();
        QueryWrapper<DistributionOrderDO> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id",order.getOrderId());
        distributionOrderDo.setBillId(billTotal.getId());
        distributionOrderMapper.update(distributionOrderDo,wrapper);


        // 如果没有一级运营商
        if (null != order.getMemberIdLv1() && order.getMemberIdLv1() != 0) {
            BillMemberDO billMemberLv1 = this.getBillByStart(time[0],
                    order.getMemberIdLv1());
            //如果为空
            if (null == billMemberLv1) {
                billMemberLv1 = this.createBill(order.getMemberIdLv1(),
                        billTotal.getId());

            }
            // 修改分销商结算单
            BillMemberDO billMemberDO = new BillMemberDO();
            UpdateWrapper wrapper2 = new UpdateWrapper();
            wrapper2.eq("id",billMemberLv1.getId());
            wrapper2.setSql("push_money = push_money + " + order.getGrade1Rebate());
            wrapper2.setSql("final_money = final_money + " + order.getGrade1Rebate());
            wrapper2.setSql("order_count = order_count + 1");
            wrapper2.setSql("order_money = order_money + " + order.getOrderPrice());
            billMemberMapper.update(billMemberDO,wrapper2);

            // 修改总结算单
            BillTotalDO billTotalDo = new BillTotalDO();
            UpdateWrapper wrapper3 = new UpdateWrapper();
            wrapper3.eq("id",billTotal.getId());
            wrapper3.setSql("push_money = push_money + " + order.getGrade1Rebate());
            wrapper3.setSql("final_money = final_money + " + order.getGrade1Rebate());
            billTotalMapper.update(billTotalDo,wrapper3);

        }
        // 如果没有二级运营商
        if (null != order.getMemberIdLv2() && order.getMemberIdLv2() != 0) {
            BillMemberDO billMemberLv2 = this.getBillByStart(time[0],
                    order.getMemberIdLv2());
            //如果为空
            if (null == billMemberLv2) {
                billMemberLv2 = this.createBill(order.getMemberIdLv2(),
                        billTotal.getId());
            }

            // 修改分销商结算单
            BillMemberDO billMemberDO = new BillMemberDO();
            UpdateWrapper wrapper2 = new UpdateWrapper();
            wrapper2.eq("id",billMemberLv2.getId());
            wrapper2.setSql("push_money = push_money + " + order.getGrade2Rebate());
            wrapper2.setSql("final_money = final_money + " + order.getGrade2Rebate());
            wrapper2.setSql("order_count = order_count + 1");
            wrapper2.setSql("order_money = order_money + " + order.getOrderPrice());
            billMemberMapper.update(billMemberDO,wrapper2);

            // 修改总结算单
            BillTotalDO billTotalDo = new BillTotalDO();
            UpdateWrapper wrapper3 = new UpdateWrapper();
            wrapper3.eq("id",billTotal.getId());
            wrapper3.setSql("push_money = push_money + " + order.getGrade2Rebate());
            wrapper3.setSql("final_money = final_money + " + order.getGrade2Rebate());
            billTotalMapper.update(billTotalDo,wrapper3);
        }

    }

    @Override
    public void returnShop(DistributionOrderDO order, DistributionRefundDTO distributionRefundDTO) {
        //修改 is_return
        DistributionOrderDO distributionOrderDo = new DistributionOrderDO();
        UpdateWrapper<DistributionOrderDO> wrapper = new UpdateWrapper<>();
        wrapper.eq("order_id",order.getOrderId());
        distributionOrderDo.setIsReturn(1);
        distributionOrderMapper.update(distributionOrderDo,wrapper);

        Long[] time = getCurrentMonth();
        BillTotalDO billTotal = billTotalManager.getTotalByStart(time[0]);
        // 如果没有创建 总结算单
        if (null == billTotal) {
            billTotal = this.createTotal();
        }
        // 修改总结算单
        BillTotalDO billTotalDO = new BillTotalDO();
        UpdateWrapper wrapper1 = new UpdateWrapper();
        wrapper1.eq("id",billTotal.getId());
        wrapper1.setSql("return_order_count = return_order_count + 1");
        wrapper1.setSql("return_order_money = return_order_money + " + distributionRefundDTO.getRefundMoney());
        billTotalMapper.update(billTotalDO,wrapper1);

        // 如果没有一级运营商
        if (null != order.getMemberIdLv1() && 0 != order.getMemberIdLv1()) {
            BillMemberDO billMemberLv1 = this.getBillByStart(time[0],
                    order.getMemberIdLv1());

            // 修改总结算单
            BillTotalDO billTotalDo = new BillTotalDO();
            UpdateWrapper wrapper3 = new UpdateWrapper();
            wrapper3.eq("id", billTotal.getId());
            if (distributionRefundDTO.getRefundLv1() != null){
                wrapper3.setSql("return_push_money = return_push_money + " + distributionRefundDTO.getRefundLv1());
                wrapper3.setSql("final_money = final_money - " + distributionRefundDTO.getRefundLv1());
            }
            wrapper3.setSql("return_order_count=return_order_count+1");
            billTotalMapper.update(billTotalDO,wrapper3);

            //如果为空
            if (null == billMemberLv1) {
                billMemberLv1 = this.createBill(order.getMemberIdLv1(),
                        billTotal.getId());
                // 修改总结算单
                BillMemberDO billMemberDO = new BillMemberDO();
                UpdateWrapper wrapper2 = new UpdateWrapper();
                wrapper2.eq("id",billTotal.getId());
                if (distributionRefundDTO.getRefundLv1() != null){
                    wrapper2.setSql("return_push_money = return_push_money + " + distributionRefundDTO.getRefundLv1());
                    wrapper2.setSql("final_money = final_money - " + distributionRefundDTO.getRefundLv1());
                }
                billMemberMapper.update(billMemberDO,wrapper2);
            }
            // 修改分销商结算单
            BillMemberDO billMemberDO = new BillMemberDO();
            UpdateWrapper wrapper2 = new UpdateWrapper();
            wrapper2.eq("id",billMemberLv1.getId());
            if (distributionRefundDTO.getRefundLv1() != null){
                wrapper2.setSql("return_push_money = return_push_money + " + distributionRefundDTO.getRefundLv1());
                wrapper2.setSql("final_money = final_money - " + distributionRefundDTO.getRefundLv1());
            }
            wrapper2.setSql("return_order_count = return_order_count + 1");
            wrapper2.setSql("return_order_money = return_order_money + " + distributionRefundDTO.getRefundMoney());
            billMemberMapper.update(billMemberDO,wrapper2);
        }
        // 如果没有二级运营商
        if (null != order.getMemberIdLv2() && 0 != order.getMemberIdLv2()) {
            BillMemberDO billMemberLv2 = this.getBillByStart(time[0],
                    order.getMemberIdLv2());
            if (null == billMemberLv2) {
                billMemberLv2 = this.createBill(order.getMemberIdLv2(),
                        billTotal.getId());
            }
            // 修改分销商结算单
            BillMemberDO billMemberDO = new BillMemberDO();
            UpdateWrapper wrapper2 = new UpdateWrapper();
            wrapper2.eq("id",billMemberLv2.getId());
            if (distributionRefundDTO.getRefundLv2() != null){
                wrapper2.setSql("return_push_money = return_push_money + " + distributionRefundDTO.getRefundLv2());
                wrapper2.setSql("final_money = final_money - " + distributionRefundDTO.getRefundLv2());
            }
            wrapper2.setSql("return_order_count = return_order_count + 1");
            wrapper2.setSql("return_order_money = return_order_money + " + distributionRefundDTO.getRefundMoney());
            billMemberMapper.update(billMemberDO,wrapper2);

            // 修改总结算单
            BillMemberDO billMemberDo = new BillMemberDO();
            UpdateWrapper wrapper3 = new UpdateWrapper();
            wrapper3.eq("id",billTotal.getId());
            if (distributionRefundDTO.getRefundLv2() != null){
                wrapper3.setSql("return_push_money = return_push_money + " + distributionRefundDTO.getRefundLv2());
                wrapper3.setSql("final_money = final_money - " + distributionRefundDTO.getRefundLv2());
            }
            billMemberMapper.update(billMemberDO,wrapper3);
        }
    }

    /**
     * 创建分销商结算单
     *
     * @return
     */
    private BillMemberDO createBill(Long memberId, Long totalId) {
        Long[] time = getCurrentMonth();
        BillMemberDO billMember = new BillMemberDO();
        billMember.setMemberId(memberId);
        billMember.setStartTime(time[0]);
        billMember.setEndTime(time[1]);
        billMember.setOrderCount(0);
        billMember.setFinalMoney(0D);
        billMember.setOrderMoney(0d);
        billMember.setPushMoney(0d);
        billMember.setReturnPushMoney(0d);
        billMember.setReturnOrderMoney(0d);
        billMember.setReturnOrderCount(0);
        billMember.setSn(this.createSn());
        billMember.setTotalId(totalId);
        billMember.setMemberName(memberClient.getModel(memberId).getUname());
        this.add(billMember);

        QueryWrapper<BillMemberDO> wrapper = new QueryWrapper<>();
        wrapper.eq("total_id",totalId).eq("member_id",memberId);
        return billMemberMapper.selectOne(wrapper);
    }

    /**
     * 获取 分销商结算单
     *
     * @param startTime
     * @param memberId
     * @param billId
     * @return
     */
    public BillMemberDO getBillByStart(Long startTime, Long memberId) {
        QueryWrapper<BillMemberDO> wrapper = new QueryWrapper<>();
        wrapper.eq("start_time",startTime).eq("member_id",memberId);
        return billMemberMapper.selectOne(wrapper);
    }

    /**
     * 获取 分销商结算单
     *
     * @param startTime
     * @param memberId
     * @param billId
     * @return
     */
    public BillMemberDO getBillByStart(Long startTime, Long memberId, Long billId) {
        QueryWrapper<BillMemberDO> wrapper = new QueryWrapper<>();
        wrapper.eq("start_time",startTime).eq("member_id",memberId);
        if (billId != null && billId != 0) {
            QueryWrapper<BillMemberDO> wrapperr = new QueryWrapper<>();
            wrapperr.eq("id",billId);
            return billMemberMapper.selectOne(wrapperr);
        }
        return billMemberMapper.selectOne(wrapper);
    }

    /**
     * 创建总结算单
     */
    private BillTotalDO createTotal() {
        Long[] time = getCurrentMonth();
        BillTotalDO billTotal = new BillTotalDO();
        billTotal.setSn(this.createSn());
        billTotal.setFinalMoney(0D);
        billTotal.setEndTime(time[1]);
        billTotal.setStartTime(time[0]);
        billTotal.setOrderCount(0);
        billTotal.setOrderMoney(0D);
        billTotal.setPushMoney(0D);
        billTotal.setReturnOrderCount(0);
        billTotal.setReturnOrderMoney(0D);
        billTotal.setReturnPushMoney(0D);
        billTotal.setFinalMoney(0D);
        billTotalManager.add(billTotal);

        QueryWrapper<BillTotalDO> wrapper = new QueryWrapper<>();
        wrapper.eq("start_time",time[0]);
        return billTotalMapper.selectOne(wrapper);
    }

    /**
     * 创建结算单号（日期+两位随机数）
     */
    public String createSn() {
        Random random = new Random();

        int result = random.nextInt(10);

        String sn = DateUtil.getDateline() + "" + result;
        return sn;
    }

    /**
     * 获取随机数
     *
     * @return
     */
    public int getRandom() {
        Random random = new Random();
        int num = Math.abs(random.nextInt()) % 100;
        //如果num小于10
        if (num < 10) {
            num = getRandom();
        }
        return num;
    }


    /**
     * 获取某个会员的下线业绩
     *
     * @param memberId
     * @return
     */
    @Override
    public List<BillMemberVO> allDown(Long memberId, Long billId) {
        //获取所有下线的分销业绩
        List<BillMemberDO> billMemberDOS = billMemberMapper.queryAllDown(memberId, billId);

        //获取下级 分销商集合
        List<DistributionVO> distributionVOS = this.distributionManager.getLowerDistributorTree(memberId);

        /*
         * 返回所有分销商集合  及分销商订单
         * 下级分销商没有下单,则返回分销商信息   分销业绩默认为0
         * add by liuyulei 2019-08-01
         */
        distributionVOS = this.convertDistribution(distributionVOS, billMemberDOS);

        List<BillMemberVO> result = new ArrayList<>();

        result = convertBillMember(distributionVOS, result);

        return result;
    }


    @Override
    public BillMemberDO getBillByTotalSn(String totalSn, Long memberId) {
        BillMemberDO billmember = billMemberMapper.getBillByTotalSn(totalSn, memberId);
        return billmember;
    }

    @Override
    public BillMemberDO getHistoryBillMember(Long memberId, String sn) {
        QueryWrapper<BillMemberDO> wrapper = new QueryWrapper<>();
        wrapper.eq("member_id",memberId).eq("sn",sn);
        return billMemberMapper.selectOne(wrapper);
    }

    @Override
    public WebPage<BillMemberVO> getAllByMemberId(Long memberId, Long pageNo, Long pageSize) {
        IPage<BillMemberVO> iPage = billMemberMapper.billMemberPage(new Page<BillMemberVO>(pageNo, pageSize),memberId);
        return PageConvert.convert(iPage);
    }

    @Override
    public WebPage<BillMemberVO> billMemberPage(Long pageNo, Long pageSize, Long memberId) {
        IPage<BillMemberVO> iPage = billMemberMapper.billMemberPage(new Page<BillMemberVO>(pageNo, pageSize),memberId);
        return PageConvert.convert(iPage);
    }

    /**
     * 获取当月的开始结束时间
     *
     * @return
     */
    private static Long[] getCurrentMonth() {
        // 取得系统当前时间
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        // 输出下月第一天日期
        int notMonth = cal.get(Calendar.MONTH) + 2;
        // 取得系统当前时间所在月第一天时间对象
        cal.set(Calendar.DAY_OF_MONTH, 1);

        // 日期减一,取得上月最后一天时间对象
        cal.add(Calendar.DAY_OF_MONTH, -1);

        String months = "";
        String nextMonths = "";

        if (!(String.valueOf(month).length() > 1)) {
            months = "0" + month;
        } else {
            months = String.valueOf(month);
        }
        if (!(String.valueOf(notMonth).length() > 1)) {
            nextMonths = "0" + notMonth;
        } else {
            nextMonths = String.valueOf(notMonth);
        }
        String firstDay = "" + year + "-" + months + "-01";
        String lastDay = "" + year + "-" + nextMonths + "-01";
        Long[] currentMonth = new Long[2];
        currentMonth[0] = DateUtil.getDateline(firstDay);
        currentMonth[1] = DateUtil.getDateline(lastDay);

        return currentMonth;
    }


    /**
     * 整合分销商集合与分销业绩集合
     *
     * @param distributionVOS
     * @param billMemberDOS
     * @return
     */
    private List<DistributionVO> convertDistribution(List<DistributionVO> distributionVOS, List<BillMemberDO> billMemberDOS) {

        for (DistributionVO distributionVO : distributionVOS) {
            if (distributionVO.getItem() != null && !distributionVO.getItem().isEmpty()) {
                convertDistribution(distributionVO.getItem(), billMemberDOS);
            }

            for (BillMemberDO billMemberDO : billMemberDOS) {
                if (distributionVO.getId().equals(billMemberDO.getMemberId())) {
                    distributionVO.setBillMemberVO(new BillMemberVO(billMemberDO));
                } else {
                    distributionVO.setBillMemberVO(new BillMemberVO());
                }
            }
        }

        return distributionVOS;
    }


    /**
     * 递归遍历所有分销商的业绩列表
     *
     * @param distributionVOS
     * @param result
     * @return
     */
    private List<BillMemberVO> convertBillMember(List<DistributionVO> distributionVOS, List<BillMemberVO> result) {

        for (DistributionVO dos : distributionVOS) {

            BillMemberVO billMemberVO = dos.getBillMemberVO();
            if (billMemberVO == null) {
                billMemberVO = new BillMemberVO();

            }
            billMemberVO.setMemberName(dos.getName());
            billMemberVO.setItem(new ArrayList<>());
            if (dos.getItem() != null && !dos.getItem().isEmpty()) {
                billMemberVO.setItem(convertBillMember(dos.getItem(), billMemberVO.getItem()));
            }
            result.add(billMemberVO);
        }

        return result;
    }

}
