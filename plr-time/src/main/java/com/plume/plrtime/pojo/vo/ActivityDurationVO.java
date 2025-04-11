package com.plume.plrtime.pojo.vo;

import lombok.Data;

@Data
public class ActivityDurationVO {
    private String activityName;
    private Long totalDurationToday; // 秒
}
