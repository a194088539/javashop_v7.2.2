package com.enation.app.javashop.mapper.system;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.pagedata.Article;
import com.enation.app.javashop.model.pagedata.ArticleCategory;
import com.enation.app.javashop.model.pagedata.vo.ArticleDetail;
import com.enation.app.javashop.model.pagedata.vo.ArticleVO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 文章的Mapper
 * @author zhanghao
 * @version v1.0
 * @since v7.2.2
 * 2020/7/21
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface ArticleMapper extends BaseMapper<Article>{

     /**
      * 查询文章列表
      * @param page 页码和每页数量
      * @param name 文章名称
      * @param categoryId 文章分类
      * @return ArticleDetail
      */
     IPage<ArticleDetail> queryPageDetail(Page<ArticleDetail> page,
                                          @Param("name")String name,
                                          @Param("categoryId")Long categoryId,
                                          @Param("articleCategorys")List<ArticleCategory> articleCategorys);

     /**
      * 查询分类下的文章
      * @param catIds 分类下的文章
      * @return ArticleVO
      */
     List<ArticleVO> queryForCatIds(@Param("array") Long[] catIds);

     /**
      * 测试使用
      * @param params 参数
      * @return ArticleDetail
      */
     List<ArticleDetail> listDetail(@Param("params") Map params);
}
