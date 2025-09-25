package com.campus.secondhand.controller;

import com.campus.secondhand.common.Result;
import com.campus.secondhand.entity.User;
import com.campus.secondhand.entity.Product;
import com.campus.secondhand.service.AdminService;
import com.campus.secondhand.service.AdminService.*;
import com.campus.secondhand.service.UserManageService;
import com.campus.secondhand.service.ProductManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

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
    private final UserManageService userManageService;
    private final ProductManageService productManageService;

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



    /**
     * 获取待审核商品列表
     */
    @GetMapping("/products/pending")
    public Result<Map<String, Object>> getPendingProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Map<String, Object> result = productManageService.getPendingProducts(page, size);
        return Result.success(result);
    }

    /**
     * 获取系统状态
     */
    @GetMapping("/system/status")
    public Result<Map<String, Object>> getSystemStatus() {
        Map<String, Object> status = adminService.getSystemStatus();
        return Result.success(status);
    }

    /**
     * 获取用户增长趋势数据
     */
    @GetMapping("/stats/user-growth")
    public Result<List<Map<String, Object>>> getUserGrowthTrend(@RequestParam(defaultValue = "7") int days) {
        List<Map<String, Object>> data = adminService.getUserGrowthTrend(days);
        return Result.success(data);
    }

    /**
     * 获取分类统计数据
     */
    @GetMapping("/stats/categories")
    public Result<List<Map<String, Object>>> getCategoryStats() {
        List<Map<String, Object>> stats = adminService.getCategoryStats();
        return Result.success(stats);
    }

    /**
     * 审核通过商品
     */
    @PostMapping("/products/{productId}/approve")
    public Result<String> approveProduct(@PathVariable Long productId) {
        boolean success = productManageService.approveProduct(productId);
        return success ? Result.success("商品审核通过") : Result.error("审核失败");
    }

    /**
     * 审核拒绝商品
     */
    @PostMapping("/products/{productId}/reject")
    public Result<String> rejectProduct(@PathVariable Long productId, @RequestParam String reason) {
        boolean success = productManageService.rejectProduct(productId, reason);
        return success ? Result.success("商品审核拒绝") : Result.error("审核失败");
    }
}