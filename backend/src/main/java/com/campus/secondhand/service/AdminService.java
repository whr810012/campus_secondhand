package com.campus.secondhand.service;

/**
 * 管理员服务接口
 *
 * @author campus-secondhand
 */
public interface AdminService {

    /**
     * 获取仪表盘统计数据
     *
     * @return 仪表盘数据
     */
    DashboardStats getDashboardStats();

    /**
     * 获取用户统计数据
     *
     * @param days 统计天数
     * @return 用户统计
     */
    UserStats getUserStats(int days);

    /**
     * 获取商品统计数据
     *
     * @param days 统计天数
     * @return 商品统计
     */
    ProductStats getProductStats(int days);

    /**
     * 获取订单统计数据
     *
     * @param days 统计天数
     * @return 订单统计
     */
    OrderStats getOrderStats(int days);

    /**
     * 获取交易统计数据
     *
     * @param days 统计天数
     * @return 交易统计
     */
    TransactionStats getTransactionStats(int days);

    /**
     * 仪表盘统计数据
     */
    class DashboardStats {
        private Long totalUsers;
        private Long totalProducts;
        private Long totalOrders;
        private Long totalReviews;
        private Long activeUsers;
        private Long onSaleProducts;
        private Long pendingOrders;
        private Long todayNewUsers;
        private Long todayNewProducts;
        private Long todayNewOrders;

        // Getters and Setters
        public Long getTotalUsers() { return totalUsers; }
        public void setTotalUsers(Long totalUsers) { this.totalUsers = totalUsers; }
        public Long getTotalProducts() { return totalProducts; }
        public void setTotalProducts(Long totalProducts) { this.totalProducts = totalProducts; }
        public Long getTotalOrders() { return totalOrders; }
        public void setTotalOrders(Long totalOrders) { this.totalOrders = totalOrders; }
        public Long getTotalReviews() { return totalReviews; }
        public void setTotalReviews(Long totalReviews) { this.totalReviews = totalReviews; }
        public Long getActiveUsers() { return activeUsers; }
        public void setActiveUsers(Long activeUsers) { this.activeUsers = activeUsers; }
        public Long getOnSaleProducts() { return onSaleProducts; }
        public void setOnSaleProducts(Long onSaleProducts) { this.onSaleProducts = onSaleProducts; }
        public Long getPendingOrders() { return pendingOrders; }
        public void setPendingOrders(Long pendingOrders) { this.pendingOrders = pendingOrders; }
        public Long getTodayNewUsers() { return todayNewUsers; }
        public void setTodayNewUsers(Long todayNewUsers) { this.todayNewUsers = todayNewUsers; }
        public Long getTodayNewProducts() { return todayNewProducts; }
        public void setTodayNewProducts(Long todayNewProducts) { this.todayNewProducts = todayNewProducts; }
        public Long getTodayNewOrders() { return todayNewOrders; }
        public void setTodayNewOrders(Long todayNewOrders) { this.todayNewOrders = todayNewOrders; }
    }

    /**
     * 用户统计数据
     */
    class UserStats {
        private Long totalCount;
        private Long newCount;
        private Long activeCount;
        private Long verifiedCount;
        private Long bannedCount;

        // Getters and Setters
        public Long getTotalCount() { return totalCount; }
        public void setTotalCount(Long totalCount) { this.totalCount = totalCount; }
        public Long getNewCount() { return newCount; }
        public void setNewCount(Long newCount) { this.newCount = newCount; }
        public Long getActiveCount() { return activeCount; }
        public void setActiveCount(Long activeCount) { this.activeCount = activeCount; }
        public Long getVerifiedCount() { return verifiedCount; }
        public void setVerifiedCount(Long verifiedCount) { this.verifiedCount = verifiedCount; }
        public Long getBannedCount() { return bannedCount; }
        public void setBannedCount(Long bannedCount) { this.bannedCount = bannedCount; }
    }

    /**
     * 商品统计数据
     */
    class ProductStats {
        private Long totalCount;
        private Long newCount;
        private Long onSaleCount;
        private Long soldCount;
        private Long pendingReviewCount;
        private Long rejectedCount;

        // Getters and Setters
        public Long getTotalCount() { return totalCount; }
        public void setTotalCount(Long totalCount) { this.totalCount = totalCount; }
        public Long getNewCount() { return newCount; }
        public void setNewCount(Long newCount) { this.newCount = newCount; }
        public Long getOnSaleCount() { return onSaleCount; }
        public void setOnSaleCount(Long onSaleCount) { this.onSaleCount = onSaleCount; }
        public Long getSoldCount() { return soldCount; }
        public void setSoldCount(Long soldCount) { this.soldCount = soldCount; }
        public Long getPendingReviewCount() { return pendingReviewCount; }
        public void setPendingReviewCount(Long pendingReviewCount) { this.pendingReviewCount = pendingReviewCount; }
        public Long getRejectedCount() { return rejectedCount; }
        public void setRejectedCount(Long rejectedCount) { this.rejectedCount = rejectedCount; }
    }

    /**
     * 订单统计数据
     */
    class OrderStats {
        private Long totalCount;
        private Long newCount;
        private Long pendingCount;
        private Long completedCount;
        private Long cancelledCount;
        private Double totalAmount;

        // Getters and Setters
        public Long getTotalCount() { return totalCount; }
        public void setTotalCount(Long totalCount) { this.totalCount = totalCount; }
        public Long getNewCount() { return newCount; }
        public void setNewCount(Long newCount) { this.newCount = newCount; }
        public Long getPendingCount() { return pendingCount; }
        public void setPendingCount(Long pendingCount) { this.pendingCount = pendingCount; }
        public Long getCompletedCount() { return completedCount; }
        public void setCompletedCount(Long completedCount) { this.completedCount = completedCount; }
        public Long getCancelledCount() { return cancelledCount; }
        public void setCancelledCount(Long cancelledCount) { this.cancelledCount = cancelledCount; }
        public Double getTotalAmount() { return totalAmount; }
        public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    }

    /**
     * 交易统计数据
     */
    class TransactionStats {
        private Double totalAmount;
        private Double newAmount;
        private Long transactionCount;
        private Double averageAmount;

        // Getters and Setters
        public Double getTotalAmount() { return totalAmount; }
        public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
        public Double getNewAmount() { return newAmount; }
        public void setNewAmount(Double newAmount) { this.newAmount = newAmount; }
        public Long getTransactionCount() { return transactionCount; }
        public void setTransactionCount(Long transactionCount) { this.transactionCount = transactionCount; }
        public Double getAverageAmount() { return averageAmount; }
        public void setAverageAmount(Double averageAmount) { this.averageAmount = averageAmount; }
    }
}