package com.enation.app.javashop.service.trade.pintuan;

import com.enation.app.javashop.framework.util.HexUtils;
import com.enation.app.javashop.model.promotion.pintuan.PtGoodsDoc;
import com.enation.app.javashop.service.trade.pintuan.impl.PinTuanSearchManagerImpl;
import com.enation.app.javashop.framework.elasticsearch.DefaultEsTemplateBuilder;
import com.enation.app.javashop.framework.elasticsearch.EsConfig;
import com.enation.app.javashop.framework.elasticsearch.EsTemplateBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.util.List;

/**
 * Created by kingapex on 2019-01-28.
 *
 * @author kingapex
 * @version 1.0
 * @since 7.1.0
 * 2019-01-28
 */
public class PinTuanIndexTest  {


    private ElasticsearchTemplate elasticsearchTemplate;

    private PinTuanSearchManagerImpl pinTuanSearchManager;


    @Before
    public void init() {
        EsConfig esConfig = new EsConfig();
        esConfig.setIndexName("javashop10");

        elasticsearchTemplate = elasticsearchTemplate();
        pinTuanSearchManager = new PinTuanSearchManagerImpl(elasticsearchTemplate,esConfig);
     }

    @Test
    public void testQuery() {
        List<PtGoodsDoc>  list  = pinTuanSearchManager.search(null, 1, 20);
        System.out.println(list);
    }

    @Test
    public void testDeleteByGoodsId() {
        pinTuanSearchManager.deleteByGoodsId(1L);

    }

    @Test
    public void testDeleteByPinTuanId() {
        pinTuanSearchManager.deleteByPintuanId(1L);

    }

    @Test
    public void testAdd() {

        for (int i = 0; i < 5; i++) {
            PtGoodsDoc ptGoodsDoc = new PtGoodsDoc();
            ptGoodsDoc.setPinTuanId(1L);
            ptGoodsDoc.setGoodsName("wf-"+i);
            ptGoodsDoc.setSalesPrice(100D);
            ptGoodsDoc.setGoodsId(1L);
            ptGoodsDoc.setSkuId(Long.valueOf(i+1));
            ptGoodsDoc.setCategoryId(1L);
            ptGoodsDoc.setCategoryPath( HexUtils.encode("0|86|91|") );
            IndexQuery indexQuery = new IndexQuery();
            indexQuery.setIndexName("javashop2");
            indexQuery.setType("pintuan_goods");
            indexQuery.setId(ptGoodsDoc.getSkuId().toString());
            indexQuery.setObject(ptGoodsDoc);
            elasticsearchTemplate.index(indexQuery);
        }

    }

    @Test
    public void test() {

//        PtGoodsDoc goodsDoc = new PtGoodsDoc();
//        goodsDoc.setSalesPrice(51D);
//
//        IndexQuery indexQuery = new IndexQueryBuilder().withId("" + 1563).withObject(goodsDoc).build();
//        elasticsearchTemplate.index(indexQuery);

        elasticsearchTemplate = elasticsearchTemplate();
        BoolQueryBuilder bqb = QueryBuilders.boolQuery();
        bqb.must(  QueryBuilders.termQuery("goodsId", 619));

        NativeSearchQueryBuilder searchQuery = new NativeSearchQueryBuilder();
        searchQuery.withIndices("javashop2").withTypes("pintuan_goods").withQuery(bqb);

        List<PtGoodsDoc> list  = elasticsearchTemplate.queryForList(searchQuery.build(), PtGoodsDoc.class);
        list.forEach(ptGoods->{

            if (ptGoods.getSkuId().equals(1564)) {
                ptGoods.setSalesPrice(55D);
                IndexQuery indexQuery = new IndexQueryBuilder().withId("" + 1564).withObject(ptGoods).build();
                elasticsearchTemplate.index(indexQuery);
            }


        });
     }

    public ElasticsearchTemplate elasticsearchTemplate() {
        EsTemplateBuilder esTemplateBuilder = new DefaultEsTemplateBuilder().setClusterName("elasticsearch-cluster").setClusterNodes("192.168.2.13:9300");
        return esTemplateBuilder.build();
    }

}
