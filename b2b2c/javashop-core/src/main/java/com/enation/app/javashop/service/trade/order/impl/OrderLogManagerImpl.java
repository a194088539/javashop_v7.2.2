package com.enation.app.javashop.service.trade.order.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.trade.order.OrderLogMapper;
import com.enation.app.javashop.model.trade.order.dos.OrderLogDO;
import com.enation.app.javashop.service.trade.order.OrderLogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 订单日志表业务类
 * @author Snow
 * @version v7.0.0
 * @since v7.0.0
 * 2018-05-16 12:01:34
 */
@Service
public class OrderLogManagerImpl implements OrderLogManager {

	@Autowired
	private OrderLogMapper orderLogMapper;

	@Override
	public WebPage list(long page, long pageSize){

		IPage<OrderLogDO> iPage = new QueryChainWrapper<>(orderLogMapper).page(new Page<>(page, pageSize));

		return PageConvert.convert(iPage);
	}

	@Override
	public List listAll(String orderSn) {

		List<OrderLogDO> list = new QueryChainWrapper<>(orderLogMapper)
				//按订单编号查询
				.eq("order_sn", orderSn)
				//列表查询
				.list();

		return list;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public OrderLogDO add(OrderLogDO orderLog)	{
		orderLogMapper.insert(orderLog);
		return orderLog;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public OrderLogDO  edit(OrderLogDO	orderLog,Long id){
		orderLog.setLogId(id);
		orderLogMapper.updateById(orderLog);
		return orderLog;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public void delete( Long id)	{
		orderLogMapper.deleteById(id);
	}

	@Override
	public	OrderLogDO getModel(Long id)	{
		return orderLogMapper.selectById(id);
	}
}
