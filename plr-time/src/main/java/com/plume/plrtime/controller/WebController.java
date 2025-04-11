package com.plume.plrtime.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plume.plrtime.common.Result;
import com.plume.plrtime.mapper.UsersMapper;
import com.plume.plrtime.pojo.Activities;
import com.plume.plrtime.pojo.TimeRecords;
import com.plume.plrtime.pojo.Users;
import com.plume.plrtime.pojo.vo.ActivityDurationVO;
import com.plume.plrtime.pojo.vo.LoginUser;
import com.plume.plrtime.pojo.vo.StatisticsVO;
import com.plume.plrtime.service.ActivitiesService;
import com.plume.plrtime.service.StatisticsService;
import com.plume.plrtime.service.TimeRecordsService;
import com.plume.plrtime.service.UsersService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/web")
public class WebController {

    private final StatisticsService statisticsService;
    private final ActivitiesService activitiesService;
    private final TimeRecordsService timeRecordsService;
    private final UsersService usersService;
    private final UsersMapper usersMapper;

    public WebController(StatisticsService statisticsService, ActivitiesService activitiesService, TimeRecordsService timeRecordsService, UsersService usersService, UsersMapper usersMapper) {
        this.statisticsService = statisticsService;
        this.activitiesService = activitiesService;
        this.timeRecordsService = timeRecordsService;
        this.usersService = usersService;
        this.usersMapper = usersMapper;
    }

    @GetMapping("/test")
    public String showTodayTimeDistribution(Model model) throws JsonProcessingException {
        List<TimeRecords> list = timeRecordsService.list();

        // 计算每小时的总时长（按日期区分）
        Map<LocalDate, Map<Integer, Integer>> dailyHourlyDuration = new HashMap<>();

        for (TimeRecords stat : list) {
            LocalDate date = stat.getStartTime().toLocalDate();
            int hour = stat.getStartTime().getHour();
            int minute = stat.getStartTime().getMinute();
            int second = stat.getStartTime().getSecond();
            int duration = stat.getDuration(); // 总时长（秒）

            LocalDateTime endTime = stat.getStartTime().plusSeconds(duration);
            LocalDate endDate = endTime.toLocalDate();

            // 只计算 **今天** 及 **跨天延续到今天的**
            if (date.equals(LocalDate.now()) || endDate.equals(LocalDate.now())) {
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

        LocalDate current = oneMonthAgo;
        while (!current.isAfter(now)) {
            dailyDuration.putIfAbsent(current, 0);  // 填充缺失日期
            current = current.plusDays(1);
        }

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



        // 今日时间分布
        List<ActivityDurationVO> todayActivityDurations = statisticsService.getTodayActivityDurations();
        String json = new ObjectMapper().writeValueAsString(todayActivityDurations);
        model.addAttribute("todayActivityDurationsJson", json);
        System.out.println("json = " + json);


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


    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/")
    public String info(Model model) {
        System.out.println("index controller");

        // 获取当前认证上下文
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();

        // 获取当前认证用户的相关信息
        String username = authentication.getName();  // 用户名



        // 打印认证信息
        System.out.println("Username: " + username);

        System.out.println("============");
        System.out.println(loginUser.getUser().getUserId()+loginUser.getUser().getUsername());
        // 将用户名添加到 Model 中
        model.addAttribute("username", username);

        // 返回视图名称，Thymeleaf 会根据视图名称渲染页面
        return "info";
    }



    @GetMapping("/index")
    public String index(Model model) {
        List<Activities> activitiesList = activitiesService.list();

        List<StatisticsVO> statisticsVOS = statisticsService.show();
        // 将信息转换成activityId,duration的map
        Map<Integer, Integer> activityIdDurationMap = statisticsVOS.stream()
                .collect(java.util.stream.Collectors.toMap(StatisticsVO::getActivityId, StatisticsVO::getTotalDuration));

        // 根据statisticsVOS将activitiesList排序
        Map<Integer, Integer> activityIdOrderMap = new HashMap<>();
        for (int i = 0; i < statisticsVOS.size(); i++) {
            activityIdOrderMap.put(statisticsVOS.get(i).getActivityId(), i);
        }

        activitiesList.sort((a1, a2) -> {
            Integer index1 = activityIdOrderMap.getOrDefault(a1.getActivityId(), Integer.MAX_VALUE);
            Integer index2 = activityIdOrderMap.getOrDefault(a2.getActivityId(), Integer.MAX_VALUE);
            return index1.compareTo(index2);
        });


        model.addAttribute("activitiesList", activitiesList);

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


        List<StatisticsVO> statisticsVOS = statisticsService.show();
        // 将信息转换成activityId,duration的map
        Map<Integer, Integer> activityIdDurationMap = statisticsVOS.stream()
                .collect(java.util.stream.Collectors.toMap(StatisticsVO::getActivityId, StatisticsVO::getTotalDuration));

        // 根据statisticsVOS将activitiesList排序
        Map<Integer, Integer> activityIdOrderMap = new HashMap<>();
        for (int i = 0; i < statisticsVOS.size(); i++) {
            activityIdOrderMap.put(statisticsVOS.get(i).getActivityId(), i);
        }

        activitiesList.sort((a1, a2) -> {
            Integer index1 = activityIdOrderMap.getOrDefault(a1.getActivityId(), Integer.MAX_VALUE);
            Integer index2 = activityIdOrderMap.getOrDefault(a2.getActivityId(), Integer.MAX_VALUE);
            return index1.compareTo(index2);
        });

        // 获取当前用户的认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();

        model.addAttribute("activitiesList", activitiesList);
        model.addAttribute("uid", loginUser.getUser().getUserId());
        model.addAttribute("statisticsVOS", activityIdDurationMap);
        return "activity";
    }
}
