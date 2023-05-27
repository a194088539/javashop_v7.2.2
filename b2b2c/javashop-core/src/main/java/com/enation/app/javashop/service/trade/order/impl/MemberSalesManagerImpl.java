package com.enation.app.javashop.service.trade.order.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.trade.order.TransactionRecordMapper;
import com.enation.app.javashop.model.member.vo.SalesVO;
import com.enation.app.javashop.model.trade.order.dos.TransactionRecord;
import com.enation.app.javashop.service.trade.order.MemberSalesManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * MemberSalesMangerImpl
 *
 * @author chopper
 * @version v1.0
 * @since v7.0
 * 2018-06-29 上午9:41
 */
@Service
public class MemberSalesManagerImpl implements MemberSalesManager {

    @Autowired
    private TransactionRecordMapper transactionRecordMapper;

    /**
     * 商品销售记录
     *
     * @param pageSize
     * @param pageNo
     * @param goodsId
     * @return
     */
    @Override
    public WebPage<SalesVO> list(Long pageSize, Long pageNo, Long goodsId) {

        //根据商品id分页查询销售记录
        QueryWrapper<TransactionRecord> queryWrapper = new QueryWrapper<TransactionRecord>()
                //查询卖家姓名，价格，商品数量，创建时间
                .select("uname as buyerName", "price", "goods_num as num", "create_time")
                //按商品id查询
                .eq("goods_id", goodsId)
                //按创建时间倒序
                .orderByDesc("create_time");

        IPage<SalesVO> iPage = transactionRecordMapper.selectSalesVoPage(new Page(pageNo,pageSize), queryWrapper);

        return PageConvert.convert(iPage);
    }
}
