package com.enation.app.javashop.mapper.distribution;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.distribution.dos.WithdrawApplyDO;
import com.enation.app.javashop.model.distribution.vo.WithdrawApplyVO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

/**
 * 提现设置的Mapper
 *
 * @author zhanghao
 * @version v1.0
 * @since v7.2.2
 * 2020/8/5
 */
@CacheNamespace(implementation = MybatisRedisCache.class, eviction = MybatisRedisCache.class)
public interface WithdrawApplyMapper extends BaseMapper<WithdrawApplyDO> {

    /**
     * 根据member_id查询提现记录
     *
     * @param page     页码和每页数量
     * @param memberId 会员id
     * @return WithdrawApplyVO
     */
    IPage<WithdrawApplyDO> pageWithdrawApply(Page<WithdrawApplyVO> page, @Param("memberId") Long memberId);
}
