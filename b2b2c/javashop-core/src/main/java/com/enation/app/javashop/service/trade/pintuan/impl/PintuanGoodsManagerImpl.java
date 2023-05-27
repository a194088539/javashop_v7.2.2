package com.enation.app.javashop.service.trade.pintuan.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.client.goods.GoodsClient;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.exception.ResourceNotFoundException;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import com.enation.app.javashop.framework.util.*;
import com.enation.app.javashop.mapper.trade.pintuan.PintuanGoodsMapper;
import com.enation.app.javashop.model.goods.vo.CacheGoods;
import com.enation.app.javashop.model.goods.vo.GoodsSkuVO;
import com.enation.app.javashop.model.promotion.pintuan.PinTuanGoodsVO;
import com.enation.app.javashop.model.promotion.pintuan.Pintuan;
import com.enation.app.javashop.model.promotion.pintuan.PintuanGoodsDO;
import com.enation.app.javashop.model.promotion.pintuan.PintuanGoodsDTO;
import com.enation.app.javashop.model.promotion.tool.enums.PromotionStatusEnum;
import com.enation.app.javashop.model.promotion.tool.enums.PromotionTypeEnum;
import com.enation.app.javashop.model.promotion.tool.vo.PromotionAbnormalGoods;
import com.enation.app.javashop.service.trade.pintuan.PinTuanSearchManager;
import com.enation.app.javashop.service.trade.pintuan.PintuanGoodsManager;
import com.enation.app.javashop.service.trade.pintuan.PintuanManager;
import com.enation.app.javashop.service.trade.pintuan.PintuanScriptManager;
import com.enation.app.javashop.service.trade.pintuan.exception.PintuanErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 拼团商品业务类
 *
 * @author admin
 * @version vv1.0.0
 * @since vv7.1.0
 * 2019-01-22 11:20:56
 */
@Service
public class PintuanGoodsManagerImpl implements PintuanGoodsManager {


    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private PintuanGoodsMapper pintuanGoodsMapper;

    @Autowired
    private PinTuanSearchManager pinTuanSearchManager;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private PintuanManager pintuanManager;

    @Autowired
    private PintuanScriptManager pintuanScriptManager;

