package com.enation.app.javashop.mapper.trade;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.orderbill.dos.BillItem;
import org.apache.ibatis.annotations.CacheNamespace;

/**
 * 结算单项表业务的Mapper
 * @author zhanghao
 * @version v1.0
 * @since v7.2.2
 * 2020/8/8
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface BillItemMapper extends BaseMapper<BillItem> {

    /**
     * 更新结算项的状态
     * @param sellerId 卖家id
     * @param billId 结算单id
     * @param startTime 开始时间
     * @param lastTime 结束时间
     * @return
     */
    void updateBillItem(Long sellerId, Long billId, String startTime, String lastTime);

}
