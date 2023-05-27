package com.enation.app.javashop.service.aftersale.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.client.payment.PaymentClient;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.security.model.Seller;
import com.enation.app.javashop.mapper.trade.aftersale.AfterSaleExpressMapper;
import com.enation.app.javashop.mapper.trade.aftersale.AfterSaleServiceMapper;
import com.enation.app.javashop.mapper.trade.order.OrderItemsMapper;
import com.enation.app.javashop.model.errorcode.AftersaleErrorCode;
import com.enation.app.javashop.model.aftersale.dos.*;
import com.enation.app.javashop.model.aftersale.dto.AfterSaleOrderDTO;
import com.enation.app.javashop.model.aftersale.dto.AfterSaleQueryParam;
import com.enation.app.javashop.model.aftersale.dto.ServiceOperateAllowable;
import com.enation.app.javashop.model.aftersale.enums.AccountTypeEnum;
import com.enation.app.javashop.model.aftersale.enums.RefundWayEnum;
import com.enation.app.javashop.model.aftersale.enums.ServiceStatusEnum;
import com.enation.app.javashop.model.aftersale.enums.ServiceTypeEnum;
import com.enation.app.javashop.model.aftersale.vo.AfterSaleExportVO;
import com.enation.app.javashop.model.aftersale.vo.AfterSaleRecordVO;
import com.enation.app.javashop.model.aftersale.vo.ApplyAfterSaleVO;
import com.enation.app.javashop.model.goods.enums.Permission;
import com.enation.app.javashop.service.aftersale.*;
import com.enation.app.javashop.client.member.ShopClient;
import com.enation.app.javashop.client.system.LogiCompanyClient;
import com.enation.app.javashop.model.errorcode.MemberErrorCode;
import com.enation.app.javashop.model.payment.dos.PaymentMethodDO;
import com.enation.app.javashop.model.promotion.fulldiscount.dos.FullDiscountGiftDO;
import com.enation.app.javashop.model.shop.vo.ShopVO;
import com.enation.app.javashop.model.system.enums.DeleteStatusEnum;
import com.enation.app.javashop.model.system.dos.LogisticsCompanyDO;
import com.enation.app.javashop.model.errorcode.TradeErrorCode;
import com.enation.app.javashop.model.trade.order.dos.OrderDO;
import com.enation.app.javashop.model.trade.order.dos.OrderItemsDO;
import com.enation.app.javashop.model.trade.order.dos.OrderMetaDO;
import com.enation.app.javashop.model.trade.order.enums.OrderMetaKeyEnum;
import com.enation.app.javashop.model.trade.order.enums.OrderServiceStatusEnum;
import com.enation.app.javashop.model.trade.order.enums.OrderTypeEnum;
import com.enation.app.javashop.service.trade.order.OrderMetaManager;
import com.enation.app.javashop.service.trade.order.OrderQueryManager;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.security.model.Buyer;
import com.enation.app.javashop.framework.util.BeanUtil;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.framework.util.JsonUtil;
import com.enation.app.javashop.framework.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 售后服务查询管理接口实现
 *
 * @author duanmingyu
 * @version v1.0
 * @since v7.1.5
 * 2019-12-04
 */
@SuppressWarnings("Duplicates")
@Service
public class AfterSaleQueryManagerImpl implements AfterSaleQueryManager {
    @Autowired
    private AfterSaleServiceMapper afterSaleServiceMapper;

    @Autowired
    private AfterSaleExpressMapper afterSaleExpressMapper;

    @Autowired
    private OrderItemsMapper orderItemsMapper;

    @Autowired
    private AfterSaleRefundManager afterSaleRefundManager;

    @Autowired
    private AfterSaleGoodsManager afterSaleGoodsManager;

    @Autowired
    private AfterSaleChangeManager afterSaleChangeManager;

    @Autowired
    private AfterSaleGalleryManager afterSaleGalleryManager;

    @Autowired
    private AfterSaleLogManager afterSaleLogManager;

