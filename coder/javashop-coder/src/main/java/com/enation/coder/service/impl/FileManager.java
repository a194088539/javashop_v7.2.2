package com.enation.coder.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DaoSupport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.enation.coder.model.enums.FiledDataType;
import com.enation.coder.model.po.DataField;
import com.enation.coder.model.po.DataModel;
import com.enation.coder.service.IDataFieldManager;
import com.enation.coder.service.IDataModelManager;
import com.enation.coder.service.IFileManager;
import com.enation.coder.service.IProjectManager;
import com.enation.framework.context.ThreadContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.util.DateUtil;
import com.enation.pdm.PDMParser;
import com.enation.pdm.model.Column;
import com.enation.pdm.model.Project;
import com.enation.pdm.model.Table;

/**
 * 文件上传实现类
 * @author xulipeng
 *
 */
@Service
public class FileManager implements IFileManager {

	@Autowired
	private IDaoSupport daoSupport;
	
	@Autowired
	private IDataModelManager dataModelManager;
	
	@Autowired
	private IDataFieldManager dataFieldManager;
	
	@Autowired
	private IProjectManager projectManager;
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.coder.service.IFileManager#getTables(org.springframework.web.multipart.MultipartFile)
	 */
	@Override
	public List<Map> getTables(MultipartFile file) {
		
		List<Map> tablelist = new ArrayList<Map>();
		try {
			Project project = PDMParser.parse(file.getInputStream());
			ThreadContextHolder.getHttpRequest().getSession().setAttribute("project", project);
			
			//所有表的集合
			List<Table> tableList = project.getTableList();
			for (Table table : tableList) {
				String tableName = table.getName().split("\\(")[0];
				String tableCode = table.getCode();
				
				String[] name = tableCode.split("_");
				
				String e_name = "";
				for (int i = 1; i < name.length; i++) {
					String str = name[i];
					String caseName = str.substring(0,1).toUpperCase() + str.substring(1);
					e_name += caseName; 
				}
				
				Map map = new HashMap();
				map.put(tableCode, tableName);
				tablelist.add(map);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return tablelist;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.coder.service.IFileManager#createModel(java.lang.String[])
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void createModel(String[] code,Integer projectId,Integer modeuleId) {
		
		Project project = (Project) ThreadContextHolder.getHttpRequest().getSession().getAttribute("project");
		com.enation.coder.model.po.Project project2 = this.projectManager.get(projectId);
		
		//所有表的集合
		List<Table> tableList = project.getTableList();
		
		for (String c : code) {
			for (Table table : tableList) {
				String tableName = table.getName().split("\\(")[0];
				String tableCode = table.getCode();
				String[] name = tableCode.split("_");
				
				if(!c.equals(tableCode)) {
					continue;
				}
				
				String e_name = "";
				for (int i = 1; i < name.length; i++) {
					String str = name[i];
					String caseName =str;
					if(i!=1) {
						caseName = str.substring(0,1).toUpperCase() + str.substring(1);
					}
					e_name += caseName; 
				}
				
				DataModel dataModel = new DataModel();
				dataModel.setEnglish_name(e_name);
				dataModel.setModel_name(tableName);
				dataModel.setProject_id(projectId);
				//dataModel.setTable_name(tableCode);
				dataModel.setVersion(project2.getSince());
				dataModel.setAdd_time(DateUtil.getDateline());
				dataModel.setModule_id(modeuleId);
				this.dataModelManager.add(dataModel);
				
				List<Column> columnList = table.getColumns();
				for (Column column : columnList) {
					String columnName = column.getName();
					String columnCode = column.getCode();
					String columnType = column.getType();
					boolean pkFlag = column.isPkFlag(); //是否为主键
					String comment = column.getComment();
					
					String type = columnType.split("\\(")[0];
					String length = "0";
					
					try {
						if(type.equals("longtext")) {
							length = "50";
						}else {
							length = columnType.substring(0, columnType.length()-1).split("\\(")[1];
						}
					} catch (Exception e) {
						throw new RuntimeException("请检查pdm 【表名:"+tableName+"  字段名:"+columnName+"】的类型");
					}
					
					
					String fieldType = "INT";
					if(type.equals("int")) {
						
					}else if(type.equals("varchar")) {
						fieldType = "VARCHAR";
						
					}else if(type.equals("bigint")) {
						fieldType = "BIGINT";
						
					}else if(type.equals("decimal")) {
						fieldType = "DECIMAL";
						
					}else if(type.equals("smallint")) {
						
					}
					
					DataField field = new DataField();
				    field.setChina_name(columnName);
				    field.setEnglish_name(columnCode);
				    field.setData_type(fieldType);
				    field.setData_size(length);
					field.setIs_primary(pkFlag==true?1:0);
					field.setModel_id(dataModel.getModel_id());
					
					this.dataFieldManager.add(field);
				}
			}
		}
	}
	
}
