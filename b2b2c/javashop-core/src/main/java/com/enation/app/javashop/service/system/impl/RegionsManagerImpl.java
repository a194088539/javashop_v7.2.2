package com.enation.app.javashop.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.enation.app.javashop.mapper.system.RegionsMapper;
import com.enation.app.javashop.model.base.CachePrefix;
import com.enation.app.javashop.model.member.vo.RegionVO;
import com.enation.app.javashop.model.system.dos.Regions;
import com.enation.app.javashop.model.system.vo.RegionsVO;
import com.enation.app.javashop.service.system.RegionsManager;
import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.database.DaoSupport;
import com.enation.app.javashop.framework.exception.ResourceNotFoundException;
import com.enation.app.javashop.framework.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 地区业务类
 *
 * @author zh
 * @version v7.0.0
 * @since v7.0.0
 * 2018-04-28 13:49:38
 */
@Service
public class RegionsManagerImpl implements RegionsManager {

    @Autowired
    private Cache cache;
    @Autowired
    private RegionsMapper regionsMapper;

    @Override
    @Transactional(value = "systemTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Regions add(RegionsVO regionsVO) {
        Regions regions = new Regions();
        BeanUtil.copyProperties(regionsVO, regions);
        regionsMapper.insert(regions);
        String regionPath = "";
        long regionId = regions.getId();
        regions = getModel(regionId);
        if (regions.getParentId() != null && regions.getParentId() != 0) {
            Regions p = getModel(regions.getParentId());
            if (p == null) {
                throw new ResourceNotFoundException("当前地区父地区id无效");
            }
            regionPath = p.getRegionPath() + regionId + ",";
        } else {
            regionPath = "," + regionId + ",";
        }
        //对地区级别进行处理
        String subreg = regionPath.substring(0, regionPath.length() - 1);
        subreg = subreg.substring(1);
        String[] regs = subreg.split(",");
        regions.setRegionGrade(regs.length);
        regions.setRegionPath(regionPath);
        //修改地区
        regionsMapper.updateById(regions);
        this.clearRegionsCache(regions, 1);
        return regions;
    }

    @Override
    public Regions edit(Regions regions, Long id) {
        regions.setId(id);
        Regions parentRegion = this.getModel(regions.getParentId());
        if (regions.getParentId() != 0 && parentRegion == null) {
            throw new ResourceNotFoundException("当前地区父地区id无效");
        }

        //修改地区信息
        regionsMapper.updateById(regions);
        //修改是否支持货到付款到下级地区
        Regions region = new Regions();
        UpdateWrapper<Regions> wrapper = new UpdateWrapper<>();
        wrapper.like("region_path", regions.getId());
        region.setCod(regions.getCod());
        regionsMapper.update(region, wrapper);
//        this.systemDaoSupport.update(regions, id);
//        this.systemDaoSupport.execute("update es_regions set cod = ? where region_path like ? ", regions.getCod(), "%," + regions.getId() + ",%");

        //清除地区缓存
        this.clearRegionsCache(this.getModel(id), 2);
        return regions;
    }

    @Override
    @Transactional(value = "systemTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(Long id) {

        Regions regions = this.getModel(id);
        //清除缓存
        this.clearRegionsCache(regions, -1);
        QueryWrapper<Regions> wrapper = new QueryWrapper<>();
        wrapper.like("region_path", "," + id + ",");
        regionsMapper.delete(wrapper);
        if (regions == null ) {
            throw new ResourceNotFoundException("该地区不存在");
        }
        //this.systemDaoSupport.execute("delete from es_regions where region_path like '%," + id + ",%'");
    }

    @Override
    public Regions getModel(Long id) {
        return regionsMapper.selectById(id);
    }

    /**
     * 更新地区缓存数据
     *
     * @param regions
     * @param operation -1 删除 1 添加 2 修改
     */
    private void clearRegionsCache(Regions regions, Integer operation) {

        //获取地区深度，更新相关地区深度缓存
        Integer path = regions.getRegionGrade();
        List<Regions> regionsList = (List<Regions>) this.cache.get(CachePrefix.REGIONLIDEPTH.getPrefix() + path);

        //缓存为空不进行操作
        if (regionsList == null) {
            return;
        }

        switch (operation) {
            case -1:
                regionsList.remove(regions);
                break;
            case 1:
                regionsList.add(regions);
                break;
            case 2:
                for (int i = 0; i <= regionsList.size() - 1; i++) {
                    Regions r = regionsList.get(i);
                    if (r.getId().equals(regions.getId())) {
                        regionsList.remove(i);
                        regionsList.add(i, regions);
                        break;
                    }
                }
                break;
            default:
        }
        this.cache.put(CachePrefix.REGIONLIDEPTH.getPrefix() + path, regionsList);

        for (int i = 1; i <= 4; i++) {
            this.cache.remove(CachePrefix.REGIONALL.getPrefix() + i);
        }
    }


