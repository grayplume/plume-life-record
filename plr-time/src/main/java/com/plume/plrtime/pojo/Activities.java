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
 * @TableName activities
 */
@TableName(value ="activities")
@Data
public class Activities {
    /**
     * 活动ID
     */
    @TableId(type = IdType.AUTO)
    private Integer activityId;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 活动名称
     */
    private String name;

    /**
     * 活动描述
     */
    private String description;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 活动分类D
     */
    private Integer categoryId;
}