package com.enation.app.javashop.service.member;

import com.enation.app.javashop.model.member.dos.HistoryDO;
import com.enation.app.javashop.model.member.dto.HistoryDTO;
import com.enation.app.javashop.model.member.dto.HistoryDelDTO;
import com.enation.app.javashop.framework.database.WebPage;

/**
 * 会员足迹业务层
 *
 * @author zh
 * @version v7.1.4
 * @since vv7.1
 * 2019-06-18 15:18:56
 */
public interface HistoryManager {

    /**
     * 查询会员足迹列表
     *
     * @param page     页码
     * @param pageSize 每页数量
     * @return WebPage
     */
    WebPage list(long page, long pageSize);

    /**
     * 修改会员足迹
     *
     * @param history 会员足迹
     * @param id      会员足迹主键
     * @return HistoryDO 会员足迹
     */
    HistoryDO edit(HistoryDO history, Long id);

    /**
     * 删除会员足迹
     *
     * @param historyDelDTO 会员足迹主键
     */
    void delete(HistoryDelDTO historyDelDTO);

    /**
     * 删除100条以后的足迹信息
     * @param memberId 会员ID
     */
    void delete(Long memberId);

    /**
     * 获取会员足迹
     *
     * @param id 会员足迹主键
     * @return History  会员足迹
     */
    HistoryDO getModel(Long id);

    /**
     * 根据商品和会员id查询此商品足迹信息
     *
     * @param goodsId  商品id
     * @param memberId 会员id
     * @return 会员历史足迹
     */
    HistoryDO getHistoryByGoods(Long goodsId, Long memberId);

    /**
     * 添加会员历史足迹
     *
     * @param historyDTO 历史足迹dto
     */
    void addMemberHistory(HistoryDTO historyDTO);

}
