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
import com.plume.plrtime.pojo.vo.UserDurationStatsVO;
import com.plume.plrtime.service.ActivitiesService;
import com.plume.plrtime.service.StatisticsService;
import com.plume.plrtime.service.TimeRecordsService;
import com.plume.plrtime.service.UsersService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
        // 获取学习记录
        List<TimeRecords> list = timeRecordsService.list();

        // 初始化数据结构
        Map<LocalDate, Map<Integer, Integer>> dailyHourlyDuration = new HashMap<>();
        Map<LocalDate, Integer> dailyDuration = new HashMap<>();
        LocalDate now = LocalDate.now();
        LocalDate oneMonthAgo = now.minusMonths(1);

        // 填充日期映射
        fillMissingDates(dailyDuration, oneMonthAgo, now);

        // 遍历记录，计算每日和每小时的时长
        for (TimeRecords stat : list) {
            LocalDateTime startTime = stat.getStartTime();
            LocalDate date = startTime.toLocalDate();
            int duration = stat.getDuration();

            // 处理今日及跨天数据
            if (isTodayOrCrossDay(startTime, duration)) {
                calculateHourlyDuration(startTime, duration, date, dailyHourlyDuration);
            }

            // 处理一个月内的每日总时长
            if (!date.isBefore(oneMonthAgo) && !date.isAfter(now)) {
                dailyDuration.put(date, dailyDuration.getOrDefault(date, 0) + duration);
            }
        }

        // 排序每日时长
        List<Map.Entry<LocalDate, Integer>> sortedDailyDuration = sortByDate(dailyDuration);

        // 获取今日活动时长
