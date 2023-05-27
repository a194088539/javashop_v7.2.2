package com.enation.app.javashop.service.goods.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.security.model.Seller;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.framework.util.StringUtil;
import com.enation.app.javashop.mapper.goods.GoodsMapper;
import com.enation.app.javashop.mapper.goods.TagGoodsMapper;
import com.enation.app.javashop.mapper.goods.TagsMapper;
import com.enation.app.javashop.model.errorcode.GoodsErrorCode;
import com.enation.app.javashop.model.goods.dos.GoodsDO;
import com.enation.app.javashop.model.goods.dos.TagGoodsDO;
import com.enation.app.javashop.model.goods.dos.TagsDO;
import com.enation.app.javashop.model.goods.enums.TagType;
import com.enation.app.javashop.model.goods.vo.GoodsSelectLine;
import com.enation.app.javashop.model.goods.vo.TagGoodsNum;
import com.enation.app.javashop.service.goods.TagsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品标签业务类
 *
 * @author fk
 * @version v2.0
 * @since v7.0.0
 * 2018-03-28 14:49:36
 */
@Service
public class TagsManagerImpl implements TagsManager {

    @Autowired
    private TagsMapper tagsMapper;
    @Autowired
    private TagGoodsMapper tagGoodsMapper;
    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public WebPage list(long page, long pageSize) {

        Seller seller = UserContext.getSeller();

        IPage iPage = tagsMapper.selectPage(new Page<>(page, pageSize), new QueryWrapper<TagsDO>().eq("seller_id", seller.getSellerId()));

        return PageConvert.convert(iPage);
    }


    @Override
    @Transactional(value = "goodsTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void addShopTags(Long sellerId) {

        TagType[] tags = TagType.values();
        for (TagType tag : tags) {
            TagsDO tagDO = new TagsDO(tag.tagName(), sellerId, tag.mark());
            this.tagsMapper.insert(tagDO);
        }

    }


    @Override
    public WebPage queryTagGoods(Long tagId, Long pageNo, Long pageSize) {

        Seller seller = UserContext.getSeller();
        TagsDO tag = this.getModel(tagId);
        if (tag == null || !tag.getSellerId().equals(seller.getSellerId())) {
            throw new ServiceException(GoodsErrorCode.E309.code(), "无权操作");
        }

        IPage<Map> iPage = this.tagGoodsMapper.queryTagGoodsPage(new Page(pageNo, pageSize), tagId);

        return PageConvert.convert(iPage);
    }


    @Override
    @Transactional(value = "goodsTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveTagGoods(Long tagId, Long[] goodsIds) {

        Seller seller = UserContext.getSeller();
        TagsDO tag = this.getModel(tagId);
        if (tag == null || !tag.getSellerId().equals(seller.getSellerId())) {
            throw new ServiceException(GoodsErrorCode.E309.code(), "无权操作");
        }

        if (goodsIds[0] != -1) {

            Integer count = this.goodsMapper.selectCount(new QueryWrapper<GoodsDO>().in("goods_id", Arrays.asList(goodsIds)).eq("seller_id", seller.getSellerId()));

            if (goodsIds.length != count) {
                throw new ServiceException(GoodsErrorCode.E309.code(), "无权操作");
            }
        }

        //删除
        tagGoodsMapper.delete(new QueryWrapper<TagGoodsDO>().eq("tag_id", tagId));

        if (goodsIds[0] == -1) {
            //表示这个标签下不保存商品
            return;
        }
        //添加
        for (Long goodsId : goodsIds) {
            TagGoodsDO tagGoods = new TagGoodsDO(tagId, goodsId);
            this.tagGoodsMapper.insert(tagGoods);
        }
    }


    @Override
    public List<GoodsSelectLine> queryTagGoods(Long sellerId, Integer num, String mark) {

        TagsDO tag = this.tagsMapper.selectOne(new QueryWrapper<TagsDO>()
                .select("tag_id")
                .eq("seller_id", sellerId)
                .eq("mark", mark));

//        String sql = "select tag_id from es_tags where seller_id =? and  mark=?";
        long tagId = tag.getTagId();

//        sql = "select g.goods_id,g.goods_name,g.price,g.sn,g.thumbnail,g.big,g.quantity,g.buy_count from es_tag_goods r "
//                + " inner join es_goods g on g.goods_id=r.goods_id "
//                + " where g.disabled=1 and g.market_enable=1 and  r.tag_id=? limit 0,? ";

        return this.tagGoodsMapper.queryTagGoods(tagId, num);
    }

    @Override
    public TagsDO getModel(Long id) {

        return this.tagsMapper.selectById(id);
    }


    @Override
    public TagGoodsNum queryGoodsNumByShopId(Long shopId) {

        String sql = "select tag_id,mark from es_tags where seller_id=?";
        List<Map<String, Object>> tagIdList = this.tagsMapper.selectMaps(new QueryWrapper<TagsDO>()
                .select("tag_id", "mark")
                .eq("seller_id", shopId));

//        sql = "select count(0) num,tag_id from es_tag_goods where tag_id in (" + this.joinInStr(tagIdList) + ") group by tag_id";
//        List<Map> list = this.daoSupport.queryForList(sql);

        List<Long> ids = tagIdList.stream()
                .map(tag -> Long.parseLong(tag.get("tag_id").toString())).collect(Collectors.toList());

        List<Map<String, Object>> list = this.tagGoodsMapper.selectMaps(new QueryWrapper<TagGoodsDO>()
                .select("count(0) num", "tag_id")
                .in("tag_id", ids)
                .groupBy("tag_id"));


        Integer hotNum = 0;
        Integer newNum = 0;
        Integer recommendNum = 0;

        if (StringUtil.isNotEmpty(list)) {
            for (Map map : list) {
                String mark = this.getMark(tagIdList, (Long) map.get("tag_id"));
                Integer count = Integer.valueOf(map.get("num").toString());

                switch (mark) {
                    case "hot":
                        hotNum = count;
                        break;
                    case "new":
                        newNum = count;
                        break;
                    case "recommend":
                        recommendNum = count;
                        break;
                    default:
                        break;
                }

            }
        }

        TagGoodsNum tagGoodsNum = new TagGoodsNum(hotNum, newNum, recommendNum);

        return tagGoodsNum;
    }


    /**
     * 根据tagid获取mark
     *
     * @param tagIdList
     * @param tagId
     * @return
     */
    String getMark(List<Map<String, Object>> tagIdList, Long tagId) {
        for (Map map : tagIdList) {
            if (tagId.equals(map.get("tag_id"))) {
                return map.get("mark").toString();
            }
        }
        return "";
    }
}
