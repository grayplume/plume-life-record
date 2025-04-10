package com.plume.plrtime.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.plume.plrtime.mapper.UsersMapper;
import com.plume.plrtime.pojo.Users;
import com.plume.plrtime.pojo.vo.LoginUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Component
public class DBUserDetailsManager implements UserDetailsManager, UserDetailsPasswordService {

    private UsersMapper usersMapper;

    public DBUserDetailsManager(UsersMapper usersMapper) {
        this.usersMapper = usersMapper;
    }


    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        return null;
    }

    @Override
    public void createUser(UserDetails user) {

    }

    @Override
    public void updateUser(UserDetails user) {

    }

    @Override
    public void deleteUser(String username) {

    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

    }

    @Override
    public boolean userExists(String username) {
        return false;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",username);
        Users users = usersMapper.selectOne(queryWrapper);
        if (users == null) {
            throw new UsernameNotFoundException("用户不存在");
        }else {
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            return new LoginUser(
                    users
            );
        }

    }
}
