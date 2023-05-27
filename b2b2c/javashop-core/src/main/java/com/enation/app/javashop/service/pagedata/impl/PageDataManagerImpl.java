package com.enation.app.javashop.service.pagedata.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.system.PageDataMapper;
import com.enation.app.javashop.model.base.rabbitmq.AmqpExchange;
import com.enation.app.javashop.model.base.message.CmsManageMsg;
import com.enation.app.javashop.client.goods.GoodsClient;
import com.enation.app.javashop.model.goods.vo.CacheGoods;
import com.enation.app.javashop.model.pagedata.PageData;
import com.enation.app.javashop.service.pagedata.PageDataManager;
import com.enation.app.javashop.model.errorcode.SystemErrorCode;
import com.enation.app.javashop.model.system.enums.ClientType;
import com.enation.app.javashop.framework.database.DaoSupport;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import com.enation.app.javashop.framework.util.JsonUtil;
import com.enation.app.javashop.framework.util.StringUtil;
import com.enation.app.javashop.framework.validation.annotation.DemoSiteDisable;
import com.enation.app.javashop.framework.rabbitmq.MessageSender;
import com.enation.app.javashop.framework.rabbitmq.MqMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 楼层业务类
 *
 * @author fk
 * @version v1.0
 * @since v7.0.0
 * 2018-05-21 16:39:22
 */
@Service
public class PageDataManagerImpl implements PageDataManager {

    @Autowired
    private MessageSender messageSender;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private PageDataMapper pageDataMapper;

    protected final Logger logger = LoggerFactory.getLogger(PageDataManagerImpl.class);

    @Override
    public WebPage list(long page, long pageSize) {

        QueryWrapper<PageData> wrapper = new QueryWrapper<>();
        IPage<PageData> iPage = pageDataMapper.selectPage(new Page<>(page,pageSize), wrapper);
        return PageConvert.convert(iPage);

    }

    @Override
    @Transactional(value = "systemTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PageData add(PageData page) {
        pageDataMapper.insert(page);
        //发送消息
        this.sendFocusChangeMessage(page.getClientType());
        return page;
    }

    @Override
    @Transactional(value = "systemTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PageData edit(PageData page, Long id) {
        page.setPageId(id);
        pageDataMapper.updateById(page);
        //发送消息
        this.sendFocusChangeMessage(page.getClientType());
        return page;
    }

    @Override
    @Transactional(value = "systemTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(Long id) {
        pageDataMapper.deleteById(WebPage.class);
        //发送消息
        PageData page = this.getModel(id);
        this.sendFocusChangeMessage(page.getClientType());
    }

    @Override
    public PageData getModel(Long id) {
        PageData page = pageDataMapper.selectById(id);
        return page;
    }


    @Override
    public PageData queryPageData(String clientType, String pageType) {
        PageData page = this.getByType(clientType, pageType);
        if (page == null) {
            throw new ServiceException(SystemErrorCode.E806.code(), "楼层找不到");
        }
        constructPageData(page);
        return page;
    }


    @Override
    @DemoSiteDisable
    @Transactional(value = "systemTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PageData editByType(PageData pageData) {
        PageData data = this.getByType(pageData.getClientType(), pageData.getPageType());
        //首次保存
        if (data == null) {
            pageDataMapper.insert(pageData);
            pageData.setPageId(pageData.getPageId());
        } else {
            pageData.setPageId(data.getPageId());
            pageDataMapper.updateById(pageData);
            //this.daoSupport.update(pageData, data.getPageId());

        }

        return pageData;
    }

    @Override
    public PageData getByType(String clientType, String pageType) {

        QueryWrapper<PageData> wrapper = new QueryWrapper<>();
        wrapper.eq("client_type", clientType).eq("page_type", pageType);
        PageData page = pageDataMapper.selectOne(wrapper);

        constructPageData(page);

        return page;
    }

    /**
     * 重新渲染楼层数据
     * @param page
     */
    private void constructPageData(PageData page){

        if(page != null && "WAP".equals(page.getClientType())) {
            String pageData = page.getPageData();
            List<HashMap> dataList = JsonUtil.jsonToList(pageData, HashMap.class);
            for (Map map : dataList) {
                List<Map> blockList = (List<Map>) map.get("blockList");
                for (Map blockMap : blockList) {
                    String blockType = (String) blockMap.get("block_type");
                    switch (blockType) {
                        case "GOODS":
                            if("".equals(blockMap.get("block_value")) || blockMap.get("block_value") == null){
                                blockMap.put("block_value", null);
                                return;
                            }
                            Map blockValue = (Map) blockMap.get("block_value");

                            long goodsId = 0;
                            if(blockValue.get("goods_id")!=null){

                                goodsId = StringUtil.toLong(blockValue.get("goods_id").toString(),false);
                            }

                            CacheGoods goods = null;
                            try {
                                goods = this.goodsClient.getFromCache(goodsId);
                            } catch (Exception e) {
                                logger.error("读取商品异常",e);
                            }
                            //如果商品被删除则返回空数据
                            if (goods == null || goods.getDisabled() == 0) {
                                blockMap.put("block_value", null);
                                break;
                            }
                            blockValue.put("goods_name", goods.getGoodsName());
                            blockValue.put("sn", goods.getSn());
                            blockValue.put("thumbnail", goods.getThumbnail());
                            blockValue.put("seller_name", goods.getSellerName());
                            blockValue.put("goods_image", goods.getThumbnail());
                            blockValue.put("enable_quantity", goods.getEnableQuantity());
                            blockValue.put("quantity", goods.getQuantity());
                            blockValue.put("goods_price", goods.getPrice());
                            blockValue.put("market_enable", goods.getMarketEnable());
                            blockValue.put("is_auth", goods.getIsAuth());

                            break;
                        default:
                            break;
                    }

                }

            }
            page.setPageData(JsonUtil.objectToJson(dataList));
        }
    }

    /**
     * 发送首页变化消息
     *
     * @param clientType
     */
    private void sendFocusChangeMessage(String clientType) {

        CmsManageMsg cmsManageMsg = new CmsManageMsg();

        if (ClientType.PC.name().equals(clientType)) {
            this.messageSender.send(new MqMessage(AmqpExchange.PC_INDEX_CHANGE, AmqpExchange.PC_INDEX_CHANGE + "_ROUTING", cmsManageMsg));
        } else {
            this.messageSender.send(new MqMessage(AmqpExchange.MOBILE_INDEX_CHANGE, AmqpExchange.MOBILE_INDEX_CHANGE + "_ROUTING", cmsManageMsg));
        }
    }


}
