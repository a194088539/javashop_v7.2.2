package com.enation.app.javashop.service.goods.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.model.base.CachePrefix;
import com.enation.app.javashop.model.base.message.GoodsChangeMsg;
import com.enation.app.javashop.model.base.rabbitmq.AmqpExchange;
import com.enation.app.javashop.client.member.ShopCatClient;
import com.enation.app.javashop.client.promotion.ExchangeGoodsClient;
import com.enation.app.javashop.framework.JavashopConfig;
import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import com.enation.app.javashop.framework.rabbitmq.MessageSender;
import com.enation.app.javashop.framework.rabbitmq.MqMessage;
import com.enation.app.javashop.framework.security.model.Seller;
import com.enation.app.javashop.framework.util.*;
import com.enation.app.javashop.mapper.goods.CategoryMapper;
import com.enation.app.javashop.mapper.goods.GoodsMapper;
import com.enation.app.javashop.mapper.goods.GoodsSkuMapper;
import com.enation.app.javashop.model.base.CachePrefix;
import com.enation.app.javashop.model.base.rabbitmq.AmqpExchange;
import com.enation.app.javashop.model.errorcode.GoodsErrorCode;
import com.enation.app.javashop.model.goods.dos.GoodsDO;
import com.enation.app.javashop.model.goods.dos.GoodsGalleryDO;
import com.enation.app.javashop.model.goods.dos.GoodsSkuDO;
import com.enation.app.javashop.model.goods.dto.GoodsQueryParam;
import com.enation.app.javashop.model.goods.enums.GoodsType;
import com.enation.app.javashop.model.goods.enums.QuantityType;
import com.enation.app.javashop.model.goods.vo.GoodsQuantityVO;
import com.enation.app.javashop.model.goods.vo.GoodsSelectorSkuVO;
import com.enation.app.javashop.model.goods.vo.GoodsSkuVO;
import com.enation.app.javashop.model.goods.vo.SpecValueVO;
import com.enation.app.javashop.model.promotion.exchange.dos.ExchangeDO;
import com.enation.app.javashop.model.promotion.tool.enums.PromotionTypeEnum;
import com.enation.app.javashop.model.promotion.tool.vo.PromotionScriptVO;
import com.enation.app.javashop.service.goods.*;
import com.enation.app.javashop.service.goods.impl.util.SearchUtil;
import com.enation.app.javashop.service.goods.impl.util.StockCacheKeyUtil;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品sku业务类
 *
 * @author fk
 * @author kingapex
 * @version v3.0
 * @since v7.0.0 2018-03-21 11:48:40
 * <p>
 * version 3.0 written by kingapex 2019-02-22 :<br/>
 * <li>sku表通过hashcode字段来确定是否有规格变化</li>
 * <li>通过lua脚本来更新库存</li>
 */
@Service
public class GoodsSkuManagerImpl implements GoodsSkuManager {


    @Autowired
    private GoodsGalleryManager goodsGalleryManager;

    @Autowired
    private GoodsQuantityManager goodsQuantityManager;

    @Autowired
    private Cache cache;

    @Autowired
    private ShopCatClient shopCatClient;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private JavashopConfig javashopConfig;

    @Autowired
    private GoodsQueryManager goodsQueryManager;

    @Autowired
    private GoodsManager goodsManager;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private ExchangeGoodsClient exchangeGoodsClient;

    @Autowired
    private GoodsSkuMapper goodsSkuMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 查询SKU列表
     *
     * @param goodsQueryParam
     * @return
     */
    @Override
    public WebPage list(GoodsQueryParam goodsQueryParam) {

        QueryWrapper<GoodsDO> wrapper = new QueryWrapper<>();
        //基础查询
        SearchUtil.baseQuery(goodsQueryParam,wrapper,"g." );
        //分类查询
        SearchUtil.categoryQuery(goodsQueryParam, wrapper, categoryMapper);
        // 店铺分组
        SearchUtil.shopCatQuery(goodsQueryParam, wrapper, shopCatClient);

        wrapper.orderByDesc("g.goods_id");
        IPage<GoodsSelectorSkuVO> iPage = this.goodsMapper.querySkusPage(new Page<>(goodsQueryParam.getPageNo(),goodsQueryParam.getPageSize()),wrapper);

        return PageConvert.convert(iPage);
    }

