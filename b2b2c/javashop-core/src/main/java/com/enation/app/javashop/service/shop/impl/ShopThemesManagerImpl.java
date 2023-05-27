package com.enation.app.javashop.service.shop.impl;


import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.rabbitmq.MessageSender;
import com.enation.app.javashop.framework.rabbitmq.MqMessage;
import com.enation.app.javashop.model.base.rabbitmq.AmqpExchange;
import com.enation.app.javashop.model.shop.enums.ShopMessageTypeEnum;
import com.enation.app.javashop.model.shop.vo.ShopChangeMsg;
import com.enation.app.javashop.model.shop.vo.ShopVO;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.member.ShopDetailMapper;
import com.enation.app.javashop.mapper.member.ShopThemesMapper;
import com.enation.app.javashop.model.shop.dos.ShopDetailDO;
import com.enation.app.javashop.service.shop.ShopThemesManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.security.model.Seller;
import com.enation.app.javashop.model.errorcode.ShopErrorCode;
import com.enation.app.javashop.model.shop.dos.ShopThemesDO;
import com.enation.app.javashop.model.shop.enums.ShopThemesEnum;
import com.enation.app.javashop.model.shop.vo.ShopThemesVO;

/**
 * 店铺模版业务类
 *
 * @author zjp
 * @version v7.0.0
 * @since v7.0.0
 * 2018-03-27 14:17:32
 */
@Service
public class ShopThemesManagerImpl implements ShopThemesManager {

	@Autowired
	private ShopThemesMapper shopThemesMapper;

	@Autowired
	private ShopDetailMapper shopDetailMapper;

	@Autowired
	private MessageSender messageSender;

	@Override
	public WebPage list(long page, long pageSize, String type){
		//校验店铺主题模板类型
		if(!type.equals(ShopThemesEnum.PC.name())&&!type.equals(ShopThemesEnum.WAP.name())) {
			throw new ServiceException(ShopErrorCode.E201.name(),"模版类型不匹配");
		}

		//新建查询条件包装器
		QueryWrapper<ShopThemesDO> wrapper = new QueryWrapper<>();
		//以主题模板类型为查询条件
		wrapper.eq("type", type);
		//获取店铺主题模板分页列表数据
		IPage<ShopThemesDO> iPage = shopThemesMapper.selectPage(new Page<>(page, pageSize), wrapper);
		return PageConvert.convert(iPage);
	}

