package com.enation.app.javashop.service.member.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.client.promotion.CouponClient;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.member.MemberCouponMapper;
import com.enation.app.javashop.model.errorcode.MemberErrorCode;
import com.enation.app.javashop.model.member.dos.MemberCoupon;
import com.enation.app.javashop.model.member.dto.MemberCouponQueryParam;
import com.enation.app.javashop.model.member.vo.MemberCouponNumVO;
import com.enation.app.javashop.service.member.MemberCouponManager;
import com.enation.app.javashop.model.promotion.coupon.dos.CouponDO;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.security.model.Buyer;
import com.enation.app.javashop.framework.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 会员优惠券
 *
 * @author Snow create in 2018/5/24
 * @version v2.0
 * @since v7.0.0
 */
@Service
public class MemberCouponManagerImpl implements MemberCouponManager {

    @Autowired
    private MemberCouponMapper memberCouponMapper;

    @Autowired
    private CouponClient couponClient;

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void receiveBonus(Long memberId, String memberName, Long couponId) {
        //根据优惠券id获取优惠券信息
        CouponDO couponDO = this.couponClient.getModel(couponId);
        //如果会员id不为空
        if (memberId != null) {
            //添加会员优惠券表
            MemberCoupon memberCoupon = new MemberCoupon(couponDO);
            //设置优惠券创建时间
            memberCoupon.setCreateTime(DateUtil.getDateline());
            //设置会员ID
            memberCoupon.setMemberId(memberId);
            //设置会员名称
            memberCoupon.setMemberName(memberName);
            //设置优惠券使用状态 0:未使用,1:已使用,2:已过期,3:已作废
            memberCoupon.setUsedStatus(0);
            //会员优惠券入库
            memberCouponMapper.insert(memberCoupon);
            // 修改优惠券已被领取的数量
            this.couponClient.addReceivedNum(couponId);
        }
    }


    @Override
    public WebPage<MemberCoupon> list(MemberCouponQueryParam param) {
        //当前登录的会员
        Buyer buyer = UserContext.getBuyer();
        //当前服务器时间
        long nowTime = DateUtil.getDateline();
        //新建查询条件包装器
        QueryWrapper<MemberCoupon> wrapper = new QueryWrapper<>();
        //以会员ID为查询条件
        wrapper.eq("member_id", buyer.getUid());

        // 判断读取可用或者不可用优惠券 1:未使用 2：已使用，3已过期,4为不可用优惠券（已使用和已过期）
        if (param.getStatus() != null && param.getStatus().intValue() == 1) {
            // 可用优惠券读取条件 当前时间大于等于生效时间 并且 当前时间小于等于失效时间且使用状态是未使用
            // 并且 大于等于优惠券使用金额条件
            wrapper.le("start_time", nowTime);
            wrapper.ge("end_time", nowTime);
            wrapper.eq("used_status", 0);
            wrapper.le(param.getOrderPrice() != null, "coupon_threshold_price", param.getOrderPrice());

        } else if (param.getStatus() != null && param.getStatus().intValue() == 2) {
            //已使用优惠券
            wrapper.eq("used_status", 1);

        } else if (param.getStatus() != null && param.getStatus().intValue() == 3) {
            // 已过期优惠券读取条件 当前时间小于生效时间 或者 当前时间大于失效时间
            wrapper.lt("end_time", nowTime);
            wrapper.eq("used_status", 0);
        } else if (param.getStatus() != null && param.getStatus().intValue() == 4) {
            // 查询已使用和已过期的优惠券
            wrapper.and(wp -> {
               wp.lt("end_time", nowTime).eq("used_status", 0).or().eq("used_status", 1);
            });
        }

        //如果商家id不为空，以商家ID做为查询条件
        wrapper.in(param.getSellerIds() != null && param.getSellerIds().length != 0, "seller_id", param.getSellerIds());
        //按优惠券金额倒序排序
        wrapper.orderByDesc("coupon_price");
        //获取会员优惠券分页列表信息
        IPage<MemberCoupon> iPage = memberCouponMapper.selectPage(new Page<>(param.getPageNo(), param.getPageSize()), wrapper);
        //获取结果集并循环修改使用状态
        List<MemberCoupon> list = iPage.getRecords();
        if (list != null) {
            for (MemberCoupon memberCoupon : list) {
                if (memberCoupon.getEndTime() < nowTime && memberCoupon.getUsedStatus().equals(0)) {
                    memberCoupon.setUsedStatus(2);
                }
            }
        }
        iPage.setRecords(list);

        return PageConvert.convert(iPage);
    }


    @Override
    public MemberCoupon getModel(Long memberId, Long mcId) {
        //新建查询条件包装器
        QueryWrapper<MemberCoupon> wrapper = new QueryWrapper<>();
        //以会员id为查询条件
        wrapper.eq("member_id", memberId);
        //以优惠券id为查询条件
        wrapper.eq("mc_id", mcId);
        //查询一条优惠券信息
        MemberCoupon memberCoupon = memberCouponMapper.selectOne(wrapper);
        return memberCoupon;
    }


    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void usedCoupon(Long mcId, String orderSn) {
        //新建会员优惠券ID
        MemberCoupon coupon = new MemberCoupon();
        //修改使用状态为已使用 0:未使用,1:已使用,2:已过期,3:已作废
        coupon.setUsedStatus(1);
        //修改订单编号
        coupon.setOrderSn(orderSn);
        //修改使用时间
        coupon.setUsedTime(DateUtil.getDateline());
        //以优惠券id作为修改条件
        coupon.setMcId(mcId);
        //修改会员优惠券内容
        memberCouponMapper.updateById(coupon);
    }


