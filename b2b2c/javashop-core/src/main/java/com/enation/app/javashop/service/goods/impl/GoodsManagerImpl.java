package com.enation.app.javashop.service.goods.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.enation.app.javashop.client.member.MemberCommentClient;
import com.enation.app.javashop.client.member.ShopClient;
import com.enation.app.javashop.client.promotion.ExchangeGoodsClient;
import com.enation.app.javashop.client.system.SettingClient;
import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.context.request.ThreadContextHolder;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import com.enation.app.javashop.framework.rabbitmq.MessageSender;
import com.enation.app.javashop.framework.rabbitmq.MqMessage;
import com.enation.app.javashop.framework.redis.transactional.RedisTransactional;
import com.enation.app.javashop.framework.security.model.Seller;
import com.enation.app.javashop.framework.util.CurrencyUtil;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.framework.util.JsonUtil;
import com.enation.app.javashop.framework.util.StringUtil;
import com.enation.app.javashop.mapper.goods.GoodsMapper;
import com.enation.app.javashop.mapper.goods.GoodsSkuMapper;
import com.enation.app.javashop.model.base.CachePrefix;
import com.enation.app.javashop.model.base.CharacterConstant;
import com.enation.app.javashop.model.base.SettingGroup;
import com.enation.app.javashop.model.base.message.GoodsChangeMsg;
import com.enation.app.javashop.model.base.rabbitmq.AmqpExchange;
import com.enation.app.javashop.model.errorcode.GoodsErrorCode;
import com.enation.app.javashop.model.errorcode.PromotionErrorCode;
import com.enation.app.javashop.model.goods.dos.GoodsDO;
import com.enation.app.javashop.model.goods.dos.GoodsGalleryDO;
import com.enation.app.javashop.model.goods.dos.GoodsSkuDO;
import com.enation.app.javashop.model.goods.dto.ExchangeClientDTO;
import com.enation.app.javashop.model.goods.dto.GoodsAuditParam;
import com.enation.app.javashop.model.goods.dto.GoodsDTO;
import com.enation.app.javashop.model.goods.dto.GoodsSettingVO;
import com.enation.app.javashop.model.goods.enums.GoodsOperate;
import com.enation.app.javashop.model.goods.enums.GoodsType;
import com.enation.app.javashop.model.goods.enums.Permission;
import com.enation.app.javashop.model.goods.vo.BackendGoodsVO;
import com.enation.app.javashop.model.goods.vo.OperateAllowable;
import com.enation.app.javashop.model.member.vo.GoodsGrade;
import com.enation.app.javashop.model.promotion.exchange.dos.ExchangeDO;
import com.enation.app.javashop.model.promotion.tool.dto.PromotionGoodsDTO;
import com.enation.app.javashop.model.shop.vo.ShopVO;
import com.enation.app.javashop.model.trade.order.vo.OrderSkuVO;
import com.enation.app.javashop.model.util.sensitiveutil.SensitiveFilter;
import com.enation.app.javashop.service.goods.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品业务类
 *
 * @author fk
 * @version v2.0
 * @since v7.0.0 2018-03-21 11:23:10
 */
@Service
public class GoodsManagerImpl implements GoodsManager {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private GoodsGalleryManager goodsGalleryManager;
    @Autowired
    private GoodsParamsManager goodsParamsManager;
    @Autowired
    private GoodsSkuManager goodsSkuManager;
    @Autowired
    private MessageSender messageSender;
    @Autowired
    private Cache cache;

    @Autowired
    private GoodsQueryManager goodsQueryManager;
    @Autowired
    private SettingClient settingClient;
    @Autowired
    private ExchangeGoodsClient exchangeGoodsClient;
    @Autowired
    private MemberCommentClient memberCommentClient;
    @Autowired
    private ShopClient shopClient;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsSkuMapper goodsSkuMapper;


