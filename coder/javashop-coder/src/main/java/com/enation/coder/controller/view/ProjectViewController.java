package com.enation.coder.controller.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.enation.coder.model.po.Project;
import com.enation.coder.service.IProjectManager;

/**
 * Created by kingapex on 17/02/2018.
 *
 * @author kingapex
 * @version 1.0
 * @since 6.4.0
 * 17/02/2018
 */
@Controller
@RequestMapping("/view/project")
public class ProjectViewController {

    @Autowired
    private IProjectManager projectManager;

    /**
     * 列表
     * @return
     */
    @RequestMapping("/list")
    public  String listView(  ){
        return "project_list";
    }


    /**
     * 新建客户
     * @return
     */
    @RequestMapping("/new")
    public String newOne() {

        return "new_project";
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
