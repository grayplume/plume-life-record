package com.plume.plrtime.service;

import com.plume.plrtime.pojo.Statistics;
import com.baomidou.mybatisplus.extension.service.IService;
import com.plume.plrtime.pojo.vo.ActivityDurationVO;
import com.plume.plrtime.pojo.vo.StatisticsVO;
import com.plume.plrtime.pojo.vo.UserDurationStatsVO;

import java.util.List;

/**
* @author plume
* @description 针对表【statistics】的数据库操作Service
* @createDate 2025-03-27 01:06:25
*/
public interface StatisticsService extends IService<Statistics> {

    List<StatisticsVO> show();

    List<StatisticsVO> getByUserId(Integer userId);

    List<ActivityDurationVO> getTodayActivityDurations();

    UserDurationStatsVO getUserDurationStats();
}
