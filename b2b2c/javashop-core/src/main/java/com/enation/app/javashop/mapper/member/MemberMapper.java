package com.enation.app.javashop.mapper.member;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.member.dos.Member;
import com.enation.app.javashop.model.member.vo.BackendMemberVO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会员Mapper接口
 * @author duanmingyu
 * @version v1.0
 * @since v7.2.2
 * 2020-07-27
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface MemberMapper extends BaseMapper<Member> {

    /**
     * 根据用户名获取会员信息
     * @param name 用户名
     * @return
     */
    Member getMemberByUname(@Param("name") String name);

    /**
     * 查询一定数量会员信息集合
     * @param num 查询数量
     * @return
     */
    List<BackendMemberVO> selectNewMember(@Param("num") Integer num);

}
