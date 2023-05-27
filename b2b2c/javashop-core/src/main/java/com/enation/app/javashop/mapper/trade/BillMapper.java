package com.enation.app.javashop.mapper.trade;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.orderbill.dos.Bill;
import com.enation.app.javashop.model.orderbill.vo.BillDetail;
import com.enation.app.javashop.model.orderbill.vo.BillQueryParam;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

/**
 * 结算单业务的Mapper
 * @author zhanghao
 * @version v1.0
 * @since v7.2.2
 * 2020/8/8
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface BillMapper extends BaseMapper<Bill> {

    /**
     * 查询账单列表
     * @param page 页码和每页数量
     * @param param 结算单查询条件
     * @return BillDetail
     */
    IPage<BillDetail> queryBillDetail(Page<BillDetail> page, @Param("param") BillQueryParam param);

    /**
     * 查看每个周期的结果统计
     * @param page 页码和每页数量
     * @param sn 结算单号
     * @return BillDetail
     */
    IPage<Bill> queryAllBill(Page<Bill> page, @Param("sn") String sn);

    /**
     * 查看每个周期的结果统计
     * @param billId 结算单号
     * @return BillDetail
     */
    BillDetail queryDetail(@Param("billId") Long billId);

}