    @Override
    @Transactional(value = "goodsTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void add(List<GoodsSkuVO> skuList, GoodsDO goods) {

        List<GoodsSkuDO> newSkuList = new ArrayList<>();
        // 如果有规格
        if (skuList != null && skuList.size() > 0) {
            // 添加商品sku
            this.addGoodsSku(skuList, goods);

            //转为do
            skuList.forEach(skuVO -> {
                skuVO.setGoodsId(goods.getGoodsId());
                newSkuList.add(skuVO);
            });
        } else {
            // 添加没有规格的sku信息
            GoodsSkuDO newSku = this.addNoSku(goods);
            newSkuList.add(newSku);
        }

        //为新增的sku增加库存
        updateStock(newSkuList);
    }


    @Override
    @Transactional(value = "goodsTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void edit(List<GoodsSkuVO> skuList, GoodsDO goods) {

        //新增的sku列表，用于同步这些sku的缓存
        List<GoodsSkuDO> newSkuList = new ArrayList<>();

        // 如果编辑的时候sku数据有变化(包括规格项组合变了，有规格改成无规格，无规格改成有规格) hasChanged=1 规格有改变
        Long goodsId = goods.getGoodsId();

        //生成这个商品的所有sku的集合，不在这个集后的要删除
        String hashCodeStr = "";

        if (skuList != null) {
            for (GoodsSkuVO goodsSkuVO : skuList) {
                goodsSkuVO.setGoodsId(goodsId);

                int hashCode = buildHashCode(goodsSkuVO.getSpecList());
                goodsSkuVO.setHashCode(hashCode);

                if (!StringUtil.isEmpty(hashCodeStr)) {
                    hashCodeStr = hashCodeStr + ",";
                }
                hashCodeStr = hashCodeStr + "" + hashCode;

                //新增的sku
                if (goodsSkuVO.getSkuId() == null || goodsSkuVO.getSkuId() == 0) {
                    GoodsSkuDO newSku = add(goodsSkuVO, goods);
                    newSku.setGoodsId(goodsId);
                    newSkuList.add(newSku);
                } else {
                    //更新已经存在的
                    update(goodsSkuVO, goods);
                }
            }
        }


        //清除不存在的sku的缓存及数据库,返回清除掉的skuId
        List<Long> delSkuIds = cleanNotExits(hashCodeStr, goodsId);


        //没有规格的商品，用goods_id和hash_code=-1做为条件
        if (skuList == null || skuList.isEmpty()) {

            //查找是否有不带规格的sku，如果有则更新，没有则添加一个
            QueryWrapper<GoodsSkuDO> queryWrapper = new QueryWrapper<GoodsSkuDO>().eq("goods_id",goods.getGoodsId()).eq("hash_code",-1);
            int count = this.goodsSkuMapper.selectCount(queryWrapper);

            if (count > 0) {
                // 修改没有规格的sku信息
                GoodsSkuDO goodsSku = new GoodsSkuDO();
                BeanUtils.copyProperties(goods, goodsSku);
                //没有规格的sku的hashcode 为-1
                goodsSku.setHashCode(-1);

                Map map = new HashMap(16);
                map.put("goods_id", goods.getGoodsId());
                map.put("hash_code", "-1");
                this.goodsSkuMapper.update(goodsSku,queryWrapper);
            } else {
                GoodsSkuDO newSku = addNoSku(goods);
                newSku.setGoodsId(goodsId);
                newSkuList.add(newSku);

            }

        }

        if (newSkuList != null && !newSkuList.isEmpty()) {
            //为新增的sku增加库存
            updateStock(newSkuList);
        }

        //重新计算库存
        reCountGoodsStock(goodsId);

        // 发送商品sku变化消息
        this.messageSender.send(new MqMessage(AmqpExchange.GOODS_SKU_CHANGE, AmqpExchange.GOODS_SKU_CHANGE + "_ROUTING", delSkuIds));


    }


