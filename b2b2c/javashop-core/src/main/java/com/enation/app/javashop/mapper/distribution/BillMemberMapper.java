package com.enation.app.javashop.mapper.distribution;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.distribution.dos.BillMemberDO;
import com.enation.app.javashop.model.distribution.vo.BillMemberVO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户结算单的Mapper
 * @author zhanghao
 * @version v1.0
 * @since v7.2.2
 * 2020/8/5
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface BillMemberMapper extends BaseMapper<BillMemberDO> {

    /**
     * 获取结算单
     * @param totalSn 编号
     * @param memberId 会员id
     * @return BillMemberDO
     */
    BillMemberDO getBillByTotalSn(@Param("totalSn") String totalSn, @Param("memberId") Long memberId);

    /**
     * 获取所有下线的分销业绩
     * @param memberId 会员id
     * @param billId 当前页面 业绩单id
     * @return BillMemberDO
     */
    List<BillMemberDO> queryAllDown(@Param("memberId") Long memberId, @Param("billId") Long billId);

    /**
     * 结算单会员分页查询
     * @param page 页码和每页数量
     * @param memberId 会员id
     * @return BillMemberVO
     */
    IPage<BillMemberVO> billMemberPage(Page<BillMemberVO> page, @Param("memberId") Long memberId);


}
