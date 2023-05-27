package com.enation.coder.controller.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.enation.coder.model.vo.CoderParam;
import com.enation.coder.service.ITempateGenerateManager;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 生成模板controller
 * @author fk
 * @version v1.0
 * @since v7.0
 * 2018年3月12日 上午10:52:01
 */
@Controller
@RequestMapping("/view/template")
public class TemplateViewController {

	@Autowired
	private ITempateGenerateManager tempateGenerateManager;
	
    @RequestMapping("/model/{model_id}")
    public  String getModel( @PathVariable int model_id ,Model model,CoderParam param){
    	
    	param.setModelId(model_id);
    	
    	this.tempateGenerateManager.generateModel(param,model);
    	
        return "template/Model";
    }
    
    @RequestMapping("/service/{model_id}")
    public  String getService( @PathVariable int model_id ,Model model,CoderParam param){
    	
    	param.setModelId(model_id);
    	tempateGenerateManager.generateService(param,model);
    	
    	return "template/Manager";
    }
    
    @RequestMapping("/interface/{model_id}")
    public  String getInterfaceService( @PathVariable int model_id, Model model,CoderParam param){
    	
    	param.setModelId(model_id);
    	tempateGenerateManager.generateInterfaceService(param,model);
    	
    	return "template/IManager";
    }
    
    @RequestMapping("/controller/{model_id}")
    public  String getController( @PathVariable int model_id ,Model model,CoderParam param){
    	
    	param.setModelId(model_id);
    	
    	tempateGenerateManager.generateController(param,model);
    	
    	return "template/Controller";
    }

    @RequestMapping("/pdm/{project_id}")
    public  String getPdm(@PathVariable int project_id , @RequestParam("version_id") Integer versionId, Model model){

        tempateGenerateManager.generatePdm(project_id,versionId,model);

        return "template/pdm";
    }
}
