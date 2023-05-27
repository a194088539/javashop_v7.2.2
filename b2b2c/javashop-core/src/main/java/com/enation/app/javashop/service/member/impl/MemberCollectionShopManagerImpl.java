package com.enation.app.javashop.service.member.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.client.goods.GoodsClient;
import com.enation.app.javashop.client.member.ShopClient;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.member.MemberCollectionShopMapper;
import com.enation.app.javashop.model.base.rabbitmq.AmqpExchange;
import com.enation.app.javashop.model.goods.dto.GoodsQueryParam;
import com.enation.app.javashop.model.goods.vo.GoodsVO;
import com.enation.app.javashop.service.member.MemberCollectionShopManager;
import com.enation.app.javashop.model.errorcode.MemberErrorCode;
import com.enation.app.javashop.model.member.dos.Member;
import com.enation.app.javashop.model.member.dos.MemberCollectionShop;
import com.enation.app.javashop.model.member.vo.MemberCollectionShopVO;
import com.enation.app.javashop.service.member.MemberManager;
import com.enation.app.javashop.model.shop.vo.ShopVO;
import com.enation.app.javashop.model.statistics.dto.ShopData;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.exception.NoPermissionException;
import com.enation.app.javashop.framework.exception.ResourceNotFoundException;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.security.model.Buyer;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.framework.rabbitmq.MessageSender;
import com.enation.app.javashop.framework.rabbitmq.MqMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


/**
 * 会员收藏店铺表业务类
 *
 * @author zh
 * @version v7.0.0
 * @since v7.0.0
 * 2018-03-30 20:34:23
 */
@Service
public class MemberCollectionShopManagerImpl implements MemberCollectionShopManager {

    @Autowired
    private MemberManager memberManager;
    @Autowired
    private ShopClient shopClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private MessageSender messageSender;
    @Autowired
    private MemberCollectionShopMapper memberCollectionShopMapper;