	@Override
	@Transactional(value = "memberTransactionManager",propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public	ShopThemesDO  add(ShopThemesDO	shopThemes)	{
		//新建查询条件包装器
		QueryWrapper<ShopThemesDO> wrapper = new QueryWrapper<>();
		//以主题模板标识为查询条件
		wrapper.eq("mark", shopThemes.getMark());
		//获取模板数量
		Integer num = shopThemesMapper.selectCount(wrapper);
		//如果数量大于0，证明标识重复
		if (num > 0) {
			throw  new ServiceException(ShopErrorCode.E225.name(),"店铺模版标识重复");
		}

		//如果店铺不存在主题模版，则设置当前模板为默认模版
		if (shopThemesMapper.selectCount(new QueryWrapper<>()) == 0) {
			shopThemes.setIsDefault(1);
		}

		//只能有一个默认模板
		if (shopThemes.getIsDefault().equals(1)) {
			//新建修改条件包装器
			UpdateWrapper<ShopThemesDO> updateWrapper = new UpdateWrapper<>();
			//设置店铺其他模板都为非默认模板
			updateWrapper.set("is_default", 0);
			//以模板类型为修改条件
			updateWrapper.eq("type", shopThemes.getType());
			//修改操作
			shopThemesMapper.update(new ShopThemesDO(), updateWrapper);
		}

		//新增店铺主题模板
		shopThemesMapper.insert(shopThemes);
		return shopThemes;
	}

	@Override
	@Transactional(value = "memberTransactionManager",propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public	ShopThemesDO  edit(ShopThemesDO	shopThemes,Long id){
		//根据模板ID获取店铺模板信息并校验
		ShopThemesDO model = this.getModel(id);
		if(model==null) {
			throw new ServiceException(ShopErrorCode.E202.name(), "模版不存在");
		}

		//新建查询条件包装器
		QueryWrapper<ShopThemesDO> wrapper = new QueryWrapper<>();
		//以主题模板标识为查询条件
		wrapper.eq("mark", shopThemes.getMark());
		//以主题模板id不等于当前模板ID为查询条件
		wrapper.ne("id", id);
		//获取店铺模板数量
		Integer num = shopThemesMapper.selectCount(wrapper);
		//数量大于0证明店铺模板标识重复
		if (num > 0) {
			throw new ServiceException(ShopErrorCode.E225.name(),"店铺模版标识重复");
		}

		//不允许将默认模板改成非默认
		if (model.getIsDefault().equals(1) && shopThemes.getIsDefault().equals(0)) {
			throw new ServiceException(ShopErrorCode.E225.name(), "至少要有一个默认模板");
		}

		//只能有一个默认模板
		if(shopThemes.getIsDefault().equals(1)){
			//新建修改条件包装器
			UpdateWrapper<ShopThemesDO> updateWrapper = new UpdateWrapper<>();
			//设置店铺其他模板都为非默认模板
			updateWrapper.set("is_default", 0);
			//以模板类型为修改条件
			updateWrapper.eq("type", shopThemes.getType());
			//修改操作
			shopThemesMapper.update(new ShopThemesDO(), updateWrapper);
		}

		//修改店铺模板信息
		shopThemes.setId(id);
		shopThemesMapper.updateById(shopThemes);
		return shopThemes;
	}

	@Override
	@Transactional(value = "memberTransactionManager",propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public	void delete( Long id)	{
		//根据模板ID获取店铺模板信息并校验
		ShopThemesDO shopThemes = this.getModel(id);
		if(shopThemes==null) {
			throw new ServiceException(ShopErrorCode.E202.name(), "模版不存在");
		}
		//店铺的默认主题模板不允许删除
		if (shopThemes.getIsDefault() == 1) {
			throw new ServiceException(ShopErrorCode.E205.name(), "默认模版不能删除");
		}

		//新建查询条件包装器
		QueryWrapper<ShopThemesDO> wrapper = new QueryWrapper<>();
		//以默认模板为查询条件 0：非默认模板，1：默认模板
		wrapper.eq("is_default", 1);
		//以模板类型为查询条件
		wrapper.eq("type", shopThemes.getType());
		//查询到当前默认模版
		ShopThemesDO shopThemesDO = shopThemesMapper.selectOne(wrapper);

		//将用到此模版的店铺修改成默认模版
		updateShopThemes(id, shopThemesDO.getId(), shopThemes.getType(), shopThemesDO.getMark());

		//删除模版
		shopThemesMapper.deleteById(id);
	}

	/**
	 * 修改店铺默认模板信息
	 * @param oldThemeId 原店铺模板ID
	 * @param newThemeId 新店铺模板ID
	 * @param mark 模板标识
	 * @param type 模板类型
	 */
	private void updateShopThemes(Long oldThemeId, Long newThemeId, String mark, String type) {
		//判断模板类型是PC还是WAP
		if(ShopThemesEnum.PC.name().equals(type)){
			//新建修改条件包装器
			UpdateWrapper<ShopDetailDO> updateWrapper = new UpdateWrapper<>();
			//修改店铺主题ID
			updateWrapper.set("shop_themeid", newThemeId);
			//修改店铺主题标记
			updateWrapper.set("shop_theme_path", mark);
			//以原店铺模板ID为修改条件
			updateWrapper.eq("shop_themeid", oldThemeId);
			//修改PC端店铺的默认模版
			shopDetailMapper.update(new ShopDetailDO(), updateWrapper);
		}else{
			//新建修改条件包装器
			UpdateWrapper<ShopDetailDO> updateWrapper = new UpdateWrapper<>();
			//修改店铺主题ID
			updateWrapper.set("wap_themeid", newThemeId);
			//修改店铺主题标记
			updateWrapper.set("wap_theme_path", mark);
			//以原店铺模板ID为修改条件
			updateWrapper.eq("wap_themeid", oldThemeId);
			//修改WAP端店铺的默认模版
			shopDetailMapper.update(new ShopDetailDO(), updateWrapper);
		}
	}

	@Override
	public	ShopThemesDO getModel(Long id)	{
		return shopThemesMapper.selectById(id);
	}

	@Override
	@Transactional(value = "memberTransactionManager",propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public void changeShopThemes(Long themesId) {
		//获取当前登录的商家信息
		Seller seller = UserContext.getSeller();
		//根据模板ID获取店铺模板信息并校验
		ShopThemesDO model = this.getModel(themesId);
		if (model == null) {
			throw new ServiceException(ShopErrorCode.E202.name(), "模版不存在");
		}
		//判断模板类型
		if (ShopThemesEnum.PC.name().equals(model.getType())) {
			//新建修改条件包装器
			UpdateWrapper<ShopDetailDO> updateWrapper = new UpdateWrapper<>();
			//修改店铺主题ID
			updateWrapper.set("shop_themeid", themesId);
			//修改店铺主题标记
			updateWrapper.set("shop_theme_path", model.getMark());
			//以原店铺模板ID为修改条件
			updateWrapper.eq("shop_id", seller.getSellerId());
			//修改PC端店铺的默认模版
			shopDetailMapper.update(new ShopDetailDO(), updateWrapper);
		}else {
			//新建修改条件包装器
			UpdateWrapper<ShopDetailDO> updateWrapper = new UpdateWrapper<>();
			//修改店铺主题ID
			updateWrapper.set("wap_themeid", themesId);
			//修改店铺主题标记
			updateWrapper.set("wap_theme_path", model.getMark());
			//以原店铺模板ID为修改条件
			updateWrapper.eq("shop_id", seller.getSellerId());
			//修改WAP端店铺的默认模版
			shopDetailMapper.update(new ShopDetailDO(), updateWrapper);
		}
		ShopVO shopVO = new ShopVO();
		shopVO.setShopId(seller.getSellerId());
		//发送店铺信息改变消息
		messageSender.send(new MqMessage(AmqpExchange.SHOP_CHANGE, AmqpExchange.SHOP_CHANGE + "_ROUTING", new ShopChangeMsg(null, shopVO, ShopMessageTypeEnum.PAGE_CREATE.value())));
	}

	@Override
	public ShopThemesDO getDefaultShopThemes(String type) {
		//校验店铺模板类型
		if(!ShopThemesEnum.PC.name().equals(type) && !ShopThemesEnum.WAP.name().equals(type)) {
			throw new ServiceException(ShopErrorCode.E201.name(),"模版类型不匹配");
		}
		//新建查询条件包装器
		QueryWrapper<ShopThemesDO> wrapper = new QueryWrapper<>();
		//以默认店铺模板为查询条件
		wrapper.eq("is_default", 1);
		//以模板类型为查询条件
		wrapper.eq("type", type);
		return shopThemesMapper.selectOne(wrapper);
	}

	@Override
	public List<ShopThemesVO> list(String type) {
		//校验店铺模板类型
		if(!ShopThemesEnum.PC.name().equals(type) && !ShopThemesEnum.WAP.name().equals(type)) {
			throw new ServiceException(ShopErrorCode.E201.name(),"模版类型不匹配");
		}

		//新建查询条件包装器
		QueryWrapper<ShopThemesDO> wrapper = new QueryWrapper<>();
		//以模板类型为查询条件
		wrapper.eq("type", type);
		//查询店铺主题模板集合
		List<ShopThemesVO> list = shopThemesMapper.selectShopThemesListVo(wrapper);
		return list;
	}

	@Override
	public ShopThemesDO get(String type) {
		//获取当前登录的商家信息
		Seller seller = UserContext.getSeller();
		String sql = "";
		//新建查询条件包装器
		QueryWrapper<ShopDetailDO> wrapper = new QueryWrapper<>();
		//以店铺id为查询条件
		wrapper.eq("shop_id", seller.getSellerId());
		//获取店铺详情信息
		ShopDetailDO shopDetailDO = shopDetailMapper.selectOne(wrapper);
		Long id = null;
		//判断店铺模板类型
		if (ShopThemesEnum.PC.name().equals(type)) {
			id = shopDetailDO.getShopThemeid();
		} else {
			id = shopDetailDO.getWapThemeid();
		}
		return this.getModel(id);
	}
}