    @Override
    public void checkLimitNum(Long couponId) {
        //根据优惠券ID获取优惠券信息
        CouponDO couponDO = this.couponClient.getModel(couponId);
        //获取当前登录的会员信息
        Buyer buyer = UserContext.getBuyer();
        //获取每人限领数量
        int limitNum = couponDO.getLimitNum();
        //新建查询条件包装器
        QueryWrapper<MemberCoupon> wrapper = new QueryWrapper<>();
        //以会员id为查询条件
        wrapper.eq("member_id", buyer.getUid());
        //以优惠券id为查询条件
        wrapper.eq("coupon_id", couponId);
        //查询会员优惠券数量
        int num = memberCouponMapper.selectCount(wrapper);
        //判断优惠券是否已被领完
        if (couponDO.getReceivedNum() >= couponDO.getCreateNum()) {
            throw new ServiceException(MemberErrorCode.E203.code(), "优惠券已被领完");
        }
        //判断优惠券是否已达到限领数量
        if (limitNum != 0 && num >= limitNum) {
            throw new ServiceException(MemberErrorCode.E203.code(), "优惠券限领" + limitNum + "个");
        }

    }

    @Override
    public List<MemberCoupon> listByCheckout(Long[] sellerIds, Long memberId) {
        //获取当前时间戳
        long nowTime = DateUtil.getDateline();
        //新建查询条件包装器
        QueryWrapper<MemberCoupon> wrapper = new QueryWrapper<>();
        //设置要查询的优惠券信息
        wrapper.select("coupon_id", "coupon_threshold_price", "coupon_price", "title", "start_time",
                "end_time", "used_status", "seller_id", "mc_id", "seller_name", "use_scope", "scope_id");
        //以会员id为查询条件
        wrapper.eq("member_id", memberId);
        //以使用状态为未使用作为查询条件
        wrapper.eq("used_status", 0);
        //以优惠券生效时间小于当前时间作为查询条件
        wrapper.lt("start_time", nowTime);
        //以优惠券生效时间大于当前时间作为查询条件
        wrapper.gt("end_time", nowTime);
        //如果商家id集合不为空并且长度不等于0
        if (sellerIds != null && sellerIds.length != 0) {
            //查询商家或者平台优惠券
            wrapper.and(wp -> {
               wp.in("seller_id", sellerIds).or().eq("seller_id", 0);
            });
        }
        //以优惠券金额倒序排序
        wrapper.orderByDesc("coupon_price");
        //查询的所有优惠券
        List<MemberCoupon> couponList = memberCouponMapper.selectList(wrapper);
        return couponList;
    }


    @Override
    public MemberCouponNumVO statusNum() {
        //当前登录的会员
        Buyer buyer = UserContext.getBuyer();
        //当前服务器时间
        long nowTime = DateUtil.getDateline();

        //未使用的数量
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("member_id", buyer.getUid());
        wrapper.le("start_time", nowTime);
        wrapper.ge("end_time", nowTime);
        int unUsedNum = memberCouponMapper.selectCount(wrapper);

        //已使用的数量
        wrapper = new QueryWrapper();
        wrapper.eq("member_id", buyer.getUid());
        wrapper.eq("used_status", 1);
        int usedNum = memberCouponMapper.selectCount(wrapper);

        //已过期
        wrapper = new QueryWrapper();
        wrapper.eq("member_id", buyer.getUid());
        wrapper.lt("end_time", nowTime);
        int expiredNum = memberCouponMapper.selectCount(wrapper);

        MemberCouponNumVO couponNumVO = new MemberCouponNumVO();
        couponNumVO.setExpiredNum(expiredNum);
        couponNumVO.setUseNum(usedNum);
        couponNumVO.setUnUseNum(unUsedNum);

        return couponNumVO;
    }

    @Override
    public WebPage queryByCouponId(Long couponId, Long pageNo, Long pageSize) {
        //新建查询条件包装器
        QueryWrapper<MemberCoupon> wrapper = new QueryWrapper<>();
        //以优惠券id为查询条件
        wrapper.eq("coupon_id", couponId);
        //获取会员优惠券分页列表数据
        IPage<MemberCoupon> iPage = memberCouponMapper.selectPage(new Page<>(pageNo, pageSize), wrapper);
        //获取结果集
        List<MemberCoupon> list = iPage.getRecords();
        //循环设置优惠券使用状态
        if (list != null) {
            for (MemberCoupon memberCoupon : list) {
                if (memberCoupon.getEndTime() < DateUtil.getDateline() && memberCoupon.getUsedStatus().equals(0)) {
                    memberCoupon.setUsedStatus(2);
                }
            }
        }
        iPage.setRecords(list);

        return PageConvert.convert(iPage);
    }

    @Override
    public void cancel(Long memberCouponId) {
        //新建会员优惠券对象
        MemberCoupon coupon = new MemberCoupon();
        //修改使用状态为已作废 0:未使用,1:已使用,2:已过期,3:已作废
        coupon.setUsedStatus(3);
        //以会员优惠券ID为修改条件
        coupon.setMcId(memberCouponId);
        //修改会员优惠券信息
        memberCouponMapper.updateById(coupon);
    }

    @Override
    public void updateSellerName(Long sellerId, String sellerName) {
        //新建修改条件包装器
        UpdateWrapper<MemberCoupon> wrapper = new UpdateWrapper<>();
        //修改会员优惠券所属店铺名称
        wrapper.set("seller_name", sellerName);
        //以店铺ID为修改条件
        wrapper.eq("seller_id", sellerId);
        //修改会员优惠券信息
        memberCouponMapper.update(new MemberCoupon(), wrapper);
    }


}
