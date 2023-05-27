package com.enation.app.javashop.api.buyer.goods;

import com.enation.app.javashop.model.goods.dos.CategoryDO;
import com.enation.app.javashop.service.goods.CategoryManager;
import com.enation.app.javashop.service.goodssearch.GoodsIndexManager;
import com.enation.app.javashop.framework.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

/**
 * @author fk
 * @version v2.0
 * @Description: 全文检索单元测试
 * @date 2018/6/21 15:51
 * @since v7.0.0
 */
@Transactional(value = "goodsTransactionManager", rollbackFor = Exception.class)
public class GoodsSearchControllerTest extends BaseTest {

    @Autowired
    private GoodsIndexManager goodsIndexManager;

    @MockBean
    private CategoryManager categoryManager;


    @Test
    public void testQueryGoods() throws Exception{


        CategoryDO categoryDO = new CategoryDO();
        categoryDO.setCategoryId(1L);
        categoryDO.setCategoryPath("0|1|");
        when (categoryManager.getModel(1L)).thenReturn(categoryDO);

        Map goods = new HashMap<>();
        goods.put("goods_name","其实这就是个测试商品");
        goods.put("goods_id","-10");
        goods.put("category_id","1");
        goods.put("buy_count","0");
        goods.put("seller_name","0");
        goods.put("disabled","1");
        goods.put("market_enable","1");
        goods.put("is_auth","1");
        goods.put("seller_id","1");

        goodsIndexManager.deleteIndex(goods);
        goodsIndexManager.addIndex(goods);

        //查询商品返回200
        String res = mockMvc.perform(get("/goods/search").param("keyword","其实这就是个测试商品"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        //Assert.assertThat(res,containsString("\"name\":\"其实这就是个测试商品\""));



    }
}
