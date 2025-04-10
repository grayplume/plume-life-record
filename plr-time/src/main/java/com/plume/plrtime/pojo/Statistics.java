package com.plume.plrtime.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName statistics
 */
@TableName(value ="statistics")
@Data
public class Statistics {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer statId;

    /**
     * 
     */
    private Integer userId;

    /**
     * 
     */
    private Integer activityId;

    /**
     * 
     */
    private LocalDateTime updatedAt;

    /**
     * 
     */
    private Integer totalDuration;

    /**
     * 
     */
    private Integer status;

    private LocalDateTime createdAt;
}