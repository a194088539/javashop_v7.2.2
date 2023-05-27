package com.enation.app.javashop.service.aftersale;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.aftersale.dos.AfterSaleExpressDO;
import com.enation.app.javashop.model.aftersale.dos.AfterSaleServiceDO;
import com.enation.app.javashop.model.aftersale.dto.AfterSaleOrderDTO;
import com.enation.app.javashop.model.aftersale.dto.AfterSaleQueryParam;
import com.enation.app.javashop.model.aftersale.vo.AfterSaleExportVO;
import com.enation.app.javashop.model.aftersale.vo.ApplyAfterSaleVO;
import com.enation.app.javashop.model.goods.enums.Permission;

import java.util.List;

/**
 * 售后服务查询管理接口
 * @author duanmingyu
 * @version v1.0
 * @since v7.1.5
 * 2019-12-04
 */
public interface AfterSaleQueryManager {

    /**
     * 获取申请售后服务记录列表
     * @param param 查询条件
     * @return
     */
    WebPage list(AfterSaleQueryParam param);

    /**
     * 获取申请售后页面订单收货信息和商品信息
     * @param orderSn 订单ID
     * @param skuId 商品skuID
     * @return
     */
    AfterSaleOrderDTO applyOrderInfo(String orderSn, Long skuId);

    /**
     * 获取售后服务详细
     * @param serviceSn 售后服务单号
     * @param permission 权限
     * @return
     */
    ApplyAfterSaleVO detail(String serviceSn, Permission permission);

    /**
     * 根据条件导出售后服务信息
     * @param param 导出条件信息
     * @return
     */
    List<AfterSaleExportVO> exportAfterSale(AfterSaleQueryParam param);

    /**
     * 获取还未完成的售后服务
     * @param memberId 会员id
     * @param sellerId 商家id
     * @return
     */
    Integer getAfterSaleCount(Long memberId, Long sellerId);

    /**
     * 根据售后服务单编号获取售后服务单信息
     * @param serviceSn 售后服务单编号
     * @return
     */
    AfterSaleServiceDO getService(String serviceSn);

    /**
     * 根据订单编号获取取消订单售后服务信息
     * @param orderSn 订单编号
     * @return
     */
    AfterSaleServiceDO getCancelService(String orderSn);

    /**
     * 根据售后服务单号获取物流相关信息
     * @param serviceSn 售后服务单编号
     * @return
     */
    AfterSaleExpressDO getExpress(String serviceSn);

}
