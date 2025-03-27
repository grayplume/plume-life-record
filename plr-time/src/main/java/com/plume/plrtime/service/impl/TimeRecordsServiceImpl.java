package com.plume.plrtime.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.plume.plrtime.common.Result;
import com.plume.plrtime.exception.BusinessException;
import com.plume.plrtime.pojo.Activities;
import com.plume.plrtime.pojo.Statistics;
import com.plume.plrtime.pojo.TimeRecords;
import com.plume.plrtime.service.ActivitiesService;
import com.plume.plrtime.service.StatisticsService;
import com.plume.plrtime.service.TimeRecordsService;
import com.plume.plrtime.mapper.TimeRecordsMapper;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
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
    private final StatisticsService statisticsService;

    public TimeRecordsServiceImpl(ActivitiesService activitiesService, StatisticsService statisticsService) {
        this.activitiesService = activitiesService;
        this.statisticsService = statisticsService;
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
        timeRecord.setStartTime(LocalDateTime.now());
        timeRecord.setEndTime(null);
        timeRecord.setDuration(0);
        timeRecord.setNotes("");
        timeRecord.setCreatedAt(LocalDateTime.now());

        // 4. 保存到数据库
        this.save(timeRecord);

        // 更新统计数据
        totalTime(userId, activityId);
        return true;
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
        timeRecord.setEndTime(LocalDateTime.now());
        timeRecord.setDuration((int) Duration.between(timeRecord.getStartTime(), timeRecord.getEndTime()).getSeconds());

        this.updateById(timeRecord);

        totalTime(userId, activityId);
        return true;
    }

    /**
     * 计算总时长并更新统计数据
     */
    private void totalTime(Long userId, Long activityId) {
        // 计算总时长并更新统计数据
        // 查询所有相同用户相同活动id时间记录
        LambdaQueryWrapper<TimeRecords> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TimeRecords::getUserId, userId.intValue())
                .eq(TimeRecords::getActivityId, activityId.intValue());

        Integer totalDuration = 0;

        for (TimeRecords timeRecords : this.list(queryWrapper)) {
            totalDuration += timeRecords.getDuration();
        }

        updateStatistics(userId, activityId, totalDuration);
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
            runningRecord.setEndTime(LocalDateTime.now());
            runningRecord.setDuration((int) ((Duration.between(runningRecord.getStartTime(), runningRecord.getEndTime()).getSeconds())));
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

    private void updateStatistics(Long userId, Long activityId, Integer duration) {
        LambdaQueryWrapper<Statistics> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Statistics::getUserId, userId)
                .eq(Statistics::getActivityId, activityId);

        Statistics record = statisticsService.getOne(queryWrapper);
        if (record == null) {
            // 插入新记录
            Statistics newRecord = new Statistics();
            newRecord.setUserId(userId.intValue());
            newRecord.setActivityId(activityId.intValue());
            newRecord.setDate(LocalDateTime.now());
            newRecord.setTotalDuration(duration);
            statisticsService.save(newRecord);
        } else {
            // 更新现有记录
            record.setTotalDuration(duration);
            statisticsService.updateById(record);
        }
    }
}