package com.enation.app.javashop.service.shop.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.member.ShopNoticeLogMapper;
import com.enation.app.javashop.model.shop.dos.ShopNoticeLogDO;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.service.shop.ShopNoticeLogManager;

/**
 * 店铺站内消息业务类
 *
 * @author zjp
 * @version v7.0.0
 * @since v7.0.0
 * 2018-07-10 10:21:45
 */
@Service
public class ShopNoticeLogManagerImpl implements ShopNoticeLogManager {

    @Autowired
    private ShopNoticeLogMapper shopNoticeLogMapper;

    @Override
    public WebPage list(long page, long pageSize, String type, Integer isRead) {
        //新建查询条件包装器
        QueryWrapper<ShopNoticeLogDO> wrapper = new QueryWrapper<>();
        //以店铺ID为查询条件
        wrapper.eq("shop_id", UserContext.getSeller().getSellerId());
        //以删除状态为未删除作为查询条件 1：删除，0：未删除
        wrapper.eq("is_delete", 0);
        //以是否已读为查询条件 1：已读，0：未读
        wrapper.eq("is_read", isRead);
        //如果消息类型不为空，以消息类型为查询条件
        wrapper.eq(StringUtil.notEmpty(type), "type", type);
        //以消息发送时间倒序排序
        wrapper.orderByDesc("send_time");
        //获取店铺站内消息分页列表
        IPage<ShopNoticeLogDO> iPage = shopNoticeLogMapper.selectPage(new Page<>(page, pageSize), wrapper);
        return PageConvert.convert(iPage);
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ShopNoticeLogDO add(ShopNoticeLogDO shopNoticeLog) {
        //店铺站内消息入库
        shopNoticeLogMapper.insert(shopNoticeLog);
        return shopNoticeLog;
    }


    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(Long[] ids) {
        //新建修改条件包装器
        UpdateWrapper<ShopNoticeLogDO> wrapper = new UpdateWrapper<>();
        //修改状态为删除状态 0：正常，1：删除
        wrapper.set("is_delete", 1);
        //以消息ID为修改条件
        wrapper.in("id", ids);
        //以店铺ID为修改条件
        wrapper.eq("shop_id", UserContext.getSeller().getSellerId());
        //修改店铺站内消息为删除状态
        shopNoticeLogMapper.update(new ShopNoticeLogDO(), wrapper);
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void read(Long[] ids) {
        //新建修改条件包装器
        UpdateWrapper<ShopNoticeLogDO> wrapper = new UpdateWrapper<>();
        //修改读取状态为已读状态 0：未读，1：已读
        wrapper.set("is_read", 1);
        //以消息ID为修改条件
        wrapper.in("id", ids);
        //以店铺ID为修改条件
        wrapper.eq("shop_id", UserContext.getSeller().getSellerId());
        //修改店铺站内消息为删除状态
        shopNoticeLogMapper.update(new ShopNoticeLogDO(), wrapper);
    }
}
