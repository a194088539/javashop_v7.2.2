package com.enation.app.javashop.service.member.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.member.MemberZpzzMapper;
import com.enation.app.javashop.model.base.CachePrefix;
import com.enation.app.javashop.model.errorcode.MemberErrorCode;
import com.enation.app.javashop.model.member.dos.MemberZpzzDO;
import com.enation.app.javashop.model.member.dto.ZpzzQueryParam;
import com.enation.app.javashop.model.member.enums.ZpzzStatusEnum;
import com.enation.app.javashop.model.trade.order.support.CheckoutParamName;
import com.enation.app.javashop.service.member.MemberZpzzManager;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.security.model.Buyer;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.framework.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 会员增票资质业务实现
 *
 * @author duanmingyu
 * @version v7.1.4
 * @since v7.0.0
 * 2019-06-18
 */
@Service
public class MemberZpzzManagerImpl implements MemberZpzzManager {

    @Autowired
    private MemberZpzzMapper memberZpzzMapper;

    @Autowired
    private Cache cache;

    @Override
    public WebPage list(Long pageNo, Long pageSize, ZpzzQueryParam zpzzQueryParam) {
        //新建查询条件包装器
        QueryWrapper<MemberZpzzDO> wrapper = new QueryWrapper<>();
        //如果用户名不为空，则以用户名为条件进行模糊查询
        wrapper.like(StringUtil.notEmpty(zpzzQueryParam.getUname()), "uname", zpzzQueryParam.getUname());
        //如果状态值不为空，则以状态为条件查询
        wrapper.eq(StringUtil.notEmpty(zpzzQueryParam.getStatus()), "status", zpzzQueryParam.getStatus());
        //如果申请时间-时间范围中的起始时间不为空，则以申请时间大于等于起始时间为条件查询
        wrapper.ge(StringUtil.notEmpty(zpzzQueryParam.getStartTime()), "apply_time", zpzzQueryParam.getStartTime());
        //如果申请时间-时间范围中的结束时间不为空，则以申请时间小于等于结束时间为条件查询
        wrapper.le(StringUtil.notEmpty(zpzzQueryParam.getEndTime()), "apply_time", zpzzQueryParam.getEndTime());
        //以申请时间倒序排序
        wrapper.orderByDesc("apply_time");
        //查询会员资质审核信息分页列表集合
        IPage<MemberZpzzDO> iPage = memberZpzzMapper.selectPage(new Page<>(pageNo, pageSize), wrapper);
        return PageConvert.convert(iPage);
    }

    @Override
    public MemberZpzzDO add(MemberZpzzDO memberZpzzDO) {
        //获取当前登录的会员信息并校验
        Buyer buyer = UserContext.getBuyer();
        if (buyer == null) {
            throw new ServiceException(MemberErrorCode.E137.code(), "当前会员登录信息已经失效");
        }
        //获取会员的增票资质信息并校验
        MemberZpzzDO zpzzDO = this.get();
        if (zpzzDO != null) {
            throw new ServiceException(MemberErrorCode.E148.code(), "增票资质信息已存在，不可重复添加");
        }

        //参数验证
        this.verify(memberZpzzDO);

        //设置申请时间
        memberZpzzDO.setApplyTime(DateUtil.getDateline());
        //设置会员ID
        memberZpzzDO.setMemberId(buyer.getUid());
        //设置会员名称
        memberZpzzDO.setUname(buyer.getUsername());
        //设置增票资质信息状态
        memberZpzzDO.setStatus(ZpzzStatusEnum.NEW_APPLY.value());
        //入库操作
        memberZpzzMapper.insert(memberZpzzDO);
        return memberZpzzDO;
    }

    @Override
    public MemberZpzzDO edit(MemberZpzzDO memberZpzzDO, Long id) {
        //获取当前登录的会员信息并校验
        Buyer buyer = UserContext.getBuyer();
        if (buyer == null) {
            throw new ServiceException(MemberErrorCode.E137.code(), "当前会员登录信息已经失效");
        }

        //获取会员的增票资质信息并校验
        MemberZpzzDO zpzzDO = this.get(id);
        if (zpzzDO == null || buyer.getUid().intValue() != zpzzDO.getMemberId().intValue()) {
            throw new ServiceException(MemberErrorCode.E136.code(), "没有操作权限");
        }

        //参数验证
        this.verify(memberZpzzDO);

        //设置主键ID
        memberZpzzDO.setId(id);
        //设置会员ID
        memberZpzzDO.setMemberId(buyer.getUid());
        //设置申请时间
        memberZpzzDO.setApplyTime(DateUtil.getDateline());
        //设置会员名称
        memberZpzzDO.setUname(buyer.getUsername());
        //设置资质审核信息状态
        memberZpzzDO.setStatus(ZpzzStatusEnum.NEW_APPLY.value());
        //修改资质审核信息
        memberZpzzMapper.updateById(memberZpzzDO);

        //如果会员修改了增票资质信息，那么需要将会员缓存的发票信息清除掉
        String key = CachePrefix.CHECKOUT_PARAM_ID_PREFIX.getPrefix() + buyer.getUid();
        this.cache.putHash(key, CheckoutParamName.RECEIPT, null);

        return memberZpzzDO;
    }

