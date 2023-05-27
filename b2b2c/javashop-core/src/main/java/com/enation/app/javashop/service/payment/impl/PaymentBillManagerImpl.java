package com.enation.app.javashop.service.payment.impl;

import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.logs.Debugger;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import com.enation.app.javashop.framework.sncreator.SnCreator;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.framework.util.StringUtil;
import com.enation.app.javashop.mapper.payment.PaymentBillMapper;
import com.enation.app.javashop.model.base.SubCode;
import com.enation.app.javashop.model.base.message.PaymentBillStatusChangeMsg;
import com.enation.app.javashop.model.base.rabbitmq.AmqpExchange;
import com.enation.app.javashop.model.errorcode.PaymentErrorCode;
import com.enation.app.javashop.model.payment.dos.PaymentBillDO;
import com.enation.app.javashop.model.payment.vo.PayBill;
import com.enation.app.javashop.service.payment.PaymentBillManager;
import com.enation.app.javashop.framework.rabbitmq.MessageSender;
import com.enation.app.javashop.framework.rabbitmq.MqMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 支付帐单业务类
 *
 * @author fk
 * @version v2.0
 * @since v7.0.0
 * 2018-04-16 17:28:07
 */
@Service
public class PaymentBillManagerImpl implements PaymentBillManager {


	@Autowired
	private Debugger debugger;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private MessageSender messageSender;

	@Autowired
	SnCreator snCreator;

	@Autowired
	private PaymentBillMapper payBillMapper;

	@Override
	public WebPage list(long page, long pageSize) {

		Page<PaymentBillDO> ipage = new QueryChainWrapper<>(payBillMapper).page(new Page<>(page, pageSize));

		return PageConvert.convert(ipage);
	}

	@Override
	@Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public PayBill add(PaymentBillDO paymentBill) {
		//根据子业务单号和子业务类型获取账单
		PaymentBillDO paymentBillDO = this.getBySubSnAndServiceType(paymentBill.getSubSn(), paymentBill.getServiceType());
		//判断账单是否存在
		if (paymentBillDO == null) {
			//如果不存在，则新增
			paymentBill.setBillSn(""+snCreator.create(SubCode.PAY_BILL));
			this.payBillMapper.insert(paymentBill);
		} else if (!paymentBillDO.getTradePrice().equals(paymentBill.getTradePrice())) {
			//生成新的账单号
			paymentBill.setBillSn(""+snCreator.create(SubCode.PAY_BILL));
			this.edit(paymentBill,paymentBillDO.getBillSn());
		} else {
			//如果存在则修改
			paymentBill.setBillSn(paymentBillDO.getBillSn());
			this.edit(paymentBill,null);
		}
		PayBill payBill = new PayBill(paymentBill);
		return payBill;
	}

	private PaymentBillDO getByBillSn(String billSn) {

		return new QueryChainWrapper<>(payBillMapper)
				//按支付账单编号查询
				.eq("bill_sn", billSn)
				//查询单个对象
				.one();
	}

	@Override
	public PaymentBillDO getBillByReturnTradeNo(String returnTradeNo) {

		return new QueryChainWrapper<>(payBillMapper)
				//按第三方平台返回交易号查询
				.eq("return_trade_no", returnTradeNo)
				//查询单个对象
				.one();
	}

	@Override
	public PaymentBillDO getModel(String billSn) {

		return new QueryChainWrapper<>(payBillMapper)
				//按支付账单编号查询
				.eq("bill_sn", billSn)
				//查询单个对象
				.one();
	}

	@Override
	public void edit(PaymentBillDO paymentBillDO, String sn) {
		String billSn;
		if(StringUtil.isEmpty(sn)){
			billSn = paymentBillDO.getBillSn();
		}else{
			billSn = sn;
		}

		new UpdateChainWrapper<>(payBillMapper)
				//按支付账单编号修改
				.eq("bill_sn", billSn)
				//提交修改
				.update(paymentBillDO);
	}


	/**
	 * 根据子业务编号和子业务类型获取账单信息
	 *
	 * @param subSn
	 * @param serviceType
	 * @return
	 */
	@Override
	public PaymentBillDO getBySubSnAndServiceType(String subSn, String serviceType) {

		PaymentBillDO one = new QueryChainWrapper<>(payBillMapper)
				//拼接交易单号查询条件
				.eq("sub_sn", subSn)
				//拼接业务类型查询条件
				.eq("service_type", serviceType)
				//查询单个对象
				.one();

		return one;
	}

	@Override
	public boolean check(String billSn) {
		PaymentBillDO paymentBillDO = this.getModel(billSn);
		return paymentBillDO.getIsPay() == 0;
	}

	@Override
	@Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void paySuccess(String billSn, String returnTradeNo, Double payPrice) {


		//根据支付单号找到交易单号
		PaymentBillDO bill = this.getByBillSn(billSn);
		if (bill == null) {
			debugger.log("支付回调失败，原因为：【" + bill.getServiceType() + "类型的交易,编号为：" + billSn + "没有找到相应的账单】");
			logger.error("支付回调失败，原因为：【" + bill.getServiceType() + "类型的交易,编号为：" + billSn + "没有找到相应的账单】");
			throw new RuntimeException("支付回调失败，原因为：【" + bill.getServiceType() + "类型的交易编号为：" + billSn + "没有找到相应的账单】");
		}

		logger.debug("找到账单：");
		debugger.log("找到账单：");
		logger.debug(bill.toString());
		debugger.log(bill.toString());
		//验证回调金额和支付金额是否一致
		if (!bill.getTradePrice().equals(payPrice)) {
			logger.error("支付金额不一致，账单支付金额：" + bill.getTradePrice() + "，回调金额：" + payPrice);
			debugger.log("支付金额不一致，账单支付金额：" + bill.getTradePrice() + "，回调金额：" + payPrice);
			throw new ServiceException(PaymentErrorCode.E508.code(), "支付金额不一致");
		}

		//修改支付账单的状态
		new UpdateChainWrapper<>(payBillMapper)
				//设置是否支付
				.set("is_pay", 1)
				//设置第三方平台返回交易号
				.set("return_trade_no", returnTradeNo)
				//拼接支付账单id修改条件
				.eq("bill_id", bill.getBillId())
				//提交修改
				.update();

		//发送账单状态变化消息
		bill.setReturnTradeNo(returnTradeNo);
		PaymentBillStatusChangeMsg msg = new PaymentBillStatusChangeMsg(bill, payPrice);
		msg.setStatus(PaymentBillStatusChangeMsg.SUCCESS);
		this.messageSender.send(new MqMessage(AmqpExchange.PAYMENT_BILL_CHANGE, AmqpExchange.PAYMENT_BILL_CHANGE + "_ROUTING", msg));

		logger.debug("更改支付账单状态成功");
		debugger.log("更改支付账单状态成功");
	}


}
