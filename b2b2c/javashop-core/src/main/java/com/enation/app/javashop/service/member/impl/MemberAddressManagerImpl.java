package com.enation.app.javashop.service.member.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.member.MemberAddressMapper;
import com.enation.app.javashop.model.base.CachePrefix;
import com.enation.app.javashop.model.errorcode.MemberErrorCode;
import com.enation.app.javashop.model.member.dos.Member;
import com.enation.app.javashop.model.member.dos.MemberAddress;
import com.enation.app.javashop.service.member.MemberAddressManager;
import com.enation.app.javashop.service.member.MemberManager;
import com.enation.app.javashop.model.trade.order.support.CheckoutParamName;
import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.exception.NoPermissionException;
import com.enation.app.javashop.framework.exception.ResourceNotFoundException;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.security.model.Buyer;
import com.enation.app.javashop.framework.util.BeanUtil;
import com.enation.app.javashop.framework.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 会员地址业务类
 *
 * @author zh
 * @version v2.0
 * @since v7.0.0
 * 2018-03-18 15:37:00
 */
@Service
public class MemberAddressManagerImpl implements MemberAddressManager {

    @Autowired
    private MemberManager memberManager;
    @Autowired
    private Cache cache;
    @Autowired
    private MemberAddressMapper memberAddressMapper;

    @Override
    public List<MemberAddress> list() {
        //新建查询条件包装器
        QueryWrapper<MemberAddress> wrapper = new QueryWrapper<>();
        //以当前登录的会员ID为查询条件
        wrapper.eq("member_id", UserContext.getBuyer().getUid());
        //以地址ID倒序排序
        wrapper.orderByDesc("addr_id");
        //根据条件获取会员地址信息集合
        List<MemberAddress> addresses = memberAddressMapper.selectList(wrapper);
        //循环地址集合
        for (MemberAddress address : addresses) {
            //如果地址的所属城镇ID为0，将所属城镇ID设置为null
            if (address.getTownId() == 0) {
                address.setTownId(null);
            }
        }
        return addresses;
    }

