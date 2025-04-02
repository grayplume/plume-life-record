package com.plume.plrtime.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.plume.plrtime.exception.BusinessException;
import com.plume.plrtime.pojo.Activities;
import com.plume.plrtime.pojo.Statistics;
import com.plume.plrtime.pojo.TimeRecords;
import com.plume.plrtime.pojo.vo.StatisticsVO;
import com.plume.plrtime.service.ActivitiesService;
import com.plume.plrtime.service.StatisticsService;
import com.plume.plrtime.mapper.StatisticsMapper;
import com.plume.plrtime.service.TimeRecordsService;
import org.springframework.stereotype.Service;

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

    public StatisticsServiceImpl(ActivitiesService activitiesService) {
        this.activitiesService = activitiesService;
    }

    @Override
    public List<StatisticsVO> show() {
        // Step 1: 查询所有统计数据
        List<Statistics> statisticsList = this.list();

        // Step 2: 提取所有 activityId
        List<Integer> activityIds = statisticsList.stream()
                .map(Statistics::getActivityId)
                .distinct() // 去重
                .collect(Collectors.toList());

        // Step 3: 批量查询活动名称
        Map<Integer, String> activityNameMap = activitiesService.listByIds(activityIds).stream()
                .collect(Collectors.toMap(Activities::getActivityId, Activities::getName));

        // Step 4: 转换为 StatisticsVO 列表
        return statisticsList.stream()
                .map(statistics -> {
                    StatisticsVO vo = new StatisticsVO();
                    vo.setActivityId(statistics.getActivityId());
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
}




