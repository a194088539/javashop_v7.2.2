package com.enation.app.javashop.service.goods.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.goods.ParameterGroupMapper;
import com.enation.app.javashop.mapper.goods.ParametersMapper;
import com.enation.app.javashop.model.errorcode.GoodsErrorCode;
import com.enation.app.javashop.model.goods.dos.CategoryDO;
import com.enation.app.javashop.model.goods.dos.ParameterGroupDO;
import com.enation.app.javashop.model.goods.dos.ParametersDO;
import com.enation.app.javashop.model.goods.vo.ParameterGroupVO;
import com.enation.app.javashop.service.goods.CategoryManager;
import com.enation.app.javashop.service.goods.ParameterGroupManager;
import com.enation.app.javashop.service.goods.ParametersManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 参数组业务类
 *
 * @author fk
 * @version v2.0
 * @since v7.0.0 2018-03-20 16:14:17
 */
@Service
public class ParameterGroupManagerImpl implements ParameterGroupManager {

    @Autowired
    private ParameterGroupMapper parameterGroupMapper;
    @Autowired
    private ParametersMapper parametersMapper;

    @Autowired
    private ParametersManager parametersManager;
    @Autowired
    private CategoryManager categoryManager;

    @Override
    public WebPage list(long pageNo, long pageSize) {

        IPage<ParameterGroupDO> page = parameterGroupMapper.selectPage(new Page<>(pageNo, pageSize),new QueryWrapper<>());

        return PageConvert.convert(page);
    }

    @Override
    @Transactional(value = "goodsTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ParameterGroupDO add(ParameterGroupDO parameterGroup) {

        // 查看分类是否存在
        CategoryDO category = categoryManager.getModel(parameterGroup.getCategoryId());
        if (category == null) {
            throw new ServiceException(GoodsErrorCode.E304.code(), "关联分类不存在");
        }
        ParameterGroupDO grouptmp = this.parameterGroupMapper.selectOne(new QueryWrapper<ParameterGroupDO>()
                .eq("category_id",parameterGroup.getCategoryId())
                .orderByDesc("sort")
                .last("limit 1"));

        if (grouptmp == null) {
            parameterGroup.setSort(1);
        } else {
            parameterGroup.setSort(grouptmp.getSort() + 1);
        }

        this.parameterGroupMapper.insert(parameterGroup);
        return parameterGroup;
    }

    @Override
    @Transactional(value = "goodsTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ParameterGroupDO edit(String groupName, Long id) {
        ParameterGroupDO group = this.getModel(id);
        if (group == null) {
            throw new ServiceException(GoodsErrorCode.E304.code(), "参数组不存在");
        }
        group.setGroupName(groupName);
        // 更新
        this.parameterGroupMapper.updateById(group);
        return group;
    }

    @Override
    @Transactional(value = "goodsTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(Long id) {
        this.parameterGroupMapper.deleteById(id);
        // 删除参数组，需要将参数组下的参数同时删除
        parametersManager.deleteByGroup(id);
    }

    @Override
    public ParameterGroupDO getModel(Long id) {
        return this.parameterGroupMapper.selectById(id);
    }

    @Override
    public List<ParameterGroupVO> getParamsByCategory(Long categoryId) {

		/* 查询参数组 */
//        String sql = "select * from es_parameter_group where category_id = ? order by sort asc";
        List<ParameterGroupDO> groupList = this.parameterGroupMapper.selectList(new QueryWrapper<ParameterGroupDO>()
                .eq("category_id",categoryId)
                .orderByAsc("sort"));

//        sql = "select p.param_id,p.param_name,p.param_type,p.`options`,p.required,p.group_id,p.is_index "
//                + "from es_parameters p where p.category_id = ? order by sort asc";

        List<ParametersDO> paramList = parametersMapper.selectList(new QueryWrapper<ParametersDO>()
                .eq("category_id",categoryId)
                .orderByAsc("sort"));

//        List<ParametersDO> paramList = this.daoSupport.queryForList(sql, ParametersDO.class, categoryId);

        List<ParameterGroupVO> resList = this.convertParamList(groupList, paramList);

        return resList;
    }

    /**
     * 拼装参数组和参数的返回值
     *
     * @param groupList
     * @param paramList
     * @return
     */
    private List<ParameterGroupVO> convertParamList(List<ParameterGroupDO> groupList, List<ParametersDO> paramList) {
        Map<Long, List<ParametersDO>> map = new HashMap<>(paramList.size());
        for (ParametersDO param : paramList) {

            List<ParametersDO> list = map.get(param.getGroupId());
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(param);
            map.put(param.getGroupId(), list);
        }
        List<ParameterGroupVO> resList = new ArrayList<>();
        for (ParameterGroupDO group : groupList) {
            ParameterGroupVO groupVo = new ParameterGroupVO();
            groupVo.setGroupId(group.getGroupId());
            groupVo.setGroupName(group.getGroupName());
            groupVo.setParams(map.get(group.getGroupId()) == null ? new ArrayList<>() : map.get(group.getGroupId()));
            resList.add(groupVo);
        }
        return resList;
    }

    @Override
    public void groupSort(Long groupId, String sortType) {

        String sql = "";
        ParameterGroupDO curGroup = this.getModel(groupId);
        if (curGroup == null) {
            throw new ServiceException(GoodsErrorCode.E304.code(), "参数组不存在");
        }

        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("category_id",curGroup.getCategoryId());
        if ("up".equals(sortType)) {
            wrapper.lt("sort",curGroup.getSort());
            wrapper.orderByDesc("sort");
            //sql = "select * from es_parameter_group where sort<? and category_id=? order by sort desc limit 0,1";
        } else if ("down".equals(sortType)) {
            wrapper.gt("sort",curGroup.getSort());
            wrapper.orderByAsc("sort");
            //sql = "select * from es_parameter_group where sort>? and category_id=? order by sort asc limit 0,1";
        }
        wrapper.last("limit 0,1");
        ParameterGroupDO changeGroup = this.parameterGroupMapper.selectOne(wrapper);

        if(changeGroup != null){

            UpdateWrapper<ParameterGroupDO> updateWrapper = new UpdateWrapper<ParameterGroupDO>()
                    .set("sort", changeGroup.getSort())
                    .eq("group_id", curGroup.getGroupId());

            this.parameterGroupMapper.update(new ParameterGroupDO(),updateWrapper);

            updateWrapper.set("sort", curGroup.getSort())
                    .eq("group_id", changeGroup.getGroupId());
//            sql = "update es_parameter_group set sort = ? where group_id = ?";
//            this.daoSupport.execute(sql, changeGroup.getSort(), curGroup.getGroupId());
//            this.daoSupport.execute(sql, curGroup.getSort(), changeGroup.getGroupId());
            this.parameterGroupMapper.update(new ParameterGroupDO(),updateWrapper);
        }

    }
}
