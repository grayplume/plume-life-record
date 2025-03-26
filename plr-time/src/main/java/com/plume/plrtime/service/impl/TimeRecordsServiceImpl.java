package com.plume.plrtime.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.plume.plrtime.exception.BusinessException;
import com.plume.plrtime.pojo.Activities;
import com.plume.plrtime.pojo.TimeRecords;
import com.plume.plrtime.service.ActivitiesService;
import com.plume.plrtime.service.TimeRecordsService;
import com.plume.plrtime.mapper.TimeRecordsMapper;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author plume
 * @description 针对表【time_records】的数据库操作Service实现
 * @createDate 2025-03-26 23:25:21
 */
@Service
public class TimeRecordsServiceImpl extends ServiceImpl<TimeRecordsMapper, TimeRecords>
        implements TimeRecordsService {

    private final ActivitiesService activitiesService;

    public TimeRecordsServiceImpl(ActivitiesService activitiesService) {
        this.activitiesService = activitiesService;
    }

    /**
     * 开始计时
     */
    @Override
    public Boolean startTimer(Long userId, Long activityId) {
        // 1. 验证活动是否存在及归属
        validateActivity(userId, activityId);

        // 2. 查找并结束当前用户的未结束记录（如果有）
        endRunningRecord(userId, activityId);

        // 3. 创建新的时间记录
        TimeRecords timeRecord = new TimeRecords();
        timeRecord.setUserId(userId.intValue());
        timeRecord.setActivityId(activityId.intValue());
        timeRecord.setStartTime(new Date());
        timeRecord.setEndTime(null);
        timeRecord.setDuration(0);
        timeRecord.setNotes("");
        timeRecord.setCreatedAt(new Date());

        // 4. 保存到数据库
        return this.save(timeRecord);
    }

    /**
     * 停止计时
     */
    @Override
    public Boolean endTimer(Long userId, Long activityId) {
        // 1. 验证活动是否存在及归属
        validateActivity(userId, activityId);

        // 2. 查询最新的未结束时间记录
        TimeRecords timeRecord = findLatestRunningRecord(userId, activityId);
        if (timeRecord == null) {
            throw new BusinessException(503, "时间记录不存在");
        }

        // 3. 更新时间记录的结束时间和时长
        timeRecord.setEndTime(new Date());
        timeRecord.setDuration((int) ((timeRecord.getEndTime().getTime() - timeRecord.getStartTime().getTime()) / 1000));
        return this.updateById(timeRecord);
    }

    /**
     * 验证活动是否存在及归属
     */
    private void validateActivity(Long userId, Long activityId) {
        // 1. 查询活动
        Activities activity = activitiesService.getById(activityId);
        if (activity == null) {
            throw new BusinessException(501, "活动不存在");
        }

        // 2. 验证活动是否属于当前用户
        Long activityUserId = Long.valueOf(activity.getUserId()); // 确保类型一致
        if (!activityUserId.equals(userId)) {
            throw new BusinessException(502, "活动不属于当前用户");
        }
    }

    /**
     * 查找并结束当前用户的未结束记录
     */
    private void endRunningRecord(Long userId, Long activityId) {
        LambdaQueryWrapper<TimeRecords> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TimeRecords::getUserId, userId.intValue())
                .eq(TimeRecords::getActivityId, activityId.intValue())
                .isNull(TimeRecords::getEndTime);

        TimeRecords runningRecord = this.getOne(queryWrapper);
        if (runningRecord != null) {
            runningRecord.setEndTime(new Date());
            runningRecord.setDuration((int) ((runningRecord.getEndTime().getTime() - runningRecord.getStartTime().getTime()) / 1000));
            this.updateById(runningRecord);
        }
    }

    /**
     * 查找最新的未结束时间记录
     */
    private TimeRecords findLatestRunningRecord(Long userId, Long activityId) {
        LambdaQueryWrapper<TimeRecords> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TimeRecords::getUserId, userId.intValue())
                .eq(TimeRecords::getActivityId, activityId.intValue())
                .isNull(TimeRecords::getEndTime)
                .orderByDesc(TimeRecords::getCreatedAt)
                .last("LIMIT 1");

        return this.getOne(queryWrapper);
    }
}