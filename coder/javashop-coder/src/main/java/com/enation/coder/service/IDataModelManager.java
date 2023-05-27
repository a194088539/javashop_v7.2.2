package com.enation.coder.service;

import java.io.File;
import java.util.List;

import com.enation.coder.model.po.DataModel;
import com.enation.coder.model.vo.GenerateParam;
import com.enation.framework.database.Page;

/**
 * 模型业务类
 * @author fk
 * @version v1.0
 * @since v7.0
 * 2018年3月9日 上午11:36:47
 */
public interface IDataModelManager
{

    /**
     * 分页查询某项目的模型列表
     * @param pageNo
     * @param pageSize
     * @param versionId
     * @return
     */
    public Page<DataModel> list(int pageNo, int pageSize, int project_id, int versionId);


    /**
     * 添加一个模型
     * @param dataModel
     * @return
     */
    public DataModel add(DataModel dataModel);

	/**
	 * 修改一个模型
	 * @param dataModel
	 * @return
	 */
	public DataModel edit(DataModel dataModel);

    /**
     * 获取一个模型实例
     * @param dataModel_id
     * @return
     */
    public DataModel get(int dataModel_id);

    /**
     * 删除一个模型
     * @param dataModel_id
     */
    public void delete(int dataModel_id);


    /**
     * 生成代码
     * @param params
     */
	public void generate(GenerateParam params);


	/**
	 * 下载文件
	 * @param model_id
	 * @return
	 */
	public File download(int model_id);


	/**
	 * 获取生成该模型的sql
	 * @param model_id
	 * @return
	 */
	public String getSql(int model_id);

	/**
	 * 查询某个模块下的模型
	 * @param module_id
	 * @return
	 */
    List<DataModel> listModelsByModule(Integer module_id);
}
