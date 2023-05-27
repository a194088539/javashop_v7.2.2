package com.enation.app.javashop.service.member;

import com.enation.app.javashop.model.member.dos.MemberReceipt;

import java.util.List;

/**
 * 会员发票信息缓存业务层
 * @author duanmingyu
 * @version v7.1.4
 * @since v7.0.0
 * 2019-06-19
 */
public interface MemberReceiptManager {

    /**
     * 根据发票类型查询当前会员发票列表
     * @param receiptType 发票类型 ELECTRO：电子普通发票，VATORDINARY：增值税普通发票
     * @return
     */
    List<MemberReceipt> list(String receiptType);

    /**
     * 添加会员发票信息缓存
     * @param memberReceipt 会员发票信息缓存
     * @return
     */
    MemberReceipt add(MemberReceipt memberReceipt);

    /**
     * 修改会员发票信息缓存
     * @param memberReceipt 会员发票信息缓存
     * @param id 主键ID
     * @return
     */
    MemberReceipt edit(MemberReceipt memberReceipt, Long id);

    /**
     * 删除会员发票
     *
     * @param id 会员发票主键
     */
    void delete(Long id);

    /**
     * 获取会员发票
     *
     * @param id 会员发票主键
     * @return MemberReceipt  会员发票
     */
    MemberReceipt getModel(Long id);

    /**
     * 设置会员发票信息默认选项
     * @param receiptType 发票类型 ELECTRO：电子普通发票，VATORDINARY：增值税普通发票
     * @param id 主键ID 发票抬头为个人时则设置此参数为0
     */
    void setDefaultReceipt(String receiptType, Long id);

}
