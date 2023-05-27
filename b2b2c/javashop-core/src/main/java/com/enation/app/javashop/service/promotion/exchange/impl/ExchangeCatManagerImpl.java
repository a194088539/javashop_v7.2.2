package com.enation.app.javashop.service.promotion.exchange.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.enation.app.javashop.mapper.promotion.exchange.ExchangeCatMapper;
import com.enation.app.javashop.model.errorcode.GoodsErrorCode;
import com.enation.app.javashop.model.errorcode.PromotionErrorCode;
import com.enation.app.javashop.model.promotion.exchange.dos.ExchangeCat;
import com.enation.app.javashop.service.promotion.exchange.ExchangeCatManager;
import com.enation.app.javashop.service.promotion.exchange.ExchangeGoodsManager;
import com.enation.app.javashop.framework.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 积分兑换分类业务类
 * @author Snow
 * @version v7.0.0
 * @since v7.0.0
 * 2018-05-29 16:56:22
 */
@SuppressWarnings("Duplicates")
@Service
public class ExchangeCatManagerImpl implements ExchangeCatManager {

	@Autowired
	private ExchangeCatMapper exchangeCatMapper;

	@Autowired
	private ExchangeGoodsManager exchangeGoodsManager;

	@Override
	public List<ExchangeCat> list(Long parentId){
		//新建查询条件包装器
		QueryWrapper<ExchangeCat> wrapper = new QueryWrapper<>();
		//以分类父ID为查询条件
		wrapper.eq("parent_id", parentId);
		//安装分类排序正序查询
		wrapper.orderByAsc("category_order");
		//获取积分商品分类信息集合
		List<ExchangeCat> list = exchangeCatMapper.selectList(wrapper);
		return list;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public	ExchangeCat  add(ExchangeCat exchangeCat) {
		//新建查询条件包装器
		QueryWrapper<ExchangeCat> wrapper = new QueryWrapper<>();
		//以分类名称为查询条件
		wrapper.eq("name", exchangeCat.getName());
		//获取积分商品分类信息集合
		List list = exchangeCatMapper.selectList(wrapper);
		//如果结果集长度大于0，证明此分类已存在
		if(list.size() > 0){
			throw new ServiceException(PromotionErrorCode.E407.code(),"积分分类名称重复");
		}
		//如果分类父id为空，默认设置为0
		if(exchangeCat.getParentId()==null){
			exchangeCat.setParentId(0L);
		}
		//如果分类是否显示的值为空，默认设置为1，1：显示，0：不显示
		if(exchangeCat.getListShow()==null){
			exchangeCat.setListShow(1);
		}
		//积分商品分类信息入库操作
		exchangeCatMapper.insert(exchangeCat);
		return exchangeCat;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public	ExchangeCat  edit(ExchangeCat	exchangeCat,Long id){
		//新建查询条件包装器
		QueryWrapper<ExchangeCat> wrapper = new QueryWrapper<>();
		//以分类名称为查询条件
		wrapper.eq("name", exchangeCat.getName());
		//以分类id不等于当前分类ID为查询条件
		wrapper.ne("category_id", id);
		//获取积分商品分类信息集合
		List list = exchangeCatMapper.selectList(wrapper);
		//如果结果集长度大于0，证明此分类已存在
		if(list.size() > 0){
			throw new ServiceException(PromotionErrorCode.E407.code(),"积分分类名称重复");
		}
		//如果分类父id为空，默认设置为0
		if(exchangeCat.getParentId()==null){
			exchangeCat.setParentId(0L);
		}
		//如果分类是否显示的值为空，默认设置为1，1：显示，0：不显示
		if(exchangeCat.getListShow()==null){
			exchangeCat.setListShow(1);
		}
		//设置积分商品分类主键ID
		exchangeCat.setCategoryId(id);
		//修改积分商品分类信息
		exchangeCatMapper.updateById(exchangeCat);
		return exchangeCat;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public void delete( Long id) {
		//判断当前分类下面是否已经绑定了商品，如果有则不允许删除
		if (exchangeGoodsManager.getModelByCategoryId(id) != null){
			throw new ServiceException(GoodsErrorCode.E300.code(), "此类别下存在商品不能删除");
		}
		//删除积分商品分类信息
		exchangeCatMapper.deleteById(id);
	}

	@Override
	public	ExchangeCat getModel(Long id)	{
		//根据积分商品分类ID获取一条积分商品分类信息
		return exchangeCatMapper.selectById(id);
	}
}
