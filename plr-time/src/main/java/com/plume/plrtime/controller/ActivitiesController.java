package com.plume.plrtime.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.plume.plrtime.common.Result;
import com.plume.plrtime.pojo.Activities;
import com.plume.plrtime.pojo.vo.LoginUser;
import com.plume.plrtime.service.ActivitiesService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activity")
public class ActivitiesController {

    private final ActivitiesService activitiesService;
    public ActivitiesController(ActivitiesService activitiesService) {
        this.activitiesService = activitiesService;
    }

    /**
     * 查询所有活动
     */
    @GetMapping("/list")
    public Result list() {
        // 获取当前登录用户信息
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LambdaQueryWrapper<Activities> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Activities::getUserId, loginUser.getUser().getUserId());
        List<Activities> list = activitiesService.list(lambdaQueryWrapper);
        return Result.success();
    }

    /**
     * 添加活动
     */
    @PostMapping("/save")
    public Result save(@RequestBody Activities activity) {
        // 获取当前登录用户信息
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        activity.setUserId(loginUser.getUser().getUserId());
        activitiesService.save(activity);
        return Result.success();
    }

    /**
     * 修改活动
     */
    @PostMapping("/update")
    public Result update(@RequestBody Activities activity) {
        activitiesService.updateById(activity);
        return Result.success();
    }

    /**
     * 删除活动
     */
    @PostMapping("/delete/{ids}")
    public Result delete(@PathVariable("ids") List<Long> ids) {
        activitiesService.removeBatchByIds(ids);
        return Result.success();
    }

    /**
     * 根据id查询活动
     */
    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(activitiesService.getById(id));
    }

    /**
     * 根据用户id查询活动
     */
    @GetMapping("/findByUserId/{userId}")
    public Result findByUserId(@PathVariable Integer userId) {
        LambdaQueryWrapper<Activities> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Activities::getUserId, userId);
        return Result.success(activitiesService.list(lambdaQueryWrapper));
    }
}
