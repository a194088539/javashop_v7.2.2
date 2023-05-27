package com.enation.app.javashop.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.system.SensitiveWordsMapper;
import com.enation.app.javashop.model.base.message.SensitiveWordsMsg;
import com.enation.app.javashop.model.base.dos.SensitiveWords;
import com.enation.app.javashop.framework.redis.RedisChannel;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.service.system.SensitiveWordsManager;
import com.enation.app.javashop.model.errorcode.SystemErrorCode;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.util.JsonUtil;
import com.enation.app.javashop.framework.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.javashop.framework.database.DaoSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * 敏感词业务类
 *
 * @author fk
 * @version v1.0
 * @since v7.0.0
 * 2018-08-02 11:30:59
 */
@Service
public class SensitiveWordsManagerImpl implements SensitiveWordsManager {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private SensitiveWordsMapper sensitiveWordsMapper;


    @Override
    public WebPage list(long page, long pageSize, String keyword) {

        QueryWrapper<SensitiveWords> wrapper = new QueryWrapper<>();
        wrapper.eq("is_delete",1);
        wrapper.like(keyword != null,"word_name",keyword);
        IPage<SensitiveWords> iPage = sensitiveWordsMapper.selectPage(new Page<>(page,pageSize), wrapper);
        return PageConvert.convert(iPage);

    }

    @Override
    @Transactional(value = "systemTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public SensitiveWords add(SensitiveWords sensitiveWords) {

        //不能重复添加
        QueryWrapper<SensitiveWords> wrapper = new QueryWrapper<>();
        wrapper.eq("word_name", sensitiveWords.getWordName()).eq("is_delete", 1);
        List list = sensitiveWordsMapper.selectList(wrapper);

        if (StringUtil.isNotEmpty(list)) {
            throw new ServiceException(SystemErrorCode.E928.code(), "敏感词语不能重复");
        }

        sensitiveWords.setIsDelete(1);
        sensitiveWords.setCreateTime(DateUtil.getDateline());

        sensitiveWordsMapper.insert(sensitiveWords);

        //将敏感词发送消息
        SensitiveWordsMsg msg = new SensitiveWordsMsg(sensitiveWords.getWordName(), SensitiveWordsMsg.ADD);
        redisTemplate.convertAndSend(RedisChannel.SENSITIVE_WORDS, JsonUtil.objectToJson(msg));

        return sensitiveWords;
    }

    @Override
    @Transactional(value = "systemTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public SensitiveWords edit(SensitiveWords sensitiveWords, Long id) {
        sensitiveWordsMapper.updateById(sensitiveWords);
        return sensitiveWords;
    }

    @Override
    @Transactional(value = "systemTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(Long id) {

        SensitiveWords model = this.getModel(id);

        QueryWrapper<SensitiveWords> wrapper = new QueryWrapper<>();
        wrapper.eq("id", id);
        model.setIsDelete(0);
        sensitiveWordsMapper.updateById(model);

        //将删除敏感词发送消息
        SensitiveWordsMsg msg = new SensitiveWordsMsg(model.getWordName(), SensitiveWordsMsg.DELETE);
        redisTemplate.convertAndSend(RedisChannel.SENSITIVE_WORDS, JsonUtil.objectToJson(msg));

    }

    @Override
    public SensitiveWords getModel(Long id) {
        return sensitiveWordsMapper.selectById(id);
    }

    @Override
    public List<String> listWords() {

        QueryWrapper<SensitiveWords> wrapper = new QueryWrapper<>();
        wrapper.eq("is_delete", 1);
        List<SensitiveWords> list = sensitiveWordsMapper.selectList(wrapper);

        List<String> words = new ArrayList<>();
        if (list != null) {

            for (SensitiveWords word : list) {
                words.add(word.getWordName());
            }
        }
        return words;
    }

}
