package com.enation.app.javashop.service.promotion.seckill.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.promotion.seckill.SeckillRangeMapper;
import com.enation.app.javashop.model.promotion.seckill.dos.SeckillRangeDO;
import com.enation.app.javashop.model.promotion.seckill.vo.TimeLineVO;
import com.enation.app.javashop.service.promotion.seckill.SeckillRangeManager;
import com.enation.app.javashop.service.promotion.tool.support.PromotionCacheKeys;
import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 限时抢购时刻业务类
 *
 * @author Snow
 * @version v2.0.0
 * @since v7.0.0
 * 2018-04-02 18:24:47
 */
@Service
public class SeckillRangeManagerImpl implements SeckillRangeManager {

    @Autowired
    private SeckillRangeMapper seckillRangeMapper;

    @Autowired
    private Cache cache;

    @Override
    public WebPage list(long page, long pageSize) {

        IPage<SeckillRangeDO> iPage = new QueryChainWrapper<>(seckillRangeMapper).page(new Page<>(page, pageSize));

        return PageConvert.convert(iPage);
    }

    @Override
    public SeckillRangeDO edit(SeckillRangeDO seckillRange, Long id) {
        seckillRange.setRangeId(id);
        seckillRangeMapper.updateById(seckillRange);
        return seckillRange;
    }

    @Override
    public void delete(Long id) {
        seckillRangeMapper.deleteById(id);
    }

    @Override
    public SeckillRangeDO getModel(Long id) {
        return seckillRangeMapper.selectById(id);
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {ServiceException.class, RuntimeException.class})
    public void addList(List<Integer> list, Long seckillId) {

        seckillRangeMapper.delete(new QueryWrapper<SeckillRangeDO>().
                //根据限时抢购活动id删除
                eq("seckill_id", seckillId)
        );
        List<Integer> termList = new ArrayList<>();
        for (Integer time : list) {
            termList.add(time);
            SeckillRangeDO seckillRangeDO = new SeckillRangeDO();
            seckillRangeDO.setSeckillId(seckillId);
            seckillRangeDO.setRangeTime(time);
            seckillRangeMapper.insert(seckillRangeDO);
        }
    }


    @Override
    public List<SeckillRangeDO> getList(Long seckillId) {

        List<SeckillRangeDO> list = new QueryChainWrapper<>(seckillRangeMapper)
                //按限时活动id查询
                .eq("seckill_id", seckillId)
                //列表查询
                .list();

        return list;
    }


    @Override
    public List<TimeLineVO> readTimeList() {
        long today = DateUtil.getDateline(DateUtil.toString(new Date(), "yyyy-MM-dd"));

        String redisKey = PromotionCacheKeys.getSeckillKey(DateUtil.toString(DateUtil.getDateline(), "yyyyMMdd"));
        Map<Integer, List> map = this.cache.getHash(redisKey);

        if (map.isEmpty()) {
            map =  new LinkedHashMap<>();
            List<SeckillRangeDO> list = seckillRangeMapper.selectReadTimeList(today);

            if (list.isEmpty()) {
                return new ArrayList<TimeLineVO>();
            }

            for (SeckillRangeDO rangeDO : list) {
                map.put(rangeDO.getRangeTime(), null);
            }
        }

        //读取系统时间的时刻
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);

        List<TimeLineVO> list = new ArrayList<>();

        //距离时刻的集合
        List<Long> distanceTimeList = new ArrayList<>();

        //未开始的活动
        for (Map.Entry<Integer, List> entry : map.entrySet()) {
            //大于当前的小时数
            if (entry.getKey() > hour) {

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String date = format.format(new Date());

                //当前时间的秒数
                long currentTime = DateUtil.getDateline();
                //限时抢购的时刻
                long timeLine = DateUtil.getDateline(date + " " + entry.getKey(), "yyyy-MM-dd HH");
                long distanceTime = timeLine - currentTime < 0 ? 0 : timeLine - currentTime;
                distanceTimeList.add(distanceTime);

                TimeLineVO timeLineVO = new TimeLineVO();
                timeLineVO.setTimeText(entry.getKey() + "");
                timeLineVO.setDistanceTime(distanceTime);
                list.add(timeLineVO);
            }
        }

        //正在进行中的活动的时刻
        int currentTime = -1;

        //正在进行的活动读取
        for (Map.Entry<Integer, List> entry : map.entrySet()) {
            //如果有时间相等的则直接将当前时间，设为正在进行中活动的时刻
            if (entry.getKey() == hour) {
                currentTime = hour;
                break;
            }

            //大于循环前面的时间,小于当前的时间
            if (entry.getKey() > currentTime && entry.getKey() <= hour) {
                currentTime = entry.getKey();
            }
        }

        //距离时刻的数据
        Long[] distanceTimes = new Long[distanceTimeList.size()];
        //排序
        distanceTimeList.toArray(distanceTimes);
        long distanceTime = distanceTimes.length > 1 ? distanceTimes[0] : 0;

        //如果当前时间大于等于0
        if (currentTime >= 0) {
            //正在进行中的活动
            TimeLineVO timeLine = new TimeLineVO();
            timeLine.setTimeText(currentTime + "");
            timeLine.setDistanceTime(0L);
            timeLine.setNextDistanceTime(distanceTime);
            list.add(0, timeLine);
        }

        return list;
    }


}