    @Override
    @Transactional(value = "goodsTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public GoodsDO add(GoodsDTO goodsVo) {
        Seller seller = UserContext.getSeller();
        // 没有规格给这个字段塞0
        goodsVo.setHaveSpec(0);
        //对商品名字进行敏感词检测
        goodsVo.setGoodsName(SensitiveFilter.filter(goodsVo.getGoodsName(), CharacterConstant.WILDCARD_STAR));
        GoodsDO goods = new GoodsDO(goodsVo);
        // 判断是否添加的是积分商品
        if (goodsVo.getExchange() != null && goodsVo.getExchange().getEnableExchange() == 1) {
            if (seller.getSelfOperated() != 1) {
                throw new ServiceException(GoodsErrorCode.E301.code(), "非自营店铺不能添加积分商品");
            }
            goods.setGoodsType(GoodsType.POINT.name());
        } else {
            goods.setGoodsType(GoodsType.NORMAL.name());
        }
        // 判断是否是自营店铺
        goods.setSelfOperated(seller.getSelfOperated() == 1 ? 1 : 0);
        String goodsSettingJson = settingClient.get(SettingGroup.GOODS);

        GoodsSettingVO goodsSetting = JsonUtil.jsonToObject(goodsSettingJson, GoodsSettingVO.class);
        // 判断商品是否需要审核
        goods.setIsAuth(goodsSetting.getMarcketAuth() == 1 ? 0 : 1);
        // 商品状态 是否可用
        goods.setDisabled(1);
        // 商品创建时间
        goods.setCreateTime(DateUtil.getDateline());
        // 商品浏览次数
        goods.setViewCount(0);
        // 商品购买数量
        goods.setBuyCount(0);
        // 评论次数
        goods.setCommentNum(0);
        // 商品评分
        goods.setGrade(100.0);
        // 商品最后更新时间
        goods.setLastModify(DateUtil.getDateline());
        // 商品库存
        goods.setQuantity(goodsVo.getQuantity() == null ? 0 : goodsVo.getQuantity());
        // 可用库存
        goods.setEnableQuantity(goods.getQuantity());
        // 向goods加入图片
        GoodsGalleryDO goodsGalley = goodsGalleryManager
                .getGoodsGallery(goodsVo.getGoodsGalleryList().get(0).getOriginal());
        goods.setOriginal(goodsGalley.getOriginal());
        goods.setBig(goodsGalley.getBig());
        goods.setSmall(goodsGalley.getSmall());
        goods.setThumbnail(goodsGalley.getThumbnail());
        goods.setSellerId(seller.getSellerId());
        goods.setSellerName(seller.getSellerName());
        // 添加商品
        this.goodsMapper.insert(goods);
        // 获取添加商品的商品ID
        Long goodsId = goods.getGoodsId();
        // 添加商品参数
        this.goodsParamsManager.addParams(goodsVo.getGoodsParamsList(), goodsId);
        // 添加商品sku信息，此时商品和sku为一对一
        this.goodsSkuManager.add(null, goods);
        // 添加相册
        this.goodsGalleryManager.add(goodsVo.getGoodsGalleryList(), goodsId);
        // 添加积分换购商品
        if (goods.getGoodsType().equals(GoodsType.POINT.name())) {
            PromotionGoodsDTO goodsDTO = new PromotionGoodsDTO();
            BeanUtils.copyProperties(goods, goodsDTO);
            ExchangeDO exchange = new ExchangeDO();
            BeanUtils.copyProperties(goodsVo.getExchange(), exchange);
            //校验积分兑换的价格不能高于商品销售价
            if (exchange.getExchangeMoney() > goods.getPrice()) {
                throw new ServiceException(GoodsErrorCode.E301.code(), "积分商品价格不能高于商品原价");
            }
            exchangeGoodsClient.add(new ExchangeClientDTO(exchange, goodsDTO));
        }
        // 发送增加商品消息，店铺增加自身商品数量，静态页使用
        GoodsChangeMsg goodsChangeMsg = new GoodsChangeMsg(new Long[]{goods.getGoodsId()},
                GoodsChangeMsg.ADD_OPERATION);
        this.messageSender.send(new MqMessage(AmqpExchange.GOODS_CHANGE, AmqpExchange.GOODS_CHANGE + "_ROUTING", goodsChangeMsg));

        return goods;
    }

