package com.enation.app.javashop.service.member.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.member.MemberCollectionGoodsMapper;
import com.enation.app.javashop.model.base.rabbitmq.AmqpExchange;
import com.enation.app.javashop.client.goods.GoodsClient;
import com.enation.app.javashop.model.goods.vo.CacheGoods;
import com.enation.app.javashop.model.errorcode.MemberErrorCode;
import com.enation.app.javashop.model.member.dos.Member;
import com.enation.app.javashop.model.member.dos.MemberCollectionGoods;
import com.enation.app.javashop.service.member.MemberCollectionGoodsManager;
import com.enation.app.javashop.service.member.MemberManager;
import com.enation.app.javashop.model.statistics.dto.GoodsData;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.database.WebPage;
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

import java.util.List;

/**
 * 会员商品收藏业务类
 *
 * @author zh
 * @version v7.0.0
 * @since v7.0.0
 * 2018-04-02 10:13:41
 */
@Service
public class MemberCollectionGoodsManagerImpl implements MemberCollectionGoodsManager {

    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private MemberManager memberManager;
    @Autowired
    private MessageSender messageSender;
    @Autowired
    private MemberCollectionGoodsMapper memberCollectionGoodsMapper;

    @Override
    public WebPage list(long page, long pageSize) {
        //获取当前登录的会员信息
        Buyer buyer = UserContext.getBuyer();
        //新建查询条件包装器
        QueryWrapper<MemberCollectionGoods> wrapper = new QueryWrapper<>();
        //以会员ID为查询条件
        wrapper.eq("member_id", buyer.getUid());
        //获取会员收藏的商品分页列表数据
        IPage<MemberCollectionGoods> iPage = memberCollectionGoodsMapper.selectPage(new Page<>(page, pageSize), wrapper);
        return PageConvert.convert(iPage);
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public MemberCollectionGoods add(MemberCollectionGoods memberCollectionGoods) {
        //获取当前登录的会员信息
        Buyer buyer = UserContext.getBuyer();
        //根据当前登录会员ID获取会员完整信息
        Member member = memberManager.getModel(buyer.getUid());
        if (member == null) {
            throw new ResourceNotFoundException("当前会员不存在");
        }
        //获取商品id
        Long goodsId = memberCollectionGoods.getGoodsId();
        //查询此商品信息
        CacheGoods goods = goodsClient.getFromCache(memberCollectionGoods.getGoodsId());
        //判断商品是否存在
        if (goods == null) {
            throw new ResourceNotFoundException("此商品不存在");
        }

        //新建查询条件包装器
        QueryWrapper<MemberCollectionGoods> wrapper = new QueryWrapper<>();
        //以会员ID为查询条件
        wrapper.eq("member_id", buyer.getUid());
        //以商品ID为查询条件
        wrapper.eq("goods_id", goodsId);
        //获取会员收藏的商品集合
        List<MemberCollectionGoods> list = memberCollectionGoodsMapper.selectList(wrapper);
        //判断当前商品是否已经添加为收藏
        if (list.size() > 0) {
            throw new ServiceException(MemberErrorCode.E105.code(), "当前商品已经添加为收藏");
        }
        //如果当前会员拥有店铺，则查看是否是收藏的自己店铺的商品
        if (!member.getHaveShop().equals(0)) {
            if (member.getShopId().equals(goods.getSellerId())) {
                throw new ServiceException(MemberErrorCode.E104.code(), "无法收藏自己店铺的商品");
            }
        }

        //设置会员ID
        memberCollectionGoods.setMemberId(buyer.getUid());
        //设置商品名称
        memberCollectionGoods.setGoodsName(goods.getGoodsName());
        //设置商品图片
        memberCollectionGoods.setGoodsImg(goods.getThumbnail());
        //设置店铺ID
        memberCollectionGoods.setShopId(goods.getSellerId());
        //设置收藏时间
        memberCollectionGoods.setCreateTime(DateUtil.getDateline());
        //设置商品编号
        memberCollectionGoods.setGoodsSn(goods.getSn());
        //设置商品价格
        memberCollectionGoods.setGoodsPrice(goods.getPrice());
        //商品收藏信息入库
        memberCollectionGoodsMapper.insert(memberCollectionGoods);

        //发送消息
        GoodsData goodsData = new GoodsData();
        goodsData.setGoodsId(goodsId);
        goodsData.setFavoriteNum(this.getGoodsCollectCount(goodsId));
        messageSender.send(new MqMessage(AmqpExchange.GOODS_COLLECTION_CHANGE, AmqpExchange.GOODS_COLLECTION_CHANGE + "_ROUTING",
                goodsData));
        return memberCollectionGoods;
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(Long goodsId) {
        //获取当前登录的会员信息
        Buyer buyer = UserContext.getBuyer();
        //新建查询条件包装器
        QueryWrapper<MemberCollectionGoods> wrapper = new QueryWrapper<>();
        //以会员ID为查询条件
        wrapper.eq("member_id", buyer.getUid());
        //以商品ID为查询条件
        wrapper.eq("goods_id", goodsId);
        //获取会员收藏的商品信息
        MemberCollectionGoods memberCollectionGoods = memberCollectionGoodsMapper.selectOne(wrapper);
        //校验收藏的商品信息是否为空
        if (memberCollectionGoods != null) {
            //根据收藏ID删除会员收藏的商品
            memberCollectionGoodsMapper.deleteById(memberCollectionGoods.getId());
            //发送消息
            GoodsData goodsData = new GoodsData();
            goodsData.setGoodsId(goodsId);
            goodsData.setFavoriteNum(this.getGoodsCollectCount(goodsId));
            messageSender.send(new MqMessage(AmqpExchange.GOODS_COLLECTION_CHANGE, AmqpExchange.GOODS_COLLECTION_CHANGE + "_ROUTING",
                    goodsData));
        }
    }


    @Override
    public boolean isCollection(Long id) {
        //获取当前登录的会员信息
        Buyer buyer = UserContext.getBuyer();
        //新建查询条件包装器
        QueryWrapper<MemberCollectionGoods> wrapper = new QueryWrapper<>();
        //以会员ID为查询条件
        wrapper.eq("member_id", buyer.getUid());
        //以商品ID为查询条件
        wrapper.eq("goods_id", id);
        //获取收藏商品的数量
        int count = memberCollectionGoodsMapper.selectCount(wrapper);
        return count > 0;
    }

    @Override
    public void updateGoodsName(Long goodsId, String goodsName) {
        //新建收藏商品对象
        MemberCollectionGoods goods = new MemberCollectionGoods();
        //设置商品名称
        goods.setGoodsName(goodsName);
        //新建修改条件包装器
        UpdateWrapper<MemberCollectionGoods> wrapper = new UpdateWrapper<>();
        //以商品ID为修改条件
        wrapper.eq("goods_id", goodsId);
        //修改会员收藏商品信息
        memberCollectionGoodsMapper.update(goods, wrapper);
    }

    @Override
    public MemberCollectionGoods getModel(Long id) {
        return memberCollectionGoodsMapper.selectById(id);
    }

    @Override
    public Integer getMemberCollectCount() {
        //新建查询条件包装器
        QueryWrapper<MemberCollectionGoods> wrapper = new QueryWrapper<>();
        //以会员ID为查询条件
        wrapper.eq("member_id", UserContext.getBuyer().getUid());
        return memberCollectionGoodsMapper.selectCount(wrapper);
    }

    /**
     * 获取会员收藏商品数
     *
     * @return 收藏商品数
     */
    @Override
    public Integer getGoodsCollectCount(Long goodsId) {
        try {
            //新建查询条件包装器
            QueryWrapper<MemberCollectionGoods> wrapper = new QueryWrapper<>();
            //以商品ID为查询条件
            wrapper.eq("goods_id", goodsId);
            return memberCollectionGoodsMapper.selectCount(wrapper);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }
}
