package com.plume.plrtime.controller;

import com.plume.plrtime.common.Result;
import com.plume.plrtime.pojo.dto.TimeRequest;
import com.plume.plrtime.service.TimeRecordsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/timeRecord")
public class TimeRecordController {

    private final TimeRecordsService timeRecordsService;

    public TimeRecordController(TimeRecordsService timeRecordsService)
    {
        this.timeRecordsService = timeRecordsService;
    }

    /**
     * 获取所有记录
     */
    @GetMapping("/list")
    public Result list()
    {
        return Result.success(timeRecordsService.list());
    }

    /**
     * 开始时间记录
     */
    @PostMapping("/start")
    public Result start(@RequestBody TimeRequest request) {

        // 调用服务层方法开始计时
        Boolean record = timeRecordsService.startTimer(request.getUserId(), request.getActivityId());
        return Result.success(record);

    }

    /**
     * 结束时间记录
     */
    @PostMapping("/end")
    public Result end(@RequestBody TimeRequest request) {

        // 调用服务层方法停止计时
        Boolean b = timeRecordsService.endTimer(request.getUserId(), request.getActivityId());
        return Result.success(b);

    }

}
