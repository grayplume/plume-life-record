package com.plume.plrtime.service;

import com.plume.plrtime.pojo.TimeRecords;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author plume
* @description 针对表【time_records】的数据库操作Service
* @createDate 2025-03-26 23:25:21
*/
public interface TimeRecordsService extends IService<TimeRecords> {

    Boolean startTimer(Long userId, Long activityId);

    Boolean endTimer(Long userId, Long activityId);

}
