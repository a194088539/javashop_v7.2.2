package com.enation.app.javashop.service.member.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.member.MemberPointHistoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.javashop.model.member.dos.MemberPointHistory;
import com.enation.app.javashop.service.member.MemberPointHistoryManager;

/**
 * 会员积分表业务类
 *
 * @author zh
 * @version v7.0.0
 * @since v7.0.0
 * 2018-04-03 15:44:12
 */
@Service
public class MemberPointHistoryManagerImpl implements MemberPointHistoryManager {

    @Autowired
    private MemberPointHistoryMapper memberPointHistoryMapper;

    @Override
    public WebPage list(long page, long pageSize, Long memberId) {
        //新建查询条件包装器
        QueryWrapper<MemberPointHistory> wrapper = new QueryWrapper<>();
        //以会员ID为查询条件
        wrapper.eq("member_id", memberId);
        //以会员等级积分大于0或者消费积分大于0为查询条件
        wrapper.and(ew -> {
            ew.gt("grade_point", 0).or().gt("consum_point", 0);
        });
        //以添加时间倒序排序
        wrapper.orderByDesc("time");
        //获取会员积分操作历史分页列表数据
        IPage<MemberPointHistory> iPage = memberPointHistoryMapper.selectPage(new Page<>(page, pageSize), wrapper);
        return PageConvert.convert(iPage);
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public MemberPointHistory add(MemberPointHistory memberPointHistory) {
        //添加会员积分操作历史
        memberPointHistoryMapper.insert(memberPointHistory);
        return memberPointHistory;
    }
}
