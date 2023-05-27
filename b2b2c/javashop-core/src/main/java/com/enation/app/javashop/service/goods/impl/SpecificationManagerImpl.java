package com.enation.app.javashop.service.goods.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.security.model.Seller;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.framework.util.StringUtil;
import com.enation.app.javashop.mapper.goods.CategorySpecMapper;
import com.enation.app.javashop.mapper.goods.SpecValuesMapper;
import com.enation.app.javashop.mapper.goods.SpecificationMapper;
import com.enation.app.javashop.model.errorcode.GoodsErrorCode;
import com.enation.app.javashop.model.goods.dos.CategoryDO;
import com.enation.app.javashop.model.goods.dos.CategorySpecDO;
import com.enation.app.javashop.model.goods.dos.SpecValuesDO;
import com.enation.app.javashop.model.goods.dos.SpecificationDO;
import com.enation.app.javashop.model.goods.vo.SelectVO;
import com.enation.app.javashop.model.goods.vo.SpecificationVO;
import com.enation.app.javashop.service.goods.CategoryManager;
import com.enation.app.javashop.service.goods.SpecificationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 规格项业务类
 *
 * @author fk
 * @version v2.0
 * @since v7.0.0
 * 2018-03-20 09:31:27
 */
@Service
public class SpecificationManagerImpl implements SpecificationManager {

    @Autowired
    private CategoryManager categoryManager;
    @Autowired
    private SpecificationMapper specificationMapper;
    @Autowired
    private CategorySpecMapper categorySpecMapper;
    @Autowired
    private SpecValuesMapper specValuesMapper;

    @Override
    public WebPage list(long page, long pageSize, String keyword) {

        IPage iPage = this.specificationMapper.selectPage(new Page<>(page, pageSize),
                new QueryWrapper<SpecificationDO>()
                        .eq("disabled", 1)
                        .eq("seller_id", 0)
                        //如果查询关键字不为空
                        .like(StringUtil.notEmpty(keyword), "spec_name", keyword)
                        .orderByDesc("spec_id"));

        return PageConvert.convert(iPage);
    }

    @Override
    @Transactional(value = "goodsTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public SpecificationDO add(SpecificationDO specification) {


        //如果是管理端添加的规格，则验证管理端的对个名称是否重复
        if (specification.getSellerId() == 0) {
            this.checkSpecName(specification.getSpecName(), null);
        }

        specification.setDisabled(1);
        this.specificationMapper.insert(specification);

        return specification;
    }

    @Override
    @Transactional(value = "goodsTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public SpecificationDO edit(SpecificationDO specification, Long id) {

        SpecificationDO model = this.getModel(id);
        if (model == null) {
            throw new ServiceException(GoodsErrorCode.E305.code(), "规格不存在");
        }

        //验证规格名称是否重复
        this.checkSpecName(specification.getSpecName(), id);

        specification.setSpecId(id);
        this.specificationMapper.updateById(specification);
        return specification;
    }


    /**
     * 验证规格名称是否重复
     *
     * @param specName
     * @param specId
     */
    private void checkSpecName(String specName, Long specId) {

        QueryWrapper<SpecificationDO> wrapper = new QueryWrapper<SpecificationDO>()
                .eq("disabled", 1)
                .eq("seller_id", 0)
                .eq("spec_name", specName)
                .ne(specId != null, "spec_id", specId);

        Integer count = this.specificationMapper.selectCount(wrapper);
        if (count > 0) {
            throw new ServiceException(GoodsErrorCode.E305.code(), "规格名称重复");
        }
    }


    @Override
    @Transactional(value = "goodsTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(Long[] ids) {

        //查看是否已经有分类绑定了该规格
        List<CategorySpecDO> list = this.categorySpecMapper.selectList(new QueryWrapper<CategorySpecDO>()
                .in("spec_id", Arrays.asList(ids)));

        if (list.size() > 0) {

            throw new ServiceException(GoodsErrorCode.E305.code(), "有分类已经绑定要删除的规格，请先解绑分类规格");
        }

        this.specificationMapper.update(new SpecificationDO(), new UpdateWrapper<SpecificationDO>()
                .set("disabled", 0)
                .in("spec_id", Arrays.asList(ids)));
    }

