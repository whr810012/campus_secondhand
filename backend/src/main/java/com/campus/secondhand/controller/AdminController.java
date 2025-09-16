package com.campus.secondhand.controller;

import com.campus.secondhand.common.Result;
import com.campus.secondhand.service.AdminService;
import com.campus.secondhand.service.AdminService.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员控制器
 *
 * @author campus-secondhand
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /**
     * 获取仪表盘统计数据
     */
    @GetMapping("/dashboard/stats")
    public Result<DashboardStats> getDashboardStats() {
        DashboardStats stats = adminService.getDashboardStats();
        return Result.success(stats);
    }

    /**
     * 获取用户统计数据
     */
    @GetMapping("/stats/users")
    public Result<UserStats> getUserStats(@RequestParam(defaultValue = "30") int days) {
        UserStats stats = adminService.getUserStats(days);
        return Result.success(stats);
    }

    /**
     * 获取商品统计数据
     */
    @GetMapping("/stats/products")
    public Result<ProductStats> getProductStats(@RequestParam(defaultValue = "30") int days) {
        ProductStats stats = adminService.getProductStats(days);
        return Result.success(stats);
    }

    /**
     * 获取订单统计数据
     */
    @GetMapping("/stats/orders")
    public Result<OrderStats> getOrderStats(@RequestParam(defaultValue = "30") int days) {
        OrderStats stats = adminService.getOrderStats(days);
        return Result.success(stats);
    }

    /**
     * 获取交易统计数据
     */
    @GetMapping("/stats/transactions")
    public Result<TransactionStats> getTransactionStats(@RequestParam(defaultValue = "30") int days) {
        TransactionStats stats = adminService.getTransactionStats(days);
        return Result.success(stats);
    }
}