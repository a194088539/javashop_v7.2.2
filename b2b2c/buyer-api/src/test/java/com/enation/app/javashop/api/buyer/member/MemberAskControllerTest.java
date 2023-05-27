package com.enation.app.javashop.api.buyer.member;

import com.enation.app.javashop.client.goods.GoodsClient;
import com.enation.app.javashop.model.errorcode.GoodsErrorCode;
import com.enation.app.javashop.model.goods.vo.CacheGoods;
import com.enation.app.javashop.model.member.dos.Member;
import com.enation.app.javashop.service.member.MemberManager;
import com.enation.app.javashop.framework.exception.ErrorMessage;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.test.BaseTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author fk
 * @version v1.0
 * @Description: 会员咨询单元测试
 * @date 2018/5/8 11:32
 * @since v7.0.0
 */
@Transactional(value = "memberTransactionManager",propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
public class MemberAskControllerTest extends BaseTest{

    List<MultiValueMap<String, String>> list = null;
    @MockBean
    private GoodsClient goodsClient;
    @MockBean
    private MemberManager memberManager;

    @Before
    public void insertTestData(){

        String[] names = new String[]{"ask_content","goods_id","message"};
        String[] values1 = new String[]{"","1","请输入咨询内容"};
        String[] values2 = new String[]{"是纯棉的吗","","咨询商品不能为空"};
        list = toMultiValueMaps(names,values2,values1);

    }

    /**
     * 添加咨询
     */
    @Test
    public void testInsert() throws Exception{

        // 必填验证
        for (MultiValueMap<String,String> params  : list){
            String message =  params.get("message").get(0);
            ErrorMessage error  = new ErrorMessage("004",message);
            mockMvc.perform(post("/members/asks")
                    .params( params )
                    .header("Authorization",buyer1))
                    .andExpect(status().is(400))
                    .andExpect(  objectEquals( error ));
        }

        when(goodsClient.getFromCache(-1L)).thenThrow(    new ServiceException(GoodsErrorCode.E301.code(), "该商品已被彻底删除"));


        // 不存在的商品
        ErrorMessage error  = new ErrorMessage("301","该商品已被彻底删除");
        mockMvc.perform(post("/members/asks")
                .param("ask_content","是纯棉的吗")
                .param("goods_id","-1")
                .header("Authorization",buyer1))
                .andExpect(status().is(500))
                .andExpect(  objectEquals( error ));

        CacheGoods goods = new CacheGoods();
        goods.setGoodsId(1L);
        goods.setGoodsName("测试商品");
        goods.setSellerId(3L);

        when(goodsClient.getFromCache(1L)).thenReturn(goods);
        Member member = new Member();
        member.setMemberId(1L);
        member.setFace("http://a.jpg");
        when(memberManager.getModel(1L)).thenReturn(member);
        // 正确添加
        mockMvc.perform(post("/members/asks")
                .param("ask_content","是纯棉的吗")
                .param("goods_id","1")
                .header("Authorization",buyer1))
                .andExpect(status().is(200));

    }

    /**
     * 查询我的咨询列表
     */
    @Test
    public void testQuery()throws Exception{

        // 正确查询
        mockMvc.perform(get("/members/asks")
                .header("Authorization",buyer1))
                .andExpect(status().is(200));

    }







}
