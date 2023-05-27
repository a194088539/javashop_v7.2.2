package com.enation.app.javashop.service.pagedata.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.system.SiteNavigationMapper;
import com.enation.app.javashop.model.base.message.CmsManageMsg;
import com.enation.app.javashop.model.base.rabbitmq.AmqpExchange;
import com.enation.app.javashop.model.base.CachePrefix;
import com.enation.app.javashop.model.pagedata.enums.ClientType;
import com.enation.app.javashop.model.errorcode.SystemErrorCode;
import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.util.StringUtil;
import com.enation.app.javashop.service.pagedata.SiteNavigationManager;
import com.enation.app.javashop.framework.rabbitmq.MessageSender;
import com.enation.app.javashop.framework.rabbitmq.MqMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.javashop.model.pagedata.SiteNavigation;

import java.util.List;

/**
 * 导航栏业务类
 *
 * @author fk
 * @version v1.0
 * @since v7.0.0
 * 2018-06-12 17:07:22
 */
@Service
public class SiteNavigationManagerImpl implements SiteNavigationManager {

    @Autowired
    private Cache cache;
    @Autowired
    private MessageSender messageSender;
    @Autowired
    private SiteNavigationMapper siteNavigationMapper;

    @Override
    public WebPage list(long page, long pageSize, String clientType) {

        QueryWrapper<SiteNavigation> wrapper = new QueryWrapper<>();
        wrapper.eq("client_type",clientType);
        wrapper.orderByDesc("sort");
        IPage<SiteNavigation> iPage = siteNavigationMapper.selectPage(new Page<>(page,pageSize), wrapper);
        return PageConvert.convert(iPage);
    }

    @Override
    @Transactional(value = "systemTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public SiteNavigation add(SiteNavigation siteNavigation) {

        //移动端图片地址必填
        if(ClientType.MOBILE.name().equals(siteNavigation.getClientType())){
            if(StringUtil.isEmpty(siteNavigation.getImage())){
                throw new ServiceException(SystemErrorCode.E953.code(),"移动端导航，图片必传");
            }
        }
        //导航名称长度不能超过6
        if(siteNavigation.getNavigationName().length()>6){
            throw new ServiceException(SystemErrorCode.E953.code(),"导航栏菜单名称已经超出最大限制");
        }

        /**查询数据库sort最大值方便给新添加的数据赋值*/
        Integer sort = siteNavigationMapper.queryMaxSort();
        siteNavigation.setSort(sort+1);

        siteNavigationMapper.insert(siteNavigation);
        Long id = siteNavigation.getNavigationId();

        siteNavigation.setNavigationId(id);

        this.avigationChange(siteNavigation);

        return siteNavigation;
    }

    @Override
    @Transactional(value = "systemTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public SiteNavigation edit(SiteNavigation siteNavigation, Long id) {

        SiteNavigation siteNav = this.getModel(id);
        if(siteNav == null){
            throw new ServiceException(SystemErrorCode.E953.code(),"导航栏不存在，请正确操作");
        }

        //移动端图片地址必填
        if(ClientType.MOBILE.name().equals(siteNavigation.getClientType())){
            if(StringUtil.isEmpty(siteNavigation.getImage())){
                throw new ServiceException(SystemErrorCode.E953.code(),"移动端导航，图片必传");
            }
        }
        //导航名称长度不能超过6
        if(siteNavigation.getNavigationName().length()>6){
            throw new ServiceException(SystemErrorCode.E953.code(),"导航栏菜单名称已经超出最大限制");
        }

        siteNavigation.setSort(siteNav.getSort());

        this.avigationChange(siteNav);

        siteNavigationMapper.updateById(siteNavigation);

        return siteNavigation;
    }

    @Override
    @Transactional(value = "systemTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(Long id) {

        SiteNavigation siteNav = this.getModel(id);
        if(siteNav == null){
            throw new ServiceException(SystemErrorCode.E953.code(),"导航栏不存在，请正确操作");
        }

        this.avigationChange(siteNav);

        siteNavigationMapper.deleteById(id);
    }

    @Override
    public SiteNavigation getModel(Long id) {
        return siteNavigationMapper.selectById(id);
    }

    @Override
    public SiteNavigation updateSort(Long id, String sort) {

        SiteNavigation siteNav = this.getModel(id);
        if(siteNav == null){
            throw new ServiceException(SystemErrorCode.E953.code(),"导航栏不存在，请正确操作");
        }

        Integer menuSort = siteNav.getSort();
        QueryWrapper<SiteNavigation> wrapper = new QueryWrapper<>();
        /**判断是否操作是下移或者上移 up上移 否则 下移*/
        if("up".equals(sort)) {
            wrapper.lt("sort",siteNav.getSort());
            wrapper.eq("client_type",siteNav.getClientType());
            wrapper.orderByAsc("sort");
            wrapper.last("limit 1");
        }else {
            wrapper.gt("sort",siteNav.getSort());
            wrapper.eq("client_type",siteNav.getClientType());
            wrapper.orderByDesc("sort");
            wrapper.last("limit 1");
        }
        /** 当前记录的上或者下一条记录 */
        SiteNavigation operationSiteMenu = siteNavigationMapper.selectOne(wrapper);
        /** 如果为null 则为最顶级或者最下级*/
        if(operationSiteMenu != null) {
            Integer operMenuSort = operationSiteMenu.getSort();
            /** 改变当前记录的排序 */
            siteNav.setSort(operMenuSort);
            siteNavigationMapper.updateById(siteNav);
        }

        this.avigationChange(siteNav);

        return siteNav;
    }

    @Override
    public List<SiteNavigation> listByClientType(String clientType) {

        List<SiteNavigation> list  = (List<SiteNavigation>)cache.get(CachePrefix.SITE_NAVIGATION.getPrefix()+clientType);

        if(list == null || list.isEmpty()){

            QueryWrapper<SiteNavigation> wrapper = new QueryWrapper<>();
            wrapper.eq("client_type", clientType).orderByDesc("sort");
            list = siteNavigationMapper.selectList(wrapper);

            cache.put(CachePrefix.SITE_NAVIGATION.getPrefix()+clientType,list);
        }

        return list;
    }

    /**
     * 导航栏变化清除缓存，发送mq消息
     * @param siteNav
     */
    private void avigationChange(SiteNavigation siteNav){

        this.cache.remove(CachePrefix.SITE_NAVIGATION.getPrefix()+siteNav.getClientType());

        CmsManageMsg cmsManageMsg = new CmsManageMsg();

        if (ClientType.PC.name().equals(siteNav.getClientType())) {
            this.messageSender.send(new MqMessage(AmqpExchange.PC_INDEX_CHANGE, AmqpExchange.PC_INDEX_CHANGE + "_ROUTING", cmsManageMsg));
        } else {
            this.messageSender.send(new MqMessage(AmqpExchange.MOBILE_INDEX_CHANGE, AmqpExchange.MOBILE_INDEX_CHANGE + "_ROUTING", cmsManageMsg));
        }
    }


}
