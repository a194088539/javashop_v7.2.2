package com.enation.coder.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.enation.coder.model.po.DataField;
import com.enation.coder.service.IDataFieldManager;
import com.enation.coder.service.IFileManager;

/**
 * 文件上传控制器
 * @author xulipeng
 * 2018年03月16日17:28:42
 */
@RestController
@RequestMapping("/file")
public class FileController {

	@Autowired
	private IFileManager fileManager;
	
	@Autowired
	private IDataFieldManager dataFieldManager;
	
	@ResponseBody
	@PostMapping("/upload/pdm")
	public List<Map>  uploadPDM(MultipartFile file) {
		List<Map> list = this.fileManager.getTables(file);
		return list;
	}
	
	@ResponseBody
	@PostMapping("/create/model")
	public String  createModel(String [] column_code,Integer projectId,Integer moduleId) {
		try {
			this.fileManager.createModel(column_code, projectId,moduleId);
		} catch (Exception e) {
			return e.getMessage();
		}
		return "ok";
	}
	
	@ResponseBody
	@PostMapping("/create/test")
	public String  test() {

		DataField field = new DataField();
	    field.setChina_name("sss");
	    field.setEnglish_name("dddd");
	    field.setData_type("INT");
	    field.setData_size("10");
	    field.setIs_primary(1);
	    this.dataFieldManager.add(field);
	    
		return "ok";
	}
}
