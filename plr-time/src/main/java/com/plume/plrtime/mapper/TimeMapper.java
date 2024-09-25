package com.plume.plrtime.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.plume.plrtime.pojo.Time;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.Date;
import java.util.function.Function;

/**
* @author plume
* @description 针对表【plr_time(时间记录表)】的数据库操作Mapper
* @createDate 2024-09-20 18:20:48
* @Entity com.plume.plrtime.domain.Time
*/
public interface TimeMapper extends BaseMapper<Time> {

    @Select("SELECT SUM(t_duration) AS total_duration FROM plr_time WHERE a_id = #{aId} AND end_time >= #{endTime}")
    Integer getTotalDurationByAIdAndEndTime(@Param("aId") Integer aId, @Param("endTime") Date endTime);
}