    /**
     * 重新计算商品的库存
     *
     * @param goodsId
     */
    private void reCountGoodsStock(Long goodsId) {

        GoodsSkuDO goodsSku = this.goodsSkuMapper.selectOne(new QueryWrapper<GoodsSkuDO>()
                .select("sum(quantity) quantity", "sum(enable_quantity) enable_quantity")
                .eq("goods_id", goodsId));

        Integer quantity = goodsSku.getQuantity();
        Integer enableQuantity = goodsSku.getEnableQuantity();

        //更新数据库的库存
        this.goodsMapper.update(new GoodsDO(),new UpdateWrapper<GoodsDO>()
                .set("quantity",quantity)
                .set("enable_quantity",enableQuantity)
                .eq("goods_id",goodsId));

        //更新缓存的库存
        stringRedisTemplate.opsForValue().set(StockCacheKeyUtil.goodsActualKey(goodsId), "" + quantity);
        stringRedisTemplate.opsForValue().set(StockCacheKeyUtil.goodsEnableKey(goodsId), "" + enableQuantity);

    }

    private void updateStock(List<GoodsSkuDO> skuList) {
        List<GoodsQuantityVO> quantityList = new ArrayList<>();
        skuList.forEach(sku -> {
            addStockToList(quantityList, sku);
        });
        goodsQuantityManager.updateSkuQuantity(quantityList);

        //如果商品库存缓冲池开启了，那么需要立即同步数据库的商品库存，以保证商品库存显示正常
        if (javashopConfig.isStock()) {
            goodsQuantityManager.syncDataBase();
        }
    }

    /**
     * sku转为库存信息并压入list
     *
     * @param quantityList 要压入的库存list
     * @param sku          sku
     */
    private void addStockToList(List<GoodsQuantityVO> quantityList, GoodsSkuDO sku) {
        //实际库存vo
        GoodsQuantityVO actualQuantityVO = new GoodsQuantityVO();
        actualQuantityVO.setQuantity(sku.getQuantity());
        actualQuantityVO.setGoodsId(sku.getGoodsId());
        actualQuantityVO.setSkuId(sku.getSkuId());
        actualQuantityVO.setQuantityType(QuantityType.actual);

        //可用库存vo
        GoodsQuantityVO enableQuantityVO = new GoodsQuantityVO();
        enableQuantityVO.setQuantity(sku.getQuantity());
        enableQuantityVO.setGoodsId(sku.getGoodsId());
        enableQuantityVO.setSkuId(sku.getSkuId());
        enableQuantityVO.setQuantityType(QuantityType.enable);

        quantityList.add(actualQuantityVO);
        quantityList.add(enableQuantityVO);
    }

    /**
     * 清除不存在的sku的缓存及数据库
     *
     * @param hashCodeStr
     * @param goodsId
     * @return
     */
    private List<Long> cleanNotExits(String hashCodeStr, Long goodsId) {

        QueryWrapper<GoodsSkuDO> wrapper = new QueryWrapper<GoodsSkuDO>().select("sku_id").eq("goods_id", goodsId);

        if (StringUtil.isEmpty(hashCodeStr)) {
            wrapper.ne("hash_code", -1);
        } else {
            wrapper.notIn("hash_code", Arrays.asList(hashCodeStr.split(",")));
        }
        List<GoodsSkuDO> list = this.goodsSkuMapper.selectList(wrapper);
        List<Long> skuIdList = list.stream().map(s -> s.getSkuId()).collect(Collectors.toList());

        List<String> keys = StockCacheKeyUtil.skuKeys(skuIdList);

        logger.debug("删除keys:");
        logger.debug(keys.toString());


        if (keys != null && !keys.isEmpty()) {

            stringRedisTemplate.delete(keys);
        }

        //批量删除要删除的：hashcode 不存在的 ，但不能是hashcode=-1的，因为有可能是没有规格导致的skuList为空
        this.goodsSkuMapper.delete(wrapper);

        return skuIdList;

    }


