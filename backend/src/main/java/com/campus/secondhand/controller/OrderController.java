package com.campus.secondhand.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.common.Result;
import com.campus.secondhand.entity.Order;
import com.campus.secondhand.service.OrderService;
import com.campus.secondhand.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器
 *
 * @author campus-secondhand
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final JwtUtil jwtUtil;

    /**
     * 创建订单
     */
    @PostMapping
    public Result<Order> createOrder(@RequestBody CreateOrderRequest request) {
        try {
            Order order = orderService.createOrder(
                request.getProductId(),
                request.getBuyerId(),
                request.getTradeType(),
                request.getTradeLocation(),
                request.getDeliveryAddress(),
                request.getReceiverName(),
                request.getReceiverPhone(),
                request.getRemark()
            );
            return Result.success(order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{id}")
    public Result<Order> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        return Result.success(order);
    }

    /**
     * 获取当前用户的订单（从token中解析用户ID）
     *
     * @param token Authorization header中的token
     * @param page 页码
     * @param size 每页大小
     * @param status 订单状态
     * @param keyword 关键词
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 订单分页数据
     */
    @GetMapping("/my")
    public Result<Page<Order>> getMyOrders(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        // 从token中解析用户ID
        Long userId = jwtUtil.getUserIdFromToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return Result.error("无效的token");
        }
        
        Page<Order> orders = orderService.getUserOrders(userId, page, size, status, "");
        return Result.success(orders);
    }

    /**
     * 分页查询用户订单
     *
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @param status 订单状态
     * @param keyword 关键词
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 订单分页数据
     */
    @GetMapping("/user/{userId}")
    public Result<Page<Order>> getUserOrders(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Page<Order> orders = orderService.getUserOrders(userId, page, size, status, "");
        return Result.success(orders);
    }

    /**
     * 支付订单
     */
    @PostMapping("/{id}/pay")
    public Result<Void> payOrder(@PathVariable Long id, @RequestBody PayOrderRequest request) {
        try {
            boolean success = orderService.payOrder(id, request.getPaymentMethod(), request.getPaymentNo());
            if (success) {
                return Result.success();
            } else {
                return Result.error("支付失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 确认收货
     */
    @PostMapping("/{id}/confirm")
    public Result<Void> confirmReceive(@PathVariable Long id, @RequestParam Long userId) {
        try {
            boolean success = orderService.confirmReceive(id, userId);
            if (success) {
                return Result.success();
            } else {
                return Result.error("确认收货失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 取消订单
     */
    @PostMapping("/{id}/cancel")
    public Result<Void> cancelOrder(@PathVariable Long id, @RequestBody CancelOrderRequest request) {
        try {
            boolean success = orderService.cancelOrder(id, request.getUserId(), request.getReason());
            if (success) {
                return Result.success();
            } else {
                return Result.error("取消订单失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 发货
     */
    @PostMapping("/{id}/ship")
    public Result<Void> shipOrder(@PathVariable Long id, @RequestParam Long sellerId) {
        try {
            boolean success = orderService.shipOrder(id, sellerId);
            if (success) {
                return Result.success();
            } else {
                return Result.error("发货失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 申请退款
     */
    @PostMapping("/{id}/refund")
    public Result<Void> requestRefund(@PathVariable Long id, @RequestBody RefundRequest request) {
        try {
            boolean success = orderService.requestRefund(id, request.getUserId(), request.getReason());
            if (success) {
                return Result.success();
            } else {
                return Result.error("申请退款失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }



    // 内部类定义请求参数
    public static class CreateOrderRequest {
        private Long productId;
        private Long buyerId;
        private Integer tradeType;
        private String tradeLocation;
        private String deliveryAddress;
        private String receiverName;
        private String receiverPhone;
        private String remark;

        // Getters and Setters
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public Long getBuyerId() { return buyerId; }
        public void setBuyerId(Long buyerId) { this.buyerId = buyerId; }
        public Integer getTradeType() { return tradeType; }
        public void setTradeType(Integer tradeType) { this.tradeType = tradeType; }
        public String getTradeLocation() { return tradeLocation; }
        public void setTradeLocation(String tradeLocation) { this.tradeLocation = tradeLocation; }
        public String getDeliveryAddress() { return deliveryAddress; }
        public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
        public String getReceiverName() { return receiverName; }
        public void setReceiverName(String receiverName) { this.receiverName = receiverName; }
        public String getReceiverPhone() { return receiverPhone; }
        public void setReceiverPhone(String receiverPhone) { this.receiverPhone = receiverPhone; }
        public String getRemark() { return remark; }
        public void setRemark(String remark) { this.remark = remark; }
    }

    public static class PayOrderRequest {
        private String paymentMethod;
        private String paymentNo;

        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
        public String getPaymentNo() { return paymentNo; }
        public void setPaymentNo(String paymentNo) { this.paymentNo = paymentNo; }
    }

    public static class CancelOrderRequest {
        private Long userId;
        private String reason;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }

    public static class RefundRequest {
        private Long userId;
        private String reason;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }

    /**
     * 管理员获取订单列表
     */
    @GetMapping("/admin")
    public Result<Page<Order>> getOrderListForAdmin(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        Page<Order> orders = orderService.getOrderListForAdmin(page, size, status, keyword);
        return Result.success(orders);
    }
}