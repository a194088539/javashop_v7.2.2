package com.enation.app.javashop.framework.elasticsearch;

import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

/**
 * Created by kingapex on 2019-02-13.
 *
 * @author kingapex
 * @version 1.0
 * @since 7.1.0
 * 2019-02-13
 */
public interface EsTemplateBuilder {

     ElasticsearchTemplate build();


}
