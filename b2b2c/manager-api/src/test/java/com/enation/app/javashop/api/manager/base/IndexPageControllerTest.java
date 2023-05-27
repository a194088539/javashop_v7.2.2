package com.enation.app.javashop.api.manager.base;

import com.enation.app.javashop.framework.test.BaseTest;
import org.junit.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 首页api单元测试
 *
 * @author chopper
 * @version v1.0
 * @Description:
 * @since v7.0
 * 2018/7/2 上午5:35
 */

public class IndexPageControllerTest extends BaseTest {

    @Test
    public void index() throws Exception {
        mockMvc.perform(get("/admin/index/page")
                .header("Authorization",superAdmin))
                .andExpect(status().is(200));
    }


}
