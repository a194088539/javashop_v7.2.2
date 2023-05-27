package com.enation.app.javashop.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.system.ComplainTopicMapper;
import com.enation.app.javashop.model.errorcode.SystemErrorCode;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.framework.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.system.dos.ComplainTopic;
import com.enation.app.javashop.service.system.ComplainTopicManager;

import java.util.List;

/**
 * 投诉主题业务类
 *
 * @author fk
 * @version v2.0
 * @since v2.0
 * 2019-11-26 16:06:44
 */
@Service
public class ComplainTopicManagerImpl implements ComplainTopicManager {

    @Autowired
    private ComplainTopicMapper complainTopicMapper;

    @Override
    public WebPage list(long page, long pageSize) {

        QueryWrapper<ComplainTopic> wrapper = new QueryWrapper<>();
        IPage<ComplainTopic> iPage = complainTopicMapper.selectPage(new Page<>(page,pageSize), wrapper);
        return PageConvert.convert(iPage);
    }

    @Override
    @Transactional(value = "systemTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ComplainTopic add(ComplainTopic complainTopic) {

        this.checkName(complainTopic.getTopicName(), null);
        complainTopic.setCreateTime(DateUtil.getDateline());

        complainTopicMapper.insert(complainTopic);
        complainTopic.setTopicId(complainTopic.getTopicId());

        return complainTopic;
    }

    /**
     * 查看主体名称是否重复
     *
     * @param topicName
     * @param id
     */
    private void checkName(String topicName, Long id) {

        QueryWrapper<ComplainTopic> wrapper = new QueryWrapper<>();
        wrapper.eq("topic_name", topicName);
        wrapper.ne(id != null,"topic_id", id);
        List list = complainTopicMapper.selectList(wrapper);

        if (StringUtil.isNotEmpty(list)) {
            throw new ServiceException(SystemErrorCode.E929.code(), "主题重复");
        }
    }

    @Override
    @Transactional(value = "systemTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ComplainTopic edit(ComplainTopic complainTopic, Long id) {
        this.checkName(complainTopic.getTopicName(), id);
        complainTopicMapper.updateById(complainTopic);

        return complainTopic;
    }

    @Override
    @Transactional(value = "systemTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(Long id) {
        complainTopicMapper.deleteById(ComplainTopic.class);
    }

    @Override
    public ComplainTopic getModel(Long id) {
        return complainTopicMapper.selectById(ComplainTopic.class);
    }

    @Override
    public List<ComplainTopic> list() {

        QueryWrapper<ComplainTopic> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("create_time");
        return complainTopicMapper.selectList(wrapper);
    }
}
