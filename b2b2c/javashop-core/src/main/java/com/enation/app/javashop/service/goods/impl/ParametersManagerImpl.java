package com.enation.app.javashop.service.goods.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.framework.util.StringUtil;
import com.enation.app.javashop.mapper.goods.ParametersMapper;
import com.enation.app.javashop.model.errorcode.GoodsErrorCode;
import com.enation.app.javashop.model.goods.dos.ParameterGroupDO;
import com.enation.app.javashop.model.goods.dos.ParametersDO;
import com.enation.app.javashop.service.goods.ParameterGroupManager;
import com.enation.app.javashop.service.goods.ParametersManager;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 参数业务类
 *
 * @author fk
 * @version v2.0
 * @since v7.0.0 2018-03-20 16:14:31
 */
@Service
public class ParametersManagerImpl implements ParametersManager {


	@Autowired
	private ParameterGroupManager parameterGroupManager;
	@Autowired
	private ParametersMapper parametersMapper;

	@Override
	public WebPage list(long page, long pageSize) {

		IPage iPage = this.parametersMapper.selectPage(new Page<>(page,pageSize),new QueryWrapper<>());

		return PageConvert.convert(iPage);
	}

	@Override
	@Transactional(value = "goodsTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ParametersDO add(ParametersDO parameters) {

		// 查询参数组
		ParameterGroupDO group = parameterGroupManager.getModel(parameters.getGroupId());
		if(group == null){
			throw new ServiceException(GoodsErrorCode.E303.code(), "所属参数组不存在");
		}
		parameters.setCategoryId(group.getCategoryId());
		// 选择项
		if(parameters.getParamType() == 2){
			if(!StringUtil.notEmpty(parameters.getOptions()) ){
				throw new ServiceException(GoodsErrorCode.E303.code(), "选择项类型必须填写选择内容");
			}
		}
		ParametersDO paramtmp = this.parametersMapper.selectOne(new QueryWrapper<ParametersDO>()
				.eq("group_id",parameters.getGroupId())
				.orderByDesc("sort")
				.last("limit 0,1"));
		if (paramtmp == null) {
			parameters.setSort(1);
		} else {
			parameters.setSort(paramtmp.getSort() + 1);
		}

		this.parametersMapper.insert(parameters);

		return parameters;
	}

	@Override
	@Transactional(value = "goodsTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ParametersDO edit(ParametersDO parameters, Long id) {

		ParametersDO temp = this.getModel(id);
		if(temp == null ){
			throw new ServiceException(GoodsErrorCode.E303.code(), "参数不存在");
		}
		parameters.setCategoryId(temp.getCategoryId());
		// 选择项
		if(parameters.getParamType() == 2){
			if(!StringUtil.notEmpty(parameters.getOptions()) ){
				throw new ServiceException(GoodsErrorCode.E303.code(), "选择项类型必须填写选择内容");
			}
		}

		BeanUtils.copyProperties(parameters,temp);

		this.parametersMapper.updateById(temp);

		return parameters;
	}

	@Override
	@Transactional(value = "goodsTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void delete(Long id) {
		this.parametersMapper.deleteById(id);
	}

	@Override
	public ParametersDO getModel(Long id) {
		return this.parametersMapper.selectById(id);
	}

	@Override
	@Transactional(value = "goodsTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void paramSort(Long paramId, String sortType) {

		String sql = "";

		ParametersDO curParam = this.getModel(paramId);

		if(curParam == null){
			throw new ServiceException(GoodsErrorCode.E303.code(), "要移动的参数不存在");
		}


		QueryWrapper wrapper = new QueryWrapper();
		wrapper.eq("group_id",curParam.getGroupId());
		if ("up".equals(sortType)) {
			wrapper.lt("sort",curParam.getSort());
			wrapper.orderByDesc("sort");
		} else if ("down".equals(sortType)) {
			wrapper.gt("sort",curParam.getSort());
			wrapper.orderByAsc("sort");
		}
		wrapper.last("limit 0,1");

		ParametersDO changeParam = this.parametersMapper.selectOne(wrapper);
		if (changeParam != null) {

			UpdateWrapper<ParametersDO> updateWrapper = new UpdateWrapper<ParametersDO>()
					.set("sort", changeParam.getSort())
					.eq("param_id", curParam.getParamId());
			this.parametersMapper.update(new ParametersDO(),updateWrapper);

			updateWrapper.set("sort", curParam.getSort())
					.eq("group_id", changeParam.getParamId());
			this.parametersMapper.update(new ParametersDO(),updateWrapper);


		}
	}

	@Override
	@Transactional(value = "goodsTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteByGroup(Long groupId) {
		this.parametersMapper.delete(new QueryWrapper<ParametersDO>().eq("group_id",groupId));
	}
}
