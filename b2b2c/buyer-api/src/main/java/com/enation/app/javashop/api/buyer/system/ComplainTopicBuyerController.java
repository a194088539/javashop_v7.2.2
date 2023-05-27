package com.enation.app.javashop.api.buyer.system;

import com.enation.app.javashop.model.system.dos.ComplainTopic;
import com.enation.app.javashop.service.system.ComplainTopicManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 投诉主题控制器
 * @author fk
 * @version v2.0
 * @since v2.0
 * 2019-11-26 16:06:44
 */
@RestController
@RequestMapping("/trade/order-complains/topics")
@Api(description = "投诉主题相关API")
public class ComplainTopicBuyerController {
	
	@Autowired
	private	ComplainTopicManager complainTopicManager;
				

	@ApiOperation(value	= "查询投诉主题列表", response = ComplainTopic.class)
	@GetMapping
	public List<ComplainTopic> list()	{
		
		return	this.complainTopicManager.list();
	}

				
}