    @Override
    public WebPage list(long page, long pageSize) {
        //新建查询条件包装器
        QueryWrapper<MemberCollectionShopVO> wrapper = new QueryWrapper<>();
        //以会员ID为查询条件
        wrapper.eq("member_id", UserContext.getBuyer().getUid());
        //按收藏时间倒序排序
        wrapper.orderByDesc("create_time");
        //获取会员收藏店铺分页列表数据
        IPage<MemberCollectionShopVO> iPage = memberCollectionShopMapper.selectShopVo(new Page(page, pageSize), wrapper);
        //存储查询到的商品数据
        List<MemberCollectionShopVO> memberCollectionShopVOS = new ArrayList<>();
        for (int i = 0; i < iPage.getRecords().size(); i++) {
            //获取当前的查询到的收藏信息
            MemberCollectionShopVO memberCollectionShopVO = iPage.getRecords().get(i);
            //组织查询条件查询商品数据，将查询到的商品数据组织好
            GoodsQueryParam goodsQueryParam = new GoodsQueryParam();
            goodsQueryParam.setSellerId(memberCollectionShopVO.getShopId());
            goodsQueryParam.setPageNo(1L);
            goodsQueryParam.setPageSize(10L);
            WebPage goodsPage = goodsClient.list(goodsQueryParam);
            memberCollectionShopVO.setGoodsList((List<GoodsVO>) goodsPage.getData());
            memberCollectionShopVOS.add(memberCollectionShopVO);
        }
        //将收藏店铺信息重新组织返回前端
        iPage.setRecords(memberCollectionShopVOS);
        return PageConvert.convert(iPage);
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public MemberCollectionShop add(MemberCollectionShop memberCollectionShop) {
        //获取店铺ID
        Long shopId = memberCollectionShop.getShopId();
        //根据店铺ID获取店铺信息并校验
        ShopVO shop = shopClient.getShop(shopId);
        if (shop == null) {
            throw new ResourceNotFoundException("当前店铺不存在");
        }
        //获取当前登录的会员信息
        Buyer buyer = UserContext.getBuyer();
        //根据当前登录的会员id获取完整会员信息并校验
        Member member = memberManager.getModel(buyer.getUid());
        if (member == null) {
            throw new ResourceNotFoundException("当前会员不存在");
        }
        //判断是否收藏的店铺是自己的
        if (!member.getHaveShop().equals(0)) {
            if (member.getShopId().equals(shopId)) {
                throw new ServiceException(MemberErrorCode.E102.code(), "无法将自己的店铺添加为收藏");
            }
        }

        //新建查询条件包装器
        QueryWrapper<MemberCollectionShop> wrapper = new QueryWrapper<>();
        //以会员ID为查询条件
        wrapper.eq("member_id", buyer.getUid());
        //以店铺ID为查询条件
        wrapper.eq("shop_id", shopId);
        //获取会员收藏店铺集合
        List<MemberCollectionShop> list = memberCollectionShopMapper.selectList(wrapper);
        if (list.size() > 0) {
            throw new ServiceException(MemberErrorCode.E103.code(), "此店铺已经添加为收藏");
        }

        //设置收藏时间
        memberCollectionShop.setCreateTime(DateUtil.getDateline());
        //设置会员ID
        memberCollectionShop.setMemberId(buyer.getUid());
        //设置店铺ID
        memberCollectionShop.setShopId(shop.getShopId());
        //设置店铺名称
        memberCollectionShop.setShopName(shop.getShopName());
        //设置店铺所属地区省份
        memberCollectionShop.setShopProvince(shop.getShopProvince());
        //设置店铺所属地区城市
        memberCollectionShop.setShopCity(shop.getShopCity());
        //设置店铺所属地区区县
        memberCollectionShop.setShopRegion(shop.getShopCounty());
        //设置店铺所属地区乡镇
        memberCollectionShop.setShopTown(shop.getShopTown());
        //设置店铺logo
        memberCollectionShop.setLogo(shop.getShopLogo());
        //设置店铺好评率
        memberCollectionShop.setShopPraiseRate(shop.getShopPraiseRate());
        //设置店铺描述相符度
        memberCollectionShop.setShopDescriptionCredit(shop.getShopDescriptionCredit());
        //设置服务态度分数
        memberCollectionShop.setShopServiceCredit(shop.getShopServiceCredit());
        //设置发货速度分数
        memberCollectionShop.setShopDeliveryCredit(shop.getShopDeliveryCredit());
        //收藏店铺信息入库
        memberCollectionShopMapper.insert(memberCollectionShop);

        //添加为收藏后需要更新店铺收藏数
        shopClient.addCollectNum(shop.getShopId());

        //发送消息
        ShopData shopData = new ShopData();
        shopData.setSellerId(shopId);
        shopData.setFavoriteNum(this.getCollectionBySeller(shopId));
        messageSender.send(new MqMessage(AmqpExchange.SELLER_COLLECTION_CHANGE, AmqpExchange.SELLER_COLLECTION_CHANGE + "_ROUTING",
                shopData));

        return memberCollectionShop;
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(Long shopId) {
        //新建查询条件包装器
        QueryWrapper<MemberCollectionShop> wrapper = new QueryWrapper<>();
        //以会员ID为查询条件
        wrapper.eq("member_id", UserContext.getBuyer().getUid());
        //以店铺ID为查询条件
        wrapper.eq("shop_id", shopId);
        //查询收藏的店铺信息
        MemberCollectionShop memberCollectionShop = memberCollectionShopMapper.selectOne(wrapper);
        //非空校验
        if (memberCollectionShop == null) {
            throw new NoPermissionException("无权限操作此收藏");
        }
        //根据收藏店铺ID删除会员收藏的店铺信息
        memberCollectionShopMapper.deleteById(memberCollectionShop.getId());
        //取消收藏后更新收藏店铺数
        shopClient.reduceCollectNum(shopId);
        //发送消息
        ShopData shopData = new ShopData();
        shopData.setSellerId(shopId);
        shopData.setFavoriteNum(this.getCollectionBySeller(shopId));
        messageSender.send(new MqMessage(AmqpExchange.SELLER_COLLECTION_CHANGE, AmqpExchange.SELLER_COLLECTION_CHANGE + "_ROUTING",
                shopData));
    }

    @Override
    public MemberCollectionShop getModel(Long id) {
        return memberCollectionShopMapper.selectById(id);
    }

    /**
     * 获取会员收藏店铺
     *
     * @param id 会员id
     * @return
     */
    @Override
    public boolean isCollection(Long id) {
        //新建查询条件包装器
        QueryWrapper<MemberCollectionShop> wrapper = new QueryWrapper<>();
        //以会员ID为查询条件
        wrapper.eq("member_id", UserContext.getBuyer().getUid());
        //以店铺ID为查询条件
        wrapper.eq("shop_id", id);
        //获取收藏店铺数量，数量大于0证明当前店铺已经被收藏
        int count = memberCollectionShopMapper.selectCount(wrapper);
        return count > 0;
    }

    @Override
    public Integer getMemberCollectCount() {
        //新建查询条件包装器
        QueryWrapper<MemberCollectionShop> wrapper = new QueryWrapper<>();
        //以会员ID为查询条件
        wrapper.eq("member_id", UserContext.getBuyer().getUid());
        return memberCollectionShopMapper.selectCount(wrapper);
    }

    /**
     * 获取店铺有多少收藏量
     *
     * @param sellerId
     * @return 收藏量
     */
    @Override
    public Integer getCollectionBySeller(Long sellerId) {
        //新建查询条件包装器
        QueryWrapper<MemberCollectionShop> wrapper = new QueryWrapper<>();
        //以店铺ID为查询条件
        wrapper.eq("shop_id", sellerId);
        return memberCollectionShopMapper.selectCount(wrapper);
    }

    @Override
    public void changeSellerName(Long sellerId, String sellerName) {
        //新建收藏店铺对象
        MemberCollectionShop shop = new MemberCollectionShop();
        //设置店铺名称
        shop.setShopName(sellerName);
        //新建修改条件包装器
        UpdateWrapper<MemberCollectionShop> wrapper = new UpdateWrapper<>();
        //以店铺ID为修改条件
        wrapper.eq("shop_id", sellerId);
        //修改收藏的店铺名称
        memberCollectionShopMapper.update(shop, wrapper);
    }
}
