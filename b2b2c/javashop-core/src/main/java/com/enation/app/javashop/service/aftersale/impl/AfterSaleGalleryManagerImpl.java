package com.enation.app.javashop.service.aftersale.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.enation.app.javashop.mapper.trade.aftersale.AfterSaleGalleryMapper;
import com.enation.app.javashop.model.aftersale.dos.AfterSaleGalleryDO;
import com.enation.app.javashop.service.aftersale.AfterSaleGalleryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 售后服务图片业务接口实现
 * @author duanmingyu
 * @version v1.0
 * @since v7.1.5
 * 2019-12-03
 */
@Service
public class AfterSaleGalleryManagerImpl implements AfterSaleGalleryManager {

    @Autowired
    private AfterSaleGalleryMapper afterSaleGalleryMapper;

    @Override
    public void add(AfterSaleGalleryDO galleryDO) {
        //新增售后图片信息
        afterSaleGalleryMapper.insert(galleryDO);
    }

    @Override
    public void fillImage(String serviceSn, List<AfterSaleGalleryDO> images) {
        //如果售后图片信息集合不为空并且长度不为0
        if (images != null && images.size() != 0) {
            //售后图片循环入库
            for (AfterSaleGalleryDO image : images) {
                //设置售后服务单号
                image.setServiceSn(serviceSn);
                //新增售后图片信息
                this.add(image);
            }
        }
    }

    @Override
    public List<AfterSaleGalleryDO> listImages(String serviceSn) {
        //新建查询条件包装器
        QueryWrapper<AfterSaleGalleryDO> wrapper = new QueryWrapper<>();
        //以售后服务单号为查询条件
        wrapper.eq("service_sn", serviceSn);
        //返回售后图片信息集合
        return afterSaleGalleryMapper.selectList(wrapper);
    }
}
