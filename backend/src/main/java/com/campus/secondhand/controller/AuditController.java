package com.campus.secondhand.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.common.Result;
import com.campus.secondhand.entity.Product;
import com.campus.secondhand.entity.Review;
import com.campus.secondhand.service.AuditService;
import com.campus.secondhand.service.AuditService.AuditRecord;
import com.campus.secondhand.service.AuditService.AuditStats;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 审核控制器
 *
 * @author campus-secondhand
 */
@RestController
@RequestMapping("/admin/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    /**
     * 分页查询待审核商品
     */
    @GetMapping("/products/pending")
    public Result<Page<Product>> getPendingProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        Page<Product> products = auditService.getPendingProducts(page, size, keyword);
        return Result.success(products);
    }

    /**
     * 审核商品
     */
    @PostMapping("/products/{productId}")
    public Result<Void> auditProduct(
            @PathVariable Long productId,
            @RequestBody AuditRequest request) {
        try {
            boolean success = auditService.auditProduct(
                productId, 
                request.getAdminId(), 
                request.getStatus(), 
                request.getReason()
            );
            if (success) {
                return Result.success();
            } else {
                return Result.error("审核失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 批量审核商品
     */
    @PostMapping("/products/batch")
    public Result<Integer> batchAuditProducts(@RequestBody BatchAuditRequest request) {
        try {
            int successCount = auditService.batchAuditProducts(
                request.getProductIds(),
                request.getAdminId(),
                request.getStatus(),
                request.getReason()
            );
            return Result.success(successCount);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 分页查询待审核评价
     */
    @GetMapping("/reviews/pending")
    public Result<Page<Review>> getPendingReviews(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        Page<Review> reviews = auditService.getPendingReviews(page, size, keyword);
        return Result.success(reviews);
    }

    /**
     * 审核评价
     */
    @PostMapping("/reviews/{reviewId}")
    public Result<Void> auditReview(
            @PathVariable Long reviewId,
            @RequestBody AuditRequest request) {
        try {
            boolean success = auditService.auditReview(
                reviewId,
                request.getAdminId(),
                request.getStatus(),
                request.getReason()
            );
            if (success) {
                return Result.success();
            } else {
                return Result.error("审核失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 批量审核评价
     */
    @PostMapping("/reviews/batch")
    public Result<Integer> batchAuditReviews(@RequestBody BatchAuditReviewRequest request) {
        try {
            int successCount = auditService.batchAuditReviews(
                request.getReviewIds(),
                request.getAdminId(),
                request.getStatus(),
                request.getReason()
            );
            return Result.success(successCount);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取审核统计
     */
    @GetMapping("/stats")
    public Result<AuditStats> getAuditStats() {
        AuditStats stats = auditService.getAuditStats();
        return Result.success(stats);
    }

    /**
     * 分页查询审核历史
     */
    @GetMapping("/history")
    public Result<Page<AuditRecord>> getAuditHistory(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long adminId) {
        Page<AuditRecord> history = auditService.getAuditHistory(page, size, type, adminId);
        return Result.success(history);
    }

    // 内部类定义请求参数
    public static class AuditRequest {
        private Long adminId;
        private String status;
        private String reason;

        public Long getAdminId() { return adminId; }
        public void setAdminId(Long adminId) { this.adminId = adminId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }

    public static class BatchAuditRequest {
        private List<Long> productIds;
        private Long adminId;
        private String status;
        private String reason;

        public List<Long> getProductIds() { return productIds; }
        public void setProductIds(List<Long> productIds) { this.productIds = productIds; }
        public Long getAdminId() { return adminId; }
        public void setAdminId(Long adminId) { this.adminId = adminId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }

    public static class BatchAuditReviewRequest {
        private List<Long> reviewIds;
        private Long adminId;
        private String status;
        private String reason;

        public List<Long> getReviewIds() { return reviewIds; }
        public void setReviewIds(List<Long> reviewIds) { this.reviewIds = reviewIds; }
        public Long getAdminId() { return adminId; }
        public void setAdminId(Long adminId) { this.adminId = adminId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
}