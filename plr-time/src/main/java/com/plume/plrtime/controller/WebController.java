package com.plume.plrtime.controller;

import com.plume.plrtime.common.Result;
import com.plume.plrtime.pojo.Activities;
import com.plume.plrtime.pojo.vo.StatisticsVO;
import com.plume.plrtime.service.ActivitiesService;
import com.plume.plrtime.service.StatisticsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/web")
public class WebController {

    private final StatisticsService statisticsService;
    private final ActivitiesService activitiesService;

    public WebController(StatisticsService statisticsService, ActivitiesService activitiesService) {
        this.statisticsService = statisticsService;
        this.activitiesService = activitiesService;
    }

    @GetMapping("/test")
    public String hello(Model model) {
        return "test";
    }

    @GetMapping("/index")
    public String index(Model model) {
        List<Activities> activitiesList = activitiesService.list();
        model.addAttribute("activitiesList", activitiesList);

        List<StatisticsVO> statisticsVOS = statisticsService.show();
        // 将信息转换成activityId,duration的map
        Map<Integer, Integer> activityIdDurationMap = statisticsVOS.stream()
                .collect(java.util.stream.Collectors.toMap(StatisticsVO::getActivityId, StatisticsVO::getTotalDuration));

        model.addAttribute("statisticsVOS", activityIdDurationMap);

        // 将信息转换成activityId,status的map
        Map<Integer, Integer> activityIdStatusMap = statisticsVOS.stream()
                .collect(java.util.stream.Collectors.toMap(StatisticsVO::getActivityId, StatisticsVO::getStatus));
        model.addAttribute("activityIdStatusMap", activityIdStatusMap);

        return "index";
    }

    @GetMapping("/activity")
    public String activity(Model model) {
        List<Activities> activitiesList = activitiesService.list();
        model.addAttribute("activitiesList", activitiesList);

        List<StatisticsVO> statisticsVOS = statisticsService.show();
        // 将信息转换成activityId,duration的map
        Map<Integer, Integer> activityIdDurationMap = statisticsVOS.stream()
                .collect(java.util.stream.Collectors.toMap(StatisticsVO::getActivityId, StatisticsVO::getTotalDuration));

        model.addAttribute("statisticsVOS", activityIdDurationMap);
        return "activity";
    }
}
