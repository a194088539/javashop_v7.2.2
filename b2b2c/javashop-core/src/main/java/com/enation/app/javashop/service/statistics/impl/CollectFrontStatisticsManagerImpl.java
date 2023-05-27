package com.enation.app.javashop.service.statistics.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.statistics.GoodsDataMapper;
import com.enation.app.javashop.model.statistics.dto.GoodsData;
import com.enation.app.javashop.model.statistics.vo.ChartSeries;
import com.enation.app.javashop.model.statistics.vo.SimpleChart;
import com.enation.app.javashop.service.statistics.CollectFrontStatisticsManager;
import com.enation.app.javashop.framework.database.WebPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 商家中心，商品收藏统计实现类
 *
 * @author mengyuanming
 * @version 2.0
 * @since 7.0
 * 2018年4月20日下午4:35:26
 */
@Service
public class CollectFrontStatisticsManagerImpl implements CollectFrontStatisticsManager {

    @Autowired
    private GoodsDataMapper goodsDataMapper;


    /**
     * 商品收藏数量统计
     *
     * @param sellerId，商家id
     * @return simpleChart 简单图表数据
     */
    @Override
    public SimpleChart getChart(Long sellerId) {

        // 从es_sss_goods_data表中查询，商品名称，收藏数量
        QueryWrapper<GoodsData> queryWrapper = new QueryWrapper<GoodsData>()
                //查询商品id，商品名称，收藏数量
                .select("goods_id", "favorite_num", "goods_name")
                //拼接商家id查询条件
                .eq("seller_id", sellerId)
                //按收藏数量倒序
                .orderByDesc("favorite_num")
                //查询前50条
                .last("limit 50");

        List<Map<String, Object>> list = goodsDataMapper.selectMaps(queryWrapper);

        // 收藏数量数组，对应chart数据
        String[] data = new String[list.size()];

        // 商品名数组，对应chart数据名称
        String[] localName = new String[list.size()];

        // x轴刻度，从1开始，以数据量为准，没有数据则为0
        String[] xAxis = new String[list.size()];

        // 如果有数据，则加入数组
        if (!list.isEmpty()) {
            int i = 0;
            for (Map<String, Object> map : list) {
                data[i] = map.get("favorite_num").toString();
                localName[i] = map.get("goods_name").toString();
                xAxis[i] = i + 1 + "";
                i++;
            }
        }

        ChartSeries series = new ChartSeries("收藏数", data, localName);

        // 数据，x轴刻度，y轴刻度
        return new SimpleChart(series, xAxis, new String[0]);
    }

    /**
     * 商品收藏统计表格
     *
     * @param pageNo，页码
     * @param pageSize，页面数据量
     * @param sellerId，商家id
     * @return WebPage 分页数据
     */
    @Override
    public WebPage getPage(Long pageNo, Long pageSize, Long sellerId) {

        // 获取商品名，收藏数量，商品价格的正在出售的商品，按收藏数量排序
        IPage<GoodsData> page = new QueryChainWrapper<>(goodsDataMapper)
                //查询商品id，商品名称，收藏数量，价格
                .select("goods_id", "goods_name", "favorite_num", "price")
                //拼接商家id查询条件
                .eq("seller_id", sellerId)
                //拼接是否上架查询条件， 1 上架 0下架
                .eq("market_enable", 1)
                //按收藏数量倒序
                .orderByDesc("favorite_num")
                //分页查询
                .page(new Page<>(pageNo, pageSize));

        return PageConvert.convert(page);
    }

}
