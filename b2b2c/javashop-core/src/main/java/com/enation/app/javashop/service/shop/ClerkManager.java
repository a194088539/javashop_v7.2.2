package com.enation.app.javashop.service.shop;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.member.vo.SellerInfoVO;
import com.enation.app.javashop.model.shop.dto.ClerkDTO;
import com.enation.app.javashop.model.shop.vo.ClerkVO;
import com.enation.app.javashop.model.shop.dos.ClerkDO;

/**
 * 店员业务层
 *
 * @author zh
 * @version v7.0.0
 * @since v7.0.0
 * 2018-08-04 18:48:39
 */
public interface ClerkManager {


    SellerInfoVO login(String username,String password);
    /**
     * 查询店员列表
     *
     * @param page     页码
     * @param pageSize 每页数量
     * @param disabled 店员状态
     * @param keyword 关键字
     * @return WebPage
     */
    WebPage list(long page, long pageSize, int disabled, String keyword);

    /**
     * 添加店员
     *
     * @param clerkVO 店员
     * @return Clerk 店员
     */
    ClerkDO addNewMemberClerk(ClerkVO clerkVO);

    /**
     * 添加老会员为店员
     *
     * @param clerkDTO 店员dto
     * @return Clerk 店员
     */
    ClerkDO addOldMemberClerk(ClerkDTO clerkDTO);

    /**
     * 添加超级店员
     *
     * @param clerk 店员信息
     * @return 店员信息
     */
    ClerkDO addSuperClerk(ClerkDO clerk);

    /**
     * 修改店员
     *
     * @param clerk 店员
     * @param id    店员主键
     * @return Clerk 店员
     */
    ClerkDO edit(ClerkDO clerk, Long id);

    /**
     * 删除店员
     *
     * @param id 店员主键
     */
    void delete(Long id);

    /**
     * 获取店员
     *
     * @param id 店员主键
     * @return Clerk  店员
     */
    ClerkDO getModel(Long id);

    /**
     * 恢复店员
     *
     * @param id 店员id
     */
    void recovery(Long id);

    /**
     * 根据会员id查询店员
     *
     * @param memberId 会员ID
     * @return
     */
    ClerkDO getClerkByMemberId(Long memberId);

}