    @Override
    public List<Regions> getRegionsChildren(Long regionId) {
        Regions region = this.getModel(regionId);
        if (region == null && regionId != 0) {
            throw new ResourceNotFoundException("该地区不存在");
        }
        Integer depth = 0;
        if (regionId != 0) {
            depth = region.getRegionGrade();
        }
        //因为需要查找下一级，所以需要将深度+1
        depth = depth + 1;
        Object obj = this.cache.get(CachePrefix.REGIONLIDEPTH.getPrefix() + depth);
        List<Regions> regions = null;
        // 如果为空的话需要重数据库中查出数据 然后放入缓存
        if (obj == null) {
            for (int i = 1; i <= 4; i++) {

                QueryWrapper<Regions> wrapper = new QueryWrapper<>();
                wrapper.eq("region_grade", i);
                List<Regions> rgs = regionsMapper.selectList(wrapper);
                //List<Regions> rgs = this.systemDaoSupport.queryForList("select * from es_regions where region_grade = ?", Regions.class, i);

                this.cache.put(CachePrefix.REGIONLIDEPTH.getPrefix() + i, rgs);
                if (i == depth) {
                    regions = rgs;
                }
            }
        } else {
            regions = (List<Regions>) obj;
        }
        List<Regions> rgs = new ArrayList<>();
        for (int i = 0; i < regions.size(); i++) {
            if (regions.get(i).getParentId().equals(regionId)) {
                rgs.add(regions.get(i));
            }
        }
        return rgs;
    }


    @Override
    public List<RegionVO> getRegionByDepth(Integer depth) {
        //如果深度大于4级，则修改深度为最深4级
        if (depth > 4) {
            depth = 4;
        }
        Object obj = this.cache.get(CachePrefix.REGIONALL.getPrefix() + depth);
        List<RegionVO> regions = null;
        //如果从缓存中拿到数据则直接取值，否则需要放到缓存中一份在返回取值
        if (obj != null) {
            regions = (List<RegionVO>) obj;
        } else {
            List<RegionVO> rgs = this.getAll(depth);
            this.cache.put(CachePrefix.REGIONALL.getPrefix() + depth, rgs);
            regions = rgs;
        }
        return regions;
    }

    /**
     * 根据深度，从数据库获取组织好树结构的地区数据
     *
     * @param depth 深度
     * @return 地区集合
     */
    private List<RegionVO> getAll(Integer depth) {
        QueryWrapper<Regions> wrapper = new QueryWrapper<>();
        List<Regions> data = regionsMapper.selectList(wrapper);
        List<RegionVO> tree = new ArrayList<>();
        this.sort(1, depth, tree, data);
        return tree;
    }

    /**
     * 负责递归的停止
     *
     * @param level 标示
     * @param depth 深度
     * @param tree  新的树结构
     * @param data  原始数据
     */
    private void sort(int level, int depth, List<RegionVO> tree, List<Regions> data) {
        if (level + 1 > depth) {
            // 如果是第一级的情况直接返回
            if (depth == 1) {
                for (Regions regions : data) {
                    if (regions.getParentId() == 0) {
                        tree.add(regions.toVo());
                    }
                }
            }
            return;
        }
        // 如果为0 则代表初始化 初始化顶级数据
        if (level == 1) {
            for (Regions regions : data) {
                if (regions.getParentId() == 0) {
                    tree.add(regions.toVo());
                }
            }
        }
        this.recursion(level, tree, data);
        level++;
        this.sort(level, depth, tree, data);
    }

    /**
     * 负责树结构的创建
     *
     * @param level 标示
     * @param tree  树结构
     * @param data  原始数据
     */
    private void recursion(int level, List<RegionVO> tree, List<Regions> data) {
        for (RegionVO vo : tree) {
            if (vo.getLevel() != level) {
                if (vo.getChildren().size() != 0) {
                    this.recursion(level, vo.getChildren(), data);
                }
                continue;
            }
            for (Regions regions : data) {
                if (regions.getParentId().equals(vo.getId())) {
                    vo.getChildren().add(regions.toVo());
                }
            }
        }
    }

}
