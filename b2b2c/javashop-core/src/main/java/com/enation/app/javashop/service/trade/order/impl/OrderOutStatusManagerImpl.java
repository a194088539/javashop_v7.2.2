package com.enation.app.javashop.service.trade.order.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.trade.order.OrderOutStatusMapper;
import com.enation.app.javashop.model.trade.order.dos.OrderOutStatus;
import com.enation.app.javashop.model.trade.order.enums.OrderOutStatusEnum;
import com.enation.app.javashop.model.trade.order.enums.OrderOutTypeEnum;
import com.enation.app.javashop.service.trade.order.OrderOutStatusManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.javashop.framework.database.WebPage;

/**
 * 订单出库状态业务类
 * @author xlp
 * @version v2.0
 * @since v7.0.0
 * 2018-07-10 14:06:38
 */
@Service
public class OrderOutStatusManagerImpl implements OrderOutStatusManager {

	@Autowired
	private OrderOutStatusMapper orderOutStatusMapper;

	@Override
	public WebPage list(long page, long pageSize){

		IPage iPage = new QueryChainWrapper<>(orderOutStatusMapper).page(new Page<>(page,pageSize));

		return PageConvert.convert(iPage);
	}

	@Override
	@Transactional(value = "",propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public	OrderOutStatus  add(OrderOutStatus	orderOutStatus)	{
		orderOutStatusMapper.insert(orderOutStatus);
		return orderOutStatus;
	}

	@Override
	@Transactional(value = "",propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public	void   edit(String orderSn, OrderOutTypeEnum typeEnum, OrderOutStatusEnum statusEnum){

		new UpdateChainWrapper<>(orderOutStatusMapper)
				//设置出库状态
				.set("out_status", statusEnum.name())
				//拼接订单编号修改条件
				.eq("order_sn", orderSn)
				//拼接订单出库类型修改条件
				.eq("out_type", typeEnum.name())
				//提交修改
				.update();
	}

	@Override
	@Transactional(value = "",propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public	void delete( Long id)	{
		orderOutStatusMapper.deleteById(id);
	}


	@Override
	public	OrderOutStatus getModel(String orderSn, OrderOutTypeEnum typeEnum)	{

		OrderOutStatus orderOutStatus = new QueryChainWrapper<>(orderOutStatusMapper)
				//拼接订单编号查询条件
				.eq("order_sn", orderSn)
				//拼接订单出库类型查询条件
				.eq("out_type", typeEnum.name())
				//查询单个对象
				.one();

		return orderOutStatus;
	}

}
