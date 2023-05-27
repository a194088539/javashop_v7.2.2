package com.enation.coder.service.impl;

import com.enation.coder.config.FileConfig;
import com.enation.coder.model.po.*;
import com.enation.coder.service.IAdminUserManager;
import com.enation.coder.service.IModuleManager;
import com.enation.coder.service.IProjectManager;
import com.enation.coder.service.IVersionManager;
import com.enation.coder.util.FileHelper;
import com.enation.coder.util.UrlUtil;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;

/**
 * Created by kingapex on 13/02/2018.
 * 客户业务管理实现类
 *
 * @author kingapex
 * @version 1.0
 * @since 6.4.0
 * 13/02/2018
 */
@Service
public class VersionManagerImpl implements IVersionManager {


    @Autowired
    private IDaoSupport daoSupport;

    @Autowired
    private IAdminUserManager adminUserManager;

    @Autowired
    private IProjectManager projectManager;


    @Override
    public List<Version> list(Integer projectId) {

        String sql = "select * from es_version where project_id = ?";

        return this.daoSupport.queryForList(sql, projectId);
    }

    @Override
    public Version add(Integer fromVersionId, Version version) {

        version.setAdd_time(DateUtil.getDateline());

        this.daoSupport.insert("es_version", version);

        int id = this.daoSupport.getLastId("");

        projectManager.createBranch(version.getProject_id(), fromVersionId, id);

        return version;
    }

    @Override
    public Version get(Integer versionId) {

        String sql = "select * from es_version where id = ? ";

        return this.daoSupport.queryForObject(sql, Version.class, versionId);
    }
}
