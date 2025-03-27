package com.plume.plrtime.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

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
    private LocalDateTime date;

    /**
     * 
     */
    private Integer totalDuration;

    /**
     * 
     */
    private LocalDateTime createdAt;
}