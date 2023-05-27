package com.enation.app.javashop.service.goods.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.client.member.ShipTemplateClient;
import com.enation.app.javashop.client.member.ShopCatClient;
import com.enation.app.javashop.client.member.ShopClient;
import com.enation.app.javashop.client.promotion.ExchangeGoodsClient;
import com.enation.app.javashop.client.promotion.PromotionGoodsClient;
import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import com.enation.app.javashop.framework.security.model.Seller;
import com.enation.app.javashop.framework.util.JsonUtil;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.framework.util.StringUtil;
import com.enation.app.javashop.mapper.goods.CategoryMapper;
import com.enation.app.javashop.mapper.goods.GoodsMapper;
import com.enation.app.javashop.mapper.goods.GoodsParamsMapper;
import com.enation.app.javashop.mapper.goods.GoodsSkuMapper;
import com.enation.app.javashop.model.base.CachePrefix;
import com.enation.app.javashop.model.errorcode.GoodsErrorCode;
import com.enation.app.javashop.model.goods.dos.GoodsDO;
import com.enation.app.javashop.model.goods.dos.GoodsGalleryDO;
import com.enation.app.javashop.model.goods.dos.GoodsSkuDO;
import com.enation.app.javashop.model.goods.dto.GoodsQueryParam;
import com.enation.app.javashop.model.goods.enums.GoodsType;
import com.enation.app.javashop.model.goods.vo.*;
import com.enation.app.javashop.model.promotion.exchange.dos.ExchangeDO;
import com.enation.app.javashop.model.promotion.tool.enums.PromotionTypeEnum;
import com.enation.app.javashop.model.promotion.tool.vo.PromotionVO;
import com.enation.app.javashop.model.shop.vo.ShipTemplateChildBuyerVO;
import com.enation.app.javashop.model.shop.vo.ShipTemplateVO;
import com.enation.app.javashop.model.shop.vo.ShopVO;
import com.enation.app.javashop.service.goods.*;
import com.enation.app.javashop.service.goods.impl.util.SearchUtil;
import com.enation.app.javashop.service.goods.impl.util.StockCacheKeyUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品业务类
 *
 * @author fk
 * @version v2.0
 * @since v7.0.0 2018-03-21 11:23:10
 */
