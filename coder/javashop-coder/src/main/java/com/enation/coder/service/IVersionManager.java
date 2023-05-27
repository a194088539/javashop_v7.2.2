package com.enation.coder.service;

import com.enation.coder.model.po.Version;

import java.util.List;

/**
 * 项目业务类
 * @author fk
 * @version v1.0
 * @since v7.0
 * 2018年3月9日 上午9:17:18
 */
public interface IVersionManager{

    /**
     * 查询某个项目的版本
     * @param projectId
     * @return
     */
    List<Version> list(Integer projectId);


    /**
     * 新建某工程的分支
     * @param version
     * @return
     */
    Version add(Integer fromVersionId,Version version);

    /**
     * 获取一个版本信息
     * @param versionId
     * @return
     */
    Version get(Integer versionId);
}