    @Override
    public MemberZpzzDO get(Long id) {
        return memberZpzzMapper.selectById(id);
    }

    @Override
    public MemberZpzzDO get() {
        //获取当前登录的会员信息并校验
        Buyer buyer = UserContext.getBuyer();
        if (buyer == null) {
            throw new ServiceException(MemberErrorCode.E137.code(), "当前会员登录信息已经失效");
        }

        //新建查询条件包装器
        QueryWrapper<MemberZpzzDO> wrapper = new QueryWrapper<>();
        //以会员ID为条件查询
        wrapper.eq("member_id", buyer.getUid());
        return memberZpzzMapper.selectOne(wrapper);
    }

    @Override
    public void delete(Long id) {
        //获取当前登录的会员信息并校验
        Buyer buyer = UserContext.getBuyer();
        if (buyer == null) {
            throw new ServiceException(MemberErrorCode.E137.code(), "当前会员登录信息已经失效");
        }
        //根据ID获取完整资质审核信息并校验
        MemberZpzzDO memberZpzzDO = this.get(id);
        if (memberZpzzDO.getMemberId().intValue() != buyer.getUid().intValue()) {
            throw new ServiceException(MemberErrorCode.E136.code(), "没有操作权限");
        }

        //删除资质审核信息
        memberZpzzMapper.deleteById(id);
    }

    @Override
    public void audit(Long id, String status, String remark) {
        //校验状态
        if (StringUtil.isEmpty(status)) {
            throw new ServiceException(MemberErrorCode.E147.code(), "参数错误");
        }
        if (!ZpzzStatusEnum.AUDIT_PASS.value().equals(status) && !ZpzzStatusEnum.AUDIT_REFUSE.value().equals(status)) {
            throw new ServiceException(MemberErrorCode.E147.code(), "参数错误");
        }

        //如果状态为审核拒绝
        if (ZpzzStatusEnum.AUDIT_REFUSE.value().equals(status)) {
            //审核备注不允许为空
            if (StringUtil.isEmpty(remark)) {
                throw new ServiceException(MemberErrorCode.E148.code(), "审核备注不能为空");
            }
            //备注内容不能超过200个字符
            if (remark.length() > 200) {
                throw new ServiceException(MemberErrorCode.E148.code(), "审核备注不能超过200个字符");
            }
        }

        //新建会员资质审核信息对象
        MemberZpzzDO memberZpzzDO = new MemberZpzzDO();
        //设置审核状态
        memberZpzzDO.setStatus(status);
        //设置审核备注
        memberZpzzDO.setAuditRemark(remark);
        //设置主键ID
        memberZpzzDO.setId(id);
        //修改会员资质审核信息
        memberZpzzMapper.updateById(memberZpzzDO);
    }

    /**
     * 会员增票资质参数验证
     * @param memberZpzzDO
     */
    protected void verify(MemberZpzzDO memberZpzzDO) {
        //校验单位名称
        if (StringUtil.isEmpty(memberZpzzDO.getCompanyName())) {
            throw new ServiceException(MemberErrorCode.E148.code(), "单位名称不能为空");
        }
        //校验纳税人识别码
        if (StringUtil.isEmpty(memberZpzzDO.getTaxpayerCode())) {
            throw new ServiceException(MemberErrorCode.E148.code(), "纳税人识别码不能为空");
        }
        //校验公司注册地址
        if (StringUtil.isEmpty(memberZpzzDO.getRegisterAddress())) {
            throw new ServiceException(MemberErrorCode.E148.code(), "公司注册地址不能为空");
        }
        //校验公司注册电话
        if (StringUtil.isEmpty(memberZpzzDO.getRegisterTel())) {
            throw new ServiceException(MemberErrorCode.E148.code(), "公司注册电话不能为空");
        }
        //校验开户银行
        if (StringUtil.isEmpty(memberZpzzDO.getBankName())) {
            throw new ServiceException(MemberErrorCode.E148.code(), "开户银行不能为空");
        }
        //校验银行账户
        if (StringUtil.isEmpty(memberZpzzDO.getBankAccount())) {
            throw new ServiceException(MemberErrorCode.E148.code(), "银行账户不能为空");
        }
    }
}
