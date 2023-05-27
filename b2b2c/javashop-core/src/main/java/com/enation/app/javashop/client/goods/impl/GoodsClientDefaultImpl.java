package com.enation.app.javashop.client.goods.impl;

import com.enation.app.javashop.client.goods.GoodsClient;
import com.enation.app.javashop.client.promotion.ExchangeGoodsClient;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.goods.dos.BrandDO;
import com.enation.app.javashop.model.goods.dos.CategoryDO;
import com.enation.app.javashop.model.goods.dos.GoodsDO;
import com.enation.app.javashop.model.goods.dos.GoodsGalleryDO;
import com.enation.app.javashop.model.goods.dto.GoodsQueryParam;
import com.enation.app.javashop.model.goods.enums.GoodsType;
import com.enation.app.javashop.model.goods.vo.*;
import com.enation.app.javashop.model.promotion.exchange.dos.ExchangeDO;
import com.enation.app.javashop.model.trade.order.vo.OrderSkuVO;
import com.enation.app.javashop.service.goods.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author fk
 * @version v2.0
 * @Description: 商品对外的接口实现
 * @date 2018/7/26 10:43
 * @since v7.0.0
 */
@Service
@ConditionalOnProperty(value = "javashop.product", havingValue = "stand")
public class GoodsClientDefaultImpl implements GoodsClient {

    @Autowired
    private GoodsManager goodsManager;
    @Autowired
    private GoodsQueryManager goodsQueryManager;

    @Autowired
    private GoodsSkuManager goodsSkuManager;

    @Autowired
    private GoodsParamsManager goodsParamsManager;
    @Autowired
    private CategoryManager categoryManager;
    @Autowired
    private BrandManager brandManager;
    @Autowired
    private GoodsGalleryManager goodsGalleryManager;
    @Autowired
    private ExchangeGoodsClient exchangeGoodsClient;

    @Override
    public List<BackendGoodsVO> newGoods(Integer length) {

        return this.goodsManager.newGoods(length);
    }

    @Override
    public void underShopGoods(Long sellerId) {

        this.goodsManager.underShopGoods(sellerId);
    }

    @Override
    public void updateGoodsGrade() {

        this.goodsManager.updateGoodsGrade();
    }

    @Override
    public CacheGoods getFromCache(Long goodsId) {

        return this.goodsQueryManager.getFromCache(goodsId);
    }

    @Override
    public List<GoodsSelectLine> query(Long[] goodsIds) {

        return this.goodsQueryManager.queryGoodsLines(goodsIds, new GoodsQueryParam());
    }

    @Override
    public List<GoodsSelectLine> querySkus(Long[] skuIds) {

        return this.goodsQueryManager.querySkus(skuIds);
    }

    @Override
    public List<GoodsDO> queryDo(Long[] goodsIds) {

        return this.goodsQueryManager.queryDo(goodsIds);
    }

    @Override
    public void checkSellerGoodsCount(Long[] goodsIds) {

        this.goodsQueryManager.checkSellerGoodsCount(goodsIds);
    }

    @Override
    public List<Map<String, Object>> getGoodsAndParams(Long[] goodsIds) {

        return this.goodsQueryManager.getGoodsAndParams(goodsIds);
    }

    @Override
    public List<Map<String, Object>> getGoodsAndParams(Long sellerId) {
        return this.goodsQueryManager.getGoodsAndParams(sellerId);
    }

    @Override
    public List<GoodsDO> listGoods(Long sellerId) {

        return this.goodsQueryManager.listGoods(sellerId);
    }

    @Override
    public GoodsSkuVO getSkuFromCache(Long skuId) {

        return this.goodsSkuManager.getSkuFromCache(skuId);
    }

    @Override
    public GoodsSkuVO getSku(Long skuId) {
        return this.goodsSkuManager.getSku(skuId);
    }

    @Override
    public List<Map<String, Object>> getGoods(Long[] goodsIds) {
        return goodsQueryManager.getGoods(goodsIds);
    }

