package com.enation.app.javashop.service.pagedata.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.enation.app.javashop.mapper.system.FocusPictureMapper;
import com.enation.app.javashop.model.base.rabbitmq.AmqpExchange;
import com.enation.app.javashop.model.base.message.CmsManageMsg;
import com.enation.app.javashop.model.pagedata.FocusPicture;
import com.enation.app.javashop.service.pagedata.FocusPictureManager;
import com.enation.app.javashop.model.errorcode.SystemErrorCode;
import com.enation.app.javashop.model.system.enums.ClientType;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.validation.annotation.DemoSiteDisable;
import com.enation.app.javashop.framework.rabbitmq.MessageSender;
import com.enation.app.javashop.framework.rabbitmq.MqMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.javashop.framework.database.DaoSupport;

import java.util.List;

/**
 * 焦点图业务类
 *
 * @author fk
 * @version v1.0
 * @since v7.0.0
 * 2018-05-21 15:23:23
 */
@Service
public class FocusPictureManagerImpl implements FocusPictureManager {

    @Autowired
    private MessageSender messageSender;
    @Autowired
    private FocusPictureMapper focusPictureMapper;


    @Override
    public List list(String clientType) {

        QueryWrapper<FocusPicture> wrapper = new QueryWrapper<>();
        wrapper.eq("client_type", clientType).orderByAsc("id");
        return focusPictureMapper.selectList(wrapper);

    }

    @Override
    @DemoSiteDisable
    @Transactional(value = "systemTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public FocusPicture add(FocusPicture cmsFocusPicture) {
        //焦点图不能超过5个
        QueryWrapper<FocusPicture> wrapper = new QueryWrapper<>();
        wrapper.eq("client_type",cmsFocusPicture.getClientType());
        Integer count = focusPictureMapper.selectCount(wrapper);

        if (count >= 5) {
            throw new ServiceException(SystemErrorCode.E956.code(), "焦点图数量不能超过五张");
        }

        focusPictureMapper.insert(cmsFocusPicture);
        cmsFocusPicture.setId(cmsFocusPicture.getId());

        //发送消息
        sendFocusChangeMessage(cmsFocusPicture.getClientType());

        return cmsFocusPicture;
    }

    /**
     * 发送首页变化消息
     *
     * @param clientType
     */
    private void sendFocusChangeMessage(String clientType) {

        CmsManageMsg cmsManageMsg = new CmsManageMsg();

        if (ClientType.PC.name().equals(clientType)) {
            this.messageSender.send(new MqMessage(AmqpExchange.PC_INDEX_CHANGE, AmqpExchange.PC_INDEX_CHANGE + "_ROUTING", cmsManageMsg));
        } else {
            this.messageSender.send(new MqMessage(AmqpExchange.MOBILE_INDEX_CHANGE, AmqpExchange.MOBILE_INDEX_CHANGE + "_ROUTING", cmsManageMsg));
        }
    }

    @Override
    @Transactional(value = "systemTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public FocusPicture edit(FocusPicture cmsFocusPicture, Long id) {

        focusPictureMapper.updateById(cmsFocusPicture);
        //发送消息
        sendFocusChangeMessage(cmsFocusPicture.getClientType());

        return cmsFocusPicture;
    }

    @Override
    @DemoSiteDisable
    @Transactional(value = "systemTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(Long id) {
        FocusPicture cmsFocusPicture = this.getModel(id);
        focusPictureMapper.deleteById(FocusPicture.class);
        //发送消息
        sendFocusChangeMessage(cmsFocusPicture.getClientType());
    }

    @Override
    public FocusPicture getModel(Long id) {
        return focusPictureMapper.selectById(FocusPicture.class);
    }
}
