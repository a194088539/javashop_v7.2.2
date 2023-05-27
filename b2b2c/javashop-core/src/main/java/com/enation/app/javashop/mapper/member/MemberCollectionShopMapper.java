package com.enation.app.javashop.mapper.member;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.member.dos.MemberCollectionShop;
import com.enation.app.javashop.model.member.vo.MemberCollectionShopVO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

/**
 * 会员店铺收藏Mapper接口
 * @author duanmingyu
 * @version v1.0
 * @since v7.2.2
 * 2020-07-23
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface MemberCollectionShopMapper extends BaseMapper<MemberCollectionShop> {

    /**
     * 获取会员收藏的店铺分页数据
     * @param page 分页信息
     * @param wrapper 查询条件构造器
     * @return
     */
    IPage<MemberCollectionShopVO> selectShopVo(Page page, @Param("ew") QueryWrapper<MemberCollectionShopVO> wrapper);
}
