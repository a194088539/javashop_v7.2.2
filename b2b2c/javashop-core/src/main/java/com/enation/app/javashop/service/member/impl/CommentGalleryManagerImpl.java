package com.enation.app.javashop.service.member.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.enation.app.javashop.mapper.member.CommentGalleryMapper;
import com.enation.app.javashop.model.member.dos.CommentGallery;
import com.enation.app.javashop.service.member.CommentGalleryManager;
import com.enation.app.javashop.framework.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 评论图片业务类
 *
 * @author fk
 * @version v1.0
 * @since v7.0.0
 * 2018-05-03 14:11:46
 */
@Service
public class CommentGalleryManagerImpl implements CommentGalleryManager {

    @Autowired
    private CommentGalleryMapper commentGalleryMapper;

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void add(Long commentId, List<String> list) {
        //校验集合是否为空
        if (StringUtil.isNotEmpty(list)) {
            int i = 0;
            //循环入库
            for (String image : list) {
                //新建评论图片相册对象
                CommentGallery commentGallery = new CommentGallery();
                //设置评论ID
                commentGallery.setCommentId(commentId);
                //设置评论图片
                commentGallery.setOriginal(image);
                //设置排序值
                commentGallery.setSort(i);
                //评论图片入库
                commentGalleryMapper.insert(commentGallery);
                i++;
            }
        }
    }

    @Override
    public Map<Long, List<String>> getGalleryByCommentIds(Long commentIds) {
        //新建查询条件包装器
        QueryWrapper<CommentGallery> wrapper = new QueryWrapper<>();
        //以评论ID作为查询条件
        wrapper.eq("comment_id", commentIds);
        //查询评论图片信息集合
        List<CommentGallery> resList = commentGalleryMapper.selectList(wrapper);
        //新建map对象
        Map<Long, List<String>> resMap = new HashMap<>(resList.size());
        //循环初始化map对象
        for(CommentGallery image : resList){
            Long commentId = image.getCommentId();
            List<String> imageList = resMap.get(commentId);
            if(imageList == null){
                imageList = new ArrayList<>();
            }
            imageList.add(image.getOriginal());
            resMap.put(commentId,imageList);
        }
        return resMap;
    }
}
