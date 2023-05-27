package com.enation.app.javashop.shard;

import com.enation.app.javashop.framework.database.DaoSupport;
import com.enation.app.javashop.framework.test.BaseTest;
import com.enation.app.javashop.model.goods.dos.GoodsDO;
import com.enation.app.javashop.model.goods.dos.GoodsSkuDO;
import com.enation.app.javashop.model.goods.dos.TagGoodsDO;
import com.enation.app.javashop.model.goods.dos.TagsDO;
import com.enation.app.javashop.model.goods.enums.TagType;
import com.enation.app.javashop.model.goods.vo.TagGoodsNum;
import com.enation.app.javashop.service.goods.TagsManager;
import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import java.util.List;

import static com.enation.app.javashop.framework.util.StringUtil.random;

/**
 * @author kingapex
 * @version 1.0
 * @since 7.1.0
 * 2020/6/15
 */
@Rollback(false)
public class GoodsShardTest extends BaseTest {
    long sellerId = 1;
    @Autowired
    DaoSupport daoSupport;

    @Autowired
    TagsManager tagsManager;


    @Test
    public void queryTagGoodsTest() {
        TagGoodsNum goodsNum = tagsManager.queryGoodsNumByShopId(sellerId);
        System.out.println(goodsNum);
        List list =tagsManager.queryTagGoods(sellerId, 100, TagType.NEW.mark());
        System.out.println(list);
    }

    @Test
    public void goodsTagTest() {

        Long hotTagId = 1L;
        Long newTagId = 1L;
        TagType[] tags = TagType.values();
        for (TagType tag : tags) {
            TagsDO tagDO = new TagsDO(tag.tagName(), sellerId, tag.mark());

            this.daoSupport.insert(tagDO);

            Long tagId = daoSupport.getLastId("");
            if (tag.mark().equals(TagType.HOT.mark())) {
                hotTagId = tagId;
            }

            if (tag.mark().equals(TagType.NEW.mark())) {
                newTagId = tagId;
            }
        }

        System.out.println("newTagId: "+ newTagId);
        System.out.println("hotTagId: "+ hotTagId);

        for (int i = 1; i <= 10; i++) {
            GoodsDO goods = new GoodsDO();
            goods.setSellerId(Long.valueOf( random()));
            goods.setGoodsName("test" + i);
            goods.setQuantity(1);
            goods.setEnableQuantity(1);
            goods.setDisabled(1);
            goods.setMarketEnable(1);

            daoSupport.insert(goods);
            Long goodsId = daoSupport.getLastId("");

            TagGoodsDO hotTagGoods = new TagGoodsDO();
            hotTagGoods.setGoodsId(goodsId);
            hotTagGoods.setTagId(hotTagId);

            TagGoodsDO newTagGoods = new TagGoodsDO();
            newTagGoods.setGoodsId(goodsId);
            newTagGoods.setTagId(newTagId);

            this.daoSupport.insert( hotTagGoods);
            this.daoSupport.insert( newTagGoods);
        }

        this.queryTagGoodsTest();

    }

    @Test
    public void goodsSkuTest() {
        for (int i = 1; i <= 100; i++) {
            GoodsDO goods = new GoodsDO();
            goods.setSellerId(Long.valueOf(random()));
            goods.setGoodsName("test" + i);
            goods.setQuantity(1);
            goods.setEnableQuantity(1);

            daoSupport.insert(goods);

            GoodsSkuDO goodsSku = new GoodsSkuDO();
            BeanUtils.copyProperties(goods, goodsSku);
            goodsSku.setEnableQuantity(goodsSku.getQuantity());
            goodsSku.setHashCode(-1);
            this.daoSupport.insert("es_goods_sku", goodsSku);
        }
    }
}
