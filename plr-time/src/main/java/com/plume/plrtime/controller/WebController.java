package com.plume.plrtime.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plume.plrtime.common.Result;
import com.plume.plrtime.pojo.Activities;
import com.plume.plrtime.pojo.TimeRecords;
import com.plume.plrtime.pojo.vo.StatisticsVO;
import com.plume.plrtime.service.ActivitiesService;
import com.plume.plrtime.service.StatisticsService;
import com.plume.plrtime.service.TimeRecordsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/web")
public class WebController {

    private final StatisticsService statisticsService;
    private final ActivitiesService activitiesService;
    private final TimeRecordsService timeRecordsService;

    public WebController(StatisticsService statisticsService, ActivitiesService activitiesService, TimeRecordsService timeRecordsService) {
        this.statisticsService = statisticsService;
        this.activitiesService = activitiesService;
        this.timeRecordsService = timeRecordsService;
    }

    @GetMapping("/test")
    public String showTodayTimeDistribution(Model model) throws JsonProcessingException {
        List<TimeRecords> list = timeRecordsService.list();

        // 计算每小时的总时长（按日期区分）
        Map<LocalDate, Map<Integer, Integer>> dailyHourlyDuration = new HashMap<>();


        for (TimeRecords stat : list) {
            if (stat.getStartTime().toLocalDate().equals(LocalDate.now())) {
                LocalDate date = stat.getStartTime().toLocalDate();
                int hour = stat.getStartTime().getHour();
                int minute = stat.getStartTime().getMinute();
                int second = stat.getStartTime().getSecond();
                int duration = stat.getDuration(); // 总时长（秒）

                while (duration > 0) {
                    // 获取当前小时还剩多少秒
                    int remainingTimeInHour = 3600 - (minute * 60 + second);

                    // 计算本小时内最多能加多少秒
                    int timeToAdd = Math.min(duration, remainingTimeInHour);

                    // 记录时间到当前日期和小时
                    dailyHourlyDuration
                            .computeIfAbsent(date, k -> new HashMap<>())  // 如果当天没有数据，初始化
                            .merge(hour, timeToAdd, Integer::sum);  // 累加到当前小时

                    // 更新剩余时间
                    duration -= timeToAdd;

                    // 进入下一个小时
                    if (duration > 0) {
                        hour = (hour + 1) % 24;  // 小时进位

                        // 如果到了 00:00，说明跨天了，日期也要变
                        if (hour == 0) {
                            date = date.plusDays(1); // 日期加一天
                        }

                        // 从新小时的 0 分钟 0 秒开始
                        minute = 0;
                        second = 0;
                    }
                }
            }
        }

        //计算当月
        // 获取当前日期
        LocalDate now = LocalDate.now();
        // 获取一个月前的日期
        LocalDate oneMonthAgo = now.minusMonths(1);
        // 初始化一个Map来存储每日学习时长
        Map<LocalDate, Integer> dailyDuration = new HashMap<>();
        // 遍历学习记录
        for (TimeRecords stat : list) {
            LocalDateTime startTime = stat.getStartTime();
            LocalDate date = startTime.toLocalDate();
            // 只处理最近一个月内的数据
            if (!date.isBefore(oneMonthAgo) && !date.isAfter(now)) {
                // 累加每天的学习时长
                int duration = stat.getDuration();
                dailyDuration.put(date, dailyDuration.getOrDefault(date, 0) + duration);
            }
        }
        // 按照日期排序
        List<Map.Entry<LocalDate, Integer>> sortedList = new ArrayList<>(dailyDuration.entrySet());
        sortedList.sort(Map.Entry.comparingByKey());

        // 将数据传递到Thymeleaf模板
        // 转化为JSON 字符串
        ObjectMapper objectMapper2 = new ObjectMapper();
        String dailyDurationJson = objectMapper2.writeValueAsString(sortedList);
//        System.out.println(dailyDurationJson);
        model.addAttribute("dailyDurationJson", dailyDurationJson);


        // 将 hourlyDuration 转换为 JSON 字符串并传递给前端
        ObjectMapper objectMapper = new ObjectMapper();
        String hourlyDurationJson = objectMapper.writeValueAsString(dailyHourlyDuration);
        model.addAttribute("hourlyDurationJson", hourlyDurationJson);

        return "test"; // 返回 Thymeleaf 模板
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
