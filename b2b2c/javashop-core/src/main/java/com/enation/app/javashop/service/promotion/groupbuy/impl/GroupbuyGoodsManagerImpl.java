package com.enation.app.javashop.service.promotion.groupbuy.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.client.goods.GoodsClient;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.promotion.PromotionGoodsMapper;
import com.enation.app.javashop.mapper.promotion.groupbuy.GroupbuyGoodsMapper;
import com.enation.app.javashop.model.goods.vo.CacheGoods;
import com.enation.app.javashop.model.goods.vo.GoodsSkuVO;
import com.enation.app.javashop.model.errorcode.PromotionErrorCode;
import com.enation.app.javashop.model.promotion.groupbuy.dos.GroupbuyActiveDO;
import com.enation.app.javashop.model.promotion.groupbuy.dos.GroupbuyGoodsDO;
import com.enation.app.javashop.model.promotion.groupbuy.dos.GroupbuyQuantityLog;
import com.enation.app.javashop.model.promotion.groupbuy.enums.GroupBuyGoodsStatusEnum;
import com.enation.app.javashop.model.promotion.groupbuy.enums.GroupbuyQuantityLogEnum;
import com.enation.app.javashop.model.promotion.groupbuy.vo.GroupbuyGoodsVO;
import com.enation.app.javashop.model.promotion.groupbuy.vo.GroupbuyQueryParam;
import com.enation.app.javashop.model.promotion.tool.dos.PromotionGoodsDO;
import com.enation.app.javashop.service.promotion.groupbuy.GroupbuyGoodsManager;
import com.enation.app.javashop.service.promotion.groupbuy.GroupbuyQuantityLogManager;
import com.enation.app.javashop.service.promotion.groupbuy.GroupbuyActiveManager;
import com.enation.app.javashop.model.promotion.tool.dto.PromotionDTO;
import com.enation.app.javashop.model.promotion.tool.dto.PromotionGoodsDTO;
import com.enation.app.javashop.model.promotion.tool.enums.PromotionTypeEnum;
import com.enation.app.javashop.service.promotion.tool.PromotionGoodsManager;
import com.enation.app.javashop.service.promotion.tool.impl.AbstractPromotionRuleManagerImpl;
import com.enation.app.javashop.model.system.enums.DeleteStatusEnum;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.exception.NoPermissionException;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.security.model.Seller;
import com.enation.app.javashop.framework.util.DateUtil;
import org.redisson.api.RedissonClient;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 团购商品业务类
 *
 * @author Snow
 * @version v7.0.0
 * @since v7.0.0
 * 2018-04-02 16:57:26
 */
@SuppressWarnings("Duplicates")
@Service
public class GroupbuyGoodsManagerImpl extends AbstractPromotionRuleManagerImpl implements GroupbuyGoodsManager {

    @Autowired
    private GroupbuyGoodsMapper groupbuyGoodsMapper;

    @Autowired
    private PromotionGoodsMapper promotionGoodsMapper;

    @Autowired
    private GroupbuyActiveManager groupbuyActiveManager;

    @Autowired
    private GroupbuyQuantityLogManager groupbuyQuantityLogManager;

    @Autowired
    private RedissonClient redisson;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private PromotionGoodsManager promotionGoodsManager;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public WebPage listPage(GroupbuyQueryParam param) {
        //获取当前时间
        Long currTime = DateUtil.getDateline();
        //获取团购商品分页列表数据
        IPage<GroupbuyGoodsVO> iPage = groupbuyGoodsMapper.sellerSelectPageVo(new Page(param.getPage(), param.getPageSize()), currTime, DeleteStatusEnum.NORMAL.value(), param);

        //获取结果集
        List<GroupbuyGoodsVO> groupbuyGoodsVOList = iPage.getRecords();
        //循环结果集并设置相应信息
        for (GroupbuyGoodsVO goodsVO : groupbuyGoodsVOList) {
            switch (goodsVO.getGbStatus()) {
                case 0:
                    goodsVO.setGbStatusText("待审核");
                    break;
                case 1:
                    goodsVO.setGbStatusText("通过审核");
                    break;
                case 2:
                    goodsVO.setGbStatusText("未通过审核");
                    break;
                default:
                    break;
            }
            if (currTime < goodsVO.getStartTime()) {
                //活动没有开始，可以对商品进行修改
                goodsVO.setIsEnable(1);
            } else if (currTime >= goodsVO.getStartTime() && currTime <= goodsVO.getEndTime()) {
                //活动正在进行中，不可以对商品进行修改
                goodsVO.setIsEnable(2);
            } else {
                //活动已经结束
                goodsVO.setIsEnable(0);
            }

        }
        //转换结果集
        WebPage webPage = new WebPage(param.getPage(), iPage.getTotal(), param.getPageSize(), groupbuyGoodsVOList);
        return webPage;
    }

