package com.enation.app.javashop.service.statistics.impl;

import com.enation.app.javashop.mapper.statistics.GoodsPageViewMapper;
import com.enation.app.javashop.mapper.statistics.ShopPageViewMapper;
import com.enation.app.javashop.model.base.rabbitmq.AmqpExchange;
import com.enation.app.javashop.client.goods.GoodsClient;
import com.enation.app.javashop.model.goods.vo.CacheGoods;
import com.enation.app.javashop.model.member.dto.HistoryDTO;
import com.enation.app.javashop.model.statistics.dos.GoodsPageView;
import com.enation.app.javashop.model.statistics.dos.ShopPageView;
import com.enation.app.javashop.service.statistics.DisplayTimesManager;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.security.model.Buyer;
import com.enation.app.javashop.framework.util.StringUtil;
import com.enation.app.javashop.framework.rabbitmq.MessageSender;
import com.enation.app.javashop.framework.rabbitmq.MqMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DisplayTimesManagerImpl
 *
 * @author Chopper
 * @version v1.0
 * @since v7.0
 * 2018-08-07 上午8:22
 */
@Service
public class DisplayTimesManagerImpl implements DisplayTimesManager {


    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private ShopPageViewMapper shopPageViewMapper;

    @Autowired
    private GoodsPageViewMapper goodsPageViewMapper;

    /**
     * 阙值，数据累计次数后，进行入库操作
     */
    private final int THRESHOLD = 100;

    /**
     * 商品访问
     */
    private final String GOODS = "{GOODS_VIEW}";
    /**
     * 店铺访问
     */
    private final String SHOP = "{SHOP_VIEW}";

    /**
     * 访问记录
     */
    private final String HISTORY = "{VIEW_HISTORY}";


    @Autowired
    private Cache cache;

    public DisplayTimesManagerImpl() {
    }

    /**
     * 访问某地址
     *
     * @param url
     */
    @Override
    public void view(String url, String uuid) {

        //记录访问
        List<String> history = new ArrayList<>(16);

        Object object = cache.get(HISTORY);
        //非空判定，为空则创建
        if (object != null) {
            history = (List) object;
        }

        //判定访问是商品还是店铺
        int type = regular(url);

        //如果访问的是商品，那么需求新增或更新会员商品浏览足迹
        if (type == 1) {
            createFootmark(urlParams(url, type));
        }

        //如果已经访问过，则不进行统计
        if (history.contains(url + uuid)) {
            return;
        }
        //否则记录访问
        history.add(url + uuid);
        cache.put(HISTORY, history);

        //无效访问过滤
        if (type == 2) {
            return;
        }
        switch (type) {
            case 0:
                viewShop(urlParams(url, type));
                break;
            case 1:
                viewGoods(urlParams(url, type));
                break;
            default:
        }
    }

    /**
     * 立即整理现有的数据
     */
    @Override
    public void countNow() {

        List<ShopPageView> shopPageViews = (List<ShopPageView>) cache.get(SHOP);
        List<GoodsPageView> goodsPageViews = (List<GoodsPageView>) cache.get(GOODS);
        this.cache.remove(GOODS);
        this.cache.remove(SHOP);
        this.cache.remove(HISTORY);
        if (StringUtil.isNotEmpty(shopPageViews)) {
            shopPageViews = reBuildShop(shopPageViews);
            this.countShop(shopPageViews);
        }

        if (StringUtil.isNotEmpty(goodsPageViews)) {
            goodsPageViews = reBuildGoods(goodsPageViews);
            this.countGoods(goodsPageViews);
        }
    }


    /**
     * 重新构造商品浏览
     *
     * @param goodsPageViews
     */
    private List<GoodsPageView> reBuildGoods(List<GoodsPageView> goodsPageViews) {
        //整理商品
        Map<Integer, GoodsPageView> countGoods = new HashMap<>(16);
        for (GoodsPageView goodsPageView : goodsPageViews) {
            if (countGoods.containsKey(goodsPageView.hashCode())) {
                GoodsPageView cGoodsPageView = countGoods.get(goodsPageView.hashCode());
                cGoodsPageView.setNum(cGoodsPageView.getNum() + 1);
                countGoods.put(goodsPageView.hashCode(), cGoodsPageView);
            } else {
                CacheGoods cacheGoods = goodsClient.getFromCache(goodsPageView.getGoodsId());
                goodsPageView.setGoodsName(cacheGoods.getGoodsName());
                goodsPageView.setSellerId(cacheGoods.getSellerId());
                countGoods.put(goodsPageView.hashCode(), goodsPageView);
            }
        }
        goodsPageViews = new ArrayList<>();
        for (Integer key : countGoods.keySet()) {
            goodsPageViews.add(countGoods.get(key));
        }
        return goodsPageViews;
    }

    /**
     * 重新构造店铺浏览
     *
     * @param shopPageViews
     */
    private List<ShopPageView> reBuildShop(List<ShopPageView> shopPageViews) {
        //整理店铺
        Map<Integer, ShopPageView> countShop = new HashMap<>(16);
        for (ShopPageView shopPageView : shopPageViews) {
            if (countShop.containsKey(shopPageView.hashCode())) {
                int num = countShop.get(shopPageView.hashCode()).getNum();
                countShop.get(shopPageView.hashCode()).setNum(num++);
            } else {
                countShop.put(shopPageView.hashCode(), shopPageView);
            }
        }
        shopPageViews = new ArrayList<>();
        for (Integer key : countShop.keySet()) {
            shopPageViews.add(countShop.get(key));
        }
        return shopPageViews;
    }

