package com.enation.app.javashop.service.promotion.seckill.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.client.goods.GoodsClient;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.promotion.PromotionGoodsMapper;
import com.enation.app.javashop.mapper.promotion.seckill.SeckillApplyMapper;
import com.enation.app.javashop.model.goods.vo.CacheGoods;
import com.enation.app.javashop.model.goods.vo.GoodsSkuVO;
import com.enation.app.javashop.model.errorcode.PromotionErrorCode;
import com.enation.app.javashop.model.promotion.seckill.dos.SeckillApplyDO;
import com.enation.app.javashop.model.promotion.seckill.dto.SeckillQueryParam;
import com.enation.app.javashop.model.promotion.seckill.enums.SeckillGoodsApplyStatusEnum;
import com.enation.app.javashop.model.promotion.seckill.vo.SeckillGoodsVO;
import com.enation.app.javashop.model.promotion.seckill.vo.SeckillVO;
import com.enation.app.javashop.service.promotion.seckill.SeckillGoodsManager;
import com.enation.app.javashop.service.promotion.seckill.SeckillManager;
import com.enation.app.javashop.model.promotion.tool.dto.PromotionDTO;
import com.enation.app.javashop.service.promotion.tool.impl.AbstractPromotionRuleManagerImpl;
import com.enation.app.javashop.service.promotion.tool.support.PromotionCacheKeys;
import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.framework.util.SqlUtil;
import com.enation.app.javashop.framework.util.StringUtil;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.locks.Lock;

/**
 * 限时抢购申请业务类
 *
 * @author Snow
 * @version v7.0.0
 * @since v7.0.0
 * 2018-04-02 17:30:09
 */
@Service
public class SeckillGoodsManagerImpl extends AbstractPromotionRuleManagerImpl implements SeckillGoodsManager {

    @Autowired
    private SeckillApplyMapper seckillApplyMapper;

    @Autowired
    private PromotionGoodsMapper promotionGoodsMapper;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SeckillManager seckillManager;

    @Autowired
    private Cache cache;

    @Autowired
    private RedissonClient redisson;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public WebPage list(SeckillQueryParam queryParam) {

        QueryWrapper<SeckillApplyDO> queryWrapper = new QueryWrapper<SeckillApplyDO>()
                //拼接限时抢购活动ID查询条件
                .eq("seckill_id", queryParam.getSeckillId())
                //拼接商家id查询条件
                .eq(queryParam.getSellerId() != null && queryParam.getSellerId() != 0, "seller_id", queryParam.getSellerId())
                //拼接商品名称查询条件
                .like(StringUtil.notEmpty(queryParam.getGoodsName()), "goods_name", queryParam.getGoodsName())
                //拼接申请状态查询条件
                .eq(StringUtil.notEmpty(queryParam.getStatus()), "status", queryParam.getStatus())
                //按申请id倒序
                .orderByDesc("apply_id");

        IPage iPage = seckillApplyMapper.selectSeckillApplyVOPage(new Page(queryParam.getPageNo(), queryParam.getPageSize()), queryWrapper);

        return PageConvert.convert(iPage);
    }


    @Override
    public void delete(Long id) {

        seckillApplyMapper.deleteById(id);
    }

    @Override
    public SeckillApplyDO getModel(Long id) {

        return seckillApplyMapper.selectById(id);
    }