    @Override
    @Transactional(value = "goodsTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public GoodsDO edit(GoodsDTO goodsVO, Long id) {

        Seller seller = UserContext.getSeller();
        GoodsDO goodsDO = goodsQueryManager.getModel(id);
        if (goodsDO == null || !goodsDO.getSellerId().equals(seller.getSellerId())) {
            throw new ServiceException(GoodsErrorCode.E301.code(), "没有操作权限");
        }

        goodsVO.setGoodsId(id);
        //对商品名字进行敏感词检测
        goodsVO.setGoodsName(SensitiveFilter.filter(goodsVO.getGoodsName(), CharacterConstant.WILDCARD_STAR));
        GoodsDO goods = new GoodsDO(goodsVO);
        // 判断是否把商品修改成积分商品,自营店
        if (seller.getSelfOperated() == 1) {
            goods.setGoodsType(goodsVO.getExchange() != null && goodsVO.getExchange().getEnableExchange() == 1 ? GoodsType.POINT.name() : GoodsType.NORMAL.name());
        } else {
            goods.setGoodsType(GoodsType.NORMAL.name());
        }

        String goodsSettingJson = settingClient.get(SettingGroup.GOODS);

        GoodsSettingVO goodsSetting = JsonUtil.jsonToObject(goodsSettingJson, GoodsSettingVO.class);
        // 判断商品修改是否需要审核
        goods.setIsAuth(goodsSetting.getUpdateAuth() == 1 ? 0 : 1);

        // 添加商品更新时间
        goods.setLastModify(DateUtil.getDateline());
        // 修改相册信息
        List<GoodsGalleryDO> goodsGalleys = goodsVO.getGoodsGalleryList();
        this.goodsGalleryManager.edit(goodsGalleys, goodsVO.getGoodsId());
        // 向goods加入图片
        goods.setOriginal(goodsGalleys.get(0).getOriginal());
        goods.setBig(goodsGalleys.get(0).getBig());
        goods.setSmall(goodsGalleys.get(0).getSmall());
        goods.setThumbnail(goodsGalleys.get(0).getThumbnail());
        goods.setSellerId(seller.getSellerId());
        goods.setSellerName(seller.getSellerName());

        //查询商品的规格信息，则将规格中最低的价格赋值到商品价格中 update by liuyulei  2019-05-21
        if (goodsDO.getHaveSpec() != null && goodsDO.getHaveSpec() == 1) {
            //有规格则商品价格等不变，以规格的为主
            goods.setPrice(goodsDO.getPrice());
            goods.setCost(goodsDO.getCost());
            goods.setWeight(goodsDO.getWeight());
        }

        // 更新商品
        goods.setGoodsId(id);
        UpdateWrapper wrapper = new UpdateWrapper<GoodsDO>();
        wrapper.eq("goods_id", id);
        // 如果商品品牌为空，更新商品品牌为空
        if (goods.getBrandId() == null) {
            wrapper.set("brand_id", null);
        }

        goodsMapper.update(goods, wrapper);

        //如果商品修改之前开启了积分兑换功能，那么需要在关闭积分兑换功能时删除存放在缓存中的脚本数据 add by duanmingyu 2020-02-20
        this.goodsSkuManager.deleteSkuExchangeScript(id, goodsDO.getGoodsType(), goods.getGoodsType());


        // 处理参数信息
        this.goodsParamsManager.addParams(goodsVO.getGoodsParamsList(), id);
        // 处理规格信息
        this.goodsSkuManager.updateSkuByGoods(goods);
        // 添加商品的积分换购信息
        PromotionGoodsDTO goodsDTO = new PromotionGoodsDTO();
        BeanUtils.copyProperties(goods, goodsDTO);
        if (goodsVO.getExchange() == null || GoodsType.NORMAL.name().equals(goods.getGoodsType())) {
            exchangeGoodsClient.edit(new ExchangeClientDTO(null, goodsDTO));
        } else {
            ExchangeDO exchange = new ExchangeDO();
            BeanUtils.copyProperties(goodsVO.getExchange(), exchange);
            if (exchange.getExchangeMoney() > goods.getPrice()) {
                throw new ServiceException(GoodsErrorCode.E301.code(), "积分商品价格不能高于商品原价");
            }
            exchangeGoodsClient.edit(new ExchangeClientDTO(exchange, goodsDTO));
        }

        //清除该商品关联的东西
        this.cleanGoodsAssociated(id, goodsVO.getMarketEnable());


        // 发送增加商品消息，店铺增加自身商品数量，静态页使用
        GoodsChangeMsg goodsChangeMsg = new GoodsChangeMsg(new Long[]{id}, GoodsChangeMsg.UPDATE_OPERATION);
        //修改商品时需要删除商品参与的促销活动
        goodsChangeMsg.setDelPromotion(true);
        //判断商品名称是否变化
        if (!goodsDO.getGoodsName().equals(goodsVO.getGoodsName())) {
            goodsChangeMsg.setNameChange(true);
        }
        this.messageSender.send(new MqMessage(AmqpExchange.GOODS_CHANGE, AmqpExchange.GOODS_CHANGE + "_ROUTING", goodsChangeMsg));

        return goods;
    }


