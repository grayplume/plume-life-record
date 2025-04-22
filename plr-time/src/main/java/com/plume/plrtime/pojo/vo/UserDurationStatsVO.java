package com.plume.plrtime.pojo.vo;

import lombok.Data;

@Data
public class UserDurationStatsVO {
    private Integer userId;
    private String username;
    private Integer totalMinutes;
    private Integer yearMinutes;
    private Integer monthMinutes;
    private Integer weekMinutes;
    private Integer todayMinutes;
}