    @Autowired
    private OrderQueryManager orderQueryManager;

    @Autowired
    private PaymentClient paymentClient;

    @Autowired
    private OrderMetaManager orderMetaManager;

    @Autowired
    private LogiCompanyClient logiCompanyClient;

    @Autowired
    private ShopClient shopClient;

    @Override
    public WebPage list(AfterSaleQueryParam param) {
        //新建查询条件包装器
        QueryWrapper<AfterSaleServiceDO> wrapper = new QueryWrapper<>();
        //查询所有正常状态的售后服务单信息
        wrapper.eq("disabled", DeleteStatusEnum.NORMAL.value());
        //如果会员ID不为空并且不等于0，则以会员ID为查询条件
        wrapper.eq(param.getMemberId() != null && param.getMemberId() != 0, "member_id", param.getMemberId());
        //如果商家ID不为空并且不等于0，则以商家ID为查询条件
        wrapper.eq(param.getSellerId() != null && param.getSellerId() != 0, "seller_id", param.getSellerId());
        //如果查询关键字不为空，以售后服务单号或订单编号或售后商品信息作为条件进行模糊查询
        wrapper.and(StringUtil.notEmpty(param.getKeyword()), ew -> {
            ew.like("sn", param.getKeyword()).or().like("order_sn", param.getKeyword()).or().like("goods_json", param.getKeyword());
        });
        //如果售后服务单号不为空，则以售后服务单号作为条件进行模糊查询
        wrapper.like(StringUtil.notEmpty(param.getServiceSn()), "sn", param.getServiceSn());
        //如果订单编号不为空，则以订单编号作为条件进行模糊查询
        wrapper.like(StringUtil.notEmpty(param.getOrderSn()), "order_sn", param.getOrderSn());
        //如果商品名称不为空，则以商品名称作为条件进行模糊查询
        wrapper.like(StringUtil.notEmpty(param.getGoodsName()), "goods_json", param.getGoodsName());
        //如果售后服务类型不为空，则以售后服务类型为查询条件
        wrapper.eq(StringUtil.notEmpty(param.getServiceType()), "service_type", param.getServiceType());
        //如果售后服务状态不为空，则以售后服务状态为查询条件
        wrapper.eq(StringUtil.notEmpty(param.getServiceStatus()), "service_status", param.getServiceStatus());
        //如果申请时间-起始时间不为空也不等于0，则按售后服务申请时间大于等于当前这个起始时间进行查询
        wrapper.ge(param.getStartTime() != null && param.getStartTime() != 0, "create_time", param.getStartTime());
        //如果申请时间-结束时间不为空也不等于0，则按售后服务申请时间小于等于当前这个结束时间进行查询
        wrapper.le(param.getEndTime() != null && param.getEndTime() != 0, "create_time", param.getEndTime());
        //如果售后服务创建渠道不为空，则以售后服务创建渠道为查询条件
        wrapper.eq(StringUtil.notEmpty(param.getCreateChannel()), "create_channel", param.getCreateChannel());
        //按照售后服务申请时间倒序排序
        wrapper.orderByDesc("create_time");
        //获取售后服务分页信息数据
        IPage<AfterSaleServiceDO> iPage = afterSaleServiceMapper.selectPage(new Page<>(param.getPageNo(), param.getPageSize()), wrapper);

        //转换商品数据
        List<AfterSaleServiceDO> serviceDOList = iPage.getRecords();
        List<AfterSaleRecordVO> recordVOList = new ArrayList<>();

        for (AfterSaleServiceDO serviceDO : serviceDOList) {
            AfterSaleRecordVO recordVO = new AfterSaleRecordVO(serviceDO);

            //获取售后服务允许操作信息
            ServiceOperateAllowable allowable = new ServiceOperateAllowable(serviceDO);
            recordVO.setAllowable(allowable);

            recordVOList.add(recordVO);
        }

        //返回转换后的分页数据信息
        WebPage<AfterSaleRecordVO> webPage = new WebPage(param.getPageNo(), iPage.getTotal(), param.getPageSize(), recordVOList);
        return webPage;
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AfterSaleOrderDTO applyOrderInfo(String orderSn, Long skuId) {
        //获取当前登录的会员信息
        Buyer buyer = UserContext.getBuyer();
        if (buyer == null) {
            throw new ServiceException(MemberErrorCode.E110.code(), "当前会员已经退出登录");
        }
        //订单编号和商品skuID不能为空
        if (StringUtil.isEmpty(orderSn) || skuId == null) {
            throw new ServiceException(AftersaleErrorCode.E601.name(), "参数上传有误，不允许操作");
        }

        //获取要申请售后的订单信息
        OrderDO order = this.orderQueryManager.getOrder(orderSn);
        //不存在的订单或者不属于当前会员的订单进行校验
        if (order == null || !buyer.getUid().equals(order.getMemberId())) {
            throw new ServiceException(AftersaleErrorCode.E604.name(), "申请售后服务的订单信息不存在");
        }

        //获取订单的支付方式
        PaymentMethodDO paymentMethodDO = null;
        if (order.getPaymentPluginId() != null) {
            paymentMethodDO = this.paymentClient.getByPluginId(order.getPaymentPluginId());
        }

        //根据订单编号和商品skuID获取订单项信息
        OrderItemsDO itemsDO = this.getOrderItems(orderSn, skuId);
        if (itemsDO == null) {
            throw new ServiceException(AftersaleErrorCode.E604.name(), "申请售后服务的订单信息不存在");
        }

        List<FullDiscountGiftDO> resultList = new ArrayList<>();
        //获取订单赠品信息json
        OrderMetaDO orderMetaDO = this.orderMetaManager.getModel(orderSn, OrderMetaKeyEnum.GIFT);
        if (orderMetaDO != null) {
            List<FullDiscountGiftDO> giftList = JsonUtil.jsonToList(orderMetaDO.getMetaValue(), FullDiscountGiftDO.class);
            if (giftList != null && giftList.size() != 0) {
                for (FullDiscountGiftDO giftDO : giftList) {
                    if (OrderServiceStatusEnum.NOT_APPLY.value().equals(orderMetaDO.getStatus())) {
                        resultList.add(giftDO);
                    }
                }
            }
        }

        AfterSaleOrderDTO afterSaleOrderDTO = new AfterSaleOrderDTO();
        afterSaleOrderDTO.setOrderSn(orderSn);
        afterSaleOrderDTO.setGoodId(itemsDO.getGoodsId());
        afterSaleOrderDTO.setSkuId(skuId);
        afterSaleOrderDTO.setGoodsName(itemsDO.getName());
        afterSaleOrderDTO.setGoodsImg(itemsDO.getImage());
        afterSaleOrderDTO.setGoodsPrice(itemsDO.getPrice());
        afterSaleOrderDTO.setBuyNum(itemsDO.getNum());
        afterSaleOrderDTO.setProvinceId(order.getShipProvinceId());
        afterSaleOrderDTO.setCityId(order.getShipCityId());
        afterSaleOrderDTO.setCountyId(order.getShipCountyId());
        afterSaleOrderDTO.setTownId(order.getShipTownId());
        afterSaleOrderDTO.setProvince(order.getShipProvince());
        afterSaleOrderDTO.setCity(order.getShipCity());
        afterSaleOrderDTO.setCounty(order.getShipCounty());
        afterSaleOrderDTO.setTown(order.getShipTown());
        afterSaleOrderDTO.setShipAddr(order.getShipAddr());
        afterSaleOrderDTO.setShipName(order.getShipName());
        afterSaleOrderDTO.setShipMobile(order.getShipMobile());
        afterSaleOrderDTO.setGiftList(resultList);
        afterSaleOrderDTO.setSellerName(order.getSellerName());

        //如果订单类型是换货或补发商品售后服务重新生成的订单，或者订单金额为0的订单，那么不允许申请退货
        if (OrderTypeEnum.CHANGE.name().equals(order.getOrderType()) || OrderTypeEnum.SUPPLY_AGAIN.name().equals(order.getOrderType()) || order.getOrderPrice().doubleValue() == 0) {
            afterSaleOrderDTO.setAllowReturnGoods(false);
        } else {
            afterSaleOrderDTO.setAllowReturnGoods(true);
        }

        //如果订单没有支付方式信息或者支付方式不支持原路退款
        if (paymentMethodDO == null || paymentMethodDO.getIsRetrace() == 0) {
            afterSaleOrderDTO.setIsRetrace(false);
        } else {
            afterSaleOrderDTO.setIsRetrace(true);
        }

        //判断是否使用预存款支付  如果使用预存款支付则全部退款至预存款中
        if (order.getBalance() != null && order.getBalance() > 0) {
            afterSaleOrderDTO.setIsRetraceBalance(true);
        } else {
            afterSaleOrderDTO.setIsRetraceBalance(false);
        }

        //订单是否含有发票
        afterSaleOrderDTO.setIsReceipt(order.getNeedReceipt() == 1 ? true : false);
        return afterSaleOrderDTO;
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ApplyAfterSaleVO detail(String serviceSn, Permission permission) {

        //售后服务单编号不能为空
        if (StringUtil.isEmpty(serviceSn)) {
            throw new ServiceException(AftersaleErrorCode.E614.name(), "售后服务单编号不能为空");
        }

        //根据售后服务单号获取服务单信息
        AfterSaleServiceDO afterSaleServiceDO = this.getService(serviceSn);

        if (afterSaleServiceDO == null) {
            throw new ServiceException(AftersaleErrorCode.E614.name(), "售后服务单信息不存在");
        }

        switch (permission) {
            case SELLER:
                Seller seller = UserContext.getSeller();
                if (seller == null || !seller.getSellerId().equals(afterSaleServiceDO.getSellerId())) {
                    throw new ServiceException(AftersaleErrorCode.E614.name(), "售后服务单信息不存在");
                }
                break;
            case BUYER:
                Buyer buyer = UserContext.getBuyer();
                if (buyer == null || !buyer.getUid().equals(afterSaleServiceDO.getMemberId())) {
                    throw new ServiceException(AftersaleErrorCode.E614.name(), "售后服务单信息不存在");
                }
                break;
            default:
                break;
        }

        //获取申请售后的订单信息
        OrderDO orderDO = this.orderQueryManager.getOrder(afterSaleServiceDO.getOrderSn());

        if (orderDO == null) {
            throw new ServiceException(TradeErrorCode.E453.name(), "订单信息不存在");
        }

        ApplyAfterSaleVO applyAfterSaleVO = new ApplyAfterSaleVO();
        BeanUtil.copyProperties(afterSaleServiceDO, applyAfterSaleVO);

        //如果售后服务类型为退货或取消订单，则需要获取退款账户相关信息
        if (ServiceTypeEnum.RETURN_GOODS.value().equals(applyAfterSaleVO.getServiceType()) || ServiceTypeEnum.ORDER_CANCEL.value().equals(applyAfterSaleVO.getServiceType())) {
            AfterSaleRefundDO afterSaleRefundDO = this.afterSaleRefundManager.getAfterSaleRefund(serviceSn);
            applyAfterSaleVO.setRefundInfo(afterSaleRefundDO);
        }

        //获取售后服务单允许操作情况
        ServiceOperateAllowable allowable = new ServiceOperateAllowable(afterSaleServiceDO);
        applyAfterSaleVO.setAllowable(allowable);

        //获取申请售后的商品信息集合
        List<AfterSaleGoodsDO> goodsList = this.afterSaleGoodsManager.listGoods(serviceSn);
        applyAfterSaleVO.setGoodsList(goodsList);

        //获取售后服务收货地址相关信息
        AfterSaleChangeDO afterSaleChangeDO = this.afterSaleChangeManager.getModel(serviceSn);
        applyAfterSaleVO.setChangeInfo(afterSaleChangeDO);

        //获取售后服务物流相关信息
        AfterSaleExpressDO afterSaleExpressDO = this.getExpress(serviceSn);
        applyAfterSaleVO.setExpressInfo(afterSaleExpressDO);

        //获取售后服务用户上传的图片信息
        List<AfterSaleGalleryDO> images = this.afterSaleGalleryManager.listImages(serviceSn);
        applyAfterSaleVO.setImages(images);

        //获取售后服务日志相关信息
        List<AfterSaleLogDO> logs = this.afterSaleLogManager.list(serviceSn);
        applyAfterSaleVO.setLogs(logs);

        //获取平台所有的正常开启使用的物流公司信息集合
        List<LogisticsCompanyDO> logiList = this.logiCompanyClient.listAllNormal();
        applyAfterSaleVO.setLogiList(logiList);

        //获取订单的发货状态
        applyAfterSaleVO.setOrderShipStatus(orderDO.getShipStatus());
        //获取订单的付款类型
        applyAfterSaleVO.setOrderPaymentType(orderDO.getPaymentType());

        //如果退货地址为空，那么需要获取商家店铺的默认地址作为退货地址
        if (StringUtil.isEmpty(applyAfterSaleVO.getReturnAddr())) {
            ShopVO shopDetailDO = this.shopClient.getShop(applyAfterSaleVO.getSellerId());
            String returnAddr = "收货人：" + shopDetailDO.getLinkName() + "，联系方式：" + shopDetailDO.getLinkPhone() + "，地址："
                    + shopDetailDO.getShopProvince() + shopDetailDO.getShopCity() + shopDetailDO.getShopCounty() + shopDetailDO.getShopTown() + "  " + shopDetailDO.getShopAdd();
            applyAfterSaleVO.setReturnAddr(returnAddr);
        }

        return applyAfterSaleVO;
    }

    @Override
    public List<AfterSaleExportVO> exportAfterSale(AfterSaleQueryParam param) {
        //查询需要导出的售后服务信息
        List<AfterSaleExportVO> exportList = afterSaleServiceMapper.selectExportVoList(DeleteStatusEnum.NORMAL.value(), param);

        //循环结果集，并对结果集相关数据进行转换
        for (AfterSaleExportVO exportVO : exportList) {
            //转换售后服务类型和状态
            exportVO.setServiceTypeText(ServiceTypeEnum.valueOf(exportVO.getServiceType()).description());
            exportVO.setServiceStatusText(ServiceStatusEnum.valueOf(exportVO.getServiceStatus()).description());

            //转换退款方式
            if (StringUtil.notEmpty(exportVO.getRefundWay())) {
                exportVO.setRefundWayText(RefundWayEnum.valueOf(exportVO.getRefundWay()).description());
            }

            //转换账户类型
            if (StringUtil.notEmpty(exportVO.getAccountType())) {
                exportVO.setAccountTypeText(AccountTypeEnum.valueOf(exportVO.getAccountType()).description());
            }

            //组合商品信息
            List<AfterSaleGoodsDO> goodsList = this.afterSaleGoodsManager.listGoods(exportVO.getServiceSn());
            String goodsInfo = "";
            for (AfterSaleGoodsDO goodsDO : goodsList) {
                String storageNum = goodsDO.getStorageNum() == null ? "未入库" : goodsDO.getStorageNum() + "";
                goodsInfo += "【商品名称：" + goodsDO.getGoodsName() + "，商品价格：" + goodsDO.getPrice() + "，购买数量：" + goodsDO.getShipNum() + "，申请售后数量：" + goodsDO.getReturnNum() + "，入库数量：" + storageNum + "】";
            }
            exportVO.setGoodsInfo(goodsInfo);

            //组合收货地址信息
            AfterSaleChangeDO changeDO = this.afterSaleChangeManager.getModel(exportVO.getServiceSn());
            String rogInfo = "收货地址：" + changeDO.getProvince() + changeDO.getCity() + changeDO.getCounty() + changeDO.getTown() + changeDO.getShipAddr() + "，收货人：" + changeDO.getShipName() + "，联系方式：" + changeDO.getShipMobile();
            exportVO.setRogInfo(rogInfo);

            //组合用户退还商品的物流信息
            AfterSaleExpressDO expressDO = this.getExpress(exportVO.getServiceSn());
            if (expressDO != null) {
                String expressInfo = "物流公司：" + expressDO.getCourierCompany() + "，快递单号：" + expressDO.getCourierNumber() + "，发货时间：" + DateUtil.toString(expressDO.getShipTime(), "yyyy-MM-dd");
                exportVO.setExpressInfo(expressInfo);
            }

        }

        return exportList;
    }

    @Override
    public Integer getAfterSaleCount(Long memberId, Long sellerId) {
        //新建查询条件包装器
        QueryWrapper<AfterSaleServiceDO> wrapper = new QueryWrapper<>();
        //以售后服务状态不等于完成状态为查询条件
        wrapper.ne("service_status", ServiceStatusEnum.COMPLETED.value());
        //以售后服务状态不等于审核未通过状态为查询条件
        wrapper.ne("service_status", ServiceStatusEnum.REFUSE.value());
        //如果会员ID不等于空，则以会员ID为条件查询
        wrapper.eq(memberId != null, "member_id", memberId);
        //如果商家ID不等于空，则以商家ID为条件查询
        wrapper.eq(sellerId != null, "seller_id", sellerId);
        //返回售后服务数量
        return afterSaleServiceMapper.selectCount(wrapper);
    }

    @Override
    public AfterSaleServiceDO getService(String serviceSn) {
        //新建查询条件包装器
        QueryWrapper<AfterSaleServiceDO> wrapper = new QueryWrapper<>();
        //以售后服务单号为查询条件
        wrapper.eq("sn", serviceSn);
        //返回查询出的售后服务单信息
        return afterSaleServiceMapper.selectOne(wrapper);
    }

    @Override
    public AfterSaleServiceDO getCancelService(String orderSn) {
        //新建查询条件包装器
        QueryWrapper<AfterSaleServiceDO> wrapper = new QueryWrapper<>();
        //以订单编号为查询条件
        wrapper.eq("order_sn", orderSn);
        //以售后服务单类型为取消订单作为查询条件
        wrapper.eq("service_type", ServiceTypeEnum.ORDER_CANCEL.value());
        //以售后服务状态不等于审核未通过状态为查询条件
        wrapper.ne("service_status", ServiceStatusEnum.REFUSE.value());
        //以售后服务状态不等于已关闭状态为查询条件
        wrapper.ne("service_status", ServiceStatusEnum.CLOSED.value());
        //返回查询出的售后服务单信息
        return afterSaleServiceMapper.selectOne(wrapper);
    }

    @Override
    public AfterSaleExpressDO getExpress(String serviceSn) {
        QueryWrapper<AfterSaleExpressDO> wrapper = new QueryWrapper<>();
        //以售后服务单号为查询条件
        wrapper.eq("service_sn", serviceSn);
        //返回查询出的售后物流信息
        return afterSaleExpressMapper.selectOne(wrapper);
    }

    /**
     * 获取订单项信息
     *
     * @param orderSn 订单编号
     * @param skuId   商品skuID
     * @return
     */
    private OrderItemsDO getOrderItems(String orderSn, Long skuId) {
        //新建查询条件包装器
        QueryWrapper<OrderItemsDO> wrapper = new QueryWrapper<>();
        //以订单编号为查询条件
        wrapper.eq("order_sn", orderSn);
        //以订单商品sku为查询条件
        wrapper.eq("product_id", skuId);
        //获取订单项信息
        OrderItemsDO orderItemsDO = orderItemsMapper.selectOne(wrapper);
        return orderItemsDO;
    }
}
