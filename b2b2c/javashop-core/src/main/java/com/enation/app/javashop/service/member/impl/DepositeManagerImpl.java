package com.enation.app.javashop.service.member.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.enation.app.javashop.mapper.member.MemberWalletMapper;
import com.enation.app.javashop.model.base.CachePrefix;
import com.enation.app.javashop.model.base.SceneType;
import com.enation.app.javashop.client.trade.DepositeLogClient;
import com.enation.app.javashop.model.errorcode.MemberErrorCode;
import com.enation.app.javashop.model.member.dos.Member;
import com.enation.app.javashop.model.member.vo.MemberDepositeVO;
import com.enation.app.javashop.service.member.MemberManager;
import com.enation.app.javashop.service.passport.PassportManager;
import com.enation.app.javashop.model.trade.deposite.DepositeLogDO;
import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.exception.ResourceNotFoundException;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.javashop.model.member.dos.MemberWalletDO;
import com.enation.app.javashop.service.member.DepositeManager;

/**
 * 会员预存款业务类
 * @author liuyulei
 * @version v1.0
 * @since v7.2.0
 * 2019-12-30 16:24:51
 */
@Service
public class DepositeManagerImpl implements DepositeManager {

	@Autowired
	private MemberWalletMapper memberWalletMapper;

	@Autowired
	private MemberManager memberManager;

	@Autowired
	private DepositeLogClient depositeLogClient;

	@Autowired
	private PassportManager passportManager;

	@Autowired
	private Cache cache;

	@Override
	@Transactional(value = "memberTransactionManager",propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public Boolean increase(Double money, Long memberId, String detail) {
		//根据会员ID获取会员预存款信息
		MemberWalletDO wallet = this.getModel(memberId);

		//新建修改条件包装器
		UpdateWrapper<MemberWalletDO> updateWrapper = new UpdateWrapper<>();
		//设置要修改的内容
		updateWrapper.setSql("pre_deposite = pre_deposite + " + money);
		//以会员ID为修改条件
		updateWrapper.eq("member_id", memberId);
		//修改会员预存款信息并获取修改成功数量
		Integer count = memberWalletMapper.update(new MemberWalletDO(), updateWrapper);
		//如果充值成功，则添加充值日志
		if(count == 1){
			DepositeLogDO logDO = new DepositeLogDO(wallet.getMemberId(),wallet.getMemberName(),money,detail);
			depositeLogClient.add(logDO);
			//移除缓存中预存款余额
			cache.remove(CachePrefix.BALANCE_CACHE_PREFIX.getPrefix() + memberId);
			return true;
		}
		return false;

	}

	@Override
	@Transactional(value = "memberTransactionManager",propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public Boolean reduce(Double money, Long memberId, String detail) {
		//根据会员ID获取会员预存款信息
		MemberWalletDO wallet = this.getModel(memberId);
		//修改会员预存款余额并获取修改成功数量
		Integer count = memberWalletMapper.updateDeposite(money, memberId);
		//如果扣减成功，则添加扣减日志
		if(count == 1){
			DepositeLogDO logDO = new DepositeLogDO(wallet.getMemberId(),wallet.getMemberName(),-money,detail);
			depositeLogClient.add(logDO);
			//移除缓存中预存款余额
			cache.remove(CachePrefix.BALANCE_CACHE_PREFIX.getPrefix() + memberId);
			return true;
		}
		return false;
	}

	@Override
	@Transactional(value = "memberTransactionManager",propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public void setDepositePwd(Long memberId,String password) {
		//根据会员ID获取会员信息
		Member member = this.memberManager.getModel(memberId);
		//校验当前会员是否存在
		if (member == null) {
			throw new ResourceNotFoundException("当前会员不存在");
		}

		//校验当前会员是否被禁用
		if (!member.getDisabled().equals(0)) {
			throw new ServiceException(MemberErrorCode.E107.code(), "当前账号已经禁用");
		}

		//将余额支付密码进行MD5加密
		String pwd = StringUtil.md5(password.toLowerCase() + member.getUname().toLowerCase());
		//新建修改条件包装器
		UpdateWrapper<MemberWalletDO> wrapper = new UpdateWrapper<>();
		//设置修改密码
		wrapper.set("deposite_password", pwd);
		//以会员ID为修改条件
		wrapper.eq("member_id", memberId);
		//修改会员预存款信息
		memberWalletMapper.update(new MemberWalletDO(), wrapper);
		//清除步骤标记缓存
		passportManager.clearSign(member.getMobile(), SceneType.SET_PAY_PWD.name());
	}

	@Override
	public void checkPwd(Long memberId,String password) {
		//根据会员ID获取会员信息
		Member member = this.memberManager.getModel(memberId);
		//密码设置方式  为会员用户名+md5(密码)
		String pwd = StringUtil.md5(password.toLowerCase() + member.getUname().toLowerCase());
		//根据会员ID获取会员预存款信息
		MemberWalletDO walletDO = this.getModel(memberId);
		if(!walletDO.getDepositePassword().equals(pwd)){
			throw new ServiceException(MemberErrorCode.E107.name(),"支付密码错误！");
		}
	}


	@Override
	@Transactional(value = "memberTransactionManager",propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public MemberWalletDO add(MemberWalletDO wallet)	{
		memberWalletMapper.insert(wallet);
		return wallet;
	}

	@Override
	@Transactional(value = "memberTransactionManager",propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public MemberWalletDO edit(MemberWalletDO memberWallet, Long id){
		memberWallet.setId(id);
		memberWalletMapper.updateById(memberWallet);
		return memberWallet;
	}

	@Override
	@Transactional(value = "memberTransactionManager",propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public	void delete( Long id)	{
		memberWalletMapper.deleteById(id);
	}

	@Override
	public MemberWalletDO getModel(Long memberId)	{
		//新建查询条件包装器
		QueryWrapper<MemberWalletDO> wrapper = new QueryWrapper<>();
		//以会员ID为查询条件
		wrapper.eq("member_id", memberId);
		return memberWalletMapper.selectOne(wrapper);
	}

	@Override
	public Double getDeposite(Long memberId) {
		MemberWalletDO walletDO = this.getModel(memberId);
		return walletDO.getPreDeposite();

	}

	@Override
	public MemberDepositeVO getDepositeVO(Long memberId) {
		MemberDepositeVO memberDepositeVO = new MemberDepositeVO();
		MemberWalletDO memberWalletDO =  this.getModel(memberId);
		memberDepositeVO.setBalance(memberWalletDO.getPreDeposite());
		memberDepositeVO.setIsPwd(!"-1".equals(memberWalletDO.getDepositePassword()));
		memberDepositeVO.setIsUsed(false);
		return memberDepositeVO;
	}
}
