package com.plume.plrtime.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DayTimeDistributionVO {
    private Integer hour;           // 小时（0~23）
    private Integer durationMinutes; // 持续时间（分钟）
}

