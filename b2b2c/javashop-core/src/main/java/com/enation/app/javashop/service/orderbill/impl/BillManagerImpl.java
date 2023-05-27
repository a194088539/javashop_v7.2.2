package com.enation.app.javashop.service.orderbill.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.client.distribution.DistributionSellerBillClient;
import com.enation.app.javashop.client.member.ShopClient;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.mapper.trade.BillItemMapper;
import com.enation.app.javashop.mapper.trade.BillMapper;
import com.enation.app.javashop.model.distribution.dto.DistributionSellerBillDTO;
import com.enation.app.javashop.model.goods.enums.Permission;
import com.enation.app.javashop.model.errorcode.OrderBillErrorCode;
import com.enation.app.javashop.model.orderbill.dos.BillItem;
import com.enation.app.javashop.model.orderbill.enums.BillStatusEnum;
import com.enation.app.javashop.model.orderbill.enums.BillType;
import com.enation.app.javashop.model.orderbill.vo.*;
import com.enation.app.javashop.service.orderbill.BillItemManager;
import com.enation.app.javashop.model.shop.dto.ShopBankDTO;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.security.model.Seller;
import com.enation.app.javashop.framework.util.*;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.javashop.framework.database.DaoSupport;
import com.enation.app.javashop.model.orderbill.dos.Bill;
import com.enation.app.javashop.service.orderbill.BillManager;

import java.util.*;

/**
 * 结算单业务类
 *
 * @author fk
 * @version v1.0
 * @since v7.0.0
 * 2018-04-26 16:21:26
 */
@Service
public class BillManagerImpl implements BillManager {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private BillItemManager billItemManager;
    @Autowired
    private DistributionSellerBillClient distributionSellerBillClient;
    @Autowired
    private ShopClient shopClient;
    @Autowired
    private BillItemMapper billItemMapper;
    @Autowired
    private BillMapper billMapper;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 结算的前缀
     */
    private static final String BILL_SN_CACHE_PREFIX = "BILL_SN";

    /**
     * 结算的周期账单前缀
     */
    private static final String SN_CACHE_PREFIX = "BILL_SN_TOTAL";

    @Override
    public WebPage list(long page, long pageSize) {
        QueryWrapper<Bill> wrapper = new QueryWrapper<>();
        IPage<Bill> iPage = billMapper.selectPage(new Page<>(page,pageSize), wrapper);
        return PageConvert.convert(iPage);
    }

    @Override
    public WebPage getAllBill(Long pageNo, Long pageSize, String sn) {
        IPage<Bill> iPage = billMapper.queryAllBill(new Page<>(pageNo, pageSize),sn);
        return PageConvert.convert(iPage);
    }


    @Override
    public WebPage queryBills(BillQueryParam param) {
        IPage<BillDetail> iPage = billMapper.queryBillDetail(new Page<>(param.getPageNo(), param.getPageSize()),param);
        return PageConvert.convert(iPage);
    }

    @Override
    public BillDetail getBillDetail(Long billId, Permission permission) {

        BillDetail bill = billMapper.queryDetail(billId);

        if (bill == null) {
            throw new ServiceException(OrderBillErrorCode.E700.code(), "没有权限");
        }

        //卖家所属权限校验
        if (Permission.SELLER.equals(permission)) {
            Seller seller = UserContext.getSeller();
            if (!bill.getSellerId().equals(seller.getSellerId())) {
                throw new ServiceException(OrderBillErrorCode.E700.code(), "没有权限");
            }
        }

        OperateAllowable allowable = new OperateAllowable(BillStatusEnum.valueOf(bill.getStatus()), permission);
        bill.setOperateAllowable(allowable);

        return bill;
    }