    @Override
    @Transactional(value = "goodsTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void cleanGoodsAssociated(long goodsId, Integer markenable) {

        logger.debug("清除goodsid[" + goodsId + "]相关的缓存，包括促销的缓存");

        this.cache.remove(CachePrefix.GOODS.getPrefix() + goodsId);

        // 删除这个商品的sku缓存(必须要在删除库中sku前先删缓存),首先查出商品对应的sku_id
        List<GoodsSkuDO> skuIds = goodsSkuMapper.selectList(
                new QueryWrapper<GoodsSkuDO>()
                        .select("sku_id")
                        .eq("goods_id", goodsId));

        for (GoodsSkuDO map : skuIds) {
            cache.remove(CachePrefix.SKU.getPrefix() + map.getSkuId());
        }

        //不再读一次缓存竟然清不掉？？所以在这里又读了一下
        this.cache.get(CachePrefix.GOODS.getPrefix() + goodsId);

        //删除该商品关联的活动缓存
        long currTime = DateUtil.getDateline();
        String currDate = DateUtil.toString(currTime, "yyyyMMdd");

        //清除此商品的缓存
        this.cache.remove(CachePrefix.PROMOTION_KEY + currDate + goodsId);

        if (markenable == 0) {
            this.deleteExchange(goodsId);
        }

    }

    @Override
    public void updateCommentCount(Long goodsId, Integer num) {

        this.goodsMapper.update(null, new UpdateWrapper<GoodsDO>().setSql("comment_num=comment_num +" + num).eq("goods_id", goodsId));

        // 发送商品消息变化消息
        GoodsChangeMsg goodsChangeMsg = new GoodsChangeMsg(new Long[]{goodsId},
                GoodsChangeMsg.UPDATE_OPERATION);
        this.messageSender.send(new MqMessage(AmqpExchange.GOODS_CHANGE, AmqpExchange.GOODS_CHANGE + "_ROUTING", goodsChangeMsg));
    }

    @Override
    public void updateBuyCount(List<OrderSkuVO> list) {
        Set<Long> set = new HashSet<>();
        for (OrderSkuVO sku : list) {
            this.goodsMapper.update(null, new UpdateWrapper<GoodsDO>().setSql("buy_count=buy_count+" + sku.getNum()).eq("goods_id", sku.getGoodsId()));
            set.add(sku.getGoodsId());
        }
        // 发送修改商品消息
        GoodsChangeMsg goodsChangeMsg = new GoodsChangeMsg(set.toArray(new Long[set.size()]),
                GoodsChangeMsg.UPDATE_OPERATION);
        this.messageSender.send(new MqMessage(AmqpExchange.GOODS_CHANGE, AmqpExchange.GOODS_CHANGE + "_ROUTING", goodsChangeMsg));
    }


    /**
     * 删除积分商品
     *
     * @param goodsId
     */
    private void deleteExchange(Long goodsId) {

        //删除积分商品
        exchangeGoodsClient.del(goodsId);
    }

    @Override
    @RedisTransactional
    @Transactional(value = "goodsTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void under(Long[] goodsIds, String reason, Permission permission) {

        if (reason.length() > 500) {
            throw new ServiceException(PromotionErrorCode.E400.code(), "下架原因长度不能超过500个字符");
        }

        if (Permission.SELLER.equals(permission)) {
            this.checkPermission(goodsIds, GoodsOperate.UNDER);
            Seller seller = UserContext.getSeller();
            reason = "店员" + seller.getUsername() + "下架，原因为：" + reason;
        } else {
            //查看是否是不能下架的状态
            List<GoodsDO> list = this.goodsMapper.selectList(new QueryWrapper<GoodsDO>()
                    .select("disabled", "market_enable")
                    .in("goods_id", Arrays.asList(goodsIds)));

            for (GoodsDO map : list) {
                OperateAllowable operateAllowable = new OperateAllowable(map.getMarketEnable(), map.getDisabled());
                if (!operateAllowable.getAllowUnder()) {
                    throw new ServiceException(GoodsErrorCode.E301.code(), "存在不能下架的商品，不能操作");
                }

            }
            reason = "平台下架，原因为：" + reason;
        }

        this.goodsMapper.update(new GoodsDO(),
                new UpdateWrapper<GoodsDO>()
                        .set("market_enable", 0)
                        .set("under_message", reason)
                        .set("last_modify", DateUtil.getDateline())
                        .in("goods_id", Arrays.asList(goodsIds)));

        //清除相关的关联
        for (long goodsId : goodsIds) {
            this.cleanGoodsAssociated(goodsId, 0);
        }

        GoodsChangeMsg goodsChangeMsg = new GoodsChangeMsg(goodsIds, GoodsChangeMsg.UNDER_OPERATION, reason);
        this.messageSender.send(new MqMessage(AmqpExchange.GOODS_CHANGE, AmqpExchange.GOODS_CHANGE + "_ROUTING", goodsChangeMsg));

    }

