package com.enation.app.javashop.service.promotion.groupbuy.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.promotion.groupbuy.GroupbuyCatMapper;
import com.enation.app.javashop.model.errorcode.PromotionErrorCode;
import com.enation.app.javashop.model.promotion.groupbuy.dos.GroupbuyCatDO;
import com.enation.app.javashop.service.promotion.groupbuy.GroupbuyCatManager;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.javashop.framework.database.WebPage;

import java.util.List;

/**
 * 团购分类业务类
 *
 * @author Snow
 * @version v7.0.0
 * @since v7.0.0
 * 2018-04-02 16:08:03
 */
@Service
public class GroupbuyCatManagerImpl implements GroupbuyCatManager {

    @Autowired
    private GroupbuyCatMapper groupbuyCatMapper;

    @Override
    public WebPage list(Long pageNo, Long pageSize) {
        //获取团购分类分页列表数据
        IPage<GroupbuyCatDO> iPage = groupbuyCatMapper.selectPage(new Page<>(pageNo, pageSize), new QueryWrapper<>());
        return PageConvert.convert(iPage);
    }

    @Override
    public List<GroupbuyCatDO> getList(Long parentId) {
        //新建查询条件包装器
        QueryWrapper<GroupbuyCatDO> wrapper = new QueryWrapper<>();
        //以分类父ID为条件查询
        wrapper.eq("parent_id", parentId);
        //以分类排序正序查询
        wrapper.orderByAsc("cat_order");
        //获取团购分类信息集合
        List<GroupbuyCatDO> list = groupbuyCatMapper.selectList(wrapper);
        return list;
    }

    @Override
    public GroupbuyCatDO add(GroupbuyCatDO groupbuyCat) {
        //新建查询条件包装器
        QueryWrapper<GroupbuyCatDO> wrapper = new QueryWrapper<>();
        //以分类名称为条件查询
        wrapper.eq("cat_name", groupbuyCat.getCatName());
        //获取团购分类信息集合
        List list = groupbuyCatMapper.selectList(wrapper);
        //如果结果集长度大于0，证明当前这个分类已存在
        if (list.size() > 0) {
            throw new ServiceException(PromotionErrorCode.E408.code(), "团购分类名称重复");
        }
        //如果分类父id为空，默认设置为0
        if (groupbuyCat.getParentId() == null) {
            groupbuyCat.setParentId(0L);
        }
        //团购分类信息入库
        groupbuyCatMapper.insert(groupbuyCat);
        return groupbuyCat;
    }

    @Override
    public GroupbuyCatDO edit(GroupbuyCatDO groupbuyCat, Long id) {
        //新建查询条件包装器
        QueryWrapper<GroupbuyCatDO> wrapper = new QueryWrapper<>();
        //以分类名称为条件查询
        wrapper.eq("cat_name", groupbuyCat.getCatName());
        //排除当前这个分类的ID
        wrapper.ne("cat_id", id);
        //获取团购分类信息集合
        List list = groupbuyCatMapper.selectList(wrapper);
        //如果结果集长度大于0，证明当前这个分类已存在
        if (list.size() > 0) {
            throw new ServiceException(PromotionErrorCode.E408.code(), "团购分类名称重复");
        }

        //设置主键ID
        groupbuyCat.setCatId(id);
        //修改团购分类信息
        groupbuyCatMapper.updateById(groupbuyCat);
        return groupbuyCat;
    }

    @Override
    public void delete(Long id) {
        //检查团购分类是否可以被删除
        if (!this.checkCat(id)) {
            throw new ServiceException(PromotionErrorCode.E408.code(), "当前有正在进行或还未开始的团购活动商品关联了此分类，不可删除");
        }
        //删除团购分类信息
        groupbuyCatMapper.deleteById(id);
    }

    @Override
    public GroupbuyCatDO getModel(Long id) {
        //根据ID获取团购分类信息并返回
        return groupbuyCatMapper.selectById(id);
    }

    /**
     * 检查团购分类是否可以被删除
     * @param catId 分类id
     * @return
     */
    private boolean checkCat(Long catId) {
        //查询团购分类已被团购活动或者团购商品占用的数量
        int count = groupbuyCatMapper.selectCatCount(catId, DateUtil.getDateline());
        boolean flag = count == 0 ? true : false;
        return  flag;
    }
}
