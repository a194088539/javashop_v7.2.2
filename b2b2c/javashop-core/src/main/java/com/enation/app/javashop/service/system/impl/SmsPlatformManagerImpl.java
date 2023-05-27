package com.enation.app.javashop.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.enation.app.javashop.mapper.system.SmsPlatformMapper;
import com.enation.app.javashop.model.base.CachePrefix;
import com.enation.app.javashop.service.base.plugin.sms.SmsPlatform;
import com.enation.app.javashop.service.base.plugin.sms.SmsPlatformConverter;
import com.enation.app.javashop.service.system.SmsPlatformManager;
import com.enation.app.javashop.model.errorcode.SystemErrorCode;
import com.enation.app.javashop.model.system.dos.SmsPlatformDO;
import com.enation.app.javashop.model.system.vo.SmsPlatformVO;
import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.exception.ResourceNotFoundException;
import com.enation.app.javashop.framework.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 短信网关表业务类
 *
 * @author zh
 * @version v7.0.0
 * @since v7.0.0
 * 2018-03-23 11:31:05
 */
@Service
public class SmsPlatformManagerImpl implements SmsPlatformManager {

    @Autowired
    private List<SmsPlatform> smsPlatforms;
    @Autowired
    private Cache cache;
    @Autowired
    private SmsPlatformMapper smsPlatformMapper;

    @Override
    public WebPage list(long pageNo, long pageSize) {
        List<SmsPlatformVO> resultList = this.getPlatform();
        for (SmsPlatformVO vo : resultList) {
            this.add(vo);
        }
        return new WebPage(pageNo, Integer.valueOf(resultList.size()).longValue(), pageSize, resultList);
    }

    /**
     * 添加短信网关
     *
     * @param smsPlatform 短信网关参数
     * @return 短信网关对象
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public SmsPlatformVO add(SmsPlatformVO smsPlatform) {
        SmsPlatformDO smsPlatformDO = new SmsPlatformDO(smsPlatform);
        if (smsPlatformDO.getId() == null || smsPlatformDO.getId() == 0) {
            SmsPlatformDO platformDO = this.getSmsPlateform(smsPlatformDO.getBean());
            if (platformDO != null) {
                throw new ServiceException(SystemErrorCode.E900.code(), "该存储方案已经存在");
            }
            smsPlatformMapper.insert(smsPlatformDO);
        }
        // 更新缓存
        cache.remove(CachePrefix.SPlATFORM.getPrefix());
        return smsPlatform;
    }

    @Override
    public SmsPlatformDO getModel(Long id) {
        return smsPlatformMapper.selectById(id);
    }

    @Override
    public void openPlatform(String bean) {
        List<SmsPlatformVO> vos = this.getPlatform();
        for (SmsPlatformVO vo : vos) {
            this.add(vo);
        }
        SmsPlatformDO smsPlatformDO = this.getSmsPlateform(bean);
        if (smsPlatformDO == null) {
            throw new ResourceNotFoundException("该短信方案不存在");
        }

        SmsPlatformDO smsPlatformDo = new SmsPlatformDO();
        UpdateWrapper<SmsPlatformDO> wrapper = new UpdateWrapper<>();
        smsPlatformDo.setOpen(0);
        smsPlatformMapper.update(smsPlatformDo,wrapper);
        wrapper.eq("bean",bean);
        smsPlatformDo.setOpen(1);
        smsPlatformMapper.update(smsPlatformDo,wrapper);

        // 更新缓存
        cache.remove(CachePrefix.SPlATFORM.getPrefix());

    }


    /**
     * 获取所有的短信方案
     *
     * @return 所有的短信方案
     */
    private List<SmsPlatformVO> getPlatform() {
        List<SmsPlatformVO> resultList = new ArrayList<>();

        QueryWrapper<SmsPlatformDO> wrapper = new QueryWrapper<>();
        List<SmsPlatformDO> list = smsPlatformMapper.selectList(wrapper);

        Map<String, SmsPlatformDO> map = new HashMap<>(16);

        for (SmsPlatformDO smsPlatformDO : list) {
            map.put(smsPlatformDO.getBean(), smsPlatformDO);
        }
        for (SmsPlatform plugin : smsPlatforms) {
            SmsPlatformDO smsPlatformDO = map.get(plugin.getPluginId());
            SmsPlatformVO result = null;

            if (smsPlatformDO != null) {
                result = new SmsPlatformVO(smsPlatformDO);
            } else {
                result = SmsPlatformConverter.toSmsPlatformVO(plugin);
            }

            resultList.add(result);
        }
        return resultList;
    }

    @Override
    public SmsPlatformVO getConfig(String bean) {
        List<SmsPlatformVO> vos = this.getPlatform();
        for (SmsPlatformVO vo : vos) {
            this.add(vo);
        }
        SmsPlatformDO smsPlatformDO = this.getSmsPlateform(bean);
        if (smsPlatformDO == null) {
            throw new ResourceNotFoundException("该短信网关方案不存在");
        }
        return new SmsPlatformVO(smsPlatformDO);
    }

    @Override
    public SmsPlatformVO getOpen() {
        SmsPlatformVO smsPlatformVO = (SmsPlatformVO) this.cache.get(CachePrefix.SPlATFORM.getPrefix());
        if (smsPlatformVO == null) {

            QueryWrapper<SmsPlatformDO> wrapper = new QueryWrapper<>();
            wrapper.eq("open",1);
            SmsPlatformDO smsPlatformDO = smsPlatformMapper.selectOne(wrapper);

            if (smsPlatformDO == null) {
                throw new ResourceNotFoundException("未找到可用的短信网关");
            }
            smsPlatformVO = new SmsPlatformVO();
            smsPlatformVO.setConfig(smsPlatformDO.getConfig());
            smsPlatformVO.setBean(smsPlatformDO.getBean());
            cache.put(CachePrefix.SPlATFORM.getPrefix(), smsPlatformVO);
        }
        return smsPlatformVO;
    }


    @Override
    public SmsPlatformDO getSmsPlateform(String bean) {
        QueryWrapper<SmsPlatformDO> wrapper = new QueryWrapper<>();
        wrapper.eq("bean",bean);
        return smsPlatformMapper.selectOne(wrapper);
    }

    @Override
    public SmsPlatformVO edit(SmsPlatformVO smsPlatform) {
        List<SmsPlatformVO> vos = this.getPlatform();
        for (SmsPlatformVO vo : vos) {
            this.add(vo);
        }
        SmsPlatformDO up = this.getSmsPlateform(smsPlatform.getBean());
        if (up == null) {
            throw new ResourceNotFoundException("该短信方案不存在");
        }
        smsPlatform.setId(up.getId());
        smsPlatformMapper.updateById(new SmsPlatformDO(smsPlatform));
        //this.systemDaoSupport.update(new SmsPlatformDO(smsPlatform), up.getId());
        return smsPlatform;
    }
}
