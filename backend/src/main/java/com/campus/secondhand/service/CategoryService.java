package com.campus.secondhand.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    /**
     * 分页获取分类列表
     *
     * @param page 分页参数
     * @param name 分类名称（可选）
     * @param status 状态（可选）
     * @param parentId 父分类ID（可选）
     * @return 分页结果
     */
    IPage<Category> getCategoriesPage(Page<Category> page, String name, Integer status, Long parentId);

    /**
     * 创建分类
     *
     * @param category 分类信息
     * @return 创建的分类
     */
    Category createCategory(Category category);

    /**
     * 更新分类
     *
     * @param category 分类信息
     * @return 更新的分类
     */
    Category updateCategory(Category category);

    /**
     * 删除分类
     *
     * @param id 分类ID
     * @return 是否删除成功
     */
    boolean deleteCategory(Long id);

    /**
     * 批量删除分类
     *
     * @param ids 分类ID列表
     * @return 是否删除成功
     */
    boolean batchDeleteCategories(List<Long> ids);

    /**
     * 更新分类状态
     *
     * @param id 分类ID
     * @param status 状态
     * @return 是否更新成功
     */
    boolean updateCategoryStatus(Long id, Integer status);

    /**
     * 获取分类树结构
     *
     * @return 分类树
     */
    List<Category> getCategoryTree();

}