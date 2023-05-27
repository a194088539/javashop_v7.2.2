package com.enation.app.javashop.mapper.member;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.member.dos.MemberShopScore;
import com.enation.app.javashop.model.member.dto.MemberShopScoreDTO;
import org.apache.ibatis.annotations.CacheNamespace;

import java.util.List;

/**
 * 店铺评分实体Mapper接口
 * @author duanmingyu
 * @version v1.0
 * @since v7.2.2
 * 2020-07-23
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface MemberShopScoreMapper extends BaseMapper<MemberShopScore> {

    /**
     * 查询每个店铺的评分集合
     * @return
     */
    List<MemberShopScoreDTO> selectScoreDto();

}
