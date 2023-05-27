package com.enation.coder.controller.data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.enation.coder.model.po.DataModel;
import com.enation.coder.model.po.Module;
import com.enation.coder.model.vo.GenerateParam;
import com.enation.coder.service.IDataModelManager;
import com.enation.coder.service.IModuleManager;
import com.enation.coder.util.FileToZip;
import com.enation.framework.database.Page;

/**
 * Created by kingapex on 12/02/2018.
 * 用户数据控制器
 *
 * @author kingapex
 * @version 1.0
 * @since 6.4.0
 * 12/02/2018
 */
@RestController
@RequestMapping("/data")
@Validated
public class DataModelDataController {

    @Autowired
    private IDataModelManager dataModelManager;
    @Autowired
    private IModuleManager moduleManager;

    @GetMapping("/project/{module_id}/{version_id}/model")
    public Page<DataModel> list(int page_no, int page_size, @PathVariable int module_id, @PathVariable("version_id") int versionId) {

        return dataModelManager.list(page_no, page_size, module_id,versionId);
    }


    /**
     * 添加模型
     *
     * @param dataModel
     * @param module_id
     * @return
     */
    @PostMapping("/project/{module_id}/model")
    public DataModel add(@Valid DataModel dataModel, @PathVariable int module_id,@RequestParam("version_id") Integer versionId) {
        Module module = moduleManager.get(module_id);
        dataModel.setModule_id(module_id);
        dataModel.setProject_id(module.getProject_id());
        dataModel.setVersion_id(versionId);

        dataModel = dataModelManager.add(dataModel);

        return dataModel;
    }

    /**
     * 修改模型信息
     *
     * @param dataModel
     * @param model_id
     * @param module_id
     * @return
     */
    @PostMapping("/project/{module_id}/model/{model_id}")
    public DataModel edit(@Valid DataModel dataModel,
                          @PathVariable("model_id") int model_id, @PathVariable int module_id) {

        dataModel.setModel_id(model_id);
        dataModelManager.edit(dataModel);

        return dataModel;
    }

    /**
     * 获取一个模型信息
     *
     * @param model_id
     * @param module_id
     * @return
     */
    @GetMapping("/project/{module_id}/model/{model_id}")
    public DataModel get(@PathVariable("model_id") int model_id, @PathVariable int module_id) {

        DataModel model = dataModelManager.get(model_id);

        return model;
    }

    /**
     * 删除一个模型信息
     *
     * @param customer_id
     */
    @DeleteMapping("/project/{module_id}/model/{model_id}")
    public void delete(@PathVariable("module_id") int module_id, @PathVariable("model_id") int model_id) {

        dataModelManager.delete(model_id);
    }

    /**
     * 生成代码
     *
     * @param project_id
     * @param model_id
     */
    @GetMapping("/project/model/{model_id}/generate")
    public void generate(@PathVariable("model_id") int model_id, GenerateParam params) {

        params.setModelId(model_id);

        dataModelManager.generate(params);

    }

    /**
     * 下载代码
     *
     * @param project_id
     * @param model_id
     */
    @GetMapping("/project/model/{model_id}/download")
    public HttpServletResponse download(@PathVariable("model_id") int model_id, HttpServletResponse response) {

        try {

            File file = dataModelManager.download(model_id);

            response.reset();
            return FileToZip.downloadZip(file, response);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    /**
     * 生成sql
     *
     * @param model_id
     * @return
     */
    @GetMapping("/project/model/{model_id}/sql")
    public String generateSql(@PathVariable("model_id") int model_id) {

        return this.dataModelManager.getSql(model_id);
    }


}
