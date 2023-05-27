package com.enation.app.javashop.service.shop.impl;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.enation.app.javashop.mapper.member.ShopSildeMapper;
import com.enation.app.javashop.service.shop.ShopSildeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.security.model.Seller;

import com.enation.app.javashop.model.errorcode.ShopErrorCode;
import com.enation.app.javashop.model.shop.dos.ShopSildeDO;

/**
 * 店铺幻灯片业务类
 * @author zjp
 * @version v7.0.0
 * @since v7.0.0
 * 2018-03-28 18:50:58
 */
@Service
public class ShopSildeManagerImpl implements ShopSildeManager {

	@Autowired
	private ShopSildeMapper shopSildeMapper;

	@Override
	public List<ShopSildeDO> list(Long shopId){
		//新建查询条件包装器
		QueryWrapper<ShopSildeDO> wrapper = new QueryWrapper<>();
		//以店铺id为查询条件
		wrapper.eq("shop_id", shopId);
		return shopSildeMapper.selectList(wrapper);
	}

	@Override
	@Transactional(value = "memberTransactionManager",propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public	ShopSildeDO  add(ShopSildeDO	shopSilde)	{
		//店铺幻灯片信息入库
		shopSildeMapper.insert(shopSilde);
		return shopSilde;
	}

	@Override
	@Transactional(value = "memberTransactionManager",propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public	void edit(List<ShopSildeDO> list){
		//获取当前登录的商家信息
		Seller seller = UserContext.getSeller();

		//如果店铺幻灯片集合长度大于0
		if (list.size() > 0) {
			//循环修改幻灯片
			for (ShopSildeDO shopSildeDO:list) {
				//设置店铺信息
				shopSildeDO.setShopId(seller.getSellerId());

				// 如果前端传入id为0，则进行新增操作，否则进行修改操作
				if (shopSildeDO.getSildeId() == 0) {
					this.add(shopSildeDO);
				} else {
					//对不存在的或不属于本店铺的幻灯片进行校验
					ShopSildeDO model = this.getModel(shopSildeDO.getSildeId());
					if (model == null || !model.getShopId().equals(seller.getSellerId())) {
						throw new ServiceException(ShopErrorCode.E208.name(), "存在无效幻灯片，无法进行编辑操作");
					}
					this.edit(shopSildeDO, shopSildeDO.getSildeId());
				}
			}
		}
	}

	@Override
	@Transactional(value = "memberTransactionManager",propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public void delete( Long id) {
		//对于不存在的或者不属于本店铺的幻灯片同时校验
		Seller seller = UserContext.getSeller();
		ShopSildeDO model = this.getModel(id);
		if (model == null || !model.getShopId().equals(seller.getSellerId())) {
			throw new ServiceException(ShopErrorCode.E208.name(), "不存在此幻灯片，无法删除");
		}

		//根据幻灯片ID删除幻灯片信息
		shopSildeMapper.deleteById(id);
	}

	@Override
	public	ShopSildeDO getModel(Long id)	{
		return shopSildeMapper.selectById(id);
	}

	@Override
	@Transactional(value = "memberTransactionManager",propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public	ShopSildeDO  edit(ShopSildeDO shopSilde, Long id){
		//设置主键ID
		shopSilde.setSildeId(id);
		//修改幻灯片信息
		this.shopSildeMapper.updateById(shopSilde);
		return shopSilde;
	}
}
