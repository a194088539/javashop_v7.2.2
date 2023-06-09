package com.enation.app.javashop.service.goods.util;

import com.enation.app.javashop.model.goods.dos.BrandDO;
import com.enation.app.javashop.model.goodssearch.SearchSelector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 品牌url工具
 *
 * @author fk
 * @version v6.4
 * @since v6.4
 * 2017年9月15日 下午5:00:28
 */
public class BrandUrlUtils {


    /**
     * 生成品牌的url
     *
     * @param brandid
     * @return
     */
    public static String createBrandUrl(String brandid) {
        Map<String, String> params = ParamsUtils.getReqParams();

        params.put("brand", brandid);

        return ParamsUtils.paramsToUrlString(params);
    }

    /**
     * 生成没有品牌的url
     *
     * @return
     */
    private static String createUrlWithOutBrand() {
        Map<String, String> params = ParamsUtils.getReqParams();

        params.remove("brand");

        return ParamsUtils.paramsToUrlString(params);
    }

    /**
     * 根据id查找brand
     *
     * @param brandList
     * @param brandid
     * @return
     */
    public static BrandDO findBrand(List<BrandDO> brandList, int brandid) {

        for (BrandDO brand : brandList) {
            if (brand.getBrandId() == brandid) {
                return brand;
            }
        }
        return null;
    }

    /**
     * 生成已经选择的品牌
     *
     * @param brandList
     * @param brandId
     * @return
     */
    public static List<SearchSelector> createSelectedBrand(List<BrandDO> brandList, Integer brandId) {
        List<SearchSelector> selectorList = new ArrayList();
        if (brandId == null) {
            return selectorList;
        }
        String brandName = "";
        BrandDO findBrand = findBrand(brandList, brandId);
        if (findBrand != null) {
            brandName = findBrand.getName();
        }

        SearchSelector selector = new SearchSelector();
        selector.setName(brandName);
        String url = createUrlWithOutBrand();
        selector.setUrl(url);
        selectorList.add(selector);
        return selectorList;
    }

}
