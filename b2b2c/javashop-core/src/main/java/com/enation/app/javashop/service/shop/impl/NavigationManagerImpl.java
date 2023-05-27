package com.enation.app.javashop.service.shop.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.member.NavigationMapper;
import com.enation.app.javashop.service.shop.NavigationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.security.model.Seller;
import com.enation.app.javashop.model.errorcode.ShopErrorCode;
import com.enation.app.javashop.model.shop.dos.NavigationDO;

import java.util.List;

/**
 * 店铺导航管理业务类
 * @author zjp
 * @version v7.0.0
 * @since v7.0.0
 * 2018-03-28 20:44:54
 */
@Service
public class NavigationManagerImpl implements NavigationManager {

	@Autowired
	private NavigationMapper navigationMapper;

	@Override
	public WebPage list(long page, long pageSize, Long shopId){
		//新建查询条件包装器
		QueryWrapper<NavigationDO> wrapper = new QueryWrapper<>();
		//以店铺ID为查询条件
		wrapper.eq("shop_id", shopId);
		//获取店铺导航分页列表数据
		IPage<NavigationDO> iPage = navigationMapper.selectPage(new Page<>(page, pageSize), wrapper);
		return PageConvert.convert(iPage);
	}

	@Override
	@Transactional(value = "memberTransactionManager",propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public	NavigationDO  add(NavigationDO	navigation)	{
		//获取店铺导航数量
		int sum = this.getNavSum(navigation.getShopId());
		//导航数量不得大于10个
		if (sum >= 10) {
			throw new ServiceException(ShopErrorCode.E231.name(), "店铺导航最多允许添加10个");
		}
		//新增店铺导航
		navigationMapper.insert(navigation);
		return navigation;
	}

	@Override
	@Transactional(value = "memberTransactionManager",propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public	NavigationDO  edit(NavigationDO	navigation,Long id){
		//获取当前登录的商家信息
		Seller seller = UserContext.getSeller();
		//获取店铺导航信息
		NavigationDO model = this.getModel(id);
		//权限校验
		if (model == null || !seller.getSellerId().equals(model.getShopId())) {
			throw new ServiceException(ShopErrorCode.E209.name(), "导航不存在，不能进行编辑操作");
		}
		//设置店铺ID
		navigation.setShopId(seller.getSellerId());
		//设置导航id
		navigation.setId(id);
		//修改店铺导航信息
		navigationMapper.updateById(navigation);
		return navigation;
	}

	@Override
	@Transactional(value = "memberTransactionManager",propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public	void delete( Long id)	{
		//获取当前登录的商家信息
		Seller seller = UserContext.getSeller();
		//获取店铺导航信息
		NavigationDO model = this.getModel(id);
		//权限校验
		if (model == null || !seller.getSellerId().equals(model.getShopId())) {
			throw new ServiceException(ShopErrorCode.E209.name(), "导航不存在，不能进行删除操作");
		}
		//删除店铺导航
		navigationMapper.deleteById(id);
	}

	@Override
	public	NavigationDO getModel(Long id)	{
		return navigationMapper.selectById(id);
	}

	@Override
	public List<NavigationDO> list(Long shopId,Boolean isShow) {
		//新建查询条件包装器
		QueryWrapper<NavigationDO> wrapper = new QueryWrapper<>();
		//以店铺ID为查询条件
		wrapper.eq("shop_id", shopId);
		//如果是否显示值不为空并且是显示状态，则以显示状态为条件查询 0：否，1：是
		wrapper.eq(isShow != null && isShow, "disable", 1);
		//按排序值倒序排序
		wrapper.orderByDesc("sort");
		return navigationMapper.selectList(wrapper);
	}

	/**
	 * 获取店铺导航数量
	 * @param shopId 商家店铺ID
	 * @return
	 */
	protected int getNavSum(Long shopId) {
		//新建查询条件包装器
		QueryWrapper<NavigationDO> wrapper = new QueryWrapper<>();
		//以店铺ID为查询条件
		wrapper.eq("shop_id", shopId);
		return navigationMapper.selectCount(wrapper);
	}
}
