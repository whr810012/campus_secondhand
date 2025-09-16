package com.campus.controller;

import com.campus.common.PageResult;
import com.campus.common.Result;
import com.campus.entity.Category;
import com.campus.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 分类控制器
 * 
 * @author Campus Team
 */
@RestController
@RequestMapping("/category")
@CrossOrigin
public class CategoryController {
    
    @Autowired
    private CategoryService categoryService;
    
    /**
     * 创建分类
     */
    @PostMapping("/create")
    public Result<Void> createCategory(@RequestBody Category category) {
        try {
            categoryService.createCategory(category);
            return Result.success("分类创建成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 分页查询分类列表（管理端）
     */
    @GetMapping("/admin/page")
    public Result<PageResult<Category>> getCategoryPageForAdmin(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder) {
        try {
            PageResult<Category> result = categoryService.getCategoryPageForAdmin(current, size, keyword, status, sortBy, sortOrder);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 查询所有启用的分类（用户端）
     */
    @GetMapping("/enabled")
    public Result<List<Category>> getEnabledCategories() {
        try {
            List<Category> categories = categoryService.getEnabledCategories();
            return Result.success(categories);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取分类详情
     */
    @GetMapping("/{categoryId}")
    public Result<Category> getCategoryDetail(@PathVariable Long categoryId) {
        try {
            Category category = categoryService.getById(categoryId);
            return Result.success(category);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新分类信息
     */
    @PutMapping("/{categoryId}")
    public Result<Void> updateCategory(@PathVariable Long categoryId, @RequestBody Category category) {
        try {
            categoryService.updateCategory(categoryId, category);
            return Result.success("分类更新成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除分类
     */
    @DeleteMapping("/{categoryId}")
    public Result<Void> deleteCategory(@PathVariable Long categoryId) {
        try {
            categoryService.deleteCategory(categoryId);
            return Result.success("分类删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 启用/禁用分类
     */
    @PutMapping("/status/{categoryId}")
    public Result<Void> updateCategoryStatus(@PathVariable Long categoryId, @RequestBody Map<String, Integer> params) {
        try {
            Integer status = params.get("status");
            categoryService.updateCategoryStatus(categoryId, status);
            return Result.success("状态更新成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新分类排序
     */
    @PutMapping("/sort/{categoryId}")
    public Result<Void> updateCategorySort(@PathVariable Long categoryId, @RequestBody Map<String, Integer> params) {
        try {
            Integer sortOrder = params.get("sortOrder");
            categoryService.updateCategorySort(categoryId, sortOrder);
            return Result.success("排序更新成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 检查分类名称是否存在
     */
    @GetMapping("/check-name")
    public Result<Boolean> checkCategoryName(@RequestParam String name, @RequestParam(required = false) Long excludeId) {
        try {
            boolean exists = categoryService.isCategoryNameExists(name, excludeId);
            return Result.success(exists);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 检查分类下是否有商品
     */
    @GetMapping("/check-products/{categoryId}")
    public Result<Boolean> checkCategoryHasProducts(@PathVariable Long categoryId) {
        try {
            boolean hasProducts = categoryService.hasCategoryProducts(categoryId);
            return Result.success(hasProducts);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取分类统计信息
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getCategoryStatistics() {
        try {
            Map<String, Object> statistics = categoryService.getCategoryStatistics();
            return Result.success(statistics);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取分类商品数量统计
     */
    @GetMapping("/product-count")
    public Result<List<Map<String, Object>>> getCategoryProductCount() {
        try {
            List<Map<String, Object>> result = categoryService.getCategoryProductCount();
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}