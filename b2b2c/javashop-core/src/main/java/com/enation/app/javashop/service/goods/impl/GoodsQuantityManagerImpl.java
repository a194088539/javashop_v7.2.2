package com.enation.app.javashop.service.goods.impl;

import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.enation.app.javashop.framework.JavashopConfig;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import com.enation.app.javashop.mapper.goods.GoodsMapper;
import com.enation.app.javashop.mapper.goods.GoodsSkuMapper;
import com.enation.app.javashop.model.goods.enums.QuantityType;
import com.enation.app.javashop.model.goods.vo.GoodsQuantityVO;
import com.enation.app.javashop.service.goods.GoodsQuantityManager;
import com.enation.app.javashop.service.goods.impl.util.StockCacheKeyUtil;
import com.enation.app.javashop.service.goods.impl.util.UpdatePool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.ScriptSource;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品库存接口
 *
 * @author fk
 * @author kingapex
 * @version v2.0 written by kingapex  2019年2月27日
 * 采用lua脚本执行redis中的库存扣减<br/>
 * 数据库的更新采用非时时同步<br/>
 * 而是建立了一个缓冲池，当达到一定条件时再同步数据库<br/>
 * 这样条件有：缓冲区大小，缓冲次数，缓冲时间<br/>
 * 上述条件在配置中心可以配置，如果没有配置采用 ${@link UpdatePool} 默认值<br/>
 * 在配置项说明：<br/>
 * <li>缓冲区大小：javashop.pool.stock.max-pool-size</li>
 * <li>缓冲次数：javashop.pool.stock.max-update-time</li>
 * <li>缓冲时间（秒数）：javashop.pool.stock.max-lazy-second</li>
 * @see JavashopConfig
 */
@Service
public class GoodsQuantityManagerImpl implements GoodsQuantityManager {


    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private JavashopConfig javashopConfig;

    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsSkuMapper goodsSkuMapper;


    /**
     * sku库存更新缓冲池
     */
    private static UpdatePool skuUpdatePool;
    /**
     * goods库存更新缓冲池
     */
    private static UpdatePool goodsUpdatePool;


    /**
     * 单例获取sku pool ，初始化时设置参数
     *
     * @return
     */
    private UpdatePool getSkuPool() {
        if (skuUpdatePool == null) {
            skuUpdatePool = new UpdatePool(javashopConfig.getMaxUpdateTime(), javashopConfig.getMaxPoolSize(), javashopConfig.getMaxLazySecond());
            logger.debug("初始化sku pool:");
            logger.debug(skuUpdatePool.toString());
        }

        return skuUpdatePool;
    }


    /**
     * 单例获取goods pool ，初始化时设置参数
     *
     * @return
     */
    private UpdatePool getGoodsPool() {
        if (goodsUpdatePool == null) {
            goodsUpdatePool = new UpdatePool(javashopConfig.getMaxUpdateTime(), javashopConfig.getMaxPoolSize(), javashopConfig.getMaxLazySecond());


        }

        return goodsUpdatePool;
    }

    @Autowired
    public StringRedisTemplate stringRedisTemplate;

    private static RedisScript<Boolean> script = null;

