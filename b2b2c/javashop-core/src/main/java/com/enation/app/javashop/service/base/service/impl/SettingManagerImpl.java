package com.enation.app.javashop.service.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.enation.app.javashop.framework.util.StringUtil;
import com.enation.app.javashop.mapper.system.SettingsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.javashop.model.base.CachePrefix;
import com.enation.app.javashop.model.base.SettingGroup;
import com.enation.app.javashop.model.base.dos.SettingsDO;
import com.enation.app.javashop.service.base.service.SettingManager;
import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.util.JsonUtil;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * 系统设置接口实现
 *
 * @author zh
 * @version v7.0
 * @since v7.0
 * 2018年3月27日 下午4:50:15
 */
@Service
public class SettingManagerImpl implements SettingManager {

    @Autowired
    private Cache cache;
    @Autowired
    private SettingsMapper settingsMapper;

    @Override
    @Transactional(value = "systemTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void save(SettingGroup group, Object settings) {

        QueryWrapper<SettingsDO> wrapperr = new QueryWrapper<>();
        wrapperr.select("cfg_value");
        wrapperr.eq("cfg_group", group.name());
        SettingsDO settingsDO =  settingsMapper.selectOne(wrapperr);

        //将要保存的对象 转换成json
        String setting = JsonUtil.objectToJson(settings);

        if (settingsDO == null) {
            settingsDO.setCfgValue(setting);
            settingsDO.setCfgGroup(group.name());
            settingsMapper.insert(settingsDO);
        } else {
            UpdateWrapper<SettingsDO> wrapper = new UpdateWrapper<>();
            SettingsDO settingsDo = new SettingsDO();
            wrapper.eq("cfg_group ",group.name());
            settingsDo.setCfgValue(setting);
            settingsMapper.update(settingsDo,wrapper);
        }
        //清除缓存
        cache.remove(CachePrefix.SETTING.getPrefix() + group.name());
    }


    @Override
    public String get(SettingGroup group) {
        //从缓存中获取参数配置
        String setting = StringUtil.toString(cache.get(CachePrefix.SETTING.getPrefix() + group.name()), false);
        //如果没有获取到从数据库获取
        if (StringUtil.isEmpty(setting)) {

            QueryWrapper<SettingsDO> wrapper = new QueryWrapper<>();
            wrapper.eq("cfg_group",group.name());
            SettingsDO settingsDO = settingsMapper.selectOne(wrapper);

            if (settingsDO == null) {
                return null;
            }
            setting = settingsDO.getCfgValue();
            this.cache.put(CachePrefix.SETTING.getPrefix() + group.name(), setting);
        }
        return setting;
    }
}
