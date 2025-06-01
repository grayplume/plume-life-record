package com.plume.plrtime.pojo.vo;

import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class StatisticsVO {
    private Integer status;
    private Integer activityId;
    private Integer categoryId;
    private String activityName;
    private Integer totalDuration;
    private LocalDateTime updatedAt; // 用于排序，不展示也可以隐藏掉


}