    @Override
    @Transactional(value = "tradeTransactionManager",propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, ServiceException.class})
    public void addApply(List<SeckillApplyDO> list) {

        Long sellerId = list.get(0).getSellerId();
        String sellerName = list.get(0).getShopName();

        SeckillVO seckillVO = this.seckillManager.getModel(list.get(0).getSeckillId());
        String sellerIds = seckillVO.getSellerIds();

        //如果是空的则不需要进行判断重复参与
        if (!StringUtil.isEmpty(sellerIds)) {
            String[] sellerIdSplit = sellerIds.split(",");
            if (Arrays.asList(sellerIdSplit).contains(sellerId + "")) {
                throw new ServiceException(PromotionErrorCode.E402.code(), "您已参与此活动，无法重复参与");
            }
        }
        for (SeckillApplyDO seckillApplyDO : list) {
            Long skuId = seckillApplyDO.getSkuId();
            //查询商品
            GoodsSkuVO sku = goodsClient.getSkuFromCache(skuId);
            //判断参加活动的数量和库存数量
            if (seckillApplyDO.getSoldQuantity() > sku.getEnableQuantity()) {
                throw new ServiceException(PromotionErrorCode.E402.code(), seckillApplyDO.getGoodsName() + ",此商品库存不足");
            }

            /**
             * *************两种情况：******************
             * 团购时间段：      |________________|
             * 秒杀时间段：  |_____|           |_______|
             *
             * ************第三种情况：******************
             * 团购时间段：        |______|
             * 秒杀时间段：   |________________|
             *
             * ************第四种情况：******************
             * 团购时间段：   |________________|
             * 秒杀时间段：        |______|
             *
             */
            //这个商品的开始时间计算要用他参与的时间段来计算，结束时间是当天晚上23：59：59
            String date = DateUtil.toString(seckillVO.getStartDay(), "yyyy-MM-dd");
            long startTime = DateUtil.getDateline(date + " " + seckillApplyDO.getTimeLine() + ":00:00", "yyyy-MM-dd HH:mm:ss");
            long endTime = DateUtil.getDateline(date + " 23:59:59", "yyyy-MM-dd HH:mm:ss");

            int count = promotionGoodsMapper.selectCountByTime(skuId, startTime, endTime);

            if (count > 0) {
                throw new ServiceException(PromotionErrorCode.E400.code(), "商品[" + sku.getGoodsName() + "]已经在重叠的时间段参加了团购活动，不能参加限时抢购活动");
            }


            //商品的原始价格
            seckillApplyDO.setOriginalPrice(sku.getPrice());
            seckillApplyDO.setSellerId(sellerId);
            seckillApplyDO.setShopName(sellerName);
            seckillApplyDO.setStatus(SeckillGoodsApplyStatusEnum.APPLY.name());
            seckillApplyDO.setSalesNum(0);
            seckillApplyDO.setSpecs(sku.getSpecs());
            seckillApplyMapper.insert(seckillApplyDO);
            long applyId = seckillApplyDO.getApplyId();
            seckillApplyDO.setApplyId(applyId);
        }

        this.seckillManager.sellerApply(sellerId, seckillVO.getSeckillId());

    }



    /**
     * 回滚秒杀库存
     * @param goodsList
     */
    private void innerRollbackStock(List<SeckillGoodsVO> goodsList) {
        for (SeckillGoodsVO goodsVO : goodsList) {
            int num = goodsVO.getSoldNum();

            new UpdateChainWrapper<>(seckillApplyMapper)
                    //设置售空数量
                    .setSql("sold_quantity = sold_quantity + " + 0)
                    //设置已售数量
                    .setSql("sales_num = sales_num - " + num)
                    //拼接商品id修改条件
                    .eq("goods_id", goodsVO.getGoodsId())
                    //拼接秒杀活动ID修改条件
                    .eq("seckill_id", goodsVO.getSeckillId())
                    //提交修改
                    .update();
        }

    }



    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, ServiceException.class})
    public void rollbackStock(List<PromotionDTO> promotionDTOList) {

        List<SeckillGoodsVO> lockedList = new ArrayList<>();

        //遍历活动与商品关系的类
        for (PromotionDTO promotionDTO : promotionDTOList) {

            Map<Integer, List<SeckillGoodsVO>> map = this.getSeckillGoodsList();

            for (Map.Entry<Integer, List<SeckillGoodsVO>> entry : map.entrySet()) {

                List<SeckillGoodsVO> seckillGoodsDTOList = entry.getValue();
                for (SeckillGoodsVO goodsVO : seckillGoodsDTOList) {

                    if (goodsVO.getGoodsId().equals(promotionDTO.getGoodsId())) {
                        //用户购买的数量
                        int num = promotionDTO.getNum();
                        goodsVO.setSoldNum(num);
                        lockedList.add(goodsVO);

                    }
                }
            }
        }

        this.cache.remove(PromotionCacheKeys.getSeckillKey(DateUtil.toString(DateUtil.getDateline(), "yyyyMMdd")));

        innerRollbackStock(lockedList);

    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, ServiceException.class})
    public void deleteSeckillGoods(List<Long> delSkuIds) {

        List<Object> term = new ArrayList<>();
        String inStr = SqlUtil.getInSql(delSkuIds.toArray(new Long[delSkuIds.size()]),term);
        term.add(DateUtil.startOfTodDay());
        //删除限时抢购已经开始和未开始的商品
        seckillApplyMapper.delete(new QueryWrapper<SeckillApplyDO>().
                //拼接sku删除条件
                in("sku_id", delSkuIds)
                //拼接活动开始日期删除条件
                .ge("start_day", DateUtil.startOfTodDay())
        );

        //移除缓存中的数据
        String redisKey = getRedisKey(DateUtil.getDateline());

        this.cache.remove(redisKey);

    }

    @Override
    public void deleteBySeckillId(Long seckillId) {

        seckillApplyMapper.delete(new QueryWrapper<SeckillApplyDO>()
                //根据秒杀活动id删除
                .eq("seckill_id", seckillId)
        );
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, ServiceException.class})
    public boolean addSoldNum(List<PromotionDTO> promotionDTOList) {

        List<SeckillGoodsVO> lockedList = new ArrayList<>();

        boolean result = true;
        //遍历活动与商品关系的类
        for (PromotionDTO promotionDTO : promotionDTOList) {

            try {
                Map<Integer, List<SeckillGoodsVO>> map = this.getSeckillGoodsList();

                for (Map.Entry<Integer, List<SeckillGoodsVO>> entry : map.entrySet()) {

                    List<SeckillGoodsVO> seckillGoodsDTOList = entry.getValue();

                    for (SeckillGoodsVO goodsVO : seckillGoodsDTOList) {

                        if (goodsVO.getGoodsId().equals(promotionDTO.getGoodsId())) {
                            //用户购买的数量
                            int num = promotionDTO.getNum();

                            boolean updateResult = new UpdateChainWrapper<>(seckillApplyMapper)
                                    //设置售空数量
                                    .setSql("sold_quantity = sold_quantity - " + num)
                                    //设置已售数量
                                    .setSql("sales_num = sales_num + " + num)
                                    //拼接商品id修改条件
                                    .eq("goods_id", goodsVO.getGoodsId())
                                    //拼接秒杀活动id修改条件
                                    .eq("seckill_id", goodsVO.getSeckillId())
                                    //拼接售空数量修改条件
                                    .ge("sold_quantity", num)
                                    //提交修改
                                    .update();
                            logger.debug("num is " + num + ";goodsid: " + goodsVO.getGoodsId() + " ; seckill: " + goodsVO.getSeckillId());

                            //库存不足
                            if (!updateResult) {
                                logger.debug("秒杀更新失败");
                                result = false;
                                break;

                            } else {
                                result = true;

                                goodsVO.setSoldNum(num);

                                //记录此商品已经锁成功,以便回滚
                                lockedList.add(goodsVO);
                                logger.debug("秒杀更新成功");

                            }
                        }

                    }

                    //发生锁库失败，则break;
                    if (!result) {
                        break;
                    }

                }

                this.cache.remove(PromotionCacheKeys.getSeckillKey(DateUtil.toString(DateUtil.getDateline(), "yyyyMMdd")));

            } catch (Exception e) {
                e.printStackTrace();
                result = false;
            }
        }

        //如果有锁库失败，则回滚已经更新成功的
        if (!result) {
            innerRollbackStock(lockedList);
        }
        return result;
    }



    @Override
    public Map<Integer, List<SeckillGoodsVO>> getSeckillGoodsList() {

        //读取今天的时间
        long today = DateUtil.startOfTodDay();
        //从缓存读取限时抢购的活动的商品
        String redisKey = getRedisKey(DateUtil.getDateline());
        Map<Integer, List<SeckillGoodsVO>> map = this.cache.getHash(redisKey);

        //如果redis中没有则从数据取
        if (map == null || map.isEmpty()) {

            //读取当天正在进行的活限时抢购活动的商品
            List<SeckillApplyDO> list = new QueryChainWrapper<>(seckillApplyMapper)
                    //拼接活动开始日期查询条件
                    .eq("start_day", today)
                    //拼接申请状态查询条件
                    .eq("status", SeckillGoodsApplyStatusEnum.PASS.name())
                    //列表查询
                    .list();

            //遍历所有的商品，并保存所有不同的时刻
            for (SeckillApplyDO applyDO : list) {
                map.put(applyDO.getTimeLine(), new ArrayList());
            }

            //遍历所有的时刻，并为每个时刻赋值商品
            for (SeckillApplyDO applyDO : list) {
                for (Map.Entry<Integer, List<SeckillGoodsVO>> entry : map.entrySet()) {
                    if (applyDO.getTimeLine().equals(entry.getKey())) {

                        //活动开始日期（天）的时间戳
                        long startDay = applyDO.getStartDay();
                        //形成 2018090910 这样的串
                        String timeStr = DateUtil.toString(startDay, "yyyyMMdd") + applyDO.getTimeLine();
                        //得到开始日期的时间戳
                        long startTime = DateUtil.getDateline(timeStr, "yyyyMMddHH");

                        //查询商品
                        CacheGoods goods = goodsClient.getFromCache(applyDO.getGoodsId());
                        List<GoodsSkuVO>  skuVOs = goodsClient.listByGoodsId(applyDO.getGoodsId());
                        for (GoodsSkuVO skuVO:skuVOs) {
                            if(!skuVO.getSkuId().equals(applyDO.getSkuId())){
                                continue;
                            }
                            SeckillGoodsVO seckillGoods = new SeckillGoodsVO();
                            seckillGoods.setGoodsId(goods.getGoodsId());
                            seckillGoods.setGoodsName(goods.getGoodsName());
                            seckillGoods.setOriginalPrice(skuVO.getPrice());
                            seckillGoods.setSeckillPrice(applyDO.getPrice());
                            seckillGoods.setSoldNum(applyDO.getSalesNum());
                            seckillGoods.setSoldQuantity(applyDO.getSoldQuantity());
                            seckillGoods.setGoodsImage(goods.getThumbnail());
                            seckillGoods.setStartTime(startTime);
                            seckillGoods.setSeckillId(applyDO.getSeckillId());
                            seckillGoods.setSkuId(skuVO.getSkuId());
                            seckillGoods.setRemainQuantity(applyDO.getSoldQuantity() - applyDO.getSalesNum());

                            if (entry.getValue() == null) {
                                entry.setValue(new ArrayList<>());
                            }
                            entry.getValue().add(seckillGoods);
                        }
                        }

                }
            }

            //压入缓存
            for (Map.Entry<Integer, List<SeckillGoodsVO>> entry : map.entrySet()) {
                this.cache.putHash(redisKey, entry.getKey(), entry.getValue());
            }
        }

        return map;
    }

    public static void main(String[] args) {
        //活动开始日期（天）的时间戳
        long startDay = DateUtil.getDateline("20180101000000", "yyyyMMddHHMMss");
        //形成 2018090910 这样的串
        String timeStr = DateUtil.toString(startDay, "yyyyMMdd") + 10;
        //得到开始日期的时间戳
        long startTime = DateUtil.getDateline(timeStr, "yyyyMMddHH");

        String str = DateUtil.toString(startTime, "yyyyMMddHH");
        System.out.println(timeStr);
        System.out.println(str);


    }


    @Override
    public void addRedis(Long startTime, Integer rangeTime, SeckillGoodsVO goodsVO) {
        //得到活动缓存的key
        String redisKey = getRedisKey(startTime);
        //查询活动商品
        List<SeckillGoodsVO> list = (List<SeckillGoodsVO>) this.cache.getHash(redisKey, rangeTime);
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(goodsVO);

        //压入缓存
        this.cache.putHash(redisKey, rangeTime, list);
    }


    @Override
    public List getSeckillGoodsList(Integer rangeTime, Long pageNo, Long pageSize) {

        //读取限时抢购活动商品
        Map<Integer, List<SeckillGoodsVO>> map = this.getSeckillGoodsList();
        List<SeckillGoodsVO> totalList = new ArrayList();

        //遍历活动商品
        for (Map.Entry<Integer, List<SeckillGoodsVO>> entry : map.entrySet()) {
            if (rangeTime.intValue() == entry.getKey().intValue()) {
                totalList = entry.getValue();
                break;
            }
        }

        //redis不能分页 手动根据分页读取数据
        List<SeckillGoodsVO> list = new ArrayList<SeckillGoodsVO>();
        long currIdx = (pageNo > 1 ? (pageNo - 1) * pageSize : 0);
        for (int i = 0; i < pageSize && i < totalList.size() - currIdx; i++) {
            long index = currIdx + i;

            SeckillGoodsVO goods = totalList.get((int)index);
            list.add(goods);
        }

        return list;
    }

    @Override
    public List<SeckillApplyDO> getGoodsList(Long seckillId, String status) {

        List<SeckillApplyDO> goodsList = new QueryChainWrapper<>(seckillApplyMapper)
                //拼接活动id查询条件
                .eq("seckill_id", seckillId)
                //拼接申请状态查询条件
                .eq("status", status)
                //列表查询
                .list();

        return goodsList;
    }

    private Lock getGoodsQuantityLock(Integer gbId) {
        RLock lock = redisson.getLock("seckill_goods_quantity_lock_" + gbId);
        return lock;
    }


    /**
     * 获取限时抢购key
     * @param dateline
     * @return
     */
    private String getRedisKey(long dateline) {
        return PromotionCacheKeys.getSeckillKey(DateUtil.toString(dateline, "yyyyMMdd"));
    }


}
