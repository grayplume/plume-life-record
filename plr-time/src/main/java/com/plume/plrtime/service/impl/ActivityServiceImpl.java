package com.plume.plrtime.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.plume.plrtime.pojo.Activity;
import com.plume.plrtime.service.ActivityService;
import com.plume.plrtime.mapper.ActivityMapper;
import org.springframework.stereotype.Service;

/**
* @author plume
* @description 针对表【plr_activity(活动表)】的数据库操作Service实现
* @createDate 2024-09-20 23:06:38
*/
@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity>
    implements ActivityService{

}