    @Override
    @Transactional(value = "goodsTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void inRecycle(Long[] goodsIds) {

        this.checkPermission(goodsIds, GoodsOperate.RECYCLE);

        this.goodsMapper.update(new GoodsDO(),
                new UpdateWrapper<GoodsDO>()
                        //下架状态
                        .set("market_enable", 0)
                        //删除
                        .set("disabled", 0)
                        //修改最后修改时间
                        .set("last_modify", DateUtil.getDateline())
                        .in("goods_id", Arrays.asList(goodsIds)));

        //清除相关的关联
        for (long goodsId : goodsIds) {
            this.cleanGoodsAssociated(goodsId, 0);
        }

        GoodsChangeMsg goodsChangeMsg = new GoodsChangeMsg(goodsIds, GoodsChangeMsg.INRECYCLE_OPERATION);
        this.messageSender.send(new MqMessage(AmqpExchange.GOODS_CHANGE, AmqpExchange.GOODS_CHANGE + "_ROUTING", goodsChangeMsg));

    }

    @Override
    @RedisTransactional
    @Transactional(value = "goodsTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(Long[] goodsIds) {

        this.checkPermission(goodsIds, GoodsOperate.DELETE);

        this.goodsMapper.update(new GoodsDO(),
                new UpdateWrapper<GoodsDO>()
                        //修改删除商品的逻辑不做物理删除 add by liuyulei 2019-06-03
                        .set("disabled", -1)
                        .in("goods_id", Arrays.asList(goodsIds)));
        //删除商品发送商品删除消息DEL_OPERATION
        GoodsChangeMsg goodsChangeMsg = new GoodsChangeMsg(goodsIds, GoodsChangeMsg.DEL_OPERATION);
        this.messageSender.send(new MqMessage(AmqpExchange.GOODS_CHANGE, AmqpExchange.GOODS_CHANGE + "_ROUTING", goodsChangeMsg));
    }

    /**
     * 查看商品是否属于当前登录用户
     *
     * @param goodsIds
     */
    private void checkPermission(Long[] goodsIds, GoodsOperate goodsOperate) {

        Seller seller = UserContext.getSeller();

        List<GoodsDO> list = this.goodsMapper.selectList(
                new QueryWrapper<GoodsDO>()
                        .select("disabled", "market_enable")
                        .in("goods_id", Arrays.asList(goodsIds))
                        .eq("seller_id", seller.getSellerId()));


        if (list.size() != goodsIds.length) {
            throw new ServiceException(GoodsErrorCode.E301.code(), "存在不属于您的商品，不能操作");
        }

        for (GoodsDO g : list) {

            OperateAllowable operateAllowable = new OperateAllowable(g.getMarketEnable(), g.getDisabled());
            switch (goodsOperate) {
                case DELETE:
                    if (!operateAllowable.getAllowDelete()) {
                        throw new ServiceException(GoodsErrorCode.E301.code(), "存在不能删除的商品，不能操作");
                    }
                    break;
                case RECYCLE:
                    if (!operateAllowable.getAllowRecycle()) {
                        throw new ServiceException(GoodsErrorCode.E301.code(), "存在不能放入回收站的商品，不能操作");
                    }
                    break;
                case REVRET:
                    if (!operateAllowable.getAllowRevert()) {
                        throw new ServiceException(GoodsErrorCode.E301.code(), "存在不能还原的商品，不能操作");
                    }
                    break;
                case UNDER:
                    if (!operateAllowable.getAllowUnder()) {
                        throw new ServiceException(GoodsErrorCode.E301.code(), "存在不能下架的商品，不能操作");
                    }
                    break;
                default:
                    break;
            }
        }

    }

