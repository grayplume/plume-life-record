package com.plume.plrtime.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.plume.plrtime.exception.BusinessException;
import com.plume.plrtime.pojo.Activities;
import com.plume.plrtime.pojo.Statistics;
import com.plume.plrtime.pojo.vo.ActivityDurationVO;
import com.plume.plrtime.pojo.vo.LoginUser;
import com.plume.plrtime.pojo.vo.StatisticsVO;
import com.plume.plrtime.pojo.vo.UserDurationStatsVO;
import com.plume.plrtime.service.ActivitiesService;
import com.plume.plrtime.service.StatisticsService;
import com.plume.plrtime.mapper.StatisticsMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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

    public StatisticsServiceImpl(ActivitiesService activitiesService, StatisticsMapper statisticsMapper) {
        this.activitiesService = activitiesService;
        this.statisticsMapper = statisticsMapper;
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
    public List<StatisticsVO> getByUserId(Integer userId) {
        // Step 1: 根据 userId 查询统计数据
        QueryWrapper<Statistics> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId); // 添加 userId 等值条件
        List<Statistics> statisticsList = this.list(queryWrapper);

        // Step 2: 提取所有 activityId
        List<Integer> activityIds = statisticsList.stream()
                .map(Statistics::getActivityId)
                .distinct() // 去重
                .collect(Collectors.toList());

        if (activityIds.isEmpty()) {
            throw new BusinessException(504, "该用户没有任何活动");
        }

        // Step 3: 批量查询活动名称
        Map<Integer, String> activityNameMap = activitiesService.listByIds(activityIds).stream()
                .collect(Collectors.toMap(Activities::getActivityId, Activities::getName));

        // Step 4: 转换为 StatisticsVO 列表
        return statisticsList.stream()
                .map(statistics -> {
                    StatisticsVO vo = new StatisticsVO();
                    vo.setActivityId(statistics.getActivityId());
                    vo.setTotalDuration(statistics.getTotalDuration() / 60); // 将秒数转换为分钟
                    vo.setActivityName(activityNameMap.get(statistics.getActivityId()));
                    return vo;
                })
                .collect(Collectors.toList());

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
}




