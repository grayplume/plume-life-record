package com.plume.plrtime.controller;

import com.plume.plrtime.common.Constants;
import com.plume.plrtime.common.Result;
import com.plume.plrtime.exception.BusinessException;
import com.plume.plrtime.exception.TimeException;
import com.plume.plrtime.pojo.Activity;
import com.plume.plrtime.pojo.Time;
import com.plume.plrtime.pojo.dto.TimeDTO;
import com.plume.plrtime.service.ActivityService;
import com.plume.plrtime.service.TimeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/time")
public class TimeController {

    @Resource
    private TimeService timeService;

    @GetMapping("/list")
    public Result list() {
        List<Time> list = timeService.list();
        return Result.success(list);
    }

    @PostMapping("/start")
    public Result start(@RequestBody TimeDTO timeDTO) {
        // 查询活动状态
        Activity activity = activityService.getById(timeDTO.getAId());
        boolean save = false;
        // 返回结果
        HashMap<String, Integer> map = new HashMap<String,Integer>();

        if (activity.getAStatus() == 0){
            // TODO 检查其他活动
            changeStatus(activity.getAId());
            Time time = new Time();
            time.setUId(timeDTO.getUId());
            time.setAId(timeDTO.getAId());
            time.setStartTime(new Date());
            save = timeService.save(time);
           // 将刚保存的时间id传给前端
            map.put("tId", time.getTId());
            if (save){
                activity.setTId(time.getTId());
                activity.setAStatus(1);
                activityService.updateById(activity);
            }
        }else {
            // 查询数据库中数据
            Time timeDb = timeService.getById(activity.getTId());
            // 如果该对象存在endTime说明tid错误
            if (timeDb.getEndTime()!=null){
                throw new BusinessException(TimeException.ACTIVITY_TID_ERR);
            }
            // 活动正在运行,关闭活动
            changeStatus(activity.getAId());

            Date endDate = new Date();
            // 计算持续时间
            long differenceInMillis = endDate.getTime()-timeDb.getStartTime().getTime();
            timeDb.setEndTime(endDate);
            timeDb.setTDuration(differenceInMillis/1000);
            save = timeService.updateById(timeDb);
            map.put("duration", (int) (differenceInMillis/1000));
        }
        return save?Result.success(map):Result.error(Constants.CODE_500,"保存失败");
    }


    @Resource
    private ActivityService activityService;
    // 改变活动状态
    private void changeStatus(Integer aid){
        // 查询所有活动
        List<Activity> activities = activityService.list();
        // 记录当前活动状态
        Activity activity = activityService.getById(aid);
        int currentStatus = activity.getAStatus();
        // 判断其他活动是否停止
        for (Activity item : activities) {
            if (item.getAId().equals(aid)) {
                continue; // 跳过当前活动
            }
            if (item.getAStatus() == 1) {
                // 其他活动处于运行状态，先停止这些活动
                throw new BusinessException(TimeException.ACTIVITY_RUNNING);
            }
        }

        activity.setAStatus((currentStatus+1)%2);
        activityService.updateById(activity);
    }



}
