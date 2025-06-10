package com.plume.plrtime.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.plume.plrtime.common.Result;
import com.plume.plrtime.pojo.Activities;
import com.plume.plrtime.pojo.Devices;
import com.plume.plrtime.pojo.vo.DevicesVO;
import com.plume.plrtime.pojo.vo.LoginUser;
import com.plume.plrtime.service.DevicesService;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/devices")
public class DevicesController {
    private final DevicesService devicesService;

    public DevicesController(DevicesService devicesService) {
        this.devicesService = devicesService;
    }

    /**
     * 查询所有设备
     */
    @GetMapping("/list")
    public Result list() {
        // 获取当前登录用户信息
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LambdaQueryWrapper<Devices> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Devices::getUserId, loginUser.getUser().getUserId());
        List<Devices> list = devicesService.list(lambdaQueryWrapper);

        // 转换为 DevicesVO
        List<DevicesVO> voList = new java.util.ArrayList<>(list.stream().map(device -> {
            DevicesVO vo = new DevicesVO();
            BeanUtils.copyProperties(device, vo);

            // 使用天数计算：购买日至今天（最小为1天）
            LocalDate purchaseDate = device.getPurchaseDate();
            long usageDays = 1;
            if (purchaseDate != null) {
                usageDays = ChronoUnit.DAYS.between(purchaseDate, LocalDate.now());
                usageDays = Math.max(usageDays, 1)
                ;
            }
            vo.setUsageDays(usageDays);

            // 日均成本 = 购买价值 / 使用天数
            if (device.getPurchasePrice() != null) {
                BigDecimal dailyCost = device.getPurchasePrice()
                        .divide(BigDecimal.valueOf(usageDays), 2, RoundingMode.HALF_UP);
                vo.setDailyCost(dailyCost);
            }
            return vo;
        }).toList());

        // 根据日均成本排序降序
        voList.sort(Comparator.comparing(DevicesVO::getDailyCost).reversed());

        return Result.success(voList);
    }

    /**
     * 添加设备
     */
    @PostMapping("/save")
    public Result save(@RequestBody Devices devices) {
        // 获取当前登录用户信息
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        devices.setUserId(loginUser.getUser().getUserId());
        boolean save = devicesService.save(devices);
        if (!save) {
            return Result.error();
        }
        return Result.success();
    }

    /**
     * 修改设备
     */
    @PostMapping("/update")
    public Result update(@RequestBody Devices devices) {
        boolean update = devicesService.updateById(devices);
        return Result.success(update);
    }

    /**
     * 删除设备
     */
    @PostMapping("/delete/{ids}")
    public Result delete(@PathVariable("ids") List<Long> ids) {
        boolean remove = devicesService.removeByIds(ids);
        return Result.success(remove);
    }


}
