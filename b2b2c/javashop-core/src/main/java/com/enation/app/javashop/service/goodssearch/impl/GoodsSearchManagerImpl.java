package com.enation.app.javashop.service.goodssearch.impl;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.HexUtils;
import com.enation.app.javashop.mapper.goods.GoodsWordsMapper;
import com.enation.app.javashop.model.base.rabbitmq.AmqpExchange;
import com.enation.app.javashop.client.member.ShopCatClient;
import com.enation.app.javashop.model.goodssearch.*;
import com.enation.app.javashop.model.goods.dos.BrandDO;
import com.enation.app.javashop.model.goods.dos.CategoryDO;
import com.enation.app.javashop.model.goods.vo.CategoryVO;
import com.enation.app.javashop.service.goods.BrandManager;
import com.enation.app.javashop.service.goods.CategoryManager;
import com.enation.app.javashop.service.goods.util.CatUrlUtils;
import com.enation.app.javashop.service.goods.util.Separator;
import com.enation.app.javashop.service.goodssearch.GoodsSearchManager;
import com.enation.app.javashop.service.goodssearch.util.SelectorUtil;
import com.enation.app.javashop.service.goodssearch.util.SortContainer;
import com.enation.app.javashop.model.shop.dos.ShopCatDO;
import com.enation.app.javashop.framework.database.DaoSupport;
import com.enation.app.javashop.framework.elasticsearch.EsConfig;
import com.enation.app.javashop.framework.elasticsearch.EsSettings;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.util.StringUtil;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.InternalAggregations;
import org.elasticsearch.search.aggregations.bucket.nested.InternalNested;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.sort.SortOrder;
import com.enation.app.javashop.framework.rabbitmq.MessageSender;
import com.enation.app.javashop.framework.rabbitmq.MqMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 基于es的商品检索
 *
 * @author fk
 * @version v6.4
 * @since v6.4
 * 2017年9月18日 上午11:42:06
 */
@Service
public class GoodsSearchManagerImpl implements GoodsSearchManager {

    @Autowired
    protected CategoryManager categoryManager;
    @Autowired
    protected BrandManager brandManager;

    @Autowired
    protected ShopCatClient shopCatClient;

    @Autowired
    protected EsConfig esConfig;

    @Autowired
    protected ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private GoodsWordsMapper goodsWordsMapper;


    public GoodsSearchManagerImpl() {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }

