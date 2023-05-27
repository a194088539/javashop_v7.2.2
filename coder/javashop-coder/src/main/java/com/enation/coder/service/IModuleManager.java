package com.enation.coder.service;

import java.io.File;
import java.util.List;

import com.enation.coder.model.po.Module;
import com.enation.framework.database.Page;

/**
 * 模型业务类
 *
 * @author fk
 * @version v1.0
 * @since v7.0
 * 2018年3月9日 上午11:36:47
 */
public interface IModuleManager {

    /**
     * 查询模块列表
     *
     * @param pageNo
     * @param pageSize
     * @param projectId
     * @return
     */
    Page<Module> list(int pageNo, int pageSize, int projectId, Integer versionId);

    /**
     * 查询所有
     *
     * @return
     */
    List<Module> list(int projectId, Integer versionId);


    /**
     * 查询所有
     *
     * @return
     */
    List<Module> list(int projectId);

    /**
     * 添加
     *
     * @param module
     * @return
     */
    Module add(Module module);

    /**
     * 修改
     *
     * @param module
     * @return
     */
    public Module edit(Module module);

    /**
     * 获取
     *
     * @param moduleId
     * @return
     */
    public Module get(int moduleId);

    /**
     * 删除
     *
     * @param moduleId
     */
    public void delete(int moduleId);

}
