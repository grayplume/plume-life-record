package com.plume.plrtime.mapper;

import com.plume.plrtime.pojo.Users;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author plume
* @description 针对表【users】的数据库操作Mapper
* @createDate 2025-03-26 22:11:30
* @Entity com.plume.plrtime.pojo.Users
*/
public interface UsersMapper extends BaseMapper<Users> {

    Users selectByUsername(String username);
}




