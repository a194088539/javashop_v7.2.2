package com.enation.app.javashop.service.shop.impl;


import java.text.SimpleDateFormat;
import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.mapper.member.ShopDetailMapper;
import com.enation.app.javashop.mapper.member.ShopMapper;
import com.enation.app.javashop.model.base.message.ShopStatusChangeMsg;
import com.enation.app.javashop.model.base.rabbitmq.AmqpExchange;
import com.enation.app.javashop.client.goods.GoodsClient;
import com.enation.app.javashop.client.goods.TagClient;
import com.enation.app.javashop.client.statistics.ShopDataClient;
import com.enation.app.javashop.model.goods.dos.GoodsDO;
import com.enation.app.javashop.model.errorcode.MemberErrorCode;
import com.enation.app.javashop.model.member.dos.Member;
import com.enation.app.javashop.model.member.dos.MemberZpzzDO;
import com.enation.app.javashop.model.member.dto.MemberShopScoreDTO;
import com.enation.app.javashop.model.member.enums.ZpzzStatusEnum;
import com.enation.app.javashop.model.shop.enums.ShopMessageTypeEnum;
import com.enation.app.javashop.service.member.MemberManager;
import com.enation.app.javashop.service.member.MemberShopScoreManager;
import com.enation.app.javashop.service.member.MemberZpzzManager;
import com.enation.app.javashop.model.shop.dos.ClerkDO;
import com.enation.app.javashop.model.shop.dto.*;
import com.enation.app.javashop.model.shop.vo.*;
import com.enation.app.javashop.service.shop.ShopThemesManager;
import com.enation.app.javashop.service.shop.ClerkManager;
import com.enation.app.javashop.model.statistics.dto.ShopData;
import com.enation.app.javashop.framework.util.*;
import com.enation.app.javashop.framework.rabbitmq.MessageSender;
import com.enation.app.javashop.framework.rabbitmq.MqMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.javashop.model.errorcode.ShopErrorCode;
import com.enation.app.javashop.model.shop.dos.ShopDO;
import com.enation.app.javashop.model.shop.dos.ShopDetailDO;
import com.enation.app.javashop.model.shop.dos.ShopThemesDO;
import com.enation.app.javashop.model.shop.enums.ShopStatusEnum;
import com.enation.app.javashop.model.shop.enums.ShopThemesEnum;
import com.enation.app.javashop.service.shop.ShopManager;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.security.model.Buyer;
import com.enation.app.javashop.framework.security.model.Seller;

/**
 * 店铺业务类
 *
 * @author zhangjiping
 * @version v7.0
 * @since v7.0
 * 2018年3月20日 上午10:06:33
 */
@Service
public class ShopManagerImpl implements ShopManager {

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private ShopDetailMapper shopDetailMapper;

    @Autowired
    private ShopThemesManager shopThemesManager;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private TagClient tagClient;

    @Autowired
    private MemberManager memberManager;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private ClerkManager clerkManager;

    @Autowired
    private ShopDataClient shopDataClient;

    @Autowired
    private MemberZpzzManager memberZpzzManager;

    @Autowired
    private MemberShopScoreManager memberShopScoreManager;

    //店铺名称最大长度
    private static final int SHOP_NAME_MAX_LENGTH = 15;

