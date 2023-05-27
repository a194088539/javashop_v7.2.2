package com.enation.app.javashop.api.manager.system;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.service.system.LogManager;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.exception.SystemErrorCodeV1;
import com.enation.app.javashop.framework.util.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.*;

/**
 * @Description : 日志分析
 * @Author snow
 * @Date: 2020-02-21 14:46
 * @Version v1.0
 */
@RestController
@RequestMapping("/admin/services")
@Api(description = "日志分析相关API")
public class LogManagerController {

    @Autowired
    private LogManager logManager;

    @ApiOperation(value = "读取服务名列表")
    @GetMapping()
    public List appNameList(){
        List list = logManager.appNameList();
        return list;
    }

    @GetMapping(value = "/{app_name}/instances")
    @ApiOperation(value = "读取实例UUID列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "app_name", value = "服务名", required = true, dataType = "string", paramType = "path")})
    public List instancesList(@ApiIgnore @PathVariable("app_name") String appName){
        List list = this.logManager.instancesList(appName);
        return list;
    }


    @GetMapping(value = "/{app_name}/instances/{uuid}/logs")
    @ApiOperation(value = "读取日志详情列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "app_name", value = "服务名", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "uuid", value = "实例uuid", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "time", value = "时间戳", required = true, dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "pageNo", value = "页数", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "条数", dataType = "int", paramType = "query"),
    })
    public WebPage getLogs(@ApiIgnore @PathVariable("app_name") String appName, @ApiIgnore @PathVariable("uuid") String uuid, Long time,
                           Integer pageNo, Integer pageSize){
        pageNo = (pageNo == null?1:pageNo);
        pageSize = (pageSize == null?100:pageSize);
        String date = DateUtil.toString(time,"yyyy-MM-dd");
        WebPage<String> page = null;
        try {
            page = this.logManager.getLogs(appName,uuid,date,pageNo,pageSize);
        } catch (Exception e){
            if(e.getMessage().equals("no such index")){
                throw new ServiceException(SystemErrorCodeV1.INVALID_REQUEST_PARAMETER,"此日期没有日志，或被删除。");
            }
        }
        return page;

    }


    @GetMapping(value = "/{app_name}/instances/{uuid}/logs/downloader")
    @ApiOperation(value = "导出TXT数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "app_name", value = "服务名", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "uuid", value = "实例uuid", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "time", value = "时间戳", required = true, dataType = "long", paramType = "query")
    })
    public List txtLogs(@ApiIgnore @PathVariable("app_name") String appName,
                        @ApiIgnore @PathVariable("uuid") String uuid,
                        Long time){
        String date = DateUtil.toString(time,"yyyy-MM-dd");
        WebPage<String> page = this.logManager.getLogs(appName,uuid,date,1,-1);
        try {
            page = this.logManager.getLogs(appName,uuid,date,1,-1);
        } catch (Exception e){
            if(e.getMessage().equals("no such index")){
                throw new ServiceException(SystemErrorCodeV1.INVALID_REQUEST_PARAMETER,"此日期没有日志，或被删除。");
            }
        }
        return page.getData();
    }

}
