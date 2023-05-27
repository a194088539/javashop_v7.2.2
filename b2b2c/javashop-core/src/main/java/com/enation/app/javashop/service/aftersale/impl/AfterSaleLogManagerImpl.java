package com.enation.app.javashop.service.aftersale.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.mapper.trade.aftersale.AfterSaleLogMapper;
import com.enation.app.javashop.model.aftersale.dos.AfterSaleLogDO;
import com.enation.app.javashop.service.aftersale.AfterSaleLogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 售后日志业务接口实现类
 * @author duanmingyu
 * @version v1.0
 * @since v7.1.5
 * 2019-10-16
 */
@Service
public class AfterSaleLogManagerImpl implements AfterSaleLogManager {

    @Autowired
    private AfterSaleLogMapper afterSaleLogMapper;

    @Override
    public void add(String serviceSn, String logDetail, String operator) {
        //新建售后服务操作日志对象
        AfterSaleLogDO afterSaleLogDO = new AfterSaleLogDO();
        //设置售后服务单号
        afterSaleLogDO.setSn(serviceSn);
        //设置日志内容
        afterSaleLogDO.setLogDetail(logDetail);
        //设置操作人
        afterSaleLogDO.setOperator(operator);
        //设置日志添加时间
        afterSaleLogDO.setLogTime(DateUtil.getDateline());
        //售后服务操作日志入库
        afterSaleLogMapper.insert(afterSaleLogDO);
    }

    @Override
    public List<AfterSaleLogDO> list(String serviceSn) {
        //新建查询条件包装器
        QueryWrapper<AfterSaleLogDO> wrapper = new QueryWrapper<>();
        //以售后服务单号为查询条件
        wrapper.eq("sn", serviceSn);
        //以日志添加时间倒序排序
        wrapper.orderByDesc("log_time");
        //返回售后服务日志信息集合
        return afterSaleLogMapper.selectList(wrapper);
    }
}
