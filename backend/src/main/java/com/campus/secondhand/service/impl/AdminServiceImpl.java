package com.campus.secondhand.service.impl;

import com.campus.secondhand.mapper.AdminMapper;
import com.campus.secondhand.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 管理员服务实现类
 *
 * @author campus-secondhand
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminMapper adminMapper;

    @Override
    public DashboardStats getDashboardStats() {
        DashboardStats stats = adminMapper.selectDashboardStats();
        if (stats == null) {
            stats = new DashboardStats();
            stats.setTotalUsers(0L);
            stats.setTotalProducts(0L);
            stats.setTotalOrders(0L);
            stats.setTotalReviews(0L);
            stats.setActiveUsers(0L);
            stats.setOnSaleProducts(0L);
            stats.setPendingOrders(0L);
            stats.setTodayNewUsers(0L);
            stats.setTodayNewProducts(0L);
            stats.setTodayNewOrders(0L);
        }
        log.info("获取仪表盘统计数据: 总用户数={}, 总商品数={}, 总订单数={}", 
                stats.getTotalUsers(), stats.getTotalProducts(), stats.getTotalOrders());
        return stats;
    }

    @Override
    public UserStats getUserStats(int days) {
        UserStats stats = adminMapper.selectUserStats(days);
        if (stats == null) {
            stats = new UserStats();
            stats.setTotalCount(0L);
            stats.setNewCount(0L);
            stats.setActiveCount(0L);
            stats.setVerifiedCount(0L);
            stats.setBannedCount(0L);
        }
        log.info("获取{}天内用户统计数据: 总数={}, 新增={}, 活跃={}", 
                days, stats.getTotalCount(), stats.getNewCount(), stats.getActiveCount());
        return stats;
    }

    @Override
    public ProductStats getProductStats(int days) {
        ProductStats stats = adminMapper.selectProductStats(days);
        if (stats == null) {
            stats = new ProductStats();
            stats.setTotalCount(0L);
            stats.setNewCount(0L);
            stats.setOnSaleCount(0L);
            stats.setSoldCount(0L);
            stats.setPendingReviewCount(0L);
            stats.setRejectedCount(0L);
        }
        log.info("获取{}天内商品统计数据: 总数={}, 新增={}, 在售={}, 已售={}", 
                days, stats.getTotalCount(), stats.getNewCount(), stats.getOnSaleCount(), stats.getSoldCount());
        return stats;
    }

    @Override
    public OrderStats getOrderStats(int days) {
        OrderStats stats = adminMapper.selectOrderStats(days);
        if (stats == null) {
            stats = new OrderStats();
            stats.setTotalCount(0L);
            stats.setNewCount(0L);
            stats.setPendingCount(0L);
            stats.setCompletedCount(0L);
            stats.setCancelledCount(0L);
            stats.setTotalAmount(0.0);
        }
        log.info("获取{}天内订单统计数据: 总数={}, 新增={}, 待处理={}, 已完成={}, 总金额={}", 
                days, stats.getTotalCount(), stats.getNewCount(), stats.getPendingCount(), 
                stats.getCompletedCount(), stats.getTotalAmount());
        return stats;
    }

    @Override
    public TransactionStats getTransactionStats(int days) {
        TransactionStats stats = adminMapper.selectTransactionStats(days);
        if (stats == null) {
            stats = new TransactionStats();
            stats.setTotalAmount(0.0);
            stats.setNewAmount(0.0);
            stats.setTransactionCount(0L);
            stats.setAverageAmount(0.0);
        }
        log.info("获取{}天内交易统计数据: 总金额={}, 新增金额={}, 交易数={}, 平均金额={}", 
                days, stats.getTotalAmount(), stats.getNewAmount(), 
                stats.getTransactionCount(), stats.getAverageAmount());
        return stats;
    }
}