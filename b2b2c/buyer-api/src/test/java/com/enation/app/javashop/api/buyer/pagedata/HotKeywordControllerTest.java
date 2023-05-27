package com.enation.app.javashop.api.buyer.pagedata;

import com.enation.app.javashop.framework.test.BaseTest;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author fk
 * @version v1.0
 * @Description: 热门关键字单元测试
 * @date 2018/6/14 11:15
 * @since v7.0.0
 */
@Transactional(value = "systemTransactionManager",rollbackFor=Exception.class)
public class HotKeywordControllerTest extends BaseTest {


    @Test
    public void testQuery() throws Exception{

        mockMvc.perform(get("/pages/hot-keywords").param("num","3"))
                .andExpect(status().is(200));
    }

}
