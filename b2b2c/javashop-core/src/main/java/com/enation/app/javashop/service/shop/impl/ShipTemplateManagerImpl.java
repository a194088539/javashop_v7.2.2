package com.enation.app.javashop.service.shop.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.enation.app.javashop.mapper.member.ShipTemplateChildMapper;
import com.enation.app.javashop.mapper.member.ShipTemplateMapper;
import com.enation.app.javashop.model.base.CachePrefix;
import com.enation.app.javashop.model.base.rabbitmq.AmqpExchange;
import com.enation.app.javashop.client.goods.GoodsClient;
import com.enation.app.javashop.client.system.RegionsClient;
import com.enation.app.javashop.model.goods.dos.GoodsDO;
import com.enation.app.javashop.model.goods.vo.ShipTemplateMsg;
import com.enation.app.javashop.model.member.vo.RegionVO;
import com.enation.app.javashop.service.shop.ShipTemplateManager;
import com.enation.app.javashop.model.errorcode.ShopErrorCode;
import com.enation.app.javashop.model.shop.dos.ShipTemplateChild;
import com.enation.app.javashop.model.shop.dos.ShipTemplateDO;
import com.enation.app.javashop.model.shop.vo.ShipTemplateChildBuyerVO;
import com.enation.app.javashop.model.shop.vo.ShipTemplateChildSellerVO;
import com.enation.app.javashop.model.shop.vo.ShipTemplateSellerVO;
import com.enation.app.javashop.model.shop.vo.ShipTemplateVO;
import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.util.ScriptUtil;
import com.enation.app.javashop.framework.util.StringUtil;
import com.google.gson.Gson;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import com.enation.app.javashop.framework.rabbitmq.MessageSender;
import com.enation.app.javashop.framework.rabbitmq.MqMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 运费模版业务类
 *
 * @author zjp
 * @version v7.0.0
 * @since v7.0.0
 * 2018-03-28 21:44:49
 */
@Service
public class ShipTemplateManagerImpl implements ShipTemplateManager {

    @Autowired
    private ShipTemplateMapper shipTemplateMapper;

    @Autowired
    private ShipTemplateChildMapper shipTemplateChildMapper;

    @Autowired
    private Cache cache;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private RegionsClient regionsClient;

