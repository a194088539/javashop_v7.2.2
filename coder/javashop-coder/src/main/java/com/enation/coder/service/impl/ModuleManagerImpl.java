package com.enation.coder.service.impl;

import java.util.List;

import com.enation.coder.model.po.Project;
import com.enation.coder.service.IProjectManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.coder.model.po.DataModel;
import com.enation.coder.model.po.Module;
import com.enation.coder.service.IModuleManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;

/**
 * 模块
 *
 * @author fk
 * @version v1.0
 * @since v7.0
 * 2018年3月21日 下午2:12:42
 */
@Service
public class ModuleManagerImpl implements IModuleManager {

    @Autowired
    private IDaoSupport daoSupport;

    @Autowired
    private IProjectManager projectManager;

    @Override
    public Page<Module> list(int pageNo, int pageSize, int projectId, Integer versionId) {

        String sql = "select * from es_module where project_id = ? and version_id = ? order by add_time desc";

        return this.daoSupport.queryForPage(sql, pageNo, pageSize, projectId, versionId);
    }

    @Override
    public List<Module> list(int projectId, Integer versionId) {

        String sql = "select * from es_module where project_id = ? and version_id = ? order by add_time desc";

        return this.daoSupport.queryForList(sql, Module.class, projectId, versionId);
    }

    @Override
    public List<Module> list(int projectId) {

        String sql = "select * from es_module where project_id = ?  order by add_time asc";

        return this.daoSupport.queryForList(sql, Module.class, projectId);
    }

    @Override
    public Module add(Module module) {

        module.setAdd_time(DateUtil.getDateline());
        this.daoSupport.insert("es_module", module);

        return module;
    }

    @Override
    public Module edit(Module module) {

        Module dbModule = this.get(module.getModule_id());
        if (!dbModule.getModule_name().equals(module.getModule_name())) {
            //修改关联的子模型
            String sql = "update es_data_model set module_name = ? where module_id = ?";
            this.daoSupport.execute(sql, module.getModule_name(), module.getModule_id());
        }

        this.daoSupport.update("es_module", module, "module_id=" + module.getModule_id());

        return module;
    }

    @Override
    public Module get(int moduleId) {
        String sql = "select * from es_module where module_id = ? ";

        return this.daoSupport.queryForObject(sql, Module.class, moduleId);
    }

    @Override
    public void delete(int moduleId) {

        String sql = "delete from es_module where module_id = ?";
        this.daoSupport.execute(sql, moduleId);

    }


}
