package com.enation.app.javashop.service.promotion.fulldiscount.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.promotion.fulldiscount.FullDiscountGiftMapper;
import com.enation.app.javashop.mapper.promotion.fulldiscount.FullDiscountMapper;
import com.enation.app.javashop.model.goods.enums.QuantityType;
import com.enation.app.javashop.model.errorcode.PromotionErrorCode;
import com.enation.app.javashop.model.promotion.fulldiscount.dos.FullDiscountDO;
import com.enation.app.javashop.model.promotion.fulldiscount.dos.FullDiscountGiftDO;
import com.enation.app.javashop.service.promotion.fulldiscount.FullDiscountGiftManager;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.exception.NoPermissionException;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.security.model.Seller;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.framework.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 满优惠赠品业务类
 *
 * @author Snow
 * @version v7.0.0
 * @since v7.0.0
 * 2018-03-30 17:34:46
 */
@Service
public class FullDiscountGiftManagerImpl implements FullDiscountGiftManager {

    @Autowired
    private FullDiscountGiftMapper fullDiscountGiftMapper;

    @Autowired
    private FullDiscountMapper fullDiscountMapper;

    @Override
    public WebPage list(long page, long pageSize, String keyWord) {
        //获取当前登录的商家店铺信息
        Seller seller = UserContext.getSeller();
        //新建查询条件包装器
        QueryWrapper<FullDiscountGiftDO> wrapper = new QueryWrapper<>();
        //以商家店铺ID为查询条件
        wrapper.eq("seller_id", seller.getSellerId());
        //如果查询关键字不为空，则以赠品名称为条件进行模糊查询
        wrapper.like(StringUtil.notEmpty(keyWord), "gift_name", keyWord);
        //以赠品添加时间倒序排序
        wrapper.orderByDesc("create_time");
        //获取赠品分页列表数据
        IPage<FullDiscountGiftDO> iPage = fullDiscountGiftMapper.selectPage(new Page<>(page, pageSize), wrapper);
        return PageConvert.convert(iPage);
    }

    @Override
    public List<FullDiscountGiftDO> listAll() {
        //获取当前登录的商家店铺信息
        Seller seller = UserContext.getSeller();
        //新建查询条件包装器
        QueryWrapper<FullDiscountGiftDO> wrapper = new QueryWrapper<>();
        //以商家店铺ID为查询条件
        wrapper.eq("seller_id", seller.getSellerId());
        //返回查询到的赠品信息集合
        return fullDiscountGiftMapper.selectList(wrapper);
    }

    @Override
    public FullDiscountGiftDO add(FullDiscountGiftDO fullDiscountGift) {
        //赠品信息入库
        fullDiscountGiftMapper.insert(fullDiscountGift);
        return fullDiscountGift;
    }

    @Override
    public FullDiscountGiftDO edit(FullDiscountGiftDO giftDO, Long id) {
        //设置赠品主键ID
        giftDO.setGiftId(id);
        //修改赠品信息
        fullDiscountGiftMapper.updateById(giftDO);
        return giftDO;
    }

    @Override
    public void delete(Long id) {
        //获取当前登录的商家店铺信息
        Seller seller = UserContext.getSeller();
        //新建删除条件包装器
        QueryWrapper<FullDiscountGiftDO> wrapper = new QueryWrapper<>();
        //以赠品ID和商家ID为删除条件
        wrapper.eq("gift_id", id).eq("seller_id", seller.getSellerId());
        //删除赠品信息
        fullDiscountGiftMapper.delete(wrapper);
    }

    @Override
    public FullDiscountGiftDO getModel(Long id) {
        //根据赠品ID获取赠品信息
        FullDiscountGiftDO giftDO = fullDiscountGiftMapper.selectById(id);
        return giftDO;
    }

    @Override
    public void verifyAuth(Long id) {
        //根据赠品ID获取赠品信息
        FullDiscountGiftDO fullDiscountGift = this.getModel(id);
        //获取当前登录的商家店铺信息
        Seller seller = UserContext.getSeller();
        //验证越权操作
        if (fullDiscountGift == null || !seller.getSellerId().equals(fullDiscountGift.getSellerId())) {
            throw new NoPermissionException("无权操作");
        }

    }


    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public boolean addGiftQuantity(List<FullDiscountGiftDO> giftDOList) {
        try {
            //循环修改赠品库存信息
            for (FullDiscountGiftDO giftDO : giftDOList) {
                //新建修改条件包装器
                UpdateWrapper<FullDiscountGiftDO> wrapper = new UpdateWrapper<>();
                //修改赠品可用库存和时间库存
                wrapper.setSql("enable_store=enable_store+1,actual_store=actual_store+1");
                //以赠品ID为修改条件
                wrapper.eq("gift_id", giftDO.getGiftId());
                //修改赠品库存信息
                fullDiscountGiftMapper.update(new FullDiscountGiftDO(), wrapper);
            }
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public boolean addGiftEnableQuantity(List<FullDiscountGiftDO> giftDOList) {
        try {
            //循环修改赠品可用库存信息
            for (FullDiscountGiftDO giftDO : giftDOList) {
                //新建修改条件包装器
                UpdateWrapper<FullDiscountGiftDO> wrapper = new UpdateWrapper<>();
                //修改赠品可用库存和时间库存
                wrapper.setSql("enable_store=enable_store+1");
                //以赠品ID为修改条件
                wrapper.eq("gift_id", giftDO.getGiftId());
                //修改赠品库存信息
                fullDiscountGiftMapper.update(new FullDiscountGiftDO(), wrapper);
            }
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public boolean reduceGiftQuantity(List<FullDiscountGiftDO> giftDOList, QuantityType type) {
        try {
            //新建修改条件包装器
            UpdateWrapper<FullDiscountGiftDO> wrapper = new UpdateWrapper<>();
            //如果库存类型为可用库存
            if (QuantityType.enable.equals(type)) {
                //赠品可用库存-1
                wrapper.setSql("enable_store=enable_store-1");
                //以可用库存大于0为修改条件
                wrapper.gt("enable_store", 0);
            } else if (QuantityType.actual.equals(type)) {
                //赠品实际库存-1
                wrapper.setSql("actual_store=actual_store-1");
                //以实际库存大于0为修改条件
                wrapper.gt("actual_store", 0);
            }

            for (FullDiscountGiftDO giftDO : giftDOList) {
                //以赠品ID为修改条件
                wrapper.eq("gift_id", giftDO.getGiftId());
                //修改赠品库存信息
               fullDiscountGiftMapper.update(new FullDiscountGiftDO(), wrapper);
            }
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public void verifyGift(Long giftId) {
        //获取当前时间
        Long currentDate = DateUtil.getDateline();
        //新建查询条件包装器
        QueryWrapper<FullDiscountDO> wrapper = new QueryWrapper<>();
        //以赠品ID为查询条件
        wrapper.eq("gift_id", giftId);
        //以促销活动开始时间小于等于当前时间并且结束时间大于等于当前时间为查询条件
        wrapper.le("start_time", currentDate).ge("end_time", currentDate);
        //获取查询到的数量
        Integer count = fullDiscountMapper.selectCount(wrapper);
        //如果结果大于0，证明当前赠品正在参与促销活动
        if(count > 0){
            throw new ServiceException(PromotionErrorCode.E401.code(),"赠品参与促销活动正在进行中，无法删除！");
        }
    }
}
