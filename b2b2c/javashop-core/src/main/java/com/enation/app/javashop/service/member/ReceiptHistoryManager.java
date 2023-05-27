package com.enation.app.javashop.service.member;

import com.enation.app.javashop.model.member.dto.HistoryQueryParam;
import com.enation.app.javashop.model.member.vo.ReceiptFileVO;
import com.enation.app.javashop.model.member.vo.ReceiptHistoryVO;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.member.dos.ReceiptHistory;

/**
 * 会员开票历史记录业务层
 *
 * @author duanmingyu
 * @version v7.1.4
 * @since v7.0.0
 * 2019-06-20
 */
public interface ReceiptHistoryManager {

    /**
     * 查询会员开票历史记录列表
     *
     * @param page     页码
     * @param pageSize 每页数量
     * @param params 搜索参数
     * @return WebPage
     */
    WebPage list(long page, long pageSize, HistoryQueryParam params);

    /**
     * 添加会员开票历史记录
     * @param receiptHistory 会员开票历史记录信息
     * @return
     */
    ReceiptHistory add(ReceiptHistory receiptHistory);

    /**
     * 修改会员开票历史记录
     * @param receiptHistory 会员开票历史记录信息
     * @param historyId 开票记录ID
     * @return
     */
    ReceiptHistory edit(ReceiptHistory receiptHistory, Long historyId);

    /**
     * 根据订单编号查询会员开票历史记录
     * @param orderSn 订单编号
     * @return
     */
    ReceiptHistoryVO getReceiptHistory(String orderSn);


    /**
     * 获取会员开票历史记录详细信息
     *
     * @param historyId 会员开票历史记录id
     * @return 发票详细VO
     */
    ReceiptHistoryVO get(Long historyId);

    /**
     * 商家开票--增值税普通发票和增值税专用发票
     * @param historyId 开票历史记录id
     * @param logiId 物流公司id
     * @param logiName 物流公司名称
     * @param logiCode 快递单号
     */
    void updateLogi(Long historyId, Long logiId, String logiName, String logiCode);

    /**
     * 商家开具发票-上传电子普通发票附件
     * @param receiptFileVO 电子普通发票附件信息
     * @return
     */
    void uploadFiles(ReceiptFileVO receiptFileVO);

    /**
     * 修改该订单的发票的订单金额
     * @param orderPrice 订单金额
     * @param orderSn 订单编号
     */
    void updatePriceByOrderSn(Double orderPrice, String orderSn);
}