    @Override
    public WebPage listPageByBuyer(GroupbuyQueryParam param) {
        //获取团购商品分页列表数据
        IPage<GroupbuyGoodsVO> iPage = groupbuyGoodsMapper.buyerSelectPageVo(new Page(param.getPage(), param.getPageSize()), GroupBuyGoodsStatusEnum.APPROVED.status(), DeleteStatusEnum.NORMAL.value(), param);
        return PageConvert.convert(iPage);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, RuntimeException.class})
    public GroupbuyGoodsDO add(GroupbuyGoodsDO goodsDO) {
        //获取团购活动信息并校验
        GroupbuyActiveDO activeDO = groupbuyActiveManager.getModel(goodsDO.getActId());
        if (activeDO == null) {
            throw new ServiceException(PromotionErrorCode.E400.code(), "参与的活动不存在");
        }
        //如果团购价格 大于等于商品原价，则抛出异常
        if (goodsDO.getPrice() >= goodsDO.getOriginalPrice()) {
            throw new ServiceException(PromotionErrorCode.E400.code(), "参与活动的商品促销价格不得大于或等于商品原价");
        }
        //校验限购数量是否超过商品总数
        if (goodsDO.getLimitNum() > goodsDO.getGoodsNum()) {
            throw new ServiceException(PromotionErrorCode.E400.code(), "商品限购数量不能大于商品总数");
        }

        //新建查询条件包装器
        QueryWrapper<PromotionGoodsDO> wrapper = new QueryWrapper<>();
        //以促销活动类型为限时抢购为条件查询
        wrapper.eq("promotion_type", PromotionTypeEnum.SECKILL.name());
        //以商品SKU为查询条件
        wrapper.eq("sku_id", goodsDO.getSkuId());
        //按（活动开始时间小于等于当前活动开始时间 并且 活动结束时间大于等于当前活动开始时间）或者（活动开始时间小于等于当前活动结束时间 并且 活动结束时间大于等于当前活动结束时间）
        //或者 （活动开始时间小于等于当前活动开始时间 并且 活动结束时间大于等于当前活动结束时间）或者 活动开始时间大于等于当前活动开始时间 并且 活动结束时间小于等于当前活动结束时间）
        wrapper.and(i -> i.le("start_time", activeDO.getStartTime()).ge("end_time", activeDO.getStartTime())
                .or().le("start_time", activeDO.getEndTime()).ge("end_time", activeDO.getEndTime())
                .or().le("start_time", activeDO.getStartTime()).ge("end_time", activeDO.getEndTime())
                .or().ge("start_time", activeDO.getStartTime()).le("end_time", activeDO.getEndTime()));

        //获取当前商品参与限时抢购活动的数量
        int count = promotionGoodsMapper.selectCount(wrapper);
        if (count > 0) {
            throw new ServiceException(PromotionErrorCode.E400.code(), "该商品已经在重叠的时间段参加了限时抢购活动，不能参加团购活动");
        }

        //新建促销商品集合
        List<PromotionGoodsDTO> goodsDTOList = new ArrayList<>();
        //新建促销商品对象DTO
        PromotionGoodsDTO goodsDTO = new PromotionGoodsDTO();
        //设置商品ID
        goodsDTO.setGoodsId(goodsDO.getGoodsId());
        //设置商品图片
        goodsDTO.setThumbnail(goodsDO.getThumbnail());
        //设置商品名称
        goodsDTO.setGoodsName(goodsDO.getGoodsName());
        //将商品放入集合中
        goodsDTOList.add(goodsDTO);
        //检测活动商品规则
        this.verifyRule(goodsDTOList);

        //设置团购商品审核状态为待审核
        goodsDO.setGbStatus(GroupBuyGoodsStatusEnum.PENDING.status());
        //获取商品sku
        Long skuId = goodsDO.getSkuId();
        //查询存储相关的规格
        GoodsSkuVO sku = goodsClient.getSkuFromCache(skuId);
        //设置团购商品规格信息
        goodsDO.setSpecs(sku.getSpecs());
        //团购商品信息入库
        groupbuyGoodsMapper.insert(goodsDO);
        return goodsDO;
    }

    @Override
    public GroupbuyGoodsDO edit(GroupbuyGoodsDO goodsDO, Long id) {
        //获取团购活动信息
        GroupbuyActiveDO activeDO = groupbuyActiveManager.getModel(goodsDO.getActId());
        //获取当前时间并校验团购活动是否已经开始
        long currTime = DateUtil.getDateline();
        if (currTime > activeDO.getStartTime() && currTime < activeDO.getEndTime()) {
            throw new ServiceException(PromotionErrorCode.E408.code(), "团购活动已经开始，不允许修改团购商品信息");
        }

        //新建促销商品集合
        List<PromotionGoodsDTO> goodsDTOList = new ArrayList<>();
        //新建促销商品对象DTO
        PromotionGoodsDTO goodsDTO = new PromotionGoodsDTO();
        //设置商品ID
        goodsDTO.setGoodsId(goodsDO.getGoodsId());
        //设置商品图片
        goodsDTO.setThumbnail(goodsDO.getThumbnail());
        //设置商品名称
        goodsDTO.setGoodsName(goodsDO.getGoodsName());
        //将商品放入集合中
        goodsDTOList.add(goodsDTO);
        //检测活动商品规则
        this.verifyRule(goodsDTOList);

        //如果团购价格 大于等于商品原价，则抛出异常
        if (goodsDO.getPrice() >= goodsDO.getOriginalPrice()) {
            throw new ServiceException(PromotionErrorCode.E400.code(), "参与活动的商品促销价格不得大于或等于商品原价");
        }
        //校验限购数量是否超过商品总数
        if (goodsDO.getLimitNum() > goodsDO.getGoodsNum()) {
            throw new ServiceException(PromotionErrorCode.E400.code(), "商品限购数量不能大于商品总数");
        }

        //获取当前登录的商家店铺信息
        Seller seller = UserContext.getSeller();
        //设置商家店铺ID
        goodsDO.setSellerId(seller.getSellerId());
        //将团购商品状态重置为待审核
        goodsDO.setGbStatus(GroupBuyGoodsStatusEnum.PENDING.status());
        //设置团购活动ID
        goodsDO.setGbId(id);
        //修改团购活动商品信息
        groupbuyGoodsMapper.updateById(goodsDO);

        //删除促销活动商品信息
        this.promotionGoodsManager.delete(goodsDTO.getGoodsId(), goodsDO.getActId(), PromotionTypeEnum.GROUPBUY.name());
        return goodsDO;
    }

    @Override
    public void delete(Long id) {
        //获取团购商品信息
        GroupbuyGoodsVO groupbuyGoods = this.getModel(id);
        //获取商品ID
        Long goodsId = groupbuyGoods.getGoodsId();
        //删除活动商品表中的团购信息
        this.promotionGoodsManager.delete(goodsId, groupbuyGoods.getActId(), PromotionTypeEnum.GROUPBUY.name());
        //删除团购商品信息
        groupbuyGoodsMapper.deleteById(id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, RuntimeException.class})
    public void deleteGoods(List<Long> delSkuIds) {
        //根据商品SKU删除团购商品信息
        groupbuyGoodsMapper.delete(new QueryWrapper<GroupbuyGoodsDO>().in("sku_id", delSkuIds));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, RuntimeException.class})
    public GroupbuyGoodsVO getModel(Long gbId) {
        //根据团购商品ID获取团购商品信息
        GroupbuyGoodsVO groupbuyGoodsVO = groupbuyGoodsMapper.selectGroupbuyGoodsVo(gbId);

        //如果团购商品信息不为空并且商品sku不为空
        if (groupbuyGoodsVO != null && groupbuyGoodsVO.getSkuId() != null) {
            //根据商品skuID获取商品SKU信息
            GoodsSkuVO skuVO = goodsClient.getSku(groupbuyGoodsVO.getSkuId());
            //设置团购商品的可用库存
            groupbuyGoodsVO.setEnableQuantity(skuVO.getEnableQuantity());
            //设置团购商品的实际库存
            groupbuyGoodsVO.setQuantity(skuVO.getQuantity());
        } else {
            //根据商品ID获取商品信息
            CacheGoods cacheGoods = goodsClient.getFromCache(groupbuyGoodsVO.getGoodsId());
            //设置团购商品的可用库存
            groupbuyGoodsVO.setEnableQuantity(cacheGoods.getEnableQuantity());
            //设置团购商品的实际库存
            groupbuyGoodsVO.setQuantity(cacheGoods.getQuantity());
        }

        return groupbuyGoodsVO;
    }

    @Override
    public GroupbuyGoodsDO getModel(Long actId, Long goodsId) {
        //新建查询条件包装器
        QueryWrapper<GroupbuyGoodsDO> wrapper = new QueryWrapper<>();
        //以促销活动ID和商品ID为查询条件
        wrapper.eq("act_id", actId).eq("goods_id", goodsId);
        //获取查询结果
        GroupbuyGoodsDO groupbuyGoodsDO = groupbuyGoodsMapper.selectOne(wrapper);
        return groupbuyGoodsDO;
    }

    @Override
    public GroupbuyGoodsDO getModel(Long actId, Long goodsId, Long skuId) {
        //新建查询条件包装器
        QueryWrapper<GroupbuyGoodsDO> wrapper = new QueryWrapper<>();
        //以促销活动ID、商品ID和商品skuID为查询条件
        wrapper.eq("act_id", actId).eq("goods_id", goodsId).eq("sku_id", skuId);
        //获取查询结果
        GroupbuyGoodsDO groupbuyGoodsDO = groupbuyGoodsMapper.selectOne(wrapper);
        return groupbuyGoodsDO;
    }


    @Override
    public void verifyAuth(Long id) {
        //根据团购商品ID获取团购商品信息
        GroupbuyGoodsDO groupbuyGoodsDO = this.getModel(id);
        //获取当前登录的商家店铺信息
        Seller seller = UserContext.getSeller();
        //权限校验
        if (groupbuyGoodsDO == null || !groupbuyGoodsDO.getSellerId().equals(seller.getSellerId())) {
            throw new NoPermissionException("无权操作");
        }
    }


    @Override
    public void updateStatus(Long gbId, Integer status) {
        //新建团购商品对象
        GroupbuyGoodsDO goodsDO = new GroupbuyGoodsDO();
        //设置团购商品状态
        goodsDO.setGbStatus(status);
        //设置团购商品ID
        goodsDO.setGbId(gbId);
        //修改团购商品状态
        groupbuyGoodsMapper.updateById(goodsDO);
    }


    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public boolean cutQuantity(String orderSn, List<PromotionDTO> promotionDTOList) {
        //循环促销活动商品集合
        for (PromotionDTO promotionDTO : promotionDTOList) {
            //获取商品数量
            int num = promotionDTO.getNum();
            //获取商品ID
            long goodsId = promotionDTO.getGoodsId();
            //获取活动ID
            long actId = promotionDTO.getActId();

            try {
                //新建修改条件包装器
                UpdateWrapper<GroupbuyGoodsDO> wrapper = new UpdateWrapper<>();
                //设置修改团购购买数量
                String setSql = "buy_num = buy_num + " + num + ",goods_num = goods_num - " + num;
                wrapper.setSql(setSql);
                //以商品id、活动id和商品数量大于当前数量为修改条件
                wrapper.eq("goods_id", goodsId).eq("act_id", actId).ge("goods_num", num);
                String sql = "update es_groupbuy_goods set buy_num=buy_num+?,goods_num=goods_num-? where goods_id=? and act_id=? and goods_num >=?";
                //修改团购购买数量
                int rowNum = groupbuyGoodsMapper.update(new GroupbuyGoodsDO(), wrapper);
                //判断是否执行成功
                if (rowNum <= 0) {
                    return false;
                } else {
                    //修改成功，记录日志并清空缓存
                    logAndCleanCache(promotionDTO, orderSn);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean renewBuyNum(Long goodId, Integer num, Long skuId) {
        UpdateChainWrapper<GroupbuyGoodsDO> groupbuyGoodsDOUpdateChainWrapper = new UpdateChainWrapper<>(groupbuyGoodsMapper);
        boolean update = groupbuyGoodsDOUpdateChainWrapper.setSql("buy_num = buy_num + " + num)
                .eq("goods_id", goodId).eq("sku_id", skuId).update();
        return update;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, RuntimeException.class})
    public void addQuantity(String orderSn) {
        //还原团购商品库存并获取团购商品库存日志集合
        List<GroupbuyQuantityLog> logs = groupbuyQuantityLogManager.rollbackReduce(orderSn);
        //循环结果集
        for (GroupbuyQuantityLog log : logs) {
            //获取库存数量
            int num = log.getQuantity();
            //获取商品ID
            long goodsId = log.getGoodsId();
            //获取团购活动ID
            long actId = log.getGbId();
            //新建修改条件包装器
            UpdateWrapper<GroupbuyGoodsDO> wrapper = new UpdateWrapper<>();
            //设置修改团购购买数量
            String setSql = "buy_num = buy_num - " + num + ",goods_num = goods_num + " + num;
            wrapper.setSql(setSql);
            //以商品id、活动id为修改条件
            wrapper.eq("goods_id", goodsId).eq("act_id", actId);
            //修改操作
            groupbuyGoodsMapper.update(new GroupbuyGoodsDO(), wrapper);
        }
    }

    @Override
    public GroupbuyGoodsVO getModelAndQuantity(Long id) {
        //获取团购商品信息
        GroupbuyGoodsDO groupbuyGoods = this.getModel(id);
        //如果团购商品信息不为空
        if (groupbuyGoods != null) {
            //新建团购商品对象vo
            GroupbuyGoodsVO res = new GroupbuyGoodsVO();
            //复制相关属性值
            BeanUtils.copyProperties(groupbuyGoods, res);
            //从缓存中获取商品信息
            CacheGoods goods = goodsClient.getFromCache(groupbuyGoods.getGoodsId());
            //设置团购商品可用库存
            res.setEnableQuantity(goods.getEnableQuantity());
            //设置团购商品实际库存
            res.setQuantity(goods.getQuantity());
            return res;
        }
        return null;
    }

    @Override
    public void updateGoodsInfo(Long[] goodsIds) {
        //如果商品ID组不为空
        if (goodsIds == null) {
            return;
        }
        //获取商品信息集合
        List<Map<String, Object>> result = goodsClient.getGoods(goodsIds);
        //如果商品信息集合不为空
        if (result == null || result.isEmpty()) {
            return;
        }
        //循环集合并修改相关信息
        for (Map<String, Object> map : result) {
            //新建修改条件包装器
            UpdateWrapper<GroupbuyGoodsDO> wrapper = new UpdateWrapper<>();
            //修改商品名称和商品原价
            wrapper.set("goods_name", map.get("goods_name")).set("original_price", map.get("original_price"));
            //以商品ID为修改条件
            wrapper.eq("goods_id", map.get("goods_id"));
            //修改操作
            groupbuyGoodsMapper.update(new GroupbuyGoodsDO(), wrapper);
        }
    }

    @Override
    public void rollbackStock(List<PromotionDTO> promotionDTOList, String orderSn) {
        //循环促销活动信息
        for (PromotionDTO promotionDTO : promotionDTOList) {
            //获取购买数量
            int num = promotionDTO.getNum();
            //获取商品ID
            long goodsId = promotionDTO.getGoodsId();
            //获取促销活动ID
            long actId = promotionDTO.getActId();

            //新建修改条件包装器
            UpdateWrapper<GroupbuyGoodsDO> wrapper = new UpdateWrapper<>();
            //设置修改团购购买数量
            String setSql = "buy_num = buy_num - " + num + ",goods_num = goods_num + " + num;
            wrapper.setSql(setSql);
            //以商品id、活动id为修改条件
            wrapper.eq("goods_id", goodsId).eq("act_id", actId);
            //修改操作
            groupbuyGoodsMapper.update(new GroupbuyGoodsDO(), wrapper);
            //记录日志并清空缓存
            logAndCleanCache(promotionDTO, orderSn);

        }
    }

    /**
     * 记录日志并清空缓存
     *
     * @param promotionDTO
     * @param orderSn
     */
    private void logAndCleanCache(PromotionDTO promotionDTO, String orderSn) {
        GroupbuyQuantityLog groupbuyQuantityLog = new GroupbuyQuantityLog();
        groupbuyQuantityLog.setOpTime(DateUtil.getDateline());
        groupbuyQuantityLog.setQuantity(promotionDTO.getNum());
        groupbuyQuantityLog.setReason("团购销售");
        groupbuyQuantityLog.setGbId(promotionDTO.getActId());
        groupbuyQuantityLog.setLogType(GroupbuyQuantityLogEnum.BUY.name());
        groupbuyQuantityLog.setOrderSn(orderSn);
        groupbuyQuantityLog.setGoodsId(promotionDTO.getGoodsId());
        groupbuyQuantityLogManager.add(groupbuyQuantityLog);
        this.promotionGoodsManager.reputCache(promotionDTO.getGoodsId());
    }


}
