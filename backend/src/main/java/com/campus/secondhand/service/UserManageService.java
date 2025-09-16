package com.campus.secondhand.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.entity.User;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户管理服务接口
 *
 * @author campus-secondhand
 */
public interface UserManageService {

    /**
     * 分页查询用户列表
     *
     * @param page 页码
     * @param size 每页大小
     * @param keyword 搜索关键词（用户名、手机号、学号）
     * @param status 用户状态（ACTIVE/BANNED）
     * @param verifyStatus 认证状态（PENDING/APPROVED/REJECTED）
     * @return 用户分页数据
     */
    Page<User> getUserList(int page, int size, String keyword, String status, String verifyStatus);

    /**
     * 封禁用户
     *
     * @param userId 用户ID
     * @param adminId 管理员ID
     * @param reason 封禁原因
     * @param banDays 封禁天数，0表示永久封禁
     * @return 是否成功
     */
    boolean banUser(Long userId, Long adminId, String reason, int banDays);

    /**
     * 解封用户
     *
     * @param userId 用户ID
     * @param adminId 管理员ID
     * @return 是否成功
     */
    boolean unbanUser(Long userId, Long adminId);

    /**
     * 批量封禁用户
     *
     * @param userIds 用户ID列表
     * @param adminId 管理员ID
     * @param reason 封禁原因
     * @param banDays 封禁天数
     * @return 成功封禁的用户数量
     */
    int batchBanUsers(List<Long> userIds, Long adminId, String reason, int banDays);

    /**
     * 分页查询待审核的学生身份认证
     *
     * @param page 页码
     * @param size 每页大小
     * @param keyword 搜索关键词
     * @return 待审核用户分页数据
     */
    Page<User> getPendingVerifications(int page, int size, String keyword);

    /**
     * 审核学生身份认证
     *
     * @param userId 用户ID
     * @param adminId 管理员ID
     * @param status 审核状态（APPROVED/REJECTED）
     * @param reason 审核原因（拒绝时必填）
     * @return 是否成功
     */
    boolean verifyStudentIdentity(Long userId, Long adminId, String status, String reason);

    /**
     * 批量审核学生身份认证
     *
     * @param userIds 用户ID列表
     * @param adminId 管理员ID
     * @param status 审核状态
     * @param reason 审核原因
     * @return 成功审核的用户数量
     */
    int batchVerifyStudentIdentity(List<Long> userIds, Long adminId, String status, String reason);

    /**
     * 获取用户详细信息
     *
     * @param userId 用户ID
     * @return 用户详细信息
     */
    UserDetail getUserDetail(Long userId);

    /**
     * 获取用户统计信息
     *
     * @return 用户统计数据
     */
    UserStats getUserStats();

    /**
     * 重置用户密码
     *
     * @param userId 用户ID
     * @param adminId 管理员ID
     * @return 新密码
     */
    String resetUserPassword(Long userId, Long adminId);

    /**
     * 用户详细信息
     */
    class UserDetail {
        private User user;
        private int productCount;      // 发布商品数
        private int orderCount;        // 订单数
        private int reviewCount;       // 评价数
        private Double avgRating;      // 平均评分
        private LocalDateTime lastLoginTime; // 最后登录时间
        private String banReason;      // 封禁原因
        private LocalDateTime banEndTime; // 封禁结束时间

        // Getters and Setters
        public User getUser() { return user; }
        public void setUser(User user) { this.user = user; }
        public int getProductCount() { return productCount; }
        public void setProductCount(int productCount) { this.productCount = productCount; }
        public int getOrderCount() { return orderCount; }
        public void setOrderCount(int orderCount) { this.orderCount = orderCount; }
        public int getReviewCount() { return reviewCount; }
        public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }
        public Double getAvgRating() { return avgRating; }
        public void setAvgRating(Double avgRating) { this.avgRating = avgRating; }
        public LocalDateTime getLastLoginTime() { return lastLoginTime; }
        public void setLastLoginTime(LocalDateTime lastLoginTime) { this.lastLoginTime = lastLoginTime; }
        public String getBanReason() { return banReason; }
        public void setBanReason(String banReason) { this.banReason = banReason; }
        public LocalDateTime getBanEndTime() { return banEndTime; }
        public void setBanEndTime(LocalDateTime banEndTime) { this.banEndTime = banEndTime; }
    }

    /**
     * 用户统计信息
     */
    class UserStats {
        private long totalUsers;           // 总用户数
        private long activeUsers;          // 活跃用户数
        private long bannedUsers;          // 被封禁用户数
        private long pendingVerifications; // 待审核认证数
        private long approvedVerifications; // 已通过认证数
        private long rejectedVerifications; // 已拒绝认证数
        private long todayNewUsers;        // 今日新增用户数
        private long weekNewUsers;         // 本周新增用户数
        private long monthNewUsers;        // 本月新增用户数

        // Getters and Setters
        public long getTotalUsers() { return totalUsers; }
        public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
        public long getActiveUsers() { return activeUsers; }
        public void setActiveUsers(long activeUsers) { this.activeUsers = activeUsers; }
        public long getBannedUsers() { return bannedUsers; }
        public void setBannedUsers(long bannedUsers) { this.bannedUsers = bannedUsers; }
        public long getPendingVerifications() { return pendingVerifications; }
        public void setPendingVerifications(long pendingVerifications) { this.pendingVerifications = pendingVerifications; }
        public long getApprovedVerifications() { return approvedVerifications; }
        public void setApprovedVerifications(long approvedVerifications) { this.approvedVerifications = approvedVerifications; }
        public long getRejectedVerifications() { return rejectedVerifications; }
        public void setRejectedVerifications(long rejectedVerifications) { this.rejectedVerifications = rejectedVerifications; }
        public long getTodayNewUsers() { return todayNewUsers; }
        public void setTodayNewUsers(long todayNewUsers) { this.todayNewUsers = todayNewUsers; }
        public long getWeekNewUsers() { return weekNewUsers; }
        public void setWeekNewUsers(long weekNewUsers) { this.weekNewUsers = weekNewUsers; }
        public long getMonthNewUsers() { return monthNewUsers; }
        public void setMonthNewUsers(long monthNewUsers) { this.monthNewUsers = monthNewUsers; }
    }
}