package com.enation.app.javashop.mapper.member;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.member.dos.MemberComment;
import com.enation.app.javashop.model.member.vo.CommentVO;
import com.enation.app.javashop.model.member.vo.GoodsGrade;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 会员评论Mapper接口
 * @author duanmingyu
 * @version v1.0
 * @since v7.2.2
 * 2020-07-22
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface MemberCommentMapper extends BaseMapper<MemberComment> {

    /**
     * 查询会员评论分页信息
     * @param page 分页参数
     * @param wrapper 查询条件构造器
     * @return
     */
    IPage<CommentVO> selectCommentVo(Page page, @Param("ew") QueryWrapper<CommentVO> wrapper);

    /**
     * 查询商品的好评比例
     * @param wrapper 查询条件构造器
     * @return
     */
    List<GoodsGrade> selectGoodsGrade(@Param("ew") QueryWrapper<GoodsGrade> wrapper);

    /**
     * 查询某个商品的相关评论数量
     * @param wrapper 查询条件构造器
     * @return
     */
    List<Map> selectGoodsGradeCount(QueryWrapper wrapper);

    /**
     * 查询会员评论集合
     * @param wrapper 查询条件构造器
     * @return
     */
    List<CommentVO> selectCommentVoList(@Param("ew") QueryWrapper<CommentVO> wrapper);
}
