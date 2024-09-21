package com.plume.plrtime.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.plume.plrtime.pojo.Time;
import com.plume.plrtime.service.TimeService;
import com.plume.plrtime.mapper.TimeMapper;
import org.springframework.stereotype.Service;

/**
* @author plume
* @description 针对表【plr_time(时间记录表)】的数据库操作Service实现
* @createDate 2024-09-20 18:20:48
*/
@Service
public class TimeServiceImpl extends ServiceImpl<TimeMapper, Time>
    implements TimeService{

}




