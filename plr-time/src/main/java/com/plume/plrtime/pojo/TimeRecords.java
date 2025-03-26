package com.plume.plrtime.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName time_records
 */
@TableName(value ="time_records")
@Data
public class TimeRecords {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer recordId;

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
    private Date startTime;

    /**
     * 
     */
    private Date endTime;

    /**
     * 
     */
    private Integer duration;

    /**
     * 
     */
    private String notes;

    /**
     * 
     */
    private Date createdAt;
}