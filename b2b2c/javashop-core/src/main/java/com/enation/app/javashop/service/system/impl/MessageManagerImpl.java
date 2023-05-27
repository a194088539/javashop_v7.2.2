package com.enation.app.javashop.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.system.MessageMapper;
import com.enation.app.javashop.model.base.rabbitmq.AmqpExchange;
import com.enation.app.javashop.model.errorcode.MemberErrorCode;
import com.enation.app.javashop.model.system.dos.Message;
import com.enation.app.javashop.model.system.dto.MessageDTO;
import com.enation.app.javashop.model.system.dto.MessageQueryParam;
import com.enation.app.javashop.model.system.vo.MessageVO;
import com.enation.app.javashop.service.system.MessageManager;
import com.enation.app.javashop.framework.context.user.AdminUserContext;
import com.enation.app.javashop.framework.database.DaoSupport;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.util.BeanUtil;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.framework.util.StringUtil;
import com.enation.app.javashop.framework.rabbitmq.MessageSender;
import com.enation.app.javashop.framework.rabbitmq.MqMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 站内消息业务类
 *
 * @author zh
 * @version v7.0.0
 * @since v7.0.0
 * 2018-07-04 21:50:52
 */
@Service
public class MessageManagerImpl implements MessageManager {

    @Autowired
    private MessageSender messageSender;
    @Autowired
    private MessageMapper messageMapper;

    @Override
    public WebPage list(MessageQueryParam param) {
        IPage<MessageDTO> webPage = messageMapper.queryMessageDTOList(new Page<>(param.getPageNo(), param.getPageSize()),param);
        return PageConvert.convert(webPage);
    }

    @Override
    @Transactional(value = "systemTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Message add(MessageVO messageVO) {
        if (messageVO.getSendType().equals(1)) {
            if (StringUtil.isEmpty(messageVO.getMemberIds())) {
                throw new ServiceException(MemberErrorCode.E122.code(), "请指定发送会员");
            }
        }
        Message message = new Message();
        BeanUtil.copyProperties(messageVO, message);
        message.setAdminId(AdminUserContext.getAdmin().getUid());
        message.setAdminName(AdminUserContext.getAdmin().getUsername());
        message.setSendTime(DateUtil.getDateline());
        message.setDisabled(0);

        messageMapper.insert(message);
        Long id = message.getId();
        message.setId(id);
        this.messageSender.send(new MqMessage(AmqpExchange.MEMBER_MESSAGE, AmqpExchange.MEMBER_MESSAGE + "_ROUTING", id));
        return message;
    }

    @Override
    public Message get(Long id) {
        return messageMapper.selectById(id);
    }
}
