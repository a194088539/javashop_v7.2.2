package com.enation.app.javashop.service.shop.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.enation.app.javashop.mapper.member.ShopCatMapper;
import com.enation.app.javashop.model.shop.enums.ShopCatShowTypeEnum;
import com.enation.app.javashop.model.errorcode.ShopErrorCode;
import com.enation.app.javashop.model.shop.dos.ShopCatDO;
import com.enation.app.javashop.service.shop.ShopCatManager;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 店铺分组业务类
 * @author zjp
 * @version v7.0.0
 * @since v7.0.0
 * 2018-04-24 11:18:37
 */
@Service
public class ShopCatManagerImpl implements ShopCatManager{

	@Autowired
	private ShopCatMapper shopCatMapper;

	@Override
	public List list(Long shopId,String display){
		//新建查询条件包装器
		QueryWrapper<ShopCatDO> wrapper = new QueryWrapper<>();
		//以店铺ID和店铺分组父ID为0为条件查询
		wrapper.eq("shop_id", shopId).eq("shop_cat_pid", 0);
		//构建wrapper
		this.appendWrapper(display, wrapper);
		//获取店铺分组集合
		List<ShopCatDO> list = shopCatMapper.selectList(wrapper);

		//循环集合
		for (ShopCatDO shopCatDO : list) {
			//新建查询条件包装器
			wrapper = new QueryWrapper<>();
			//以店铺分组父ID为查询条件
			wrapper.eq("shop_cat_pid", shopCatDO.getShopCatId());
			//构建wrapper
			this.appendWrapper(display, wrapper);
			//获取子分组集合
			List<ShopCatDO> shopCatDOS = shopCatMapper.selectList(wrapper);
			//设置子分组
			shopCatDO.setChildren(shopCatDOS);
		}
		return list;
	}

