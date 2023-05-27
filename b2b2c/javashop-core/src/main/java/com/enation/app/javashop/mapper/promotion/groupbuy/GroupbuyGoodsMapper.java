package com.enation.app.javashop.mapper.promotion.groupbuy;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.promotion.groupbuy.dos.GroupbuyGoodsDO;
import com.enation.app.javashop.model.promotion.groupbuy.vo.GroupbuyGoodsVO;
import com.enation.app.javashop.model.promotion.groupbuy.vo.GroupbuyQueryParam;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

/**
 * 团购商品Mapper接口
 * @author duanmingyu
 * @version v1.0
 * @since v7.2.2
 * 2020-08-10
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface GroupbuyGoodsMapper extends BaseMapper<GroupbuyGoodsDO> {

    /**
     * 商家端获取团购商品分页列表数据
     * @param page 分页信息
     * @param nowTime 当前时间
     * @param deleteStatus 活动删除状态 NORMAL：未删除，DELETE：已删除
     * @param param 其它查询参数
     * @return
     */
    IPage<GroupbuyGoodsVO> sellerSelectPageVo(Page page, @Param("now_time") Long nowTime, @Param("delete_status") String deleteStatus, @Param("params") GroupbuyQueryParam param);

    /**
     * 买家端获取团购商品分页列表数据
     * @param page 分页信息
     * @param auditStatus 活动商品审核状态 0：待审核，1：审核通过，2：审核未通过
     * @param deleteStatus 活动删除状态 NORMAL：未删除，DELETE：已删除
     * @param param 其它查询参数
     * @return
     */
    IPage<GroupbuyGoodsVO> buyerSelectPageVo(Page page, @Param("audit_status") Integer auditStatus, @Param("delete_status") String deleteStatus, @Param("params") GroupbuyQueryParam param);

    /**
     * 根据团购商品ID获取团购商品信息
     * @param gbId 团购商品ID
     * @return
     */
    GroupbuyGoodsVO selectGroupbuyGoodsVo(@Param("gb_id") Long gbId);
}
