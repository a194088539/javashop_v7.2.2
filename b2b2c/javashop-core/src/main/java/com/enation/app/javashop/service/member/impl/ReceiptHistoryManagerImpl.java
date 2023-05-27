package com.enation.app.javashop.service.member.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.BeanUtil;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.member.ReceiptFileMapper;
import com.enation.app.javashop.mapper.member.ReceiptHistoryMapper;
import com.enation.app.javashop.model.errorcode.MemberErrorCode;
import com.enation.app.javashop.model.member.dos.ReceiptFileDO;
import com.enation.app.javashop.model.member.dto.HistoryQueryParam;
import com.enation.app.javashop.model.member.enums.ReceiptTypeEnum;
import com.enation.app.javashop.model.member.vo.ReceiptFileVO;
import com.enation.app.javashop.model.member.vo.ReceiptHistoryVO;
import com.enation.app.javashop.model.trade.order.enums.OrderStatusEnum;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.security.model.Seller;
import com.enation.app.javashop.framework.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.javashop.model.member.dos.ReceiptHistory;
import com.enation.app.javashop.service.member.ReceiptHistoryManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 会员开票历史记录业务实现
 *
 * @author duanmingyu
 * @version v7.1.4
 * @since v7.0.0
 * 2019-06-20
 */
@Service
public class ReceiptHistoryManagerImpl implements ReceiptHistoryManager {

    @Autowired
    private ReceiptHistoryMapper receiptHistoryMapper;

    @Autowired
    private ReceiptFileMapper receiptFileMapper;