    @Override
    public List<GoodsSkuVO> listByGoodsId(Long goodsId) {

        List<GoodsSkuDO> list = goodsSkuMapper.selectList(new QueryWrapper<GoodsSkuDO>().eq("goods_id", goodsId));

        List<GoodsSkuVO> result = new ArrayList<>();
        for (GoodsSkuDO sku : list) {
            GoodsSkuVO skuVo = new GoodsSkuVO();
            BeanUtils.copyProperties(sku, skuVo);
            result.add(skuVo);
        }
        return result;
    }

    @Override
    public GoodsSkuVO getSkuFromCache(Long skuId) {
        // 从缓存读取sku信息
        GoodsSkuVO skuVo = (GoodsSkuVO) cache.get(CachePrefix.SKU.getPrefix() + skuId);
        // 缓存中没有找到商品，或者最后修改时间为空（表示数据异常），从数据库中查询
        if (skuVo == null || skuVo.getLastModify() == null) {
            GoodsSkuDO sku = this.getModel(skuId);
            if (sku == null) {
                return null;
            }
            skuVo = getSku(skuId);
            return skuVo;
        } else {
            //填充sku中的库存信息
            fillStock(skuVo);
        }
        return skuVo;
    }

    @Override
    public GoodsSkuVO getSku(Long skuId) {
        GoodsSkuDO sku = this.getModel(skuId);
        GoodsSkuVO skuVo = new GoodsSkuVO();
        BeanUtils.copyProperties(sku, skuVo);

        //以下信息由商品中获取
        GoodsDO goods = this.goodsQueryManager.getModel(sku.getGoodsId());

        skuVo.setLastModify(goods.getLastModify());
        skuVo.setGoodsTransfeeCharge(goods.getGoodsTransfeeCharge());
        skuVo.setDisabled(goods.getDisabled());
        skuVo.setMarketEnable(goods.getMarketEnable());
        skuVo.setTemplateId(goods.getTemplateId());
        skuVo.setGoodsType(goods.getGoodsType());
        //如果sku绑定的运费模板不为空的话则将script重新赋值
        if (skuVo.getTemplateId() != 0) {
            List<String> scripts = (List<String>) cache.get(CachePrefix.SHIP_SCRIPT.getPrefix() + skuVo.getTemplateId());
            if (scripts != null) {
                skuVo.setScripts(scripts);
            }
        }
        cache.put(CachePrefix.SKU.getPrefix() + skuId, skuVo);
        return skuVo;
    }


    @Override
    public List<GoodsSkuDO> querySellerAllSku() {
        Seller seller = UserContext.getSeller();
        return this.goodsSkuMapper.querySellerAllSku(seller.getSellerId());
    }

    @Override
    public void checkSellerGoodsCount(Long[] skuIds) {
        Seller seller = UserContext.getSeller();
        Integer count = this.goodsSkuMapper.selectCount(new QueryWrapper<GoodsSkuDO>()
                .in("sku_id", Arrays.asList(skuIds))
                .eq("seller_id", seller.getSellerId()));
        if (count != skuIds.length) {
            throw new ServiceException(GoodsErrorCode.E301.code(), "存在不属于您的商品，不能操作");
        }
    }

    @Override
    public GoodsSkuDO getModel(Long id) {
        return this.goodsSkuMapper.selectById(id);
    }


    @Override
    @Transactional(value = "goodsTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(Long[] goodsIds) {
        // 删除这个商品的sku缓存(必须要在删除库中sku前先删缓存),首先查出商品对应的sku_id
        QueryWrapper<GoodsSkuDO> wrapper = new QueryWrapper<GoodsSkuDO>().in("goods_id", Arrays.asList(goodsIds)).select("sku_id");
        List<GoodsSkuDO> list = this.goodsSkuMapper.selectList(wrapper);

        for (GoodsSkuDO sku : list) {
            cache.remove(CachePrefix.SKU.getPrefix() + sku.getSkuId());
        }
        this.goodsSkuMapper.delete(wrapper);
    }

    /**
     * 增加sku集合
     *
     * @param skuList
     * @param goods
     */
    private void addGoodsSku(List<GoodsSkuVO> skuList, GoodsDO goods) {

        for (GoodsSkuVO skuVO : skuList) {
            add(skuVO, goods);

        }
    }

