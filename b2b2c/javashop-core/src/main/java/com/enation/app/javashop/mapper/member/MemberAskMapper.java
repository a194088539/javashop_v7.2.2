package com.enation.app.javashop.mapper.member;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.member.dos.MemberAsk;
import com.enation.app.javashop.model.member.vo.MemberAskVO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

/**
 * 会员问题咨询Mapper接口
 * @author duanmingyu
 * @version v1.0
 * @since v7.2.2
 * 2020-07-20
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface MemberAskMapper extends BaseMapper<MemberAsk> {

    /**
     * 获取与会员商品咨询相关的其它咨询
     * @param page 分页数据
     * @param wrapper wrapper条件
     * @return
     */
    IPage<MemberAskVO> listGoodsAsks(Page page, @Param("ew") QueryWrapper<MemberAskVO> wrapper);

    /**
     * 修改会员商品咨询回复数量
     * @param num 数量
     * @param askId 会员商品咨询ID
     */
    void updateReplyNum(@Param("num") Integer num, @Param("ask_id") Long askId);
}
