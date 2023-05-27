package com.enation.app.javashop.service.orderbill.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.framework.util.StringUtil;
import com.enation.app.javashop.mapper.trade.BillItemMapper;
import com.enation.app.javashop.model.orderbill.dos.BillItem;
import com.enation.app.javashop.model.orderbill.vo.BillResult;
import com.enation.app.javashop.service.orderbill.BillItemManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 结算单项表业务类
 *
 * @author fk
 * @version v1.0
 * @since v7.0.0
 * 2018-04-26 15:39:57
 */
@Service
public class BillItemManagerImpl implements BillItemManager {

    @Autowired
    private BillItemMapper billItemMapper;

    @Override
    public WebPage list(long page, long pageSize, Long billId, String billType) {
        QueryWrapper<BillItem> wrapper = new QueryWrapper<>();
        wrapper.eq("bill_id",billId).eq("item_type",billType);
        IPage<BillItem> iPage = billItemMapper.selectPage(new Page<>(page,pageSize), wrapper);
        return PageConvert.convert(iPage);
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public BillItem add(BillItem billItem) {
        billItemMapper.insert(billItem);
        return billItem;
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public BillItem edit(BillItem billItem, Long id) {
        billItemMapper.updateById(billItem);
        return billItem;
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(Long id) {
        billItemMapper.deleteById(id);
    }

    @Override
    public BillItem getModel(Long id) {
        return billItemMapper.selectById(id);
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateBillItem(Long sellerId, Long billId, String startTime, String lastTime) {
        Assert.notNull(sellerId, "卖家id为null");
        Assert.notNull(billId, "结算单id为null");
        billItemMapper.updateBillItem(billId, sellerId, startTime, lastTime);
    }

    @Override
    public Map<Long, BillResult> countBillResultMap(String startTime, String lastTime) {
//        String sql = "select " +
//                " sum(case when payment_type = 'online' and item_type = 'PAYMENT'  then price else 0 end ) online_price ," +
//                " sum(case when payment_type = 'online' and item_type = 'REFUND' then price else 0 end ) online_refund_price ," +
//                " sum(case when payment_type = 'cod' and item_type = 'PAYMENT' then price else 0 end ) cod_price," +
//                " sum(case when payment_type = 'cod' and item_type = 'REFUND' then price else 0 end ) cod_refund_price,seller_id," +
//                " sum(case when item_type = 'PAYMENT' then site_coupon_price*coupon_commission else 0 end ) as site_coupon_commi " +
//                " from es_bill_item where `status` = 0 and (add_time < ? and add_time >= ?) and bill_id is null group by seller_id ";
//        List<BillResult> billList = this.daoSupport.queryForList(sql, BillResult.class, lastTime,startTime);


        QueryWrapper<BillItem> wrapper = new QueryWrapper<>();
        wrapper.eq("status",0).isNull("bill_id").groupBy("seller_id");
        wrapper.select("sum(price)");
        wrapper.eq("payment_type","online").eq("item_type","PAYMENT");
        //在线支付的总金额
        Double onlinePrice = billItemMapper.selectOne(wrapper).getPrice();
        wrapper.select("sum(price)");
        wrapper.eq("payment_type","online").eq("item_type","REFUND");
        //在线支付后退款的总金额
        Double onlineRefundPrice = billItemMapper.selectOne(wrapper).getPrice();
        wrapper.select("sum(price)");
        wrapper.eq("payment_type","cod").eq("item_type","PAYMENT");
        //货到付款的总金额
        Double codPrice = billItemMapper.selectOne(wrapper).getPrice();
        wrapper.select("sum(price)");
        wrapper.eq("payment_type","cod").eq("item_type","REFUND");
        //货到付款后退款的总金额
        Double codRefundPrice = billItemMapper.selectOne(wrapper).getPrice();
        wrapper.select("sum(site_coupon_price*coupon_commission)");
        wrapper.eq("item_type","PAYMENT");
        //站点优惠券佣金
        Double siteCouponCommi = billItemMapper.selectOne(wrapper).getSiteCouponPrice()*billItemMapper.selectOne(wrapper).getCouponCommission();

        List<BillResult> billList = null;
        for (BillResult listOne : billList ){
            listOne.setOnlinePrice(onlinePrice);
            listOne.setOnlineRefundPrice(onlineRefundPrice);
            listOne.setCodPrice(codPrice);
            listOne.setCodRefundPrice(codRefundPrice);
            listOne.setSiteCouponCommi(siteCouponCommi);
        }

        Map<Long, BillResult> billMap = new HashMap<>(billList.size());
        if (StringUtil.isNotEmpty(billList)) {
            for (BillResult bill : billList) {
                billMap.put(bill.getSellerId(), bill);
            }
        }

        return billMap;
    }
}
