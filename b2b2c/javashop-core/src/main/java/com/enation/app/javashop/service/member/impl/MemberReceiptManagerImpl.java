package com.enation.app.javashop.service.member.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.enation.app.javashop.mapper.member.MemberReceiptMapper;
import com.enation.app.javashop.model.errorcode.MemberErrorCode;
import com.enation.app.javashop.model.member.dos.MemberReceipt;
import com.enation.app.javashop.model.member.enums.ReceiptTypeEnum;
import com.enation.app.javashop.service.member.MemberReceiptManager;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.exception.NoPermissionException;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.security.model.Buyer;
import com.enation.app.javashop.framework.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 会员发票信息缓存业务实现
 * @author duanmingyu
 * @version v7.1.4
 * @since v7.0.0
 * 2019-06-19
 */
@Service
public class MemberReceiptManagerImpl implements MemberReceiptManager {

    @Autowired
    private MemberReceiptMapper memberReceiptMapper;

    @Override
    public List<MemberReceipt> list(String receiptType) {
        //获取当前登录的会员信息并校验
        Buyer buyer = UserContext.getBuyer();
        if (buyer == null) {
            throw new ServiceException(MemberErrorCode.E137.code(), "当前会员登录信息已经失效");
        }
        //校验发票类型
        if (!ReceiptTypeEnum.ELECTRO.value().equals(receiptType) && !ReceiptTypeEnum.VATORDINARY.value().equals(receiptType)) {
            throw new ServiceException(MemberErrorCode.E147.code(), "参数错误");
        }

        //新建查询条件包装器
        QueryWrapper<MemberReceipt> wrapper = new QueryWrapper<>();
        //以会员ID和发票类型为查询条件，并按发票ID倒序排序
        wrapper.eq("member_id", buyer.getUid())
                .eq("receipt_type", receiptType)
                .orderByDesc("receipt_id");

        return memberReceiptMapper.selectList(wrapper);
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public MemberReceipt add(MemberReceipt memberReceipt) {
        //校验当前会员是否存在
        Buyer buyer = UserContext.getBuyer();
        if (buyer == null) {
            throw new ServiceException(MemberErrorCode.E137.code(), "当前会员登录信息已经失效");
        }
        //校验发票抬头
        if (this.checkTitle(memberReceipt.getReceiptTitle(), null, buyer.getUid(), memberReceipt.getReceiptType())) {
            throw new ServiceException(MemberErrorCode.E121.code(), "发票抬头不能重复");
        }
        //校验纳税人识别号
        if(StringUtil.isEmpty(memberReceipt.getTaxNo())){
            throw new ServiceException(MemberErrorCode.E121.code(), "纳税人识别号不能为空");
        }
        //根据类型获取会员发票缓存信息集合
        List<MemberReceipt> list = this.list(memberReceipt.getReceiptType());
        //会员发票缓存信息数量不能超过10条
        if (list.size() >= 10) {
            throw new ServiceException(MemberErrorCode.E121.code(), "会员发票信息缓存数不能超过10条");
        }
        //设置会员ID
        memberReceipt.setMemberId(buyer.getUid());
        //设置是否为默认选项 0：否，1：是
        memberReceipt.setIsDefault(1);
        //设置主键为null
        memberReceipt.setReceiptId(null);
        //会员发票缓存信息入库
        memberReceiptMapper.insert(memberReceipt);
        Long receiptId = memberReceipt.getReceiptId();
        memberReceipt.setReceiptId(receiptId);

        //将此会员发票信息设置为默认选项
        this.setDefaultReceipt(memberReceipt.getReceiptType(), memberReceipt.getReceiptId());

        return memberReceipt;
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public MemberReceipt edit(MemberReceipt memberReceipt, Long id) {
        //校验当前会员是否存在
        Buyer buyer = UserContext.getBuyer();
        if (buyer == null) {
            throw new ServiceException(MemberErrorCode.E137.code(), "当前会员登录信息已经失效");
        }
        //根据ID获取会员发票缓存信息
        MemberReceipt receipt = this.getModel(id);
        //校验权限
        if (receipt == null || buyer.getUid().intValue() != receipt.getMemberId().intValue()) {
            throw new ServiceException(MemberErrorCode.E136.code(), "没有操作权限");
        }
        //校验发票抬头
        if (this.checkTitle(memberReceipt.getReceiptTitle(), id, buyer.getUid(), memberReceipt.getReceiptType())) {
            throw new ServiceException(MemberErrorCode.E121.code(), "发票抬头不能重复");
        }
        //校验纳税人识别号
        if(StringUtil.isEmpty(memberReceipt.getTaxNo())){
            throw new ServiceException(MemberErrorCode.E121.code(), "纳税人识别号不能为空");
        }

        //设置发票ID
        memberReceipt.setReceiptId(id);
        //设置会员ID
        memberReceipt.setMemberId(buyer.getUid());
        //设置是否为默认选项 0：否，1：是
        memberReceipt.setIsDefault(receipt.getIsDefault());
        //修改会员发票缓存信息
        memberReceiptMapper.updateById(memberReceipt);
        return memberReceipt;
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(Long id) {
        //校验当前会员是否存在
        Buyer buyer = UserContext.getBuyer();
        if (buyer == null) {
            throw new ServiceException(MemberErrorCode.E137.code(), "当前会员登录信息已经失效");
        }

        //根据id获取会员发票缓存信息并校验
        MemberReceipt memberReceipt = this.getModel(id);
        if (memberReceipt == null || !memberReceipt.getMemberId().equals(buyer.getUid())) {
            throw new NoPermissionException("无权操作");
        }
        //删除会员发票缓存信息
        memberReceiptMapper.deleteById(id);
    }

    @Override
    public MemberReceipt getModel(Long id) {
        return memberReceiptMapper.selectById(id);
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void setDefaultReceipt(String receiptType, Long id) {
        //校验当前会员是否存在
        Buyer buyer = UserContext.getBuyer();
        if (buyer == null) {
            throw new ServiceException(MemberErrorCode.E137.code(), "当前会员登录信息已经失效");
        }

        //先将所有发票信息默认选项去除
        //新建修改条件包装器
        UpdateWrapper<MemberReceipt> wrapper = new UpdateWrapper<>();
        //以会员ID和发票类型为条件修改是否为默认选项 0：否，1：是
        wrapper.set("is_default", 0).eq("member_id", buyer.getUid()).eq("receipt_type", receiptType);
        //修改操作
        memberReceiptMapper.update(new MemberReceipt(), wrapper);

        //如果发票抬头不为个人，则将此发票信息设置为默认选项
        if (id != 0) {
            //获取会员发票缓存信息并校验
            MemberReceipt memberReceipt = this.getModel(id);
            if (memberReceipt == null || buyer.getUid().intValue() != memberReceipt.getMemberId().intValue()) {
                throw new NoPermissionException("无权操作");
            }
            //新建会员发票缓存对象
            MemberReceipt receipt = new MemberReceipt();
            //设置为默认选项 0：否，1：是
            receipt.setIsDefault(1);
            //设置会员ID
            receipt.setMemberId(buyer.getUid());
            //设置发票类型
            receipt.setReceiptType(receiptType);
            //设置发票ID
            receipt.setReceiptId(id);
            //修改会员发票缓存信息
            memberReceiptMapper.updateById(receipt);
        }
    }

    /**
     * 检测发票抬头是否重复
     * @param title 发票抬头
     * @param id 主键id
     * @param memberId 会员id
     * @param receiptType 发票类型
     * @return
     */
    protected boolean checkTitle(String title, Long id, Long memberId, String receiptType) {
        //如果发票抬头是个人
        if ("个人".equals(title)) {
            return true;
        }

        //新建查询条件包装器
        QueryWrapper<MemberReceipt> wrapper = new QueryWrapper<>();
        //以会员ID、发票抬头和发票类型为查询条件
        wrapper.eq("member_id", memberId).eq("receipt_title", title).eq("receipt_type", receiptType);
        //如果发票ID不为空，以发票ID为查询条件
        wrapper.ne(id != null, "receipt_id", id);
        //获取会员发票缓存信息数量
        int count = memberReceiptMapper.selectCount(wrapper);
        //数量不为0代表true，为0代表false
        boolean flag = count != 0 ? true : false;
        return flag;
    }
}