    @Override
    public WebPage list(long page, long pageSize, Long memberId) {
        //新建查询条件包装器
        QueryWrapper<MemberAddress> wrapper = new QueryWrapper<>();
        //以会员ID为查询条件
        wrapper.eq("member_id", memberId);
        //获取会员地址分页列表数据
        IPage<MemberAddress> iPage = memberAddressMapper.selectPage(new Page<>(page, pageSize), wrapper);
        return PageConvert.convert(iPage);
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public MemberAddress add(MemberAddress memberAddress) {
        //复制地址信息
        BeanUtil.copyProperties(memberAddress.getRegion(), memberAddress);
        //获取当前登录会员信息
        Buyer buyer = UserContext.getBuyer();
        //对会员是否存在进行校验
        Member member = memberManager.getModel(buyer.getUid());
        if (member == null) {
            throw new ResourceNotFoundException("当前会员不存在");
        }

        //地址别名长度校验
        if (StringUtil.notEmpty(memberAddress.getShipAddressName())) {
            if (memberAddress.getShipAddressName().length() > 20) {
                throw new ServiceException(MemberErrorCode.E151.code(), "地址别名长度不能超过20个字符");
            }
        }

        //新建查询条件包装器
        QueryWrapper<MemberAddress> wrapper = new QueryWrapper<>();
        //以当前登录的会员ID为查询条件
        wrapper.eq("member_id", buyer.getUid());
        //获取会员地址数量
        Integer count = memberAddressMapper.selectCount(wrapper);
        //对会员地址数量上限进行校验
        if (count == 20) {
            throw new ServiceException(MemberErrorCode.E100.code(), "会员地址已达20个上限，无法添加");
        }
        //设置地址所属会员ID
        memberAddress.setMemberId(buyer.getUid());
        //设置地址所属国家
        memberAddress.setCountry("中国");

        //获取会员默认收货地址
        MemberAddress defAddr = this.getDefaultAddress(buyer.getUid());
        //默认地址的处理
        if (memberAddress.getDefAddr() > 1 || memberAddress.getDefAddr() < 0) {
            memberAddress.setDefAddr(0);
        }
        //如果没有默认地址，将当前地址设置为默认收货地址
        if (defAddr == null) {
            memberAddress.setDefAddr(1);
        } else {
            //不是第一个，且设置为默认地址了，则更新其它地址为非默认地址
            if (memberAddress.getDefAddr() == 1) {
                //新建修改条件包装器
                UpdateWrapper<MemberAddress> updateWrapper = new UpdateWrapper<>();
                //修改收货地址是否为默认收货地址 0：否，1：是
                updateWrapper.set("def_addr", 0);
                //以会员ID作为修改条件
                updateWrapper.eq("member_id", buyer.getUid());
                //修改收货地址信息
                memberAddressMapper.update(new MemberAddress(), updateWrapper);
            }
        }
        //会员收货地址信息入库
        memberAddressMapper.insert(memberAddress);
        return memberAddress;
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public MemberAddress edit(MemberAddress memberAddress, Long id) {
        //获取当前登录会员信息
        Buyer buyer = UserContext.getBuyer();
        //根据当前登录的会员ID获取完整会员信息
        Member member = memberManager.getModel(buyer.getUid());
        if (member == null) {
            throw new ResourceNotFoundException("当前会员不存在");
        }
        //权限判断
        MemberAddress address = this.getModel(id);
        if (address == null || !Objects.equals(address.getMemberId(), buyer.getUid())) {
            throw new NoPermissionException("无权限操作此地址");
        }

        //地址别名长度校验
        if (StringUtil.notEmpty(memberAddress.getShipAddressName())) {
            if (memberAddress.getShipAddressName().length() > 20) {
                throw new ServiceException(MemberErrorCode.E151.code(), "地址别名长度不能超过20个字符");
            }
        }

        //如果要将默认地址修改为非默认地址
        if (address.getDefAddr() == 1 && memberAddress.getDefAddr() == 0) {
            throw new ServiceException(MemberErrorCode.E101.code(), "无法更改当前默认地址为非默认地址");
        }
        //如果要将非默认地址修改为默认
        if (address.getDefAddr() == 0 && memberAddress.getDefAddr() == 1) {
            //新建修改条件包装器
            UpdateWrapper<MemberAddress> updateWrapper = new UpdateWrapper<>();
            //修改收货地址是否为默认收货地址 0：否，1：是
            updateWrapper.set("def_addr", 0);
            //以会员ID作为修改条件
            updateWrapper.eq("member_id", buyer.getUid());
            //修改收货地址信息
            memberAddressMapper.update(new MemberAddress(), updateWrapper);
        }
        //复制地区信息
        BeanUtil.copyProperties(memberAddress.getRegion(), address);
        //设置是否为默认地址
        address.setDefAddr(memberAddress.getDefAddr());
        //设置收货人
        address.setName(memberAddress.getName());
        //设置详细地址
        address.setAddr(memberAddress.getAddr());
        //设置手机号
        address.setMobile(memberAddress.getMobile());
        //设置电话号
        address.setTel(memberAddress.getTel());
        //设置地址别名
        address.setShipAddressName(memberAddress.getShipAddressName());
        //修改收货地址
        memberAddressMapper.updateById(address);
        return address;
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(Long id) {
        //获取当前登录会员信息
        Buyer buyer = UserContext.getBuyer();
        //根据地址ID获取收货地址详细
        MemberAddress address = this.getModel(id);
        if (address == null || !address.getMemberId().equals(buyer.getUid())) {
            throw new NoPermissionException("无权限操作此地址");
        }
        //默认地址不能删除
        if (address.getDefAddr().equals(1)) {
            throw new ServiceException(MemberErrorCode.E101.code(), "默认地址不能删除");
        }
        //根据地址ID删除地址
        memberAddressMapper.deleteById(id);
        //会员地址删除后，检测结算参数中的收货地址是否为该地址
        String key = CachePrefix.CHECKOUT_PARAM_ID_PREFIX.getPrefix() + buyer.getUid();
        Map<String, Object> map = cache.getHash(key);
        //如果为空，则不作处理
        if (map == null) {
            return;
        }
        //如果取到了，则取出来生成param，将缓存中的收货地址id设置为会员默认的收货地址ID
        Long addressId = (Long) map.get(CheckoutParamName.ADDRESS_ID);
        if (id.equals(addressId)) {
            //查询默认的收货地址
            MemberAddress defaultAddress = this.getDefaultAddress(buyer.getUid());
            this.cache.putHash(key, CheckoutParamName.ADDRESS_ID, defaultAddress.getAddrId());
        }
    }

    @Override
    public MemberAddress getModel(Long id) {
        return memberAddressMapper.selectById(id);
    }

    @Override
    public MemberAddress getDefaultAddress(Long memberId) {
        //新建查询条件包装器
        QueryWrapper<MemberAddress> wrapper = new QueryWrapper<>();
        //以会员ID为查询条件
        wrapper.eq("member_id", memberId);
        //以是否为默认地址为查询条件 0：否，1：是
        wrapper.eq("def_addr", 1);
        //获取会员默认地址信息
        List<MemberAddress> addressList = memberAddressMapper.selectList(wrapper);
        MemberAddress address = null;
        if (!addressList.isEmpty()) {
            address = addressList.get(0);
        }
        return address;
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void editDefault(Long id) {
        //根据ID获取收货地址信息
        MemberAddress memberAddress = this.getModel(id);
        //校验收货地址信息
        if (memberAddress == null) {
            throw new NoPermissionException("收货地址信息不存在");
        }
        //获取当前登录的会员信息
        Buyer buyer = UserContext.getBuyer();
        //权限校验
        if (!buyer.getUid().equals(memberAddress.getMemberId())) {
            throw new NoPermissionException("权限不足");
        }

        //新建修改条件包装器
        UpdateWrapper<MemberAddress> updateWrapper = new UpdateWrapper<>();
        //修改收货地址是否为默认收货地址 0：否，1：是
        updateWrapper.set("def_addr", 0);
        //以会员ID作为修改条件
        updateWrapper.eq("member_id", buyer.getUid());
        //先将会员所有收货地址设置为非默认地址
        memberAddressMapper.update(new MemberAddress(), updateWrapper);

        //新建收货地址对象
        MemberAddress address = new MemberAddress();
        //设置为默认收货地址
        address.setDefAddr(1);
        //设置地址ID
        address.setAddrId(id);
        //然后再将当前收货地址设置为默认收货地址
        memberAddressMapper.updateById(address);
    }
}
