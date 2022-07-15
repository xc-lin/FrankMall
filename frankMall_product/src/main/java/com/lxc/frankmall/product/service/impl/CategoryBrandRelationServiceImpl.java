package com.lxc.frankmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.lxc.frankmall.product.dao.BrandDao;
import com.lxc.frankmall.product.dao.CategoryDao;
import com.lxc.frankmall.product.entity.BrandEntity;
import com.lxc.frankmall.product.service.BrandService;
import com.lxc.frankmall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxc.common.utils.PageUtils;
import com.lxc.common.utils.Query;

import com.lxc.frankmall.product.dao.CategoryBrandRelationDao;
import com.lxc.frankmall.product.entity.CategoryBrandRelationEntity;
import com.lxc.frankmall.product.service.CategoryBrandRelationService;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Autowired
    BrandDao brandDao;
    @Autowired
    CategoryDao categoryDao;

    @Autowired
    CategoryBrandRelationDao categoryBrandRelationDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveDetails(CategoryBrandRelationEntity categoryBrandRelation) {
        Long brandId = categoryBrandRelation.getBrandId();
        Long catelogId = categoryBrandRelation.getCatelogId();
        String brandName = brandDao.selectById(brandId).getName();
        String categoryName = categoryDao.selectById(catelogId).getName();
        categoryBrandRelation.setBrandName(brandName);
        categoryBrandRelation.setCatelogName(categoryName);

        this.save(categoryBrandRelation);


    }

    @Override
    public void updateBrand(Long brandId, String name) {
        LambdaUpdateWrapper<CategoryBrandRelationEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(CategoryBrandRelationEntity::getBrandId, brandId);
        wrapper.set(CategoryBrandRelationEntity::getBrandName, name);
        this.update(wrapper);
    }

    @Override
    public void updateCategory(Long catId, String name) {
        this.baseMapper.updateCategory(catId, name);
    }

    @Override
    public List<BrandEntity> getBrandByCatId(Long catId) {
        List<CategoryBrandRelationEntity> categoryBrandRelationEntities = categoryBrandRelationDao.selectList(new LambdaQueryWrapper<CategoryBrandRelationEntity>()
                .eq(CategoryBrandRelationEntity::getCatelogId, catId));
        List<Long> barndIds = categoryBrandRelationEntities.stream()
                .map(item -> item.getBrandId())
                .collect(Collectors.toList());
        List<BrandEntity> brandEntities = brandDao.selectList(new LambdaQueryWrapper<BrandEntity>()
                .in(BrandEntity::getBrandId, barndIds));
        return brandEntities;
    }

}