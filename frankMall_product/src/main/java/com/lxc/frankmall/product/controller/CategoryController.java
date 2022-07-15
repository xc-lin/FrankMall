package com.lxc.frankmall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.lxc.frankmall.product.entity.BrandEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.lxc.frankmall.product.entity.CategoryEntity;
import com.lxc.frankmall.product.service.CategoryService;
import com.lxc.common.utils.PageUtils;
import com.lxc.common.utils.R;


/**
 * 商品三级分类
 *
 * @author Franklin
 * @email xianchaolin@126.com
 * @date 2022-06-25 00:59:50
 */
@RestController
@RequestMapping("product/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 查处所有分类以及子分类，以树形结构组装起来
     */
    @RequestMapping("/list/tree")
    public R list() {
        List<CategoryEntity> list = categoryService.listWithTree();

        return R.ok().put("data", list);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
    public R info(@PathVariable("catId") Long catId) {
        CategoryEntity category = categoryService.getById(catId);

        return R.ok().put("category", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CategoryEntity category) {
        categoryService.save(category);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody CategoryEntity category) {
        categoryService.updateCascade(category);

        return R.ok();
    }


    @RequestMapping("/update/sort")
    public R updateSort(@RequestBody List<CategoryEntity> category) {
        categoryService.updateBatchById(category);

        return R.ok();
    }


    /**
     * 删除
     * 获取请求体，必须发送post请求
     * springmvc 自动将请求题的数据(json)，转为对应的对象
     */
    @PostMapping("/delete")
    public R delete(@RequestBody Long[] catIds) {
        // categoryService.removeByIds(Arrays.asList(catIds));
        // 检查当前要删除的菜单，是否被别的地方饮用
        categoryService.removeMenusByIds(Arrays.asList(catIds));
        return R.ok();
    }

}