    @Override
    public WebPage search(GoodsSearchDTO goodsSearch) {

        int pageNo = Integer.parseInt(goodsSearch.getPageNo()+"");
        int pageSize = Integer.parseInt(goodsSearch.getPageSize()+"");

        SearchRequestBuilder searchRequestBuilder;
        try {
            searchRequestBuilder = this.createQuery(goodsSearch);

            //如果不为空 则表示关键词搜索
            if (!StringUtil.isEmpty(goodsSearch.getKeyword())) {
                //搜索关键字消息
                this.messageSender.send(new MqMessage(AmqpExchange.SEARCH_KEYWORDS, AmqpExchange.SEARCH_KEYWORDS + "_ROUTING", goodsSearch.getKeyword()));
            }

            //设置分页信息
            searchRequestBuilder.setFrom((pageNo - 1) * pageSize).setSize(pageSize);
            // 设置是否按查询匹配度排序
            searchRequestBuilder.setExplain(true);
            SearchResponse response = searchRequestBuilder.execute().actionGet();

            SearchHits searchHits = response.getHits();
            List<GoodsSearchLine> resultlist = new ArrayList<>();
            for (SearchHit hit : searchHits) {
                Map<String, Object> map = hit.getSource();
                GoodsSearchLine goodsSearchLine = new GoodsSearchLine();
                goodsSearchLine.setName(map.get("goodsName").toString());
                goodsSearchLine.setDiscountPrice(StringUtil.toDouble(map.get("discountPrice").toString()));
                goodsSearchLine.setThumbnail(map.get("thumbnail").toString());
                goodsSearchLine.setPrice(StringUtil.toDouble(map.get("price").toString(), 0d));
                goodsSearchLine.setGoodsId(Long.parseLong(map.get("goodsId").toString()));
                goodsSearchLine.setSmall(map.get("small").toString());
                goodsSearchLine.setCommentNum(Integer.parseInt(map.get("commentNum").toString()));
                goodsSearchLine.setBuyCount(Integer.parseInt(map.get("buyCount").toString()));
                goodsSearchLine.setGrade(StringUtil.toDouble(map.get("grade").toString(), 0d));
                goodsSearchLine.setSellerId(Long.parseLong(map.get("sellerId").toString()));
                goodsSearchLine.setSellerName(map.get("sellerName").toString());
                goodsSearchLine.setSelfOperated(map.get("selfOperated") == null ? 0 : StringUtil.toInt(map.get("selfOperated").toString(), 0));
                resultlist.add(goodsSearchLine);
            }

            WebPage webPage = new WebPage(goodsSearch.getPageNo(), searchHits.getTotalHits(), goodsSearch.getPageSize(), resultlist);

            return webPage;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new WebPage(goodsSearch.getPageNo(), 0L, goodsSearch.getPageSize(), new ArrayList());

    }

    @Override
    public Map<String, Object> getSelector(GoodsSearchDTO goodsSearch) {
        SearchRequestBuilder searchRequestBuilder;
        try {
            searchRequestBuilder = this.createQuery(goodsSearch);
            //分类
            AggregationBuilder categoryTermsBuilder = AggregationBuilders.terms("categoryAgg").field("categoryId").size(Integer.MAX_VALUE);
            //品牌
            AggregationBuilder brandTermsBuilder = AggregationBuilders.terms("brandAgg").field("brand").size(Integer.MAX_VALUE);
            //参数
            AggregationBuilder valuesBuilder = AggregationBuilders.terms("valueAgg").field("params.value").size(Integer.MAX_VALUE);
            AggregationBuilder paramsNameBuilder = AggregationBuilders.terms("nameAgg").field("params.name").subAggregation(valuesBuilder).size(Integer.MAX_VALUE);
            AggregationBuilder avgBuild = AggregationBuilders.nested("paramsAgg", "params").subAggregation(paramsNameBuilder);

            searchRequestBuilder.addAggregation(categoryTermsBuilder);
            searchRequestBuilder.addAggregation(brandTermsBuilder);
            searchRequestBuilder.addAggregation(avgBuild);

            SearchResponse sr = searchRequestBuilder.execute().actionGet();
            Map<String, Aggregation> aggMap = sr.getAggregations().asMap();

            Map<String, Object> map = new HashMap<>(16);

            //分类
            LongTerms categoryTerms = (LongTerms) aggMap.get("categoryAgg");
            List<LongTerms.Bucket> categoryBuckets = categoryTerms.getBuckets();

            List<CategoryVO> allCatList = this.categoryManager.listAllChildren(0L);

            List<SearchSelector> catDim = SelectorUtil.createCatSelector(categoryBuckets, allCatList, goodsSearch.getCategory());
            map.put("cat", catDim);
            String catPath = null;
            if (goodsSearch.getCategory() != null) {
                CategoryDO cat = categoryManager.getModel(goodsSearch.getCategory());
                String path = cat.getCategoryPath();
                catPath = path.replace("|", Separator.SEPARATOR_PROP_VLAUE).substring(0, path.length() - 1);
            }

            List<SearchSelector> selectedCat = CatUrlUtils.getCatDimSelected(categoryBuckets, allCatList, catPath);
            //已经选择的分类
            map.put("selected_cat", selectedCat);

            //品牌
            LongTerms brandTerms = (LongTerms) aggMap.get("brandAgg");
            List<LongTerms.Bucket> brandBuckets = brandTerms.getBuckets();
            List<BrandDO> brandList = brandManager.getAllBrands();
            List<SearchSelector> brandDim = SelectorUtil.createBrandSelector(brandBuckets, brandList);
            map.put("brand", brandDim);
//            List<SearchSelector> selectedBrand = BrandUrlUtils.createSelectedBrand(brandList, goodsSearch.getBrand());

            //参数
            InternalNested paramsAgg = (InternalNested) aggMap.get("paramsAgg");
            InternalAggregations paramTerms = paramsAgg.getAggregations();
            Map<String, Aggregation> nameMap = paramTerms.asMap();
            StringTerms nameTerms = (StringTerms) nameMap.get("nameAgg");


            Iterator<StringTerms.Bucket> paramBucketIt = nameTerms.getBuckets().iterator();


            List<PropSelector> paramDim = SelectorUtil.createParamSelector(paramBucketIt);
            map.put("prop", paramDim);


            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap<>(16);
    }


    /**
     * 构建查询条件
     *
     * @return
     * @throws Exception
     */
    protected SearchRequestBuilder createQuery(GoodsSearchDTO goodsSearch) throws Exception {


        String keyword = goodsSearch.getKeyword();
        Long cat = goodsSearch.getCategory();
        Long brand = goodsSearch.getBrand();
        String price = goodsSearch.getPrice();
        Long sellerId = goodsSearch.getSellerId();
        Long shopCatId = goodsSearch.getShopCatId();
        SearchRequestBuilder searchRequestBuilder = elasticsearchTemplate.getClient().prepareSearch(esConfig.getIndexName() + "_" + EsSettings.GOODS_INDEX_NAME);

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 关键字检索
        if (!StringUtil.isEmpty(keyword)) {
            QueryStringQueryBuilder queryString = new QueryStringQueryBuilder(keyword).field("goodsName");
            queryString.defaultOperator(Operator.AND);
            //按照关键字检索  关键字无需按照最细粒度分词  update by liuyulei 2019-12-12
            queryString.analyzer("ik_smart");
            queryString.useDisMax(false);
            boolQueryBuilder.must(queryString);
        }
        // 品牌搜素
        if (brand != null) {
            boolQueryBuilder.must(QueryBuilders.termQuery("brand", brand));
        }
        // 分类检索
        if (cat != null) {

            CategoryDO category = categoryManager.getModel(cat);
            if (category == null) {
                throw new ServiceException("", "该分类不存在");
            }

            boolQueryBuilder.must(QueryBuilders.wildcardQuery("categoryPath", HexUtils.encode(category.getCategoryPath()) + "*"));
        }
        //卖家搜索
        if (sellerId != null) {
            boolQueryBuilder.must(QueryBuilders.termQuery("sellerId", sellerId));
        }
        // 卖家分组搜索
        if (shopCatId != null) {
            ShopCatDO shopCat = shopCatClient.getModel(shopCatId);
            if (shopCat == null) {
                throw new ServiceException("", "该分组不存在");
            }
            boolQueryBuilder.must(QueryBuilders.wildcardQuery("shopCatPath", HexUtils.encode(shopCat.getCatPath()) + "*"));
        }

        // 参数检索
        String prop = goodsSearch.getProp();
        if (!StringUtil.isEmpty(prop)) {
            String[] propArray = prop.split(Separator.SEPARATOR_PROP);
            for (String p : propArray) {
                String[] onpropAr = p.split(Separator.SEPARATOR_PROP_VLAUE);
                String name = onpropAr[0];
                String value = onpropAr[1];
                boolQueryBuilder.must(QueryBuilders.nestedQuery("params", QueryBuilders.termQuery("params.name", name), ScoreMode.None));
                boolQueryBuilder.must(QueryBuilders.nestedQuery("params", QueryBuilders.termQuery("params.value", value), ScoreMode.None));
            }
        }

        //价格搜索
        if (!StringUtil.isEmpty(price)) {
            String[] pricear = price.split(Separator.SEPARATOR_PROP_VLAUE);
            double min = StringUtil.toDouble(pricear[0], 0.0);
            double max = Integer.MAX_VALUE;

            if (pricear.length == 2) {
                max = StringUtil.toDouble(pricear[1], Double.MAX_VALUE);
            }
            boolQueryBuilder.must(QueryBuilders.rangeQuery("price").from(min).to(max).includeLower(true).includeUpper(true));
        }

        // 删除的商品不显示
        boolQueryBuilder.must(QueryBuilders.termQuery("disabled", "1"));
        // 未上架的商品不显示
        boolQueryBuilder.must(QueryBuilders.termQuery("marketEnable", "1"));
        // 待审核和审核不通过的商品不显示
        boolQueryBuilder.must(QueryBuilders.termQuery("isAuth", "1"));

        searchRequestBuilder.setQuery(boolQueryBuilder);


        //排序
        String sortField = goodsSearch.getSort();

        String sortId = "priority";

        SortOrder sort = SortOrder.DESC;

        if (!StringUtil.isEmpty(sortField)) {

            Map<String, String> sortMap = SortContainer.getSort(sortField);

            sortId = sortMap.get("id");

            // 如果是默认排序  --默认排序根据 商品优先级排序  add by liuyulei _2019-07-01
            if ("def".equals(sortId)) {
                sortId = "priority";
            }
            if ("buynum".equals(sortId)) {
                sortId = "buyCount";
            }

            if ("desc".equals(sortMap.get("def_sort"))) {
                sort = SortOrder.DESC;
            } else {
                sort = SortOrder.ASC;
            }
        }


        searchRequestBuilder.addSort(sortId, sort);

        //好平率
        if ("grade".equals(sortId)) {
            searchRequestBuilder.addSort("commentNum", SortOrder.DESC);
            searchRequestBuilder.addSort("buyCount", SortOrder.DESC);
        }

        //如果不是默认排序 则在原有搜索结果基础上加上商品优先级排序   add by liuyulei 2019-07-01
        if (!"priority".equals(sortId)) {
            //商品优先级
            searchRequestBuilder.addSort("priority", SortOrder.DESC);
        }


        return searchRequestBuilder;


    }

    @Override
    public List<GoodsWords> getGoodsWords(String keyword) {

        return this.goodsWordsMapper.getGoodsWords("%" + keyword + "%");
    }
}
