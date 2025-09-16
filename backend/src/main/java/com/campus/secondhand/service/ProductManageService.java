package com.campus.secondhand.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.entity.Product;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品管理服务接口
 *
 * @author campus-secondhand
 */
public interface ProductManageService {

    /**
     * 分页查询商品列表
     *
     * @param page 页码
     * @param size 每页大小
     * @param keyword 搜索关键词（商品名称、描述）
     * @param status 商品状态（ACTIVE/SOLD/REMOVED）
     * @param auditStatus 审核状态（PENDING/APPROVED/REJECTED）
     * @param categoryId 分类ID
     * @param userId 用户ID
     * @return 商品分页数据
     */
    Page<Product> getProductList(int page, int size, String keyword, String status, 
                                String auditStatus, Long categoryId, Long userId);

    /**
     * 下架违规商品
     *
     * @param productId 商品ID
     * @param adminId 管理员ID
     * @param reason 下架原因
     * @return 是否成功
     */
    boolean removeProduct(Long productId, Long adminId, String reason);

    /**
     * 批量下架违规商品
     *
     * @param productIds 商品ID列表
     * @param adminId 管理员ID
     * @param reason 下架原因
     * @return 成功下架的商品数量
     */
    int batchRemoveProducts(List<Long> productIds, Long adminId, String reason);

    /**
     * 恢复商品
     *
     * @param productId 商品ID
     * @param adminId 管理员ID
     * @return 是否成功
     */
    boolean restoreProduct(Long productId, Long adminId);

    /**
     * 获取商品详细信息
     *
     * @param productId 商品ID
     * @return 商品详细信息
     */
    ProductDetail getProductDetail(Long productId);

    /**
     * 获取商品统计信息
     *
     * @return 商品统计数据
     */
    ProductStats getProductStats();

    /**
     * 分页查询违规商品
     *
     * @param page 页码
     * @param size 每页大小
     * @param keyword 搜索关键词
     * @return 违规商品分页数据
     */
    Page<Product> getViolationProducts(int page, int size, String keyword);

    /**
     * 分页查询热门商品
     *
     * @param page 页码
     * @param size 每页大小
     * @param days 统计天数
     * @return 热门商品分页数据
     */
    Page<ProductPopularity> getPopularProducts(int page, int size, int days);

    /**
     * 强制删除商品（物理删除）
     *
     * @param productId 商品ID
     * @param adminId 管理员ID
     * @param reason 删除原因
     * @return 是否成功
     */
    boolean forceDeleteProduct(Long productId, Long adminId, String reason);

    /**
     * 商品详细信息
     */
    class ProductDetail {
        private Product product;
        private String userName;           // 发布者用户名
        private String userPhone;          // 发布者手机号
        private int viewCount;             // 浏览次数
        private int favoriteCount;         // 收藏次数
        private int inquiryCount;          // 咨询次数
        private LocalDateTime lastViewTime; // 最后浏览时间
        private String removeReason;       // 下架原因
        private LocalDateTime removedAt;   // 下架时间

        // Getters and Setters
        public Product getProduct() { return product; }
        public void setProduct(Product product) { this.product = product; }
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
        public String getUserPhone() { return userPhone; }
        public void setUserPhone(String userPhone) { this.userPhone = userPhone; }
        public int getViewCount() { return viewCount; }
        public void setViewCount(int viewCount) { this.viewCount = viewCount; }
        public int getFavoriteCount() { return favoriteCount; }
        public void setFavoriteCount(int favoriteCount) { this.favoriteCount = favoriteCount; }
        public int getInquiryCount() { return inquiryCount; }
        public void setInquiryCount(int inquiryCount) { this.inquiryCount = inquiryCount; }
        public LocalDateTime getLastViewTime() { return lastViewTime; }
        public void setLastViewTime(LocalDateTime lastViewTime) { this.lastViewTime = lastViewTime; }
        public String getRemoveReason() { return removeReason; }
        public void setRemoveReason(String removeReason) { this.removeReason = removeReason; }
        public LocalDateTime getRemovedAt() { return removedAt; }
        public void setRemovedAt(LocalDateTime removedAt) { this.removedAt = removedAt; }
    }

    /**
     * 商品统计信息
     */
    class ProductStats {
        private long totalProducts;        // 总商品数
        private long activeProducts;       // 在售商品数
        private long soldProducts;         // 已售商品数
        private long removedProducts;      // 已下架商品数
        private long pendingAuditProducts; // 待审核商品数
        private long rejectedProducts;     // 审核拒绝商品数
        private long todayNewProducts;     // 今日新增商品数
        private long weekNewProducts;      // 本周新增商品数
        private long monthNewProducts;     // 本月新增商品数
        private long violationProducts;    // 违规商品数

        // Getters and Setters
        public long getTotalProducts() { return totalProducts; }
        public void setTotalProducts(long totalProducts) { this.totalProducts = totalProducts; }
        public long getActiveProducts() { return activeProducts; }
        public void setActiveProducts(long activeProducts) { this.activeProducts = activeProducts; }
        public long getSoldProducts() { return soldProducts; }
        public void setSoldProducts(long soldProducts) { this.soldProducts = soldProducts; }
        public long getRemovedProducts() { return removedProducts; }
        public void setRemovedProducts(long removedProducts) { this.removedProducts = removedProducts; }
        public long getPendingAuditProducts() { return pendingAuditProducts; }
        public void setPendingAuditProducts(long pendingAuditProducts) { this.pendingAuditProducts = pendingAuditProducts; }
        public long getRejectedProducts() { return rejectedProducts; }
        public void setRejectedProducts(long rejectedProducts) { this.rejectedProducts = rejectedProducts; }
        public long getTodayNewProducts() { return todayNewProducts; }
        public void setTodayNewProducts(long todayNewProducts) { this.todayNewProducts = todayNewProducts; }
        public long getWeekNewProducts() { return weekNewProducts; }
        public void setWeekNewProducts(long weekNewProducts) { this.weekNewProducts = weekNewProducts; }
        public long getMonthNewProducts() { return monthNewProducts; }
        public void setMonthNewProducts(long monthNewProducts) { this.monthNewProducts = monthNewProducts; }
        public long getViolationProducts() { return violationProducts; }
        public void setViolationProducts(long violationProducts) { this.violationProducts = violationProducts; }
    }

    /**
     * 商品热度信息
     */
    class ProductPopularity {
        private Product product;
        private int viewCount;      // 浏览次数
        private int favoriteCount;  // 收藏次数
        private int inquiryCount;   // 咨询次数
        private double popularityScore; // 热度评分

        // Getters and Setters
        public Product getProduct() { return product; }
        public void setProduct(Product product) { this.product = product; }
        public int getViewCount() { return viewCount; }
        public void setViewCount(int viewCount) { this.viewCount = viewCount; }
        public int getFavoriteCount() { return favoriteCount; }
        public void setFavoriteCount(int favoriteCount) { this.favoriteCount = favoriteCount; }
        public int getInquiryCount() { return inquiryCount; }
        public void setInquiryCount(int inquiryCount) { this.inquiryCount = inquiryCount; }
        public double getPopularityScore() { return popularityScore; }
        public void setPopularityScore(double popularityScore) { this.popularityScore = popularityScore; }
    }
}