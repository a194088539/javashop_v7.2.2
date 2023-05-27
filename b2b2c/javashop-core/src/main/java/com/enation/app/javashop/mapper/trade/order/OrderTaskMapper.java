package com.enation.app.javashop.mapper.trade.order;

import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.trade.order.vo.OrderDetailVO;
import org.apache.ibatis.annotations.CacheNamespace;

import java.util.List;
import java.util.Map;

/**
 * 订单查询业务mapper
 * @author zs
 * @version v1.0
 * @since v7.2.2
 * 2020-08-08
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface OrderTaskMapper {

    /**
     * 获取要取消的订单编号集合
     * @param time 当前时间减自动完成设置的时间
     * @return
     */
    List<Map> selectCompleteTaskList(long time);

    /**
     * 获取评论自动变更为好评的订单。
     * @param time 当前时间减去评价超时天数
     * @return
     */
    List<OrderDetailVO> selectCommentTaskList(long time);
}
