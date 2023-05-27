package com.enation.app.javashop.service.goods.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.mapper.goods.SpecValuesMapper;
import com.enation.app.javashop.model.errorcode.GoodsErrorCode;
import com.enation.app.javashop.model.goods.dos.SpecValuesDO;
import com.enation.app.javashop.model.goods.dos.SpecificationDO;
import com.enation.app.javashop.model.goods.enums.Permission;
import com.enation.app.javashop.service.goods.SpecValuesManager;
import com.enation.app.javashop.service.goods.SpecificationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 规格值业务类
 *
 * @author fk
 * @version v2.0
 * @since v7.0.0
 * 2018-03-20 10:23:53
 */
@Service
public class SpecValuesManagerImpl implements SpecValuesManager {

    @Autowired
    private SpecValuesMapper specValuesMapper;
    @Autowired
    private SpecificationManager specificationManager;

    @Override
    @Transactional(value = "goodsTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public SpecValuesDO add(SpecValuesDO specValues) {

        this.specValuesMapper.insert(specValues);

        return specValues;
    }

    @Override
    @Transactional(value = "goodsTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public SpecValuesDO edit(SpecValuesDO specValues, Long id) {

        specValues.setSpecId(id);
        this.specValuesMapper.updateById(specValues);
        return specValues;
    }

    @Override
    public SpecValuesDO getModel(Long id) {
        return this.specValuesMapper.selectById(id);
    }

    @Override
    public List<SpecValuesDO> listBySpecId(Long specId, Permission permission) {

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("spec_id",specId);

        //商家或者后台调用
        if (Permission.ADMIN.equals(permission)) {
            queryWrapper.eq("seller_id",0);
        }

        List<SpecValuesDO> list = this.specValuesMapper.selectList(queryWrapper);

        return list;
    }

    @Override
    @Transactional(value = "goodsTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<SpecValuesDO> saveSpecValue(Long specId, String[] valueList) {

        //查询规格是否存在
        SpecificationDO spec = specificationManager.getModel(specId);
        if (spec == null) {
            throw new ServiceException(GoodsErrorCode.E306.code(), "所属规格不存在");
        }

        this.specValuesMapper.delete(new QueryWrapper<SpecValuesDO>()
                .eq("spec_id",specId)
                .eq("seller_id",0));

        List<SpecValuesDO> res = new ArrayList<>();
        for (String value : valueList) {
            if (value.length() > 50) {
                throw new ServiceException(GoodsErrorCode.E305.code(), "规格值为1到50个字符之间");
            }
            SpecValuesDO specValue = new SpecValuesDO(specId, value, 0L);
            specValue.setSpecName(spec.getSpecName());
            this.specValuesMapper.insert(specValue);
            res.add(specValue);
        }
        return res;

    }
}
