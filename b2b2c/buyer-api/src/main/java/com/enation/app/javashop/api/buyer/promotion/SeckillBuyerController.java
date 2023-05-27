package com.enation.app.javashop.api.buyer.promotion;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.errorcode.PromotionErrorCode;
import com.enation.app.javashop.model.promotion.seckill.vo.TimeLineVO;
import com.enation.app.javashop.service.promotion.seckill.SeckillGoodsManager;
import com.enation.app.javashop.service.promotion.seckill.SeckillRangeManager;
import com.enation.app.javashop.framework.exception.ServiceException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.bcel.verifier.statics.LONG_Upper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * 限时抢购相关API
 * @author Snow create in 2018/7/23
 * @version v2.0
 * @since v7.0.0
 */
@RestController
@RequestMapping("/promotions/seckill")
@Api(description = "限时抢购相关API")
@Validated
public class SeckillBuyerController {

    @Autowired
    private SeckillGoodsManager seckillApplyManager;

    @Autowired
    private SeckillRangeManager seckillRangeManager;

    @ApiOperation(value = "读取秒杀时刻")
    @ResponseBody
    @GetMapping(value = "/time-line")
    public List<TimeLineVO> readTimeLine(){
        List<TimeLineVO> timeLineVOList = this.seckillRangeManager.readTimeList();
        return timeLineVOList;
    }


    @ApiOperation(value = "根据参数读取限时抢购的商品列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name	= "range_time", value = "时刻",dataType = "int",paramType =	"query"),
            @ApiImplicitParam(name	= "page_no", value = "页码", dataType = "int",	paramType =	"query"),
            @ApiImplicitParam(name	= "page_size", value = "条数", dataType = "int",	paramType =	"query")
    })
    @GetMapping("/goods-list")
    public WebPage goodsList(@ApiIgnore Integer rangeTime, @ApiIgnore Long pageSize, @ApiIgnore Long pageNo){

        if(rangeTime == null){
            throw new ServiceException(PromotionErrorCode.E400.code(),"时刻不能为空");
        }

        if(rangeTime > 24){
            throw new ServiceException(PromotionErrorCode.E400.code(),"时刻必须是0~24的整数");
        }

        List list = this.seckillApplyManager.getSeckillGoodsList(rangeTime,pageNo,pageSize);

        WebPage page = new WebPage();
        page.setData(list);
        page.setPageNo(pageNo.longValue());
        page.setPageSize(pageSize.longValue());
        page.setDataTotal(Integer.valueOf(list.size()).longValue());
        return page;

    }


}