    /**
     * 将统计好的店铺数据 写入数据库
     *
     * @param shopPageViews
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
    public void countShop(List<ShopPageView> shopPageViews) {

        if (shopPageViews == null) {
            return;
        }
        List<ShopPageView> buildShop = reBuildShop(shopPageViews);

        for(ShopPageView shopPageView : buildShop){
            shopPageViewMapper.insert(shopPageView);
        }
    }

    /**
     * 将统计好的商品数据 写入数据库
     *
     * @param goodsPageViews
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
    public void countGoods(List<GoodsPageView> goodsPageViews) {
        if (goodsPageViews == null) {
            return;
        }
        List<GoodsPageView> buildGoods = reBuildGoods(goodsPageViews);

        for(GoodsPageView goodsPageView : buildGoods){
            goodsPageViewMapper.insert(goodsPageView);
        }
    }

    /**
     * 访问商品
     *
     * @param goodsId
     */
    private void viewGoods(Long goodsId) {
        List<GoodsPageView> goodsPageViews = (List<GoodsPageView>) cache.get(GOODS);
        if (goodsPageViews == null) {
            goodsPageViews = new ArrayList<>(16);
        }
        LocalDate localDate = LocalDate.now();
        GoodsPageView goodsPageView = new GoodsPageView();
        goodsPageView.setGoodsId(goodsId);
        goodsPageView.setNum(1);
        goodsPageView.setYear(localDate.getYear());
        goodsPageView.setMonth(localDate.getMonthValue());
        goodsPageView.setCreateTime(DateUtil.getDateline());
        goodsPageViews.add(goodsPageView);
        //如果达到阙值，则发送AMQP进行处理
        if (goodsPageViews.size() > THRESHOLD) {
            messageSender.send(new MqMessage(AmqpExchange.GOODS_VIEW_COUNT, AmqpExchange.GOODS_VIEW_COUNT + "_ROUTING",
                    goodsPageViews));
            this.cache.remove(GOODS);
            return;
        }

        this.cache.put(GOODS, goodsPageViews);

    }

    /**
     * 处理会员对商品访问的历史足迹信息
     * @param goodsId
     */
    private void createFootmark(Long goodsId) {
        //对商品访问历史足迹进行统计
        //获取当前会员
        Buyer buyer = UserContext.getBuyer();
        //如果当前会员为空或者商品id为null则不去生成足迹
        if (buyer != null && goodsId != null) {
            HistoryDTO historyDTO = new HistoryDTO(goodsId,buyer.getUid());
            this.messageSender.send(new MqMessage(AmqpExchange.MEMBER_HISTORY, AmqpExchange.MEMBER_HISTORY + "_ROUTING", historyDTO));
        }
    }

    /**
     * 访问店铺
     *
     * @param shopId
     */
    private void viewShop(Long shopId) {
        List<ShopPageView> shopPageViews = (List<ShopPageView>) cache.get(SHOP);
        if (shopPageViews == null) {
            shopPageViews = new ArrayList<>(16);
        }
        LocalDate localDate = LocalDate.now();
        ShopPageView shopPageView = new ShopPageView();
        shopPageView.setNum(1);
        shopPageView.setYear(localDate.getYear());
        shopPageView.setMonth(localDate.getMonthValue());
        shopPageView.setSellerId(shopId);
        shopPageView.setDay(localDate.getDayOfMonth());
        shopPageView.setCreateTime(DateUtil.getDateline());
        shopPageViews.add(shopPageView);

        //如果达到阙值，则发送AMQP进行处理
        if (shopPageViews.size() > THRESHOLD) {
            messageSender.send(new MqMessage(AmqpExchange.SHOP_VIEW_COUNT, AmqpExchange.SHOP_VIEW_COUNT + "_ROUTING",
                    shopPageViews));
            this.cache.remove(SHOP);
            return;
        }
        this.cache.put(SHOP, shopPageViews);
    }

    /**
     * 匹配当前url访问的是商品还是店铺
     *
     * @param url
     * @return 1 商品 0店铺 2 无效
     */
    private int regular(String url) {
        if (StringUtil.isEmpty(url)) {
            return 2;
        }
        if (url.contains("/shop/")) {
            return 0;
        } else if (url.contains("/goods/")) {
            return 1;
        } else {
            return 2;
        }
    }

    /**
     * 获取当前店铺的商品id/或者店铺id
     *
     * @param url
     * @return 1 商品 0店铺 2 无效
     */
    private long urlParams(String url, int type) {
        switch (type) {
            case 0:
                String pattern = "(/shop/)(\\d+)";
                // 创建 Pattern 对象
                Pattern r = Pattern.compile(pattern);
                // 现在创建 matcher 对象
                Matcher m = r.matcher(url);
                if (m.find()) {
                    return new Long(m.group(2));
                }
            case 1:
                pattern = "(/goods/)(\\d+)";
                // 创建 Pattern 对象
                r = Pattern.compile(pattern);
                // 现在创建 matcher 对象
                m = r.matcher(url);
                if (m.find()) {
                    return new Long(m.group(2));
                }
            default:
                return 0;

        }
    }
}
