package com.enation.app.javashop.service.trade.complain.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.enation.app.javashop.mapper.trade.complain.OrderComplainCommunicationMapper;
import com.enation.app.javashop.model.trade.complain.dos.OrderComplainCommunication;
import com.enation.app.javashop.service.trade.complain.OrderComplainCommunicationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 交易投诉对话表业务类
 * @author fk
 * @version v2.0
 * @since v2.0
 * 2019-11-29 10:46:34
 */
@Service
public class OrderComplainCommunicationManagerImpl implements OrderComplainCommunicationManager {

	@Autowired
	private OrderComplainCommunicationMapper orderComplainCommunicationMapper;


	@Override
	public List<OrderComplainCommunication> list(long complainId) {

		return this.orderComplainCommunicationMapper.selectList(new QueryWrapper<OrderComplainCommunication>()
				.eq("complain_id",complainId)
				.orderByAsc("create_time"));
	}

	@Override
	@Transactional(value = "tradeTransactionManager",propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public	OrderComplainCommunication  add(OrderComplainCommunication	orderComplainCommunication)	{
		this.orderComplainCommunicationMapper.insert(orderComplainCommunication);

		return orderComplainCommunication;
	}

}
