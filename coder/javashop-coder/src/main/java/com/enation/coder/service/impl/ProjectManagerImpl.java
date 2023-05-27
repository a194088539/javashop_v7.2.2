package com.enation.coder.service.impl;

import com.enation.coder.config.FileConfig;
import com.enation.coder.model.po.*;
import com.enation.coder.service.IModuleManager;
import com.enation.coder.util.FileHelper;
import com.enation.coder.util.UrlUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.enation.coder.service.IAdminUserManager;
import com.enation.coder.service.IProjectManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonUtil;

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
public class ProjectManagerImpl implements IProjectManager {


    @Autowired
    private IDaoSupport daoSupport;

    @Autowired
    private IAdminUserManager adminUserManager;

    @Autowired
    private FileConfig fileConfig;

    @Autowired
    private IModuleManager moduleManager;

    /**
     * 分页查询我的项目列表
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public Page<Project> list(int pageNo, int pageSize) {

        AdminUser user = adminUserManager.getCurrentUser();

        String sql = "select * from es_project  order by add_time desc";

        return daoSupport.queryForPage(sql, pageNo, pageSize, Project.class);
    }

    @Override
    @Transactional
    public Project add(Project project) {

        AdminUser user = adminUserManager.getCurrentUser();

        project.setUser_id(user.getUserid());
        project.setAdd_time(DateUtil.getDateline());

        this.daoSupport.insert("es_project", project);

        int project_id = this.daoSupport.getLastId("es_project");
        project.setId(project_id);

        return project;
    }

    private String toJson(Project customer) {
        return JsonUtil.ObjectToJson(customer);
    }

    @Override
    @Transactional
    public Project edit(int project_id, Project project) {

        //更改项目
        this.daoSupport.update("es_project", project, "id=" + project_id);

        return project;
    }

    @Override
    public Project get(int project_id) {

        Project project = daoSupport.queryForObject("select * from es_project  where id=?", Project.class, project_id);

        return project;
    }

    @Override
    @Transactional
    public void delete(int projectId) {

        List<Module> moduleList = moduleManager.list(projectId);
        if (moduleList != null) {
            for (Module module : moduleList) {
                //查询原模块下的表
                String sql = "select * from es_data_model where module_id = ? order by add_time desc ";
                List<DataModel> modelList = this.daoSupport.queryForList(sql, DataModel.class, module.getModule_id());
                if (modelList != null) {
                    for (DataModel model : modelList) {

                        sql = "delete from es_data_field where model_id = ?";
                        this.daoSupport.execute(sql, model.getModel_id());
                    }
                }

                String delsql = "delete from es_data_model where module_id = ?";
                this.daoSupport.execute(delsql, module.getModule_id());
            }
        }
        daoSupport.execute("delete from es_module where project_id = ?", projectId);

        daoSupport.execute("delete from es_project where id = ?", projectId);

        daoSupport.execute("delete from es_version where project_id = ?",projectId);
    }


    @Override
    public File downloadPdm(int project_id, Integer versionId) {

        try {
            Project model = this.get(project_id);
            String html = UrlUtil.getHTML(UrlUtil.getCallUrl() + "/view/template/pdm/" + project_id+"?version_id="+versionId);
            FileHelper.writeToFile(fileConfig.getFilePath() + model.getProject_name() + ".pdm", html);

            File file = new File(fileConfig.getFilePath() + model.getProject_name() + ".pdm");


            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;

    }

    @Override
    public void copy(Integer projectId) {

        //查询项目
        Project project = this.get(projectId);
        //使用名称增加一个copy字段
        String projectName = project.getProject_name() + "_copy";

        Project copyProject = new Project();
        BeanUtils.copyProperties(project, copyProject);
        copyProject.setId(null);
        copyProject.setProject_name(projectName);
        copyProject.setAdd_time(DateUtil.getDateline());
        this.daoSupport.insert("es_project", copyProject);
        int id = this.daoSupport.getLastId("es_project");

        //复制新工程，为新工程的master分支
        copyModule(projectId, id, 0, 0);

    }

    @Override
    public void createBranch(Integer projectId, Integer fromVersionId, Integer toVersionId) {


        copyModule(projectId, projectId, fromVersionId, toVersionId);
    }


    /**
     * 复制旧分支创建新分支
     *
     * @param oldProjectId
     * @param newProjectId
     * @param fromVersionId
     * @param toVersionId
     */
    private void copyModule(Integer oldProjectId, Integer newProjectId, Integer fromVersionId, Integer toVersionId) {


        //查询工程下模块
        List<Module> moduleList = moduleManager.list(oldProjectId, fromVersionId);
        if (moduleList != null) {
            for (Module module : moduleList) {
                Module moduleNew = new Module();
                BeanUtils.copyProperties(module, moduleNew);
                moduleNew.setProject_id(newProjectId);
                moduleNew.setAdd_time(DateUtil.getDateline());
                moduleNew.setModule_id(null);
                moduleNew.setVersion_id(toVersionId);
                this.daoSupport.insert("es_module", moduleNew);
                int moduleId = this.daoSupport.getLastId("es_module");
                //查询原模块下的表
                String sql = "select * from es_data_model where module_id = ? order by add_time desc ";
                List<DataModel> modelList = this.daoSupport.queryForList(sql, DataModel.class, module.getModule_id());
                if (modelList != null) {
                    for (DataModel model : modelList) {

                        DataModel modelNew = new DataModel();
                        BeanUtils.copyProperties(model, modelNew);
                        modelNew.setProject_id(newProjectId);
                        modelNew.setModule_id(moduleId);
                        modelNew.setAdd_time(DateUtil.getDateline());
                        modelNew.setModel_id(null);
                        modelNew.setVersion_id(toVersionId);
                        this.daoSupport.insert("es_data_model", modelNew);
                        int modelId = this.daoSupport.getLastId("es_data_model");
                        //查询原模型下的字段
                        sql = "select * from es_data_field where model_id = ? and status = 1 order by is_primary desc,add_time asc";

                        List<DataField> fieldList = this.daoSupport.queryForList(sql, DataField.class, model.getModel_id());

                        if (fieldList != null) {

                            for (DataField field : fieldList) {
                                DataField fieldNew = new DataField();
                                BeanUtils.copyProperties(field, fieldNew);
                                fieldNew.setModel_id(modelId);
                                fieldNew.setAdd_time(DateUtil.getDateline());
                                fieldNew.setField_id(null);
                                fieldNew.setVersion_id(toVersionId);
                                fieldNew.setProject_id(newProjectId);
                                this.daoSupport.insert("es_data_field", fieldNew);

                            }
                        }


                    }
                }


            }
        }


    }


}
