package com.enation.app.javashop.mapper.promotion.exchange;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.promotion.exchange.dos.ExchangeDO;
import com.enation.app.javashop.model.promotion.exchange.dto.ExchangeQueryParam;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

/**
 * 积分兑换商品Mapper接口
 * @author duanmingyu
 * @version v1.0
 * @since v7.2.2
 * 2020-08-10
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface ExchangeMapper extends BaseMapper<ExchangeDO> {

    /**
     * 获取积分兑换分页列表数据
     * @param page 分页信息
     * @param promotionType 活动类型
     * @param param 查询参数信息
     * @return
     */
    IPage<ExchangeDO> selectExchangePage(Page page, @Param("promotion_type") String promotionType, @Param("param") ExchangeQueryParam param);

}
