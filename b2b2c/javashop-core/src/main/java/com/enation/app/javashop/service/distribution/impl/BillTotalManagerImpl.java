package com.enation.app.javashop.service.distribution.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.distribution.BillTotalMapper;
import com.enation.app.javashop.model.distribution.dos.BillTotalDO;
import com.enation.app.javashop.service.distribution.BillTotalManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 总结算单处理
 * @author Chopper
 * @version v1.0
 * @since v7.0
 * 2018/6/14 上午7:13
 * @Description:
 *
 */

@Service
public class BillTotalManagerImpl implements BillTotalManager {

    @Autowired
    private BillTotalMapper billTotalMapper;

    @Override
    public WebPage page(long page, long pageSize) {
        QueryWrapper<BillTotalDO> wrapper = new QueryWrapper<>();
        IPage<BillTotalDO> iPage = billTotalMapper.selectPage(new Page<>(page,pageSize), wrapper);
        return PageConvert.convert(iPage);
    }

    @Override
    public BillTotalDO add(BillTotalDO billTotal) {
        billTotalMapper.insert(billTotal);
        return billTotal;
    }

    @Override
    public BillTotalDO getTotalByStart(Long startTime){
        QueryWrapper<BillTotalDO> wrapper = new QueryWrapper<>();
        wrapper.eq("start_time",startTime);
        return billTotalMapper.selectOne(wrapper);
    }

}