//        List<ActivityDurationVO> todayActivityDurations = statisticsService.getActivityDurationsByDate();
//        String todayActivityJson = new ObjectMapper().writeValueAsString(todayActivityDurations);
//        model.addAttribute("todayActivityDurationsJson", todayActivityJson);

        // 获取用户总时长
        UserDurationStatsVO userDurationStats = statisticsService.getUserDurationStats();
        model.addAttribute("totalDurationFormatted", formatMinutes(userDurationStats.getTotalMinutes()));
        model.addAttribute("yearDurationFormatted", formatMinutes(userDurationStats.getYearMinutes()));
        model.addAttribute("monthDurationFormatted", formatMinutes(userDurationStats.getMonthMinutes()));
        model.addAttribute("weekDurationFormatted", formatMinutes(userDurationStats.getWeekMinutes()));
        model.addAttribute("todayDurationFormatted", formatMinutes(userDurationStats.getTodayMinutes()));

        // 将每日时长转为JSON
        String dailyDurationJson = new ObjectMapper().writeValueAsString(sortedDailyDuration);
        model.addAttribute("dailyDurationJson", dailyDurationJson);

        // 将每小时时长转为JSON
        String hourlyDurationJson = new ObjectMapper().writeValueAsString(dailyHourlyDuration);
        model.addAttribute("hourlyDurationJson", hourlyDurationJson);

        return "test"; // 返回 Thymeleaf 模板

    }

    @GetMapping("/duration/day")
    @ResponseBody
    public Result getActivityDurationByDate(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        List<ActivityDurationVO> durations = statisticsService.getActivityDurationsByDate(date);
        return Result.success(durations);
    }

    private String formatMinutes(Integer minutes) {
        if (minutes == null) return "00h 00m";
        int hours = minutes / 60;
        int mins = minutes % 60;
        return String.format("%02dh %02dm", hours, mins);
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

        // 构建三个 Map：duration、status、order
        Map<Integer, Integer> activityIdDurationMap = new HashMap<>();
        Map<Integer, Integer> activityIdStatusMap = new HashMap<>();
        Map<Integer, Integer> activityIdOrderMap = new HashMap<>();

        for (int i = 0; i < statisticsVOS.size(); i++) {
            StatisticsVO stat = statisticsVOS.get(i);
            int activityId = stat.getActivityId();
            activityIdDurationMap.put(activityId, stat.getTotalDuration());
            activityIdStatusMap.put(activityId, stat.getStatus());
            activityIdOrderMap.put(activityId, i); // 排序用下标
        }
        // 根据 order map 排序 activitiesList
        activitiesList.sort(Comparator.comparingInt(a ->
                activityIdOrderMap.getOrDefault(a.getActivityId(), Integer.MAX_VALUE)
        ));
        // 添加到 model
        model.addAttribute("activitiesList", activitiesList);
        model.addAttribute("statisticsVOS", activityIdDurationMap);
        model.addAttribute("activityIdStatusMap", activityIdStatusMap);

        return "index";
    }

    @GetMapping("/activity")
    public String activity(Model model) {
        // 获取活动列表和统计数据
        List<Activities> activitiesList = activitiesService.list();
        List<StatisticsVO> statisticsVOS = statisticsService.show();

        // 构建 ID → Duration、Order Map
        Map<Integer, Integer> activityDurationMap = new HashMap<>();
        Map<Integer, Integer> activityOrderMap = new HashMap<>();

        for (int i = 0; i < statisticsVOS.size(); i++) {
            StatisticsVO stat = statisticsVOS.get(i);
            int activityId = stat.getActivityId();
            activityDurationMap.put(activityId, stat.getTotalDuration());
            activityOrderMap.put(activityId, i); // 排序下标
        }

        // 排序 activitiesList：按统计顺序排序，没统计数据的排后面
        activitiesList.sort(Comparator.comparingInt(a ->
                activityOrderMap.getOrDefault(a.getActivityId(), Integer.MAX_VALUE)
        ));

        // 获取当前登录用户信息
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 添加到模型
        model.addAttribute("activitiesList", activitiesList);
        model.addAttribute("statisticsVOS", activityDurationMap);
        model.addAttribute("uid", loginUser.getUser().getUserId());

        return "activity";

    }

    // 填充一个月内的所有日期
    private void fillMissingDates(Map<LocalDate, Integer> dailyDuration, LocalDate startDate, LocalDate endDate) {
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            dailyDuration.putIfAbsent(current, 0);  // 填充缺失日期
            current = current.plusDays(1);
        }
    }

    // 判断是否是今天或跨天
    private boolean isTodayOrCrossDay(LocalDateTime startTime, int duration) {
        LocalDate date = startTime.toLocalDate();
        LocalDateTime endTime = startTime.plusSeconds(duration);
        return date.equals(LocalDate.now()) || endTime.toLocalDate().equals(LocalDate.now());
    }

    // 计算每小时的时长
    private void calculateHourlyDuration(LocalDateTime startTime, int duration, LocalDate date, Map<LocalDate, Map<Integer, Integer>> dailyHourlyDuration) {
        int hour = startTime.getHour();
        int minute = startTime.getMinute();
        int second = startTime.getSecond();

        while (duration > 0) {
            int remainingTimeInHour = 3600 - (minute * 60 + second);
            int timeToAdd = Math.min(duration, remainingTimeInHour);

            dailyHourlyDuration
                    .computeIfAbsent(date, k -> new HashMap<>())
                    .merge(hour, timeToAdd, Integer::sum);

            duration -= timeToAdd;

            // 进入下一个小时
            if (duration > 0) {
                hour = (hour + 1) % 24;
                if (hour == 0) {
                    date = date.plusDays(1);
                }
                minute = 0;
                second = 0;
            }
        }
    }

    // 按日期排序
    private List<Map.Entry<LocalDate, Integer>> sortByDate(Map<LocalDate, Integer> dailyDuration) {
        List<Map.Entry<LocalDate, Integer>> sortedList = new ArrayList<>(dailyDuration.entrySet());
        sortedList.sort(Map.Entry.comparingByKey());
        return sortedList;
    }

}