    private GoodsSkuDO add(GoodsSkuVO skuVO, GoodsDO goods) {
        skuVO.setGoodsId(goods.getGoodsId());
        GoodsSkuDO sku = new GoodsSkuDO();
        BeanUtils.copyProperties(skuVO, sku);

        sku.setEnableQuantity(sku.getQuantity());
        sku.setGoodsName(goods.getGoodsName());
        sku.setCategoryId(goods.getCategoryId());
        // 得到规格值的json
        String specJson = getSpecListJson(skuVO.getSpecList());
        sku.setSpecs(specJson);
        sku.setGoodsId(goods.getGoodsId());
        sku.setSellerId(goods.getSellerId());
        sku.setSellerName(goods.getSellerName());
        sku.setThumbnail(goods.getThumbnail());
        if (sku.getHashCode() == null || sku.getHashCode() == 0) {
            int hashCode = buildHashCode(skuVO.getSpecList());
            sku.setHashCode(hashCode);
            skuVO.setHashCode(hashCode);

        }
        this.goodsSkuMapper.insert(sku);
        Long skuId = sku.getSkuId();
        skuVO.setSkuId(skuId);
        sku.setSkuId(skuId);

        return sku;
    }

    private void update(GoodsSkuVO sku, GoodsDO goods) {
        String specJson = getSpecListJson(sku.getSpecList());

        this.goodsSkuMapper.update(new GoodsSkuDO(), new UpdateWrapper<GoodsSkuDO>()
                .set("category_id", goods.getCategoryId())
                .set("goods_name", goods.getGoodsName())
                .set("sn", sku.getSn())
                .set("price", sku.getPrice())
                .set("cost", sku.getCost())
                .set("weight", sku.getWeight())
                .set("thumbnail", goods.getThumbnail())
                .set("specs", specJson)
                .eq("sku_id", sku.getSkuId()));

    }

    /**
     * sku中的spec字段的操作，返回json
     *
     * @param specList 规格值集合
     * @return 规格值json
     */
    private String getSpecListJson(List<SpecValueVO> specList) {
        String spenJson = null;
        for (SpecValueVO specvalue : specList) {
            if (specvalue.getSpecType() == null) {
                specvalue.setSpecType(0);
            }
            if (specvalue.getSpecType() == 1) {
                GoodsGalleryDO goodsGallery = goodsGalleryManager.getGoodsGallery(specvalue.getSpecImage());
                specvalue.setBig(goodsGallery.getBig());
                specvalue.setSmall(goodsGallery.getSmall());
                specvalue.setThumbnail(goodsGallery.getThumbnail());
                specvalue.setTiny(goodsGallery.getTiny());
                // 规格只有第一个规格有图片，所以找到有图片的规格后就可跳出循环
                break;
            }
        }
        spenJson = JsonUtil.objectToJson(specList);
        return spenJson;
    }

    /**
     * 添加没有规格的sku信息
     *
     * @param goods 商品信息
     * @return
     */
    private GoodsSkuDO addNoSku(GoodsDO goods) {

        GoodsSkuDO goodsSku = new GoodsSkuDO();
        BeanUtils.copyProperties(goods, goodsSku);
        goodsSku.setEnableQuantity(goodsSku.getQuantity());
        goodsSku.setHashCode(-1);
        this.goodsSkuMapper.insert(goodsSku);
        return goodsSku;
    }


    private int buildHashCode(List<SpecValueVO> specValueVOList) {
        HashCodeBuilder codeBuilder = new HashCodeBuilder(17, 37);
        specValueVOList.forEach(specValueVO -> {
            String specValue = specValueVO.getSpecValue();
            codeBuilder.append(specValue);

        });
        int hashCode = codeBuilder.toHashCode();

        return hashCode;
    }

    public static void main(String[] args) {
//        pingtuanExecuter
        HashCodeBuilder codeBuilder = new HashCodeBuilder(17, 37);
        codeBuilder.append("pingtuanExecuter");
        codeBuilder.append("2018-09-09 12:00");
        int hashCode = codeBuilder.toHashCode();
        System.out.println(hashCode);

        codeBuilder = new HashCodeBuilder(17, 37);
        codeBuilder.append("pingtuanExecuter");
        codeBuilder.append("2018-09-09 13:00");
        hashCode = codeBuilder.toHashCode();
        System.out.println(hashCode);

    }

