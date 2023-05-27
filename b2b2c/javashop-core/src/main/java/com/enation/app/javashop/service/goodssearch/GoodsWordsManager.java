package com.enation.app.javashop.service.goodssearch;


import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.goodssearch.enums.GoodsWordsType;

/**
 * @author fk
 * @version v2.0
 * @Description: 商品分词Manager
 * @date 2019/12/6 11:04
 * @since v7.0.0
 */
public interface GoodsWordsManager {

    /**
     * 添加一个分词
     * @param word 分词
     */
    void addWord(String word);

    /**
     * 修改提示词
     * @param word 分词
     * @param id 主键
     */
    void updateWords(String word,Long id);

    /**
     * 修改排序
     * @param id 主键
     * @param sort 排序
     */
    void updateSort(Long id ,Integer sort);

    /**
     * 根据分词查询列表
     * @param pageNo 每页
     * @param pageSize 每页数量
     * @param keyword 关键字
     * @return
     */
    WebPage listPage(Long pageNo, Long pageSize, String keyword);

    /**
     * 删除
     * @param goodsWordsType 分词类型
     * @param id 主键
     */
    void delete(GoodsWordsType goodsWordsType, Long id);

    /**
     * 删除某个分词
     * @param words 分词
     */
    void delete(String words);

    /**
     * 添加一组分词，存在累加数量，不存在新增
     * @param words 分词
     */
    void addWords(String words);

    /**
     * 变更商品数量
     * @param words 分词
     */
    void updateGoodsNum(String words);

    /**
     * 变更所有平台提示词商品数量
     */
    void batchUpdateGoodsNum();






}
