package com.plume.plrtime.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.plume.plrtime.pojo.TodoList;
import com.plume.plrtime.service.TodoListService;
import com.plume.plrtime.mapper.TodoListMapper;
import org.springframework.stereotype.Service;

/**
* @author plume
* @description 针对表【todo_list(任务表)】的数据库操作Service实现
* @createDate 2025-06-03 11:15:28
*/
@Service
public class TodoListServiceImpl extends ServiceImpl<TodoListMapper, TodoList>
    implements TodoListService{

}




