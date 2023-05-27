package com.enation.coder.service.impl;

import com.enation.coder.config.FileConfig;
import com.enation.coder.model.enums.FiledDataType;
import com.enation.coder.model.po.AdminUser;
import com.enation.coder.model.po.DataField;
import com.enation.coder.model.po.DataModel;
import com.enation.coder.model.po.Module;
import com.enation.coder.model.vo.GenerateParam;
import com.enation.coder.service.IAdminUserManager;
import com.enation.coder.service.IDataFieldManager;
import com.enation.coder.service.IDataModelManager;
import com.enation.coder.service.IModuleManager;
import com.enation.coder.util.FileHelper;
import com.enation.coder.util.FileToZip;
import com.enation.coder.util.UrlUtil;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 模型业务类
 *
 * @author fk
 * @version v1.0
 * @since v7.0
 * 2018年3月9日 上午11:49:45
 */
@Service
public class DataModelManager implements IDataModelManager {

    @Autowired
    private IDaoSupport daoSupport;

    @Autowired
    private FileConfig fileConfig;

    @Autowired
    private IAdminUserManager adminUserManager;

    @Autowired
    private IDataFieldManager dataFieldManager;

    @Autowired
    private IModuleManager moduleManager;


    @Override
    public Page<DataModel> list(int pageNo, int pageSize, int module_id, int versionId) {

        //查询某项目某分支
        Module module = moduleManager.get(module_id);

        String sql = "select * from es_data_model where module_name = ? and version_id = ? and project_id = ? order by add_time desc ";

        return this.daoSupport.queryForPage(sql, pageNo, pageSize, DataModel.class, module.getModule_name(), versionId, module.getProject_id());
    }

    @Override
    public DataModel add(DataModel dataModel) {

        Module module = moduleManager.get(dataModel.getModule_id());

        String sql = "select * from es_module where project_id = ? and version_id = ? and module_name = ?";
        Module moduleNew = this.daoSupport.queryForObject(sql, Module.class, module.getProject_id(), dataModel.getVersion_id(), module.getModule_name());


        dataModel.setModule_name(module.getModule_name());
        dataModel.setVersion_id(moduleNew.getVersion_id());
        dataModel.setModule_id(moduleNew.getModule_id());

        dataModel.setAdd_time(DateUtil.getDateline());

        //生成表名
        String tableName = replaceUpperCase(dataModel.getEnglish_name());
        dataModel.setTable_name(tableName);

        this.daoSupport.insert("es_data_model", dataModel);
        int modelId = this.daoSupport.getLastId("es_data_model");

        dataModel.setModel_id(modelId);

        return dataModel;
    }

    /**
     * 将驼峰格式的英文转成对应格式的表名
     *
     * @param englishName
     * @return
     */
    private String replaceUpperCase(String englishName) {

        StringBuffer tableName = new StringBuffer("es_");
        for (int i = 0; i < englishName.length(); i++) {
            if (Character.isUpperCase(englishName.charAt(i))) {//是大写
                tableName.append("_");
            }
            tableName.append(englishName.charAt(i));
        }

        return tableName.toString().toLowerCase();
    }

    @Override
    public DataModel edit(DataModel dataModel) {

        String tableName = replaceUpperCase(dataModel.getEnglish_name());
        dataModel.setTable_name(tableName);

        DataModel dbModel = this.get(dataModel.getModel_id());
        if (!dbModel.getTable_name().equals(tableName)) {
            //修改关联的字段
            String sql = "update es_data_field set table_name = ? where model_id = ? ";
            this.daoSupport.execute(sql, tableName, dataModel.getModel_id());
        }

        this.daoSupport.update("es_data_model", dataModel, "model_id=" + dataModel.getModel_id());

        return dataModel;
    }

    @Override
    public DataModel get(int dataModel_id) {

        String sql = "select * from es_data_model where model_id = ? ";

        return this.daoSupport.queryForObject(sql, DataModel.class, dataModel_id);
    }

    @Override
    public void delete(int dataModel_id) {

        String sql = "delete from es_data_model where model_id = ?";

        this.daoSupport.execute(sql, dataModel_id);
    }

