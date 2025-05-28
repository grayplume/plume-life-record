package com.plume.plrtime.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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

import java.time.LocalDate;
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
        LambdaQueryWrapper<Statistics> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // Step 1: 查询所有统计数据
        List<Statistics> statisticsList = this.list(lambdaQueryWrapper);

        // Step 2: 提取所有 activityId
        List<Integer> activityIds = statisticsList.stream()
                .map(Statistics::getActivityId)
                .distinct() // 去重
                .collect(Collectors.toList());

        // Step 3: 批量查询活动名称
        Map<Integer, String> activityNameMap = activitiesService.listByIds(activityIds).stream()
                .collect(Collectors.toMap(Activities::getActivityId, Activities::getName));

        // 查询对应活动id的对应分类id
        Map<Integer, Integer> activityCategoryMap = activitiesService.listByIds(activityIds).stream()
                .collect(Collectors.toMap(Activities::getActivityId, Activities::getCategoryId));

        // Step 4: 对 statisticsList 按照 updatedAt 字段排序，最新的排在前面
        statisticsList.sort((s1, s2) -> s2.getUpdatedAt().compareTo(s1.getUpdatedAt()));  // 按照 updatedAt 字段倒序排序

        // Step 4: 转换为 StatisticsVO 列表
        return statisticsList.stream()
                .map(statistics -> {
                    StatisticsVO vo = new StatisticsVO();
                    vo.setActivityId(statistics.getActivityId());
                    vo.setCategoryId(activityCategoryMap.get(statistics.getActivityId()));
                    vo.setTotalDuration(statistics.getTotalDuration()/60);
                    vo.setStatus(statistics.getStatus());
                    vo.setActivityName(activityNameMap.get(statistics.getActivityId()));
                    return vo;
                })
                .collect(Collectors.toList());
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




