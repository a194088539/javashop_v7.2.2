package com.enation.app.javashop.mapper.system;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.pagedata.ArticleCategory;
import com.enation.app.javashop.model.pagedata.vo.ArticleCategoryVO;
import com.enation.app.javashop.model.pagedata.vo.ArticleVO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 文章分类的Mapper
 * @author zhanghao
 * @version v1.0
 * @since v7.2.2
 * 2020/7/21
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface ArticleCategoryMapper extends BaseMapper<ArticleCategory> {

    /**
     * 查询某个分类及相应的文章
     * @param categoryType 分类类型：帮助中心，商城公告，固定位置，商城促销，其他
     * @return ArticleCategoryVO
     */
    ArticleCategoryVO queryForVo(@Param("categoryType")String categoryType);

    /**
     * 获取文章分类树
     * @return ArticleCategoryVO
     */
    List<ArticleCategoryVO> queryForAsc();


}
