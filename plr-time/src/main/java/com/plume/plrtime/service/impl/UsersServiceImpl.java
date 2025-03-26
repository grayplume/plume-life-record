package com.plume.plrtime.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.plume.plrtime.common.Constants;
import com.plume.plrtime.exception.BusinessException;
import com.plume.plrtime.pojo.Users;
import com.plume.plrtime.service.UsersService;
import com.plume.plrtime.mapper.UsersMapper;
import org.springframework.stereotype.Service;

/**
* @author plume
* @description 针对表【users】的数据库操作Service实现
* @createDate 2025-03-26 22:11:30
*/
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users>
    implements UsersService{

    private final UsersMapper usersMapper;

    public UsersServiceImpl(UsersMapper usersMapper) {
        this.usersMapper = usersMapper;
    }


    @Override
    public Users login(Users user) {

        // 1. 验证用户输入
        if (user == null || StringUtils.isEmpty(user.getUsername()) || StringUtils.isEmpty(user.getPasswordHash())) {
            throw new BusinessException(501,"用户名或密码不能为空");
        }

        // 2. 根据用户名查询数据库
        Users dbUser = usersMapper.selectByUsername(user.getUsername());
        if (dbUser == null) {
            throw new BusinessException(502,"用户不存在");
        }

        // 3. 校验密码
        if (!dbUser.getPasswordHash().equals(user.getPasswordHash())) {
            throw new BusinessException(503,"密码错误");
        }

        // 4. 返回用户信息（可以剔除敏感信息，如密码）
        dbUser.setPasswordHash(null); // 清除密码字段
        return dbUser;
    }


}




