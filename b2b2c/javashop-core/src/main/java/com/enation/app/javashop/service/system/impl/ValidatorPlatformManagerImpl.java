package com.enation.app.javashop.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.mapper.system.ValidatorPlatformMapper;
import com.enation.app.javashop.model.base.CachePrefix;
import com.enation.app.javashop.model.base.vo.ConfigItem;
import com.enation.app.javashop.service.base.plugin.validator.ValidatorPlugin;
import com.enation.app.javashop.model.system.enums.ValidatorPlatformEnum;
import com.enation.app.javashop.model.system.dos.ValidatorPlatformDO;
import com.enation.app.javashop.model.system.dto.ValidatorPlatformDTO;
import com.enation.app.javashop.model.system.vo.AliyunAfsVO;
import com.enation.app.javashop.model.system.vo.ValidatorPlatformVO;
import com.enation.app.javashop.service.system.ValidatorPlatformManager;
import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 验证平台相关接口实现
 * @author duanmingyu
 * @version v1.0
 * @since v7.1.6
 * 2019-12-18
 */
@Service
public class ValidatorPlatformManagerImpl implements ValidatorPlatformManager {

    @Autowired
    private List<ValidatorPlugin> validatorPlatforms;
    @Autowired
    private Cache cache;
    @Autowired
    private ValidatorPlatformMapper validatorPlatformMapper;

    /**
     * 阿里云滑动插件ID
     */
    private static String beanId = "aliyunAfsPlugin";

    @Override
    public WebPage list(Long pageNo, Long pageSize) {
        List<ValidatorPlatformVO> resultList = this.getPlatform();
        for (ValidatorPlatformVO vo : resultList) {
            this.add(vo);
        }
        return new WebPage(pageNo, Integer.valueOf(resultList.size()).longValue(), pageSize, resultList);
    }

    @Override
    @Transactional(value = "systemTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ValidatorPlatformVO add(ValidatorPlatformVO validatorPlatformVO) {
        ValidatorPlatformDO validatorPlatformDO = new ValidatorPlatformDO(validatorPlatformVO);
        if (validatorPlatformDO.getId() == null || validatorPlatformDO.getId() == 0) {
            ValidatorPlatformDO platformDO = this.getPlatform(validatorPlatformDO.getPluginId());
            if (platformDO != null) {
                validatorPlatformVO.setId(platformDO.getId());
                return validatorPlatformVO;
            }
            validatorPlatformMapper.insert(validatorPlatformDO);
            Long id = validatorPlatformDO.getId();
            validatorPlatformVO.setId(id);
        }
        return validatorPlatformVO;
    }

    @Override
    public ValidatorPlatformDO getPlatform(String pluginId) {
        QueryWrapper<ValidatorPlatformDO> wrapper = new QueryWrapper<>();
        wrapper.eq("plugin_id",pluginId);
        return validatorPlatformMapper.selectOne(wrapper);
    }

    @Override
    @Transactional(value = "systemTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ValidatorPlatformVO edit(ValidatorPlatformVO validatorPlatformVO) {
        List<ValidatorPlatformVO> vos = this.getPlatform();
        for (ValidatorPlatformVO vo : vos) {
            this.add(vo);
        }
        ValidatorPlatformDO validatorPlatformDO = this.getPlatform(validatorPlatformVO.getPluginId());
        if (validatorPlatformDO == null) {
            throw new ResourceNotFoundException("该验证平台不存在");
        }
        //校验之前开启状态是未开启，改为开启
        if (validatorPlatformDO.getOpen().equals(0) && validatorPlatformVO.getOpen().equals(1)) {
            //修改开启状态
            this.open(validatorPlatformDO.getPluginId());
        }
        validatorPlatformVO.setId(validatorPlatformDO.getId());
        validatorPlatformMapper.updateById(new ValidatorPlatformDO(validatorPlatformVO));
        cache.remove(CachePrefix.VALIDATOR_PLATFORM.getPrefix());
        return validatorPlatformVO;
    }

    @Override
    public ValidatorPlatformVO getConfig(String pluginId) {
        List<ValidatorPlatformVO> vos = this.getPlatform();
        for (ValidatorPlatformVO vo : vos) {
            this.add(vo);
        }
        ValidatorPlatformDO validatorPlatformDO = this.getPlatform(pluginId);
        if (validatorPlatformDO == null) {
            throw new ResourceNotFoundException("该验证平台不存在");
        }
        return new ValidatorPlatformVO(validatorPlatformDO);
    }

