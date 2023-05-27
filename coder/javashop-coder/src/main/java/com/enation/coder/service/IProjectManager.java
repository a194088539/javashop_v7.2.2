package com.enation.coder.service;

import com.enation.coder.model.po.Project;
import com.enation.framework.database.Page;

import java.io.File;

/**
 * 项目业务类
 * @author fk
 * @version v1.0
 * @since v7.0
 * 2018年3月9日 上午9:17:18
 */
public interface IProjectManager{

    /**
     * 分页查询项目列表
     * @param pageNo
     * @param pageSize
     * @return
     */
    Page<Project> list(int pageNo, int pageSize);


    /**
     * 添加一个项目
     * @param project
     * @return
     */
    Project add(Project project);

    /**
     * 修改一个项目
     * @param project_id
     * @param project
     * @return
     */
    Project edit(int project_id, Project project);

    /**
     * 获取一个项目实例
     * @param project_id
     * @return
     */
    Project get(int project_id);

    /**
     * 删除一个项目
     * @param project_id
     */
    void delete(int project_id);

    /**
     * 下载一个pdm
     * @param project_id
     * @param versionId
     */
    File downloadPdm(int project_id, Integer versionId);

    /**
     * 将工程下的模块包括模块下的表结构复制一份，用于每次版本的升级
     * @param projectId
     */
    void copy(Integer projectId);


    /**
     * 将工程下的模块包括模块下的表结构复制一份，用于每次版本的升级
     * @param projectId
     * @param fromVersionId
     * @param toVersionId
     */
    void createBranch(Integer projectId,Integer fromVersionId,Integer toVersionId);

}