    @Override
    public void generate(GenerateParam params) {

        DataModel model = this.get(params.getModelId());

        AdminUser user = adminUserManager.getCurrentUser();
        String s = model.getEnglish_name();
        String className = (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();

        String fileName = fileConfig.getFilePath();

        //使用传递的包名做一层文件夹
        model.setChild_package_name(params.getPackageName());
        this.edit(model);


        fileName += params.getPackageName() + "/" + s.toLowerCase();

        FileHelper.createDirectory(fileName);

        try {
            //判断需要生成的文件
            String[] items = params.getGenerateItems();

            FileHelper.writeToFile(fileName + "/" + className + ".java", UrlUtil.getHTML(UrlUtil.getCallUrl() + "/view/template/model/" + model.getModel_id() + "?packageName=" + params.getPackageName() + "&username=" + user.getUsername()));

            if (items != null && items.length > 0) {//如果没有选择，只生成model层
                if (Arrays.asList(items).contains("service")) {//生成业务层

                    FileHelper.writeToFile(fileName + "/" + className + "Manager.java", UrlUtil.getHTML(UrlUtil.getCallUrl() + "/view/template/interface/" + model.getModel_id() + "?packageName=" + params.getPackageName() + "&username=" + user.getUsername()));
                    FileHelper.writeToFile(fileName + "/" + className + "ManagerImpl.java", UrlUtil.getHTML(UrlUtil.getCallUrl() + "/view/template/service/" + model.getModel_id() + "?packageName=" + params.getPackageName() + "&username=" + user.getUsername()));
                }

                if (Arrays.asList(items).contains("controller")) {
                    FileHelper.writeToFile(fileName + "/" + className + "Controller.java", UrlUtil.getHTML(UrlUtil.getCallUrl() + "/view/template/controller/" + model.getModel_id() + "?packageName=" + params.getPackageName() + "&username=" + user.getUsername()));
                }
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public File download(int model_id) {

        DataModel model = this.get(model_id);

        String fileName = fileConfig.getFilePath();//G:/7.0/ding/goodssku

        fileName += model.getChild_package_name() + "/" + model.getEnglish_name().toLowerCase();//goods/goodssku

        String zipPath = fileConfig.getZipFilePath() + model.getChild_package_name();
        FileHelper.createDirectory(zipPath);

        FileToZip.fileToZip(fileName, zipPath, model.getEnglish_name().toLowerCase());

        /**创建一个临时压缩文件，我们会把文件流全部注入到这个文件中，这里的文件你可以自定义是.rar还是.zip**/
        File file = new File(zipPath + "/" + model.getEnglish_name().toLowerCase() + ".zip");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return file;
    }

    @Override
    public String getSql(int model_id) {

        DataModel model = this.get(model_id);
        List<DataField> fields = dataFieldManager.getFieldByModel(model_id);

        StringBuffer sqlStr = new StringBuffer();
        sqlStr.append("CREATE TABLE `" + model.getTable_name() + "` ( \r\n");
        String primaryKey = "";
        for (DataField field : fields) {

            FiledDataType dataType = FiledDataType.valueOf(field.getData_type());

            if (dataType.equals(FiledDataType.LONGTEXT)) {
                sqlStr.append("`" + field.getEnglish_name() + "` " + field.getData_type().toLowerCase());
            } else {
                sqlStr.append("`" + field.getEnglish_name() + "` " + field.getData_type().toLowerCase() + "(" + field.getData_size() + ")");
            }
            //是主键，则自增
            if (field.getIs_primary() == 1) {
                primaryKey = field.getEnglish_name();
                sqlStr.append(" NOT NULL AUTO_INCREMENT");
            } else if (dataType.equals(FiledDataType.LONGTEXT) || dataType.equals(FiledDataType.VARCHAR)) {
                sqlStr.append(" COLLATE utf8mb4_unicode_ci  DEFAULT NULL");
            } else {
                sqlStr.append(" DEFAULT NULL");
            }
            //增加注释
            sqlStr.append(" COMMENT '" + field.getChina_name() + "',\n");

        }
        if (!"".equals(primaryKey)) {
            sqlStr.append(" PRIMARY KEY (`" + primaryKey + "`)\r\n");
        }
        //结尾
        sqlStr.append(") ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;");

        return sqlStr.toString();
    }

    @Override
    public List<DataModel> listModelsByModule(Integer module_id) {

        String sql = "select * from es_data_model where module_id = ? order by add_time desc ";

        return this.daoSupport.queryForList(sql, DataModel.class, module_id);
    }
}
