package com.enation.app.javashop.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.mapper.system.ExpressPlatformMapper;
import com.enation.app.javashop.model.base.CachePrefix;
import com.enation.app.javashop.model.base.vo.ConfigItem;
import com.enation.app.javashop.service.base.plugin.express.ExpressPlatform;
import com.enation.app.javashop.client.system.LogiCompanyClient;
import com.enation.app.javashop.model.system.dos.LogisticsCompanyDO;
import com.enation.app.javashop.model.system.dos.ExpressPlatformDO;
import com.enation.app.javashop.model.system.vo.ExpressDetailVO;
import com.enation.app.javashop.model.system.vo.ExpressPlatformVO;
import com.enation.app.javashop.service.system.factory.ExpressFactory;
import com.enation.app.javashop.service.system.ExpressPlatformManager;
import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.exception.ResourceNotFoundException;
import com.enation.app.javashop.framework.util.StringUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 快递平台业务类
 *
 * @author zh
 * @version v7.0.0
 * @since v7.0.0
 * 2018-07-11 14:42:50
 */
@Service
public class ExpressPlatformManagerImpl implements ExpressPlatformManager {

    @Autowired
    private List<ExpressPlatform> expressPlatforms;

    @Autowired
    private LogiCompanyClient logiCompanyClient;

    @Autowired
    private Cache cache;

    @Autowired
    private ExpressFactory expressFactory;
    @Autowired
    private ExpressPlatformMapper expressPlatformMapper;

    @Override
    public WebPage list(long page, long pageSize) {
        List<ExpressPlatformVO> resultList = this.getPlatform();
        for (ExpressPlatformVO vo : resultList) {
            this.add(vo);
        }
        return new WebPage(page, Integer.valueOf(resultList.size()).longValue(), pageSize, resultList);
    }

    @Override
    @Transactional(value = "systemTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ExpressPlatformVO add(ExpressPlatformVO expressPlatformVO) {
        ExpressPlatformDO expressPlatformDO = new ExpressPlatformDO(expressPlatformVO);
        if (expressPlatformDO.getId() == null || expressPlatformDO.getId() == 0) {
            ExpressPlatformDO platformDO = this.getExpressPlatform(expressPlatformDO.getBean());
            if (platformDO != null) {
                expressPlatformVO.setId(platformDO.getId());
                return expressPlatformVO;
            }
            expressPlatformMapper.insert(expressPlatformDO);
            Long id = expressPlatformDO.getId();
            expressPlatformVO.setId(id);
        }
        return expressPlatformVO;
    }

    @Override
    @Transactional(value = "systemTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ExpressPlatformVO edit(ExpressPlatformVO expressPlatformVO) {
        List<ExpressPlatformVO> vos = this.getPlatform();
        for (ExpressPlatformVO vo : vos) {
            this.add(vo);
        }
        ExpressPlatformDO expressPlatformDO = this.getExpressPlatform(expressPlatformVO.getBean());
        if (expressPlatformDO == null) {
            throw new ResourceNotFoundException("该快递方案不存在");
        }
        //校验之前开启状态是未开启，改为开启
        if (expressPlatformDO.getOpen().equals(0) && expressPlatformVO.getOpen().equals(1)) {
            //修改开启状态
            this.open(expressPlatformDO.getBean());
        }
        expressPlatformVO.setId(expressPlatformDO.getId());
        expressPlatformMapper.updateById(new ExpressPlatformDO(expressPlatformVO));
        cache.remove(CachePrefix.EXPRESS.getPrefix());
        return expressPlatformVO;
    }

    @Override
    public ExpressPlatformDO getExpressPlatform(String bean) {

        QueryWrapper<ExpressPlatformDO> wrapper = new QueryWrapper<>();
        wrapper.eq("bean", bean);
        return expressPlatformMapper.selectOne(wrapper);

    }


    /**
     * 获取所有的快递查询方案
     *
     * @return 所有的快递方案
     */
    private List<ExpressPlatformVO> getPlatform() {
        List<ExpressPlatformVO> resultList = new ArrayList<>();

        QueryWrapper<ExpressPlatformDO> wrapper = new QueryWrapper<>();
        List<ExpressPlatformDO> list = expressPlatformMapper.selectList(wrapper);

        Map<String, ExpressPlatformDO> map = new HashMap<>(16);
        for (ExpressPlatformDO expressPlatformDO : list) {
            map.put(expressPlatformDO.getBean(), expressPlatformDO);
        }
        for (ExpressPlatform plugin : expressPlatforms) {
            ExpressPlatformDO expressPlatformDO = map.get(plugin.getPluginId());
            ExpressPlatformVO result = null;

            if (expressPlatformDO != null) {
                result = new ExpressPlatformVO(expressPlatformDO);
            } else {
                result =  ExpressPlatformConverter.toExpressPlatformVO(plugin);
            }
            resultList.add(result);
        }
        return resultList;
    }


