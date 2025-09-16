package com.campus.secondhand.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.common.Result;
import com.campus.secondhand.entity.Product;
import com.campus.secondhand.service.ProductManageService;
import com.campus.secondhand.service.ProductManageService.ProductDetail;
import com.campus.secondhand.service.ProductManageService.ProductPopularity;
import com.campus.secondhand.service.ProductManageService.ProductStats;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品管理控制器
 *
 * @author campus-secondhand
 */
@RestController
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class ProductManageController {

    private final ProductManageService productManageService;

    /**
     * 分页查询商品列表
     */
    @GetMapping
    public Result<Page<Product>> getProductList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String auditStatus,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long userId) {
        Page<Product> products = productManageService.getProductList(
            page, size, keyword, status, auditStatus, categoryId, userId);
        return Result.success(products);
    }

    /**
     * 获取商品详细信息
     */
    @GetMapping("/{productId}")
    public Result<ProductDetail> getProductDetail(@PathVariable Long productId) {
        ProductDetail productDetail = productManageService.getProductDetail(productId);
        if (productDetail != null) {
            return Result.success(productDetail);
        } else {
            return Result.error("商品不存在");
        }
    }

    /**
     * 下架违规商品
     */
    @PostMapping("/{productId}/remove")
    public Result<Void> removeProduct(
            @PathVariable Long productId,
            @RequestBody RemoveProductRequest request) {
        try {
            boolean success = productManageService.removeProduct(
                productId, 
                request.getAdminId(), 
                request.getReason()
            );
            if (success) {
                return Result.success();
            } else {
                return Result.error("下架商品失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 批量下架违规商品
     */
    @PostMapping("/batch-remove")
    public Result<Integer> batchRemoveProducts(@RequestBody BatchRemoveProductsRequest request) {
        try {
            int successCount = productManageService.batchRemoveProducts(
                request.getProductIds(),
                request.getAdminId(),
                request.getReason()
            );
            return Result.success(successCount);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 恢复商品
     */
    @PostMapping("/{productId}/restore")
    public Result<Void> restoreProduct(
            @PathVariable Long productId,
            @RequestBody RestoreProductRequest request) {
        try {
            boolean success = productManageService.restoreProduct(productId, request.getAdminId());
            if (success) {
                return Result.success();
            } else {
                return Result.error("恢复商品失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取商品统计信息
     */
    @GetMapping("/stats")
    public Result<ProductStats> getProductStats() {
        ProductStats stats = productManageService.getProductStats();
        return Result.success(stats);
    }

    /**
     * 分页查询违规商品
     */
    @GetMapping("/violations")
    public Result<Page<Product>> getViolationProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        Page<Product> products = productManageService.getViolationProducts(page, size, keyword);
        return Result.success(products);
    }

    /**
     * 分页查询热门商品
     */
    @GetMapping("/popular")
    public Result<Page<ProductPopularity>> getPopularProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "7") int days) {
        Page<ProductPopularity> products = productManageService.getPopularProducts(page, size, days);
        return Result.success(products);
    }

    /**
     * 强制删除商品
     */
    @DeleteMapping("/{productId}/force")
    public Result<Void> forceDeleteProduct(
            @PathVariable Long productId,
            @RequestBody ForceDeleteProductRequest request) {
        try {
            boolean success = productManageService.forceDeleteProduct(
                productId, 
                request.getAdminId(), 
                request.getReason()
            );
            if (success) {
                return Result.success();
            } else {
                return Result.error("删除商品失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // 内部类定义请求参数
    public static class RemoveProductRequest {
        private Long adminId;
        private String reason;

        public Long getAdminId() { return adminId; }
        public void setAdminId(Long adminId) { this.adminId = adminId; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }

    public static class BatchRemoveProductsRequest {
        private List<Long> productIds;
        private Long adminId;
        private String reason;

        public List<Long> getProductIds() { return productIds; }
        public void setProductIds(List<Long> productIds) { this.productIds = productIds; }
        public Long getAdminId() { return adminId; }
        public void setAdminId(Long adminId) { this.adminId = adminId; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }

    public static class RestoreProductRequest {
        private Long adminId;

        public Long getAdminId() { return adminId; }
        public void setAdminId(Long adminId) { this.adminId = adminId; }
    }

    public static class ForceDeleteProductRequest {
        private Long adminId;
        private String reason;

        public Long getAdminId() { return adminId; }
        public void setAdminId(Long adminId) { this.adminId = adminId; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
}