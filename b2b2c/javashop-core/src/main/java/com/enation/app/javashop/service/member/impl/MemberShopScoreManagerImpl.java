package com.enation.app.javashop.service.member.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.member.MemberShopScoreMapper;
import com.enation.app.javashop.model.member.dos.MemberShopScore;
import com.enation.app.javashop.model.member.dto.MemberShopScoreDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.javashop.service.member.MemberShopScoreManager;

import java.util.List;

/**
 * 店铺评分业务类
 * @author fk
 * @version v1.0
 * @since v7.0.0
 * 2018-05-03 10:38:00
 */
@Service
public class MemberShopScoreManagerImpl implements MemberShopScoreManager{

	@Autowired
	private MemberShopScoreMapper memberShopScoreMapper;

	@Override
	public WebPage list(long page, long pageSize){
		//获取店铺评分信息分页列表
		IPage<MemberShopScore> iPage = memberShopScoreMapper.selectPage(new Page<>(page, pageSize), new QueryWrapper<>());
		return PageConvert.convert(iPage);
	}

	@Override
	@Transactional(value = "memberTransactionManager",propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public	MemberShopScore  add(MemberShopScore memberShopScore) {
		//店铺评分信息入库
		memberShopScoreMapper.insert(memberShopScore);
		return memberShopScore;
	}

	@Override
	@Transactional(value = "memberTransactionManager",propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public	MemberShopScore  edit(MemberShopScore memberShopScore, Long id) {
		//设置主键ID
		memberShopScore.setScoreId(id);
		//修改店铺评分信息
		memberShopScoreMapper.updateById(memberShopScore);
		return memberShopScore;
	}

	@Override
	@Transactional(value = "memberTransactionManager",propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public	void delete( Long id) {
		memberShopScoreMapper.deleteById(id);
	}

	@Override
	public	MemberShopScore getModel(Long id)	{
		return memberShopScoreMapper.selectById(id);
	}

	@Override
	public List<MemberShopScoreDTO> queryEveryShopScore() {
		//查询每个店铺的评分集合
		List<MemberShopScoreDTO> shopScoreList = memberShopScoreMapper.selectScoreDto();
		return shopScoreList;
	}

	@Override
	public MemberShopScore getModel(Long memberId, String orderSn) {
		//新建查询条件包装器
		QueryWrapper<MemberShopScore> wrapper = new QueryWrapper<>();
		//以会员ID为查询条件
		wrapper.eq("member_id", memberId);
		//以订单编号为查询条件
		wrapper.eq("order_sn", orderSn);
		return memberShopScoreMapper.selectOne(wrapper);
	}
}
