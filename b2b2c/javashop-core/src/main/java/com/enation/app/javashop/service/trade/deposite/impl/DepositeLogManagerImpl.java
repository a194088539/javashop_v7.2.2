package com.enation.app.javashop.service.trade.deposite.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.framework.util.StringUtil;
import com.enation.app.javashop.mapper.trade.deposite.DepositeLogMapper;
import com.enation.app.javashop.model.member.dto.DepositeParamDTO;
import com.enation.app.javashop.model.trade.deposite.DepositeLogDO;
import com.enation.app.javashop.service.trade.deposite.DepositeLogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description: 预存款日志业务类
 * @author: liuyulei
 * @create: 2019-12-30 17:49
 * @version:1.0
 * @since:7.1.5
 **/
@Service
public class DepositeLogManagerImpl implements DepositeLogManager {

    @Autowired
    private DepositeLogMapper depositeLogMapper;

    @Override
    public void add(DepositeLogDO logDO) {
        logDO.setTime(DateUtil.getDateline());
        this.depositeLogMapper.insert(logDO);
    }

    @Override
    public WebPage list(DepositeParamDTO paramDTO) {

        IPage iPage = this.depositeLogMapper.selectPage(new Page<>(paramDTO.getPageNo(), paramDTO.getPageSize()),
                new QueryWrapper<DepositeLogDO>()
                        //根据充值时间查询
                        .ge(paramDTO.getStartTime() != null,"time",paramDTO.getStartTime())
                        //根据充值时间查询
                        .le(paramDTO.getStartTime() != null,"time",paramDTO.getEndTime())
                        //根据会员名称查询
                        .eq(!StringUtil.isEmpty(paramDTO.getMemberName()),"member_name",paramDTO.getMemberName())
                        //根据会员id查询
                        .eq(paramDTO.getMemberId() != null,"member_id",paramDTO.getMemberId())
                        //时间倒序
                        .orderByDesc("time"));

        return PageConvert.convert(iPage);
    }
}
