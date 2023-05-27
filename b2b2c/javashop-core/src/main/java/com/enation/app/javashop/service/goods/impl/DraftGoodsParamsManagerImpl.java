package com.enation.app.javashop.service.goods.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.enation.app.javashop.mapper.goods.DraftGoodsParamsMapper;
import com.enation.app.javashop.mapper.goods.ParameterGroupMapper;
import com.enation.app.javashop.model.goods.dos.DraftGoodsParamsDO;
import com.enation.app.javashop.model.goods.dos.GoodsParamsDO;
import com.enation.app.javashop.model.goods.dos.ParameterGroupDO;
import com.enation.app.javashop.model.goods.vo.GoodsParamsGroupVO;
import com.enation.app.javashop.model.goods.vo.GoodsParamsVO;
import com.enation.app.javashop.service.goods.DraftGoodsParamsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 草稿商品参数表业务类
 * @author fk
 * @version v2.0
 * @since v7.0.0
 * 2018-03-26 11:31:20
 */
@Service
public class DraftGoodsParamsManagerImpl implements DraftGoodsParamsManager{

	@Autowired
	private DraftGoodsParamsMapper draftGoodsParamsMapper;

	@Autowired
	private ParameterGroupMapper parameterGroupMapper;

	@Override
	@Transactional(value = "goodsTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void addParams(List<GoodsParamsDO> goodsParamsList, Long draftGoodsId) {

		this.draftGoodsParamsMapper.deleteById(draftGoodsId);
		for (GoodsParamsDO param : goodsParamsList){
			DraftGoodsParamsDO draftGoodsParams = new DraftGoodsParamsDO(param);
			draftGoodsParams.setDraftGoodsId(draftGoodsId);
			this.draftGoodsParamsMapper.insert(draftGoodsParams);
		}

	}

	@Override
	public List<GoodsParamsGroupVO> getParamByCatAndDraft(Long categoryId, Long draftGoodsId) {

		//查询参数组
		QueryWrapper<ParameterGroupDO> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("category_id",categoryId);
		List<ParameterGroupDO> groupList = this.parameterGroupMapper.selectList(queryWrapper);

		List<GoodsParamsVO> paramList = draftGoodsParamsMapper.queryDraftGoodsParamsValue(categoryId,draftGoodsId);

		List<GoodsParamsGroupVO> resList = this.convertParamList(groupList, paramList);

		return resList;
	}

	/**
	 * 拼装返回值
	 *
	 * @param paramList
	 * @return
	 */
	private List<GoodsParamsGroupVO> convertParamList(List<ParameterGroupDO> groupList, List<GoodsParamsVO> paramList) {
		Map<Long, List<GoodsParamsVO>> map = new HashMap<>(16);
		for (GoodsParamsVO param : paramList) {
			if (map.get(param.getGroupId()) != null) {
				map.get(param.getGroupId()).add(param);
			} else {
				List<GoodsParamsVO> list = new ArrayList<>();
				list.add(param);
				map.put(param.getGroupId(), list);
			}
		}
		List<GoodsParamsGroupVO> resList = new ArrayList<>();
		for (ParameterGroupDO group : groupList) {
			GoodsParamsGroupVO list = new GoodsParamsGroupVO();
			list.setGroupName(group.getGroupName());
			list.setGroupId(group.getGroupId());
			list.setParams(map.get(group.getGroupId()));
			resList.add(list);
		}
		return resList;
	}

}