    @Override
    public WebPage list(long page, long pageSize) {

        IPage iPage = this.pintuanGoodsMapper.selectPage(new Page<>(page, pageSize), new QueryWrapper<>());

        return PageConvert.convert(iPage);
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PintuanGoodsDO add(PintuanGoodsDO pintuanGoods) {

        this.pintuanGoodsMapper.insert(pintuanGoods);
        logger.debug("将拼团商品" + pintuanGoods + "写入数据库");

        return pintuanGoods;
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void save(Long pintuanId, List<PintuanGoodsDO> pintuanGoodsList) {
        //获取拼团促销活动信息详情
        Pintuan pintuan = pintuanManager.getModel(pintuanId);

        //获取本次操作之前参与拼团活动的商品信息集合
        List<PintuanGoodsDO> oldGoodsList = this.getPintuanGoodsList(pintuanId);


        Long[] skuIds = new Long[pintuanGoodsList.size()];

        for (int i = 0; i < pintuanGoodsList.size(); i++) {
            skuIds[i] = pintuanGoodsList.get(i).getSkuId();
        }
        //拼团促销活动
        List<PromotionAbnormalGoods> promotionAbnormalGoods = this.checkPromotion(skuIds, pintuan.getStartTime(), pintuan.getEndTime(), pintuan.getPromotionId());

        StringBuffer stringBuffer = new StringBuffer();
        if (promotionAbnormalGoods.size() > 0) {
            promotionAbnormalGoods.forEach(pGoods -> {
                stringBuffer.append("商品【" + pGoods.getGoodsName() + "】参与【" + PromotionTypeEnum.valueOf(pGoods.getPromotionType()).getPromotionName() + "】活动,");
                stringBuffer.append("时间冲突【" + DateUtil.toString(pGoods.getStartTime(), "yyyy-MM-dd HH:mm:ss") + "~" + DateUtil.toString(pGoods.getEndTime(), "yyyy-MM-dd HH:mm:ss") + "】;");
            });
            throw new ServiceException(PintuanErrorCode.E5015.code(), stringBuffer.toString());
        }

        this.pintuanGoodsMapper.delete(new QueryWrapper<PintuanGoodsDO>().eq("pintuan_id", pintuan.getPromotionId()));

        pintuanGoodsList.forEach(pintuanGoodsDO -> {

            pintuanGoodsDO.setPintuanId(pintuan.getPromotionId());
            GoodsSkuVO skuVo = goodsClient.getSkuFromCache(pintuanGoodsDO.getSkuId());
            //验证拼团价格和原始价格
            if (pintuanGoodsDO.getSalesPrice() > skuVo.getPrice()) {
                throw new ServiceException(PintuanErrorCode.E5015.code(), skuVo.getGoodsName() + ",此商品的拼团价格不能大于商品销售价格");
            }
            pintuanGoodsDO.setSellerId(skuVo.getSellerId());
            pintuanGoodsDO.setSellerName(skuVo.getSellerName());
            pintuanGoodsDO.setThumbnail(skuVo.getThumbnail());
            pintuanGoodsDO.setSpecs(StringUtil.notEmpty(skuVo.getSpecs()) ? skuVo.getSpecs() : JsonUtil.objectToJson(skuVo.getSpecList()));
            pintuanGoodsDO.setOriginPrice(skuVo.getPrice());
            pintuanGoodsDO.setGoodsId(skuVo.getGoodsId());
            pintuanGoodsDO.setGoodsName(skuVo.getGoodsName());
            pintuanGoodsDO.setLockedQuantity(0);
            pintuanGoodsDO.setSoldQuantity(0);
            pintuanGoodsDO.setSn(skuVo.getSn());
            add(pintuanGoodsDO);

        });

        //如果拼团活动是进行中的状态，则同步商品的索引和脚本信息
        if (pintuan.getStatus().equals(PromotionStatusEnum.UNDERWAY.name())) {
            //同步es
            pinTuanSearchManager.syncIndexByPinTuanId(pintuanId);

            //先将本次操作之前存放在缓存中参与拼团活动的商品脚本信息删除
            this.pintuanScriptManager.deleteCacheScript(pintuanId, oldGoodsList);
            //然后为本次操作添加的活动商品创建脚本信息
            this.pintuanScriptManager.createCacheScript(pintuanId, pintuanGoodsList);
        }


    }


    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PintuanGoodsDO edit(PintuanGoodsDO pintuanGoods, Long id) {
        pintuanGoods.setGoodsId(id);
        this.pintuanGoodsMapper.updateById(pintuanGoods);
        return pintuanGoods;
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(Long id) {
        this.pintuanGoodsMapper.deleteById(id);
    }

    @Override
    public PintuanGoodsDO getModel(Long id) {
        return this.pintuanGoodsMapper.selectById(id);
    }

    /**
     * 获取拼团商品
     *
     * @param pintuanId
     * @param skuId
     * @return
     */
    @Override
    public PintuanGoodsDO getModel(Long pintuanId, Long skuId) {

        return this.pintuanGoodsMapper.selectOne(new QueryWrapper<PintuanGoodsDO>()
                .eq("pintuan_id", pintuanId)
                .eq("sku_id", skuId));
    }


    @Override
    public PinTuanGoodsVO getDetail(Long skuId, Long time) {

        Long now = DateUtil.getDateline();

        if (time == null) {
            time = now;
        }

        List<PinTuanGoodsVO> list = this.pintuanGoodsMapper.getDetail(skuId, now, time);
        PinTuanGoodsVO pinTuanGoodsVO = null;
        if (list.size() > 0) {
            pinTuanGoodsVO = list.get(0);
        }
        if (pinTuanGoodsVO == null) {
            throw new ResourceNotFoundException("skuid为" + skuId + "的拼团商品不存在");
        }

        //计算剩余秒数为结束时间减掉当前时间（秒数）
        long currTime = DateUtil.getDateline();
        pinTuanGoodsVO.setTimeLeft(pinTuanGoodsVO.getEndTime() - currTime);

        return pinTuanGoodsVO;
    }

    @Override
    public List<GoodsSkuVO> skus(Long goodsId, Long pintuanId) {

        CacheGoods cacheGoods = goodsClient.getFromCache(goodsId);
        List<GoodsSkuVO> skuList = cacheGoods.getSkuList();

        List<Map<String, Object>> list = this.pintuanGoodsMapper.selectMaps(new QueryWrapper<PintuanGoodsDO>()
                .select("sku_id")
                .eq("goods_id", goodsId)
                .eq("pintuan_id", pintuanId));

        //将参加了拼团的sku压入到新list中
        List<GoodsSkuVO> newList = new ArrayList<>();
        skuList.forEach(goodsSkuVO -> {
            list.forEach(map -> {
                Long dbSkuId = (Long) map.get("sku_id");
                Long skuId = goodsSkuVO.getSkuId();
                if (skuId.equals(dbSkuId)) {
                    newList.add(goodsSkuVO);
                }
            });
        });

        return newList;
    }

    /**
     * 更新已团数量
     *
     * @param id  拼团商品id
     * @param num 数量
     */
    @Override
    public void addQuantity(Long id, Integer num) {

        this.pintuanGoodsMapper.update(null, new UpdateWrapper<PintuanGoodsDO>()
                .setSql("sold_quantity = sold_quantity + " + num)
                .eq("id", id));
    }

    /**
     * 获取某活动所有商品
     *
     * @param promotionId
     * @return
     */
    @Override
    public List<PinTuanGoodsVO> all(Long promotionId) {
        List<PinTuanGoodsVO> pinTuanGoodsVOS = new ArrayList<>();

        List<PintuanGoodsDO> list = this.pintuanGoodsMapper.selectList(new QueryWrapper<PintuanGoodsDO>().eq("pintuan_id", promotionId));

        list.forEach(pinTuanGoods -> {

            PinTuanGoodsVO pinTuanGoodsVO = new PinTuanGoodsVO();
            BeanUtil.copyProperties(pinTuanGoods,pinTuanGoodsVO);

            Long skuId = pinTuanGoods.getSkuId();
            GoodsSkuVO goodsSkuVO = goodsClient.getSkuFromCache(skuId);
            Integer quantity = 0;
            if (null == goodsSkuVO) {
                logger.error("错误：sku数据为空，相关信息为" + pinTuanGoods.toString());
                // 无法使用break，lambda只能用reburn
                // break;
                return;
            } else {
                quantity = goodsSkuVO.getEnableQuantity();
            }
            pinTuanGoodsVO.setEnableQuantity(quantity);

            pinTuanGoodsVOS.add(pinTuanGoodsVO);

        });

        return pinTuanGoodsVOS;

    }

    @Override
    public List<PintuanGoodsDO> getPintuanGoodsList(Long promotionId) {

        return this.pintuanGoodsMapper.selectList(new QueryWrapper<PintuanGoodsDO>().eq("pintuan_id",promotionId));
    }

    /**
     * 关闭一个活动的促销商品索引
     *
     * @param promotionId
     */
    @Override
    public void delIndex(Long promotionId) {
        pinTuanSearchManager.deleteByPintuanId(promotionId);
    }

    /**
     * 开启一个活动的促销商品索引
     *
     * @param promotionId
     */
    @Override
    public boolean addIndex(Long promotionId) {
        List<PinTuanGoodsVO> pinTuanGoodsVOS = this.all(promotionId);

        boolean hasError = false;

        for (PinTuanGoodsVO pintuanGoods : pinTuanGoodsVOS) {
            boolean result = pinTuanSearchManager.addIndex(pintuanGoods);
            hasError = result && hasError;
        }

        return hasError;

    }


    /**
     * 商品查询
     *
     * @param page        页码
     * @param pageSize    分页大小
     * @param promotionId 促销id
     * @param name        商品名称
     * @return
     */
    @Override
    public WebPage page(Long page, Long pageSize, Long promotionId, String name) {

        IPage<PintuanGoodsDO> iPage = this.pintuanGoodsMapper.selectPage(new Page<>(page, pageSize), new QueryWrapper<PintuanGoodsDO>()
                //如果名称不为空
                .like(!StringUtil.isEmpty(name), "goods_name", name)
                .eq("pintuan_id", promotionId));

        return PageConvert.convert(iPage);
    }

    /**
     * 查询指定时间范围，是否有参与其他活动
     *
     * @param skuIds    SKUid集合
     * @param startTime 开始时间
     * @param endTime   结束时间
     */
    @Override
    public List<PromotionAbnormalGoods> checkPromotion(Long[] skuIds, Long startTime, Long endTime, Long promotionID) {


        //何为冲突
        //（1）
        //                       校验时间
        //                  --》【              】《--
        // ******************************************************************       时间轴
        //                已经存在 活动
        //       --》【              】《--
        // ******************************************************************       时间轴

        //（2）
        //                       校验时间
        //                  --》【              】《--
        // ******************************************************************       时间轴
        //                                       已经存在 活动
        //                              --》【              】《--
        // ******************************************************************       时间轴

        //（3）
        //                       校验时间
        //                  --》【              】《--
        // ******************************************************************       时间轴
        //                       已经存在 活动
        //        --》【                               】《--
        // ******************************************************************       时间轴

        //（4）
        //                       校验时间
        //                  --》【              】《--
        // ******************************************************************       时间轴
        //                       已经存在 活动
        //                      --》【     】《--
        // ******************************************************************       时间轴
        List term = new ArrayList();

        Map map = new HashMap();

        map.put("skuids", Arrays.asList(skuIds));
        map.put("start_time",startTime);
        map.put("end_time",endTime);
        map.put("promotion_id",promotionID);

        //查询时间轴以外的促销活动商品
        List<PintuanGoodsDTO> promotionGoodsDOS = this.pintuanGoodsMapper.queryPromotionGoods(map);

        List<PromotionAbnormalGoods> promotionAbnormalGoods = new ArrayList<>();
        promotionGoodsDOS.forEach(goods -> {
            PromotionAbnormalGoods paGoods = new PromotionAbnormalGoods();
            paGoods.setPromotionType(PromotionTypeEnum.PINTUAN.name());
            paGoods.setGoodsName(goods.getGoodsName());
            paGoods.setGoodsId(goods.getGoodsId());
            paGoods.setEndTime(goods.getEndTime());
            paGoods.setStartTime(goods.getStartTime());
            promotionAbnormalGoods.add(paGoods);
        });
        return promotionAbnormalGoods;
    }

    @Override
    public void deletePinTuanGoods(List<Long> delSkuIds) {

        this.pintuanGoodsMapper.deletePinTuanGoods(delSkuIds,DateUtil.getDateline());
    }
}
