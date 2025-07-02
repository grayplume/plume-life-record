package com.plume.plrtime.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.plume.plrtime.pojo.TodoList;
import com.plume.plrtime.service.TodoListService;
import com.plume.plrtime.mapper.TodoListMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
* @author plume
* @description 针对表【todo_list(任务表)】的数据库操作Service实现
* @createDate 2025-06-03 11:15:28
*/
@Service
public class TodoListServiceImpl extends ServiceImpl<TodoListMapper, TodoList>
    implements TodoListService{

    private final TodoListMapper todoMapper;
    public TodoListServiceImpl(TodoListMapper todoMapper) {
        this.todoMapper = todoMapper;
    }

    public List<String> getDistinctTodoTypes() {
        return todoMapper.selectObjs(new LambdaQueryWrapper<TodoList>()
                        .select(TodoList::getTodoType)
                        .groupBy(TodoList::getTodoType))
                .stream()
                .map(obj -> (String) obj)
                .collect(Collectors.toList());
    }

    public List<String> getDistinctTodoStatuses() {
        return todoMapper.selectObjs(new LambdaQueryWrapper<TodoList>()
                        .select(TodoList::getTodoStatus)
                        .groupBy(TodoList::getTodoStatus))
                .stream()
                .map(obj -> (String) obj)
                .collect(Collectors.toList());
    }
}




