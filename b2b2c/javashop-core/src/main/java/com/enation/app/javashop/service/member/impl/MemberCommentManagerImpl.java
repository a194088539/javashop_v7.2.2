package com.enation.app.javashop.service.member.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.mapper.member.MemberCommentMapper;
import com.enation.app.javashop.model.base.CharacterConstant;
import com.enation.app.javashop.model.base.SettingGroup;
import com.enation.app.javashop.model.base.message.GoodsCommentMsg;
import com.enation.app.javashop.model.base.rabbitmq.AmqpExchange;
import com.enation.app.javashop.client.system.SettingClient;
import com.enation.app.javashop.client.trade.OrderClient;
import com.enation.app.javashop.model.errorcode.MemberErrorCode;
import com.enation.app.javashop.model.goods.dto.GoodsSettingVO;
import com.enation.app.javashop.model.goods.enums.Permission;
import com.enation.app.javashop.service.member.*;
import com.enation.app.javashop.model.member.dos.*;
import com.enation.app.javashop.model.member.dto.AdditionalCommentDTO;
import com.enation.app.javashop.model.member.dto.CommentDTO;
import com.enation.app.javashop.model.member.dto.CommentQueryParam;
import com.enation.app.javashop.model.member.dto.CommentScoreDTO;
import com.enation.app.javashop.model.member.enums.AuditEnum;
import com.enation.app.javashop.model.member.enums.CommentGrade;
import com.enation.app.javashop.model.member.enums.CommentTypeEnum;
import com.enation.app.javashop.model.member.vo.BatchAuditVO;
import com.enation.app.javashop.model.member.vo.CommentVO;
import com.enation.app.javashop.model.member.vo.GoodsGrade;
import com.enation.app.javashop.model.member.vo.MemberCommentCount;
import com.enation.app.javashop.model.util.sensitiveutil.SensitiveFilter;
import com.enation.app.javashop.model.trade.order.enums.CommentStatusEnum;
import com.enation.app.javashop.model.trade.order.dto.OrderDetailDTO;
import com.enation.app.javashop.model.trade.order.dto.OrderSkuDTO;
import com.enation.app.javashop.framework.context.user.AdminUserContext;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.exception.NoPermissionException;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.security.model.Admin;
import com.enation.app.javashop.framework.security.model.Buyer;
import com.enation.app.javashop.framework.util.*;
import com.enation.app.javashop.framework.rabbitmq.MessageSender;
import com.enation.app.javashop.framework.rabbitmq.MqMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 评论业务类
 *
 * @author fk
 * @version v1.0
 * @since v7.0.0
 * 2018-05-03 10:19:14
 */
@Service
public class MemberCommentManagerImpl implements MemberCommentManager {

    @Autowired
    private MemberShopScoreManager memberShopScoreManager;
    @Autowired
    private CommentGalleryManager commentGalleryManager;
    @Autowired
    private CommentReplyManager commentReplyManager;
    @Autowired
    private MessageSender messageSender;
    @Autowired
    private OrderClient orderClient;
    @Autowired
    private MemberManager memberManager;
    @Autowired
    private SettingClient settingClient;
    @Autowired
    private MemberCommentMapper memberCommentMapper;

