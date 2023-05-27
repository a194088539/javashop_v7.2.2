package com.enation.app.javashop.mapper.member;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.member.dos.AskReplyDO;
import com.enation.app.javashop.model.member.dto.ReplyQueryParam;
import com.enation.app.javashop.model.member.vo.AskReplyVO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 会员商品咨询回复Mapper接口
 * @author duanmingyu
 * @version v1.0
 * @since v7.2.2
 * 2020-07-22
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface AskReplyMapper extends BaseMapper<AskReplyDO> {

    /**
     * 获取会员问题咨询回复列表
     * @param page 分页数据
     * @param param 查询条件
     * @param isDel 删除状态
     * @return
     */
    IPage<AskReplyVO> listMemberReply(Page page, @Param("param") ReplyQueryParam param, @Param("is_del") String isDel);

    /**
     * 获取会员商品咨询最新一条回复
     * @param param 查询条件
     * @return
     */
    AskReplyDO getNewestModel(@Param("param") Map param);
}
