package com.plume.plrtime.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.plume.plrtime.pojo.Categories;
import com.plume.plrtime.service.CategoriesService;
import com.plume.plrtime.mapper.CategoriesMapper;
import org.springframework.stereotype.Service;

/**
* @author plume
* @description 针对表【activity_categories(活动分类表)】的数据库操作Service实现
* @createDate 2025-05-28 17:01:27
*/
@Service
public class CategoriesServiceImpl extends ServiceImpl<CategoriesMapper, Categories>
    implements CategoriesService{

}




