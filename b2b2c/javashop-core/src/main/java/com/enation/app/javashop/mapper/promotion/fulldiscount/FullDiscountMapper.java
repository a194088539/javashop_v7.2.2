package com.enation.app.javashop.mapper.promotion.fulldiscount;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.promotion.fulldiscount.dos.FullDiscountDO;
import com.enation.app.javashop.model.promotion.fulldiscount.vo.FullDiscountVO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

/**
 * 满减满赠促销活动Mapper接口
 * @author duanmingyu
 * @version v1.0
 * @since v7.2.2
 * 2020-08-10
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface FullDiscountMapper extends BaseMapper<FullDiscountDO> {

    /**
     * 获取满减满赠促销活动分页列表数据VO集合
     * @param page 分页信息
     * @param wrapper 查询条件包装器
     * @return
     */
    IPage<FullDiscountVO> selectFullDiscountVoPage(Page page, @Param("ew") QueryWrapper<FullDiscountDO> wrapper);

}
