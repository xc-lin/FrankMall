package com.lxc.frankmall.product.dao;

import com.lxc.frankmall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 商品三级分类
 * 
 * @author Franklin
 * @email xianchaolin@126.com
 * @date 2022-06-25 00:59:50
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {


    void  updateCategory(@Param("catId") Long catId, @Param("name") String name);
}