	@Override
	@Transactional(value = "memberTransactionManager",propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public ShopCatDO add(ShopCatDO	shopCat) {
		//设置店铺id为当前登录的商家店铺ID
		shopCat.setShopId(UserContext.getSeller().getSellerId());
		//新建店铺分组对象
		ShopCatDO parent = null;
		//如果为顶级分组，父分类为0
		if (shopCat.getShopCatPid() == null || shopCat.getShopCatPid() == 0) {
			shopCat.setShopCatPid(0L);
		} else {
			parent = this.getModel(shopCat.getShopCatPid());
			if (parent == null) {
				throw new ServiceException(ShopErrorCode.E223.name(),"父分组不存在");
			}
		}
		//店铺分组信息入库
		shopCatMapper.insert(shopCat);

		//判断店铺分组父id是否等于0，分别作出处理
		if (shopCat.getShopCatPid() == 0) {
			//填充分类路径
			shopCat.setCatPath("0|" + shopCat.getShopCatId() + "|");
		} else {
			//填充分类路径
			shopCat.setCatPath(this.getModel(shopCat.getShopCatPid()).getCatPath() + shopCat.getShopCatId() + "|");
			//如果是子分类且是开启状态，则父分类也要是开启状态
			if(shopCat.getDisable().equals(1)){
				//新建店铺分组对象
				ShopCatDO cat = new ShopCatDO();
				//显示状态设置为显示 0：不显示，1：显示
				cat.setDisable(1);
				//设置分组ID
				cat.setShopCatId(shopCat.getShopCatPid());
				//修改分组信息
				shopCatMapper.updateById(cat);
			}
		}

		//修改分组信息
		shopCatMapper.updateById(shopCat);
		return shopCat;
	}

	@Override
	@Transactional(value = "memberTransactionManager",propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public	ShopCatDO  edit(ShopCatDO	shopCat, Long id){
		//设置店铺id为当前登录的商家店铺ID
		shopCat.setShopId(UserContext.getSeller().getSellerId());
		//设置店铺分组ID
		shopCat.setShopCatId(id);
		//如果店铺分组父ID等于空，默认设置为0
		if (shopCat.getShopCatPid() == null) {
			shopCat.setShopCatPid(0L);
		}

		//获取店铺分组信息
		ShopCatDO shopCatDO = this.getModel(id);
		//校验店铺分组信息是否存在
		if (shopCatDO == null || !shopCatDO.getShopId().equals(UserContext.getSeller().getSellerId())) {
			throw new ServiceException(ShopErrorCode.E218.name(), "店铺分组不存在");
		}
		//校验是否为顶级分类，如果是则不允许修改上级分类
		if (shopCatDO.getShopCatPid() == 0 && shopCat.getShopCatPid() != 0) {
			throw new ServiceException(ShopErrorCode.E219.name(), "顶级分类不可修改上级分类");
		}
		//如果不是顶级分类，要校验父分组是否存在
		if(shopCat.getShopCatPid()!=0){
			ShopCatDO parent = this.getModel(shopCat.getShopCatPid());
			if(parent == null){
				throw new ServiceException(ShopErrorCode.E223.name(),"父分组不存在");
			}
		}
		//如果是顶级分类
		if(shopCat.getShopCatPid()==0 ){
			//填充分类路径
			shopCat.setCatPath("0|" + shopCat.getShopCatId() + "|");
			//如果是父分类并且是关闭状态，则子分类也要是关闭状态
			if(shopCat.getDisable().equals(0)){
				//新建修改条件包装器
				UpdateWrapper<ShopCatDO> wrapper = new UpdateWrapper<>();
				//以分组父ID为修改条件，修改分组显示状态为隐藏
				wrapper.set("disable", 0).eq("shop_cat_pid", shopCat.getShopCatId());
				//修改店铺分组信息
				shopCatMapper.update(new ShopCatDO(), wrapper);
			}
		}

		//如果不是顶级分类
		if(shopCat.getShopCatPid()!=0){
			//填充分类路径
			shopCat.setCatPath(this.getModel(shopCat.getShopCatPid()).getCatPath() + shopCat.getShopCatId() + "|");
			//如果是子分类且是开启状态，则父分类也要是开启状态
			if(shopCat.getDisable().equals(1)){
				//新建店铺分组对象
				ShopCatDO cat = new ShopCatDO();
				//分组状态设置为显示
				cat.setDisable(1);
				//设置店铺分组ID
				cat.setShopCatId(shopCat.getShopCatId());
				//修改店铺分组信息
				shopCatMapper.updateById(cat);
			}
		}

		//修改店铺分组信息
		shopCatMapper.updateById(shopCat);
		return shopCat;
	}

	@Override
	@Transactional(value = "memberTransactionManager",propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public	void delete( Long id)	{
		//获取当前登录的商家ID
		Long sellerId = UserContext.getSeller().getSellerId();

		//根据ID获取店铺分组信息并校验
		ShopCatDO shopCatDO = this.getModel(id);
		if(shopCatDO==null){
			throw new ServiceException(ShopErrorCode.E218.name(),"店铺分组不存在");
		}

		//查询当前分组下是否有子分组
		QueryWrapper<ShopCatDO> wrapper = new QueryWrapper<>();
		wrapper.eq("shop_cat_pid", id).eq("shop_id", sellerId);
		Integer num = shopCatMapper.selectCount(wrapper);
		if(num>=1){
			throw new ServiceException(ShopErrorCode.E220.name(),"当前分组存在子分组，不能删除");
		}
//TODO
//		int goodsNum = this.daoSupport.queryForInt("select count(0) from es_goods where shop_cat_id=? and seller_id=?", id,sellerId);
//		if(goodsNum>=1){
//			throw new ServiceException(ShopErrorCode.E221.name(),"当前分组存在商品，请删除此分类下所有商品(包括商品回收站)！");
//		}

		shopCatMapper.deleteById(id);
	}

	@Override
	public	ShopCatDO getModel(Long id)	{
		//获取店铺分组信息
		ShopCatDO shopCatDO = shopCatMapper.selectById(id);
		return shopCatDO;
	}

	@Override
	public List getChildren(String catPath) {
		//新建查询条件包装器
		QueryWrapper wrapper = new QueryWrapper();
		//以分组路径为条件查询分组ID集合
		wrapper.select("shop_cat_id").likeRight("cat_path", catPath);
 		return shopCatMapper.getChildren(wrapper);
	}


	/**
	 * 构建查询条件包装器
	 * @param display 店铺分组展示类型
	 * @param wrapper 查询条件包装器
	 */
	private void appendWrapper(String display, QueryWrapper<ShopCatDO> wrapper) {
		//如果展示类型为显示
		if(ShopCatShowTypeEnum.SHOW.name().equals(display)){
			wrapper.eq("disable", 1);
		}else if(ShopCatShowTypeEnum.HIDE.name().equals(display)){
			wrapper.eq("disable", 0);
		}
		//按排序值倒序排序
		wrapper.orderByDesc("sort");
	}

}
