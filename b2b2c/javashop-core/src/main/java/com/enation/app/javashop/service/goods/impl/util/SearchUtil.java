package com.enation.app.javashop.service.goods.impl.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.enation.app.javashop.client.member.ShopCatClient;
import com.enation.app.javashop.mapper.goods.CategoryMapper;
import com.enation.app.javashop.mapper.goods.GoodsMapper;
import com.enation.app.javashop.model.errorcode.GoodsErrorCode;
import com.enation.app.javashop.model.goods.dos.CategoryDO;
import com.enation.app.javashop.model.goods.dos.GoodsDO;
import com.enation.app.javashop.model.goods.dto.GoodsQueryParam;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.util.StringUtil;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * SearchUtil
 *
 * @author Chopper
 * @version v1.0
 * @since v7.0
 * 2019-01-25 下午4:17
 */
public class SearchUtil {


    /**
     * 店铺分类
     *
     * @param goodsQueryParam
     * @param queryWrapper
     * @param shopCatClient
     */
    public static void shopCatQuery(GoodsQueryParam goodsQueryParam, QueryWrapper<GoodsDO> queryWrapper, ShopCatClient shopCatClient) {
        if (goodsQueryParam.getShopCatPath() != null) {
            List<Map> catList = shopCatClient.getChildren(goodsQueryParam.getShopCatPath());

            if (!StringUtil.isNotEmpty(catList)) {
                throw new ServiceException(GoodsErrorCode.E301.code(), "店铺分组不存在");
            }

            List<Long> list = catList.stream().map(c -> Long.parseLong(c.get("shop_cat_id").toString())).collect(Collectors.toList());

            queryWrapper.in("shop_cat_id", list);
        }
    }

    /**
     * 分类查询
     *
     * @param goodsQueryParam
     * @param queryWrapper
     * @param categoryMapper
     */
    public static void categoryQuery(GoodsQueryParam goodsQueryParam, QueryWrapper<GoodsDO> queryWrapper, CategoryMapper categoryMapper) {
        // 商城分类，同时需要查询出子分类的商品
        if (StringUtil.isEmpty(goodsQueryParam.getCategoryPath())) {
            return;
        }

        List<CategoryDO> list = categoryMapper.selectList(new QueryWrapper<CategoryDO>()
                .likeRight("category_path", goodsQueryParam.getCategoryPath()));

        if (!StringUtil.isNotEmpty(list)) {
            throw new ServiceException(GoodsErrorCode.E301.code(), "分类不存在");
        }

        List<Long> term = list.stream().map(c -> c.getCategoryId()).collect(Collectors.toList());

        queryWrapper.in("category_id", term);
    }

    /**
     * 基础查询
     *
     * @param goodsQueryParam
     * @param queryWrapper
     * @param goodsTableName 商品表别名，因为此方法会公用，连表查询需要别名，单表传""，连表传"{别名}."
     */
    public static void baseQuery(GoodsQueryParam goodsQueryParam, QueryWrapper<GoodsDO> queryWrapper,String goodsTableName) {
        if (goodsQueryParam.getDisabled() == null) {
            goodsQueryParam.setDisabled(1);
        }
        queryWrapper.eq(goodsTableName+"disabled", goodsQueryParam.getDisabled());

        // 如果状态是下架，则查询下架商品
        queryWrapper.eq(goodsQueryParam.getMarketEnable() != null && goodsQueryParam.getMarketEnable() == 0, goodsTableName+"market_enable", 0);

        // 如果状态为上架，则查询商品是上架状态并且是审核通过
        queryWrapper.and(goodsQueryParam.getMarketEnable() != null && goodsQueryParam.getMarketEnable() != 0, e -> {
            e.eq(goodsTableName+"market_enable", 1).eq(goodsTableName+"is_auth", 1);
        });

        // 如果模糊关键字不为空，则模糊匹配商品名称和商品编号
        queryWrapper.and(!StringUtil.isEmpty(goodsQueryParam.getKeyword()), e -> {
            e.like(goodsTableName+"goods_name", goodsQueryParam.getKeyword())
                    .or().like(goodsTableName+"sn", goodsQueryParam.getKeyword());
        });
        // 如果名称不为空，则模糊匹配商品名称
        queryWrapper.like(!StringUtil.isEmpty(goodsQueryParam.getGoodsName()), goodsTableName+"goods_name", goodsQueryParam.getGoodsName());

        // 如果店铺名称不为空，则匹配店铺名称
        queryWrapper.like(!StringUtil.isEmpty(goodsQueryParam.getSellerName()), goodsTableName+"seller_name", goodsQueryParam.getSellerName());

        // 如果商品编号不为空，则匹配商品编号
        queryWrapper.like(!StringUtil.isEmpty(goodsQueryParam.getGoodsSn()), goodsTableName+"sn", goodsQueryParam.getGoodsSn());

        // 如果店铺id不为空
        queryWrapper.eq(goodsQueryParam.getSellerId() != null && goodsQueryParam.getSellerId().intValue() != 0,goodsTableName+"seller_id", goodsQueryParam.getSellerId());

        // 如果商品类型不为空
        queryWrapper.eq(!StringUtil.isEmpty(goodsQueryParam.getGoodsType()),goodsTableName+"goods_type", goodsQueryParam.getGoodsType());

        // 如果审核状态不为空
        queryWrapper.eq(goodsQueryParam.getIsAuth() != null,goodsTableName+"is_auth", goodsQueryParam.getIsAuth());

        // 如果商品品牌不为空
        queryWrapper.eq(goodsQueryParam.getBrandId() != null && goodsQueryParam.getBrandId().intValue() != 0,"brand_id", goodsQueryParam.getBrandId());

        // 如果商品最低价格不为空
        queryWrapper.ge(goodsQueryParam.getStart_price() != null,goodsTableName+"price", goodsQueryParam.getStart_price());

        // 如果商品最高价格不为空
        queryWrapper.le(goodsQueryParam.getEnd_price() != null,goodsTableName+"price", goodsQueryParam.getEnd_price());

    }

}
