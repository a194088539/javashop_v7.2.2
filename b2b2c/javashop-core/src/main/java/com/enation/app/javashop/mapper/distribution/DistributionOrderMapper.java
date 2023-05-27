package com.enation.app.javashop.mapper.distribution;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.distribution.dos.DistributionOrderDO;
import com.enation.app.javashop.model.distribution.vo.SellerPushVO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 分销订单的Mapper
 * @author zhanghao
 * @version v1.0
 * @since v7.2.2
 * 2020/8/5
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface DistributionOrderMapper extends BaseMapper<DistributionOrderDO> {

    /**
     * 获取分销退款单
     * @param page 页码和每页数量
     * @param totalId id
     * @param memberId  会员id
     * @return DistributionOrderDO
     */
    IPage<DistributionOrderDO> queryIdForPage(Page<DistributionOrderDO> page, @Param("totalId") Long totalId, @Param("memberId") Long memberId);

    /**
     * 结算单订单查询
     * @param page 页码和每页数量
     * @param memberId  会员id
     * @param billId  分销商id
     * @return DistributionOrderDO
     */
    IPage<DistributionOrderDO> selectPageDo(Page<DistributionOrderDO> page, @Param("memberId") Long memberId, @Param("billId") Long billId);

    /**
     * 分销商退货订单分页
     * @param page 页码和每页数量
     * @param memberId  会员id
     * @param billId  分销商id
     * @return DistributionOrderDO
     */
    IPage<DistributionOrderDO> selectPageDoo(Page<DistributionOrderDO> page, @Param("memberId") Long memberId, @Param("billId") Long billId);

    /**
     * 根据会员id获取营业额
     * @param memberId  会员id
     * @return Map
     */
    Map queryForMap(@Param("memberId") Long memberId);

    /**
     * 营业额
     * @param circleWhere 周期
     * @param timesTrampZ 开始时间
     * @param timesTrampO 结束时间
     * @param memberId  会员id
     * @return List
     */
    List<Map<String, Object>> querySumOrderPrice(@Param("circleWhere") String circleWhere, @Param("timesTrampZ") Long timesTrampZ, @Param("timesTrampO") Long timesTrampO, @Param("memberId") Long memberId);

    /**
     * 营业额
     * @param circleWhere 周期
     * @param timesTrampZ 开始时间
     * @param timesTrampO 结束时间
     * @param memberId  会员id
     * @return List
     */
    List<Map<String, Object>> querySumGrade1Rebate(@Param("circleWhere") String circleWhere, @Param("timesTrampZ") Long timesTrampZ, @Param("timesTrampO") Long timesTrampO, @Param("memberId") Long memberId);

    /**
     * 营业额
     * @param circleWhere 周期
     * @param timesTrampZ 开始时间
     * @param timesTrampO 结束时间
     * @param memberId  会员id
     * @return List
     */
    List<Map<String, Object>> querySumGrade2Rebate(@Param("circleWhere") String circleWhere, @Param("timesTrampZ") Long timesTrampZ, @Param("timesTrampO") Long timesTrampO, @Param("memberId") Long memberId);

    /**
     * 营业额
     * @param circleWhere 周期
     * @param timesTrampZ 开始时间
     * @param timesTrampO 结束时间
     * @param memberId  会员id
     * @return List
     */
    List<Map<String, Object>> queryCount(@Param("circleWhere") String circleWhere, @Param("timesTrampZ") Long timesTrampZ, @Param("timesTrampO") Long timesTrampO, @Param("memberId") Long memberId);

    /**
     * 店铺统计
     * @param page 页码和每页数量
     * @param timesTrampZ 开始时间
     * @param timesTrampO 结束时间
     * @return SellerPushVO
     */
    IPage<SellerPushVO> queryGradeRebateForPage(Page<SellerPushVO> page, @Param("timesTrampZ") Long timesTrampZ, @Param("timesTrampO") Long timesTrampO);

}
