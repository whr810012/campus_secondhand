package com.campus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.common.PageResult;
import com.campus.entity.Category;

import java.util.List;
import java.util.Map;

/**
 * 分类服务接口
 * 
 * @author Campus Team
 */
public interface CategoryService extends IService<Category> {
    
    /**
     * 创建分类
     */
    void createCategory(Category category);
    
    /**
     * 分页查询分类列表
     */
    PageResult<Category> getCategoryPage(Long current, Long size, String keyword, Integer status);
    
    /**
     * 查询所有启用的分类
     */
    List<Category> getEnabledCategories();
    
    /**
     * 根据名称查询分类
     */
    Category findByName(String name);
    
    /**
     * 更新分类信息
     */
    void updateCategory(Long categoryId, Category category);
    
    /**
     * 删除分类
     */
    void deleteCategory(Long categoryId);
    
    /**
     * 启用/禁用分类
     */
    void updateCategoryStatus(Long categoryId, Integer status);
    
    /**
     * 更新分类排序
     */
    void updateCategorySort(Long categoryId, Integer sortOrder);
    
    /**
     * 检查分类名称是否存在
     */
    boolean isCategoryNameExists(String name);
    
    /**
     * 检查分类下是否有商品
     */
    boolean hasCategoryProducts(Long categoryId);
    
    /**
     * 获取分类统计信息
     */
    Map<String, Object> getCategoryStatistics();
    
    /**
     * 获取分类商品数量统计
     */
    List<Map<String, Object>> getCategoryProductCount();
}