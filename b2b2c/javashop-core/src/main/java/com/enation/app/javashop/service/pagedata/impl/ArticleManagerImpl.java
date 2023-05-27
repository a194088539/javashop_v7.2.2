package com.enation.app.javashop.service.pagedata.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.database.DaoSupport;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.framework.util.SqlUtil;
import com.enation.app.javashop.framework.util.StringUtil;
import com.enation.app.javashop.mapper.system.ArticleMapper;
import com.enation.app.javashop.mapper.system.ArticleCategoryMapper;
import com.enation.app.javashop.model.errorcode.SystemErrorCode;
import com.enation.app.javashop.model.pagedata.Article;
import com.enation.app.javashop.model.pagedata.ArticleCategory;
import com.enation.app.javashop.model.pagedata.enums.ArticleShowPosition;
import com.enation.app.javashop.model.pagedata.vo.ArticleDetail;
import com.enation.app.javashop.service.pagedata.ArticleCategoryManager;
import com.enation.app.javashop.service.pagedata.ArticleManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 文章业务类
 *
 * @author fk
 * @version v1.0
 * @since v7.0.0
 * 2018-06-12 10:43:18
 */
@Service
public class ArticleManagerImpl implements ArticleManager {

    @Autowired
    private ArticleCategoryManager articleCategoryManager;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleCategoryMapper articleCategoryMapper;


    @Override
    public WebPage list(long page, long pageSize, String name, Long categoryId) {

        QueryWrapper<ArticleCategory> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id",categoryId);
        List<ArticleCategory> articleCategorys = articleCategoryMapper.selectList(wrapper);

        IPage<ArticleDetail> iPage = articleMapper.queryPageDetail(new Page<>(page, pageSize), name, categoryId, articleCategorys);
        return PageConvert.convert(iPage);

//        StringBuffer sql = new StringBuffer("select a.*,ac.name category_name from es_article a left join es_article_category ac on a.category_id=ac.id  ");
//        List<Object> term = new ArrayList<>();
//
//        List<String> condition = new ArrayList<>();
//
//        if (!StringUtil.isEmpty(name)) {
//            condition.add(" article_name like ? ");
//            term.add("%" + name + "%");
//        }
//
//        if (categoryId != null) {
//
////            QueryWrapper<ArticleCategory> wrapper = new QueryWrapper<>();
////            wrapper.eq("parent_id",categoryId);
////            List<ArticleCategory> articleCategorys = articleCategoryMapper.selectList(wrapper);
//            List<ArticleCategory> articleCategorys = daoSupport.queryForList("select * from es_article_category where parent_id = ?", ArticleCategory.class, categoryId);
//            //如果是对底级分类查询 则只需要查询其分类就可以，否则需要将其下级分类数据全部查询出来
//            if (articleCategorys.size() <= 0) {
//                condition.add(" a.category_id = ?");
//                term.add(categoryId);
//
//            } else {
//                String symbol = "";
//                for (ArticleCategory articleCategory : articleCategorys) {
//                    term.add(articleCategory.getId());
//                    symbol += "?,";
//                }
//                symbol = symbol.substring(0, symbol.length() - 1);
//                condition.add(" a.category_id in (" + symbol + ")");
//
//            }
//        }
//
//
//        sql.append(SqlUtil.sqlSplicing(condition));
//        sql.append(" order by article_id desc");
//
//        WebPage webPage = this.daoSupport.queryForPage(sql.toString(), page, pageSize, ArticleDetail.class, term.toArray());
//
//        return webPage;

    }

    @Override
    @Transactional(value = "systemTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Article add(Article article) {

        article.setShowPosition(ArticleShowPosition.OTHER.name());
        article.setCreateTime(DateUtil.getDateline());
        article.setModifyTime(DateUtil.getDateline());
        articleMapper.insert(article);
        article.setArticleId(article.getArticleId());

        return article;
    }

    @Override
    @Transactional(value = "systemTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Article edit(Article article, Long id) {

        Article art = this.getModel(id);
        if (art == null) {
            throw new ServiceException(SystemErrorCode.E955.code(), "文章不存在，请正确操作");
        }
        article.setShowPosition(art.getShowPosition());
        article.setModifyTime(DateUtil.getDateline());
        articleMapper.updateById(article);

        return article;
    }

    @Override
    @Transactional(value = "systemTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(Long id) {

        Article article = this.getModel(id);
        if (article == null || !ArticleShowPosition.valueOf(article.getShowPosition()).equals(ArticleShowPosition.OTHER)) {
            throw new ServiceException(SystemErrorCode.E952.code(), "该文章不可删除，只可修改");
        }

        articleMapper.deleteById(article);

    }

    @Override
    public Article getModel(Long id) {
        return articleMapper.selectById(id);
    }

    @Override
    public List<Article> listByPosition(String position) {
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.eq("show_position", position).orderByDesc("sort");
        return articleMapper.selectList(wrapper);
    }

    @Override
    public List<Article> listByCategoryType(String categoryType) {

        ArticleCategory category = this.articleCategoryManager.getCategoryByCategoryType(categoryType);
        if (category == null) {
            return null;
        }

        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.eq("category_id", category.getId()).orderByDesc("sort");
        return articleMapper.selectList(wrapper);
    }
}
