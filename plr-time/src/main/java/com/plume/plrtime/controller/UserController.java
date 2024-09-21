package com.plume.plrtime.controller;

import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.plume.plrtime.common.Constants;
import com.plume.plrtime.common.Result;
import com.plume.plrtime.pojo.User;
import com.plume.plrtime.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping("/list")
    public Result list() {
        List<User> list = userService.list();
        return Result.success(list);
    }

    @PostMapping("/save")
    public Result save(@RequestBody User user) {
        boolean save = userService.save(user);
        return save?Result.success("保存成功"):Result.error(Constants.CODE_500,"保存失败");
    };

    @PostMapping("/update")
    public Result update(@RequestBody User user) {
        UpdateChainWrapper<User> update = userService.update();
        boolean updated = update.update(user);
        return updated?Result.success("保存成功"):Result.error(Constants.CODE_500,"修改失败");
    }


}
