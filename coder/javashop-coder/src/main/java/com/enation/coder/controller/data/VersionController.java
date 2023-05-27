package com.enation.coder.controller.data;


import com.enation.coder.model.po.Project;
import com.enation.coder.model.po.Version;
import com.enation.coder.service.IVersionManager;
import com.enation.framework.database.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by kingapex on 12/02/2018.
 * 版本
 *
 * @author kingapex
 * @version 1.0
 * @since 6.4.0
 * 12/02/2018
 */
@RestController
@RequestMapping("/data")
@Validated
public class VersionController {


    @Autowired
    private IVersionManager versionManager;

    @GetMapping("/project/{project_id}/version")
    public List<Version> list(@PathVariable("project_id") Integer projectId) {

        return versionManager.list(projectId);
    }

    /**
     * 新建某个工程的分支
     *
     * @param projectId
     * @param version
     * @return
     */
    @PostMapping("/project/{project_id}/version")
    public Version add(@PathVariable("project_id") Integer projectId, @RequestParam("from") Integer fromVersionId, Version version) {


        version.setProject_id(projectId);

        return versionManager.add(fromVersionId, version);
    }


}
