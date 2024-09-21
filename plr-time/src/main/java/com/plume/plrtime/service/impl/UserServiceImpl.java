package com.plume.plrtime.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.plume.plrtime.pojo.User;
import com.plume.plrtime.mapper.UserMapper;
import com.plume.plrtime.service.UserService;
import org.springframework.stereotype.Service;

/**
* @author plume
* @description 针对表【plr_user(用户表)】的数据库操作Service实现
* @createDate 2024-09-20 15:28:18
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

}




