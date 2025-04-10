package com.plume.plrtime.controller;


import com.plume.plrtime.common.CustomMd5PasswordEncoder;
import com.plume.plrtime.common.Result;
import com.plume.plrtime.pojo.Users;
import com.plume.plrtime.service.UsersService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UsersController {

    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    /**
     * 查询所有用户
     */
    @GetMapping("/list")
    public Result list(){
        List<Users> list = usersService.list();
        return Result.success(list);
    }

    /**
     * 添加用户
     */
    @PostMapping("/save")
    public Result save(@RequestBody Users user){
        user.setPasswordHash(new CustomMd5PasswordEncoder().encode(user.getPasswordHash()));
        boolean save = usersService.save(user);
        return Result.success(save);
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result login(@RequestBody Users user){
        Users login = usersService.login(user);
        return Result.success(login);
    }

    /**
     * 修改用户
     */
    @PostMapping("/update")
    public Result update(@RequestBody Users user){
        boolean update = usersService.updateById(user);
        return Result.success(update);
    }

    /**
     * 删除用户
     */
    @PostMapping("/delete/{ids}")
    public Result delete(@PathVariable("ids") List<Long> ids){
        boolean removed = usersService.removeBatchByIds(ids);
        return Result.success(removed);
    }
}
