package com.enation.app.javashop.service.shop.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.member.ClerkMapper;
import com.enation.app.javashop.model.errorcode.MemberErrorCode;
import com.enation.app.javashop.model.member.dos.Member;
import com.enation.app.javashop.model.member.vo.SellerInfoVO;
import com.enation.app.javashop.service.member.MemberManager;
import com.enation.app.javashop.model.shop.dos.ClerkDO;
import com.enation.app.javashop.model.shop.dos.ShopRole;
import com.enation.app.javashop.model.shop.dto.ClerkDTO;
import com.enation.app.javashop.model.shop.vo.ClerkShowVO;
import com.enation.app.javashop.model.shop.vo.ClerkVO;
import com.enation.app.javashop.model.shop.vo.ShopVO;
import com.enation.app.javashop.service.shop.ClerkManager;
import com.enation.app.javashop.service.shop.ShopManager;
import com.enation.app.javashop.service.shop.ShopRoleManager;
import com.enation.app.javashop.framework.auth.Token;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.exception.NoPermissionException;
import com.enation.app.javashop.framework.exception.ResourceNotFoundException;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.security.TokenManager;
import com.enation.app.javashop.framework.security.model.Clerk;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 店员业务类
 *
 * @author zh
 * @version v7.0.0
 * @since v7.0.0
 * 2018-08-04 18:53:52
 */
@Service
public class ClerkManagerImpl implements ClerkManager {

    @Autowired
    private ClerkMapper clerkMapper;

    @Autowired
    private MemberManager memberManager;

    @Autowired
    private ShopRoleManager shopRoleManager;

    @Autowired
    private ShopManager shopManager;

    @Autowired
    private TokenManager tokenManager;

    @Override
    public SellerInfoVO login(String username, String password) {
        //校验用户名和密码
        Member member = memberManager.validation(username, password);
        //新建商家信息对象
        SellerInfoVO sellerInfoVO = new SellerInfoVO();
        //查找店员
        ClerkDO clerkDb = getClerkByMemberId(member.getMemberId());

        //如果店员信息不为空
        if (clerkDb != null) {
            //获取店铺信息
            ShopVO shopVO = shopManager.getShop(clerkDb.getShopId());
            //新建店员信息对象
            Clerk clerk = new Clerk();
            //复制店员对象属性
            BeanUtils.copyProperties(clerkDb,clerk);
            //设置店铺名称
            clerk.setSellerName(shopVO.getShopName());
            //设置店铺ID
            clerk.setSellerId(shopVO.getShopId());
            //设置用户名
            clerk.setUsername(member.getUname());
            //设置会员ID
            clerk.setUid(member.getMemberId());
            //设置是否为自营店铺 0：不是，1：是
            clerk.setSelfOperated(shopVO.getSelfOperated());

            //如果是超级店员则赋值超级店员的权限，否则去查询权限赋值
            //注意，这里的权限为了和admin的区分，以SELLER_开头
            if (clerkDb.getFounder().equals(1)) {
                clerk.add("SELLER_SUPER_SELLER");
            } else {
                ShopRole shopRole = this.shopRoleManager.getModel(clerkDb.getRoleId());
                clerk.add("SELLER_"+shopRole.getRoleName());
            }

            //生成token
            Token token= tokenManager.create(clerk);

            //设置返回给前端的 token
            sellerInfoVO.setRefreshToken(token.getRefreshToken());
            sellerInfoVO.setAccessToken(token.getAccessToken());

            sellerInfoVO.setRoleId(clerkDb.getRoleId());
            sellerInfoVO.setFounder(clerk.getFounder());
            sellerInfoVO.setUsername(clerk.getUsername());

            return sellerInfoVO;
        }

        throw new ServiceException(MemberErrorCode.E107.code(), "账户尚未申请开店！");

    }

