package com.plume.plrtime.mapper;

import com.plume.plrtime.pojo.Statistics;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.plume.plrtime.pojo.vo.ActivityDurationVO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author plume
* @description 针对表【statistics】的数据库操作Mapper
* @createDate 2025-03-27 01:06:25
* @Entity com.plume.plrtime.pojo.Statistics
*/
public interface StatisticsMapper extends BaseMapper<Statistics> {

    @Select("""
        SELECT 
            a.name AS activity_name,
            SUM(
                GREATEST(
                    LEAST(UNIX_TIMESTAMP(t.start_time) + t.duration, UNIX_TIMESTAMP(CURDATE() + INTERVAL 1 DAY)) 
                    - 
                    GREATEST(UNIX_TIMESTAMP(t.start_time), UNIX_TIMESTAMP(CURDATE())),
                    0
                )
            ) AS total_duration_today
        FROM time_records t
        JOIN activities a ON t.activity_id = a.activity_id
        WHERE 
            t.start_time < CURDATE() + INTERVAL 1 DAY
            AND t.start_time + INTERVAL t.duration SECOND > CURDATE()
        GROUP BY t.activity_id
        """)
    List<ActivityDurationVO> selectTodayActivityDuration();
}