    @Override
    @Transactional(value = "goodsTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void revert(Long[] goodsIds) {

        this.checkPermission(goodsIds, GoodsOperate.REVRET);

        this.goodsMapper.update(new GoodsDO(),
                new UpdateWrapper<GoodsDO>()
                        //更新删除状态
                        .set("disabled", 1)
                        .in("goods_id", Arrays.asList(goodsIds)));

        GoodsChangeMsg goodsChangeMsg = new GoodsChangeMsg(goodsIds, GoodsChangeMsg.REVERT_OPERATION);
        this.messageSender.send(new MqMessage(AmqpExchange.GOODS_CHANGE, AmqpExchange.GOODS_CHANGE + "_ROUTING", goodsChangeMsg));

    }

    @Override
    @Transactional(value = "goodsTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void up(Long goodsId) {

        //查看是否允许上架
        GoodsDO goods = this.goodsMapper.selectOne(
                new QueryWrapper<GoodsDO>()
                        .select("disabled")
                        .select("market_enable")
                        .select("seller_id")
                        .eq("goods_id", goodsId));

        Integer disabled = goods.getDisabled();
        Integer marketEnable = goods.getMarketEnable();
        //商品所属店铺id
        Long sellerId = goods.getSellerId();
        //查询店铺是否是关闭中，若未开启，则不能上架
        ShopVO shop = shopClient.getShop(sellerId);
        if (shop == null || !"OPEN".equals(shop.getShopDisable())) {
            throw new ServiceException(GoodsErrorCode.E301.code(), "店铺关闭中,商品不能上架操作");
        }

        OperateAllowable operateAllowable = new OperateAllowable(marketEnable, disabled);
        if (!operateAllowable.getAllowMarket()) {
            throw new ServiceException(GoodsErrorCode.E301.code(), "商品不能上架操作");
        }

        this.goodsMapper.update(new GoodsDO(),
                new UpdateWrapper<GoodsDO>()
                        .set("market_enable", 1)
                        .set("disabled", 1)
                        .eq("goods_id", goodsId));

        cache.remove(CachePrefix.GOODS.getPrefix() + goodsId);

        GoodsChangeMsg goodsChangeMsg = new GoodsChangeMsg(new Long[]{goodsId}, GoodsChangeMsg.UPDATE_OPERATION);
        this.messageSender.send(new MqMessage(AmqpExchange.GOODS_CHANGE, AmqpExchange.GOODS_CHANGE + "_ROUTING", goodsChangeMsg));

    }

    @Override
    @Transactional(value = "goodsTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void batchAuditGoods(GoodsAuditParam param) {
        if (param.getGoodsIds() == null || param.getGoodsIds().length == 0) {
            throw new ServiceException(GoodsErrorCode.E301.code(), "请选择要审核的商品");
        }

        if (param.getPass() == null) {
            throw new ServiceException(GoodsErrorCode.E301.code(), "审核状态值不能为空");
        }

        if (param.getPass().intValue() != 0 && param.getPass().intValue() != 1) {
            throw new ServiceException(GoodsErrorCode.E301.code(), "审核状态值不正确");
        }

        if (param.getPass().intValue() == 0) {
            if (StringUtil.isEmpty(param.getMessage())) {
                throw new ServiceException(GoodsErrorCode.E301.code(), "拒绝原因不能为空");
            }

            if (param.getMessage().length() > 200) {
                throw new ServiceException(GoodsErrorCode.E301.code(), "拒绝原因不能超过200个字符");
            }
        }

        for (Long goodsId : param.getGoodsIds()) {
            GoodsDO goods = this.goodsQueryManager.getModel(goodsId);
            if (goods == null) {
                throw new ServiceException(GoodsErrorCode.E301.code(), "要审核的商品【" + goods.getGoodsName() + "】不存在");
            }

            if (goods.getIsAuth() != 0) {
                throw new ServiceException(GoodsErrorCode.E301.code(), "商品【" + goods.getGoodsName() + "】已审核，请勿重复审核");
            }

            // 审核通过
            this.goodsMapper.update(new GoodsDO(),
                    new UpdateWrapper<GoodsDO>()
                            .set("is_auth", param.getPass() == 1 ? 1 : 2)
                            .set("auth_message", param.getMessage())
                            .eq("goods_id", goodsId));
            // 发送审核消息
            GoodsChangeMsg goodsChangeMsg = null;
            if (param.getPass() == 1) {
                goodsChangeMsg = new GoodsChangeMsg(new Long[]{goodsId}, GoodsChangeMsg.GOODS_VERIFY_SUCCESS,
                        param.getMessage());
            } else {
                goodsChangeMsg = new GoodsChangeMsg(new Long[]{goodsId}, GoodsChangeMsg.GOODS_VERIFY_FAIL,
                        param.getMessage());
            }
            this.messageSender.send(new MqMessage(AmqpExchange.GOODS_CHANGE, AmqpExchange.GOODS_CHANGE + "_ROUTING", goodsChangeMsg));
            // 清楚商品的缓存
            cache.remove(CachePrefix.GOODS.getPrefix() + goodsId);
        }
    }

