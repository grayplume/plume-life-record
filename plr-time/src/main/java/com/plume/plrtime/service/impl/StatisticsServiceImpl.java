package com.plume.plrtime.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.plume.plrtime.exception.BusinessException;
import com.plume.plrtime.mapper.TimeRecordsMapper;
import com.plume.plrtime.pojo.Activities;
import com.plume.plrtime.pojo.Statistics;
import com.plume.plrtime.pojo.TimeRecords;
import com.plume.plrtime.pojo.vo.*;
import com.plume.plrtime.service.ActivitiesService;
import com.plume.plrtime.service.StatisticsService;
import com.plume.plrtime.mapper.StatisticsMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author plume
* @description 针对表【statistics】的数据库操作Service实现
* @createDate 2025-03-27 01:06:25
*/
@Service
public class StatisticsServiceImpl extends ServiceImpl<StatisticsMapper, Statistics>
    implements StatisticsService{

    private final ActivitiesService activitiesService;
    private final StatisticsMapper statisticsMapper;
    private final TimeRecordsMapper timeRecordsMapper;

    public StatisticsServiceImpl(ActivitiesService activitiesService, StatisticsMapper statisticsMapper, TimeRecordsMapper timeRecordsMapper) {
        this.activitiesService = activitiesService;
        this.statisticsMapper = statisticsMapper;
        this.timeRecordsMapper = timeRecordsMapper;
    }

    @Override
    public List<StatisticsVO> show() {
        // 获取当前登录用户信息
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = loginUser.getUser().getUserId();

        // Step 1: 查出所有该用户的活动
        LambdaQueryWrapper<Activities> activitiesQuery = Wrappers.lambdaQuery(Activities.class)
                .eq(Activities::getUserId, userId);
        List<Activities> activityList = activitiesService.list(activitiesQuery);

        if (activityList.isEmpty()) {
            return Collections.emptyList(); // 没有活动直接返回空
        }

        // Step 2: 提取所有 activityId
        List<Integer> activityIds = activityList.stream()
                .map(Activities::getActivityId)
                .collect(Collectors.toList());

        // Step 3: 批量查询该用户的统计记录
        LambdaQueryWrapper<Statistics> statisticsQuery = Wrappers.lambdaQuery(Statistics.class)
                .eq(Statistics::getUserId, userId)
                .in(Statistics::getActivityId, activityIds);
        List<Statistics> statisticsList = this.list(statisticsQuery);

        // Step 4: 构建 activityId -> Statistics 映射
        Map<Integer, Statistics> statisticsMap = statisticsList.stream()
                .collect(Collectors.toMap(Statistics::getActivityId, s -> s));

        // Step 5: 构建 VO 列表（用活动表为主，统计为辅）
        List<StatisticsVO> voList = activityList.stream()
                .map(activity -> {
                    StatisticsVO vo = new StatisticsVO();
                    vo.setActivityId(activity.getActivityId());
                    vo.setActivityName(activity.getName());
                    vo.setCategoryId(activity.getCategoryId());

                    Statistics stat = statisticsMap.get(activity.getActivityId());
                    if (stat != null) {
                        vo.setTotalDuration(stat.getTotalDuration() / 60); // 转分钟
                        vo.setStatus(stat.getStatus());
                        vo.setUpdatedAt(stat.getUpdatedAt());
                    } else {
                        vo.setTotalDuration(0);
                        vo.setStatus(0);
                        vo.setUpdatedAt( activity.getCreatedAt()); // 或 null，或 createdAt
                    }
                    return vo;
                })
                .sorted(Comparator.comparing(StatisticsVO::getUpdatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());

        return voList;
    }


    @Override
    public List<ActivityDurationVO> getActivityDurationsByDate(LocalDate date) {
        return statisticsMapper.selectActivityDurationByDate(date);
    }

    @Override
    public UserDurationStatsVO getUserDurationStats() {
        // 获取当前用户id
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        return statisticsMapper.selectUserDurationStats(loginUser.getUser().getUserId().toString());
    }
    @Override
    public List<DayTimeDistributionVO> selectDayTimeDistribution(String date, Integer categoryId) {
        LocalDate targetDate = LocalDate.parse(date);
        LocalDateTime dayStart = targetDate.atStartOfDay();
        LocalDateTime dayEnd = dayStart.plusDays(1);

        // 获取当前用户 ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Integer userId = loginUser.getUser().getUserId();

        // 查询当日有交集的记录
        List<TimeRecords> records = timeRecordsMapper.selectList(
                new LambdaQueryWrapper<TimeRecords>()
                        .eq(TimeRecords::getUserId, userId)
                        .le(TimeRecords::getStartTime, dayEnd)
                        .ge(TimeRecords::getEndTime, dayStart)
                        .orderByAsc(TimeRecords::getStartTime)
        );

        // 活动信息 map，避免多次查
        Map<Integer, Activities> activityMap = activitiesService.list().stream()
                .collect(Collectors.toMap(Activities::getActivityId, a -> a));

        // 初始化 0-23 小时分布表
        Map<Integer, Integer> hourDurationMap = new LinkedHashMap<>();
        for (int i = 0; i < 24; i++) hourDurationMap.put(i, 0);

        for (TimeRecords record : records) {
            Activities activity = activityMap.get(record.getActivityId());
            if (activity == null) continue;

            if (categoryId != null && !activity.getCategoryId().equals(categoryId)) {
                continue;
            }

            // 限定在当日范围内
            LocalDateTime start = record.getStartTime().isBefore(dayStart) ? dayStart : record.getStartTime();
            LocalDateTime end = (record.getEndTime() == null || record.getEndTime().isAfter(dayEnd)) ? dayEnd : record.getEndTime();

            // 跳过无效记录
            if (!start.isBefore(end)) {
                System.out.printf("跳过无效记录: start = %s, end = %s\n", start, end);
                continue;
            }

            // 拆分进每个小时段
            while (start.isBefore(end)) {
                int hour = start.getHour();
                LocalDateTime hourEnd = start.withMinute(0).withSecond(0).withNano(0).plusHours(1);
                LocalDateTime segmentEnd = hourEnd.isAfter(end) ? end : hourEnd;

                int minutes = (int) Duration.between(start, segmentEnd).toMinutes();
                if (minutes == 0) minutes = 1; // ⚠ 补偿小于1分钟的时间段
                hourDurationMap.merge(hour, minutes, Integer::sum);

                System.out.printf("Hour: %d, Start: %s, SegmentEnd: %s, Minutes: %d\n",
                        hour, start, segmentEnd, minutes);

                start = segmentEnd;
            }
        }

        // 构造返回结果
        List<DayTimeDistributionVO> result = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            DayTimeDistributionVO vo = new DayTimeDistributionVO();
            vo.setHour(i);
            vo.setDurationMinutes(hourDurationMap.getOrDefault(i, 0));
            result.add(vo);
        }

        return result;
    }


    @Override
    public List<DateDurationVO> selectMonthTimeDistribution(String month, Integer categoryId) {
        YearMonth yearMonth = YearMonth.parse(month);
        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate lastDay = yearMonth.atEndOfMonth();

        LocalDateTime startDateTime = firstDay.atStartOfDay();
        LocalDateTime endDateTime = lastDay.plusDays(1).atStartOfDay(); // 不包含最后一天结束时间

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Integer userId = loginUser.getUser().getUserId();

        List<TimeRecords> records = timeRecordsMapper.selectList(
                new LambdaQueryWrapper<TimeRecords>()
                        .eq(TimeRecords::getUserId, userId)
                        .le(TimeRecords::getStartTime, endDateTime)
                        .ge(TimeRecords::getEndTime, startDateTime)
        );

        Map<Integer, Activities> activityMap = activitiesService.list()
                .stream().collect(Collectors.toMap(Activities::getActivityId, a -> a));

        // 初始化整个月份每天的时长为0
        Map<LocalDate, Integer> dayMap = new TreeMap<>();
        for (LocalDate date = firstDay; !date.isAfter(lastDay); date = date.plusDays(1)) {
            dayMap.put(date, 0);
        }

        for (TimeRecords record : records) {
            Activities activity = activityMap.get(record.getActivityId());
            if (activity == null) continue;
            if (categoryId != null && !activity.getCategoryId().equals(categoryId)) continue;

            LocalDateTime start = record.getStartTime().isBefore(startDateTime) ? startDateTime : record.getStartTime();
            LocalDateTime end = record.getEndTime() == null ? endDateTime :
                    (record.getEndTime().isAfter(endDateTime) ? endDateTime : record.getEndTime());

            while (start.isBefore(end)) {
                LocalDate day = start.toLocalDate();
                LocalDateTime dayEnd = day.plusDays(1).atStartOfDay();
                LocalDateTime segmentEnd = dayEnd.isAfter(end) ? end : dayEnd;

                int minutes = (int) Duration.between(start, segmentEnd).toMinutes();
                dayMap.merge(day, minutes, Integer::sum);

                start = segmentEnd;
            }
        }

        return dayMap.entrySet().stream().map(entry -> {
            DateDurationVO vo = new DateDurationVO();
            vo.setDate(entry.getKey().toString()); // yyyy-MM-dd
            vo.setDurationMinutes(entry.getValue());
            return vo;
        }).collect(Collectors.toList());
    }


}




