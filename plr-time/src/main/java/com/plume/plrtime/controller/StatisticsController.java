package com.plume.plrtime.controller;

import com.plume.plrtime.common.Result;
import com.plume.plrtime.pojo.vo.StatisticsVO;
import com.plume.plrtime.service.StatisticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    /**
     * 根据用户ID获取统计信息
     */
    @GetMapping("/getByUserId")
    public Result getByUserId(Integer userId) {
        List<StatisticsVO> statisticsVOS = statisticsService.getByUserId(userId);
        return Result.success(statisticsVOS);
    }
}
