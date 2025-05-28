package com.plume.plrtime.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.plume.plrtime.common.Result;
import com.plume.plrtime.pojo.Activities;
import com.plume.plrtime.pojo.Categories;
import com.plume.plrtime.pojo.vo.LoginUser;
import com.plume.plrtime.service.ActivitiesService;
import com.plume.plrtime.service.CategoriesService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    private final CategoriesService categoriesService;
    public CategoryController(CategoriesService categoriesService) {
        this.categoriesService = categoriesService;
    }

    /**
     * 查询所有分类
     */
    @GetMapping("/list")
    public Result list() {
        List<Categories> list = categoriesService.list();
        return Result.success(list);
    }

    /**
     * 添加活动
     */
    @PostMapping("/save")
    public Result save(@RequestBody Categories categories) {
        categoriesService.save(categories);
        return Result.success();
    }

    /**
     * 修改活动
     */
    @PostMapping("/update")
    public Result update(@RequestBody Categories categories) {
        categoriesService.updateById(categories);
        return Result.success();
    }

    /**
     * 删除活动
     */
    @PostMapping("/delete/{ids}")
    public Result delete(@PathVariable("ids") List<Long> ids) {
        categoriesService.removeBatchByIds(ids);
        return Result.success();
    }

    /**
     * 根据id查询活动
     */
    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(categoriesService.getById(id));
    }

}