    @Override
    public WebPage list(CommentQueryParam param) {
        //新建查询条件包装器
        QueryWrapper<CommentVO> wrapper = new QueryWrapper<>();
        //以评论状态为正常作为查询条件 1 正常 0 删除
        wrapper.eq("status", 1);
        //如果会员ID不为空并且不等于0，将会员ID作为查询条件
        wrapper.eq(param.getMemberId() != null && param.getMemberId().intValue() != 0, "member_id", param.getMemberId());
        //如果商家ID不为空并且不等于0，将商家ID作为查询条件
        wrapper.eq(param.getSellerId() != null && param.getSellerId().intValue() != 0, "seller_id", param.getSellerId());
        //如果商品ID不为空并且不等于0，将商品ID作为查询条件
        wrapper.eq(param.getGoodsId() != null && param.getGoodsId().intValue() != 0, "goods_id", param.getGoodsId());
        //如果搜索关键字不为空，则按评论内容或者商品名称或者会员名称进行模糊查询
        wrapper.and(StringUtil.notEmpty(param.getKeyword()), ew -> {
            ew.like("content", param.getKeyword()).or().like("goods_name", param.getKeyword()).or().like("member_name", param.getKeyword());
        });
        //如果评论内容不为空，则按评论内容模糊查询
        wrapper.like(StringUtil.notEmpty(param.getContent()), "content", param.getKeyword());
        //如果商品名称不为空，则按商品名称模糊查询
        wrapper.like(StringUtil.notEmpty(param.getGoodsName()), "goods_name", param.getKeyword());
        //如果会员名称不为空，则按会员名称模糊查询
        wrapper.like(StringUtil.notEmpty(param.getMemberName()), "member_name", param.getKeyword());
        //如果评价等级不为空，则将评价等级作为查询条件
        wrapper.eq(StringUtil.notEmpty(param.getGrade()), "grade", param.getGrade());
        //如果审核状态不为空，则将审核状态作为查询条件
        wrapper.eq(StringUtil.notEmpty(param.getAuditStatus()), "audit_status", param.getAuditStatus());
        //如果评价类型不为空，则将评价类型作为查询条件
        wrapper.eq(StringUtil.notEmpty(param.getCommentsType()), "comments_type", param.getCommentsType());
        //如果是否有图不为空，则将是否有图作为查询条件 1：有图，0：无图
        wrapper.eq(param.getHaveImage() != null, "have_image", param.getHaveImage());
        //如果回复状态不为空，则将回复状态作为查询条件 1：已回复，0：未回复
        wrapper.eq(param.getReplyStatus() != null, "reply_status", param.getReplyStatus());
        //如果评论日期-开始时间不为空，则将评论日期-开始时间作为查询条件
        wrapper.ge(param.getStartTime() != null, "create_time", param.getStartTime());
        //如果评论日期-结束时间不为空，则将评论日期-结束时间作为查询条件
        wrapper.le(param.getEndTime() != null, "create_time", param.getEndTime());

        // 按评论状态查询(主要应用于会员中心--评论管理)
        if (StringUtil.notEmpty(param.getCommentStatus())) {
            if (CommentStatusEnum.WAIT_CHASE.name().equals(param.getCommentStatus())) {
                //待追评:评论为初评且审核通过且未追评过
                wrapper.notInSql("comment_id", "select parent_id from es_member_comment where comments_type = '" + CommentTypeEnum.ADDITIONAL.value() + "'");

            } else if (CommentStatusEnum.FINISHED.name().equals(param.getCommentStatus())) {
                //以父id为0作为查询条件
                wrapper.eq("parent_id", 0);
                //以评论类型为初次评论为查询条件
                wrapper.eq("comments_type", CommentTypeEnum.INITIAL.name());
            }

        }

        // 按是否有追评查询(主要应用于商品详情页面评论列表数据展示)
        if (param.getHaveAdditional() != null && param.getHaveAdditional().intValue() == 1) {
            wrapper.inSql("comment_id", "select parent_id from es_member_comment where comments_type = '" + CommentTypeEnum.ADDITIONAL.name() + "' " +
                    "and goods_id = " + param.getGoodsId() + " and audit_status = '" + param.getAuditStatus() + "' and status = 1");
        }

        //按创建时间倒序排序
        wrapper.orderByDesc("create_time");
        //获取评论分页列表数据
        IPage<CommentVO> iPage = memberCommentMapper.selectCommentVo(new Page(param.getPageNo(), param.getPageSize()), wrapper);
        //获取结果集
        List<CommentVO> list = iPage.getRecords();
        if (StringUtil.isNotEmpty(list)) {
            // 找出有图片和回复过的评论id
            for (CommentVO comment : list) {
                //获取评论图片，回复，追评信息
                this.getCommentVO(comment);
            }
        }

        return new WebPage(param.getPageNo(), iPage.getTotal(), param.getPageSize(), list);
    }


    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public MemberComment add(CommentScoreDTO comment, Permission permission) {
        //根据订单编号获取订单详细信息
        OrderDetailDTO orderDetail = orderClient.getModel(comment.getOrderSn());
        // 不存在的订单/不是我的订单
        if (Permission.BUYER.equals(permission)) {
            Buyer member = UserContext.getBuyer();
            if (orderDetail == null || !member.getUid().equals(orderDetail.getMemberId())) {
                throw new NoPermissionException("没有操作权限");
            }
        }
        if (!orderDetail.getOrderOperateAllowableVO().getAllowComment()) {
            throw new NoPermissionException("没有操作权限");
        }

        // 添加店铺评分
        MemberShopScore shopScore = new MemberShopScore();
        BeanUtils.copyProperties(comment, shopScore);
        shopScore.setMemberId(orderDetail.getMemberId());
        shopScore.setSellerId(orderDetail.getSellerId());
        this.memberShopScoreManager.add(shopScore);

        // 添加评论
        this.add(comment.getComments(), orderDetail, false);

        return null;
    }

