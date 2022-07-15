package com.lxc.frankmall.product.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lxc.frankmall.product.entity.CategoryEntity;
import com.lxc.frankmall.product.service.CategoryService;
import com.lxc.frankmall.product.vo.Catelog2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @author Frank_lin
 * @date 2022/6/27
 */
@Controller
public class IndexController {

    @Autowired
    CategoryService categoryService;

    @GetMapping({"/", "/index.html"})
    public String indexPage(Model model) {
        // 查出所有一级分类
        List<CategoryEntity> entityList = categoryService.getLevel1Categorys();
         model.addAttribute("categories",entityList);
        return "index";
    }
    @ResponseBody
    @GetMapping("index/json/catalog.json")
    public Map<String, List<Catelog2Vo>> getCatalogJson() throws JsonProcessingException {

        Map<String, List<Catelog2Vo>> map = categoryService.getCatalogJson();
        return map;
    }

}
