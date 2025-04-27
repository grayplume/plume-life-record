package com.plume.plrtime.mapper;

import com.plume.plrtime.pojo.Statistics;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.plume.plrtime.pojo.vo.ActivityDurationVO;
import com.plume.plrtime.pojo.vo.UserDurationStatsVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
* @author plume
* @description 针对表【statistics】的数据库操作Mapper
* @createDate 2025-03-27 01:06:25
* @Entity com.plume.plrtime.pojo.Statistics
*/
public interface StatisticsMapper extends BaseMapper<Statistics> {


    List<ActivityDurationVO> selectActivityDurationByDate(@Param("date") LocalDate date);


    UserDurationStatsVO selectUserDurationStats(String userId);
}




