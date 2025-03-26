package com.plume.plrtime.service;

import com.plume.plrtime.pojo.Users;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author plume
* @description 针对表【users】的数据库操作Service
* @createDate 2025-03-26 22:11:30
*/
public interface UsersService extends IService<Users> {

    Users login(Users user);
}
