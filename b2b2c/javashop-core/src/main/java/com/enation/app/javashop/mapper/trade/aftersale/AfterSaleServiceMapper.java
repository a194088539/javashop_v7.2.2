package com.enation.app.javashop.mapper.trade.aftersale;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.aftersale.dos.AfterSaleServiceDO;
import com.enation.app.javashop.model.aftersale.dto.AfterSaleQueryParam;
import com.enation.app.javashop.model.aftersale.vo.AfterSaleExportVO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 售后服务Mapper接口
 * @author duanmingyu
 * @version v1.0
 * @since v7.2.2
 * 2020-08-08
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface AfterSaleServiceMapper extends BaseMapper<AfterSaleServiceDO> {

    /**
     * 查询需要导出的售后服务信息
     * @param disabled 售后服务单状态 NORMAL：正常，DELETE：删除
     * @param param 查询条件
     * @return
     */
    List<AfterSaleExportVO> selectExportVoList(@Param("disabled") String disabled, @Param("params") AfterSaleQueryParam param);

}
