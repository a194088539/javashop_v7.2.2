package com.enation.coder.controller.data;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.enation.coder.util.FileToZip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.enation.coder.model.po.Project;
import com.enation.coder.service.IProjectManager;
import com.enation.framework.database.Page;

import java.io.*;

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
public class ProjectDataController {

    @Autowired
    private IProjectManager projectManager;

    @GetMapping("/project")
    public Page<Project> list(int page_no, int page_size) {

        return projectManager.list(page_no, page_size);
    }


    /**
     * 添加项目
     *
     * @param project
     * @return
     */
    @PostMapping("/project")
    public Project add(@Valid Project project) {

        project = projectManager.add(project);
        return project;
    }

    /**
     * 修改项目工程
     *
     * @param project_id
     * @param project
     * @return
     */
    @PostMapping("/project/{project_id}")
    public Project edit(@PathVariable("project_id") int project_id, @Valid Project project) {

        projectManager.edit(project_id, project);

        return project;
    }

    /**
     * 复制项目工程
     *
     * @param projectId
     * @return
     */
    @PostMapping("/project/{project_id}/copy")
    public String copy(@PathVariable("project_id") Integer projectId) {

        projectManager.copy(projectId);

        return "ok";
    }


    /**
     * 获取一个项目工程
     *
     * @param project_id
     * @return
     */
    @GetMapping("/project/{project_id}")
    public Project get(@PathVariable("project_id") int project_id) {

        Project project = projectManager.get(project_id);

        return project;
    }

    /**
     * 删除一个工程
     *
     * @param projectId
     */
    @DeleteMapping("/project/{project_id}")
    public String delete(@PathVariable("project_id") int projectId) {

        projectManager.delete(projectId);

        return "";
    }

    /**
     * 下载pdm
     *
     * @param projectId
     * @param response
     * @return
     */
    @GetMapping("/project/{project_id}/pdm")
    public HttpServletResponse download(@PathVariable("project_id") int projectId, @RequestParam("version_id") Integer versionId, HttpServletResponse response) {

        File file = projectManager.downloadPdm(projectId,versionId);
        try {
            // 取得文件名。
            String filename = file.getName();

            // 以流的形式下载文件。
            InputStream fis = new BufferedInputStream(new FileInputStream(file));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            // 清空response
            response.reset();
            // 设置response的Header
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes()));
            response.addHeader("Content-Length", "" + file.length());
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            toClient.write(buffer);
            toClient.flush();
            toClient.close();
        } catch (Exception ex) {
            //ex.printStackTrace();
        }
        return response;
    }


}
