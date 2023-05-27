package com.enation.app.javashop.client.member.impl;

import com.enation.app.javashop.client.member.MemberCommentClient;
import com.enation.app.javashop.model.member.vo.GoodsGrade;
import com.enation.app.javashop.service.member.MemberCommentManager;
import com.enation.app.javashop.model.trade.order.dto.OrderDetailDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author fk
 * @version v1.0
 * @Description: 会员评论对外接口实现
 * @date 2018/7/26 11:35
 * @since v7.0.0
 */
@Service
@ConditionalOnProperty(value = "javashop.product", havingValue = "stand")
public class MemberCommentClientDefaultImpl implements MemberCommentClient {

    @Autowired
    private MemberCommentManager memberCommentManager;

    @Override
    public List<GoodsGrade> queryGoodsGrade() {

        return memberCommentManager.queryGoodsGrade();
    }

    @Override
    public Integer getGoodsCommentCount(Long goodsId) {

        return memberCommentManager.getGoodsCommentCount(goodsId);
    }

    @Override
    public void autoGoodComments(List<OrderDetailDTO> detailDTOList) {
        memberCommentManager.autoGoodComments(detailDTOList);
    }

    @Override
    public void editComment(Long memberId, String face) {
        memberCommentManager.editComment(memberId, face);
    }
}
