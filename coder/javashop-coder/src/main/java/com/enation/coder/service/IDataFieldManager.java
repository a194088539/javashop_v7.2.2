package com.enation.coder.service;

import java.util.List;

import com.enation.coder.model.po.DataField;
import com.enation.framework.database.Page;

/**
 * 模型字段业务类
 * @author fk
 * @version v1.0
 * @since v7.0
 * 2018年3月9日 上午11:36:47
 */
public interface IDataFieldManager
{

    /**
     * 分页查询某项目的模型列表
     * @param pageNo
     * @param pageSize
     * @param verdionId
     * @return
     */
    Page<DataField> list(int pageNo, int pageSize, int model_id, Integer verdionId);


    /**
     * 添加一个模型
     * @param dataField
     * @return
     */
    DataField add(DataField dataField);

    /**
     * 修改一个模型
     * @param dataField
     * @return
     */
    DataField edit(DataField dataField);

    /**
     * 获取一个模型实例
     * @param field_id
     * @return
     */
    DataField get(int field_id);

    /**
     * 删除一个模型
     * @param field_id
     */
    void delete(int field_id);


    /**
     * 查询某个模型的字段
     * @param modelId
     * @return
     */
	List<DataField> getFieldByModel(int modelId);

    /**
     * 查询某个模型中的字段列表
     * @param model_id
     * @return
     */
    List<DataField> listByModel(Integer model_id);
}
