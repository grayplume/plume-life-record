package com.plume.plrtime.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.plume.plrtime.common.Result;
import com.plume.plrtime.pojo.TodoList;
import com.plume.plrtime.pojo.vo.LoginUser;
import com.plume.plrtime.service.TodoListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TodoListController
 * 处理与任务（todo_list）相关的请求接口
 */
@RestController
@RequestMapping("/todo")
public class TodoListController {

    @Autowired
    private TodoListService todoService;

    /**
     * 获取当前用户的所有任务列表（可选根据parentId筛选子任务）
     * @param parentId 父任务ID，默认为0表示根任务
     * @return 任务列表
     */
    @GetMapping("/list")
    public Result getTodoList(@RequestParam(required = false, defaultValue = "0") Integer parentId) {
        // 获取当前登录用户信息
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        QueryWrapper<TodoList> query = new QueryWrapper<>();
        query.eq("user_id", loginUser.getUser().getUserId()).eq("parent_id", parentId);

        // 根据修改时间降序排序
        query.orderByDesc("updated_time");
        return Result.success(todoService.list(query));
    }

    /**
     * 根据任务ID获取任务详情
     * @param todoId 任务ID
     * @return 任务对象
     */
    @GetMapping("/{todoId}")
    public Result getTodoById(@PathVariable Integer todoId) {
        return Result.success(todoService.getById(todoId));
    }

    /**
     * 创建一个新任务
     * @param todo 任务实体，从请求体接收JSON
     * @return 新建任务ID
     */
    @PostMapping("/save")
    public Result addTodo(@RequestBody TodoList todo) {
        // 获取当前登录用户信息
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        todo.setUserId(loginUser.getUser().getUserId());
        return Result.success(todoService.save(todo));
    }

    /**
     * 更新任务信息
     * @param todo 更新后的任务实体
     * @return 更新是否成功
     */
    @PostMapping("/update")
    public Result updateTodo(@RequestBody TodoList todo) {
        todo.setUpdatedTime(null);
        // 如果是子任务,需要更新父任务进度
        if (todo.getParentId() != 0) {
            TodoList parentTodo = todoService.getById(todo.getParentId());
            parentTodo.setTodoStatus("进行中");
            parentTodo.setTodoSchedule(todo.getTodoName());
            parentTodo.setUpdatedTime(null);
            todoService.updateById(parentTodo);
        }
        return Result.success(todoService.updateById(todo));
    }

    /**
     * 删除任务（逻辑删除或物理删除根据业务实现）
     * @param todoId 任务ID
     * @return 删除是否成功
     */
    @DeleteMapping("/delete/{todoId}")
    public Result deleteTodo(@PathVariable int todoId) {
        // todo:需要删除子任务
        // 查询父ID为todoId的任务
        QueryWrapper<TodoList> query = new QueryWrapper<>();
        query.eq("parent_id", todoId);
        List<TodoList> parentTodos = todoService.list(query);
        // 删除子任务
        for (TodoList parentTodo : parentTodos) {
            todoService.removeById(parentTodo.getTodoId());
        }
        return Result.success(todoService.removeById(todoId));
    }
}