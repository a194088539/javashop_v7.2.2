package com.enation.app.javashop.service.member;

import com.enation.app.javashop.model.member.dos.ReceiptAddressDO;

/**
 * 会员收票地址业务层
 *
 * @author duanmingyu
 * @version v7.1.4
 * @since v7.0.0
 * 2019-06-19
 */
public interface ReceiptAddressManager {

    /**
     * 新增会员收票地址
     * @param receiptAddressDO 会员收票地址信息
     * @return
     */
    ReceiptAddressDO add(ReceiptAddressDO receiptAddressDO);

    /**
     * 修改会员收票地址
     * @param receiptAddressDO 会员收票地址信息
     * @param id 主键id
     * @return
     */
    ReceiptAddressDO edit(ReceiptAddressDO receiptAddressDO, Long id);

    /**
     * 获取会员收票地址
     * @return
     */
    ReceiptAddressDO get();
}
