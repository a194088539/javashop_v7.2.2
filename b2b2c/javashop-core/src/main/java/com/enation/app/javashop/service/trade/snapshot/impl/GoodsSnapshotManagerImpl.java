package com.enation.app.javashop.service.trade.snapshot.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.client.goods.GoodsClient;
import com.enation.app.javashop.client.promotion.CouponClient;
import com.enation.app.javashop.client.promotion.PromotionGoodsClient;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.trade.order.OrderItemsMapper;
import com.enation.app.javashop.mapper.trade.snapshot.GoodsSnapshotMapper;
import com.enation.app.javashop.model.goods.dos.BrandDO;
import com.enation.app.javashop.model.goods.dos.CategoryDO;
import com.enation.app.javashop.model.goods.dos.GoodsDO;
import com.enation.app.javashop.model.goods.dos.GoodsGalleryDO;
import com.enation.app.javashop.model.goods.enums.Permission;
import com.enation.app.javashop.model.goods.vo.GoodsParamsGroupVO;
import com.enation.app.javashop.model.goods.vo.GoodsSnapshotVO;
import com.enation.app.javashop.model.goods.vo.SpecValueVO;
import com.enation.app.javashop.model.promotion.coupon.dos.CouponDO;
import com.enation.app.javashop.model.promotion.tool.vo.PromotionVO;
import com.enation.app.javashop.model.errorcode.TradeErrorCode;
import com.enation.app.javashop.model.trade.order.dos.OrderDO;
import com.enation.app.javashop.model.trade.order.dos.OrderItemsDO;
import com.enation.app.javashop.model.trade.order.vo.OrderSkuVO;
import com.enation.app.javashop.service.trade.order.OrderOperateManager;
import com.enation.app.javashop.model.trade.snapshot.SnapshotVO;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.security.model.Buyer;
import com.enation.app.javashop.framework.security.model.Seller;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.framework.util.JsonUtil;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import com.enation.app.javashop.service.trade.snapshot.GoodsSnapshotManager;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.javashop.model.trade.snapshot.GoodsSnapshot;

import java.util.List;

/**
 * 交易快照业务类
 *
 * @author fk
 * @version v1.0
 * @since v7.0.0
 * 2018-08-01 14:55:26
 */
