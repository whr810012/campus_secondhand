package com.campus.secondhand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.entity.Category;
import com.campus.secondhand.mapper.CategoryMapper;
import com.campus.secondhand.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Override
    public IPage<Category> getCategoriesPage(Page<Category> page, String name, Integer status, Long parentId) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getDeleted, 0);
        
        if (StringUtils.hasText(name)) {
            queryWrapper.like(Category::getName, name);
        }
        
        if (status != null) {
            queryWrapper.eq(Category::getStatus, status);
        }
        
        if (parentId != null) {
            queryWrapper.eq(Category::getParentId, parentId);
        }
        
        queryWrapper.orderByAsc(Category::getSort, Category::getId);
        
        return categoryMapper.selectPage(page, queryWrapper);
    }

    @Override
    public Category createCategory(Category category) {
        // 设置默认值
        if (category.getParentId() == null) {
            category.setParentId(0L);
        }
        if (category.getSort() == null) {
            category.setSort(0);
        }
        if (category.getStatus() == null) {
            category.setStatus(1);
        }
        
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        category.setDeleted(0);
        
        categoryMapper.insert(category);
        return category;
    }

    @Override
    public Category updateCategory(Category category) {
        Category existing = categoryMapper.selectById(category.getId());
        if (existing == null || existing.getDeleted() == 1) {
            return null;
        }
        
        category.setUpdatedAt(LocalDateTime.now());
        categoryMapper.updateById(category);
        return categoryMapper.selectById(category.getId());
    }

    @Override
    @Transactional
    public boolean deleteCategory(Long id) {
        log.info("开始删除分类，ID: {}", id);
        
        try {
            // 使用LambdaQueryWrapper绕过逻辑删除过滤，查询所有记录
            Category category = categoryMapper.selectOne(
                new LambdaQueryWrapper<Category>()
                    .eq(Category::getId, id)
                    .last("AND (deleted = 0 OR deleted = 1)")
            );
            
            if (category == null) {
                log.warn("分类不存在，ID: {}", id);
                return false;
            }
            
            if (category.getDeleted() == 1) {
                log.warn("分类已被删除，ID: {}", id);
                return false;
            }
            
            log.info("找到分类: {}, 当前deleted状态: {}", category.getName(), category.getDeleted());
            
            // 检查是否有子分类
            LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Category::getParentId, id)
                       .eq(Category::getDeleted, 0);
            Long childCount = categoryMapper.selectCount(queryWrapper);
            
            if (childCount > 0) {
                log.warn("分类下存在{}个子分类，无法删除，ID: {}", childCount, id);
                throw new RuntimeException("该分类下存在子分类，无法删除");
            }
            
            // 逻辑删除 - 使用LambdaUpdateWrapper确保deleted字段被更新
            LocalDateTime now = LocalDateTime.now();
            log.info("准备更新分类，设置deleted=1，ID: {}, 更新时间: {}", id, now);
            
            LambdaUpdateWrapper<Category> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Category::getId, id)
                        .eq(Category::getDeleted, 0)  // 确保只更新未删除的记录
                        .set(Category::getDeleted, 1)
                        .set(Category::getUpdatedAt, now);
            
            int updateResult = categoryMapper.update(null, updateWrapper);
            log.info("MyBatis-Plus update执行结果: {}, ID: {}", updateResult, id);
            
            // 验证更新结果 - 直接查询数据库，不受逻辑删除影响
            if (updateResult > 0) {
                log.info("分类逻辑删除成功，ID: {}, 影响行数: {}", id, updateResult);
            } else {
                log.error("分类逻辑删除失败，ID: {}, 影响行数: {}", id, updateResult);
                throw new RuntimeException("分类删除失败，未找到可删除的记录");
            }
            
            log.info("分类删除成功，ID: {}", id);
            return updateResult > 0;
            
        } catch (Exception e) {
            log.error("删除分类时发生异常，ID: {}, 异常信息: {}", id, e.getMessage(), e);
            throw e; // 重新抛出异常，让事务回滚
        }
    }

    @Override
    public boolean batchDeleteCategories(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        
        for (Long id : ids) {
            deleteCategory(id);
        }
        
        return true;
    }

    @Override
    public boolean updateCategoryStatus(Long id, Integer status) {
        Category category = categoryMapper.selectById(id);
        if (category == null || category.getDeleted() == 1) {
            return false;
        }
        
        category.setStatus(status);
        category.setUpdatedAt(LocalDateTime.now());
        return categoryMapper.updateById(category) > 0;
    }

    @Override
    public List<Category> getCategoryTree() {
        List<Category> allCategories = getAllCategories();
        
        // 构建树形结构
        Map<Long, List<Category>> parentChildMap = allCategories.stream()
                .collect(Collectors.groupingBy(Category::getParentId));
        
        // 获取根节点
        List<Category> rootCategories = parentChildMap.getOrDefault(0L, new ArrayList<>());
        
        // 递归构建树
        buildCategoryTree(rootCategories, parentChildMap);
        
        return rootCategories;
    }
    
    private void buildCategoryTree(List<Category> categories, Map<Long, List<Category>> parentChildMap) {
        for (Category category : categories) {
            List<Category> children = parentChildMap.getOrDefault(category.getId(), new ArrayList<>());
            if (!children.isEmpty()) {
                buildCategoryTree(children, parentChildMap);
            }
        }
    }

}