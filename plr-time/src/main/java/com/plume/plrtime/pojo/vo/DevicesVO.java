package com.plume.plrtime.pojo.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DevicesVO {

    private Integer deviceId;
    private String name;              // 设备名称
    private String type;              // 设备类型
    private LocalDate purchaseDate;   // 购买日期
    private BigDecimal purchasePrice; // 购买价值
    private String status;            // 使用状态（在用/闲置/已报废）
    private String notes;             // 备注

    // ✅ 由后端计算得出：
    private Long usageDays;           // 使用天数（购买日至今）
    private BigDecimal dailyCost;     // 日均成本（购买价值 ÷ 使用天数）

}