    @Override
    public SpecificationDO getModel(Long id) {

        return this.specificationMapper.selectById(id);
    }

    @Override
    public List<SelectVO> getCatSpecification(Long categoryId) {

        List<SelectVO> selectVOS = specificationMapper.getCatSpecification(categoryId);

        if (!selectVOS.isEmpty()) {
            for (SelectVO selectVO : selectVOS) {
                if (selectVO.getFalgid() != null && categoryId.toString().equals(selectVO.getFalgid())) {
                    selectVO.setSelected(true);
                } else {
                    selectVO.setSelected(false);
                }
            }
        }
        return selectVOS;
    }

    @Override
    @Transactional(value = "goodsTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public SpecificationDO addSellerSpec(Long categoryId, String specName) {

        CategoryDO category = categoryManager.getModel(categoryId);
        if (category == null) {
            throw new ServiceException(GoodsErrorCode.E305.code(), "分类不存在");
        }

        //商家添加规格，则验证当前这个分类下是否有重复的规格
        Seller seller = UserContext.getSeller();

        Map map = new HashMap<>();
        map.put("category_id", categoryId);
        map.put("seller_id", seller.getSellerId());
        map.put("spec_name", specName);
        Integer count = specificationMapper.checkSellerSpecName(map);

        if (count > 0) {
            throw new ServiceException(GoodsErrorCode.E305.code(), "规格名称重复");
        }

        SpecificationDO specification = new SpecificationDO(specName, 1, "商家自定义", seller.getSellerId());
        specification = this.add(specification);

        //保存分类规格的关系
        CategorySpecDO categorySpec = new CategorySpecDO(categoryId, specification.getSpecId());
        this.categorySpecMapper.insert(categorySpec);

        return specification;
    }

    @Override
    public List<SpecificationVO> querySellerSpec(Long categoryId) {
        Seller seller = UserContext.getSeller();
        //查询规格
        List<SpecificationVO> specList = specificationMapper.queryCatSpec(categoryId, seller.getSellerId());
        //没有规格
        if (specList == null || specList.size() == 0) {
            return new ArrayList<>();
        }
        //封装规格id的集合
        List<Long> specIdList = new ArrayList<>();

        for (int i = 0; i < specList.size(); i++) {
            specIdList.add(specList.get(i).getSpecId());
        }

        //查询到的是所有规格的规格值
        List<SpecValuesDO> valueList = specValuesMapper.selectList(new QueryWrapper<SpecValuesDO>()
                .in("spec_id", specIdList)
                .and(e -> {
                    e.eq("seller_id", 0).or().eq("seller_id", seller.getSellerId());
                }));

        Map<Long, List<SpecValuesDO>> map = new HashMap<>(valueList.size());
        for (SpecValuesDO specValue : valueList) {

            List<SpecValuesDO> list = map.get(specValue.getSpecId());
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(specValue);
            map.put(specValue.getSpecId(), list);
        }
        //赋值规格值
        for (SpecificationVO vo : specList) {
            vo.setValueList(map.get(vo.getSpecId()));
        }

        return specList;

    }

    @Override
    public boolean flagSellerSpec(Long sellerId, Long specId, String specValue) {
        if (sellerId == null || specId == null || specValue == null) {
            return true;
        }
        Integer count = this.specValuesMapper.selectCount(new QueryWrapper<SpecValuesDO>()
                .eq("seller_id", sellerId)
                .eq("spec_id", specId)
                .eq("spec_value", specValue)
                .last("limit 1"));
        if (count <= 0) {
            return false;
        }
        return true;
    }
}