    /**
     * 清除商品规格信息和sku信息 及缓存
     *
     * @param goodsId 商品id
     */
    private void clean(Long goodsId) {
        List<String> keys = new ArrayList<>();

        List<GoodsSkuDO> list = this.goodsSkuMapper.selectList(new QueryWrapper<GoodsSkuDO>()
                .select("sku_id")
                .eq("goods_id", goodsId));

        for (GoodsSkuDO sku : list) {
            Long skuid = sku.getSkuId();

            //清除sku基本信息
            keys.add(CachePrefix.SKU.getPrefix() + skuid);

            String key1 = StockCacheKeyUtil.skuActualKey(skuid);
            String key2 = StockCacheKeyUtil.skuEnableKey(skuid);

            //清除sku缓存
            keys.add(key1);
            keys.add(key2);

        }

        //清除商品的库存
        keys.add(StockCacheKeyUtil.goodsEnableKey(goodsId));
        keys.add(StockCacheKeyUtil.goodsActualKey(goodsId));

        stringRedisTemplate.delete(keys);

    }

    /**
     * 为sku填充库存信息<br/>
     * 库存的信息存储在单独的缓存key中<br/>
     * 由缓存中读取出sku的可用库存和实际库存，并分别设置到sku库存信息中，以保证库存的实时正确性<br/>
     *
     * @param goodsSkuVO
     */
    private void fillStock(GoodsSkuVO goodsSkuVO) {
        //获取缓存中sku的实际库存
        String cacheActualStock = stringRedisTemplate.opsForValue().get(StockCacheKeyUtil.skuActualKey(goodsSkuVO.getSkuId()));
        //获取缓存中sku的可用库存
        String cacheEnableStock = stringRedisTemplate.opsForValue().get(StockCacheKeyUtil.skuEnableKey(goodsSkuVO.getSkuId()));

        //如果以上两项都不为空，也就是缓存中都存在，那么就将缓存中的库存信息set进sku对象中
        if (StringUtil.notEmpty(cacheActualStock) && StringUtil.notEmpty(cacheEnableStock)) {
            goodsSkuVO.setQuantity(StringUtil.toInt(cacheActualStock, goodsSkuVO.getQuantity()));
            goodsSkuVO.setEnableQuantity(StringUtil.toInt(cacheEnableStock, goodsSkuVO.getEnableQuantity()));
        }

    }

    /**
     * 根据商品sku主键id集合获取商品信息
     *
     * @param skuIds
     * @return
     */
    @Override
    public List<GoodsSkuVO> query(Long[] skuIds) {

        return this.goodsSkuMapper.queryGoodsSkuVOList(Arrays.asList(skuIds));
    }

    @Override
    @Transactional(value = "goodsTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateSkuByGoods(GoodsDO goods) {

        UpdateWrapper<GoodsSkuDO> updateWrapper = new UpdateWrapper<GoodsSkuDO>()
                .set("category_id", goods.getCategoryId())
                .set("goods_name", goods.getGoodsName())
                .set("thumbnail", goods.getThumbnail())
                .eq("goods_id", goods.getGoodsId());
        //如果此商品有规格则不对价格进行修改 单独在sku维护，否则需要同步商品的价格
        if (goods.getHaveSpec() == null || !goods.getHaveSpec().equals(1)) {
            updateWrapper.set("price", goods.getPrice()).set("weight", goods.getWeight());
        }

        this.goodsSkuMapper.update(new GoodsSkuDO(), updateWrapper);
    }

