package com.enation.app.javashop.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.enation.app.javashop.client.trade.OrderClient;
import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.exception.ResourceNotFoundException;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.exception.SystemErrorCodeV1;
import com.enation.app.javashop.framework.util.StringUtil;
import com.enation.app.javashop.mapper.system.WayBillMapper;
import com.enation.app.javashop.model.base.CachePrefix;
import com.enation.app.javashop.model.base.vo.ConfigItem;
import com.enation.app.javashop.model.errorcode.SystemErrorCode;
import com.enation.app.javashop.model.system.dos.WayBillDO;
import com.enation.app.javashop.model.system.vo.WayBillVO;
import com.enation.app.javashop.model.trade.order.dto.OrderDetailDTO;
import com.enation.app.javashop.model.trade.order.enums.ShipStatusEnum;
import com.enation.app.javashop.service.base.plugin.waybill.WayBillPlugin;
import com.enation.app.javashop.service.system.WaybillManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 电子面单业务类
 *
 * @author zh
 * @version v7.0.0
 * @since v7.0.0
 * 2018-06-08 16:26:05
 */
@Service
public class WaybillManagerImpl implements WaybillManager {

    @Autowired
    private Cache cache;
    @Autowired
    private List<WayBillPlugin> wayBillPlugins;
    @Autowired
    private OrderClient orderclient;
    @Autowired
    private WayBillMapper wayBillMapper;

    @Override
    public WebPage list(long page, long pageSize) {
        List<WayBillVO> resultList = this.getWayBills();
        for (WayBillVO vo : resultList) {
            this.add(vo);
        }
        return new WebPage(page, Integer.valueOf(resultList.size()).longValue(), pageSize, resultList);
    }

    @Override
    @Transactional(value = "systemTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public WayBillDO add(WayBillVO wayBill) {
        WayBillDO wayBillDO = new WayBillDO(wayBill);
        if (wayBill.getId() == null || wayBill.getId()==0) {
            //查询此方案是否已经存在数据库中
            WayBillDO wb = this.getWayBillByBean(wayBillDO.getBean());
            if (wb != null) {
                throw new ServiceException(SystemErrorCode.E910.code(), "该电子面单方案已经存在");
            }

            wayBillMapper.insert(wayBillDO);
            Long waybillId = wayBillDO.getId();

            wayBillDO.setId(waybillId);
        }
        // 更新缓存
        cache.remove(CachePrefix.WAYBILL.getPrefix());
        return wayBillDO;
    }

    @Override
    @Transactional(value = "systemTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public WayBillVO edit(WayBillVO wayBill) {
        List<WayBillVO> vos = this.getWayBills();
        for (WayBillVO vo : vos) {
            this.add(vo);
        }
        WayBillDO way = this.getWayBillByBean(wayBill.getBean());
        if (way == null) {
            throw new ResourceNotFoundException("该电子面单方案不存在");
        }
        wayBill.setId(way.getId());
        wayBillMapper.updateById(new WayBillDO(wayBill));
        return wayBill;
    }


    @Override
    public WayBillDO getModel(Long id) {
        return wayBillMapper.selectById(id);
    }

    @Override
    public void open(String bean) {
        List<WayBillVO> vos = this.getWayBills();
        for (WayBillVO vo : vos) {
            this.add(vo);
        }
        WayBillDO wayBillDO = this.getWayBillByBean(bean);
        if (wayBillDO == null) {
            throw new ResourceNotFoundException("该电子面单方案不存在");
        }

        WayBillDO wayBillDo = new WayBillDO();
        UpdateWrapper<WayBillDO> wrapper = new UpdateWrapper<>();
        wayBillDo.setOpen(0);
        wayBillMapper.update(wayBillDo,wrapper);
        wrapper.eq("bean",bean);
        wayBillDo.setOpen(1);
        wayBillMapper.update(wayBillDo,wrapper);

        // 更新缓存
        cache.remove(CachePrefix.WAYBILL.getPrefix());
    }

    @Override
    public WayBillDO getWayBillByBean(String bean) {
        QueryWrapper<WayBillDO> wrapper = new QueryWrapper<>();
        wrapper.eq("bean", bean);
        return wayBillMapper.selectOne(wrapper);
    }


