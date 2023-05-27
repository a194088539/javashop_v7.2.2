package com.enation.coder.service.impl;

import java.util.List;

import com.enation.coder.model.po.DataModel;
import com.enation.coder.service.IDataModelManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.coder.model.po.DataField;
import com.enation.coder.service.IDataFieldManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;

/**
 * 模型字段业务类
 * @author fk
 * @version v1.0
 * @since v7.0
 * 2018年3月9日 下午2:17:09
 */
@Service
public class DataFieldManager implements IDataFieldManager{

	@Autowired
	private IDaoSupport daoSupport;

	@Autowired
	private IDataModelManager dataModelManager;
	
	@Override
	public Page<DataField> list(int pageNo, int pageSize, int modelId, Integer versionId) {

		//查询表
		DataModel dataModel = dataModelManager.get(modelId);

		String sql = "select * from es_data_field where status = 1" +
				" and project_id = ? and version_id = ? and table_name = ? order by is_primary desc,add_time asc";
		
		return this.daoSupport.queryForPage(sql, pageNo, pageSize,DataField.class,
				dataModel.getProject_id(),versionId,dataModel.getTable_name());
	}

	@Override
	public DataField add(DataField dataField) {

		DataModel dataModel = dataModelManager.get(dataField.getModel_id());

		dataField.setProject_id(dataModel.getProject_id());
		dataField.setTable_name(dataModel.getTable_name());

		dataField.setAdd_time(DateUtil.getDateline());
		dataField.setStatus(1);
		this.daoSupport.insert("es_data_field", dataField);
		
		int fieldId = this.daoSupport.getLastId("es_data_field");
		
		dataField.setField_id(fieldId);
		
		return dataField;
	}

	@Override
	public DataField edit(DataField dataField) {
		if(dataField.getValidate_items()==null){
			dataField.setValidate_items("");
		}
		this.daoSupport.update("es_data_field", dataField, "field_id="+dataField.getField_id());
		return dataField;
	}

	@Override
	public DataField get(int field_id) {
		
		String sql = "select * from es_data_field where field_id = ? ";
		
		return this.daoSupport.queryForObject(sql, DataField.class, field_id);
	}

	@Override
	public void delete(int field_id) {
		
		String sql = "update es_data_field set status = 0 where field_id = ? ";
		this.daoSupport.execute(sql, field_id);
	}

	@Override
	public List<DataField> getFieldByModel(int modelId) {

		String sql = "select * from es_data_field where model_id = ? and status = 1 ";
		
		return this.daoSupport.queryForList(sql,DataField.class, modelId);
	}

	@Override
	public List<DataField> listByModel(Integer model_id) {

		String sql = "select * from es_data_field where model_id = ? and status = 1 order by is_primary desc,add_time asc";

		return this.daoSupport.queryForList(sql, DataField.class, model_id);
	}
}
