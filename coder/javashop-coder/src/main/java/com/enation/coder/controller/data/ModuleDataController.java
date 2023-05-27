package com.enation.coder.controller.data;

import java.io.File;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
 * @author kingapex
 * @version 1.0
 * @since 6.4.0
 * 12/02/2018
 */
@RestController
@RequestMapping("/data/project/{project_id}/{version_id}/module")
@Validated
public class ModuleDataController {

    @Autowired
    private IModuleManager moduleManager;

    @GetMapping
    public Page<Module> list(int page_no, int page_size,@PathVariable int project_id,@PathVariable("version_id") int versionId){
    	
        return  moduleManager.list(page_no,page_size,project_id,versionId);
    }


    @PostMapping
    public Module add(@Valid Module dataModel,@PathVariable int project_id,@PathVariable("version_id") int versionId){
    	
    	dataModel.setProject_id(project_id);
        dataModel.setVersion_id(versionId);
    	dataModel = moduleManager.add(dataModel);
    	
        return  dataModel;
    }

    @PostMapping("/{module_id}")
    public Module edit(@PathVariable("project_id") int project_id ,@PathVariable("version_id") int versionId,
                       @Valid Module dataModel,@PathVariable("module_id") int module_id){

    	dataModel.setModule_id(module_id);
    	dataModel.setProject_id(project_id);
        dataModel.setVersion_id(versionId);
    	moduleManager.edit(dataModel);

        return  dataModel;
    }

    @GetMapping("/{model_id}")
    public  Module get(@PathVariable("project_id") int project_id,
                        @PathVariable("version_id") int versionId,
                       @PathVariable("model_id") int model_id ){
    	
    	Module model = moduleManager.get(model_id);
    	
        return model;
    }


    @DeleteMapping("/{model_id}")
    public void delete(@PathVariable("project_id") int project_id, @PathVariable("version_id") int versionId,
                       @PathVariable("model_id") int model_id){
    	
    	moduleManager.delete(model_id);
    }


}
