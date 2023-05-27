package com.enation.app.javashop.service.trade.deposite.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.client.member.DepositeClient;
import com.enation.app.javashop.client.payment.PaymentBillClient;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.security.model.Buyer;
import com.enation.app.javashop.framework.util.CurrencyUtil;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.framework.util.StringUtil;
import com.enation.app.javashop.mapper.trade.deposite.RechargeMapper;
import com.enation.app.javashop.model.errorcode.TradeErrorCode;
import com.enation.app.javashop.model.member.dto.DepositeParamDTO;
import com.enation.app.javashop.model.payment.dos.PaymentBillDO;
import com.enation.app.javashop.model.trade.deposite.RechargeDO;
import com.enation.app.javashop.model.trade.order.enums.PayStatusEnum;
import com.enation.app.javashop.model.trade.order.enums.TradeTypeEnum;
import com.enation.app.javashop.service.trade.deposite.RechargeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;


/**
 * 充值记录业务类
 * @author liuyulei
 * @version v1.0
 * @since v7.1.5
 * 2019-12-30 16:38:45
 */
@Service
public class RechargeManagerImpl implements RechargeManager {


	@Autowired
	private RechargeMapper rechargeMapper;

	@Autowired
	private PaymentBillClient paymentBillClient;

	@Autowired
	private DepositeClient depositeClient;


	@Override
	@Transactional(value = "tradeTransactionManager",propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public RechargeDO recharge(Double money) {
		Buyer buyer =  UserContext.getBuyer();

		//本金额支付次数
		int times = 0;

		//账单编号格式： 年月日+ memberID掩码 + 价格掩码 +支付次数
		//掩码均为5位数，不足前面补零
		String snPrefix = createSn(buyer.getUid(), "D", money);
		String sn = snPrefix + times;

		RechargeDO rechargeDO = this.getModel(sn);

		if(rechargeDO == null ){
			//整合充值订单数据
			rechargeDO = new RechargeDO(sn,buyer.getUid(),buyer.getUsername(),money);
			//添加充值订单
			createBill(rechargeDO);
		}else{
			//如果是已付款
			if(!PayStatusEnum.PAY_NO.name().equals(rechargeDO.getPayStatus())){
				// 获取到已支付次数
				times = Integer.parseInt(sn.substring(19, sn.length()));
				//循环生成sn
				while (true) {
					times++;
					sn = snPrefix + times;
					RechargeDO rechargeTemp = this.getModel(sn);

					// 找到一个没有使用过的 就可以break了
					if (rechargeTemp == null) {
						break;
					}
				}
				//充值订单已经被支付，则表示当天再次支付
				rechargeDO.setRechargeSn(sn);
				rechargeDO.setPayStatus(PayStatusEnum.PAY_NO.name());
				createBill(rechargeDO);
			}
			//如果没有被支付则不用创建充值订单，再次为此订单支付即可
		}
		return rechargeDO;

	}



	@Override
	@Transactional(value = "tradeTransactionManager",propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public void paySuccess(String sn, Double price) {
		RechargeDO rechargeDO = this.getModel(sn);

		if(!rechargeDO.getRechargeMoney().equals(price)){
			throw new ServiceException(TradeErrorCode.E454.code(), "付款金额和应付金额不一致");
		}

		this.rechargeMapper.update(null,new UpdateWrapper<RechargeDO>().set("pay_status",PayStatusEnum.PAY_YES.name()).eq("recharge_sn",sn));

		//增加会员预存款 余额
		depositeClient.increase(price,rechargeDO.getMemberId(),"会员充值，充值单号:" + rechargeDO.getRechargeSn());
	}

	@Override
	@Transactional(value = "tradeTransactionManager",propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public void updatePaymentMethod(String subSn, String pluginId, String methodName) {
		this.rechargeMapper.update(null,
				new UpdateWrapper<RechargeDO>().set("payment_plugin_id",pluginId)
						.set("recharge_way",methodName)
						.set("pay_time",DateUtil.getDateline())
						.eq("recharge_sn",subSn));

	}


	@Override
	public WebPage list(DepositeParamDTO paramDTO){

		IPage iPage = this.rechargeMapper.selectPage(new Page<>(paramDTO.getPageNo(), paramDTO.getPageSize()),
				new QueryWrapper<RechargeDO>()
						//根据会员名称查询
						.eq(!StringUtil.isEmpty(paramDTO.getMemberName()),"member_name",paramDTO.getMemberName())
						//根据会员id查询
						.eq(paramDTO.getMemberId() != null ,"member_id",paramDTO.getMemberId())
						//根据充值编号查询
						.eq(!StringUtil.isEmpty(paramDTO.getSn()),"recharge_sn",paramDTO.getSn())
						//根据充值时间查询
						.ge(paramDTO.getStartTime() != null,"recharge_time",paramDTO.getStartTime())
						//根据充值时间查询
						.le(paramDTO.getEndTime() != null,"recharge_time",paramDTO.getEndTime())
						//已支付状态
						.eq("pay_status",PayStatusEnum.PAY_YES.name())
						//充值时间倒序
						.orderByDesc("recharge_time"));

		return PageConvert.convert(iPage);
	}

	@Override
	public Double getPrice(String sn) {

		return this.getModel(sn).getRechargeMoney();
	}



	@Override
	public RechargeDO getModel(String sn)	{
		return this.rechargeMapper.selectOne(new QueryWrapper<RechargeDO>().eq("recharge_sn",sn));
	}

	private String mask(String str) {
		String mask = "000000";
		mask = mask + str;
		mask = mask.substring(mask.length() - 5);
		return mask;
	}

	private String createSn(Long memberId,String prefix,Double price) {
		String memberMask = mask("" + memberId);
		String priceMask = mask("" + CurrencyUtil.mul(price,100).intValue());
		String snPrefix = prefix + DateUtil.toString(new Date(), "yyyyMMdd") + memberMask + priceMask;
		return snPrefix;
	}

	private void createBill(RechargeDO rechargeDO) {
		this.rechargeMapper.insert(rechargeDO);
		//创建充值 支付 账单数据
		PaymentBillDO paymentBillDO = new PaymentBillDO();
		paymentBillDO.setSubSn(rechargeDO.getRechargeSn());
		paymentBillDO.setTradePrice(rechargeDO.getRechargeMoney());
		paymentBillDO.setServiceType(TradeTypeEnum.RECHARGE.name());
		paymentBillClient.add(paymentBillDO);
	}
}