    @Autowired
    private MessageSender messageSender;

    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ShipTemplateDO save(ShipTemplateSellerVO template) {
        //设置商家ID为当前登录商家ID
        template.setSellerId(UserContext.getSeller().getSellerId());
        //新建运费模版实体对象
        ShipTemplateDO t = new ShipTemplateDO();
        //复制属性
        BeanUtils.copyProperties(template, t);
        //运费模板信息入库
        shipTemplateMapper.insert(t);

        //保存运费模板子模板
        List<ShipTemplateChildSellerVO> items = template.getItems();
        List<ShipTemplateChildSellerVO> freeItems = template.getFreeItems();

        //设置指定运费子模板
        this.addTemplateChildren(items, t.getId(),false);
        //设置指定免运费子模板
        this.addTemplateChildren(freeItems, t.getId(),true);

        //删除缓存中的运费模板缓存信息
        cache.remove(CachePrefix.SHIP_TEMPLATE.getPrefix() + template.getSellerId());
        //发送消息
        this.messageSender.send(new MqMessage(AmqpExchange.SHIP_TEMPLATE_CHANGE, AmqpExchange.SHIP_TEMPLATE_CHANGE + "_ROUTING", new ShipTemplateMsg(t.getId(), 1)));
        return t;
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ShipTemplateDO edit(ShipTemplateSellerVO template) {
        //设置商家ID为当前登录商家ID
        template.setSellerId(UserContext.getSeller().getSellerId());
        //新建运费模版实体对象
        ShipTemplateDO t = new ShipTemplateDO();
        //复制属性
        BeanUtils.copyProperties(template, t);
        //获取运费模板ID
        Long id = template.getId();
        //修改运费模板信息
        shipTemplateMapper.updateById(t);

        //删除子模板
        QueryWrapper<ShipTemplateChild> deleteWrapper = new QueryWrapper<>();
        deleteWrapper.eq("template_id", id);
        shipTemplateChildMapper.delete(deleteWrapper);

        //保存运费模板子模板
        List<ShipTemplateChildSellerVO> items = template.getItems();
        List<ShipTemplateChildSellerVO> freeItems = template.getFreeItems();

        //设置指定运费子模板
        this.addTemplateChildren(items, id,false);

        //设置指定免运费子模板
        this.addTemplateChildren(freeItems, id,true);

        //移除缓存某个VO
        this.cache.remove(CachePrefix.SHIP_TEMPLATE_ONE.getPrefix() + id);
        //删除缓存中的运费模板缓存信息
        this.cache.remove(CachePrefix.SHIP_TEMPLATE.getPrefix() + template.getSellerId());
        //发送消息
        this.messageSender.send(new MqMessage(AmqpExchange.SHIP_TEMPLATE_CHANGE, AmqpExchange.SHIP_TEMPLATE_CHANGE + "_ROUTING", new ShipTemplateMsg(id, 2)));
        return t;
    }

    /**
     * 添加运费模板子模板
     */
    private void addTemplateChildren(List<ShipTemplateChildSellerVO> items, Long templateId, boolean isFree) {

        for (ShipTemplateChildSellerVO child : items) {

            ShipTemplateChild shipTemplateChild = new ShipTemplateChild();
            BeanUtils.copyProperties(child, shipTemplateChild);
            shipTemplateChild.setTemplateId(templateId);
            //获取地区id
            String area = child.getArea();

            Gson gson = new Gson();
            Map<String, Map> map = new HashMap();
            map = gson.fromJson(area, map.getClass());
            StringBuffer areaIdBuffer = new StringBuffer(",");
            // 获取所有的地区
            Object obj = this.cache.get(CachePrefix.REGIONALL.getPrefix() + 4);
            List<RegionVO> allRegions = null;
            if (obj == null) {
                obj = regionsClient.getRegionByDepth(4);
            }
            allRegions = (List<RegionVO>) obj;
            Map<String, RegionVO> regionsMap = new HashMap();
            //循环地区放到Map中，便于取出
            for (RegionVO region : allRegions) {
                regionsMap.put(region.getId() + "", region);
            }

            for (String key : map.keySet()) {
                //拼接地区id
                areaIdBuffer.append(key + ",");
                Map dto = map.get(key);
                //需要取出改地区下面所有的子地区
                RegionVO provinceRegion = regionsMap.get(key);
                List<RegionVO> cityRegionList = provinceRegion.getChildren();

                Map<String, RegionVO> cityRegionMap = new HashMap<>();
                for (RegionVO city : cityRegionList) {
                    cityRegionMap.put(city.getId() + "", city);
                }
                //判断下面的地区是否被全选
                if ((boolean) dto.get("selected_all")) {

                    //市
                    for (RegionVO cityRegion : cityRegionList) {

                        areaIdBuffer.append(cityRegion.getId() + ",");
                        List<RegionVO> regionList = cityRegion.getChildren();
                        //区
                        for (RegionVO region : regionList) {

                            areaIdBuffer.append(region.getId() + ",");
                            List<RegionVO> townList = region.getChildren();
                            //城镇
                            if (townList != null) {
                                for (RegionVO townRegion : townList) {

                                    areaIdBuffer.append(townRegion.getId() + ",");
                                }
                            }
                        }
                    }
                } else {
                    //没有全选，则看选中城市
                    Map<String, Map> citiesMap = (Map<String, Map>) dto.get("children");
                    for (String cityKey : citiesMap.keySet()) {

                        areaIdBuffer.append(cityKey + ",");

                        Map cityMap = citiesMap.get(cityKey);

                        RegionVO cityRegion = cityRegionMap.get(cityKey);
                        List<RegionVO> regionList = cityRegion.getChildren();
                        //某个城市如果全部选中，需要取出城市下面的子地区
                        if ((boolean) cityMap.get("selected_all")) {
                            //区
                            for (RegionVO region : regionList) {

                                areaIdBuffer.append(region.getId() + ",");
                                List<RegionVO> townList = region.getChildren();
                                //城镇
                                if (townList != null) {
                                    for (RegionVO townRegion : townList) {

                                        areaIdBuffer.append(townRegion.getId() + ",");
                                    }
                                }
                            }

                        } else {
                            //选中了某个城市下面的几个区
                            Map<String, Map> regionMap = (Map<String, Map>) cityMap.get("children");
                            for (String regionKey : regionMap.keySet()) {

                                areaIdBuffer.append(regionKey + ",");
                                for (RegionVO region : regionList) {
                                    if (("" + region.getId()).equals(regionKey)) {
                                        List<RegionVO> townList = region.getChildren();
                                        //城镇
                                        if (townList != null) {
                                            for (RegionVO townRegion : townList) {

                                                areaIdBuffer.append(townRegion.getId() + ",");
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            shipTemplateChild.setAreaId(areaIdBuffer.toString());
            shipTemplateChild.setIsFree(isFree);
            shipTemplateChildMapper.insert(shipTemplateChild);
        }

    }

    @Override
    public List<ShipTemplateSellerVO> getStoreTemplate(Long sellerId) {
        cache.remove(CachePrefix.SHIP_TEMPLATE.getPrefix() + sellerId);
        List<ShipTemplateSellerVO> list = (List<ShipTemplateSellerVO>) cache.get(CachePrefix.SHIP_TEMPLATE.getPrefix() + sellerId);
        if (list == null) {
            list = shipTemplateMapper.selectShipTemplateList(sellerId);

            if (list != null) {
                for (ShipTemplateSellerVO vo : list) {
                    QueryWrapper<ShipTemplateChild> wrapper = new QueryWrapper<>();
                    wrapper.select("first_company", "first_price", "continued_company", "continued_price", "area").eq("template_id", vo.getId());
                    List<ShipTemplateChild> children = shipTemplateChildMapper.selectList(wrapper);
                    List<ShipTemplateChildSellerVO> items = new ArrayList<>();
                    if (children != null) {
                        for (ShipTemplateChild child : children) {
                            ShipTemplateChildSellerVO childvo = new ShipTemplateChildSellerVO(child, true);
                            items.add(childvo);
                        }
                    }
                    vo.setItems(items);
                }
            }
            cache.put(CachePrefix.SHIP_TEMPLATE.getPrefix() + sellerId, list);
        }

        return list;
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(Long templateId) {
        GoodsDO goodsDO = this.goodsClient.checkShipTemplate(templateId);
        if (goodsDO != null) {
            throw new ServiceException(ShopErrorCode.E226.code(), "模版被商品【" + goodsDO.getGoodsName() + "】使用，无法删除该模版");
        }

        ShipTemplateDO template = this.getOneDB(templateId);

        //删除运费模板
        shipTemplateMapper.deleteById(templateId);
        //删除运费模板关联地区
        QueryWrapper<ShipTemplateChild> wrapper = new QueryWrapper<>();
        wrapper.eq("template_id", templateId);
        shipTemplateChildMapper.delete(wrapper);

        Long sellerId = template.getSellerId();

        //移除缓存某个VO
        this.cache.remove(CachePrefix.SHIP_TEMPLATE_ONE.getPrefix() + templateId);
        //移除缓存某商家的VO列表
        this.cache.remove(CachePrefix.SHIP_TEMPLATE.getPrefix() + sellerId);
    }

    @Override
    public ShipTemplateVO getFromCache(Long templateId) {
        ShipTemplateVO tpl = (ShipTemplateVO) this.cache.get(CachePrefix.SHIP_TEMPLATE_ONE.getPrefix() + templateId);
        if (tpl == null) {
            //编辑运费模板的查询一个运费模板
            ShipTemplateDO template = this.getOneDB(templateId);
            tpl = new ShipTemplateVO();
            BeanUtils.copyProperties(template, tpl);

            //查询运费模板的子模板
            QueryWrapper<ShipTemplateChildBuyerVO> wrapper = new QueryWrapper<>();
            wrapper.eq("template_id", templateId).eq("is_free", 0);
            List<ShipTemplateChildBuyerVO> children = shipTemplateChildMapper.selectChildVo(wrapper);

            //查询运费模板的子模板  免运费
            wrapper = new QueryWrapper<>();
            wrapper.eq("template_id", templateId).eq("is_free", 1);
            List<ShipTemplateChildBuyerVO> freeChildren = shipTemplateChildMapper.selectChildVo(wrapper);

            tpl.setItems(children);
            tpl.setFreeItems(freeChildren);

            cache.put(CachePrefix.SHIP_TEMPLATE_ONE.getPrefix() + templateId, tpl);
        }
        return tpl;

    }

    @Override
    public ShipTemplateSellerVO getFromDB(Long templateId) {

        ShipTemplateDO template = this.getOneDB(templateId);
        ShipTemplateSellerVO tpl = new ShipTemplateSellerVO();
        BeanUtils.copyProperties(template, tpl);

        //查询运费模板的子模板
        QueryWrapper<ShipTemplateChild> wrapper = new QueryWrapper<>();
        wrapper.select("first_company", "first_price", "continued_company", "continued_price", "area").eq("template_id", templateId);
        List<ShipTemplateChild> children = shipTemplateChildMapper.selectList(wrapper);

        List<ShipTemplateChildSellerVO> items = new ArrayList<>();
        if (children != null) {
            for (ShipTemplateChild child : children) {
                ShipTemplateChildSellerVO childvo = new ShipTemplateChildSellerVO(child, false);
                items.add(childvo);
            }
        }

        tpl.setItems(items);

        return tpl;
    }

    @Override
    public List<String> getScripts(Long id) {

        List<String> scripts = (List<String>) cache.get(CachePrefix.SHIP_SCRIPT.getPrefix() + id);
        if (scripts == null) {
            ShipTemplateVO shipTemplateVO = this.getFromCache(id);
            return this.cacheShipTemplateScript(shipTemplateVO);
        }
        return scripts;
    }

    @Override
    public List<String> cacheShipTemplateScript(ShipTemplateVO shipTemplateVO) {
        //获取运费模板子模板
        List<ShipTemplateChildBuyerVO> items = shipTemplateVO.getItems();

        //获取免运费子模板
        List<ShipTemplateChildBuyerVO> freeItems = shipTemplateVO.getFreeItems();

        //免运费区域areaids
        StringBuffer freeAreaIds = new StringBuffer();

        for (ShipTemplateChildBuyerVO buyerVO:freeItems) {
            freeAreaIds.append(buyerVO.getAreaId());
        }

        List<String> scripts = new ArrayList<>();
        //循环子模板生成script
        for (ShipTemplateChildBuyerVO shipTemplateChildBuyerVO : items) {
            String script = getScript(shipTemplateChildBuyerVO, shipTemplateVO.getType(),freeAreaIds.toString());
            if (StringUtil.isEmpty(script)) {
                logger.error("运费模板id为" + shipTemplateVO.getId() + "的模板生成错误");
            }
            scripts.add(script);
        }

        //缓存模板script，格式为SHIP_SCRIPT_模板id
        cache.put(CachePrefix.SHIP_SCRIPT.getPrefix() + shipTemplateVO.getId(), scripts);

        return scripts;
    }

    /**
     * 生成脚本
     *
     * @param shipTemplateChildBuyerVO 子模板信息
     * @param shipTempalteType         模板类型
     * @return script脚本
     */
    private String getScript(ShipTemplateChildBuyerVO shipTemplateChildBuyerVO, Integer shipTempalteType,String freeAreaIds) {
        Map<String, Object> params = new HashMap<>();
        params.put("area", shipTemplateChildBuyerVO.getAreaId());
        params.put("firstPrice", shipTemplateChildBuyerVO.getFirstPrice());
        params.put("firstCompany", shipTemplateChildBuyerVO.getFirstCompany());
        params.put("continuedCompany", shipTemplateChildBuyerVO.getContinuedCompany());
        params.put("continuedPrice", shipTemplateChildBuyerVO.getContinuedPrice());
        params.put("freeAreaIds", freeAreaIds);
        params.put("type", shipTempalteType);
        String path = "ship.ftl";
        String script = ScriptUtil.renderScript(path, params);
        logger.debug("生成运费脚本：" + script);
        return script;
    }

    /**
     * 数据库中查询运费模板
     *
     * @param templateId
     * @return
     */
    private ShipTemplateDO getOneDB(Long templateId) {
        return shipTemplateMapper.selectById(templateId);
    }

}
