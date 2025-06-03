package com.plume.plrtime.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 任务表
 * @TableName todo_list
 */
@TableName(value ="todo_list")
@Data
public class TodoList {
    /**
     * 任务ID
     */
    @TableId(type = IdType.AUTO)
    private Integer todoId;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 父任务
     */
    private Integer parentId;

    /**
     * 任务名称
     */
    private String todoName;

    /**
     * 任务类型
     */
    private String todoType;

    /**
     * 任务状态
     */
    private String todoStatus;

    /**
     * 任务当前进度
     */
    private String todoSchedule;

    /**
     * 更新时间
     */
    private Date updatedTime;

    /**
     * 创建时间
     */
    private Date createdTime;
}