package com.enation.app.javashop.service.member.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.client.goods.GoodsClient;
import com.enation.app.javashop.client.member.MemberClient;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.member.HistoryMapper;
import com.enation.app.javashop.model.goods.vo.CacheGoods;
import com.enation.app.javashop.model.member.dos.HistoryDO;
import com.enation.app.javashop.model.member.dos.Member;
import com.enation.app.javashop.model.member.dto.HistoryDTO;
import com.enation.app.javashop.model.member.dto.HistoryDelDTO;
import com.enation.app.javashop.model.member.vo.HistoryVO;
import com.enation.app.javashop.service.member.HistoryManager;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 会员足迹业务类
 *
 * @author zh
 * @version v7.1.4
 * @since vv7.1
 * 2019-06-18 15:18:56
 */
@Service
public class HistoryManagerImpl implements HistoryManager {

    @Autowired
    private HistoryMapper historyMapper;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private MemberClient memberClient;

    @Override
    public WebPage list(long page, long pageSize) {
        //新建查询条件包装器
        QueryWrapper wrapper = new QueryWrapper();
        //以会员ID为查询条件
        wrapper.eq("member_id", UserContext.getBuyer().getUid());
        //以最后更改时间分组
        wrapper.groupBy("update_time");
        //以添加时间倒序排序
        wrapper.orderByDesc("create_time");
        //根据天的日期分组，分页查询出日期
        IPage iPage = historyMapper.selectUpdateTimePage(new Page(page, pageSize), wrapper);
        //取出结果集合
        List<Map<String, Object>> data = iPage.getRecords();
        if (data != null && data.size() > 0) {
            List<HistoryVO> historyVOS = new ArrayList<>();
            List<Long> timeList = new ArrayList<>();
            //从结果中提取天的时间并且组织成以下sql查询的条件
            for (Map map : data) {
                HistoryVO historyVO = new HistoryVO();
                historyVO.setTime((long) map.get("update_time"));
                historyVOS.add(historyVO);
                timeList.add((long) map.get("update_time"));
            }

            //新建查询条件包装器
            QueryWrapper<HistoryDO> queryWrapper = new QueryWrapper<>();
            //以会员ID为查询条件
            queryWrapper.eq("member_id", UserContext.getBuyer().getUid());
            //以最后更改时间集合为查询条件
            queryWrapper.in("update_time", timeList);
            //以添加时间倒序排序
            queryWrapper.orderByDesc("create_time");
            //根据日期查询出此会员在以上结果的天里面的浏览足迹
            List<HistoryDO> historyDOS = historyMapper.selectList(queryWrapper);

            //将查询出的商品组织成要输出的格式，格式是
            //data[{
            //  time:00000
            //  history:{historyDo,historyDO}
            // }]
            for (HistoryVO historyVO : historyVOS) {
                List<HistoryDO> list = new ArrayList<>();
                for (HistoryDO history : historyDOS) {
                    if (history.getUpdateTime().equals(historyVO.getTime())) {
                        list.add(history);
                    }
                    historyVO.setHistory(list);
                }
            }

            //将组织好的数据放入返回对象里面
            iPage.setRecords(historyVOS);
        }
        return PageConvert.convert(iPage);
    }


    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public HistoryDO edit(HistoryDO historyDO, Long id) {
        //设置主键ID
        historyDO.setId(id);
        //修改浏览记录信息
        historyMapper.updateById(historyDO);
        return historyDO;
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(HistoryDelDTO historyDelDTO) {
        //新建查询条件包装器
        QueryWrapper<HistoryDO> wrapper = new QueryWrapper<>();
        //以会员ID为查询条件
        wrapper.eq("member_id", historyDelDTO.getMemberId());
        //如果日期不为空，则将日期作为查询条件
        wrapper.eq(historyDelDTO.getDate() != null, "update_time", historyDelDTO.getDate());
        //如果id不为空，则将id作为查询条件
        wrapper.eq(historyDelDTO.getId() != null, "id", historyDelDTO.getId());
        //删除浏览记录
        historyMapper.delete(wrapper);
    }

    @Override
    public void delete(Long memberId) {
        //新建查询条件包装器
        QueryWrapper wrapper = new QueryWrapper();
        //以会员ID为查询条件
        wrapper.eq("member_id", memberId);
        //以添加时间倒序排序
        wrapper.orderByDesc("create_time");
        //获取前100条数据
        wrapper.last("limit 99");
        //根据时间最新查询100条数据
        List<Long> ids = historyMapper.selectHistoryId(wrapper);

        //新建删除条件包装器
        wrapper = new QueryWrapper();
        //以会员ID为删除条件
        wrapper.eq("member_id", memberId);
        //并过滤掉上面查询出的前100条数据
        wrapper.notIn("id", ids);
        //删除多余数据
        historyMapper.delete(wrapper);


    }

    @Override
    public HistoryDO getModel(Long id) {
       return historyMapper.selectById(id);
    }

    @Override
    public HistoryDO getHistoryByGoods(Long goodsId, Long memberId) {
        //新建查询条件包装器
        QueryWrapper<HistoryDO> wrapper = new QueryWrapper<>();
        //以商品ID为查询条件
        wrapper.eq("goods_id", goodsId);
        //以会员ID为查询条件
        wrapper.eq("member_id", memberId);
        return historyMapper.selectOne(wrapper);
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void addMemberHistory(HistoryDTO historyDTO) {
        //会员id
        Long memberId = historyDTO.getMemberId();
        //校验此商品是否存在并且上架状态
        CacheGoods cacheGoods = goodsClient.getFromCache(historyDTO.getGoodsId());
        //如果商品为下架状态则不记录足迹
        if (!cacheGoods.getMarketEnable().equals(1)) {
            return;
        }
        //检测此商品是否已经存在浏览记录
        HistoryDO historyDO = this.getHistoryByGoods(historyDTO.getGoodsId(), historyDTO.getMemberId());
        //如果为空则是添加反之为修改
        if (historyDO != null) {
            historyDO.setCreateTime(DateUtil.getDateline());
            historyDO.setUpdateTime(getDateDay());
            historyDO.setGoodsName(cacheGoods.getGoodsName());
            historyDO.setGoodsPrice(cacheGoods.getPrice());
            historyDO.setGoodsImg(cacheGoods.getThumbnail());
            this.edit(historyDO, historyDO.getId());
            return;
        }

        //新建查询条件包装器
        QueryWrapper<HistoryDO> wrapper = new QueryWrapper<>();
        //以会员ID为查询条件
        wrapper.eq("member_id", memberId);
        //根据条件获取会员浏览记录数量
        Integer count = historyMapper.selectCount(wrapper);
        //校验如果从会员足迹已经超过一百个，需要删除历史最早的
        if (count >= 100) {
            this.delete(historyDTO.getMemberId());
        }
        //获取当前会员
        Member member = memberClient.getModel(historyDTO.getMemberId());
        //如果当前会员不存在，则不记录信息
        if (member == null) {
            return;
        }
        //新建会员浏览足迹最新
        historyDO = new HistoryDO();
        //设置商品图片
        historyDO.setGoodsImg(cacheGoods.getThumbnail());
        //设置商品名称
        historyDO.setGoodsName(cacheGoods.getGoodsName());
        //设置商品ID
        historyDO.setGoodsId(historyDTO.getGoodsId());
        //设置商品价格
        historyDO.setGoodsPrice(cacheGoods.getPrice());
        //设置会员ID
        historyDO.setMemberId(memberId);
        //设置会员名称
        historyDO.setMemberName(member.getUname());
        //设置添加时间
        historyDO.setCreateTime(DateUtil.getDateline());
        //设置更新时间
        historyDO.setUpdateTime(getDateDay());
        //会员浏览足迹入库
        historyMapper.insert(historyDO);
    }

    /**
     * 获取当前天的时间戳
     *
     * @return 当前天的时间戳
     */
    private static long getDateDay() {
        String res = DateUtil.toString(new Date(), "yyyy-MM-dd");
        return DateUtil.getDateline(res);
    }
}
