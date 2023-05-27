package com.enation.app.javashop.mapper.promotion.minus;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.promotion.minus.dos.MinusDO;
import com.enation.app.javashop.model.promotion.minus.vo.MinusVO;
import com.enation.app.javashop.model.promotion.seckill.dos.SeckillApplyDO;
import com.enation.app.javashop.model.promotion.seckill.vo.SeckillApplyVO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

/**
 * 单品立减mapper
 * @author zs
 * @version v1.0
 * @since v7.2.2
 * 2020-08-11
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface MinusMapper extends BaseMapper<MinusDO> {

    /**
     * 单品立减分页查询
     * @param page 分页数据
     * @param queryWrapper 查询参数
     * @return
     */
    IPage<MinusVO> selectMinusVoPage(Page page, @Param(Constants.WRAPPER) QueryWrapper<MinusDO> queryWrapper);
}
