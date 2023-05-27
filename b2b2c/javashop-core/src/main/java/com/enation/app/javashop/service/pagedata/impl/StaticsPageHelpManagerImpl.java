package com.enation.app.javashop.service.pagedata.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.mapper.system.ArticleMapper;
import com.enation.app.javashop.model.pagedata.Article;
import com.enation.app.javashop.model.pagedata.enums.ArticleShowPosition;
import com.enation.app.javashop.service.pagedata.StaticsPageHelpManager;
import com.enation.app.javashop.framework.database.DaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.data.elasticsearch.annotations.FieldType.keyword;

/**
 * 静态页面实现
 *
 * @author Chopper
 * @version v1.0
 * @since v7.0
 * 2018-07-17 下午3:27
 */
@Service
public class StaticsPageHelpManagerImpl implements StaticsPageHelpManager {

    @Autowired
    private ArticleMapper articleMapper;

    /**
     * 获取帮助页面总数
     * 不包含固定位置的文章
     *
     * @return
     */
    @Override
    public Integer count() {
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.eq("show_position",ArticleShowPosition.OTHER.name());
        return articleMapper.selectCount(wrapper);
    }

    /**
     * 分页获取帮助
     *
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public List helpList(Long page, Long pageSize) {
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.eq("show_position", ArticleShowPosition.OTHER.name());
        IPage<Article> iPage = articleMapper.selectPage(new Page<>(page,pageSize), wrapper);
        List<Article> articles = iPage.getRecords();
        List<Map> resultList = new ArrayList<>();
        for (Article article : articles) {
            Map map = new HashMap();
            map.put("id", article.getArticleId());
            resultList.add(map);
        }
        return resultList;

//        return this.daoSupport.queryForListPage("select article_id as id from es_article where show_position = ? ",
//        page,pageSize,ArticleShowPosition.OTHER.name());
    }
}
