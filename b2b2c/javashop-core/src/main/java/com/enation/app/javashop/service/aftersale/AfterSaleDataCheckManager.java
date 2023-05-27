package com.enation.app.javashop.service.aftersale;

import com.enation.app.javashop.model.aftersale.dos.AfterSaleExpressDO;
import com.enation.app.javashop.model.aftersale.dos.AfterSaleRefundDO;
import com.enation.app.javashop.model.aftersale.dto.AfterSaleApplyDTO;
import com.enation.app.javashop.model.aftersale.dto.PutInWarehouseDTO;
import com.enation.app.javashop.model.aftersale.vo.ApplyAfterSaleVO;
import com.enation.app.javashop.model.aftersale.vo.RefundApplyVO;

import java.util.List;

/**
 * 售后服务数据校验接口
 * @author duanmingyu
 * @version v1.0
 * @since v7.1.5
 * 2019-12-03
 */
public interface AfterSaleDataCheckManager {

    /**
     * 校验售后服务申请参数并获取构成售后服务单的相关数据
     * @param applyAfterSaleVO 售后服务单数据
     * @return
     */
    AfterSaleApplyDTO checkApplyService(ApplyAfterSaleVO applyAfterSaleVO);

    /**
     * 校验取消订单申请参数并获取构成售后服务单的相关数据
     * @param refundApplyVO 售后服务单数据
     * @return
     */
    AfterSaleApplyDTO checkCancelOrder(RefundApplyVO refundApplyVO);

    /**
     * 校验退货或取消订单申请参数中的退款相关信息
     * @param refundDO 退款相关信息
     */
    void checkRefundInfo(AfterSaleRefundDO refundDO);

    /**
     * 校验用户退还售后商品填写的物流信息并返回物流公司名称
     * @param afterSaleExpressDO 售后服务物流信息
     * @return
     */
    String checkAfterSaleExpress(AfterSaleExpressDO afterSaleExpressDO);

    /**
     * 商家审核售后服务申请校验并返回售后服务单详细信息
     * @param serviceSn 售后服务单号
     * @param auditStatus 审核状态
     * @param refundPrice 商家同意的退款金额
     * @param returnAddr 退货地址信息
     * @param auditRemark 审核备注信息
     * @return
     */
    ApplyAfterSaleVO checkAudit(String serviceSn, String auditStatus, Double refundPrice, String returnAddr, String auditRemark);

    /**
     * 商家入库操作校验
     * @param serviceSn 售后服务单号
     * @param storageList 入库信息
     * @param remark 入库备注
     */
    void checkPutInWarehouse(String serviceSn, List<PutInWarehouseDTO> storageList, String remark);

    /**
     * 校验关闭售后服务单参数
     * @param serviceSn 售后服务单号
     * @param remark 备注
     */
    void checkCloseAfterSale(String serviceSn, String remark);

    /**
     * 校验平台退款参数
     * @param serviceSn 售后服务单号
     * @param refundPrice 退款金额
     * @param remark 备注
     */
    void checkAdminRefund(String serviceSn, Double refundPrice, String remark);

}
