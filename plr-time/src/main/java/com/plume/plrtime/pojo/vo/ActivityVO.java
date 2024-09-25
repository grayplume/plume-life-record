package com.plume.plrtime.pojo.vo;

import com.plume.plrtime.pojo.Activity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ActivityVO extends Activity {
    private Integer today_time;
}
