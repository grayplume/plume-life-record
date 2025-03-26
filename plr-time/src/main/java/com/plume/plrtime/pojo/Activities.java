package com.plume.plrtime.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer activityId;

    /**
     * 
     */
    private Integer userId;

    /**
     * 
     */
    private String name;

    /**
     * 
     */
    private String description;

    /**
     * 
     */
    private Date createdAt;
}