    @Override
    public ShopInfoVO getShopInfo(Long shopId) {
        return new ShopInfoVO(this.getShop(shopId));
    }


    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveInit() {
        //获取会员系信息
        Buyer buyer = UserContext.getBuyer();
        ShopDO shop = new ShopDO();
        //查看会员时候拥有店铺
        if (buyer != null && this.getShopByMemberId(buyer.getUid()) == null) {
            //初始化店铺信息
            shop.setShopDisable(ShopStatusEnum.APPLYING.toString());
            //设置会员信息
            shop.setMemberId(buyer.getUid());
            shop.setMemberName(buyer.getUsername());
            //店铺基础信息入库
            shopMapper.insert(shop);

            //初始化店铺详细信息
            shop.setShopId(shop.getShopId());
            ShopDetailDO shopDetail = new ShopDetailDO();
            this.initShopDetail(shopDetail);
            //设置店铺id
            shopDetail.setShopId(shop.getShopId());
            //店铺详细信息入库
            shopDetailMapper.insert(shopDetail);

            //发送消息
            messageSender.send(new MqMessage(AmqpExchange.SHOP_CHANGE_REGISTER, AmqpExchange.SHOP_CHANGE_REGISTER + "_ROUTING",
                    shop.getShopId()));
        }
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ApplyStep1VO step1(ApplyStep1VO applyStep1) {

        if (applyStep1.getRegMoney() != null && applyStep1.getRegMoney() > 10000000000L) {
            throw new ServiceException(ShopErrorCode.E224.name(), "注册资金数值过大，请注意单位");
        }

        ShopVO shop = this.getShop();
        //判断是否拥有店铺
        this.whetheHaveShop(shop);
        //判断是否为店员
        this.validateClerk(shop);
        //设置申请开单第一步
        if (shop.getStep() == null) {
            applyStep1.setStep(1);
        }
        //新建店铺详情对象
        ShopDetailDO shopDetailDO = new ShopDetailDO();
        //设置要修改的信息
        BeanUtil.copyProperties(applyStep1, shopDetailDO);
        //设置店铺ID
        shopDetailDO.setShopId(shop.getShopId());
        //设置更新条件
        UpdateWrapper<ShopDetailDO> wrapper = new UpdateWrapper<>();
        wrapper.eq("shop_id", shop.getShopId());
        //修改店铺详情信息
        shopDetailMapper.update(shopDetailDO, wrapper);
        return applyStep1;
    }


    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ApplyStep2VO step2(ApplyStep2VO applyStep2) {
        ShopVO shop = this.getShop();
        //判断是否拥有店铺
        this.whetheHaveShop(shop);
        //判断是否为店员
        this.validateClerk(shop);
        //没有完成第一步不允许此步操作
        if (shop.getStep() == null) {
            throw new ServiceException(ShopErrorCode.E224.name(), "完成上一步才可进行此步操作");
        }
        //未完成第三步则设置为第二步
        if (shop.getStep() < 3) {
            applyStep2.setStep(2);
        }
        if (applyStep2.getLicenceEnd() != null && 0 != applyStep2.getLicenceEnd()) {
            //校验营业执照有效期
            if (applyStep2.getLicenceStart() > applyStep2.getLicenceEnd()) {
                throw new ServiceException(ShopErrorCode.E217.name(), "营业执照开始时间不能大于结束时间");
            }
            //校验营业执照开始时间
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String date = sdf.format(new Date()) + " 00:00:00";
            long startTime = DateUtil.getDateline(date);
            if (applyStep2.getLicenceEnd() <= startTime) {
                throw new ServiceException(ShopErrorCode.E217.name(), "营业执照结束时间不能小于当前时间");
            }
        }

        //新建店铺详情对象
        ShopDetailDO shopDetailDO = new ShopDetailDO();
        //设置要修改的信息
        BeanUtil.copyProperties(applyStep2, shopDetailDO);
        //设置店铺ID
        shopDetailDO.setShopId(shop.getShopId());
        //设置更新条件
        UpdateWrapper<ShopDetailDO> wrapper = new UpdateWrapper<>();
        wrapper.eq("shop_id", shop.getShopId());
        //修改店铺详情信息
        shopDetailMapper.update(shopDetailDO, wrapper);
        return applyStep2;
    }


    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ApplyStep3VO step3(ApplyStep3VO applyStep3) {
        ShopVO shop = this.getShop();
        //判断是否拥有店铺
        this.whetheHaveShop(shop);
        //判断是否为店员
        this.validateClerk(shop);
        //没有完成第二步不允许此步操作
        if (shop.getStep() == null || shop.getStep() < 2) {
            throw new ServiceException(ShopErrorCode.E224.name(), "完成上一步才可进行此步操作");
        }
        applyStep3.setStep(3);

        //新建店铺详情对象
        ShopDetailDO shopDetailDO = new ShopDetailDO();
        //设置要修改的信息
        BeanUtil.copyProperties(applyStep3, shopDetailDO);
        //设置店铺ID
        shopDetailDO.setShopId(shop.getShopId());
        //设置更新条件
        UpdateWrapper<ShopDetailDO> wrapper = new UpdateWrapper<>();
        wrapper.eq("shop_id", shop.getShopId());
        //修改店铺详情信息
        shopDetailMapper.update(shopDetailDO, wrapper);
        return applyStep3;
    }


    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ApplyStep4VO step4(ApplyStep4VO applyStep4) {
        ShopVO shop = this.getShop();
        //判断是否拥有店铺
        this.whetheHaveShop(shop);
        //判断是否为店员
        this.validateClerk(shop);
        //没有完成第三步不允许此步操作
        if (shop.getStep() == null || shop.getStep() < 3) {
            throw new ServiceException(ShopErrorCode.E224.name(), "完成上一步才可进行此步操作");
        }
        applyStep4.setStep(4);

        boolean checkShopName = this.checkShopName(applyStep4.getShopName(), shop.getShopId());
        if (checkShopName) {
            throw new ServiceException(ShopErrorCode.E203.name(), "店铺名称重复");
        }

        //更新店铺基本信息
        ShopDO shopDO = new ShopDO();
        shopDO.setShopName(applyStep4.getShopName());
        shopDO.setShopDisable(ShopStatusEnum.APPLY.toString());
        shopDO.setShopId(shop.getShopId());
        shopMapper.updateById(shopDO);

        //更新店铺地址相关信息
        UpdateWrapper<ShopDetailDO> wrapper = new UpdateWrapper<>();
        wrapper.set("shop_province", applyStep4.getShopProvince())
                .set("shop_province_id", applyStep4.getShopProvinceId())
                .set("shop_city", applyStep4.getShopCity())
                .set("shop_city_id", applyStep4.getShopCityId())
                .set("shop_county", applyStep4.getShopCounty())
                .set("shop_county_id", applyStep4.getShopCountyId())
                .set("shop_town", applyStep4.getShopTown())
                .set("shop_town_id", applyStep4.getShopTownId())
                .set("goods_management_category", applyStep4.getGoodsManagementCategory())
                .set("shop_add", applyStep4.getShopAdd())
                .eq("shop_id", shop.getShopId());
        shopDetailMapper.update(new ShopDetailDO(), wrapper);
        return applyStep4;
    }

    @Override
    public WebPage list(ShopParamsVO shopParams) {
        //检测店铺状态如果为空，则默认设置为"OPEN"
        String disabled = shopParams.getShopDisable() == null ? "OPEN" : shopParams.getShopDisable();
        shopParams.setShopDisable(disabled);
        //根据条件查询店铺分页列表数据
        IPage iPage = shopMapper.selectShopPage(new Page(shopParams.getPageNo(), shopParams.getPageSize()), shopParams);
        return PageConvert.convert(iPage);
    }

    @Override
    public List<ShopVO> list() {
        //获取所有开启中的店铺信息集合
        String shopStatus = "OPEN";
        return shopMapper.selectShopList(shopStatus);
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void disShop(Long shopId) {
        //根据店铺id获取店铺详细信息
        ShopVO shop = this.getShop(shopId);
        if (shop == null) {
            throw new ServiceException(ShopErrorCode.E206.name(), "不存在此店铺");
        }

        ShopDO shopDO = new ShopDO();
        //设置店铺id
        shopDO.setShopId(shopId);
        //设置店铺关闭时间为当前时间
        shopDO.setShopEndtime(DateUtil.getDateline());
        //设置店铺状态为已关闭
        shopDO.setShopDisable(ShopStatusEnum.CLOSED.toString());
        //更新店铺基础信息
        shopMapper.updateById(shopDO);

        //更改统计中店铺状态
        ShopData shopData = new ShopData();
        shopData.setSellerId(shop.getShopId());
        shopData.setSellerName(shop.getShopName());
        shopData.setShopDisable(ShopStatusEnum.CLOSED.toString());
        shopDataClient.updateShopData(shopData);

        //下架店铺所有商品
        goodsClient.underShopGoods(shopId);
        messageSender.send(new MqMessage(AmqpExchange.CLOSE_STORE, AmqpExchange.CLOSE_STORE + "_ROUTING", new ShopStatusChangeMsg(shopId, ShopStatusEnum.CLOSED)));

    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void useShop(Long shopId) {
        //根据店铺id获取店铺详细信息
        ShopVO shop = this.getShop(shopId);
        if (shop == null) {
            throw new ServiceException(ShopErrorCode.E206.name(), "不存在此店铺");
        }

        ShopDO shopDO = new ShopDO();
        //设置店铺id
        shopDO.setShopId(shopId);
        //设置店铺状态为开启中
        shopDO.setShopDisable(ShopStatusEnum.OPEN.toString());
        //更新店铺基础信息
        shopMapper.updateById(shopDO);

        //更改统计中店铺状态
        ShopData shopData = new ShopData();
        shopData.setSellerId(shop.getShopId());
        shopData.setSellerName(shop.getShopName());
        shopData.setShopDisable(ShopStatusEnum.OPEN.toString());
        shopDataClient.updateShopData(shopData);
    }

    @Override
    public ShopVO getShop(Long shopId) {
        //根据店铺ID获取店铺信息
        ShopVO shopVO = shopMapper.selectByShopId(shopId);
        return shopVO;
    }

    @Override
    public ShopVO getShopByMemberId(Long memberId) {
        return shopMapper.selectByMemberId(memberId);
    }

    @Override
    public boolean checkShopName(String shopName, Long shopId) {
        //创建查询包装器
        QueryWrapper<ShopDO> wrapper = new QueryWrapper<>();
        //根据店铺名称查询
        wrapper.eq("shop_name", shopName);
        //店铺状态不等于审核拒绝（也就是不等于REFUSED）
        wrapper.ne("shop_disable", ShopStatusEnum.REFUSED.value());
        //如果店铺ID不为空，那么查询条件要排除当前传入的这个店铺id
        wrapper.ne(shopId != null, "shop_id", shopId);
        //根据条件获取查询数量
        Integer count = shopMapper.selectCount(wrapper);
        return count != 0;
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void registStore(ShopVO shopVo) {

        ShopThemesDO pcThemes = shopThemesManager.getDefaultShopThemes(ShopThemesEnum.PC.name());
        ShopThemesDO wapThemes = shopThemesManager.getDefaultShopThemes(ShopThemesEnum.WAP.name());

        //验证会员是否已存在店铺
        if (this.getShopByMemberId(shopVo.getMemberId()) != null) {
            throw new ServiceException(ShopErrorCode.E207.name(), "会员已存在店铺，不可重复添加");
        }
        //验证店铺模板是否存在
        if (pcThemes == null || wapThemes == null) {
            throw new ServiceException(ShopErrorCode.E202.name(), "店铺模版不存在，请设置店铺模版");
        }
        //验证店铺名称是否重复
        if (this.checkShopName(shopVo.getShopName(), shopVo.getShopId())) {
            throw new ServiceException(ShopErrorCode.E203.name(), "店铺名称重复");
        }

        //设置模版信息
        shopVo.setShopThemeid(pcThemes.getId());
        shopVo.setShopThemePath(pcThemes.getMark());
        shopVo.setWapThemeid(wapThemes.getId());
        shopVo.setWapThemePath(wapThemes.getMark());
        //设置店铺等级
        shopVo.setShopLevel(1);
        //后台无需审核直接开店
        shopVo.setShopDisable(ShopStatusEnum.OPEN.toString());
        //设置开店时间
        shopVo.setShopCreatetime(DateUtil.getDateline());

        //保存店铺信息
        shopVo.setMemberId(shopVo.getMemberId());

        //获取店铺店铺信息实体
        ShopDO shop = new ShopDO();
        ShopDetailDO shopDetail = new ShopDetailDO();
        BeanUtils.copyProperties(shopVo, shopDetail);
        BeanUtils.copyProperties(shopVo, shop);
        //店铺基础信息入库
        shopMapper.insert(shop);
        Long lastId = shop.getShopId();
        shopDetail.setShopId(lastId);
        shopVo.setShopId(lastId);
        //初始化店铺信息
        this.initShopDetail(shopDetail);
        //店铺详细信息入库
        shopDetailMapper.insert(shopDetail);
        //增加店铺商品标签
        tagClient.addShopTags(lastId);

        //修改会员信息
        Member member = memberManager.getModel(shopVo.getMemberId());
        member.setShopId(lastId);
        member.setHaveShop(1);
        memberManager.edit(member, member.getMemberId());

    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void editShopInfo(ShopVO shopVO) {
        //校验店铺名称长度
        if (shopVO.getShopName().length() > SHOP_NAME_MAX_LENGTH) {
            throw new ServiceException(ShopErrorCode.E227.code(), "店铺名称长度不能超过15个字符");
        }
        //获取店铺店铺信息实体
        ShopDO shop = new ShopDO();
        ShopDetailDO shopDetail = new ShopDetailDO();
        BeanUtils.copyProperties(shopVO, shopDetail);
        BeanUtils.copyProperties(shopVO, shop);

        //新建查询包装器
        QueryWrapper<ShopDO> wrapper = new QueryWrapper<>();
        //按店铺名称进行查询
        wrapper.eq("shop_name", shop.getShopName());
        //将当前店铺ID排除在外
        wrapper.ne("shop_id", shopVO.getShopId());
        //获取店铺信息
        ShopDO shopDO = shopMapper.selectOne(wrapper);
        //如果根据店铺名称获取到店铺信息，证明店铺名称重复
        if (shopDO != null) {
            throw new ServiceException(ShopErrorCode.E203.code(), "店铺名称重复");
        }

        //获取修改之前的原店铺信息
        ShopVO originalShop = this.getShop(shopVO.getShopId());

        //修改店铺基础信息
        shopMapper.updateById(shop);
        //新增修改包装器
        UpdateWrapper<ShopDetailDO> updateWrapper = new UpdateWrapper<>();
        //以店铺ID作为修改条件
        updateWrapper.eq("shop_id", shopVO.getShopId());
        //修改店铺详细信息
        shopDetailMapper.update(shopDetail, updateWrapper);

        //更改统计中店铺数据
        ShopData shopData = new ShopData();
        shopData.setSellerId(shop.getShopId());
        shopData.setSellerName(shop.getShopName());
        shopData.setShopDisable(shop.getShopDisable());
        shopDataClient.updateShopData(shopData);
        //发送店铺信息改变消息
        messageSender.send(new MqMessage(AmqpExchange.SHOP_CHANGE, AmqpExchange.SHOP_CHANGE + "_ROUTING", new ShopChangeMsg(originalShop, shopVO, ShopMessageTypeEnum.All.name())));
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void editShopSetting(ShopSettingDTO shopSetting) {
        //获取当前登录的商家店铺信息
        Seller seller = UserContext.getSeller();
        //创建店铺详情实例
        ShopDetailDO shopDetail = new ShopDetailDO();
        //将相关修改的信息copy到店铺详情实例中
        BeanUtil.copyProperties(shopSetting, shopDetail);
        //新增修改包装器
        UpdateWrapper<ShopDetailDO> wrapper = new UpdateWrapper<>();
        //以当前登录的店铺ID作为修改条件
        wrapper.eq("shop_id", seller.getSellerId());
        //修改店铺详细信息
        shopDetailMapper.update(shopDetail, wrapper);
    }

    @Override
    public ShopDetailDO getShopDetail(Long shopId) {
        //新建查询包装器
        QueryWrapper<ShopDetailDO> wrapper = new QueryWrapper<>();
        //以店铺ID为查询条件
        wrapper.eq("shop_id", shopId);
        return shopDetailMapper.selectOne(wrapper);
    }

    @Override
    public boolean checkIdNumber(String idNumber) {
        //新建查询包装器
        QueryWrapper<ShopDetailDO> wrapper = new QueryWrapper<>();
        //以会员身份证号为查询条件
        wrapper.eq("legal_id", idNumber);
        //获取查询结果数量
        Integer count = shopDetailMapper.selectCount(wrapper);
        return count != 0;
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void addcollectNum(Long shopId) {
        //新建修改包装器
        UpdateWrapper<ShopDetailDO> wrapper = new UpdateWrapper<>();
        //以店铺ID为修改条件，修改店铺收藏数量+1
        wrapper.setSql("shop_collect = shop_collect + 1").eq("shop_id", shopId);
        shopDetailMapper.update(new ShopDetailDO(), wrapper);
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void reduceCollectNum(Long shopId) {
        //新建修改包装器
        UpdateWrapper<ShopDetailDO> wrapper = new UpdateWrapper<>();
        //以店铺ID为修改条件，修改店铺收藏数量-1
        wrapper.setSql("shop_collect = shop_collect - 1").eq("shop_id", shopId);
        shopDetailMapper.update(new ShopDetailDO(), wrapper);
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void editShopOnekey(String key, String value) {
        //获取当前登录的商家店铺信息
        Seller seller = UserContext.getSeller();
        //新建修改包装器
        UpdateWrapper<ShopDetailDO> wrapper = new UpdateWrapper<>();
        //以店铺ID为修改条件，修改店铺信息中的某个字段值
        wrapper.set(key, value).eq("shop_id", seller.getSellerId());
        shopDetailMapper.update(new ShopDetailDO(), wrapper);
    }

    @Override
    public List<ShopBankDTO> listShopBankInfo() {
        //设置查询条件--店铺状态为已开启和已关闭
        List<String> statusList = new ArrayList<>();
        statusList.add(ShopStatusEnum.OPEN.name());
        statusList.add(ShopStatusEnum.CLOSED.name());
        //根据店铺状态获取相关店铺的银行以及佣金比例等相关信息集合
        List<ShopBankDTO> shopBankDTOS = shopMapper.selectShopBank(statusList);
        return shopBankDTOS;
    }


    @Override
    public ShopBasicInfoDTO getShopBasicInfo(Long shopId) {
        //根据店铺ID获取店铺基础信息
        ShopBasicInfoDTO shopBasicInfoDTO = shopMapper.selectShopBasic(shopId);
        //判断店铺是否存在
        if (shopBasicInfoDTO == null) {
            throw new ServiceException(ShopErrorCode.E206.name(), "店铺不存在");
        }
        return shopBasicInfoDTO;
    }

    @Override
    public WebPage listShopBasicInfo(ShopParamsVO shopParams) {
        //设置店铺状态为已开启状态
        shopParams.setShopDisable(ShopStatusEnum.OPEN.value());
        //获取店铺分页列表信息
        IPage<ShopListVO> iPage = shopMapper.selectShopListPage(new Page(shopParams.getPageNo(), shopParams.getPageSize()), shopParams);
        //获取结果集合
        List<ShopListVO> data = iPage.getRecords();
        //循环结果集合
        for (ShopListVO shop : data) {
            //获取店铺商品集合
            List<GoodsDO> goodsDOS = this.goodsClient.listGoods(shop.getShopId());
            //将商品集合信息放入店铺信息中
            shop.setGoodsList(goodsDOS);
            //获取会员信息
            Member model = memberManager.getModel(shop.getMemberId());
            //设置会员头像
            shop.setMemberFace(model.getFace());
        }
        //将新的结果集合重新放入IPage对象中
        iPage.setRecords(data);
        return PageConvert.convert(iPage);
    }

    @Override
    public void editStatus(ShopStatusEnum shopStatusEnum, Long shopId) {
        //新建shop对象
        ShopDO shop = new ShopDO();
        //设置店铺状态
        shop.setShopDisable(shopStatusEnum.value());
        //设置店铺ID
        shop.setShopId(shopId);
        //修改店铺状态
        shopMapper.updateById(shop);
    }

    @Override
    public void editShopScore(ShopScoreDTO shopScore) {
        //新建店铺详情对象
        ShopDetailDO shopDetail = new ShopDetailDO();
        //设置店铺描述相符度
        shopDetail.setShopDescriptionCredit(CurrencyUtil.round(shopScore.getShopDescriptionCredit(), 2));
        //设置服务态度分数
        shopDetail.setShopServiceCredit(CurrencyUtil.round(shopScore.getShopServiceCredit(), 2));
        //设置发货速度分数
        shopDetail.setShopDeliveryCredit(CurrencyUtil.round(shopScore.getShopDeliveryCredit(), 2));
        //设置店铺信用
        shopDetail.setShopCredit(CurrencyUtil.round(shopScore.getShopCredit(), 2));

        //创建修改包装器
        UpdateWrapper<ShopDetailDO> wrapper = new UpdateWrapper<>();
        //以店铺id作为修改条件
        wrapper.eq("shop_id", shopScore.getShopId());
        //修改店铺信息
        shopDetailMapper.update(shopDetail, wrapper);
    }

    @Override
    public void calculateShopScore() {
        //查询店铺评分的平均值
        List<MemberShopScoreDTO> shopScoreList = this.memberShopScoreManager.queryEveryShopScore();
        if (StringUtil.isNotEmpty(shopScoreList)) {
            for (MemberShopScoreDTO shopScore : shopScoreList) {
                Double descriptionScore = shopScore.getDescriptionScore();
                Double serviceScore = shopScore.getServiceScore();
                Double deliveryScore = shopScore.getDeliveryScore();
                Double shopCredit = CurrencyUtil.div(CurrencyUtil.add(CurrencyUtil.add(descriptionScore, serviceScore), deliveryScore), 3.00);
                ShopScoreDTO dto = new ShopScoreDTO(descriptionScore, serviceScore, deliveryScore, shopCredit, shopScore.getSellerId());

                this.editShopScore(dto);
            }
        }
    }

    @Override
    public void receiptSetting(ShopReceiptDTO shopReceiptDTO) {
        Seller seller = UserContext.getSeller();
        if (seller == null) {
            throw new ServiceException(MemberErrorCode.E137.code(), "当前商家登录信息已经失效");
        }

        //新建店铺详情对象
        ShopDetailDO shopDetail = new ShopDetailDO();
        //设置店铺是否允许开具增值税普通发票 0：否，1：是
        shopDetail.setOrdinReceiptStatus(shopReceiptDTO.getOrdinReceiptStatus());
        //设置店铺是否允许开具电子普通发票 0：否，1：是
        shopDetail.setElecReceiptStatus(shopReceiptDTO.getElecReceiptStatus());
        //设置店铺是否允许开具增值税专用发票 0：否，1：是
        shopDetail.setTaxReceiptStatus(shopReceiptDTO.getTaxReceiptStatus());

        //创建修改包装器
        UpdateWrapper<ShopDetailDO> wrapper = new UpdateWrapper<>();
        //以店铺id作为修改条件
        wrapper.eq("shop_id", seller.getSellerId());
        //修改店铺信息
        shopDetailMapper.update(shopDetail, wrapper);
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ShopReceiptDTO checkSellerReceipt(Long[] ids) {
        //获取当前登录会员增票资质信息
        MemberZpzzDO memberZpzzDO = this.memberZpzzManager.get();
        //新增查询包装器
        QueryWrapper<ShopDetailDO> wrapper = new QueryWrapper<>();
        //以店铺ID作为查询条件
        wrapper.in("shop_id", ids);
        //查询店铺信息集合
        List<ShopDetailDO> detailDOS = shopDetailMapper.selectList(wrapper);

        int ordin = 1;
        int elec = 1;
        int tax = 1;

        for (ShopDetailDO shopDetailDO : detailDOS) {
            if (ordin == 1) {
                ordin = shopDetailDO.getOrdinReceiptStatus();
            }

            if (elec == 1) {
                elec = shopDetailDO.getElecReceiptStatus();
            }

            if (tax == 1) {
                tax = shopDetailDO.getTaxReceiptStatus();
            }
        }

        //如果当前登录会员还没有申请增票资质或者申请增票资质还未审核或审核不通过，那么不允许开具增值税专用发票
        if (memberZpzzDO == null || !ZpzzStatusEnum.AUDIT_PASS.value().equals(memberZpzzDO.getStatus())) {
            tax = 0;
        }

        ShopReceiptDTO shopReceiptDTO = new ShopReceiptDTO();
        shopReceiptDTO.setOrdinReceiptStatus(ordin);
        shopReceiptDTO.setElecReceiptStatus(elec);
        shopReceiptDTO.setTaxReceiptStatus(tax);
        return shopReceiptDTO;
    }

    @Override
    public int queryShopCount() {

        return this.shopMapper.selectCount(new QueryWrapper<ShopDO>().eq("shop_disable", "OPEN"));
    }

    @Override
    public List<ShopDO> queryShopByRange(Long i, Long pageSize) {
        IPage iPage = this.shopMapper.selectPage(new Page<>(1,pageSize),new QueryWrapper<ShopDO>()
                .eq("shop_disable", "OPEN")
                .orderByDesc("shop_id"));
        return iPage.getRecords();
    }

    @Override
    public void updateShopGoodsNum(Long sellerId, Integer sellerGoodsCount) {
        this.shopDetailMapper.update(new ShopDetailDO(), new UpdateWrapper<ShopDetailDO>()
                .set("goods_num", sellerGoodsCount).eq("shop_id", sellerId));
    }

    /**
     * 初始化店铺信息
     *
     * @param shopDetail
     */
    private void initShopDetail(ShopDetailDO shopDetail) {
        shopDetail.setShopCredit(5.0);
        shopDetail.setShopPraiseRate(0.0);
        shopDetail.setShopDescriptionCredit(5.0);
        shopDetail.setShopServiceCredit(5.0);
        shopDetail.setShopDeliveryCredit(5.0);
        shopDetail.setShopCommission(5.0);
        shopDetail.setShopCollect(0);
        shopDetail.setShopLevel(1);
        shopDetail.setGoodsNum(0);
        shopDetail.setGoodsWarningCount(5);
        shopDetail.setShopLevelApply(0);
        shopDetail.setStoreSpaceCapacity(0.00);
        shopDetail.setSelfOperated(0);
    }

    /**
     * 根据当前会员获取店铺
     */
    private ShopVO getShop() {
        Buyer buyer = UserContext.getBuyer();
        ShopVO shop = this.getShopByMemberId(buyer.getUid());
        return shop;
    }

    /**
     * 判断当前会员时候拥有店铺
     *
     * @param shop
     */
    private void whetheHaveShop(ShopVO shop) {
        if (shop == null) {
            throw new ServiceException(ShopErrorCode.E200.name(), "您尚未拥有店铺，不能进行此操作");
        }

        if (!shop.getShopDisable().equals(ShopStatusEnum.APPLYING.name())) {
            throw new ServiceException(ShopErrorCode.E204.name(), "店铺在申请中，不允许此操作");
        }
    }

    /**
     * 检测是否店员
     *
     * @param shop
     */
    private void validateClerk(ShopVO shop) {
        ClerkDO clerk = this.clerkManager.getClerkByMemberId(shop.getMemberId());
        if (clerk != null) {
            throw new ServiceException(ShopErrorCode.E230.name(), "当前账号为店铺的管理员，不允许此操作！");
        }
    }
}
