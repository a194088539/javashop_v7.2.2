package com.enation.coder.controller.view;

import java.util.List;

import com.enation.coder.model.po.Version;
import com.enation.coder.service.IVersionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.enation.coder.model.po.Module;
import com.enation.coder.model.po.Project;
import com.enation.coder.service.IModuleManager;
import com.enation.coder.service.IProjectManager;

/**
 * 模型控制类
 * @author fk
 * @version v1.0
 * @since v7.0
 * 2018年3月9日 上午11:29:33
 */
@Controller
@RequestMapping("/view")
public class ModelViewController {

    @Autowired
    private IModuleManager moduleManager;
    
    @Autowired
    private IProjectManager projectManager;

    @Autowired
    private IVersionManager versionManager;

    /**
     * 列表
     * @return
     */
    @RequestMapping("/module/{module_id}/{version_id}/model")
    public  String listView( @PathVariable("module_id") int module_id,Model model,
                             @PathVariable("version_id") int version_id){
    	
    	Module module = moduleManager.get(module_id);
    	Project project = projectManager.get(module.getProject_id());
    	List<Module> moduleList = moduleManager.list(project.getId());
        List<Version> versionList = versionManager.list(module.getProject_id());
        Version version = versionManager.get(version_id);
	    	
    	model.addAttribute("module",module);
    	model.addAttribute("project",project);
    	model.addAttribute("module_id",module_id);
    	model.addAttribute("project_id",project.getId());
    	model.addAttribute("moduleList",moduleList);
        model.addAttribute("version_id",version_id);
        model.addAttribute("versionList",versionList);
        if(version == null){
            version = new Version();
            version.setId(0);
            version.setVersion("master");
        }
        model.addAttribute("version",version);
    	
        return "model_list";
    }

}
