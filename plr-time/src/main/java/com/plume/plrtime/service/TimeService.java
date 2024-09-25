package com.plume.plrtime.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.plume.plrtime.common.Result;
import com.plume.plrtime.pojo.Time;
import com.baomidou.mybatisplus.extension.service.IService;
import com.plume.plrtime.pojo.dto.TimeDTO;

import java.math.BigDecimal;
import java.util.function.Function;

/**
* @author plume
* @description 针对表【plr_time(时间记录表)】的数据库操作Service
* @createDate 2024-09-20 18:20:48
*/
public interface TimeService extends IService<Time> {

    Result start(TimeDTO timeDTO);
}
