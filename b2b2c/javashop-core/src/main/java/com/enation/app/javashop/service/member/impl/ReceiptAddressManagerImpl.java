package com.enation.app.javashop.service.member.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.enation.app.javashop.mapper.member.ReceiptAddressMapper;
import com.enation.app.javashop.model.errorcode.MemberErrorCode;
import com.enation.app.javashop.model.member.dos.ReceiptAddressDO;
import com.enation.app.javashop.service.member.ReceiptAddressManager;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.security.model.Buyer;
import com.enation.app.javashop.framework.util.BeanUtil;
import com.enation.app.javashop.framework.util.StringUtil;
import com.enation.app.javashop.framework.util.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 会员收票地址业务实现
 *
 * @author duanmingyu
 * @version v7.1.4
 * @since v7.0.0
 * 2019-06-19
 */
@Service
public class ReceiptAddressManagerImpl implements ReceiptAddressManager {

    @Autowired
    private ReceiptAddressMapper receiptAddressMapper;

    @Override
    public ReceiptAddressDO add(ReceiptAddressDO receiptAddressDO) {
        //复制地区信息
        BeanUtil.copyProperties(receiptAddressDO.getRegion(), receiptAddressDO);
        //获取当前登录的会员信息并校验
        Buyer buyer = UserContext.getBuyer();
        if (buyer == null) {
            throw new ServiceException(MemberErrorCode.E137.code(), "当前会员登录信息已经失效");
        }
        //获取会员收票地址信息并校验
        ReceiptAddressDO addressDO = this.get();
        if (addressDO != null) {
            throw new ServiceException(MemberErrorCode.E149.code(), "收票地址已存在，不可重复添加");
        }

        //验证信息
        this.verify(receiptAddressDO);
        //设置会员ID
        receiptAddressDO.setMemberId(buyer.getUid());
        //会员收票地址信息入库
        receiptAddressMapper.insert(receiptAddressDO);
        return receiptAddressDO;
    }

    @Override
    public ReceiptAddressDO edit(ReceiptAddressDO receiptAddressDO, Long id) {
        //获取当前登录的会员信息并校验
        Buyer buyer = UserContext.getBuyer();
        if (buyer == null) {
            throw new ServiceException(MemberErrorCode.E137.code(), "当前会员登录信息已经失效");
        }
        //获取当前登录的会员信息并校验
        ReceiptAddressDO addressDO = this.get(id);
        if (addressDO == null || buyer.getUid().intValue() != addressDO.getMemberId().intValue()) {
            throw new ServiceException(MemberErrorCode.E136.code(), "没有操作权限");
        }

        //复制地区信息
        BeanUtil.copyProperties(receiptAddressDO.getRegion(), addressDO);
        //设置详细收票地址
        addressDO.setDetailAddr(receiptAddressDO.getDetailAddr());
        //设置收票人手机号
        addressDO.setMemberMobile(receiptAddressDO.getMemberMobile());
        //设置收票人姓名
        addressDO.setMemberName(receiptAddressDO.getMemberName());
        //修改收票地址信息
        receiptAddressMapper.updateById(addressDO);
        return addressDO;
    }

    @Override
    public ReceiptAddressDO get() {
        //获取当前登录的会员信息并校验
        Buyer buyer = UserContext.getBuyer();
        if (buyer == null) {
            throw new ServiceException(MemberErrorCode.E137.code(), "当前会员登录信息已经失效");
        }
        //新建查询条件包装器
        QueryWrapper<ReceiptAddressDO> wrapper = new QueryWrapper<>();
        //以会员ID为查询条件
        wrapper.eq("member_id", buyer.getUid());
        return receiptAddressMapper.selectOne(wrapper);
    }

    /**
     * 根据id获取收票地址信息
     * @param id
     * @return
     */
    private ReceiptAddressDO get(Long id) {
        return receiptAddressMapper.selectById(id);
    }

    /**
     * 会员收票地址参数验证
     * @param receiptAddressDO
     */
    protected void verify(ReceiptAddressDO receiptAddressDO) {
        //校验收票人姓名
        if (StringUtil.isEmpty(receiptAddressDO.getMemberName())) {
            throw new ServiceException(MemberErrorCode.E148.code(), "收票人姓名不能为空");
        }
        //校验收票人手机号
        if (StringUtil.isEmpty(receiptAddressDO.getMemberMobile())) {
            throw new ServiceException(MemberErrorCode.E148.code(), "收票人手机号不能为空");
        }
        //校验收票人手机号格式
        if (!Validator.isMobile(receiptAddressDO.getMemberMobile())) {
            throw new ServiceException(MemberErrorCode.E148.code(), "收票人手机号格式不正确");
        }
        //校验详细地址
        if (StringUtil.isEmpty(receiptAddressDO.getDetailAddr())) {
            throw new ServiceException(MemberErrorCode.E148.code(), "详细地址不能为空");
        }
    }
}
