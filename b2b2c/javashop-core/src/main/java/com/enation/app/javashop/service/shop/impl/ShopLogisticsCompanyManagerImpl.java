package com.enation.app.javashop.service.shop.impl;

import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.enation.app.javashop.client.system.LogiCompanyClient;
import com.enation.app.javashop.mapper.member.ShopLogisticsSettingMapper;
import com.enation.app.javashop.model.shop.dos.ShopLogisticsSetting;
import com.enation.app.javashop.model.shop.vo.ShopLogisticsSettingVO;
import com.enation.app.javashop.model.system.dto.KDNParams;
import com.enation.app.javashop.framework.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.javashop.model.errorcode.ShopErrorCode;
import com.enation.app.javashop.model.system.dos.LogisticsCompanyDO;
import com.enation.app.javashop.service.shop.ShopLogisticsCompanyManager;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.security.model.Seller;

/**
 * 店铺物流公司管理类
 *
 * @author zhangjiping
 * @version v7.0.0
 * @since v7.0.0
 * 2018年3月29日 下午5:25:33
 */
@Service()
public class ShopLogisticsCompanyManagerImpl implements ShopLogisticsCompanyManager {

    @Autowired
    private ShopLogisticsSettingMapper shopLogisticsSettingMapper;
    @Autowired
    private LogiCompanyClient logiCompanyClient;


    @Override
    public List list() {
        //新建店铺物流配置集合
        List<ShopLogisticsSettingVO> shopLogisticsSettingVOS = new ArrayList<>();
        //获取物流公司信息集合
        List<LogisticsCompanyDO> list = logiCompanyClient.list();

        //获取当前登录的商家信息
        Seller seller = UserContext.getSeller();
        //新建查询条件包装器
        QueryWrapper wrapper = new QueryWrapper();
        //以店铺ID为查询条件
        wrapper.eq("shop_id", seller.getSellerId());
        //获取店铺物流配置集合信息
        List<ShopLogisticsSetting> shopLogisticsSettings = shopLogisticsSettingMapper.selectList(wrapper);
        //循环结果集
        for (LogisticsCompanyDO logisticsCompanyDO : list) {
            //新建店铺物流配置对象VO
            ShopLogisticsSettingVO shopLogisticsSettingVO = new ShopLogisticsSettingVO();
            //设置物流公司信息
            shopLogisticsSettingVO.setLogisticsCompanyDO(logisticsCompanyDO);
            //如果店铺物流配置集合信息不为空并且集合长度大于0
            if (shopLogisticsSettings != null && shopLogisticsSettings.size() > 0) {
                //循环设置店铺物流配置信息
                for (ShopLogisticsSetting shopLogisticsSetting : shopLogisticsSettings) {
                    if (shopLogisticsSetting.getLogisticsId().equals(logisticsCompanyDO.getId())) {
                        shopLogisticsSettingVO.setShopLogisticsSetting(shopLogisticsSetting);
                    }
                }
            }
            shopLogisticsSettingVOS.add(shopLogisticsSettingVO);
        }
        return shopLogisticsSettingVOS;
    }