    @Override
    public WebPage list(long page, long pageSize, HistoryQueryParam params) {
        //新建查询条件包装器
        QueryWrapper<ReceiptHistoryVO> wrapper = new QueryWrapper<>();
        //以订单状态为已确认作为查询条件
        wrapper.eq("order_status", OrderStatusEnum.CONFIRM.value());
        //如果商家ID不为空，则以商家ID为查询条件
        wrapper.eq(params.getSellerId() != null, "seller_id", params.getSellerId());
        //如果会员ID不为空，则以会员ID为查询条件
        wrapper.eq(params.getMemberId() != null, "member_id", params.getMemberId());
        //如果查询关键字不为空，则以订单编号或商家名称为条件进行模糊查询
        wrapper.and(StringUtil.notEmpty(params.getKeyword()), ew -> {
            ew.like("order_sn", params.getKeyword()).or().like("seller_name", params.getKeyword());
        });
        //如果订单编号不为空，则以订单编号为条件进行模糊查询
        wrapper.like(StringUtil.notEmpty(params.getOrderSn()), "order_sn", params.getOrderSn());
        //如果商家名称不为空，则以商家名称为条件进行模糊查询
        wrapper.like(StringUtil.notEmpty(params.getSellerName()), "seller_name", params.getSellerName());
        //如果开票状态不为空，则以开票状态为查询条件 0：未开，1：已开
        wrapper.eq(StringUtil.notEmpty(params.getStatus()), "status", params.getStatus());
        //如果发票类型不为空，则以发票类型为查询条件 ELECTRO：电子普通发票，VATORDINARY：增值税普通发票，VATOSPECIAL：增值税专用发票
        wrapper.eq(StringUtil.notEmpty(params.getReceiptType()), "receipt_type", params.getReceiptType());
        //如果开票历史添加时间-查询时间范围开始时间不为空，则以添加时间大于等于这个查询范围时间为查询条件
        wrapper.ge(StringUtil.notEmpty(params.getStartTime()), "add_time", params.getStartTime());
        //如果开票历史添加时间-查询时间范围结束时间不为空，则以添加时间小于等于这个查询范围时间为查询条件
        wrapper.le(StringUtil.notEmpty(params.getEndTime()), "add_time", params.getEndTime());
        //如果用户名不为空，则以用户名为条件进行模糊查询
        wrapper.like(StringUtil.notEmpty(params.getUname()), "uname", params.getUname());
        //以添加时间倒序排序
        wrapper.orderByDesc("add_time");
        //获取会员开票历史记录分页列表数据
        IPage<ReceiptHistoryVO> iPage = receiptHistoryMapper.selectPageVo(new Page(page, pageSize), wrapper);
        return PageConvert.convert(iPage);
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ReceiptHistory add(ReceiptHistory receiptHistory) {
        //新增会员开票历史记录
        receiptHistoryMapper.insert(receiptHistory);
        return receiptHistory;
    }

    @Override
    public ReceiptHistory edit(ReceiptHistory receiptHistory, Long historyId) {
        //修改会员开票历史记录
        receiptHistoryMapper.updateById(receiptHistory);
        return receiptHistory;
    }

    @Override
    public ReceiptHistoryVO getReceiptHistory(String orderSn) {
        //新建查询条件包装器
        QueryWrapper<ReceiptHistory> wrapper = new QueryWrapper<>();
        //以订单编号为查询条件
        wrapper.eq("order_sn", orderSn);
        //查询一条会员开票记录
        ReceiptHistory receiptHistory = receiptHistoryMapper.selectOne(wrapper);
        //非空校验
        if (receiptHistory == null) {
            throw new ServiceException(MemberErrorCode.E150.code(), "会员开票历史记录不存在");
        }

        //新建会员开票历史记录对象VO
        ReceiptHistoryVO receiptHistoryVO = new ReceiptHistoryVO();
        //复制相关信息
        BeanUtil.copyProperties(receiptHistory, receiptHistoryVO);

        //获取电子发票附件
        receiptHistoryVO.setElecFileList(this.listElecFile(receiptHistoryVO.getHistoryId()));

        return receiptHistoryVO;
    }

    @Override
    public ReceiptHistoryVO get(Long historyId) {
        //获取发票详细信息
        ReceiptHistory receiptHistory = receiptHistoryMapper.selectById(historyId);
        //非空校验
        if (receiptHistory == null) {
            throw new ServiceException(MemberErrorCode.E150.code(), "会员开票历史记录不存在");
        }

        //新建会员开票历史记录对象VO
        ReceiptHistoryVO receiptHistoryVO = new ReceiptHistoryVO();
        //复制相关信息
        BeanUtil.copyProperties(receiptHistory, receiptHistoryVO);

        //获取电子发票附件
        receiptHistoryVO.setElecFileList(this.listElecFile(historyId));

        return receiptHistoryVO;
    }

    @Override
    public void updateLogi(Long historyId, Long logiId, String logiName, String logiCode) {
        //获取当前登录的商家信息并校验
        Seller seller = UserContext.getSeller();
        if (seller == null) {
            throw new ServiceException(MemberErrorCode.E137.code(), "当前商家登录信息已经失效");
        }

        //获取会员开票历史记录信息
        ReceiptHistoryVO receiptHistoryVO = this.get(historyId);
        //权限校验
        if (!receiptHistoryVO.getSellerId().equals(seller.getSellerId())) {
            throw new ServiceException(MemberErrorCode.E136.code(), "没有操作权限");
        }
        //发票类型校验
        if (!ReceiptTypeEnum.VATOSPECIAL.value().equals(receiptHistoryVO.getReceiptType())
                && !ReceiptTypeEnum.VATORDINARY.value().equals(receiptHistoryVO.getReceiptType())) {
            throw new ServiceException(MemberErrorCode.E150.code(), "发票类型错误，不可操作");
        }
        //物流公司校验
        if (logiId == null || StringUtil.isEmpty(logiName)) {
            throw new ServiceException(MemberErrorCode.E150.code(), "物流公司信息不能为空");
        }
        //快递单号校验
        if (StringUtil.isEmpty(logiCode)) {
            throw new ServiceException(MemberErrorCode.E150.code(), "快递单号不能为空");
        }

        //新建会员开票历史记录对象
        ReceiptHistory history = new ReceiptHistory();
        //设置主键ID
        history.setHistoryId(historyId);
        //设置物流公司ID
        history.setLogiId(logiId);
        //设置物流公司名称
        history.setLogiName(logiName);
        //设置快递单号
        history.setLogiCode(logiCode);
        //设置开票状态
        history.setStatus(1);
        //修改会员开票历史记录
        receiptHistoryMapper.updateById(history);
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void uploadFiles(ReceiptFileVO receiptFileVO) {
        //获取当前登录的商家信息并校验
        Seller seller = UserContext.getSeller();
        if (seller == null) {
            throw new ServiceException(MemberErrorCode.E137.code(), "当前商家登录信息已经失效");
        }

        //获取开票记录id并校验
        Long historyId = receiptFileVO.getHistoryId();
        if (historyId == null) {
            throw new ServiceException(MemberErrorCode.E147.code(), "参数错误");
        }

        //获取详细开票信息
        ReceiptHistoryVO receiptHistoryVO = this.get(historyId);
        //校验权限
        if (!receiptHistoryVO.getSellerId().equals(seller.getSellerId())) {
            throw new ServiceException(MemberErrorCode.E136.code(), "没有操作权限");
        }
        //校验发票类型
        if (!ReceiptTypeEnum.ELECTRO.value().equals(receiptHistoryVO.getReceiptType())) {
            throw new ServiceException(MemberErrorCode.E150.code(), "发票类型错误，不可操作");
        }
        //校验电子发票附件
        if (receiptFileVO.getFiles() == null || receiptFileVO.getFiles().size() == 0) {
            throw new ServiceException(MemberErrorCode.E150.code(), "电子发票附件不能为空");
        }

        //循环电子发票附件并入库
        for (String file : receiptFileVO.getFiles()) {
            ReceiptFileDO receiptFileDO = new ReceiptFileDO();
            receiptFileDO.setHistoryId(historyId);
            receiptFileDO.setElecFile(file);
            receiptFileMapper.insert(receiptFileDO);
        }

        //将会员开票历史记录信息标记为已开票状态
        ReceiptHistory history = new ReceiptHistory();
        history.setHistoryId(historyId);
        history.setStatus(1);
        receiptHistoryMapper.updateById(history);
    }

    @Override
    public void updatePriceByOrderSn(Double orderPrice, String orderSn) {
        //新建修改条件包装器
        UpdateWrapper<ReceiptHistory> wrapper = new UpdateWrapper<>();
        //修改订单价格
        wrapper.set("order_price", orderPrice);
        //以订单编号为修改条件
        wrapper.eq("order_sn", orderSn);
        //修改开票信息
        receiptHistoryMapper.update(new ReceiptHistory(), wrapper);
    }

    /**
     * 获取电子发票附件集合
     *
     * @param historyId 会员开票历史记录ID
     * @return
     */
    protected List<String> listElecFile(Long historyId) {
        //新建查询条件包装器
        QueryWrapper<ReceiptFileDO> wrapper = new QueryWrapper();
        //以开票记录ID为查询条件，查询电子发票附件
        wrapper.select("elec_file").eq("history_id", historyId);
        //获取电子发票附件集合
        List<ReceiptFileDO> fileList = receiptFileMapper.selectList(wrapper);
        List<String> res = new ArrayList<>();
        if (StringUtil.isNotEmpty(fileList)) {
            for (ReceiptFileDO file : fileList) {
                res.add(file.getElecFile());
            }
        }
        return res;
    }
}
