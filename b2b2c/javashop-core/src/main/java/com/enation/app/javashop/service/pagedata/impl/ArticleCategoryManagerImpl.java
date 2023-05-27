package com.enation.app.javashop.service.pagedata.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.system.ArticleMapper;
import com.enation.app.javashop.mapper.system.ArticleCategoryMapper;
import com.enation.app.javashop.model.pagedata.Article;
import com.enation.app.javashop.model.pagedata.ArticleCategory;
import com.enation.app.javashop.model.pagedata.enums.ArticleCategoryType;
import com.enation.app.javashop.model.pagedata.vo.ArticleCategoryVO;
import com.enation.app.javashop.model.pagedata.vo.ArticleVO;
import com.enation.app.javashop.service.pagedata.ArticleCategoryManager;
import com.enation.app.javashop.model.errorcode.SystemErrorCode;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.util.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文章分类业务类
 *
 * @author fk
 * @version v1.0
 * @since v7.0.0
 * 2018-06-11 15:01:32
 */
@Service
public class ArticleCategoryManagerImpl implements ArticleCategoryManager {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleCategoryMapper articleCategoryMapper;

    @Override
    public WebPage list(long page, long pageSize, String name) {

        QueryWrapper<ArticleCategory> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id",0);
        wrapper.like(!StringUtil.isEmpty(name),"name",name);
        wrapper.orderByDesc("allow_delete");
        IPage<ArticleCategory> iPage = articleCategoryMapper.selectPage(new Page<>(page,pageSize), wrapper);
        return PageConvert.convert(iPage);
    }

    @Override
    @Transactional(value = "systemTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ArticleCategory add(ArticleCategory articleCategory) {

        if (articleCategory.getParentId() == null) {
            articleCategory.setParentId(0L);
        }
        // 非顶级分类
        ArticleCategory parent = null;
        if (articleCategory.getParentId() != 0) {
            parent = this.getModel(articleCategory.getParentId());
            if (parent == null) {
                throw new ServiceException(SystemErrorCode.E951.code(), "父分类不存在");
            }
            // 替换catPath 根据path规则来匹配级别
            String catPath = parent.getPath().replace("|", ",");
            String[] str = catPath.split(",");
            if (str.length >= 3) {
                throw new ServiceException(SystemErrorCode.E951.code(), "最多为二级分类,添加失败");
            }
        }
        articleCategory.setAllowDelete(1);
        articleCategory.setType(ArticleCategoryType.OTHER.name());
        //验证分类名称是否重复
        QueryWrapper<ArticleCategory> wrapper = new QueryWrapper<>();
        wrapper.eq("name", articleCategory.getName());
        List list = articleCategoryMapper.selectList(wrapper);

        if (list.size() > 0) {
            throw new ServiceException(SystemErrorCode.E951.code(), "分类名称重复");
        }

        articleCategoryMapper.insert(articleCategory);
        long categoryId = articleCategory.getId();
        articleCategory.setId(categoryId);

        if (parent != null) {
            articleCategory.setPath(parent.getPath() + categoryId + "|");
        } else {// 是顶级类别
            articleCategory.setPath("0|" + categoryId + "|");
        }

        articleCategory.setPath(articleCategory.getPath());
        articleCategoryMapper.updateById(articleCategory);

        return articleCategory;
    }

    @Override
    @Transactional(value = "systemTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ArticleCategory edit(ArticleCategory articleCategory, Long id) {

        ArticleCategory cat = this.getModel(id);
        //只有类型为other的,才可以修改
        if (!ArticleCategoryType.OTHER.name().equals(cat.getType())) {
            throw new ServiceException(SystemErrorCode.E950.code(), "特殊的文章分类，不可修改");
        }

        if (articleCategory.getParentId() == null) {
            articleCategory.setParentId(0L);
        }

        articleCategory.setPath(0 + "|" + cat.getId() + "|");

        // 非顶级分类
        if (articleCategory.getParentId() != 0) {
            ArticleCategory parent = this.getModel(articleCategory.getParentId());
            if (parent == null) {
                throw new ServiceException(SystemErrorCode.E951.code(), "父分类不存在");
            }
            // 替换catPath 根据path规则来匹配级别
            String catPath = parent.getPath().replace("|", ",");
            String[] str = catPath.split(",");
            if (str.length >= 3) {
                throw new ServiceException(SystemErrorCode.E951.code(), "最多为二级分类,修改失败");
            }

            articleCategory.setPath(parent.getPath() + cat.getId() + "|");
        }

        articleCategory.setAllowDelete(1);
        articleCategory.setType(ArticleCategoryType.OTHER.name());

        //验证分类名称是否重复
        QueryWrapper<ArticleCategory> wrapper = new QueryWrapper<>();
        wrapper.eq("name", articleCategory.getName()).ne("id", id);
        List list = articleCategoryMapper.selectList(wrapper);

        if (list.size() > 0) {
            throw new ServiceException(SystemErrorCode.E951.code(), "分类名称重复");
        }

        articleCategoryMapper.updateById(articleCategory);

        return articleCategory;
    }