    /**
     * 开启某个物流
     *
     * @param logisticsId
     */
    @Override
    public void open(Long logisticsId) {
        //创建店铺物流设置对象
        ShopLogisticsSetting shopLogisticsSetting = new ShopLogisticsSetting();
        //设置物流id
        shopLogisticsSetting.setLogisticsId(logisticsId);
        //查询指定物流公司数量
        int count = this.count(shopLogisticsSetting.getLogisticsId(), UserContext.getSeller().getSellerId());
        if (count > 0) {
            throw new ServiceException(ShopErrorCode.E215.name(), "物流公司已开启");
        }
        //设置店铺ID为当前登录的商家店铺ID
        shopLogisticsSetting.setShopId(UserContext.getSeller().getSellerId());
        //入库操作
        shopLogisticsSettingMapper.insert(shopLogisticsSetting);
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void add(ShopLogisticsSetting shopLogisticsSetting) {
        //获取当前登录的商家信息
        Seller seller = UserContext.getSeller();
        //检测物流公司是否存在
        check(shopLogisticsSetting);
        //查询指定物流公司数量
        int count = this.count(shopLogisticsSetting.getLogisticsId(), seller.getSellerId());
        if (count > 0) {
            throw new ServiceException(ShopErrorCode.E215.name(), "物流公司已配置，请刷新页面");
        }
        //入库操作
        shopLogisticsSettingMapper.insert(shopLogisticsSetting);
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void edit(ShopLogisticsSetting shopLogisticsSetting, long id) {
        //获取当前登录的商家信息
        Seller seller = UserContext.getSeller();
        //检测物流公司是否存在
        check(shopLogisticsSetting);
        //查询指定物流公司数量
        int count = this.count(shopLogisticsSetting.getLogisticsId(), seller.getSellerId());
        //如果不存在就添加，存在则修改
        if (count == 0) {
            this.add(shopLogisticsSetting);
        } else {
            shopLogisticsSetting.setId(id);
            shopLogisticsSettingMapper.updateById(shopLogisticsSetting);
        }
    }

    /**
     * 检测物流公司是否存在
     * @param shopLogisticsSetting
     * @throws ServiceException
     */
    private void check(ShopLogisticsSetting shopLogisticsSetting) throws ServiceException {
        LogisticsCompanyDO model = logiCompanyClient.getModel(shopLogisticsSetting.getLogisticsId());
        if (model == null) {
            throw new ServiceException(ShopErrorCode.E214.name(), "物流公司不存在");
        }
    }

    @Override
    public void setting(KDNParams kdnParams, Long logisticsId) {
        //json转换
        String params = JsonUtil.objectToJson(kdnParams);
        //获取当前登录的商家信息
        Seller seller = UserContext.getSeller();
        //判断物流公司数量是否大于0
        if (this.count(logisticsId, seller.getSellerId()) > 0) {
            //新建修改条件包装器
            UpdateWrapper<ShopLogisticsSetting> wrapper = new UpdateWrapper();
            //修改配置信息
            wrapper.set("config", params);
            //以物流公司id为修改条件
            wrapper.eq("logistics_id", logisticsId);
            //以店铺ID为修改条件
            wrapper.eq("shop_id", seller.getSellerId());
            //修改店铺物流设置信息
            shopLogisticsSettingMapper.update(new ShopLogisticsSetting(), wrapper);
        }
    }


    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(Long logiId) {
        //获取当前登录的商家信息
        Seller seller = UserContext.getSeller();
        //获取物流公司数量
        int count = this.count(logiId, seller.getSellerId());
        if (count == 0) {
            throw new ServiceException(ShopErrorCode.E216.name(), "物流公司已关闭");
        }
        //新建删除条件包装器
        QueryWrapper<ShopLogisticsSetting> wrapper = new QueryWrapper<>();
        //以店铺ID为删除条件
        wrapper.eq("shop_id", seller.getSellerId());
        //以物流公司ID为删除条件
        wrapper.eq("logistics_id", logiId);
        //删除店铺物流公司设置
        shopLogisticsSettingMapper.delete(wrapper);
    }

    @Override
    public ShopLogisticsSetting query(Long logisticsId, Long sellerId) {
        //新建查询条件包装器
        QueryWrapper<ShopLogisticsSetting> wrapper = new QueryWrapper<>();
        //以店铺ID为查询条件
        wrapper.eq("shop_id", sellerId);
        //以物流公司ID为查询条件
        wrapper.eq("logistics_id", logisticsId);
        //查询一条店铺物流设置信息
        ShopLogisticsSetting shopLogisticsSetting = shopLogisticsSettingMapper.selectOne(wrapper);
        return shopLogisticsSetting;
    }

    @Override
    public List queryListByLogisticsId(Long logisticsId) {
        //新建查询条件包装器
        QueryWrapper<ShopLogisticsSetting> wrapper = new QueryWrapper<>();
        //以物流公司ID为查询条件
        wrapper.eq("logistics_id", logisticsId);
        return shopLogisticsSettingMapper.selectList(wrapper);
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteByLogisticsId(Long logisticsId) {
        //新建删除条件包装器
        QueryWrapper<ShopLogisticsSetting> wrapper = new QueryWrapper<>();
        //以物流公司ID为删除条件
        wrapper.eq("logistics_id", logisticsId);
        //删除操作
        shopLogisticsSettingMapper.delete(wrapper);
    }

    /**
     * 查询指定物流公司
     *
     * @param logiId 物流公司ID
     * @param shopId 店铺ID
     * @return
     */
    private Integer count(Long logiId, Long shopId) {
        //新建删除条件包装器
        QueryWrapper<ShopLogisticsSetting> wrapper = new QueryWrapper<>();
        //以店铺ID为查询条件
        wrapper.eq("shop_id", shopId);
        //以物流公司ID为查询条件
        wrapper.eq("logistics_id", logiId);
        //查询物流公司信息数量
        Integer count = shopLogisticsSettingMapper.selectCount(wrapper);
        return count;
    }

}