    @Override
    public ExpressPlatformVO getExoressConfig(String bean) {
        List<ExpressPlatformVO> vos = this.getPlatform();
        for (ExpressPlatformVO vo : vos) {
            this.add(vo);
        }
        ExpressPlatformDO expressPlatformDO = this.getExpressPlatform(bean);
        if (expressPlatformDO == null) {
            throw new ResourceNotFoundException("该快递平台不存在");
        }
        return new ExpressPlatformVO(expressPlatformDO);
    }

    @Override
    public void open(String bean) {
        List<ExpressPlatformVO> vos = this.getPlatform();
        for (ExpressPlatformVO vo : vos) {
            this.add(vo);
        }
        ExpressPlatformDO expressPlatformDO = this.getExpressPlatform(bean);
        if (expressPlatformDO == null) {
            throw new ResourceNotFoundException("该快递平台不存在");
        }

        ExpressPlatformDO expressPlatformDo = new ExpressPlatformDO();
        UpdateWrapper<ExpressPlatformDO> wrapper = new UpdateWrapper<>();
        expressPlatformDo.setOpen(0);
        expressPlatformMapper.update(expressPlatformDo,wrapper);
        wrapper.eq("bean",bean);
        expressPlatformDo.setOpen(1);
        expressPlatformMapper.update(expressPlatformDo,wrapper);

        // 更新缓存
        cache.remove(CachePrefix.EXPRESS.getPrefix());
    }

    @Override
    public ExpressDetailVO getExpressDetail(Long id, String nu) {
        //获取物流公司
        LogisticsCompanyDO logisticsCompanyDO = logiCompanyClient.getModel(id);
        if (logisticsCompanyDO == null || StringUtil.isEmpty(logisticsCompanyDO.getCode())) {
            logisticsCompanyDO.setCode("shunfeng");
        }

        //得到开启的快递平台方案
        ExpressPlatform expressPlatform = expressFactory.getExpressPlatform();
        //调用查询接口返回查询到的物流信息
        ExpressDetailVO expressDetailVO = expressPlatform.getExpressDetail(logisticsCompanyDO.getCode(), nu, this.getConfig());
        if(expressDetailVO == null ){
            expressDetailVO = new ExpressDetailVO();
            expressDetailVO.setCourierNum(nu);
            expressDetailVO.setName(logisticsCompanyDO.getName());
        }
        return expressDetailVO;
    }

    @Override
    public ExpressPlatformVO getOpen() {
        //从缓存中获取开启的快递平台
        ExpressPlatformVO expressPlatformVO = (ExpressPlatformVO) cache.get(CachePrefix.EXPRESS.getPrefix());
        //如果没有找到则从数据库查询，将查询到的开启的快递平台放入缓存
        if (expressPlatformVO == null) {

            QueryWrapper<ExpressPlatformDO> wrapper = new QueryWrapper<>();
            wrapper.eq("open", 1);
            ExpressPlatformDO expressPlatformDO =  expressPlatformMapper.selectOne(wrapper);

            if (expressPlatformDO == null) {
                throw new ResourceNotFoundException("未找到开启的快递平台");
            }
            expressPlatformVO = new ExpressPlatformVO();
            expressPlatformVO.setConfig(expressPlatformDO.getConfig());
            expressPlatformVO.setBean(expressPlatformDO.getBean());
            cache.put(CachePrefix.EXPRESS.getPrefix(), expressPlatformVO);
        }
        return expressPlatformVO;
    }

    /**
     * 获取开启的快递平台方案
     *
     * @return
     */
    private Map getConfig() {
        ExpressPlatformVO expressPlatformVO = (ExpressPlatformVO) cache.get(CachePrefix.EXPRESS.getPrefix());
        if (StringUtil.isEmpty(expressPlatformVO.getConfig())) {
            return new HashMap<>(16);
        }
        Gson gson = new Gson();
        List<ConfigItem> list = gson.fromJson(expressPlatformVO.getConfig(), new TypeToken<List<ConfigItem>>() {}.getType());
        Map<String, String> result = new HashMap<>(16);
        if (list != null) {
            for (ConfigItem item : list) {
                result.put(item.getName(), StringUtil.toString(item.getValue()));
            }
        }
        return result;
    }

}
