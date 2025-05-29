package com.plume.plrtime.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.plume.plrtime.common.Result;
import com.plume.plrtime.pojo.TimeRecords;
import com.plume.plrtime.pojo.dto.TimeRequest;
import com.plume.plrtime.service.TimeRecordsService;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/timeRecords")
public class TimeRecordsController {

    private final TimeRecordsService timeRecordsService;

    public TimeRecordsController(TimeRecordsService timeRecordsService)
    {
        this.timeRecordsService = timeRecordsService;
    }

    /**
     * 获取所有记录
     */
    @GetMapping("/list")
    public Result list()
    {
        QueryWrapper<TimeRecords> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("created_at").last("LIMIT 20");

        return Result.success(timeRecordsService.list(wrapper));
    }

    @PostMapping("/save")
    public Result save(@RequestBody TimeRecords record) {
        return Result.success(timeRecordsService.save(record));
    }

    @PostMapping("/update")
    public Result update(@RequestBody TimeRecords record) {
        record.setDuration((int) Duration.between(record.getStartTime(),  record.getEndTime()).getSeconds());
        return Result.success(timeRecordsService.updateById(record));
    }

    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        return Result.success(timeRecordsService.removeById(id));
    }


}
