package com.plume.plrtime.controller;

import com.plume.plrtime.common.Constants;
import com.plume.plrtime.common.Result;
import com.plume.plrtime.pojo.Activity;
import com.plume.plrtime.service.ActivityService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activity")
public class ActivityController {

    @Resource
    private ActivityService activityService;

    @GetMapping("/list")
    public Result list() {
        List<Activity> list = activityService.list();
        return Result.success(list);
    }

    @PostMapping("/save")
    public Result save(@RequestBody Activity activity) {
        boolean save = activityService.save(activity);
        return save?Result.success("保存成功"):Result.error(Constants.CODE_500,"保存失败");
    };

    @PostMapping("/update")
    public Result update(@RequestBody Activity activity) {
        boolean updated = activityService.updateById(activity);
        return updated?Result.success("修改成功"):Result.error(Constants.CODE_500,"修改失败");
    }

    @PostMapping("/delete/{ids}")
    public Result delete(@PathVariable("ids") List<Long> ids) {
        boolean removed = activityService.removeBatchByIds(ids);
        return removed?Result.success("删除成功"):Result.error(Constants.CODE_500,"删除失败");
    }


}
