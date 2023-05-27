package com.enation.app.javashop.framework.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.enation.app.javashop.framework.database.WebPage;

/**
 * 分页数据转换工具类
 * @author duanmingyu
 * @version v1.0
 * @since v7.2.2
 * 2020-07-20
 */
public class PageConvert {

    /**
     * 分页数据转换
     * 将mybatis的分页数据转为自定义分页数据
     * @param iPage
     * @return
     */
    public static WebPage convert(IPage iPage) {
        WebPage page = new WebPage();
        page.setData(iPage.getRecords());
        page.setPageNo(iPage.getCurrent());
        page.setPageSize(iPage.getSize());
        page.setDataTotal(iPage.getTotal());
        return page;
    }

}
