package com.plume.plrtime.service;

import com.plume.plrtime.pojo.TodoList;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author plume
* @description 针对表【todo_list(任务表)】的数据库操作Service
* @createDate 2025-06-03 11:15:28
*/
public interface TodoListService extends IService<TodoList> {

    List<String> getDistinctTodoTypes();

    List<String> getDistinctTodoStatuses();
}