    @Override
    public void updateCommentCount(Long goodsId, Integer num) {

        this.goodsManager.updateCommentCount(goodsId, num);
    }

    @Override
    public void updateBuyCount(List<OrderSkuVO> list) {
        this.goodsManager.updateBuyCount(list);
    }

    @Override
    public Integer queryGoodsCount() {
        return this.goodsQueryManager.getGoodsCountByParam(null, null, 1);
    }

    @Override
    public Integer queryGoodsCountByParam(Integer status, Long sellerId) {
        return this.goodsQueryManager.getGoodsCountByParam(status, sellerId, 1);
    }


    @Override
    public List<Map<String,Object>> queryGoodsByRange(Long pageNo, Long pageSize) {

        return this.goodsQueryManager.queryGoodsByRange(pageNo, pageSize);
    }

    @Override
    public GoodsSnapshotVO queryGoodsSnapShotInfo(Long goodsId) {

        //商品
        GoodsDO goods = this.goodsQueryManager.getModel(goodsId);

        //判断是否为积分商品
        if (GoodsType.POINT.name().equals(goods.getGoodsType())) {
            //积分兑换信息
            ExchangeDO exchangeDO = this.exchangeGoodsClient.getModelByGoods(goodsId);
            goods.setPoint(exchangeDO.getExchangePoint());
        }


        //参数
        List<GoodsParamsGroupVO> paramList = goodsParamsManager.queryGoodsParams(goods.getCategoryId(), goodsId);
        //品牌
        BrandDO brand = brandManager.getModel(goods.getBrandId());
        //分类
        CategoryDO category = categoryManager.getModel(goods.getCategoryId());
        //相册
        List<GoodsGalleryDO> galleryList = goodsGalleryManager.list(goodsId);

        return new GoodsSnapshotVO(goods, paramList, brand, category, galleryList);
    }

    @Override
    public WebPage list(GoodsQueryParam goodsQueryParam) {

        return goodsQueryManager.list(goodsQueryParam);
    }

    @Override
    public CategoryDO getCategory(Long id) {

        return categoryManager.getModel(id);
    }

    /**
     * 校验商品模版是否使用
     *
     * @param templateId
     * @return 商品名称
     */
    @Override
    public GoodsDO checkShipTemplate(Long templateId) {
        return goodsManager.checkShipTemplate(templateId);
    }

    @Override
    public Integer getSellerGoodsCount(Long sellerId) {

        return goodsQueryManager.getSellerGoodsCount(sellerId);
    }

    /**
     * 修改某店铺商品店铺名称
     *
     * @param sellerId   商家id
     * @param sellerName 商家名称
     */
    @Override
    public void changeSellerName(Long sellerId, String sellerName) {
        this.goodsManager.changeSellerName(sellerId, sellerName);
    }

    @Override
    public void updateGoodsType(Long sellerId, String type) {
        goodsManager.updateGoodsType(sellerId, type);
    }

    @Override
    public List<GoodsDO> listPointGoods(Long shopId) {
        return this.goodsQueryManager.getSellerGoods(shopId,GoodsType.POINT);
    }

    @Override
    public List<GoodsSkuVO> listByGoodsId(Long goodsId) {
        return goodsSkuManager.listByGoodsId(goodsId);
    }

    @Override
    public List<GoodsDO> getSellerGoods(Long sellerId) {

        return goodsQueryManager.getSellerGoods(sellerId,null);
    }

    @Override
    public List<CategoryVO> listAllChildren(Long parentId) {

        return categoryManager.listAllChildren(parentId);
    }

    @Override
    public void createSkuExchangeScript(Long skuId, Long exchangeId, Double price, Integer point) {
        this.goodsSkuManager.createSkuExchangeScript(skuId, exchangeId, price, point);
    }

    @Override
    public void deleteSkuExchangeScript(Long skuId) {
        this.goodsSkuManager.deleteSkuExchangeScript(skuId);
    }
}