@Service
public class GoodsSnapshotManagerImpl implements GoodsSnapshotManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private GoodsSnapshotMapper goodsSnapshotMapper;

    @Autowired
    private OrderItemsMapper orderItemsMapper;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private OrderOperateManager orderOperateManager;

    @Autowired
    private PromotionGoodsClient promotionGoodsClient;
    @Autowired
    private CouponClient couponClient;


    @Override
    public WebPage list(long page, long pageSize) {

        IPage<GoodsSnapshot> iPage = new QueryChainWrapper<>(goodsSnapshotMapper).page(new Page<>(page, pageSize));

        return PageConvert.convert(iPage);
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public GoodsSnapshot add(GoodsSnapshot goodsSnapshot) {

        //point为数据库关键字，直接使用Mybatisplus插入会报空指针，这里先将point属性设为null，插入后再去修改
        goodsSnapshotMapper.insert(goodsSnapshot);
        return goodsSnapshot;
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public GoodsSnapshot edit(GoodsSnapshot goodsSnapshot, Long id) {
        goodsSnapshot.setSnapshotId(id);
        goodsSnapshotMapper.updateById(goodsSnapshot);
        return goodsSnapshot;
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(Long id) {
        goodsSnapshotMapper.deleteById(id);
    }

    @Override
    public GoodsSnapshot getModel(Long id) {
        return goodsSnapshotMapper.selectById(id);
    }

    @Override
    public void add(OrderDO orderDO) {

        //查看订单中的商品
        List<OrderSkuVO> skuList = JsonUtil.jsonToList(orderDO.getItemsJson(), OrderSkuVO.class);
        if (skuList != null) {
            for (OrderSkuVO sku : skuList) {

                GoodsSnapshotVO snapshotGoods = goodsClient.queryGoodsSnapShotInfo(sku.getGoodsId());

                //商品的促销信息
                List<PromotionVO> promotionVOList = this.promotionGoodsClient.getPromotion(sku.getGoodsId());


                GoodsDO goods = snapshotGoods.getGoods();

                //商品的优惠券信息
                List<CouponDO> couponDOList = this.couponClient.getList(goods.getSellerId());

                CategoryDO category = snapshotGoods.getCategoryDO();
                List<GoodsGalleryDO> galleryList = snapshotGoods.getGalleryList();
                List<GoodsParamsGroupVO> paramList = snapshotGoods.getParamList();
                BrandDO brand = snapshotGoods.getBrandDO();

                GoodsSnapshot snapshot = new GoodsSnapshot();
                snapshot.setGoodsId(sku.getGoodsId());
                snapshot.setName(goods.getGoodsName());
                snapshot.setSn(goods.getSn());
                snapshot.setCategoryName(category.getName());
                snapshot.setBrandName(brand == null ? "" : brand.getName());
                snapshot.setGoodsType(goods.getGoodsType());
                snapshot.setHaveSpec(goods.getHaveSpec() == null ? 0 : goods.getHaveSpec());
                snapshot.setWeight(goods.getWeight());
                snapshot.setIntro(goods.getIntro());
                snapshot.setPrice(sku.getOriginalPrice());
                snapshot.setCost(goods.getCost());
                snapshot.setMktprice(goods.getMktprice());
                snapshot.setParamsJson(JsonUtil.objectToJson(paramList));
                snapshot.setImgJson(JsonUtil.objectToJson(galleryList));
                snapshot.setPoint(goods.getPoint());
                snapshot.setSellerId(goods.getSellerId());
                snapshot.setCreateTime(DateUtil.getDateline());
                snapshot.setPromotionJson(JsonUtil.objectToJson(promotionVOList));
                snapshot.setCouponJson(JsonUtil.objectToJson(couponDOList));
                snapshot.setMemberId(orderDO.getMemberId());
                snapshot.setMobileIntro(goods.getMobileIntro());
                //添加快照
                this.add(snapshot);
                Long snapshotId = snapshot.getSnapshotId();
                sku.setSnapshotId(snapshotId);
                //更新订单项的快照id
                new UpdateChainWrapper<>(orderItemsMapper)
                        //设置快照id
                        .set("snapshot_id", snapshotId)
                        //拼接订单编号修改条件
                        .eq("order_sn", orderDO.getSn())
                        //拼接货品ID修改条件
                        .eq("product_id", sku.getSkuId())
                        //提交修改
                        .update();

            }
            logger.debug("生成商品快照信息");
            //更新订单
            orderOperateManager.updateItemJson(JsonUtil.objectToJson(skuList), orderDO.getSn());
        }
    }

    @Override
    public SnapshotVO get(Long id, String owner) {

        GoodsSnapshot model = this.getModel(id);
        if (!Permission.SELLER.name().equals(owner) && !Permission.BUYER.name().equals(owner)) {
            logger.debug("传参错误");
            throw new ServiceException(TradeErrorCode.E453.code(), "无权查看，请联系管理员");
        }
        //查看是卖家查看还是买家查看
        if (Permission.SELLER.name().equals(owner)) {
            Seller seller = UserContext.getSeller();
            if (seller == null || !seller.getSellerId().equals(model.getSellerId())) {
                logger.debug("seller == null？" + (seller == null) + ",equals?" + seller.getSellerId() + "==" + model.getSellerId());
                throw new ServiceException(TradeErrorCode.E453.code(), "无权查看");
            }
        }
        if (Permission.BUYER.name().equals(owner)) {
            Buyer buyer = UserContext.getBuyer();
            if (buyer == null || !buyer.getUid().equals(model.getMemberId())) {
                logger.debug("buyer == null？" + (buyer == null) + ",equals?" + buyer.getUid() + "==" + model.getMemberId());
                throw new ServiceException(TradeErrorCode.E453.code(), "无权查看");
            }
        }
        SnapshotVO snapshotVO = new SnapshotVO();
        BeanUtils.copyProperties(model, snapshotVO);

        if (model.getHaveSpec() == 1) {
            //有规格
            OrderItemsDO items = new QueryChainWrapper<>(orderItemsMapper)
                    //按快照id查询
                    .eq("snapshot_id", id)
                    //查询单个对象
                    .one();
            List<SpecValueVO> specs = JsonUtil.jsonToList(items.getSpecJson(), SpecValueVO.class);
            snapshotVO.setSpecList(specs);
        }

        return snapshotVO;
    }
}
