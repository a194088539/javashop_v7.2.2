package com.enation.app.javashop.service.payment.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.payment.PayLogMapper;
import com.enation.app.javashop.model.trade.order.dos.PayLog;
import com.enation.app.javashop.model.trade.order.dto.PayLogQueryParam;
import com.enation.app.javashop.service.payment.PayLogManager;
import com.enation.app.javashop.framework.util.StringUtil;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 收款单业务类
 * @author xlp
 * @version v2.0
 * @since v7.0.0
 * 2018-07-18 10:39:51
 */
@Service
public class PayLogManagerImpl implements PayLogManager {

	@Autowired
	private PayLogMapper payLogMapper;

	/**
	 * 日志记录
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public WebPage list(PayLogQueryParam queryParam){

		IPage<PayLog> page = new QueryChainWrapper<>(payLogMapper)

				//如果付款方式不为空，则拼接付款方式查询条件
				.eq(StringUtil.notEmpty(queryParam.getPaymentType()), "pay_way", queryParam.getPaymentType())

				//如果支付状态不为空，则拼接支付状态查询条件
				.eq(StringUtil.notEmpty(queryParam.getPayStatus()), "pay_status", queryParam.getPayStatus())

				//如果付款时间不为空，则拼接付款时间查询条件
				.between(queryParam.getStartTime() != null && queryParam.getEndTime() != null, "pay_time",
						queryParam.getStartTime(), queryParam.getEndTime())

				//如果付款会员名不为空，则拼接付款会员名查询条件
				.eq(StringUtil.notEmpty(queryParam.getMemberName()), "pay_member_name", queryParam.getMemberName())

				//如果订单编号不为空，则拼接订单编号查询条件
				.like(StringUtil.notEmpty(queryParam.getOrderSn()), "order_sn", queryParam.getOrderSn())

				//如果支付方式不为空，则拼接支付方式查询条件
				.like(StringUtil.notEmpty(formatPayType(queryParam.getPayWay())), "pay_type", formatPayType(queryParam.getPayWay()))

				//按收款单id倒序
				.orderByDesc("pay_log_id")

				//分页查询
				.page(new Page<>(queryParam.getPageNo(), queryParam.getPageSize()));

		return PageConvert.convert(page);
	}

	@Override
	@Transactional(value = "",propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public	PayLog  add(PayLog	payLog)	{
		payLogMapper.insert(payLog);
		return payLog;
	}

	@Override
	@Transactional(value = "",propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public	PayLog  edit(PayLog	payLog,Long id){
		payLog.setPayLogId(id);
		payLogMapper.updateById(payLog);
		return payLog;
	}

	@Override
	@Transactional(value = "",propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public	void delete( Long id)	{
		payLogMapper.deleteById(id);
	}

	@Override
	public	PayLog getModel(Long id)	{
		return payLogMapper.selectById(id);
	}


	@Override
	public PayLog getModel(String orderSn) {

		PayLog payLog = new QueryChainWrapper<>(payLogMapper)
				//拼接订单编号查询条件
				.eq("order_sn", orderSn)
				//查询单个对象
				.one();

		return payLog;
	}


	@Override
	public List<PayLog> exportExcel(PayLogQueryParam queryParam) {

		List<PayLog> list = new QueryChainWrapper<>(payLogMapper)

				//如果付款时间不为空，则拼接付款时间查询条件
				.between(queryParam.getStartTime() != null && queryParam.getEndTime() != null, "pay_time",
						queryParam.getStartTime(), queryParam.getEndTime())

				//如果付款会员名不为空，则拼接付款会员名查询条件
				.eq(StringUtil.notEmpty(queryParam.getMemberName()), "pay_member_name", queryParam.getMemberName())

				//如果订单编号不为空，则拼接订单编号查询条件
				.like(StringUtil.notEmpty(queryParam.getOrderSn()), "order_sn", queryParam.getOrderSn())

				//如果支付方式不为空，则拼接支付方式查询条件
				.eq(StringUtil.notEmpty(queryParam.getPayWay()), "pay_way", queryParam.getPayWay())

				//按付款单id倒序
				.orderByDesc("pay_log_id")

				//列表查询
				.list();

		return list;
	}


	private String formatPayType(String payWay) {
		if(StringUtil.isEmpty(payWay)){
			return payWay;
		}

		if("alipay".equals(payWay)){
			return "支付宝";
		}else if("wechat".equals(payWay)){
			return "微信";
		}
		return "";
	}

}
