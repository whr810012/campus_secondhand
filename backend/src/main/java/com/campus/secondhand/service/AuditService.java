package com.campus.secondhand.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.entity.Product;
import com.campus.secondhand.entity.Review;

/**
 * 审核服务接口
 *
 * @author campus-secondhand
 */
public interface AuditService {

    /**
     * 分页查询待审核商品
     *
     * @param page 页码
     * @param size 每页大小
     * @param keyword 搜索关键词
     * @return 商品分页数据
     */
    Page<Product> getPendingProducts(int page, int size, String keyword);

    /**
     * 审核商品
     *
     * @param productId 商品ID
     * @param adminId 管理员ID
     * @param status 审核状态（approved: 通过, rejected: 拒绝）
     * @param reason 审核理由（拒绝时必填）
     * @return 是否成功
     */
    boolean auditProduct(Long productId, Long adminId, String status, String reason);

    /**
     * 批量审核商品
     *
     * @param productIds 商品ID列表
     * @param adminId 管理员ID
     * @param status 审核状态
     * @param reason 审核理由
     * @return 成功数量
     */
    int batchAuditProducts(java.util.List<Long> productIds, Long adminId, String status, String reason);

    /**
     * 分页查询待审核评价
     *
     * @param page 页码
     * @param size 每页大小
     * @param keyword 搜索关键词
     * @return 评价分页数据
     */
    Page<Review> getPendingReviews(int page, int size, String keyword);

    /**
     * 审核评价
     *
     * @param reviewId 评价ID
     * @param adminId 管理员ID
     * @param status 审核状态（approved: 通过, rejected: 拒绝）
     * @param reason 审核理由（拒绝时必填）
     * @return 是否成功
     */
    boolean auditReview(Long reviewId, Long adminId, String status, String reason);

    /**
     * 批量审核评价
     *
     * @param reviewIds 评价ID列表
     * @param adminId 管理员ID
     * @param status 审核状态
     * @param reason 审核理由
     * @return 成功数量
     */
    int batchAuditReviews(java.util.List<Long> reviewIds, Long adminId, String status, String reason);

    /**
     * 获取审核统计
     *
     * @return 审核统计数据
     */
    AuditStats getAuditStats();

    /**
     * 分页查询审核历史
     *
     * @param page 页码
     * @param size 每页大小
     * @param type 审核类型（product: 商品, review: 评价）
     * @param adminId 管理员ID（可选）
     * @return 审核历史分页数据
     */
    Page<AuditRecord> getAuditHistory(int page, int size, String type, Long adminId);

    /**
     * 审核统计数据
     */
    class AuditStats {
        private Long pendingProductCount;
        private Long pendingReviewCount;
        private Long todayAuditedProductCount;
        private Long todayAuditedReviewCount;
        private Long totalAuditedProductCount;
        private Long totalAuditedReviewCount;
        private Long approvedProductCount;
        private Long rejectedProductCount;
        private Long approvedReviewCount;
        private Long rejectedReviewCount;

        // Getters and Setters
        public Long getPendingProductCount() { return pendingProductCount; }
        public void setPendingProductCount(Long pendingProductCount) { this.pendingProductCount = pendingProductCount; }
        public Long getPendingReviewCount() { return pendingReviewCount; }
        public void setPendingReviewCount(Long pendingReviewCount) { this.pendingReviewCount = pendingReviewCount; }
        public Long getTodayAuditedProductCount() { return todayAuditedProductCount; }
        public void setTodayAuditedProductCount(Long todayAuditedProductCount) { this.todayAuditedProductCount = todayAuditedProductCount; }
        public Long getTodayAuditedReviewCount() { return todayAuditedReviewCount; }
        public void setTodayAuditedReviewCount(Long todayAuditedReviewCount) { this.todayAuditedReviewCount = todayAuditedReviewCount; }
        public Long getTotalAuditedProductCount() { return totalAuditedProductCount; }
        public void setTotalAuditedProductCount(Long totalAuditedProductCount) { this.totalAuditedProductCount = totalAuditedProductCount; }
        public Long getTotalAuditedReviewCount() { return totalAuditedReviewCount; }
        public void setTotalAuditedReviewCount(Long totalAuditedReviewCount) { this.totalAuditedReviewCount = totalAuditedReviewCount; }
        public Long getApprovedProductCount() { return approvedProductCount; }
        public void setApprovedProductCount(Long approvedProductCount) { this.approvedProductCount = approvedProductCount; }
        public Long getRejectedProductCount() { return rejectedProductCount; }
        public void setRejectedProductCount(Long rejectedProductCount) { this.rejectedProductCount = rejectedProductCount; }
        public Long getApprovedReviewCount() { return approvedReviewCount; }
        public void setApprovedReviewCount(Long approvedReviewCount) { this.approvedReviewCount = approvedReviewCount; }
        public Long getRejectedReviewCount() { return rejectedReviewCount; }
        public void setRejectedReviewCount(Long rejectedReviewCount) { this.rejectedReviewCount = rejectedReviewCount; }
    }

    /**
     * 审核记录
     */
    class AuditRecord {
        private Long id;
        private String type;
        private Long targetId;
        private String targetTitle;
        private Long adminId;
        private String adminName;
        private String status;
        private String reason;
        private String createdAt;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public Long getTargetId() { return targetId; }
        public void setTargetId(Long targetId) { this.targetId = targetId; }
        public String getTargetTitle() { return targetTitle; }
        public void setTargetTitle(String targetTitle) { this.targetTitle = targetTitle; }
        public Long getAdminId() { return adminId; }
        public void setAdminId(Long adminId) { this.adminId = adminId; }
        public String getAdminName() { return adminName; }
        public void setAdminName(String adminName) { this.adminName = adminName; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    }
}