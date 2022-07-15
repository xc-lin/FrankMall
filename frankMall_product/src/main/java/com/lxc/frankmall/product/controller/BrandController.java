package com.lxc.frankmall.product.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.lxc.common.valid.AddGroup;
import com.lxc.common.valid.UpdateGroup;
import com.lxc.common.valid.UpdateStatusGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lxc.frankmall.product.entity.BrandEntity;
import com.lxc.frankmall.product.service.BrandService;
import com.lxc.common.utils.PageUtils;
import com.lxc.common.utils.R;

import javax.validation.Valid;


/**
 * 品牌
 *
 * @author Franklin
 * @email xianchaolin@126.com
 * @date 2022-06-25 00:59:50
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    public R info(@PathVariable("brandId") Long brandId) {
        BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody @Validated({AddGroup.class}) BrandEntity brand/*, BindingResult result*/) {
        brandService.save(brand);
        // if (result.hasErrors()) {
        //     Map<String, String> map = new HashMap<>();
        //
        //     result.getFieldErrors().forEach((x) -> {
        //                 String defaultMessage = x.getDefaultMessage();
        //                 String field = x.getField();
        //                 map.put(field, defaultMessage);
        //             }
        //     );
        //     return R.error().put("data", map);
        // }
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody @Validated({UpdateGroup.class}) BrandEntity brand) {
        brandService.updateDatail(brand);

        return R.ok();
    }


    @RequestMapping("/update/status")
    public R updateSort(@RequestBody @Validated({UpdateStatusGroup.class}) BrandEntity brandEntity) {
        brandService.updateById(brandEntity);

        return R.ok();
    }


    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] brandIds) {
        brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
