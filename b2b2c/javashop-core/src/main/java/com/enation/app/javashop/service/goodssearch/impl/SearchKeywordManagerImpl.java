package com.enation.app.javashop.service.goodssearch.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.goods.SearchKeywordMapper;
import com.enation.app.javashop.model.errorcode.GoodsErrorCode;
import com.enation.app.javashop.model.goodssearch.SearchKeywordDO;
import com.enation.app.javashop.service.goodssearch.SearchKeywordManager;
import com.enation.app.javashop.framework.database.DaoSupport;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.framework.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
* @author liuyulei
 * @version 1.0
 * @Description:  搜索关键词业务实现类
 * @date 2019/5/27 11:14
 * @since v7.0
 */
@Service
public class SearchKeywordManagerImpl implements SearchKeywordManager {

    @Autowired
    private SearchKeywordMapper searchKeywordMapper;

    @Override
    @Transactional(value = "goodsTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void add(String keyword) {
        if (StringUtil.isEmpty(keyword)) {
            throw new ServiceException(GoodsErrorCode.E310.code(), "关键字不能为空！");
        }
        SearchKeywordDO searchKeywordDO = new SearchKeywordDO(keyword);
        this.searchKeywordMapper.insert(searchKeywordDO);

    }

    @Override
    public WebPage list(Long pageNo, Long pageSize, String keyword) {

        IPage iPage = this.searchKeywordMapper.selectPage(new Page<>(pageNo,pageSize),
                new QueryWrapper<SearchKeywordDO>()
                        .gt("count",0)
                        //如果关键字不为空，则模糊查询keyword
                        .like(!StringUtil.isEmpty(keyword),"keyword",keyword)
                        .orderByDesc("count","id"));


        return PageConvert.convert(iPage);

    }

    @Override
    @Transactional(value = "goodsTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void update(String keyword) {
        if (StringUtil.isEmpty(keyword)) {
            return;
        }
//        String sql = "update es_keyword_search_history set count = count+1,modify_time = ? where keyword = ? ";
//        this.goodsDaoSupport.execute(sql, DateUtil.getDateline(), keyword);
        this.searchKeywordMapper.updateKeywordCount(DateUtil.getDateline(),keyword);
    }

    @Override
    public boolean isExist(String keyword) {
        int count = this.searchKeywordMapper.selectCount(new QueryWrapper<SearchKeywordDO>()
                .eq("keyword",keyword));
        if(count > 0){
            return true;
        }
        return false;
    }
}
