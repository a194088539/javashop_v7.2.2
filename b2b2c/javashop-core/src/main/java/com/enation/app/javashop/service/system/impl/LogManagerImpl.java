package com.enation.app.javashop.service.system.impl;

import com.enation.app.javashop.framework.context.instance.AppInstance;
import com.enation.app.javashop.framework.context.instance.InstanceContext;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.elasticsearch.EsSettings;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import com.enation.app.javashop.framework.logs.appender.EsLog;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.service.system.LogManager;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Description : 日志业务层实现类
 * @Author snow
 * @Date: 2020-02-03 17:22
 * @Version v1.0
 */
@Service
public class LogManagerImpl implements LogManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    InstanceContext instanceContext;


    @Override
    public List<Map> appNameList() {
        Set<String> apps = instanceContext.getApps();
        List<Map> list = new ArrayList();
        for (String appName : apps) {
            Map map = new HashMap(16);
            map.put("name", appName);
            list.add(map);
        }
        return list;
    }


    @Override
    public List<Map> instancesList(String appName) {

        List<AppInstance> instances = instanceContext.getInstances();

        List list = new ArrayList();
        for (AppInstance instance : instances) {
            if(appName.equals(instance.getAppName())){
                Map map = new HashMap(16);
                map.put("uuid", instance.getUuid());
                list.add(map);
            }


        }

        return list;
    }


    @Override
    public WebPage<String> getLogs(String appName, String instances,
                                   String date, int pageNo, int pageSize) throws RuntimeException {

        StringBuffer indexName = new StringBuffer(EsSettings.LOG_INDEX_NAME);

        if (date == null) {
            indexName.append(DateUtil.toString(new Date(), "yyyy-MM-dd"));
        } else {
            indexName.append(date);
        }

        //根据参数查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("appName", appName));
        boolQueryBuilder.must(QueryBuilders.termQuery("instance", instances));

        //分页参数
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

        //排序
        SortBuilder sortBuilder = SortBuilders.fieldSort("logTime");
        sortBuilder.order(SortOrder.ASC);

        NativeSearchQueryBuilder searchQuery = new NativeSearchQueryBuilder();
        searchQuery.withIndices(indexName.toString())
                .withTypes(EsSettings.LOG_TYPE_NAME)
                .withPageable(pageable)
                .withQuery(boolQueryBuilder)
                .withSort(sortBuilder);

        Page<EsLog> page = null;
        try {
            page = elasticsearchTemplate.queryForPage(searchQuery.build(), EsLog.class);
        } catch (Exception e) {
            logger.error("读取ES日志异常:", e);
            throw e;
        }
        List<String> logList = new ArrayList<>();
        for (EsLog esLog : page.getContent()) {
            StringBuffer logStr = new StringBuffer();
            logStr.append(DateUtil.toString(esLog.getLogTime(), "yyyy-MM-dd HH:mm:ss,SSS") + " ");
            logStr.append(esLog.getLevel() + " ");
            logStr.append("[" + esLog.getThreadName() + "] ");
            logStr.append(esLog.getLoggerName() + " : ");
            logStr.append(esLog.getMessage());
            logList.add(logStr.toString());
        }

        WebPage<String> esLogPage = new WebPage<>();
        esLogPage.setPageNo(Integer.valueOf(pageNo).longValue());
        esLogPage.setPageSize(Integer.valueOf(pageSize).longValue());
        esLogPage.setDataTotal(page.getTotalElements());
        esLogPage.setData(logList);

        return esLogPage;
    }

}