    @Override
    @Transactional(value = "goodsTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Integer visitedGoodsNum(Long goodsId) {
        /**
         * 逻辑：判断当前缓存中的viewedGoods变量是否包含了当前goods，如未，则 向变量中加入goods，如果已经存在则
         * 1、从redis中取出此商品的浏览数 2、如果为空则新增此商品浏览数为1，否则判断浏览数是否大于等于50，逻辑为每50次存入数据库 3、存入相关数据库
         */
        HttpSession session = ThreadContextHolder.getHttpRequest().getSession();
        List<Long> visitedGoods = (List<Long>) cache.get(CachePrefix.VISIT_COUNT.getPrefix() + session.getId());
        // 标识此会话是否访问过
        boolean visited = false;
        if (visitedGoods == null) {
            visitedGoods = new ArrayList<Long>();
        }

        if (visitedGoods.contains(goodsId)) {
            visited = true;
            visitedGoods.remove(goodsId);
        }
        visitedGoods.add(0, goodsId);

        String key = CachePrefix.VISIT_COUNT.getPrefix() + goodsId;
        /** 获取redis中此商品的浏览数 */
        Integer num = (Integer) this.cache.get(key);
        /** 如果redis中未记录此浏览数 则新增此商品浏览数为1 */
        if (num == null) {
            num = 0;
        }

        if (!visited) {

            num++;
            // 如果浏览数大于等于50则存入相关数据库
            if (num >= 50) {
                // num为历史访问量，如果满足条件需要将此次浏览数也要加进去 固+1
                this.goodsMapper.update(new GoodsDO(),
                        new UpdateWrapper<GoodsDO>()
                                .setSql("view_count = view_count +" + num)
                                .eq("goods_id", goodsId));
                num = 0;
            }
            this.cache.put(key, num);
        }

        return num;
    }

    /**
     * 获取新赠商品
     *
     * @param length
     * @return
     */
    @Override
    public List<BackendGoodsVO> newGoods(Integer length) {

        return this.goodsMapper.newGoods(length);
    }


    @Override
    @Transactional(value = "goodsTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void underShopGoods(Long sellerId) {

        //修改店铺商品为下架状态
        this.goodsMapper.update(new GoodsDO(),
                new UpdateWrapper<GoodsDO>()
                        .set("market_enable", 0)
                        .set("under_message", "店铺关闭，商品被下架")
                        .eq("seller_id", sellerId));
        //发送商品下架消息
        List<GoodsDO> goodsList = this.goodsMapper.selectList(
                new QueryWrapper<GoodsDO>()
                        .select("goods_id")
                        .eq("seller_id", sellerId));

        if (StringUtil.isNotEmpty(goodsList)) {

            List<Long> listIds = goodsList.stream().map(g -> g.getGoodsId()).collect(Collectors.toList());

            Long[] goodsIds = new Long[listIds.size()];

            listIds.toArray(goodsIds);

            GoodsChangeMsg goodsChangeMsg = new GoodsChangeMsg(goodsIds, GoodsChangeMsg.UNDER_OPERATION, "店铺关闭");
            this.messageSender.send(new MqMessage(AmqpExchange.GOODS_CHANGE, AmqpExchange.GOODS_CHANGE + "_ROUTING", goodsChangeMsg));

        }

    }

