package com.enation.app.javashop.mapper.trade.order;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.member.vo.SalesVO;
import com.enation.app.javashop.model.trade.order.dos.TransactionRecord;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

/**
 * 交易记录mapper
 * @author zs
 * @version v1.0
 * @since v7.2.2
 * 2020-08-08
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface TransactionRecordMapper extends BaseMapper<TransactionRecord> {

    /**
     * 商品销售记录分页查询
     * @param page 分页数据
     * @param queryWrapper 查询条件包装器
     * @return
     */
    IPage<SalesVO> selectSalesVoPage(Page page, @Param(Constants.WRAPPER) Wrapper<TransactionRecord> queryWrapper);
}
