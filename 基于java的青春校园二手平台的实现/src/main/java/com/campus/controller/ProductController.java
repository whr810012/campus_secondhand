package com.campus.controller;

import com.campus.common.PageResult;
import com.campus.common.Result;
import com.campus.entity.Product;
import com.campus.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 商品控制器
 * 
 * @author Campus Team
 */
@RestController
@RequestMapping("/product")
@CrossOrigin
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    /**
     * 发布商品
     */
    @PostMapping("/publish")
    public Result<Void> publishProduct(@RequestBody Product product) {
        try {
            productService.publishProduct(product);
            return Result.success("商品发布成功，等待审核", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 分页查询商品列表（用户端）
     */
    @GetMapping("/page")
    public Result<PageResult<Product>> getProductPage(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder) {
        try {
            PageResult<Product> result = productService.getProductPageForUser(current, size, keyword, categoryId, sortBy, sortOrder);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 分页查询商品列表（管理端）
     */
    @GetMapping("/admin/page")
    public Result<PageResult<Product>> getProductPageForAdmin(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder) {
        try {
            PageResult<Product> result = productService.getProductPageForAdmin(current, size, keyword, categoryId, status, sortBy, sortOrder);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 查询用户发布的商品
     */
    @GetMapping("/user/{userId}")
    public Result<PageResult<Product>> getUserProducts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Integer status) {
        try {
            PageResult<Product> result = productService.getUserProducts(userId, current, size, status);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取商品详情
     */
    @GetMapping("/{productId}")
    public Result<Product> getProductDetail(@PathVariable Long productId) {
        try {
            Product product = productService.getProductDetail(productId);
            return Result.success(product);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新商品信息
     */
    @PutMapping("/{productId}")
    public Result<Void> updateProduct(@PathVariable Long productId, @RequestBody Product product) {
        try {
            productService.updateProduct(productId, product);
            return Result.success("商品更新成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除商品
     */
    @DeleteMapping("/{productId}")
    public Result<Void> deleteProduct(@PathVariable Long productId) {
        try {
            productService.deleteProduct(productId);
            return Result.success("商品删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 商品上架/下架
     */
    @PutMapping("/shelf/{productId}")
    public Result<Void> updateProductShelfStatus(@PathVariable Long productId, @RequestBody Map<String, Integer> params) {
        try {
            Integer status = params.get("status");
            productService.updateProductShelfStatus(productId, status);
            return Result.success("操作成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 审核商品
     */
    @PutMapping("/audit/{productId}")
    public Result<Void> auditProduct(@PathVariable Long productId, @RequestBody Map<String, Object> params) {
        try {
            Integer status = (Integer) params.get("status");
            String auditRemark = (String) params.get("auditRemark");
            productService.auditProduct(productId, status, auditRemark);
            return Result.success("审核完成", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 增加商品浏览次数
     */
    @PostMapping("/view/{productId}")
    public Result<Void> increaseViewCount(@PathVariable Long productId) {
        try {
            productService.increaseViewCount(productId);
            return Result.success("操作成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 增加商品点赞次数
     */
    @PostMapping("/like/{productId}")
    public Result<Void> increaseLikeCount(@PathVariable Long productId) {
        try {
            productService.increaseLikeCount(productId);
            return Result.success("点赞成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 标记商品为已售出
     */
    @PutMapping("/sold/{productId}")
    public Result<Void> markAsSold(@PathVariable Long productId) {
        try {
            productService.markAsSold(productId);
            return Result.success("标记成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取热门商品
     */
    @GetMapping("/hot")
    public Result<List<Product>> getHotProducts(@RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<Product> products = productService.getHotProducts(limit);
            return Result.success(products);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取最新商品
     */
    @GetMapping("/latest")
    public Result<List<Product>> getLatestProducts(@RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<Product> products = productService.getLatestProducts(limit);
            return Result.success(products);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取推荐商品
     */
    @GetMapping("/recommend")
    public Result<List<Product>> getRecommendProducts(@RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<Product> products = productService.getRecommendProducts(limit);
            return Result.success(products);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 搜索商品
     */
    @GetMapping("/search")
    public Result<PageResult<Product>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder) {
        try {
            PageResult<Product> result = productService.searchProducts(keyword, current, size, categoryId, sortBy, sortOrder);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取商品统计信息
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getProductStatistics() {
        try {
            Map<String, Object> statistics = productService.getProductStatistics();
            return Result.success(statistics);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 上传商品图片
     */
    @PostMapping("/upload-image")
    public Result<String> uploadProductImage(@RequestParam("file") MultipartFile file) {
        try {
            // 这里需要实现文件上传逻辑，返回图片URL
            // String imageUrl = fileService.uploadImage(file);
            String imageUrl = "/uploads/products/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
            return Result.success("上传成功", imageUrl);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}