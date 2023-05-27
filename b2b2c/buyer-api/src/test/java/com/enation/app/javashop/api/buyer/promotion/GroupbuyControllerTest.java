package com.enation.app.javashop.api.buyer.promotion;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.promotion.groupbuy.dos.GroupbuyActiveDO;
import com.enation.app.javashop.model.promotion.groupbuy.dos.GroupbuyGoodsDO;
import com.enation.app.javashop.model.promotion.groupbuy.enums.GroupBuyGoodsStatusEnum;
import com.enation.app.javashop.framework.database.DaoSupport;
import com.enation.app.javashop.framework.test.BaseTest;
import com.enation.app.javashop.framework.util.JsonUtil;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 团购相关
 *
 * @author Snow create in 2018/7/20
 * @version v2.0
 * @since v7.0.0
 */
@Transactional(value = "tradeTransactionManager",rollbackFor = Exception.class)
public class GroupbuyControllerTest extends BaseTest {

    @Autowired
    @Qualifier("tradeDaoSupport")
    private DaoSupport daoSupport;

    @Before
    public void testData(){

        GroupbuyActiveDO activeDO = new GroupbuyActiveDO();
        activeDO.setActName("测试团购活动");
        activeDO.setStartTime(1527609600l);
        activeDO.setAddTime(1527609600l);
        activeDO.setEndTime(4683283200l);
        activeDO.setJoinEndTime(4651747200l);
        this.daoSupport.insert(activeDO);
        Long id = this.daoSupport.getLastId("es_groupbuy_active");
        activeDO.setActId(id);

        GroupbuyGoodsDO goodsDO = new GroupbuyGoodsDO();
        goodsDO.setGbTitle("团购商品活动");
        goodsDO.setGbStatus(GroupBuyGoodsStatusEnum.APPROVED.status());
        goodsDO.setGoodsName("团购测试商品");
        goodsDO.setGoodsId(1L);
        goodsDO.setThumbnail("path");
        goodsDO.setActId(activeDO.getActId());
        this.daoSupport.insert(goodsDO);
        long goodsId = this.daoSupport.getLastId("es_groupbuy_goods");
        goodsDO.setGbId(goodsId);
    }


    @Test
    public void test() throws Exception {

        String resultJson = mockMvc.perform(get("/promotions/group-buy/goods")
                .header("Authorization",buyer1)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().is(200))
                .andReturn().getResponse().getContentAsString();

        WebPage page = JsonUtil.jsonToObject(resultJson, WebPage.class);
        if(page.getData()==null ){
            throw new RuntimeException("读取团购商品测试出错");
        }

    }
}
