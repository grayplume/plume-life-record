package com.plume.plrtime.controller;

import com.plume.plrtime.common.Constants;
import com.plume.plrtime.common.Result;
import com.plume.plrtime.exception.BusinessException;
import com.plume.plrtime.exception.TimeException;
import com.plume.plrtime.pojo.Activity;
import com.plume.plrtime.pojo.Time;
import com.plume.plrtime.pojo.dto.TimeDTO;
import com.plume.plrtime.service.ActivityService;
import com.plume.plrtime.service.TimeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/time")
public class TimeController {

    @Resource
    private TimeService timeService;

    @GetMapping("/list")
    public Result list() {
        List<Time> list = timeService.list();
        return Result.success(list);
    }

    @PostMapping("/start")
    public Result start(@RequestBody TimeDTO timeDTO) {
        return timeService.start(timeDTO);
    }






}
