package com.campus.secondhand.mapper;

import com.campus.secondhand.service.AdminService.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 管理员数据访问层
 *
 * @author campus-secondhand
 */
@Mapper
public interface AdminMapper {

    /**
     * 获取仪表盘统计数据
     */
    @Select({
        "SELECT",
        "  (SELECT COUNT(*) FROM users WHERE deleted = 0) as totalUsers,",
        "  (SELECT COUNT(*) FROM products WHERE deleted = 0) as totalProducts,",
        "  (SELECT COUNT(*) FROM orders WHERE deleted = 0) as totalOrders,",
        "  (SELECT COUNT(*) FROM reviews WHERE deleted = 0) as totalReviews,",
        "  (SELECT COUNT(*) FROM users WHERE deleted = 0 AND status = 'active') as activeUsers,",
        "  (SELECT COUNT(*) FROM products WHERE deleted = 0 AND status = 'on_sale') as onSaleProducts,",
        "  (SELECT COUNT(*) FROM orders WHERE deleted = 0 AND status IN ('pending', 'paid', 'shipped')) as pendingOrders,",
        "  (SELECT COUNT(*) FROM users WHERE deleted = 0 AND DATE(created_at) = CURDATE()) as todayNewUsers,",
        "  (SELECT COUNT(*) FROM products WHERE deleted = 0 AND DATE(created_at) = CURDATE()) as todayNewProducts,",
        "  (SELECT COUNT(*) FROM orders WHERE deleted = 0 AND DATE(created_at) = CURDATE()) as todayNewOrders"
    })
    DashboardStats selectDashboardStats();

    /**
     * 获取用户统计数据
     */
    @Select({
        "SELECT",
        "  COUNT(*) as totalCount,",
        "  SUM(CASE WHEN created_at &gt;= DATE_SUB(NOW(), INTERVAL #{days} DAY) THEN 1 ELSE 0 END) as newCount,",
        "  SUM(CASE WHEN status = 'active' THEN 1 ELSE 0 END) as activeCount,",
        "  SUM(CASE WHEN is_verified = 1 THEN 1 ELSE 0 END) as verifiedCount,",
        "  SUM(CASE WHEN status = 'banned' THEN 1 ELSE 0 END) as bannedCount",
        "FROM users",
        "WHERE deleted = 0"
    })
    UserStats selectUserStats(@Param("days") int days);

    /**
     * 获取商品统计数据
     */
    @Select({
        "SELECT",
        "  COUNT(*) as totalCount,",
        "  SUM(CASE WHEN created_at &gt;= DATE_SUB(NOW(), INTERVAL #{days} DAY) THEN 1 ELSE 0 END) as newCount,",
        "  SUM(CASE WHEN status = 'on_sale' THEN 1 ELSE 0 END) as onSaleCount,",
        "  SUM(CASE WHEN status = 'sold' THEN 1 ELSE 0 END) as soldCount,",
        "  SUM(CASE WHEN status = 'pending_review' THEN 1 ELSE 0 END) as pendingReviewCount,",
        "  SUM(CASE WHEN status = 'rejected' THEN 1 ELSE 0 END) as rejectedCount",
        "FROM products",
        "WHERE deleted = 0"
    })
    ProductStats selectProductStats(@Param("days") int days);

    /**
     * 获取订单统计数据
     */
    @Select({
        "SELECT",
        "  COUNT(*) as totalCount,",
        "  SUM(CASE WHEN created_at &gt;= DATE_SUB(NOW(), INTERVAL #{days} DAY) THEN 1 ELSE 0 END) as newCount,",
        "  SUM(CASE WHEN status IN ('pending', 'paid', 'shipped') THEN 1 ELSE 0 END) as pendingCount,",
        "  SUM(CASE WHEN status = 'completed' THEN 1 ELSE 0 END) as completedCount,",
        "  SUM(CASE WHEN status = 'cancelled' THEN 1 ELSE 0 END) as cancelledCount,",
        "  COALESCE(SUM(total_amount), 0) as totalAmount",
        "FROM orders",
        "WHERE deleted = 0"
    })
    OrderStats selectOrderStats(@Param("days") int days);

    /**
     * 获取交易统计数据
     */
    @Select({
        "SELECT",
        "  COALESCE(SUM(total_amount), 0) as totalAmount,",
        "  COALESCE(SUM(CASE WHEN created_at &gt;= DATE_SUB(NOW(), INTERVAL #{days} DAY) THEN total_amount ELSE 0 END), 0) as newAmount,",
        "  COUNT(*) as transactionCount,",
        "  COALESCE(AVG(total_amount), 0) as averageAmount",
        "FROM orders",
        "WHERE deleted = 0 AND status = 'completed'"
    })
    TransactionStats selectTransactionStats(@Param("days") int days);

    /**
     * 获取每日新增用户数据（用于图表）
     */
    @Select({
        "SELECT DATE(created_at) as date, COUNT(*) as count",
        "FROM users",
        "WHERE deleted = 0",
        "AND created_at &gt;= DATE_SUB(NOW(), INTERVAL #{days} DAY)",
        "GROUP BY DATE(created_at)",
        "ORDER BY date"
    })
    java.util.List<java.util.Map<String, Object>> selectDailyUserStats(@Param("days") int days);

    /**
     * 获取每日新增商品数据（用于图表）
     */
    @Select({
        "SELECT DATE(created_at) as date, COUNT(*) as count",
        "FROM products",
        "WHERE deleted = 0",
        "AND created_at &gt;= DATE_SUB(NOW(), INTERVAL #{days} DAY)",
        "GROUP BY DATE(created_at)",
        "ORDER BY date"
    })
    java.util.List<java.util.Map<String, Object>> selectDailyProductStats(@Param("days") int days);

    /**
     * 获取每日订单数据（用于图表）
     */
    @Select({
        "SELECT DATE(created_at) as date, COUNT(*) as count, COALESCE(SUM(total_amount), 0) as amount",
        "FROM orders",
        "WHERE deleted = 0",
        "AND created_at &gt;= DATE_SUB(NOW(), INTERVAL #{days} DAY)",
        "GROUP BY DATE(created_at)",
        "ORDER BY date"
    })
    java.util.List<java.util.Map<String, Object>> selectDailyOrderStats(@Param("days") int days);

    /**
     * 获取分类商品统计
     */
    @Select({
        "SELECT c.name as categoryName, COUNT(p.id) as productCount",
        "FROM categories c",
        "LEFT JOIN products p ON c.id = p.category_id AND p.deleted = 0",
        "WHERE c.deleted = 0",
        "GROUP BY c.id, c.name",
        "ORDER BY productCount DESC"
    })
    java.util.List<java.util.Map<String, Object>> selectCategoryStats();
}