    @Override
    @Transactional(value = "goodsTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void editSkus(List<GoodsSkuVO> skuList, Long goodsId) {

        //修改商品的规格，可能是有规格商品变成无规格商品，可能是无规格商品变成有规格商品，可能是修改规格
        GoodsDO goods = goodsQueryManager.getModel(goodsId);

        //获取商品规格变化之前的sku信息集合
        List<GoodsSkuVO> oldSkuList = this.listByGoodsId(goodsId);

        this.goodsManager.cleanGoodsAssociated(goodsId, goods.getMarketEnable());

        this.edit(skuList, goods);

        UpdateWrapper<GoodsDO> updateWrapper = new UpdateWrapper<GoodsDO>().set("last_modify", DateUtil.getDateline()).eq("goods_id", goodsId);
        //修改规格商品需要修改商品的价格（使用规格商品的最低价），修改商品的库存等
        if (StringUtil.isNotEmpty(skuList)) {
            Double price = skuList.get(0).getPrice();
            Double cost = skuList.get(0).getCost();
            Double weight = skuList.get(0).getWeight();
            for (GoodsSkuVO goodsSkuVO : skuList) {
                if (goodsSkuVO.getPrice() < price) {
                    price = goodsSkuVO.getPrice();
                    cost = goodsSkuVO.getCost();
                    weight = goodsSkuVO.getWeight();
                }
            }
            //修改规格时，变更商品最后修改时间 add by liuyulei 2020-03-02
            updateWrapper.set("price", price)
                    .set("cost", cost)
                    .set("weight", weight)
                    .set("have_spec", 1);

            this.goodsMapper.update(new GoodsDO(), updateWrapper);
            // 发送增加商品消息，店铺增加自身商品数量，静态页使用
            GoodsChangeMsg goodsChangeMsg = new GoodsChangeMsg(new Long[]{goodsId},
                    GoodsChangeMsg.UPDATE_OPERATION);
            this.messageSender.send(new MqMessage(AmqpExchange.GOODS_CHANGE, AmqpExchange.GOODS_CHANGE + "_ROUTING", goodsChangeMsg));

        } else {
            //原来有规格，现在变成没有规格了
            if (goods.getHaveSpec() != null && goods.getHaveSpec() == 1) {
                updateWrapper.set("have_spec", 0);
                this.goodsMapper.update(new GoodsDO(), updateWrapper);
            }
        }

        //清除缓存中已被删除的商品SKU促销脚本信息
        this.cleanSkuPromotionScript(skuList, oldSkuList);
        //积分兑换商品修改商品规格更新缓存中的脚本信息
        this.updateSkuExchangeScript(goodsId, goods.getGoodsType(), oldSkuList);

    }

    @Override
    public void createSkuExchangeScript(Long skuId, Long exchangeId, Double price, Integer point) {

        //缓存key
        String cacheKey = CachePrefix.SKU_PROMOTION.getPrefix() + skuId;

        //积分兑换商品脚本信息
        PromotionScriptVO scriptVO = new PromotionScriptVO();

        //渲染并读取积分兑换商品脚本信息
        String script = renderScript(price, point);

        scriptVO.setPromotionScript(script);
        scriptVO.setPromotionId(exchangeId);
        scriptVO.setPromotionType(PromotionTypeEnum.EXCHANGE.name());
        scriptVO.setIsGrouped(false);
        scriptVO.setPromotionName("积分兑换");
        scriptVO.setSkuId(skuId);

        //从缓存中读取脚本信息
        List<PromotionScriptVO> scriptList = (List<PromotionScriptVO>) cache.get(cacheKey);
        if (scriptList == null) {
            scriptList = new ArrayList<>();
        }
        scriptList.add(scriptVO);

        cache.put(cacheKey, scriptList);

    }

    @Override
    public void deleteSkuExchangeScript(Long skuId) {
        //缓存key
        String cacheKey = CachePrefix.SKU_PROMOTION.getPrefix() + skuId;
        //从缓存中读取脚本信息
        List<PromotionScriptVO> scriptList = (List<PromotionScriptVO>) cache.get(cacheKey);
        if (scriptList != null && scriptList.size() != 0) {
            //循环促销脚本缓存数据集合
            for (PromotionScriptVO script : scriptList) {
                //如果脚本数据的促销活动信息与当前修改的促销活动信息一致，那么就将此信息删除
                if (PromotionTypeEnum.EXCHANGE.name().equals(script.getPromotionType())
                        && script.getSkuId().intValue() == skuId.intValue()) {
                    scriptList.remove(script);
                    break;
                }
            }

            //如果经过上面的处理过后脚本集合长度为0，那么直接删除；如果不为0，那么把剩余的脚本信息重新放入缓存中。
            if (scriptList.size() == 0) {
                cache.remove(cacheKey);
            } else {
                cache.put(cacheKey, scriptList);
            }
        }
    }

