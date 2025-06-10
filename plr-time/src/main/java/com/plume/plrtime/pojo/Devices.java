package com.plume.plrtime.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName devices
 */
@TableName(value ="devices")
@Data
public class Devices {
    /**
     * 设备ID
     */
    @TableId(type = IdType.AUTO)
    private Integer deviceId;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 设备名称
     */
    private String name;

    /**
     * 设备类型
     */
    private String type;

    /**
     * 使用状态（在用/闲置/已报废）
     */
    private String status;

    /**
     * 购买价格
     */
    private BigDecimal purchasePrice;

    /**
     * 购买日期
     */
    private LocalDate purchaseDate;

    /**
     * 备注
     */
    private String notes;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}