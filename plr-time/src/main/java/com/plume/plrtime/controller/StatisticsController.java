package com.plume.plrtime.controller;

import com.plume.plrtime.common.Result;
import com.plume.plrtime.pojo.vo.ActivityDurationVO;
import com.plume.plrtime.pojo.vo.StatisticsVO;
import com.plume.plrtime.service.StatisticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {
    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/list")
    public Object list() {
        return statisticsService.list();
    }

    /**
     * 展示统计信息
     */
    @GetMapping("/show")
    public Result show() {
        List<StatisticsVO> statisticsVOS = statisticsService.show();
        return Result.success(statisticsVOS);
    }

    @GetMapping("/dayTimeDistribution")
     public Result dayTimeDistribution(String date,Integer categoryId) {
        return Result.success(statisticsService.selectDayTimeDistribution(date,categoryId));
    }

    @GetMapping("/monthTimeDistribution")
    public Result monthTimeDistribution(String month, Integer categoryId) {
        return Result.success(statisticsService.selectMonthTimeDistribution(month, categoryId));
    }

    @GetMapping("/activityRanking")
    public Result activityRanking(String date, Integer categoryId) {
        List<ActivityDurationVO> ranking = statisticsService.selectActivityRanking(date, categoryId);
        return Result.success(ranking);
    }

    @GetMapping("/activityRankingByMonth")
    public Result activityRankingByMonth(String month, Integer categoryId) {
        List<ActivityDurationVO> ranking = statisticsService.selectMonthlyActivityRanking(month, categoryId);
        return Result.success(ranking);
    }



}
