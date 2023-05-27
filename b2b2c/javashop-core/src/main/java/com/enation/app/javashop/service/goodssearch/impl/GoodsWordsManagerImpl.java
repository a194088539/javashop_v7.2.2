package com.enation.app.javashop.service.goodssearch.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.sncreator.SnCreator;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.goods.GoodsMapper;
import com.enation.app.javashop.mapper.goods.GoodsWordsMapper;
import com.enation.app.javashop.model.base.SubCode;
import com.enation.app.javashop.model.base.rabbitmq.AmqpExchange;
import com.enation.app.javashop.model.goods.dos.GoodsDO;
import com.enation.app.javashop.model.goodssearch.GoodsWordsDO;
import com.enation.app.javashop.service.goodssearch.GoodsWordsManager;
import com.enation.app.javashop.model.errorcode.GoodsErrorCode;
import com.enation.app.javashop.model.goodssearch.enums.GoodsWordsType;
import com.enation.app.javashop.service.goodssearch.util.PinYinUtil;
import com.enation.app.javashop.framework.database.DaoSupport;
import com.enation.app.javashop.framework.database.StringMapper;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.util.StringUtil;
import com.enation.app.javashop.framework.rabbitmq.MessageSender;
import com.enation.app.javashop.framework.rabbitmq.MqMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fk
 * @version v2.0
 * @Description: 商品分词Manager
 * @date 2019/12/6 11:04
 * @since v7.0.0
 */
@Service
public class GoodsWordsManagerImpl implements GoodsWordsManager {

    @Autowired
    private MessageSender messageSender;

    @Autowired
    SnCreator snCreator;

    @Autowired
    private GoodsWordsMapper goodsWordsMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void addWord(String word) {

        //查询是否存在该分词
        Integer count = this.goodsWordsMapper.selectCount(new QueryWrapper<GoodsWordsDO>().eq("words", word));
        if (count > 0) {
            throw new ServiceException(GoodsErrorCode.E310.code(), "提示词【" + word + "】已存在");
        }
        //创建入库对象
        GoodsWordsDO goodsWordsDO = new GoodsWordsDO();
        goodsWordsDO.setGoodsNum(0L);
        goodsWordsDO.setQuanpin(PinYinUtil.getPingYin(word));
        goodsWordsDO.setSort(0);
        goodsWordsDO.setSzm(PinYinUtil.getPinYinHeadChar(word));
        goodsWordsDO.setType(GoodsWordsType.PLATFORM.name());
        goodsWordsDO.setWords(word);

        this.goodsWordsMapper.insert(goodsWordsDO);

        this.messageSender.send(new MqMessage(AmqpExchange.GOODS_WORDS_CHANGE, AmqpExchange.GOODS_WORDS_CHANGE + "_ROUTING", word));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateWords(String word, Long id) {
        //查询是否存在该分词
        Integer count = this.goodsWordsMapper.selectCount(new QueryWrapper<GoodsWordsDO>()
                .eq("words", word)
                .ne("id", id));
        if (count > 0) {
            return;
        }

        //更新分词相关信息
        this.goodsWordsMapper.update(new GoodsWordsDO(), new UpdateWrapper<GoodsWordsDO>()
                .set("words", word)
                .set("quanpin", PinYinUtil.getPingYin(word))
                .set("szm", PinYinUtil.getPinYinHeadChar(word))
                .eq("id", id));
        this.messageSender.send(new MqMessage(AmqpExchange.GOODS_WORDS_CHANGE, AmqpExchange.GOODS_WORDS_CHANGE + "_ROUTING", word));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateSort(Long id, Integer sort) {
        if (sort == null || sort < 0 || sort > 999999) {
            throw new ServiceException(GoodsErrorCode.E310.code(), "优先级范围值[0-999999],请重新输入");
        }
        this.goodsWordsMapper.update(new GoodsWordsDO(), new UpdateWrapper<GoodsWordsDO>()
                .set("sort", sort)
                .eq("id", id));
    }


    @Override
    public WebPage listPage(Long pageNo, Long pageSize, String keyword) {

        IPage iPage = this.goodsWordsMapper.selectPage(new Page<>(pageNo, pageSize), new QueryWrapper<GoodsWordsDO>()
                .select("id", "words", "quanpin", "sort", "type", "goods_num")
                .and(!StringUtil.isEmpty(keyword), e -> {
                    e.like("words", keyword).or().like("quanpin", keyword);
                })
                .orderByDesc("sort", "id"));

        return PageConvert.convert(iPage);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(GoodsWordsType goodsWordsType, Long id) {
        if (GoodsWordsType.SYSTEM.equals(goodsWordsType)) {
            this.goodsWordsMapper.update(new GoodsWordsDO(),
                    new UpdateWrapper<GoodsWordsDO>()
                            .set("goods_num", 0)
                            .eq("type", goodsWordsType.name()));
        } else {
            this.goodsWordsMapper.deleteById(id);
        }

    }

    @Override
    public void delete(String words) {

        this.goodsWordsMapper.reduceGoodsNum(words);
    }

    @Override
    public void addWords(String words) {

        //查询同名的分词
        Integer count = this.goodsWordsMapper.selectCount(new QueryWrapper<GoodsWordsDO>().eq("words", words));

        if (count <= 0) {
            //不存在，则添加
            GoodsWordsDO goodsWordsDO = new GoodsWordsDO();
            goodsWordsDO.setGoodsNum(1L);
            goodsWordsDO.setQuanpin(PinYinUtil.getPingYin(words));
            goodsWordsDO.setSort(0);
            goodsWordsDO.setSzm(PinYinUtil.getPinYinHeadChar(words));
            goodsWordsDO.setType(GoodsWordsType.SYSTEM.name());
            goodsWordsDO.setWords(words);

            this.goodsWordsMapper.insert(goodsWordsDO);
        } else {
            //存在则增加数量
            this.goodsWordsMapper.addGoodsNum(words);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateGoodsNum(String words) {
        //匹配已上架且审核通过的商品数量
        Integer goodsNum = this.goodsMapper.selectCount(new QueryWrapper<GoodsDO>()
                .like("goods_name", words)
                .eq("market_enable", 1)
                .eq("is_auth", 1));
        this.goodsWordsMapper.update(new GoodsWordsDO(), new UpdateWrapper<GoodsWordsDO>()
                .set("goods_num", goodsNum)
                .eq("words", words));
    }

    @Override
    public void batchUpdateGoodsNum() {
        //变更所有管理员添加的提示词相关商品数量   商品添加 或者修改时使用
        List<String> list = this.goodsWordsMapper.selectWordsList(GoodsWordsType.PLATFORM.name());

        list.forEach(str -> {
            this.updateGoodsNum(str);
        });
    }
}
