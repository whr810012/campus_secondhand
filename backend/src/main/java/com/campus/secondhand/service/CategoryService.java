package com.campus.secondhand.service;

import com.campus.secondhand.entity.Category;

import java.util.List;

/**
 * 分类服务接口
 *
 * @author campus-secondhand
 */
public interface CategoryService {

    /**
     * 获取所有分类列表
     *
     * @return 分类列表
     */
    List<Category> getAllCategories();

    /**
     * 根据ID获取分类
     *
     * @param id 分类ID
     * @return 分类信息
     */
    Category getCategoryById(Long id);

    /**
     * 根据父分类ID获取子分类列表
     *
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    List<Category> getCategoriesByParentId(Long parentId);

    /**
     * 获取启用状态的分类列表
     *
     * @return 启用的分类列表
     */
    List<Category> getEnabledCategories();

}