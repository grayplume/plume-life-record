package com.plume.plrtime.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 时间记录表
 * @TableName plr_time
 */
@TableName(value ="plr_time")
@Data
public class Time implements Serializable {
    /**
     * 记录ID
     */
    @TableId(type = IdType.AUTO)
    private Integer tId;

    /**
     * 用户ID
     */
    private Integer uId;

    /**
     * 活动ID
     */
    private Integer aId;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 持续时间
     */
    private Long tDuration;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}