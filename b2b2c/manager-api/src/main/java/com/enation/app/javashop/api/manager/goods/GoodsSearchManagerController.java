package com.enation.app.javashop.api.manager.goods;

import com.enation.app.javashop.model.base.rabbitmq.AmqpExchange;
import com.enation.app.javashop.client.goods.GoodsClient;
import com.enation.app.javashop.client.trade.PintuanClient;
import com.enation.app.javashop.model.promotion.pintuan.Pintuan;
import com.enation.app.javashop.model.promotion.tool.enums.PromotionStatusEnum;
import com.enation.app.javashop.model.system.vo.TaskProgressConstant;
import com.enation.app.javashop.model.util.progress.ProgressManager;
import com.enation.app.javashop.framework.exception.ResourceNotFoundException;
import com.enation.app.javashop.framework.rabbitmq.MessageSender;
import com.enation.app.javashop.framework.rabbitmq.MqMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author fk
 * @version v2.0
 * @Description: 商品全文检索
 * @date 2018/6/1915:55
 * @since v7.0.0
 */
@RestController
@RequestMapping("/admin/goods/search")
@Api(description = "商品检索相关API")
public class GoodsSearchManagerController {

    @Autowired
    private ProgressManager progressManager;
    @Autowired
    private MessageSender messageSender;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private PintuanClient pintuanClient;

    @GetMapping
    @ApiOperation(value = "商品索引初始化")
    public String create(){

        if (progressManager.getProgress(TaskProgressConstant.GOODS_INDEX) != null) {
            throw new ResourceNotFoundException("有索引任务正在进行中，需等待本次任务完成后才能再次生成。");
        }
        /** 发送索引生成消息 */
        messageSender.send(new MqMessage(AmqpExchange.INDEX_CREATE, AmqpExchange.INDEX_CREATE+"_ROUTING","1"));

        List<Pintuan> list = pintuanClient.get(PromotionStatusEnum.UNDERWAY.name());
        /** 获取商品数 */
        int goodsCount = this.goodsClient.queryGoodsCount();


        //检测此次任务是否发送成功
        while ((goodsCount + list.size()) > 0) {
            if(progressManager.getProgress(TaskProgressConstant.GOODS_INDEX) != null) {
                break;
            }
        }

        return TaskProgressConstant.GOODS_INDEX;
    }
}
