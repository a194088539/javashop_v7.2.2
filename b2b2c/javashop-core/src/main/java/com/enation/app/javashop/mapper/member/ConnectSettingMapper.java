package com.enation.app.javashop.mapper.member;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.member.dos.ConnectSettingDO;
import com.enation.app.javashop.model.member.vo.ConnectSettingVO;
import org.apache.ibatis.annotations.CacheNamespace;

import java.util.List;

/**
 * 信任登录后台参数Mapper接口
 * @author duanmingyu
 * @version v1.0
 * @since v7.2.2
 * 2020-07-23
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface ConnectSettingMapper extends BaseMapper<ConnectSettingDO> {

    /**
     * 获取后台信任登录参数集合
     * @return
     */
    List<ConnectSettingVO> selectConnectSettingVo();

}
