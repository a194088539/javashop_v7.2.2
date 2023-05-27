package com.enation.app.javashop.mapper.member;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.shop.dos.ShopDO;
import com.enation.app.javashop.model.shop.dto.ShopBankDTO;
import com.enation.app.javashop.model.shop.dto.ShopBasicInfoDTO;
import com.enation.app.javashop.model.shop.vo.ShopListVO;
import com.enation.app.javashop.model.shop.vo.ShopParamsVO;
import com.enation.app.javashop.model.shop.vo.ShopVO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 店铺Mapper接口
 * @author duanmingyu
 * @version v1.0
 * @since v7.2.2
 * 2020-08-05
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface ShopMapper extends BaseMapper<ShopDO> {

    /**
     * 根据店铺ID获取店铺信息
     * @param shopId 店铺ID
     * @return
     */
    ShopVO selectByShopId(@Param("shop_id") Long shopId);

    /**
     * 根据会员ID获取店铺信息
     * @param memberId 会员ID
     * @return
     */
    ShopVO selectByMemberId(@Param("member_id") Long memberId);

    /**
     * 获取店铺分页数据
     * @param page 分页信息
     * @param shopParams 查询参数
     * @return
     */
    IPage<Map> selectShopPage(Page page, @Param("params") ShopParamsVO shopParams);

    /**
     * 根据店铺状态获取店铺信息集合
     * @param shopStatus 店铺状态 OPEN：开启中，CLOSED：店铺关闭，APPLY：申请开店，REFUSED：审核拒绝，APPLYING：申请中
     * @return
     */
    List<ShopVO> selectShopList(@Param("status") String shopStatus);

    /**
     * 根据店铺状态获取相关店铺的银行以及佣金比例等相关信息集合
     * @param statusList 店铺状态集合
     * @return
     */
    List<ShopBankDTO> selectShopBank(@Param("statusList") List<String> statusList);

    /**
     * 根据店铺ID获取店铺基础信息
     * @param shopId 店铺ID
     * @return
     */
    ShopBasicInfoDTO selectShopBasic(@Param("shop_id") Long shopId);

    /**
     * 获取店铺分页列表信息
     * @param page 分页信息
     * @param shopParams 查询条件参数
     * @return
     */
    IPage<ShopListVO> selectShopListPage(Page page, @Param("params") ShopParamsVO shopParams);

}