    /**
     * 获取所有的电子面单方案
     *
     * @return 所有的电子面单方案
     */
    private List<WayBillVO> getWayBills() {
        List<WayBillVO> resultList = new ArrayList<>();
        QueryWrapper<WayBillDO> wrapper = new QueryWrapper<>();
        List<WayBillDO> list = wayBillMapper.selectList(wrapper);
        Map<String, WayBillDO> map = new HashMap<>(16);
        for (WayBillDO wayBillDO : list) {
            map.put(wayBillDO.getBean(), wayBillDO);
        }
        for (WayBillPlugin plugin : wayBillPlugins) {
            WayBillDO wayBill = map.get(plugin.getPluginId());
            WayBillVO result = null;

            if (wayBill != null) {
                result = new WayBillVO(wayBill);
            } else {
                result = WayBillVoConverter.toValidatorPlatformVO(plugin);
            }

            resultList.add(result);
        }
        return resultList;
    }

    @Override
    public WayBillVO getWaybillConfig(String bean) {
        List<WayBillVO> vos = this.getWayBills();
        for (WayBillVO vo : vos) {
            this.add(vo);
        }
        WayBillDO wayBillDO = this.getWayBillByBean(bean);
        if (wayBillDO == null) {
            throw new ResourceNotFoundException("该电子面单方案不存在");
        }
        return new WayBillVO(wayBillDO);
    }

    @Override
    public String createPrintData(String orderSn, Long logisticsId) {
        OrderDetailDTO orderDetailDTO = orderclient.getModel(orderSn);
        if (orderDetailDTO == null) {
            throw new ResourceNotFoundException("订单无效");
        }
        if (!orderDetailDTO.getShipStatus().equals(ShipStatusEnum.SHIP_NO.value())) {
            throw new ServiceException(SystemErrorCodeV1.INVALID_REQUEST_PARAMETER, "已发货订单无法打印电子面单");
        }
        Object object = cache.get(CachePrefix.WAYBILL.getPrefix());
        WayBillDO wayBillDO = null;
        if (object != null) {
            wayBillDO = (WayBillDO) object;
        } else {

            QueryWrapper<WayBillDO> wrapper = new QueryWrapper<>();
            wrapper.eq("open", 1);
            wayBillDO = wayBillMapper.selectOne(wrapper);

            if (wayBillDO == null) {
                throw new ResourceNotFoundException("找不到可用的电子面单方案");
            }
            cache.put(CachePrefix.WAYBILL.getPrefix(), wayBillDO);
        }
        WayBillPlugin plugin = this.findByBean(wayBillDO.getBean());
        try {
            return plugin.createPrintData(orderSn, logisticsId);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new ServiceException(SystemErrorCode.E911.code(), "电子面单生成失败");
    }

    /**
     * 根据beanid获取电子面单方案
     *
     * @param bean 电子面单bean id
     * @return 电子面单插件
     */

    private WayBillPlugin findByBean(String bean) {
        for (WayBillPlugin wayBillPlugin : wayBillPlugins) {
            if (wayBillPlugin.getPluginId().equals(bean)) {
                return wayBillPlugin;
            }
        }
        //如果走到这里，说明找不到可用的电子面单方案
        throw new ResourceNotFoundException("未找到可用的电子面单方案");
    }


    /**
     * 获取存储方案配置
     *
     * @return
     */
    private Map getConfig() {
        WayBillDO wayBillDO = (WayBillDO) cache.get(CachePrefix.WAYBILL.getPrefix());
        if (StringUtil.isEmpty(wayBillDO.getConfig())) {
            return new HashMap<>(16);
        }
        Gson gson = new Gson();
        List<ConfigItem> list = gson.fromJson(wayBillDO.getConfig(), new TypeToken<List<ConfigItem>>() {
        }.getType());
        Map<String, String> result = new HashMap<>(16);
        if (list != null) {
            for (ConfigItem item : list) {
                result.put(item.getName(), item.getValue().toString());
            }
        }
        return result;
    }
}
