package com.enation.app.javashop.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.system.SmtpMapper;
import com.enation.app.javashop.model.base.CachePrefix;
import com.enation.app.javashop.model.base.vo.EmailVO;
import com.enation.app.javashop.service.base.service.EmailManager;
import com.enation.app.javashop.model.system.dos.SmtpDO;
import com.enation.app.javashop.service.system.SmtpManager;
import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.exception.ResourceNotFoundException;
import com.enation.app.javashop.framework.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


/**
 * 邮件业务类
 *
 * @author zh
 * @version v7.0.0
 * @since v7.0.0
 * 2018-03-25 16:16:53
 */
@Service
public class SmtpManagerImpl implements SmtpManager {

    @Autowired
    private EmailManager emailManager;
    @Autowired
    private Cache cache;
    @Autowired
    private SmtpMapper smtpMapper;

    @Override
    public WebPage list(long page, long pageSize) {

        QueryWrapper<SmtpDO> wrapper = new QueryWrapper<>();
        IPage<SmtpDO> iPage = smtpMapper.selectPage(new Page<>(page,pageSize), wrapper);
        return PageConvert.convert(iPage);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public SmtpDO edit(SmtpDO smtp, Long id) {
        SmtpDO smtpDO = this.getModel(id);
        if (smtpDO != null) {
            cache.remove(CachePrefix.SMTP.getPrefix());
            smtpMapper.updateById(smtp);
            return smtp;
        }
        throw new ResourceNotFoundException("该smtp未找到！");
    }

    @Override
    public SmtpDO getModel(Long id) {
        SmtpDO smtp = smtpMapper.selectById(id);
        if (smtp == null) {
            throw new ResourceNotFoundException("该smtp未找到！");
        }
        return smtp;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public SmtpDO add(SmtpDO smtp) {
        if (smtp != null && smtp.getOpenSsl() > 1) {
            smtp.setOpenSsl(1);
        }
        smtpMapper.insert(smtp);
        Long id = smtp.getId();
        smtp.setId(id);
        return smtp;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(Long id) {
        SmtpDO smtp = this.getModel(id);
        if (smtp == null) {
            throw new ResourceNotFoundException("该smtp未找到！");

        }
        cache.remove(CachePrefix.SMTP.getPrefix());
        smtpMapper.deleteById(id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void send(String send, SmtpDO smtp) {
        EmailVO emailVO = new EmailVO();
        emailVO.setTitle("测试邮件");
        emailVO.setEmail(send);
        emailVO.setType("测试邮件");
        emailVO.setContent("测试邮件发送");

        if (smtp.getOpenSsl() == 1 || "smtp.qq.com".equals(smtp.getHost())) {
            emailManager.sendMailByTransport(smtp, emailVO);
        } else {
            emailManager.sendMailByMailSender(smtp, emailVO);
        }
    }

    @Override
    public SmtpDO getCurrentSmtp() {
        List<SmtpDO> smtpList = (List<SmtpDO>) cache.get(CachePrefix.SMTP.getPrefix());
        if (smtpList == null || smtpList.size() < 0) {

            QueryWrapper<SmtpDO> wrapper = new QueryWrapper<>();
            smtpList = smtpMapper.selectList(wrapper);
            cache.put(CachePrefix.SMTP.getPrefix(), smtpList);
        }
        SmtpDO currentSmtp = null;
        for (SmtpDO smtp : smtpList) {
            if (checkCount(smtp)) {
                currentSmtp = smtp;
                break;
            }
        }
        if (currentSmtp == null) {
            throw new ResourceNotFoundException("未找到可用smtp，都已达到最大发信数 ");
        }
        return currentSmtp;

    }

    /**
     * 检测smtp服务器是否可以用
     *
     * @param smtp
     * @return 检查是否通过
     */
    private boolean checkCount(SmtpDO smtp) {
        //最后一次发送时间
        long lastSendTime = smtp.getLastSendTime();
        //已经不是今天
        if (!DateUtil.toString(new Date(lastSendTime * 1000), "yyyy-MM-dd").equals(DateUtil.toString(new Date(), "yyyy-MM-dd"))) {
            smtp.setSendCount(0);
        }
        return smtp.getSendCount() < smtp.getMaxCount();
    }
}