    private static RedisScript<Boolean> getRedisScript() {

        if (script != null) {
            return script;
        }

        ScriptSource scriptSource = new ResourceScriptSource(new ClassPathResource("sku_quantity.lua"));
        String str = null;
        try {
            str = scriptSource.getScriptAsString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        script = RedisScript.of(str, Boolean.class);
        return script;
    }

    @Override
    public Boolean updateSkuQuantity(List<GoodsQuantityVO> goodsQuantityList) {

        List<Long> skuIdList = new ArrayList();
        List<Long> goodsIdList = new ArrayList();

        List keys = new ArrayList<>();
        List values = new ArrayList<>();

        for (GoodsQuantityVO quantity : goodsQuantityList) {

            Assert.notNull(quantity.getGoodsId(), "goods id must not be null");
            Assert.notNull(quantity.getSkuId(), "sku id must not be null");
            Assert.notNull(quantity.getQuantity(), "quantity id must not be null");
            Assert.notNull(quantity.getQuantityType(), "Type must not be null");


            //sku库存
            if (QuantityType.enable.equals(quantity.getQuantityType())) {
                keys.add(StockCacheKeyUtil.skuEnableKey(quantity.getSkuId()));
            } else if (QuantityType.actual.equals(quantity.getQuantityType())) {
                keys.add(StockCacheKeyUtil.skuActualKey(quantity.getSkuId()));
            }
            values.add("" + quantity.getQuantity());

            //goods库存key
            if (QuantityType.enable.equals(quantity.getQuantityType())) {
                keys.add(StockCacheKeyUtil.goodsEnableKey(quantity.getGoodsId()));
            } else if (QuantityType.actual.equals(quantity.getQuantityType())) {
                keys.add(StockCacheKeyUtil.goodsActualKey(quantity.getGoodsId()));
            }
            values.add("" + quantity.getQuantity());


            skuIdList.add(quantity.getSkuId());
            goodsIdList.add(quantity.getGoodsId());
        }

        RedisScript<Boolean> redisScript = getRedisScript();
        Boolean result = stringRedisTemplate.execute(redisScript, keys, values.toArray());

        logger.debug("更新库存：");
        logger.debug(goodsQuantityList.toString());
        logger.debug("更新结果：" + result);

        //如果lua脚本执行成功则记录缓冲区
        if (result) {

            //判断配置文件中设置的商品库存缓冲池是否开启
            if (javashopConfig.isStock()) {

                //是否需要同步数据库
                boolean needSync = getSkuPool().oneTime(skuIdList);
                getGoodsPool().oneTime(goodsIdList);

                logger.debug("是否需要同步数据库:" + needSync);
                logger.debug(getSkuPool().toString());

                //如果开启了缓冲池，并且缓冲区已经饱和，则同步数据库
                if (needSync) {
                    syncDataBase();
                }
            } else {
                //如果未开启缓冲池，则实时同步商品数据库中的库存数据
                syncDataBase(skuIdList, goodsIdList);
            }

        }


        return result;
    }

    @Override
    public void syncDataBase() {

        //获取同步的skuid 和goodsid
        List<Long> skuIdList = getSkuPool().getTargetList();
        List<Long> goodsIdList = getGoodsPool().getTargetList();

        logger.debug("goodsIdList is:");
        logger.debug(goodsIdList.toString());

        //判断要同步的goods和sku集合是否有值
        if (skuIdList.size() != 0 && goodsIdList.size() != 0) {
            //同步数据库
            syncDataBase(skuIdList, goodsIdList);
        }

        //重置缓冲池
        getSkuPool().reset();
        getGoodsPool().reset();
    }

    @Override
    public Map<String, Integer> fillCacheFromDB(Long skuId) {
        Map<String, Integer> map = this.goodsSkuMapper.queryQuantity(skuId);
        Integer enableNum = map.get("enable_quantity");
        Integer actualNum = map.get("quantity");

        stringRedisTemplate.opsForValue().set(StockCacheKeyUtil.skuActualKey(skuId), "" + actualNum);
        stringRedisTemplate.opsForValue().set(StockCacheKeyUtil.skuEnableKey(skuId), "" + enableNum);
        return map;
    }


    /**
     * 同步数据库中的库存
     *
     * @param skuIdList   需要同步的skuid
     * @param goodsIdList 需要同步的goodsid
     */
    private void syncDataBase(List<Long> skuIdList, List<Long> goodsIdList) {

        //批量获取sku的库存
        List skuKeys = StockCacheKeyUtil.skuKeys(skuIdList);
        List<String> skuQuantityList = stringRedisTemplate.opsForValue().multiGet(skuKeys);


        int i = 0;

        //形成批量更新sku的list
        for (Long skuId : skuIdList) {

            new UpdateChainWrapper<>(goodsSkuMapper)
                    .set("enable_quantity", skuQuantityList.get(i))
                    .set("quantity", skuQuantityList.get(i + 1))
                    .eq("sku_id", skuId)
                    .update();

            i = i + 2;
        }

        //批量获取商品的库存
        List goodsKeys = createGoodsKeys(goodsIdList);
        List<String> goodsQuantityList = stringRedisTemplate.opsForValue().multiGet(goodsKeys);

        i = 0;

        //形成批量更新goods的list
        for (Long goodsId : goodsIdList) {

            new UpdateChainWrapper<>(goodsMapper)
                    .set("enable_quantity", goodsQuantityList.get(i))
                    .set("quantity", goodsQuantityList.get(i + 1))
                    .eq("goods_id", goodsId)
                    .update();

            i = i + 2;
        }

    }


    /**
     * 生成批量获取goods库存的keys
     *
     * @param goodsIdList
     * @return
     */
    private List createGoodsKeys(List<Long> goodsIdList) {
        List keys = new ArrayList();
        for (Long goodsId : goodsIdList) {
            keys.add(StockCacheKeyUtil.goodsEnableKey(goodsId));
            keys.add(StockCacheKeyUtil.goodsActualKey(goodsId));
        }
        return keys;
    }


    public static void main(String[] args) {
        System.out.println(QuantityType.enable);
    }

}
