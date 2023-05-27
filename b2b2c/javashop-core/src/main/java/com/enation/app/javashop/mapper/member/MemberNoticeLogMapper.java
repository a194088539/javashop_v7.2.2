package com.enation.app.javashop.mapper.member;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.member.dos.MemberNoticeLog;
import org.apache.ibatis.annotations.CacheNamespace;

/**
 * 会员站内消息历史Mapper接口
 * @author duanmingyu
 * @version v1.0
 * @since v7.2.2
 * 2020-07-24
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface MemberNoticeLogMapper extends BaseMapper<MemberNoticeLog> {
}
