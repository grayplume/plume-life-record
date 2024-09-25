package com.plume.plrtime.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.plume.plrtime.common.Constants;
import com.plume.plrtime.common.Result;
import com.plume.plrtime.mapper.TimeMapper;
import com.plume.plrtime.pojo.Activity;
import com.plume.plrtime.pojo.Time;
import com.plume.plrtime.pojo.vo.ActivityVO;
import com.plume.plrtime.service.ActivityService;
import com.plume.plrtime.service.TimeService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/activity")
public class ActivityController {

    @Resource
    private ActivityService activityService;
    @Resource
    private TimeService timeService;
    @Autowired
    private TimeMapper timeMapper;

    @GetMapping("/list")
    public Result list() {
        List<Activity> list = activityService.list();
        // 获取昨天零点的时间戳
        LocalDateTime yesterdayMidnight = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MIDNIGHT);
        Date yesterdayMidnightDate = Date.from(yesterdayMidnight.atZone(ZoneId.systemDefault()).toInstant());

        List<ActivityVO> activityVOS = new ArrayList<>();

        for (Activity activity : list) {
            Integer totalDuration = timeMapper.getTotalDurationByAIdAndEndTime(activity.getAId(), yesterdayMidnightDate);

            ActivityVO activityVO = new ActivityVO();
            activityVO.setAId(activity.getAId());
            activityVO.setTId(activity.getTId());
            activityVO.setUId(activity.getUId());
            activityVO.setAName(activity.getAName());
            activityVO.setADec(activity.getADec());
            activityVO.setATime(activity.getATime());
            activityVO.setAStatus(activity.getAStatus());
            activityVO.setToday_time(totalDuration);

            activityVOS.add(activityVO);
        }
        return Result.success(activityVOS);
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