    /**
     * 添加评论
     *
     * @param commentList 发起的评论
     * @param orderDetail 订单
     */
    private void add(List<CommentDTO> commentList, OrderDetailDTO orderDetail, Boolean isAutoComment) {
        Map<Long, Object> skuMap = new HashMap<>(orderDetail.getOrderSkuList().size());
        // 将product循环放入map
        for (OrderSkuDTO sku : orderDetail.getOrderSkuList()) {
            skuMap.put(sku.getSkuId(), sku);
        }
        List<MemberComment> comments = new ArrayList<>();

        //获取商品评论设置
        String json = this.settingClient.get(SettingGroup.GOODS);
        GoodsSettingVO goodsSettingVO = JsonUtil.jsonToObject(json, GoodsSettingVO.class);

        for (CommentDTO comment : commentList) {
            OrderSkuDTO product = (OrderSkuDTO) skuMap.get(comment.getSkuId());
            if (product == null) {
                throw new NoPermissionException("没有操作权限");
            }

            //新建评论对象
            MemberComment memberComment = new MemberComment();
            //复制对象属性
            BeanUtils.copyProperties(comment, memberComment);
            //根据会员ID获取会员完整信息
            Member member = memberManager.getModel(orderDetail.getMemberId());
            //评论内容敏感词过滤
            memberComment.setContent(SensitiveFilter.filter(memberComment.getContent(), CharacterConstant.WILDCARD_STAR));
            //设置会员头像
            memberComment.setMemberFace(member.getFace());
            //设置商品ID
            memberComment.setGoodsId(product.getGoodsId());
            //设置评论时间
            memberComment.setCreateTime(DateUtil.getDateline());
            //设置会员ID
            memberComment.setMemberId(orderDetail.getMemberId());
            //设置评论状态 1 正常 0 删除
            memberComment.setStatus(1);
            //设置回复状态 1：已回复，0：未回复
            memberComment.setReplyStatus(0);
            //设置店铺ID
            memberComment.setSellerId(product.getSellerId());
            //设置商品名称
            memberComment.setGoodsName(product.getName());
            //设置商品图片
            memberComment.setGoodsImg(product.getGoodsImage());
            //设置会员名称
            memberComment.setMemberName(member.getUname());
            //设置订单编号
            memberComment.setOrderSn(orderDetail.getSn());
            //设置评论类型为初次评论
            memberComment.setCommentsType(CommentTypeEnum.INITIAL.name());

            //如果平台未开启商品评论审核功能，那么评论默认为审核通过状态
            if (goodsSettingVO.getCommentAuth().intValue() == 0) {
                memberComment.setAuditStatus(AuditEnum.PASS_AUDIT.name());
            } else {
                //如果平台开启了商品评论审核功能，那么评论（除系统自动好评之外，系统自动好评不用审核）默认为待审核状态
                memberComment.setAuditStatus(isAutoComment ? AuditEnum.PASS_AUDIT.name() : AuditEnum.WAIT_AUDIT.name());
            }
            //设置父ID为0
            memberComment.setParentId(0L);

            //设置是否有图片 1：有图，0：无图
            memberComment.setHaveImage(StringUtil.isNotEmpty(comment.getImages()) ? 1 : 0);

            //如果评论等级为好评并且评论内容为空（一般针对系统自动好评）
            if (CommentGrade.good.name().equals(comment.getGrade()) && StringUtil.isEmpty(memberComment.getContent())) {
                memberComment.setContent("此评论默认好评！！");
                //默认好评自动审核通过  update by liuyulei 2019-07-24
                memberComment.setAuditStatus(AuditEnum.PASS_AUDIT.name());
            }
            //如果评论等级不为好评，那么评论内容必填
            if (!CommentGrade.good.name().equals(comment.getGrade()) && StringUtil.isEmpty(memberComment.getContent())) {
                throw new ServiceException(MemberErrorCode.E201.code(), "非好评评论必填");
            }

            //评论信息入库
            memberCommentMapper.insert(memberComment);

            //添加评论图片
            this.commentGalleryManager.add(memberComment.getCommentId(), comment.getImages());
            comments.add(memberComment);
        }

        comments.forEach(comment -> {
            //如果评论不为空  且是初评
            if (comment != null && CommentTypeEnum.INITIAL.name().equals(comment.getCommentsType())) {
                // 更改订单的评论状态，同步更改 ，避免重复评论
                orderClient.updateItemsCommentStatus(comment.getOrderSn(), comment.getGoodsId(), CommentStatusEnum.WAIT_CHASE);
            }
        });

        //如果是系统自动评价，那么直接发送消息;如果不是系统自动评价，那么还需判断平台是否开启了商品评论审核功能
        if (isAutoComment) {
            sendCommentMsg(true, comments, GoodsCommentMsg.ADD);
        } else {
            //如果平台未开启商品评论审核功能，那么需要直接发送评论消息
            if (goodsSettingVO.getCommentAuth().intValue() == 0) {
                sendCommentMsg(false, comments, GoodsCommentMsg.ADD);
            }
        }
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public MemberComment edit(MemberComment memberComment, Long id) {
        //设置评论ID
        memberComment.setCommentId(id);
        //修改评论内容
        memberCommentMapper.updateById(memberComment);
        return memberComment;
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(Long id) {
        //根据ID获取评论内容并校验
        MemberComment memberComment = this.getModel(id);
        if (memberComment == null || memberComment.getStatus() == 0) {
            throw new NoPermissionException("没有操作权限");
        }

        //新建评论对象
        MemberComment comment = new MemberComment();
        //设置评论状态为删除  1 正常 0 删除
        comment.setStatus(0);
        //设置评论ID
        comment.setCommentId(id);
        //修改评论为删除状态
        memberCommentMapper.updateById(comment);

        List<MemberComment> commentList = new ArrayList<>();
        commentList.add(memberComment);

        //发送删除评论消息
        this.sendCommentMsg(false, commentList, GoodsCommentMsg.DELETE);
    }

    @Override
    public MemberComment getModel(Long id) {
        return memberCommentMapper.selectById(id);
    }

    @Override
    public List<GoodsGrade> queryGoodsGrade() {
        //新建查询条件包装器
        QueryWrapper<GoodsGrade> wrapper = new QueryWrapper<>();
        //以评论状态为正常作为查询条件 1 正常 0 删除
        wrapper.eq("status", 1);
        //以商品ID作为查询条件
        wrapper.groupBy("goods_id");
        //查询商品的好评比例信息集合
        List<GoodsGrade> goodsList = memberCommentMapper.selectGoodsGrade(wrapper);
        return goodsList;
    }

    @Override
    public Integer getGoodsCommentCount(Long goodsId) {
        //新建查询条件包装器
        QueryWrapper<MemberComment> wrapper = new QueryWrapper<>();
        //以商品ID作为查询条件
        wrapper.eq("goods_id", goodsId);
        //以评论状态为正常作为查询条件 1 正常 0 删除
        wrapper.eq("status", 1);
        return memberCommentMapper.selectCount(wrapper);
    }

    @Override
    public void autoGoodComments(List<OrderDetailDTO> detailDTOList) {
        // 查询过期没有评论订单
        List<OrderDetailDTO> list = detailDTOList;

        // 循环订单的商品自动给好评
        if (StringUtil.isNotEmpty(list)) {
            for (OrderDetailDTO orderDetail : list) {
                // 添加店铺评分
                MemberShopScore shopScore = new MemberShopScore();
                shopScore.setDeliveryScore(5);
                shopScore.setDescriptionScore(5);
                shopScore.setServiceScore(5);
                shopScore.setOrderSn(orderDetail.getSn());
                shopScore.setMemberId(orderDetail.getMemberId());
                shopScore.setSellerId(orderDetail.getSellerId());
                this.memberShopScoreManager.add(shopScore);
                //  添加商品评分
                List<OrderSkuDTO> skuList = orderDetail.getOrderSkuList();
                List<CommentDTO> commentList = new ArrayList<>();

                for (OrderSkuDTO sku : skuList) {
                    CommentDTO comment = new CommentDTO();
                    comment.setSkuId(sku.getSkuId());
                    comment.setGrade(CommentGrade.good.name());
                    comment.setContent("此商品默认好评");
                    comment.setImages(null);
                    commentList.add(comment);
                }
                this.add(commentList, orderDetail, true);
            }
        }
    }

    @Override
    public MemberCommentCount count(Long goodsId) {
        //新建查询条件包装器
        QueryWrapper wrapper = new QueryWrapper();
        //以商品ID作为查询条件
        wrapper.eq("goods_id", goodsId);
        //以审核状态通过作为查询条件
        wrapper.eq("audit_status", AuditEnum.PASS_AUDIT.name());
        //以评论等级和是否有图片进行分组
        wrapper.groupBy("grade", "have_image");
        //商品的评论数量--评论必须是审核通过的状态
        List<Map> list = memberCommentMapper.selectGoodsGradeCount(wrapper);

        Integer allCount = 0;
        Integer goodCount = 0;
        Integer neutralCount = 0;
        Integer badCount = 0;
        Integer imageCount = 0;

        if (StringUtil.isNotEmpty(list)) {
            for (Map map : list) {
                String grade = map.get("grade").toString();
                Integer count = Integer.valueOf(map.get("count").toString());
                allCount += count;
                switch (grade) {
                    case "good":
                        goodCount += count;
                        break;
                    case "neutral":
                        neutralCount += count;
                        break;
                    case "bad":
                        badCount += count;
                        break;
                    default:
                        break;
                }
                //图片评论的数量
                Integer haveImage = (Integer) map.get("have_image");
                if (haveImage == 1) {
                    imageCount += count;
                }
            }
        }

        return new MemberCommentCount(allCount, goodCount, neutralCount, badCount, imageCount);
    }


    @Override
    public void editComment(Long memberId, String face) {
        //新建评论对象
        MemberComment comment = new MemberComment();
        //设置要修改的会员头像
        comment.setMemberFace(face);
        //新建修改条件包装器
        UpdateWrapper<MemberComment> wrapper = new UpdateWrapper<>();
        //以会员ID作为修改条件
        wrapper.eq("member_id", memberId);
        //修改评论数据
        memberCommentMapper.update(comment, wrapper);
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<AdditionalCommentDTO> additionalComments(List<AdditionalCommentDTO> comments, Permission permission) {
        List<MemberComment> commentsList = new ArrayList<>();
        //参数校验
        if (comments == null || comments.isEmpty()) {
            throw new ServiceException(MemberErrorCode.E200.code(), "请补充待追评数据后再提交！");
        }

        //获取商品评论设置
        String json = this.settingClient.get(SettingGroup.GOODS);
        GoodsSettingVO goodsSettingVO = JsonUtil.jsonToObject(json, GoodsSettingVO.class);

        for (AdditionalCommentDTO commentDTO : comments) {
            //根据评论ID获取评论信息
            MemberComment memberComment = this.getModel(commentDTO.getCommentId());

            //如果会员评论为空或者评论已删除，则不允许添加追评
            if (memberComment == null || memberComment.getStatus().intValue() == 0) {
                throw new ServiceException(MemberErrorCode.E200.code(), "没有权限");
            }

            //根据评论ID获取追评信息
            MemberComment additional = this.getAdditionalById(memberComment.getCommentId());
            //校验是否已经追加过评论
            if (additional != null) {
                throw new ServiceException(MemberErrorCode.E200.code(), "已追加过评论，无法再次追加评论！");
            }
            //校验初评是否审核
            if (!AuditEnum.PASS_AUDIT.name().equals(memberComment.getAuditStatus())) {
                throw new ServiceException(MemberErrorCode.E200.code(), "初评未审核或审核拒绝，无法追评！");
            }

            // 验证权限
            if (Permission.BUYER.equals(permission)) {
                Buyer member = UserContext.getBuyer();
                if (!member.getUid().equals(memberComment.getMemberId())) {
                    throw new NoPermissionException("没有操作权限");
                }
            }
            //校验追加的评论内容不能为空
            if (StringUtil.isEmpty(commentDTO.getContent())) {
                throw new ServiceException(MemberErrorCode.E201.code(), "追加的评论内容不能为空");
            }
            //默认无图
            Integer haveImage = 0;

            //如果追评包含图片信息，则设置为有图
            if (commentDTO.getImages() != null && commentDTO.getImages().size() > 0) {
                haveImage = 1;
            }

            //如果平台没有开启商品评论审核功能，那么商品评论默认为审核通过
            if (goodsSettingVO.getCommentAuth().intValue() == 0) {
                memberComment.setAuditStatus(AuditEnum.PASS_AUDIT.name());
            } else {
                memberComment.setAuditStatus(AuditEnum.WAIT_AUDIT.name());
            }

            //追评内容敏感词过滤
            memberComment.setContent(SensitiveFilter.filter(commentDTO.getContent(), CharacterConstant.WILDCARD_STAR));
            //设置追评父ID
            memberComment.setParentId(commentDTO.getCommentId());
            //设置追评时间
            memberComment.setCreateTime(DateUtil.getDateline());
            //设置追评是否包含图片 0：否，1：是
            memberComment.setHaveImage(haveImage);
            //设置评论类型为追评
            memberComment.setCommentsType(CommentTypeEnum.ADDITIONAL.name());
            //设置追评状态为正常 0：删除，1：正常
            memberComment.setStatus(1);
            //设置追评是否回复 0：否，1：是
            memberComment.setReplyStatus(0);
            //将id置空
            memberComment.setCommentId(null);
            //追评入库
            memberCommentMapper.insert(memberComment);
            //如果追评包含图片信息
            if (haveImage == 1) {
                //添加图片
                this.commentGalleryManager.add(memberComment.getCommentId(), commentDTO.getImages());
            }
            commentsList.add(memberComment);
            //追评完成后，修改订单评论状态为已完成，即只允许一次追评  ，审核只是为了是否在前端展示
            this.orderClient.updateItemsCommentStatus(memberComment.getOrderSn(), memberComment.getGoodsId(), CommentStatusEnum.FINISHED);
        }

        //发送追评消息
        this.sendCommentMsg(false, commentsList, GoodsCommentMsg.ADD);

        return comments;
    }

    @Override
    public MemberComment getAdditionalById(Long commentId) {
        //新建查询条件包装器
        QueryWrapper<MemberComment> wrapper = new QueryWrapper<>();
        //以评论父ID为查询条件
        wrapper.eq("parent_id", commentId);
        //以评论状态为查询条件 0：删除，1：正常
        wrapper.eq("status", 1);
        //获取评论数量
        int count = memberCommentMapper.selectCount(wrapper);
        //如果数量大于0
        if (count > 0) {
            //新建查询条件包装器
            QueryWrapper<MemberComment> queryWrapper = new QueryWrapper<>();
            //以评论父ID为查询条件
            queryWrapper.eq("parent_id", commentId);
            //以评论类型为追评作为查询条件
            queryWrapper.eq("comments_type", CommentTypeEnum.ADDITIONAL.name());
            //以评论状态为查询条件 0：删除，1：正常
            queryWrapper.eq("status", 1);
            return memberCommentMapper.selectOne(queryWrapper);
        }
        return null;
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void batchAudit(BatchAuditVO batchAuditVO) {
        Admin admin = AdminUserContext.getAdmin();

        //判断是否为管理员操作
        if (admin == null) {
            throw new NoPermissionException("没有操作权限");
        }

        if (batchAuditVO.getIds() == null || batchAuditVO.getIds().length == 0) {
            throw new ServiceException(MemberErrorCode.E201.code(), "请选择要进行审核的会员商品评论");
        }

        if (!AuditEnum.PASS_AUDIT.value().equals(batchAuditVO.getAuthStatus()) && !AuditEnum.REFUSE_AUDIT.value().equals(batchAuditVO.getAuthStatus())) {
            throw new ServiceException(MemberErrorCode.E201.code(), "审核状态参数值不正确");
        }

        //新建查询条件包装器
        QueryWrapper<MemberComment> wrapper = new QueryWrapper<>();
        //以评论ID为查询条件
        wrapper.in("comment_id", batchAuditVO.getIds());
        //获取评论集合
        List<MemberComment> memberCommentList = memberCommentMapper.selectList(wrapper);
        //循环结果集
        for (MemberComment memberComment : memberCommentList) {
            if (!AuditEnum.WAIT_AUDIT.value().equals(memberComment.getAuditStatus())) {
                throw new ServiceException(MemberErrorCode.E200.code(), "内容为【" + memberComment.getContent() + "】的评论不是可以进行审核的状态");
            }
            //更改评论审核状态
            MemberComment comment = new MemberComment();
            comment.setAuditStatus(batchAuditVO.getAuthStatus());
            comment.setCommentId(memberComment.getCommentId());
            memberCommentMapper.updateById(comment);
        }

        //发送评论消息
        this.sendCommentMsg(false, memberCommentList, GoodsCommentMsg.ADD);
    }

    @Override
    public CommentVO get(Long commentId) {
        //根据评论ID获取评论信息
        MemberComment memberComment = this.getModel(commentId);
        //非空校验
        if (memberComment == null) {
            return null;
        }
        //新建评论对象VO
        CommentVO commentVO = new CommentVO();
        //复制评论对象属性
        BeanUtil.copyProperties(memberComment, commentVO);
        //初始化评论对象VO
        this.getCommentVO(commentVO);
        return commentVO;
    }

    @Override
    public List<CommentVO> get(String orderSn, Long skuId) {
        //新建查询条件包装器
        QueryWrapper<CommentVO> wrapper = new QueryWrapper<>();
        //以订单编号为查询条件
        wrapper.eq("order_sn", orderSn);
        //如果商品SKUid不为空，则以sku_id为查询条件
        wrapper.eq(skuId != null, "sku_id", skuId);
        //以评论类型为初评做为查询条件
        wrapper.eq("comments_type", CommentTypeEnum.INITIAL.name());
        //获取评论信息集合
        List<CommentVO> comments = memberCommentMapper.selectCommentVoList(wrapper);
        //循环初始化评论信息
        comments.forEach(comment -> {
            this.getCommentVO(comment);
        });

        return comments;
    }

    /**
     * 获取评论图片和回复信息下
     *
     * @param comment
     * @return
     */
    private CommentVO getCommentVO(CommentVO comment) {
        //判断是否有图 0：否，1：是
        if (comment.getHaveImage() == 1) {
            Map<Long, List<String>> map = this.commentGalleryManager.getGalleryByCommentIds(comment.getCommentId());
            comment.setImages(map.get(comment.getCommentId()));
        }

        //找出评论回复信息
        if (comment.getReplyStatus() == 1) {
            CommentReply reply = this.commentReplyManager.getReply(comment.getCommentId());
            comment.setReply(reply);
        }

        //获取店铺评分信息
        comment.setMemberShopScore(this.memberShopScoreManager.getModel(comment.getMemberId(), comment.getOrderSn()));

        //如果评论为初评则获取评论追评信息
        if (CommentTypeEnum.INITIAL.value().equals(comment.getCommentsType())) {
            this.getAdditinalInfo(comment);
        }
        return comment;
    }

    /**
     * 获取初评所属的追评信息
     *
     * @param comment
     */
    private void getAdditinalInfo(CommentVO comment) {
        //根据会员评论id获取追评信息
        MemberComment memberComment = this.getAdditionalById(comment.getCommentId());
        //如果追评信息不为空并且审核状态为审核通过
        if (memberComment != null && AuditEnum.PASS_AUDIT.name().equals(memberComment.getAuditStatus())) {
            //新建评论对象VO
            CommentVO commentVO = new CommentVO();
            //复制追评属性值
            BeanUtil.copyProperties(memberComment, commentVO);
            //初始化评论对象VO
            this.getCommentVO(commentVO);
            //设置追评信息
            comment.setAdditionalComment(commentVO);
        }
    }

    /**
     * 发送商品评论完成消息
     *
     * @param isAutoComment 是否为系统自动评论
     * @param comments      评论信息集合
     * @param operaType     操作类型
     */
    private void sendCommentMsg(Boolean isAutoComment, List<MemberComment> comments, int operaType) {
        GoodsCommentMsg goodsCommentMsg = new GoodsCommentMsg();
        goodsCommentMsg.setComment(comments);
        goodsCommentMsg.setAutoComment(isAutoComment);
        goodsCommentMsg.setOperaType(operaType);
        this.messageSender.send(new MqMessage(AmqpExchange.GOODS_COMMENT_COMPLETE, AmqpExchange.GOODS_COMMENT_COMPLETE + "_ROUTING", goodsCommentMsg));
    }
}
