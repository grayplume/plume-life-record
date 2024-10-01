package com.plume.plrtime.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 活动表
 * @TableName plr_activity
 */
@TableName(value ="plr_activity")
@Data
public class Activity implements Serializable {
    /**
     * 活动
     */
    @TableId(type = IdType.AUTO)
    private Integer aId;

    /**
     * 时间ID
     */
    private Integer tId;

    /**
     * 用户ID
     */
    private Integer uId;

    /**
     * 活动名称
     */
    private String aName;

    /**
     * 活动描述
     */
    private String aDec;

    /**
     * 活动状态 0暂停 1运行
     */
    private Integer aStatus;

    /**
     * 活动总时间
     */
    private Integer aTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}