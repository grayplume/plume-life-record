package com.plume.plrtime.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.plume.plrtime.pojo.Statistics;
import com.baomidou.mybatisplus.extension.service.IService;
import com.plume.plrtime.pojo.vo.*;

import java.time.LocalDate;
import java.util.List;

/**
* @author plume
* @description 针对表【statistics】的数据库操作Service
* @createDate 2025-03-27 01:06:25
*/
public interface StatisticsService extends IService<Statistics> {

    List<StatisticsVO> show();

    List<ActivityDurationVO> getActivityDurationsByDate(LocalDate date);

    UserDurationStatsVO getUserDurationStats();

    List<DayTimeDistributionVO> selectDayTimeDistribution(String date, Integer categoryId);

    List<DateDurationVO> selectMonthTimeDistribution(String month, Integer categoryId);
}
