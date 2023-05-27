package com.enation.app.javashop.service.goodssearch.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.goods.CustomWordsMapper;
import com.enation.app.javashop.model.errorcode.GoodsErrorCode;
import com.enation.app.javashop.framework.context.request.ThreadContextHolder;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.framework.util.StringUtil;
import com.enation.app.javashop.service.goodssearch.CustomWordsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.javashop.framework.database.DaoSupport;
import com.enation.app.javashop.model.goodssearch.CustomWords;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义分词表业务类
 *
 * @author fk
 * @version v1.0
 * @since v7.0.0
 * 2018-06-20 16:08:07
 *
 * update by liuyulei 2019-05-27
 */
@Service
public class CustomWordsManagerImpl implements CustomWordsManager {

    @Autowired
    private CustomWordsMapper customWordsMapper;

    @Override
    public WebPage list(long page, long pageSize, String keywords) {

        IPage iPage = customWordsMapper.selectPage(new Page<>(page,pageSize),
                new QueryWrapper<CustomWords>()
                        .eq("disabled",1)
                        //如果关键字不为空，则模糊查询name
                        .like(!StringUtil.isEmpty(keywords),"name",keywords)
                        //修改时间倒叙
                        .orderByDesc("modify_time"));

        return PageConvert.convert(iPage);
    }

    @Override
    @Transactional(value = "goodsTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public CustomWords add(CustomWords customWords) {

        //验证分词是否已经存在
        this.validKeywords(customWords.getName());

        customWords.setAddTime(DateUtil.getDateline());
        customWords.setModifyTime(DateUtil.getDateline());
        customWords.setDisabled(1);
        this.customWordsMapper.insert(customWords);
        return customWords;
    }

    @Override
    @Transactional(value = "goodsTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public CustomWords edit(CustomWords customWords, Long id) {
        //验证分词是否已经存在
        validKeywords(customWords.getName());
        customWords.setDisabled(1);
        customWords.setModifyTime(DateUtil.getDateline());
        customWords.setId(id);
        this.customWordsMapper.updateById(customWords);
        return customWords;
    }

    @Override
    @Transactional(value = "goodsTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(Long id) {

        CustomWords words = this.getModel(id);
        words.setModifyTime(DateUtil.getDateline());
        words.setDisabled(0);
        this.customWordsMapper.updateById(words);
    }

    @Override
    public CustomWords getModel(Long id) {
        return this.customWordsMapper.selectById(id);
    }

    @Override
    public String deploy() {

        List<CustomWords> list = this.customWordsMapper.selectList(new QueryWrapper<CustomWords>()
                .eq("disabled",1)
                .orderByDesc("modify_time"));

        HttpServletResponse response = ThreadContextHolder.getHttpResponse();

        StringBuffer buffer = new StringBuffer();

        if (StringUtil.isNotEmpty(list)) {
            int i = 0;
            for (CustomWords word : list) {

                if (i == 0) {


                    SimpleDateFormat format =   new SimpleDateFormat( "yyyy-MM-dd hh:mm:ss" );
                    try {
                        response.setHeader("Last-Modified", format.parse(DateUtil.toString(word.getAddTime(),"yyyy-MM-dd hh:mm:ss")) + "");
                        response.setHeader("ETag", format.parse(DateUtil.toString(word.getModifyTime(),"yyyy-MM-dd hh:mm:ss")) + "");
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    buffer.append(word.getName());
                } else {
                    buffer.append("\n" + word.getName());
                }

                i++;
            }
        }

        return buffer.toString();
    }

    @Override
    public boolean isExist(String keyword) {
        int count = this.customWordsMapper.selectCount(new QueryWrapper<CustomWords>()
                .eq("name",keyword)
                .eq("disabled",1));

        if(count > 0){
            return true;
        }
        return false;
    }

    /**
     * 验证分词是否窜在
     * @param keyword
     */
    private void validKeywords(String keyword){
        boolean isExist = this.isExist(keyword);
        if(isExist){
            throw new ServiceException(GoodsErrorCode.E310.code(),"【" + keyword + "】已存在");
        }
    }
}