    @Override
    @Transactional(value = "systemTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void open(String pluginId) {
        List<ValidatorPlatformVO> vos = this.getPlatform();
        for (ValidatorPlatformVO vo : vos) {
            this.add(vo);
        }

        ValidatorPlatformDO validatorPlatformDO = this.getPlatform(pluginId);
        if (validatorPlatformDO == null) {
            throw new ResourceNotFoundException("该验证平台不存在");
        }

        //重新new对象,只修改open值
        ValidatorPlatformDO validatorPlatformDo = new ValidatorPlatformDO();
        UpdateWrapper<ValidatorPlatformDO> wrapper = new UpdateWrapper<>();
        validatorPlatformDo.setOpen(0);
        validatorPlatformMapper.update(validatorPlatformDo,wrapper);
        wrapper.eq("plugin_id",pluginId);
        validatorPlatformDo.setOpen(1);
        validatorPlatformMapper.update(validatorPlatformDo,wrapper);

        // 更新缓存
        cache.remove(CachePrefix.VALIDATOR_PLATFORM.getPrefix());
    }

    @Override
    public ValidatorPlatformVO getOpen() {
        ValidatorPlatformVO validatorPlatformVO = (ValidatorPlatformVO) this.cache.get(CachePrefix.VALIDATOR_PLATFORM.getPrefix());
        if (validatorPlatformVO == null) {
            QueryWrapper<ValidatorPlatformDO> wrapper = new QueryWrapper<>();
            wrapper.eq("open",1);
            ValidatorPlatformDO validatorPlatformDO = validatorPlatformMapper.selectOne(wrapper);

            if (validatorPlatformDO == null) {
                throw new ResourceNotFoundException("未找到可用的验证平台");
            }
            validatorPlatformVO = new ValidatorPlatformVO(validatorPlatformDO);
            cache.put(CachePrefix.VALIDATOR_PLATFORM.getPrefix(), validatorPlatformVO);
        }
        return validatorPlatformVO;
    }

    @Override
    public ValidatorPlatformDTO getCurrentOpen() {

        //获取系统当前开启的验证平台信息
        ValidatorPlatformVO validatorPlatformVO = getOpen();

        ValidatorPlatformDTO platformDTO = new ValidatorPlatformDTO();
        //默认图片验证码
        platformDTO.setValidatorType(ValidatorPlatformEnum.IMAGE.value());

        //如果当前系统开启的是阿里云滑动验证
        if (beanId.equals(validatorPlatformVO.getPluginId())) {
            //验证类型为阿里云滑动验证
            platformDTO.setValidatorType(ValidatorPlatformEnum.ALIYUN.value());

            AliyunAfsVO afsVO = new AliyunAfsVO();

            List<ConfigItem> configItems = validatorPlatformVO.getConfigItems();

            for (ConfigItem configItem : configItems) {
                if ("appKey".equals(configItem.getName())) {
                    afsVO.setAppKey(configItem.getValue().toString());
                }
                if ("scene".equals(configItem.getName())) {
                    afsVO.setScene(configItem.getValue().toString());
                }
            }

            platformDTO.setAliyunAfs(afsVO);
        }

        return platformDTO;
    }

    /**
     * 获取所有的滑块验证方案
     *
     * @return 所有的滑块验证方案
     */
    private List<ValidatorPlatformVO> getPlatform() {
        List<ValidatorPlatformVO> resultList = new ArrayList<>();

        QueryWrapper<ValidatorPlatformDO> wrapper = new QueryWrapper<>();
        List<ValidatorPlatformDO> list = validatorPlatformMapper.selectList(wrapper);

        Map<String, ValidatorPlatformDO> map = new HashMap<>(16);
        for (ValidatorPlatformDO validatorPlatformDO : list) {
            map.put(validatorPlatformDO.getPluginId(), validatorPlatformDO);
        }
        for (ValidatorPlugin plugin : validatorPlatforms) {
            ValidatorPlatformDO validatorPlatformDO = map.get(plugin.getPluginId());
            ValidatorPlatformVO result = null;

            if (validatorPlatformDO != null) {
                result = new ValidatorPlatformVO(validatorPlatformDO);
            } else {
                result = ValidatorPlatformConverter.toValidatorPlatformVO(plugin);
            }
            resultList.add(result);
        }
        return resultList;
    }
}
