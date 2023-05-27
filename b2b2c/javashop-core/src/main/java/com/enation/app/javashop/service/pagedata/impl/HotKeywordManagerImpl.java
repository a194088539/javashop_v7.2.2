package com.enation.app.javashop.service.pagedata.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.system.HotKeywordMapper;
import com.enation.app.javashop.model.base.CachePrefix;
import com.enation.app.javashop.model.base.message.CmsManageMsg;
import com.enation.app.javashop.model.base.rabbitmq.AmqpExchange;
import com.enation.app.javashop.model.pagedata.HotKeyword;
import com.enation.app.javashop.service.pagedata.HotKeywordManager;
import com.enation.app.javashop.model.errorcode.SystemErrorCode;
import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.database.DaoSupport;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.rabbitmq.MessageSender;
import com.enation.app.javashop.framework.rabbitmq.MqMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 热门关键字业务类
 *
 * @author fk
 * @version v1.0
 * @since v7.0.0
 * 2018-06-04 10:43:23
 */
@Service
public class HotKeywordManagerImpl implements HotKeywordManager {

    @Autowired
    private MessageSender messageSender;
    @Autowired
    private Cache cache;
    @Autowired
    private HotKeywordMapper hotKeywordMapper;

    @Override
    public WebPage list(long page, long pageSize) {

        QueryWrapper<HotKeyword> wrapper = new QueryWrapper<>();
        IPage<HotKeyword> iPage = hotKeywordMapper.selectPage(new Page<>(page,pageSize), wrapper);
        return PageConvert.convert(iPage);
    }

    @Override
    @Transactional(value = "systemTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public HotKeyword add(HotKeyword hotKeyword) {

        hotKeywordMapper.insert(hotKeyword);
        hotKeyword.setId(hotKeyword.getId());

        this.sendMessage();
        return hotKeyword;
    }

    @Override
    @Transactional(value = "systemTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public HotKeyword edit(HotKeyword hotKeyword, Long id) {

        HotKeyword keyword = this.getModel(id);
        if (keyword == null) {
            throw new ServiceException(SystemErrorCode.E954.code(), "该记录不存在，请正确操作");
        }

        hotKeywordMapper.updateById(hotKeyword);

        this.sendMessage();

        return hotKeyword;
    }

    @Override
    @Transactional(value = "systemTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(Long id) {
        hotKeywordMapper.deleteById(HotKeyword.class);
        this.sendMessage();
    }

    @Override
    public HotKeyword getModel(Long id) {
        return hotKeywordMapper.selectById(HotKeyword.class);
    }

    @Override
    public List<HotKeyword> listByNum(Integer num) {
        List<HotKeyword> result = (List<HotKeyword>) cache.get(CachePrefix.HOT_KEYWORD.getPrefix() + num);

        if (result == null || result.isEmpty()) {
            if (num == null) {
                num = 5;
            }

            result = hotKeywordMapper.queryForLimit(num);

            cache.put(CachePrefix.HOT_KEYWORD.getPrefix() + num, result);
        }

        return result;
    }

    /**
     * 发送首页变化消息
     */
    private void sendMessage() {

        //使用模糊删除
        this.cache.vagueDel(CachePrefix.HOT_KEYWORD.getPrefix());

        CmsManageMsg cmsManageMsg = new CmsManageMsg();

        this.messageSender.send(new MqMessage(AmqpExchange.PC_INDEX_CHANGE, AmqpExchange.PC_INDEX_CHANGE + "_ROUTING", cmsManageMsg));

    }
}
