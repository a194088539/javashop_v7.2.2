package com.enation.app.javashop.service.goods.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.enation.app.javashop.client.system.SettingClient;
import com.enation.app.javashop.client.system.UploadFactoryClient;
import com.enation.app.javashop.framework.util.JsonUtil;
import com.enation.app.javashop.mapper.goods.GoodsGalleryMapper;
import com.enation.app.javashop.model.base.SettingGroup;
import com.enation.app.javashop.model.goods.dos.GoodsGalleryDO;
import com.enation.app.javashop.model.goods.dto.GoodsSettingVO;
import com.enation.app.javashop.service.goods.GoodsGalleryManager;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品相册业务类
 *
 * @author fk
 * @version v2.0
 * @since v7.0.0
 * 2018-03-21 11:39:54
 */
@Service
public class GoodsGalleryManagerImpl implements GoodsGalleryManager {

    @Autowired
    private SettingClient settingClient;
    @Autowired
    private UploadFactoryClient uploadFactoryClient;

    @Autowired
    private GoodsGalleryMapper goodsGalleryMapper;

    @Override
    @Transactional(value = "goodsTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public GoodsGalleryDO add(GoodsGalleryDO goodsGallery) {
        this.goodsGalleryMapper.insert(goodsGallery);

        return goodsGallery;
    }

    @Override
    @Transactional(value = "goodsTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public GoodsGalleryDO edit(GoodsGalleryDO goodsGallery, Long id) {
        goodsGallery.setImgId(id);
        this.goodsGalleryMapper.updateById(goodsGallery);
        return goodsGallery;
    }

    @Override
    @Transactional(value = "goodsTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(Long id) {
        this.goodsGalleryMapper.deleteById(id);
    }

    @Override
    public GoodsGalleryDO getModel(Long id) {

        return this.goodsGalleryMapper.selectById(id);
    }

    @Override
    public GoodsGalleryDO getGoodsGallery(String origin) {

        GoodsGalleryDO goodsGallery = new GoodsGalleryDO();

        String photoSizeSettingJson = settingClient.get(SettingGroup.GOODS);

        GoodsSettingVO photoSizeSetting = JsonUtil.jsonToObject(photoSizeSettingJson, GoodsSettingVO.class);

        //缩略图
        String thumbnail = uploadFactoryClient.getUrl(origin, photoSizeSetting.getThumbnailWidth(), photoSizeSetting.getThumbnailHeight());
        //小图
        String small = uploadFactoryClient.getUrl(origin, photoSizeSetting.getSmallWidth(), photoSizeSetting.getSmallHeight());
        //大图
        String big = uploadFactoryClient.getUrl(origin, photoSizeSetting.getBigWidth(), photoSizeSetting.getBigHeight());
        //赋值
        goodsGallery.setBig(big);
        goodsGallery.setSmall(small);
        goodsGallery.setThumbnail(thumbnail);
        goodsGallery.setOriginal(origin);
        return goodsGallery;
    }

    @Override
    @Transactional(value = "goodsTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void add(List<GoodsGalleryDO> goodsGalleryList, Long goodsId) {

        int i = 0;
        for (GoodsGalleryDO origin : goodsGalleryList) {
            // 获取带所有缩略的相册
            GoodsGalleryDO galley = this.getGoodsGallery(origin.getOriginal());
            galley.setGoodsId(goodsId);
            /** 默认第一个为默认图片 */
            if (i == 0) {
                galley.setIsdefault(1);
            } else {
                galley.setIsdefault(0);
            }
            i++;
            this.add(galley);
        }

    }

    @Override
    @Transactional(value = "goodsTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void edit(List<GoodsGalleryDO> goodsGalleryList, Long goodsId) {
        // 删除没有用到的商品相册信息
        this.delNoUseGalley(goodsGalleryList, goodsId);
        //用于Mapper更新使用的对象
        GoodsGalleryDO galleryUpdateDO = new GoodsGalleryDO();
        int i = 0;
        // 如果前台传入id不为-1，则新增商品图片到此商品的相册中 添加相册

        for (GoodsGalleryDO goodsGallery : goodsGalleryList) {
            //已有图片且默认
            if (goodsGallery.getImgId() != -1 && i == 0) {
                //将此图片设置为默认
                galleryUpdateDO.setIsdefault(1);
                this.goodsGalleryMapper.update(galleryUpdateDO,
                        new UpdateWrapper<GoodsGalleryDO>()
                                .eq("img_id",goodsGallery.getImgId()));
                //将其他图片设置为不默认
                galleryUpdateDO.setIsdefault(0);
                this.goodsGalleryMapper.update(galleryUpdateDO,
                        new UpdateWrapper<GoodsGalleryDO>()
                                .ne("img_id",goodsGallery.getImgId())
                                .eq("goods_id",goodsId));

                GoodsGalleryDO temp = this.getModel(goodsGallery.getImgId());
                goodsGallery.setBig(temp.getBig());
                goodsGallery.setOriginal(temp.getOriginal());
                goodsGallery.setSmall(temp.getSmall());
                goodsGallery.setThumbnail(temp.getThumbnail());
                goodsGallery.setTiny(temp.getTiny());
            }
            //新增的图片
            if (goodsGallery.getImgId() == -1) {
                //获取带所有缩略的相册
                GoodsGalleryDO galley = this.getGoodsGallery(goodsGallery.getOriginal());
                galley.setGoodsId(goodsId);
                // 默认第一个为默认图片
                if (i == 0) {
                    galley.setIsdefault(1);
                    galleryUpdateDO.setIsdefault(0);
                    this.goodsGalleryMapper.update(galleryUpdateDO,
                            new UpdateWrapper<GoodsGalleryDO>()
                                    .eq("goods_id",goodsId));
                } else {
                    galley.setIsdefault(0);
                }
                this.goodsGalleryMapper.insert(galley);
                BeanUtils.copyProperties(galley, goodsGallery);
            }
            galleryUpdateDO.setIsdefault(null);
            galleryUpdateDO.setSort(i);
            this.goodsGalleryMapper.update(galleryUpdateDO,
                    new UpdateWrapper<GoodsGalleryDO>()
                            .eq("img_id",goodsGallery.getImgId()));
            i++;
        }

    }


    /**
     * 删除没有用到的商品相册信息
     *
     * @param galleryList 商品相册
     * @param goodsId     商品id
     */
    private void delNoUseGalley(List<GoodsGalleryDO> galleryList, Long goodsId) {
        // 将传入的商品图片id进行拼接
        List<Long> imgIds = galleryList.stream().map(m->m.getImgId()).collect(Collectors.toList());
        // 删除掉不在此商品相册中得图片
        this.goodsGalleryMapper.delete(
                new QueryWrapper<GoodsGalleryDO>()
                        .notIn("img_id",imgIds)
                        .eq("goods_id",goodsId));
    }

    @Override
    public List<GoodsGalleryDO> list(Long goodsId) {

        List<GoodsGalleryDO> result = this.goodsGalleryMapper.selectList(
                new QueryWrapper<GoodsGalleryDO>()
                        .eq("goods_id", goodsId)
                        .orderByAsc("sort")
                        .orderByDesc("isdefault"));
        return result;
    }

    @Override
    public void delete(Long[] goodsIds) {

        this.goodsGalleryMapper.delete(
                new QueryWrapper<GoodsGalleryDO>()
                        .in("goods_id", Arrays.asList(goodsIds)));
    }
}