    @Override
    public void deleteSkuExchangeScript(Long goodsId, String oldType, String newType) {
        //如果原商品类型为积分商品，先商品类型为普通商品
        if (GoodsType.POINT.name().equals(oldType) && GoodsType.NORMAL.name().equals(newType)) {
            //获取商品的SKU信息
            List<GoodsSkuVO> skuList = this.listByGoodsId(goodsId);
            for (GoodsSkuVO goodsSkuVO : skuList) {
                this.deleteSkuExchangeScript(goodsSkuVO.getSkuId());
            }
        }
    }

    /**
     * 渲染并读取积分兑换商品脚本信息
     *
     * @param price 兑换积分商品所需的价钱
     * @param point 兑换积分商品所需的积分
     * @return
     */
    private String renderScript(Double price, Integer point) {
        Map<String, Object> model = new HashMap<>();

        Map<String, Object> params = new HashMap<>();
        params.put("price", price);
        params.put("point", point);

        model.put("goods", params);

        String path = "exchange.ftl";
        String script = ScriptUtil.renderScript(path, model);

        logger.debug("生成积分兑换商品脚本：" + script);

        return script;
    }

    /**
     * 清除缓存中的商品SKU促销信息脚本
     *
     * @param skuList    编辑后的商品SKU集合
     * @param oldSkuList 编辑前的商品SKU集合
     */
    private void cleanSkuPromotionScript(List<GoodsSkuVO> skuList, List<GoodsSkuVO> oldSkuList) {
        //将编辑后的商品skuID取出
        List<Long> newSkuList = new ArrayList<Long>();
        for (GoodsSkuVO goodsSkuVO : skuList) {
            if (goodsSkuVO.getSkuId() != null && goodsSkuVO.getSkuId() != 0) {
                newSkuList.add(goodsSkuVO.getSkuId());
            }
        }

        //如果编辑后的skuID集合长度为0，证明之前存在的SKU均已删除，那么编辑前的所有商品SKU的促销脚本信息均要删除
        //长度不为0，证明之前存在的SKU还有部分存在，那么取出不存在的商品SKU删除促销脚本信息
        if (newSkuList.size() == 0) {
            for (GoodsSkuVO goodsSkuVO : oldSkuList) {
                cache.remove(CachePrefix.SKU_PROMOTION.getPrefix() + goodsSkuVO.getSkuId());
            }
        } else {
            for (GoodsSkuVO goodsSkuVO : oldSkuList) {
                if (!newSkuList.contains(goodsSkuVO.getSkuId())) {
                    cache.remove(CachePrefix.SKU_PROMOTION.getPrefix() + goodsSkuVO.getSkuId());
                }
            }
        }
    }

    /**
     * 积分兑换商品修改商品规格更新缓存中的脚本信息
     *
     * @param goodsId    商品id
     * @param goodsType  商品类型
     * @param oldSkuList 原商品SKU信息集合
     */
    private void updateSkuExchangeScript(Long goodsId, String goodsType, List<GoodsSkuVO> oldSkuList) {
        //如果商品为积分兑换商品，需要更新缓存中的积分商品脚本信息
        if (GoodsType.POINT.name().equals(goodsType)) {
            //获取积分兑换商品信息
            ExchangeDO exchangeDO = this.exchangeGoodsClient.getModelByGoods(goodsId);

            //先删除规格修改前积分商品sku已有的脚本信息
            for (GoodsSkuVO goodsSkuVO : oldSkuList) {
                this.deleteSkuExchangeScript(goodsSkuVO.getSkuId());
            }

            //获取商品规格变化之后的sku信息集合
            List<GoodsSkuVO> newSkuList = this.listByGoodsId(goodsId);

            //重新生成脚本信息
            for (GoodsSkuVO goodsSkuVO : newSkuList) {
                this.createSkuExchangeScript(goodsSkuVO.getSkuId(), exchangeDO.getExchangeId(), exchangeDO.getExchangeMoney(), exchangeDO.getExchangePoint());
            }
        }
    }
}
