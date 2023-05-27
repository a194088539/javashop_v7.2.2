package com.enation.app.javashop.service.statistics.impl;

import com.enation.app.javashop.mapper.statistics.SyncopateTableMapper;
import com.enation.app.javashop.service.statistics.SyncopateTableManager;
import com.enation.app.javashop.service.statistics.util.SyncopateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * SyncopateTableManagerImpl
 *
 * @author Chopper
 * @version v1.0
 * @since v7.0
 * 2018-08-14 上午9:59
 */
@Service
public class SyncopateTableManagerImpl implements SyncopateTableManager {

    @Autowired
    private SyncopateTableMapper syncopateTableMapper;

    /**
     * 每日填充数据
     */
    @Override
    public void everyDay() {
        SyncopateUtil.createCurrentTable(syncopateTableMapper);
    }

}