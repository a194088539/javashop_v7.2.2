package com.enation.coder.controller.data;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.enation.coder.model.po.DataField;
import com.enation.coder.service.IDataFieldManager;
import com.enation.framework.database.Page;

/**
 * 字段控制器
 * @author fk
 * @version v1.0
 * @since v7.0
 * 2018年3月9日 下午2:07:34
 */
@RestController
@RequestMapping("/data/project/model/{model_id}")
@Validated
public class DataFieldDataController {

	@Autowired
	private IDataFieldManager dataFieldManager;
    
    @GetMapping("/{version_id}/field")
    public Page<DataField> list(int page_no, int page_size,@PathVariable("model_id") int model_id,@PathVariable("version_id") Integer verdionId){
    	
        return  dataFieldManager.list(page_no,page_size,model_id,verdionId);
    }


    /**
     * 添加字段
     * @param project
     * @return
     */
    @PostMapping("/field")
    public DataField add(@Valid DataField dataField,@PathVariable("model_id") int model_id,@RequestParam("version_id") Integer versionId){

        dataField.setVersion_id(versionId);
    	dataField.setModel_id(model_id);
    	dataField = dataFieldManager.add(dataField);
    	
        return  dataField;
    }

    /**
     * 修改模型信息
     * @param customer_id
     * @param customer
     * @return
     */
    @PostMapping("/field/{field_id}")
    public DataField edit(@PathVariable("field_id") int field_id ,@Valid DataField dataField,@PathVariable("model_id") int model_id){

    	dataField.setModel_id(model_id);
    	dataField.setField_id(field_id);
    	dataFieldManager.edit(dataField);

        return  dataField;
    }

    /**
     * 获取一个模型信息
     * @param customer_id
     * @return
     */
    @GetMapping("/field/{field_id}")
    public  DataField get(@PathVariable("field_id") int field_id,@PathVariable("model_id") int model_id ){
    	
        return dataFieldManager.get(field_id);
    }

    /**
     * 删除一个模型信息
     * @param customer_id
     */
    @DeleteMapping("/field/{field_id}")
    public void delete(@PathVariable("field_id") int field_id,@PathVariable("model_id") int model_id){
    	
    	dataFieldManager.delete(field_id);
    }
}
