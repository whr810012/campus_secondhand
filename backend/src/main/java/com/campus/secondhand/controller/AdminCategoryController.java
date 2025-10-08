package com.campus.secondhand.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.common.Result;
import com.campus.secondhand.entity.Category;
import com.campus.secondhand.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员分类控制器
 *
 * @author campus-secondhand
 */
@Slf4j
@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    /**
     * 分页获取分类列表
     */
    @GetMapping
    public Result<IPage<Category>> getCategoriesPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long parentId) {
        
        Page<Category> page = new Page<>(current, size);
        IPage<Category> result = categoryService.getCategoriesPage(page, name, status, parentId);
        return Result.success(result);
    }

    /**
     * 获取所有分类（包含禁用的）
     */
    @GetMapping("/all")
    public Result<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return Result.success(categories);
    }

    /**
     * 根据ID获取分类详情
     */
    @GetMapping("/{id}")
    public Result<Category> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        if (category == null) {
            return Result.error("分类不存在");
        }
        return Result.success(category);
    }

    /**
     * 创建分类
     */
    @PostMapping
    public Result<Category> createCategory(@RequestBody Category category) {
        try {
            Category created = categoryService.createCategory(category);
            return Result.success(created);
        } catch (Exception e) {
            return Result.error("创建分类失败：" + e.getMessage());
        }
    }

    /**
     * 更新分类
     */
    @PutMapping("/{id}")
    public Result<Category> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        try {
            category.setId(id);
            Category updated = categoryService.updateCategory(category);
            if (updated == null) {
                return Result.error("分类不存在");
            }
            return Result.success(updated);
        } catch (Exception e) {
            return Result.error("更新分类失败：" + e.getMessage());
        }
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        log.info("开始删除分类，ID: {}", id);
        try {
            boolean deleted = categoryService.deleteCategory(id);
            log.info("删除分类结果，ID: {}, 删除成功: {}", id, deleted);
            if (!deleted) {
                log.warn("删除分类失败，ID: {}", id);
                return Result.error("分类不存在或删除失败");
            }
            log.info("删除分类成功，ID: {}", id);
            return Result.success();
        } catch (Exception e) {
            log.error("删除分类异常，ID: {}, 异常信息: {}", id, e.getMessage(), e);
            return Result.error("删除分类失败：" + e.getMessage());
        }
    }

    /**
     * 批量删除分类
     */
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteCategories(@RequestBody List<Long> ids) {
        try {
            boolean deleted = categoryService.batchDeleteCategories(ids);
            if (!deleted) {
                return Result.error("批量删除失败");
            }
            return Result.success();
        } catch (Exception e) {
            return Result.error("批量删除失败：" + e.getMessage());
        }
    }

    /**
     * 更新分类状态
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateCategoryStatus(@PathVariable Long id, @RequestParam Integer status) {
        try {
            boolean updated = categoryService.updateCategoryStatus(id, status);
            if (!updated) {
                return Result.error("分类不存在或更新失败");
            }
            return Result.success();
        } catch (Exception e) {
            return Result.error("更新状态失败：" + e.getMessage());
        }
    }

    /**
     * 获取分类树结构
     */
    @GetMapping("/tree")
    public Result<List<Category>> getCategoryTree() {
        List<Category> tree = categoryService.getCategoryTree();
        return Result.success(tree);
    }
}