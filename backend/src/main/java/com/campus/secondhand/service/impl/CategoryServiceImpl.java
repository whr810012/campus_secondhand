package com.campus.secondhand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.secondhand.entity.Category;
import com.campus.secondhand.mapper.CategoryMapper;
import com.campus.secondhand.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 分类服务实现类
 *
 * @author campus-secondhand
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    @Override
    public List<Category> getAllCategories() {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getDeleted, 0)
                   .orderByAsc(Category::getSort, Category::getId);
        return categoryMapper.selectList(queryWrapper);
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryMapper.selectById(id);
    }

    @Override
    public List<Category> getCategoriesByParentId(Long parentId) {
        return categoryMapper.selectByParentId(parentId);
    }

    @Override
    public List<Category> getEnabledCategories() {
        return categoryMapper.selectEnabledCategories();
    }

}