    @Override
    public Bill editStatus(Long billId, Permission permission) {

        Bill bill = this.getModel(billId);
        if (bill == null) {
            throw new ServiceException(OrderBillErrorCode.E700.code(), "没有权限");
        }

        Map<BillStatusEnum, BillStatusEnum> map = new HashMap(4);
        map.put(BillStatusEnum.OUT, BillStatusEnum.RECON);
        map.put(BillStatusEnum.RECON, BillStatusEnum.PASS);
        map.put(BillStatusEnum.PASS, BillStatusEnum.PAY);
        map.put(BillStatusEnum.PAY, BillStatusEnum.COMPLETE);

        BillStatusEnum status = BillStatusEnum.valueOf(bill.getStatus());
        //通过当前状态得到下一状态
        OperateAllowable allowable = new OperateAllowable(status, permission);
        if (!allowable.getAllowNextStep()) {
            throw new ServiceException(OrderBillErrorCode.E700.code(), status.description() + "状态，您没有权限进项下一步操作");
        }
        //是不是我的账单
        if (Permission.SELLER.equals(permission)) {
            Seller seller = UserContext.getSeller();
            if (!seller.getSellerId().equals(bill.getSellerId())) {
                throw new ServiceException(OrderBillErrorCode.E700.code(), "没有权限");
            }
        }
        BillStatusEnum newStatus = map.get(status);
        bill.setStatus(newStatus.name());
        billMapper.updateById(bill);
        return bill;
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Bill add(Bill bill) {
        billMapper.insert(bill);
        bill.setBillId(bill.getBillId());
        return bill;
    }

    @Override
    public Bill getModel(Long id) {
        return billMapper.selectById(id);
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void createBills(Long startTime, Long endTime) {

        // 查询所有的卖家
        List<ShopBankDTO> shops = this.shopClient.listShopBankInfo();


        //获取分销商品返现支出
        List<DistributionSellerBillDTO> dsbs = distributionSellerBillClient.countSeller(startTime.intValue(), endTime.intValue());


        // 结束时间
        String lastTime = String.valueOf(endTime);
        // 统计各卖家的结算结果
        Map<Long, BillResult> billMap = this.billItemManager.countBillResultMap(String.valueOf(startTime), lastTime);

        String sn = "B" + DateUtil.toString(DateUtil.getDateline(), "yyyyMMdd") + new Random().nextInt(5);

        if (shops != null) {
            for (ShopBankDTO shop : shops) {
                Long sellerId = shop.getShopId();
                //佣金比例
                Double commissionRate = shop.getShopCommission() / 100;

                BillResult billRes = billMap.get(sellerId);
                //为空说明上个结算周期没有响应没有订单记录
                if (billRes == null) {
                    billRes = new BillResult(0.00, 0.00, 0.00, 0.00, sellerId,0.00);
                }
                //创建结算单号
                String billSn = this.createBillSn();
                //在线支付的总收入金额
                Double onlinePrice = billRes.getOnlinePrice();
                //在线支付退款的金额
                Double onlineRefundPrice = billRes.getOnlineRefundPrice();
                // 货到付款的总收入金额
                Double codPrice = billRes.getCodPrice();
                // 货到付款的退款金额
                Double codRefundPrice = billRes.getCodRefundPrice();
                //优惠券佣金
                Double couponCommissionPrice = billRes.getSiteCouponCommi();
                //佣金 总收入金额 * 佣金比例
                Double commissionPrice = CurrencyUtil.mul(CurrencyUtil.add(onlinePrice, codPrice), commissionRate);
                //退还佣金 总退款金额 * 佣金比例
                Double refundCommissionPrice = CurrencyUtil.mul(CurrencyUtil.add(onlineRefundPrice, codRefundPrice), commissionRate);

                //分销商品返现
                Double distributionGoodsRebate = 0d;
                //分销商品退单返现返还
                Double distributionReturnRebate = 0d;
                if (StringUtil.isNotEmpty(dsbs)) {
                    for (DistributionSellerBillDTO dsb : dsbs) {
                        if (dsb.getSellerId().equals(sellerId)) {
                            if (dsb.getCountExpenditure() != null) {
                                distributionGoodsRebate = dsb.getCountExpenditure();
                            }
                            if (dsb.getReturnExpenditure() != null) {
                                distributionReturnRebate = dsb.getReturnExpenditure();
                            }
                        }
                    }
                }

                //结算周期内订单总收入金额 = 在线支付的总收入金额 + 货到付款的总收入金额
                Double orderTotal = CurrencyUtil.add(onlinePrice, codPrice);

                //结算周期内订单总退款金额 = 在线支付总退款的金额 + 货到付款的总退款金额
                Double refundTotal = CurrencyUtil.add(onlineRefundPrice, codRefundPrice);

                //结算金额 = 结算周期内在线支付订单总收入金额 - 结算周期内在线支付订单总退款金额 - 平台收取的佣金总额 + 平台退还的佣金金额 + 用户使用的平台优惠券的金额
                Double billPrice = CurrencyUtil.sub(CurrencyUtil.sub(onlinePrice, onlineRefundPrice), commissionPrice);
                billPrice = CurrencyUtil.add(billPrice, CurrencyUtil.sub(refundCommissionPrice, couponCommissionPrice));

                //分销最终收入 = 分销返现佣金支付金额 - 分销返现佣金退还金额
                Double distributionBillPrice = CurrencyUtil.sub(distributionGoodsRebate, distributionReturnRebate);

                //最终结算金额 = 商家结算 - 分销结算
                billPrice = CurrencyUtil.sub(billPrice, distributionBillPrice);

                Bill bill = new Bill();
                bill.setStartTime(startTime);
                bill.setEndTime(endTime);
                bill.setBankAccountName(shop.getBankAccountName());
                bill.setBankAccountNumber(shop.getBankNumber());
                String bankAddress = createBankAddress(shop);
                bill.setBankAddress(bankAddress);
                bill.setBankCode("");
                bill.setBankName(shop.getBankName());
                bill.setBillSn(billSn);
                bill.setBillType(0);
                bill.setCommiPrice(commissionPrice);
                // 优惠金额目前没有用，没有计算
                bill.setDiscountPrice(0.00);
                bill.setCreateTime(DateUtil.getDateline());
                bill.setStatus(BillStatusEnum.OUT.name());
                bill.setSellerId(sellerId);
                bill.setShopName(shop.getShopName());
                // 总收入 = 在线支付总收入
                bill.setPrice(billRes.getOnlinePrice());
                bill.setRefundCommiPrice(refundCommissionPrice);
                //在线退款金额
                bill.setRefundPrice(onlineRefundPrice);
                bill.setBillPrice(billPrice);
                bill.setCodPrice(codPrice);
                bill.setCodRefundPrice(codRefundPrice);
                bill.setSiteCouponCommi(couponCommissionPrice);
                bill.setSn(sn);
                bill.setDistributionRebate(distributionGoodsRebate);
                bill.setDistributionReturnRebate(distributionReturnRebate);
                bill.setOrderTotalPrice(orderTotal);
                bill.setRefundTotalPrice(refundTotal);
                this.add(bill);
                //更新结算项
                this.billItemManager.updateBillItem(sellerId, bill.getBillId(), String.valueOf(startTime), lastTime);
            }
        }


    }

    /**
     * 创建店铺的银行地址
     *
     * @param shop
     * @return
     */
    private String createBankAddress(ShopBankDTO shop) {

        StringBuffer stringBuffer = new StringBuffer();
        //省
        stringBuffer.append(shop.getBankProvince() == null ? "" : shop.getBankProvince());
        //市
        stringBuffer.append(shop.getBankCity() == null ? "" : shop.getBankCity());
        //区
        stringBuffer.append(shop.getBankCounty() == null ? "" : shop.getBankCounty());
        //镇
        stringBuffer.append(shop.getBankTown() == null ? "" : shop.getBankTown());

        return stringBuffer.toString();
    }

    @Override
    public BillExcel exportBill(Long billId) {

        BillExcel billExcel = new BillExcel();

        QueryWrapper<Bill> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("bill_id",billId);
        Bill bill = billMapper.selectOne(wrapper1);

        if (bill == null) {
            throw new ServiceException(OrderBillErrorCode.E700.code(), "没有权限");
        }

        bill.setStatus(BillStatusEnum.valueOf(bill.getStatus()).description());
        billExcel.setBill(bill);

        //订单列表
        QueryWrapper<BillItem> wrapper = new QueryWrapper<>();
        wrapper.eq("bill_id",billId).eq("item_type",BillType.PAYMENT.name());
        List<BillItem> orderList = billItemMapper.selectList(wrapper);
        billExcel.setOrderList(orderList);
        //退单列表
        QueryWrapper<BillItem> wrapper2 = new QueryWrapper<>();
        wrapper2.eq("bill_id",billId).eq("item_type",BillType.REFUND.name());
        List<BillItem> refundList = billItemMapper.selectList(wrapper2);

        billExcel.setRefundList(refundList);

        return billExcel;
    }

    /**
     * 新建一个结算编号
     *
     * @return
     */
    private String createBillSn() {

        // 当天的日期
        String timeStr = DateUtil.toString(DateUtil.getDateline(), "yyyyMMdd");

        //组合出当天的Key
        String redisKey = BILL_SN_CACHE_PREFIX + "_" + timeStr;

        //用当天的时间进行自增
        Long sncount = stringRedisTemplate.opsForValue().increment(redisKey, 1);

        String sn;

        if (sncount < 1000000) {
            sn = "000000" + sncount;
            sn = sn.substring(sn.length() - 6, sn.length());
        } else {
            sn = String.valueOf(sncount);
        }

        sn = "B" + timeStr + sn;

        return sn;
    }
}