    @Override
    @Transactional(value = "goodsTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateGoodsGrade() {

        List<GoodsGrade> list = this.memberCommentClient.queryGoodsGrade();

        if (StringUtil.isNotEmpty(list)) {
            for (GoodsGrade goods : list) {
                double grade = CurrencyUtil.mul(goods.getGoodRate(), 100);
                this.goodsMapper.update(new GoodsDO(),
                        new UpdateWrapper<GoodsDO>()
                                .set("grade", CurrencyUtil.round(grade, 1))
                                .eq("goods_id", goods.getGoodsId()));

                cache.put(CachePrefix.GOODS_GRADE.getPrefix() + goods.getGoodsId(), CurrencyUtil.round(grade, 1));
                // 发送商品消息变化消息
                GoodsChangeMsg goodsChangeMsg = new GoodsChangeMsg(new Long[]{goods.getGoodsId()},
                        GoodsChangeMsg.UPDATE_OPERATION);
                this.messageSender.send(new MqMessage(AmqpExchange.GOODS_CHANGE, AmqpExchange.GOODS_CHANGE + "_ROUTING", goodsChangeMsg));
            }
        }


    }

    /**
     * 获取商品是否使用检测的模版
     *
     * @param templateId
     * @return 商品
     */
    @Override
    public GoodsDO checkShipTemplate(Long templateId) {

        List<GoodsDO> goodsDOS = this.goodsMapper.selectList(
                new QueryWrapper<GoodsDO>()
                        .ne("disabled", 1)
                        .eq("template_id", templateId));

        if (goodsDOS != null && goodsDOS.size() > 0) {
            return goodsDOS.get(0);
        }
        return null;
    }

    /**
     * 修改某店铺商品店铺名称
     *
     * @param sellerId
     * @param sellerName
     */
    @Override
    public void changeSellerName(Long sellerId, String sellerName) {
        //更改商品信息
        this.goodsMapper.update(new GoodsDO(),
                new UpdateWrapper<GoodsDO>()
                        .set("seller_name", sellerName)
                        .eq("seller_id", sellerId));

        //更改商品sku信息
        this.goodsSkuMapper.update(new GoodsSkuDO(),
                new UpdateWrapper<GoodsSkuDO>()
                        .set("seller_name", sellerName)
                        .eq("seller_id", sellerId));

        this.getAll(sellerId).forEach(goodsDO -> {
            this.cache.remove(CachePrefix.GOODS.getPrefix() + goodsDO.getGoodsId());
        });
        this.getAllSku(sellerId).forEach(skuDO -> {
            this.cache.remove(CachePrefix.SKU.getPrefix() + skuDO.getSkuId());
        });
    }


    /**
     * 获取店铺的商品
     * @param sellerId
     * @return
     */
    private List<GoodsDO> getAll(Long sellerId) {

        List<GoodsDO> goodsList = this.goodsMapper.selectList(
                new QueryWrapper<GoodsDO>()
                        .eq("seller_id", sellerId));

        return goodsList;
    }

    /**
     * 获取店铺的sku
     * @param sellerId
     * @return
     */
    private List<GoodsSkuDO> getAllSku(Long sellerId) {

        List<GoodsSkuDO> goodsList = this.goodsSkuMapper.selectList(
                new QueryWrapper<GoodsSkuDO>()
                        .eq("seller_id", sellerId));
        return goodsList;
    }

    @Override
    @Transactional(value = "goodsTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateGoodsType(Long sellerId, String type) {
        this.goodsMapper.update(new GoodsDO(),
                new UpdateWrapper<GoodsDO>()
                        .set("goods_type", type)
                        .eq("seller_id", sellerId));
    }

    @Override
    @Transactional(value = "goodsTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updatePriority(Long goodsId, Integer priority) {
        this.goodsMapper.update(new GoodsDO(),
                new UpdateWrapper<GoodsDO>()
                        .set("priority", priority)
                        .eq("goods_id", goodsId));
        // 发送修改商品优先级消息
        GoodsChangeMsg goodsChangeMsg = new GoodsChangeMsg(new Long[]{goodsId}, GoodsChangeMsg.GOODS_PRIORITY_CHANGE);
        this.messageSender.send(new MqMessage(AmqpExchange.GOODS_PRIORITY_CHANGE, AmqpExchange.GOODS_PRIORITY_CHANGE + "_ROUTING", goodsChangeMsg));
    }
}

