package com.plume.plrtime.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.plume.plrtime.common.Constants;
import com.plume.plrtime.common.Result;
import com.plume.plrtime.exception.BusinessException;
import com.plume.plrtime.exception.TimeException;

import com.plume.plrtime.pojo.Activity;
import com.plume.plrtime.pojo.Time;
import com.plume.plrtime.pojo.dto.TimeDTO;
import com.plume.plrtime.service.ActivityService;
import com.plume.plrtime.service.TimeService;
import com.plume.plrtime.mapper.TimeMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

/**
* @author plume
* @description 针对表【plr_time(时间记录表)】的数据库操作Service实现
* @createDate 2024-09-20 18:20:48
*/
@Service
public class TimeServiceImpl extends ServiceImpl<TimeMapper, Time>
    implements TimeService{

    @Resource
    private ActivityService activityService;
    @Resource
    private TimeMapper timeMapper;


    @Override
    public Result start(TimeDTO timeDTO) {
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
            save = save(time);
            // 将刚保存的时间id传给前端
            map.put("tId", time.getTId());

            // 更新活动TID
            activityService.getById(activity.getAId());
            if (save){
                activity.setTId(time.getTId());
                activity.setAStatus(1);
                activityService.updateById(activity);
            }
        }else {
            // 查询数据库中数据
            Time timeDb = getById(activity.getTId());
            // 如果该对象存在endTime说明tid错误
            if (timeDb.getEndTime()!=null){
                throw new BusinessException(TimeException.ACTIVITY_TID_ERR);
            }


            Date endDate = new Date();
            // 计算持续时间
            long differenceInMillis = endDate.getTime()-timeDb.getStartTime().getTime();
            timeDb.setEndTime(endDate);
            timeDb.setTDuration(differenceInMillis/1000);
            save = updateById(timeDb);

            // 活动正在运行,关闭活动
            changeStatus(activity.getAId());

            map.put("duration", (int) (differenceInMillis/1000));
        }
        return save?Result.success(map):Result.error(Constants.CODE_500,"保存失败");
    }


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
        Integer count = count(aid);
        activity.setAStatus((currentStatus+1)%2);
        activity.setATime(count);
        activity.setUpdateTime(new Date()); // 更新为当前时间
        activityService.updateById(activity);
    }

    // 计算活动总时间
    private Integer count(Integer aid){
        QueryWrapper<Time> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("a_id", aid);
        List<Time> list = list(queryWrapper);
        int total = 0;
        for (Time time : list) {
            if (time.getEndTime()!=null){
                total += time.getTDuration();
            }
        }
        return total;
    }
}