@Service
public class GoodsQueryManagerImpl implements GoodsQueryManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private GoodsGalleryManager goodsGalleryManager;
    @Autowired
    private GoodsSkuManager goodsSkuManager;
    @Autowired
    private ShopClient shopClient;
    @Autowired
    private Cache cache;
    @Autowired
    private ShopCatClient shopCatClient;
    @Autowired
    private CategoryManager categoryManager;
    @Autowired
    private ExchangeGoodsClient exchangeGoodsClient;
    @Autowired
    private ShipTemplateClient shipTemplateClient;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private GoodsQuantityManager goodsQuantityManager;

    @Autowired
    private PromotionGoodsClient promotionGoodsClient;

    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private GoodsSkuMapper goodsSkuMapper;
    @Autowired
    private GoodsParamsMapper goodsParamsMapper;

    @Override
    public WebPage list(GoodsQueryParam goodsQueryParam) {

        QueryWrapper<GoodsDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("goods_id", "goods_name", "sn", "brand_id", "thumbnail", "seller_name",
                "enable_quantity", "quantity", "price", "create_time", "market_enable", "is_auth", "under_message", "priority");

        //基础查询
        SearchUtil.baseQuery(goodsQueryParam, queryWrapper, "");
        //分类查询
        SearchUtil.categoryQuery(goodsQueryParam, queryWrapper, categoryMapper);
        // 店铺分组
        SearchUtil.shopCatQuery(goodsQueryParam, queryWrapper, shopCatClient);

        queryWrapper.orderByDesc("priority", "create_time");

        IPage<Map<String, Object>> page =
                goodsMapper.selectMapsPage(new Page<>(goodsQueryParam.getPageNo(), goodsQueryParam.getPageSize()), queryWrapper);

        return PageConvert.convert(page);
    }


    @Override
    public WebPage goodsPriorityList(GoodsQueryParam goodsQueryParam) {

        QueryWrapper<GoodsDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("goods_id", "goods_name", "thumbnail", "market_enable", "is_auth", "priority");

        //基础查询
        SearchUtil.baseQuery(goodsQueryParam, queryWrapper, "");
        //分类查询
        SearchUtil.categoryQuery(goodsQueryParam, queryWrapper, categoryMapper);
        // 店铺分组
        SearchUtil.shopCatQuery(goodsQueryParam, queryWrapper, shopCatClient);

        queryWrapper.orderByDesc("priority");

        IPage<Map<String, Object>> page =
                goodsMapper.selectMapsPage(new Page<>(goodsQueryParam.getPageNo(), goodsQueryParam.getPageSize()), queryWrapper);

        page.getRecords().forEach(priorityVO -> {
            switch (priorityVO.get("priority").toString()) {
                case "3":
                    priorityVO.put("priority_text", "高");
                    break;
                case "2":
                    priorityVO.put("priority_text", "中");
                    break;
                case "1":
                    priorityVO.put("priority_text", "低");
                    break;
                default:
                    priorityVO.put("priority_text", "低");
                    break;
            }
        });

        return PageConvert.convert(page);
    }

    @Override
    public GoodsDO getModel(Long goodsId) {

        return this.goodsMapper.selectById(goodsId);
    }

    @Override
    public GoodsVO sellerQueryGoods(Long goodsId) {

        Seller seller = UserContext.getSeller();

        GoodsDO goods = this.getModel(goodsId);

        if (goods == null || !goods.getSellerId().equals(seller.getSellerId())) {
            throw new ServiceException(GoodsErrorCode.E301.code(), "没有操作权限");
        }
        List<GoodsGalleryDO> galleryList = goodsGalleryManager.list(goodsId);
        GoodsVO goodsVO = new GoodsVO();

        BeanUtils.copyProperties(goods, goodsVO);

        goodsVO.setGoodsGalleryList(galleryList);

        //转换商品移动端详情数据
        if (StringUtil.notEmpty(goods.getMobileIntro())) {
            goodsVO.setIntroList(JsonUtil.jsonToList(goods.getMobileIntro(), GoodsMobileIntroVO.class));
        }

        //商品分类赋值
        Long categoryId = goods.getCategoryId();
        //CategoryDO category = categoryManager.getModel(categoryId);

        String categoryName = categoryManager.queryCatName(categoryId);

        //goodsVO.setCategoryIds(category.getCategoryPath().replaceAll("|",",").substring(0,category.getCategoryPath().length()-1));
        goodsVO.setCategoryName(categoryName);

        //查询积分商品信息
        if (goodsVO.getGoodsType().equals(GoodsType.POINT.name())) {
            ExchangeDO exchangeDO = exchangeGoodsClient.getModelByGoods(goodsId);

            goodsVO.setExchange(exchangeDO);
        }

        //设置商品参与的促销活动提示
        String promotionTips = this.convertTips(goodsId);
        goodsVO.setPromotionTip(promotionTips);


        return goodsVO;
    }


    @Override
    public CacheGoods getFromCache(Long goodsId) {
        CacheGoods goods = (CacheGoods) cache.get(CachePrefix.GOODS.getPrefix() + goodsId);
        logger.debug("由缓存中读出商品：");
        logger.debug(goods);
        if (goods == null) {

            GoodsDO goodsDB = this.getModel(goodsId);
            if (goodsDB == null) {
                throw new ServiceException(GoodsErrorCode.E301.code(), "该商品已被彻底删除");
            }

            // GoodsVo的对象返回,GoodsVo中的skuList是要必须填充好的
            List<GoodsSkuVO> skuList = goodsSkuManager.listByGoodsId(goodsId);


            goods = new CacheGoods();
            BeanUtils.copyProperties(goodsDB, goods);
            goods.setSkuList(skuList);

            //填充库存数据
            fillStock(goods);
            cache.put(CachePrefix.GOODS.getPrefix() + goodsId, goods);

            logger.debug("由缓存中读出商品为空,由数据库中返回商品：");
            logger.debug(goods);
            return goods;
        } else {
            //填充库存数据
            fillStock(goods);
        }

        return goods;
    }


    /**
     * 为商品填充库存信息<br/>
     * 库存的信息存储在单独的缓存key中<br/>
     * 由缓存中读取出所有sku的库存，并分别为goods.skuList中的sku设置库存，以保证库存的时时正确性<br/>
     * 同时还会将所有的sku库存累加设置为商品的库存
     *
     * @param goods
     */
    private void fillStock(CacheGoods goods) {

        List<GoodsSkuVO> skuList = goods.getSkuList();

        //由缓存中获取sku的可用库存和实际库存
        //此操作为批量操作，因为是高频操作，要尽量减少和redis的交互次数
        List keys = createKeys(skuList);

        //将商品的可用库存和实际库存一起读
        keys.add(StockCacheKeyUtil.goodsEnableKey(goods.getGoodsId()));
        keys.add(StockCacheKeyUtil.goodsActualKey(goods.getGoodsId()));

        List<String> quantityList = stringRedisTemplate.opsForValue().multiGet(keys);

        int enableTotal = 0;
        int actualTotal = 0;

        int i = 0;
        for (GoodsSkuVO skuVO : skuList) {

            //第一个是可用库存
            Integer enable = StringUtil.toInt(quantityList.get(i), null);

            i++;
            //第二个是真实库存
            Integer actual = StringUtil.toInt(quantityList.get(i), null);

            //缓存被击穿，由数据库中读取
            if (enable == null || actual == null) {
                Map<String, Integer> map = goodsQuantityManager.fillCacheFromDB(skuVO.getSkuId());
                enable = map.get("enable_quantity");
                actual = map.get("quantity");

                //重置缓存中的库存
                stringRedisTemplate.opsForValue().set(StockCacheKeyUtil.skuEnableKey(skuVO.getSkuId()), "" + enable);
                stringRedisTemplate.opsForValue().set(StockCacheKeyUtil.skuActualKey(skuVO.getSkuId()), "" + actual);
            }


            skuVO.setEnableQuantity(enable);
            skuVO.setQuantity(actual);

            if (enable == null) {
                enable = 0;
            }

            if (actual == null) {
                actual = 0;
            }
            //累计商品的库存
            enableTotal += enable;
            actualTotal += actual;

            i++;
        }


        //设置商品的库存
        goods.setEnableQuantity(enableTotal);
        goods.setQuantity(actualTotal);


        //读取缓存中商品的库存，看是否被击穿了
        //第一个是可用库存
        Integer goodsEnable = StringUtil.toInt(quantityList.get(i), null);

        i++;
        //第二个是真实库存
        Integer goodsActual = StringUtil.toInt(quantityList.get(i), null);

        //商品的库存被击穿了
        if (goodsEnable == null || goodsActual == null) {
            //重置缓存中的库存
            stringRedisTemplate.opsForValue().set(StockCacheKeyUtil.goodsEnableKey(goods.getGoodsId()), "" + enableTotal);
            stringRedisTemplate.opsForValue().set(StockCacheKeyUtil.goodsActualKey(goods.getGoodsId()), "" + actualTotal);
        }


    }

    /**
     * 生成批量获取sku库存的keys
     *
     * @param goodsList
     * @return
     */
    private List createKeys(List<GoodsSkuVO> goodsList) {
        List keys = new ArrayList();
        for (GoodsSkuVO goodsSkuVO : goodsList) {

            keys.add(StockCacheKeyUtil.skuEnableKey(goodsSkuVO.getSkuId()));
            keys.add(StockCacheKeyUtil.skuActualKey(goodsSkuVO.getSkuId()));
        }
        return keys;
    }

    @Override
    public WebPage warningGoodsList(GoodsQueryParam goodsQueryParam) {

        Seller seller = UserContext.getSeller();

        QueryWrapper<GoodsDO> queryWrapper = new QueryWrapper<>();

        ShopVO shop = shopClient.getShop(seller.getSellerId());
        if (shop == null) {
            throw new ServiceException(GoodsErrorCode.E301.code(), "店铺不存在");
        }

        goodsQueryParam.setMarketEnable(1);
        goodsQueryParam.setDisabled(1);
        //基础查询
        SearchUtil.baseQuery(goodsQueryParam, queryWrapper, "");
        //sku查询
        List<GoodsSkuDO> list = goodsSkuMapper.selectList(new QueryWrapper<GoodsSkuDO>()
                .select("goods_id")
                .eq("seller_id", seller.getSellerId())
                .le("enable_quantity", shop.getGoodsWarningCount() == null ? 5 : shop.getGoodsWarningCount()));

        if (list.size() > 0) {

            List<Long> ids = list.stream().map(s -> s.getGoodsId()).collect(Collectors.toList());

            queryWrapper.in("goods_id", ids);
            //分类查询
            SearchUtil.categoryQuery(goodsQueryParam, queryWrapper, categoryMapper);
            // 店铺分组
            SearchUtil.shopCatQuery(goodsQueryParam, queryWrapper, shopCatClient);

            IPage<GoodsDO> page = this.goodsMapper.selectPage(new Page<>(goodsQueryParam.getPageNo(), goodsQueryParam.getPageSize()), queryWrapper);
            return PageConvert.convert(page);
        }
        return new WebPage();
    }

    @Override
    public List<GoodsDO> queryDo(Long[] goodsIds) {

        return this.goodsMapper.selectList(new QueryWrapper<GoodsDO>()
                .in("goods_id", Arrays.asList(goodsIds)));
    }

    @Override
    public Double getGoodsGrade(Long goodsId) {

        Double grade = (Double) cache.get(CachePrefix.GOODS_GRADE.getPrefix() + goodsId);
        if (grade == null) {
            GoodsDO goods = this.goodsMapper.selectById(goodsId);
            cache.put(CachePrefix.GOODS_GRADE.getPrefix() + goodsId, goods.getGrade());
        }
        return grade;
    }

    @Override
    public void checkSellerGoodsCount(Long[] goodsIds) {
        Seller seller = UserContext.getSeller();

        Integer count = this.goodsMapper.selectCount(new QueryWrapper<GoodsDO>()
                .eq("seller_id", seller.getSellerId())
                .in("goods_id", Arrays.asList(goodsIds)));

        if (count != goodsIds.length) {
            throw new ServiceException(GoodsErrorCode.E301.code(), "存在不属于您的商品，不能操作");
        }
    }

    @Override
    public List<Map<String, Object>> getGoodsAndParams(Long[] goodsIds) {

        if (goodsIds == null) {
            return null;
        }

        List<Map<String, Object>> list = this.goodsMapper.selectMaps(new QueryWrapper<GoodsDO>()
                .in("goods_id", Arrays.asList(goodsIds))
                .orderByDesc("goods_id"));

        //查询该商品关联的可检索的参数集合
        this.getIndexGoodsList(list);

        return list;

    }

    @Override
    public List<Map<String, Object>> getGoodsAndParams(Long sellerId) {

        List<Map<String, Object>> list = this.goodsMapper.selectMaps(new QueryWrapper<GoodsDO>()
                .in("seller_id", sellerId)
                .eq("market_enable", 1));

        //查询该商品关联的可检索的参数集合
        this.getIndexGoodsList(list);
        return list;
    }

    /**
     * 查询该商品关联的可检索的参数集合
     *
     * @param list 原始商品数据
     * @return
     */
    private List<Map<String, Object>> getIndexGoodsList(List<Map<String, Object>> list) {
        if (list != null) {

            List<Object> ids = list.stream().map(g -> g.get("goods_id")).collect(Collectors.toList());

            // 查询该商品关联的可检索的参数集合
            List<Map<String, Object>> res = this.goodsParamsMapper.getIndexGoodsList(ids);

            for (Map<String, Object> map : list) {

                List<Map<String, Object>> listParams = res.stream().filter(m -> m.get("goods_id").equals(map.get("goods_id"))).collect(Collectors.toList());

                map.put("params", listParams);
            }
        }
        return list;
    }

    @Override
    public List<GoodsDO> listGoods(Long sellerId) {
        return this.goodsMapper.selectList(new QueryWrapper<GoodsDO>()
                .eq("seller_id", sellerId)
                .eq("market_enable", 1)
                .eq("is_auth", 1)
                .eq("disabled", 1)
                .orderByDesc("buy_count")
                .last("limit 5"));
    }

    /**
     * 查询购买量
     *
     * @param goodsIds
     * @return
     */
    @Override
    public List<BuyCountVO> queryBuyCount(Long[] goodsIds) {
        return this.goodsMapper.queryBuyCount(Arrays.asList(goodsIds));
    }


    @Override
    public Integer getSellerGoodsCount(Long sellerId) {

        return goodsMapper.selectCount(new QueryWrapper<GoodsDO>()
                .eq("seller_id", sellerId)
                .eq("market_enable", 1)
                .eq("is_auth", 1)
                .eq("disabled", 1));
    }

    @Override
    public Integer checkArea(Long goodsId, Long areaId) {

        CacheGoods goods = this.getFromCache(goodsId);
        //卖家承担运费
        if (goods.getGoodsTransfeeCharge() == 1) {
            //有货
            return 1;
        }

        ShipTemplateVO temp = this.shipTemplateClient.get(goods.getTemplateId());

        List<ShipTemplateChildBuyerVO> result = new ArrayList();
        //如果指定免运费区域不为空，则放入统一检测
        if (temp.getFreeItems() != null && !temp.getFreeItems().isEmpty()) {
            result.addAll(temp.getFreeItems());
        }
        //如果指定收费区域不为空，则放入统一检测
        if (temp.getItems() != null && !temp.getItems().isEmpty()) {
            result.addAll(temp.getItems());
        }

        //检测设置的收货地区是否在运费模板范围内，
        for (ShipTemplateChildBuyerVO child : result) {
            if (child.getAreaId() != null) {
                if (child.getAreaId().indexOf("," + areaId + ",") >= 0) {
                    //有货
                    return 1;
                }
            }
        }


        //无货
        return 0;
    }

    @Override
    public Integer getGoodsCountByParam(Integer status, Long sellerId, Integer disabled) {

        Integer num = this.goodsMapper.selectCount(new QueryWrapper<GoodsDO>()
                //审核状态为已通过
                .eq("is_auth", 1)
                //如果商品上架状态不为空
                .eq(status != null, "market_enable", status)
                //如果商家id不为空
                .eq(sellerId != null, "seller_id", sellerId)
                //如果商品删除状态不为空
                .eq(disabled != null, "disabled", disabled));

        return num;
    }

    @Override
    public List<Map<String, Object>> getGoods(Long[] goodsIds) {

        return this.goodsMapper.selectMaps(new QueryWrapper<GoodsDO>()
                .select("goods_id", "goods_name", "price as original_price")
                .in("goods_id", Arrays.asList(goodsIds)));
    }


    @Override
    public List<GoodsDO> getSellerGoods(Long sellerId, GoodsType goodsType) {

        return this.goodsMapper.selectList(new QueryWrapper<GoodsDO>()
                .eq("seller_id", sellerId)
                .ne("disabled", -1)
                .eq(goodsType != null, "goods_type", goodsType.name()));
    }

    @Override
    public List<GoodsSelectLine> querySkus(Long[] skuIds) {
        List<Long> list = Arrays.stream(skuIds).filter(skuId -> skuId != -1).collect(Collectors.toList());
        if (CollUtil.isEmpty(list)) {
            return CollUtil.newArrayList();
        }

        return this.goodsMapper.querySkus(list);
    }

    @Override
    public List<GoodsSelectLine> queryGoodsLines(Long[] goodsIds, GoodsQueryParam param) {

        //基础查询
        QueryWrapper<GoodsDO> queryWrapper = new QueryWrapper<>();
        SearchUtil.baseQuery(param, queryWrapper, "");
        queryWrapper.in("goods_id", Arrays.asList(goodsIds));

        return this.goodsMapper.queryGoodsLines(queryWrapper);
    }

    @Override
    public List<Map<String, Object>> queryGoodsByRange(Long pageNo, Long pageSize) {


        IPage iPage = this.goodsMapper.selectMapsPage(new Page<>(pageNo,pageSize),new QueryWrapper<GoodsDO>()
                .ne("disabled", -1)
                .orderByDesc("goods_id"));

        return iPage.getRecords();
    }


    /**
     * 获取商品参与的促销活动
     *
     * @param goodsId
     * @return
     */
    private String convertTips(Long goodsId) {
        List<PromotionVO> promotionVOS = promotionGoodsClient.getPromotion(goodsId);
        if (promotionVOS == null || promotionVOS.isEmpty()) {
            return "";
        }
        List<String> tips = new ArrayList<>();
        promotionVOS.forEach(promotionVO -> {
            tips.add(PromotionTypeEnum.valueOf(promotionVO.getPromotionType()).getPromotionName());
        });
        StringBuffer result = new StringBuffer("此商品参与的[");
        result.append(StringUtil.listToString(tips, ","));
        result.append("]促销活动,修改上架后将会被取消");
        return result.toString();
    }
}