    @Override
    public WebPage list(long page, long pageSize, int disabled, String keyword) {
        //获取店员分页列表数据
        IPage<ClerkDO> iPage = clerkMapper.selectClerkPage(new Page(page, pageSize), UserContext.getSeller().getSellerId(), disabled, keyword);

        List<ClerkShowVO> clerks = new ArrayList<>();
        //循环结果集
        for (ClerkDO clerk : iPage.getRecords()) {
            //新建店员实体VO
            ClerkShowVO clerkShowVO = new ClerkShowVO();
            //设置会员ID
            clerkShowVO.setMemberId(clerk.getMemberId());
            //设置角色ID
            clerkShowVO.setRoleId(clerk.getRoleId());
            //判断是否为超级店员
            if (clerk.getFounder().equals(1) && clerk.getRoleId()==0) {
                clerkShowVO.setRole("超级店员");
            } else {
                ShopRole shopRole = shopRoleManager.getModel(clerk.getRoleId());
                if (shopRole != null) {
                    clerkShowVO.setRole(shopRole.getRoleName());
                }

            }
            //根据会员ID获取会员信息
            Member member = memberManager.getModel(clerk.getMemberId());
            //设置电子邮件
            clerkShowVO.setEmail(member.getEmail());
            //设置手机号
            clerkShowVO.setMobile(member.getMobile());
            //设置角色标识 1:超级店员 0:普通店员
            clerkShowVO.setFounder(clerk.getFounder());
            //设置店员ID
            clerkShowVO.setClerkId(clerk.getClerkId());
            //设置店员名称
            clerkShowVO.setUname(clerk.getClerkName());
            //设置店铺ID
            clerkShowVO.setShopId(clerk.getShopId());
            //设置用户状态
            clerkShowVO.setUserState(clerk.getUserState());
            clerks.add(clerkShowVO);
        }
        WebPage webPage = PageConvert.convert(iPage);
        webPage.setData(clerks);
        return webPage;
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ClerkDO addOldMemberClerk(ClerkDTO clerkDTO) {
        //根据手机号获取会员信息
        Member member = memberManager.getMemberByMobile(clerkDTO.getMobile());
        //如果会员信息不为空
        if (member != null) {
            //新建店员对象
            ClerkDO clerk = new ClerkDO();
            //设置店员名称
            clerk.setClerkName(member.getUname());
            //设置会员ID
            clerk.setMemberId(member.getMemberId());
            //设置角色ID
            clerk.setRoleId(clerkDTO.getRoleId());
            return this.add(clerk);
        }
        return null;
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ClerkDO addNewMemberClerk(ClerkVO clerkVO) {
        //新建会员对象
        Member member = new Member();
        //设置用户名
        member.setUname(clerkVO.getUname());
        //设置手机号
        member.setMobile(clerkVO.getMobile());
        //设置密码
        member.setPassword(clerkVO.getPassword());
        //设置电子邮件
        member.setEmail(clerkVO.getEmail());
        //设置昵称
        member.setNickname(clerkVO.getUname());
        //设置性别 0：女，1：男
        member.setSex(0);
        //注册会员
        member = this.memberManager.register(member);

        //添加店员信息
        ClerkDO clerk = new ClerkDO();
        clerk.setClerkName(clerkVO.getUname());
        clerk.setRoleId(clerkVO.getRoleId());
        clerk.setMemberId(member.getMemberId());
        return this.add(clerk);
    }

    /**
     * 添加店员
     *
     * @param clerk 店员信息
     * @return
     */
    private ClerkDO add(ClerkDO clerk) {
        //校验是否可以添加为超级管理员
        if (clerk.getRoleId().equals(0)) {
            throw new ServiceException(MemberErrorCode.E139.code(), "无法添加超级管理员");
        }
        //设置是否为超级管理员，1为超级管理员 0为其他管理员
        clerk.setFounder(0);
        //获取店铺角色
        ShopRole shopRole = shopRoleManager.getModel(clerk.getRoleId());
        //如果店铺角色不为空并且店铺角色所属店铺ID等于当前登录的店铺ID
        if (shopRole != null && shopRole.getShopId().equals(UserContext.getSeller().getSellerId())) {
            //设置店员状态，-1为禁用，0为正常
            clerk.setUserState(0);
            //设置店员添加时间
            clerk.setCreateTime(DateUtil.getDateline());
            //设置店员所属店铺ID
            clerk.setShopId(UserContext.getSeller().getSellerId());
            //店员数据入库
            clerkMapper.insert(clerk);
            return clerk;
        }
        throw new ResourceNotFoundException("当前角色不存在");
    }

    @Override
    public ClerkDO addSuperClerk(ClerkDO clerk) {
        //设置店员添加时间
        clerk.setCreateTime(DateUtil.getDateline());
        //设置店员状态，-1为禁用，0为正常
        clerk.setUserState(0);
        //店员数据入库
        clerkMapper.insert(clerk);
        return clerk;
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ClerkDO edit(ClerkDO clerk, Long id) {
        //设置主键ID
        clerk.setClerkId(id);
        //修改店员数据
        clerkMapper.updateById(clerk);
        return clerk;
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(Long id) {
        //根据id获取店员信息
        ClerkDO clerk = this.getModel(id);
        //校验店员信息
        if (clerk == null || !clerk.getShopId().equals(UserContext.getSeller().getSellerId())) {
            throw new NoPermissionException("无权限");
        }
        if (clerk.getFounder().equals(1)) {
            throw new ServiceException(MemberErrorCode.E138.code(), "无法删除超级管理员");
        }
        //设置店员状态，-1为禁用，0为正常
        clerk.setUserState(-1);
        //修改店员数据
        clerkMapper.updateById(clerk);
    }

    @Override
    public ClerkDO getModel(Long id) {
        return clerkMapper.selectById(id);
    }

    @Override
    public void recovery(Long id) {
        //根据id获取店员信息
        ClerkDO clerk = this.getModel(id);
        //校验权限
        if (clerk == null || !clerk.getShopId().equals(UserContext.getSeller().getSellerId())) {
            throw new NoPermissionException("无权限");
        }
        //校验当前会员是否存在
        Member member = this.memberManager.getModel(clerk.getMemberId());
        if (member == null || !member.getDisabled().equals(0)) {
            throw new ServiceException(MemberErrorCode.E137.code(), "当前会员已经失效，无法恢复此店员");
        }
        //设置店员状态，-1为禁用，0为正常
        clerk.setUserState(0);
        //修改店员数据
        clerkMapper.updateById(clerk);
    }

    @Override
    public ClerkDO getClerkByMemberId(Long memberId) {
        //新建查询条件包装器
        QueryWrapper<ClerkDO> wrapper = new QueryWrapper<>();
        //以会员ID和店员状态为正常状态作为查询条件
        wrapper.eq("member_id", memberId).eq("user_state", 0);
        return clerkMapper.selectOne(wrapper);
    }


}
