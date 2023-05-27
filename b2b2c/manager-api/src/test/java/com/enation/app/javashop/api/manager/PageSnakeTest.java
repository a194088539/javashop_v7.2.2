package com.enation.app.javashop.api.manager;

import com.enation.app.javashop.model.goods.dos.BrandDO;
import com.enation.app.javashop.service.goods.BrandManager;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.test.BaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * 对Page的蛇形转换的测试
 * @author kingapex
 * @version 1.0
 * @since 7.0.0
 * 2018/5/30
 */
public class PageSnakeTest extends BaseTest {

    @MockBean
    private BrandManager brandManager;


    /**
     * 使用brands的列表
     * 对Page的蛇形转换的测试
     * @throws Exception
     */
    @Test
    public void test() throws Exception {

        //模拟一个page
        BrandDO brand = new BrandDO();

        List<BrandDO> list = new ArrayList<>();
        list.add(brand);

        WebPage page = new WebPage(1L, 1L, 10L, list);
        when(brandManager.list(1, 10, "")).thenReturn(page);

        String content =mockMvc.perform( get("/admin/goods/brands/")
                .header("Authorization",superAdmin))
                .andReturn() .getResponse().getContentAsString();

        //断言其中是带蛇形在
        String expected ="{\"data\":[{\"brand_id\":null,\"name\":null,\"logo\":null,\"disabled\":null}],\"page_no\":1,\"page_size\":10,\"data_total\":1}";
        Assert.assertEquals(expected,content);

    }

}