    @Override
    @Transactional(value = "systemTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(Long id) {
        ArticleCategory cat = this.getModel(id);
        //只有类型为other的,才可以删除
        if (cat == null || !ArticleCategoryType.OTHER.name().equals(cat.getType())) {
            throw new ServiceException(SystemErrorCode.E950.code(), "特殊的文章分类，不可删除");
        }
        //查看文章分类下是否有分类
        List<ArticleCategory> children = this.listChildren(id);
        if (children.size() > 0) {
            throw new ServiceException(SystemErrorCode.E950.code(), "该文章分类下存在子分类，不能删除");
        }

        //查看文章分类下是否有文章，如果有文章存在则不能删除该分类
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.eq("category_id", id);
        List list = articleMapper.selectList(wrapper);

        if (list.size() > 0) {
            throw new ServiceException(SystemErrorCode.E950.code(), "该文章分类下存在文章，不能删除");
        }
        articleCategoryMapper.deleteById(id);
    }

    @Override
    public ArticleCategory getModel(Long id) {
        return articleCategoryMapper.selectById(id);
    }


    @Override
    public List<ArticleCategory> listChildren(Long id) {

        QueryWrapper<ArticleCategory> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", id).orderByDesc("sort");
        return articleCategoryMapper.selectList(wrapper);

    }

    @Override
    public ArticleCategoryVO getCategoryAndArticle(String categoryType) {

        //顶级分类
        ArticleCategoryVO articleCategory = articleCategoryMapper.queryForVo(categoryType);

        List<ArticleCategory> list = this.listChildren(articleCategory.getId());
        //子分类
        List<ArticleCategoryVO> children = new ArrayList<>();
        Long[] catIds = null;
        if (StringUtil.isNotEmpty(list)) {
            catIds = new Long[list.size()];
            int i = 0;
            for (ArticleCategory cat : list) {
                ArticleCategoryVO catVO = new ArticleCategoryVO();
                BeanUtils.copyProperties(cat, catVO);
                children.add(catVO);
                catIds[i] = cat.getId();
                i++;
            }
        }
        //分类下的文章
        if (catIds != null) {

            List<ArticleVO> articleList = articleMapper.queryForCatIds(catIds);

//            List<Object> terms = new ArrayList<>();
//            String str = SqlUtil.getInSql(catIds, terms);
//            sql = "select * from es_article where category_id in (" + str + ") order  by sort";
//            List<ArticleVO> articleList = this.daoSupport.queryForList(sql, ArticleVO.class, terms.toArray());

            if (StringUtil.isNotEmpty(articleList)) {
                Map<Long, List<ArticleVO>> map = new HashMap<>(16);
                for (ArticleVO article : articleList) {
                    List<ArticleVO> values = map.get(article.getCategoryId());
                    if (values == null) {
                        values = new ArrayList<>();
                    }
                    values.add(article);
                    map.put(article.getCategoryId(), values);
                }

                for (ArticleCategoryVO cat : children) {
                    cat.setArticles(map.get(cat.getId()));
                }

            }

        }

        articleCategory.setChildren(children);

        return articleCategory;
    }

    @Override
    public ArticleCategory getCategoryByCategoryType(String categoryType) {

        QueryWrapper<ArticleCategory> wrapper = new QueryWrapper<>();
        wrapper.eq("type", categoryType);
        List<ArticleCategory> list = articleCategoryMapper.selectList(wrapper);

        if (StringUtil.isNotEmpty(list)) {
            return list.get(0);
        }

        return null;
    }


    @Override
    public List<ArticleCategoryVO> getCategoryTree() {

        List<ArticleCategoryVO> categoryList = articleCategoryMapper.queryForAsc();

        List<ArticleCategoryVO> newCategoryList = new ArrayList<ArticleCategoryVO>();
        for (ArticleCategoryVO category : categoryList) {
            if (category.getParentId() == 0) {
                List<ArticleCategoryVO> children = this.getChildren(categoryList, category.getId());
                category.setChildren(children);
                newCategoryList.add(category);
            }
        }
        return newCategoryList;
    }

    /**
     * 在一个集合中查找子
     *
     * @param categoryList 所有分类集合
     * @param parentid     父id
     * @return 找到的子集合
     */
    private List<ArticleCategoryVO> getChildren(List<ArticleCategoryVO> categoryList, Long parentid) {
        List<ArticleCategoryVO> children = new ArrayList<ArticleCategoryVO>();
        for (ArticleCategoryVO category : categoryList) {
            if (category.getParentId().compareTo(parentid) == 0) {
                category.setChildren(this.getChildren(categoryList, category.getId()));
                children.add(category);
            }
        }
        return children;
    }
}
