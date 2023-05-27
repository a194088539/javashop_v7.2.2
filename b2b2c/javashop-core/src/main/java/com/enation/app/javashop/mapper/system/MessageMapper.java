package com.enation.app.javashop.mapper.system;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.system.dos.Message;
import com.enation.app.javashop.model.system.dto.MessageDTO;
import com.enation.app.javashop.model.system.dto.MessageQueryParam;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;


/**
 * 站内消息的Mapper
 * @author zhanghao
 * @version v1.0
 * @since v7.2.2
 * 2020/7/21
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface MessageMapper extends BaseMapper<Message> {

    /**
     * 查询站内消息列表
     * @param page 页码和每页数量
     * @param param 消息搜索参数实体
     * @return MessageDTO
     */
    IPage<MessageDTO> queryMessageDTOList(Page<MessageDTO> page, @Param("param") MessageQueryParam param);
}
