package com.campus.controller;

import com.campus.common.PageResult;
import com.campus.common.Result;
import com.campus.entity.Order;
import com.campus.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * 订单控制器
 * 
 * @author Campus Team
 */
@RestController
@RequestMapping("/order")
@CrossOrigin
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    /**
     * 创建订单
     */
    @PostMapping("/create")
    public Result<String> createOrder(@RequestBody Order order) {
        try {
            String orderNo = orderService.createOrder(order);
            return Result.success("订单创建成功", orderNo);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 分页查询订单列表（管理端）
     */
    @GetMapping("/admin/page")
    public Result<PageResult<Order>> getOrderPageForAdmin(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder) {
        try {
            PageResult<Order> result = orderService.getOrderPageForAdmin(current, size, keyword, status, sortBy, sortOrder);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 查询用户购买订单
     */
    @GetMapping("/buyer/{userId}")
    public Result<PageResult<Order>> getUserBuyOrders(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Integer status) {
        try {
            PageResult<Order> result = orderService.getUserBuyOrders(userId, current, size, status);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 查询用户销售订单
     */
    @GetMapping("/seller/{userId}")
    public Result<PageResult<Order>> getUserSellOrders(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Integer status) {
        try {
            PageResult<Order> result = orderService.getUserSellOrders(userId, current, size, status);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 根据订单号查询订单
     */
    @GetMapping("/no/{orderNo}")
    public Result<Order> getOrderByNo(@PathVariable String orderNo) {
        try {
            Order order = orderService.getOrderByNo(orderNo);
            return Result.success(order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取订单详情
     */
    @GetMapping("/{orderId}")
    public Result<Order> getOrderDetail(@PathVariable Long orderId) {
        try {
            Order order = orderService.getOrderDetail(orderId);
            return Result.success(order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 确认订单
     */
    @PutMapping("/confirm/{orderId}")
    public Result<Void> confirmOrder(@PathVariable Long orderId) {
        try {
            orderService.confirmOrder(orderId);
            return Result.success("订单确认成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 取消订单
     */
    @PutMapping("/cancel/{orderId}")
    public Result<Void> cancelOrder(@PathVariable Long orderId, @RequestBody Map<String, String> params) {
        try {
            String cancelReason = params.get("cancelReason");
            orderService.cancelOrder(orderId, cancelReason);
            return Result.success("订单取消成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 开始交易
     */
    @PutMapping("/start-trade/{orderId}")
    public Result<Void> startTrade(@PathVariable Long orderId) {
        try {
            orderService.startTrade(orderId);
            return Result.success("交易开始", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 完成订单
     */
    @PutMapping("/complete/{orderId}")
    public Result<Void> completeOrder(@PathVariable Long orderId) {
        try {
            orderService.completeOrder(orderId);
            return Result.success("订单完成", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新订单状态
     */
    @PutMapping("/status/{orderId}")
    public Result<Void> updateOrderStatus(@PathVariable Long orderId, @RequestBody Map<String, Integer> params) {
        try {
            Integer status = params.get("status");
            orderService.updateOrderStatus(orderId, status);
            return Result.success("状态更新成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除订单
     */
    @DeleteMapping("/{orderId}")
    public Result<Void> deleteOrder(@PathVariable Long orderId) {
        try {
            orderService.deleteOrder(orderId);
            return Result.success("订单删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取订单统计信息
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getOrderStatistics() {
        try {
            Map<String, Object> statistics = orderService.getOrderStatistics();
            return Result.success(statistics);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取用户订单统计
     */
    @GetMapping("/user-statistics/{userId}")
    public Result<Map<String, Object>> getUserOrderStatistics(@PathVariable Long userId) {
        try {
            Map<String, Object> statistics = orderService.getUserOrderStatistics(userId);
            return Result.success(statistics);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取最近7天订单统计
     */
    @GetMapping("/recent-statistics")
    public Result<Map<String, Object>> getRecentOrderStatistics() {
        try {
            Map<String, Object> statistics = orderService.getRecentOrderStatistics();
            return Result.success(statistics);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取交易金额统计
     */
    @GetMapping("/amount-statistics")
    public Result<Map<String, Object>> getOrderAmountStatistics() {
        try {
            Map<String, Object> statistics = orderService.getOrderAmountStatistics();
            return Result.success(statistics);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}