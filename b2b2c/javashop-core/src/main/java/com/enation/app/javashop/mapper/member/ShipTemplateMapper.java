package com.enation.app.javashop.mapper.member;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.shop.dos.ShipTemplateDO;
import com.enation.app.javashop.model.shop.vo.ShipTemplateSellerVO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 运费模版Mapper接口
 * @author duanmingyu
 * @version v1.0
 * @since v7.2.2
 * 2020-07-28
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface ShipTemplateMapper extends BaseMapper<ShipTemplateDO> {

    /**
     * 根据店铺id获取店铺运费模板集合
     * @param sellerId 店铺ID
     * @return
     */
    List<ShipTemplateSellerVO> selectShipTemplateList(@Param("seller_id") Long sellerId);

}
