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
    public Result list(@RequestParam(required = false, defaultValue = "dailyCost") String sortField,
                       @RequestParam(required = false, defaultValue = "desc") String sortOrder) {
        // 获取当前登录用户信息
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 查询所有设备，不筛状态
        LambdaQueryWrapper<Devices> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Devices::getUserId, loginUser.getUser().getUserId());

        List<Devices> list = devicesService.list(lambdaQueryWrapper);

        // 转换为 DevicesVO，计算usageDays和dailyCost
        List<DevicesVO> voList = new java.util.ArrayList<>(list.stream().map(device -> {
            DevicesVO vo = new DevicesVO();
            BeanUtils.copyProperties(device, vo);

            LocalDate purchaseDate = device.getPurchaseDate();
            long usageDays = 1;
            if (purchaseDate != null) {
                usageDays = ChronoUnit.DAYS.between(purchaseDate, LocalDate.now());
                usageDays = Math.max(usageDays, 1);
            }
            vo.setUsageDays(usageDays);

            if (device.getPurchasePrice() != null) {
                BigDecimal dailyCost = device.getPurchasePrice()
                        .divide(BigDecimal.valueOf(usageDays), 2, RoundingMode.HALF_UP);
                vo.setDailyCost(dailyCost);
            }
            return vo;
        }).toList());

        // 先获取字段排序比较器
        Comparator<DevicesVO> fieldComparator = getDevicesVOComparator(sortField, sortOrder);

        // 最终比较器，先按状态排序，“正常”排前，“失效”排后，再按字段排序
        Comparator<DevicesVO> finalComparator = Comparator
                .comparing((DevicesVO vo) -> !"正常".equals(vo.getStatus())) // 正常排前，状态不是“正常”的为true，排后面
                .thenComparing(fieldComparator);

        voList.sort(finalComparator);

        return Result.success(voList);
    }


    /**
     * 根据前端传来的排序字段和顺序排序
     */
    private static Comparator<DevicesVO> getDevicesVOComparator(String sortField, String sortOrder) {
        Comparator<DevicesVO> comparator = switch (sortField) {
            case "purchasePrice" ->
                    Comparator.comparing(DevicesVO::getPurchasePrice, Comparator.nullsLast(BigDecimal::compareTo));
            case "purchaseDate" ->
                    Comparator.comparing(DevicesVO::getPurchaseDate, Comparator.nullsLast(LocalDate::compareTo));
            case "usageDays" -> Comparator.comparingLong(DevicesVO::getUsageDays);
            case "name" -> Comparator.comparing(DevicesVO::getName, Comparator.nullsLast(String::compareTo));
            default -> Comparator.comparing(DevicesVO::getDailyCost, Comparator.nullsLast(BigDecimal::compareTo));
        };

        if ("desc".equalsIgnoreCase(sortOrder)) {
            comparator = comparator.reversed();
        }
        return comparator;
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
