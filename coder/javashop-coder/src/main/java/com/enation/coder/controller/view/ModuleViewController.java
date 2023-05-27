package com.enation.coder.controller.view;

import com.enation.coder.model.po.Version;
import com.enation.coder.service.IVersionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.enation.coder.model.po.Project;
import com.enation.coder.service.IProjectManager;

import java.util.List;

/**
 * Created by kingapex on 17/02/2018.
 *
 * @author kingapex
 * @version 1.0
 * @since 6.4.0
 * 17/02/2018
 */
@Controller
@RequestMapping("/view/project/{project_id}/module")
public class ModuleViewController {

    @Autowired
    private IProjectManager projectManager;

    @Autowired
    private IVersionManager versionManager;

    /**
     * 列表
     *
     * @return
     */
    @RequestMapping("/list")
    public String listView(@PathVariable("project_id") int project_id, Model model) {

        Project project = projectManager.get(project_id);

        List<Version> versionList = versionManager.list(project_id);

        model.addAttribute("project", project);
        model.addAttribute("project_id", project_id);
        model.addAttribute("versionList", versionList);

        return "module_list";
    }

}
