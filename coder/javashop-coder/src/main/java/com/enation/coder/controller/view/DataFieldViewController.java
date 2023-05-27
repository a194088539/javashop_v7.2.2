package com.enation.coder.controller.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.enation.coder.model.po.Version;
import com.enation.coder.service.IModuleManager;
import com.enation.coder.service.IVersionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.enation.coder.model.enums.FiledDataType;
import com.enation.coder.model.po.DataModel;
import com.enation.coder.model.po.Project;
import com.enation.coder.service.IDataModelManager;
import com.enation.coder.service.IProjectManager;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 模型字段控制类
 * @author fk
 * @version v1.0
 * @since v7.0
 * 2018年3月9日 上午11:29:33
 */
@Controller
@RequestMapping("/view/project/model/{model_id}/field")
public class DataFieldViewController {

    @Autowired
    private IProjectManager projectManager;
    
    @Autowired
    private IDataModelManager dataModelManager;

    @Autowired
    private IVersionManager versionManager;

    @Autowired
    private IModuleManager moduleManager;

    /**
     * 列表
     * @return
     */
    @RequestMapping("/list")
    public  String listView(@PathVariable("model_id") int model_id, @RequestParam("version_id") Integer versionId, String remark, Model model){
    	
    	DataModel dataModel = dataModelManager.get(model_id);

        List<Version> versionList = versionManager.list(dataModel.getProject_id());
    	
    	model.addAttribute("dataModel",dataModel);
    	model.addAttribute("model_id",model_id);

        model.addAttribute("versionList",versionList);
        model.addAttribute("versionId",versionId);
        Version version = versionManager.get(versionId);
        if(version == null){
            version = new Version();
            version.setId(0);
            version.setVersion("master");
        }
        model.addAttribute("version",version);
    	
    	//字段类型
    	Map map = new HashMap<>();
    	for(FiledDataType type : FiledDataType.values()){
    		
    		map.put(type.value(), type.getDescription());
    	}
    	
    	model.addAttribute("dataTypes",map);
    	model.addAttribute("project",projectManager.get(dataModel.getProject_id()));
        model.addAttribute("module",moduleManager.get(dataModel.getModule_id()));

        return "field_list";
    }


    /**
     * 新建客户
     * @return
     */
    @RequestMapping("/new")
    public String newOne() {

        return "new_model";
    }

    /**
     * 客户明细
     * @param customer_id 客户id
     * @param model
     * @return
     */
    @RequestMapping("/detail/{project_id}")
    public String detail(@PathVariable("project_id") int project_id ,Model model) {

        Project project = projectManager.get(project_id);
        model.addAttribute("project",project);

        return "project_detail";
    }

    /**
     * 编辑客户
     * @param customer_id 客户id
     * @param model
     * @return
     */
    @RequestMapping("/edit/{project_id}")
    public String edit(@PathVariable("project_id") int project_id ,Model model) {

    	Project project = projectManager.get(project_id);
        model.addAttribute("project",project);

        return "project_edit";
    }



}
