package com.enation.app.javashop.mapper.member;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.member.dos.ReceiptHistory;
import com.enation.app.javashop.model.member.vo.ReceiptHistoryVO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

/**
 * 会员开票历史记录Mapper接口
 * @author duanmingyu
 * @version v1.0
 * @since v7.2.2
 * 2020-07-27
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface ReceiptHistoryMapper extends BaseMapper<ReceiptHistory> {

    /**
     * 查询会员开票历史分页记录
     * @param page 分页信息
     * @param wrapper 查询条件构造器
     * @return
     */
    IPage<ReceiptHistoryVO> selectPageVo(Page page, @Param("ew") QueryWrapper<ReceiptHistoryVO> wrapper);

}
