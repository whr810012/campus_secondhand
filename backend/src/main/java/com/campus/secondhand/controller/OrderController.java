package com.campus.secondhand.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.common.Result;
import com.campus.secondhand.entity.Order;
import com.campus.secondhand.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器
 *
 * @author campus-secondhand
 */
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

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
     * 分页查询用户订单
     */
    @GetMapping("/user/{userId}")
    public Result<Page<Order>> getUserOrders(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String status,
            @RequestParam(defaultValue = "") String type) {
        Page<Order> orders = orderService.getUserOrders(userId, page, size, status, type);
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

    /**
     * 管理员查看所有订单
     */
    @GetMapping("/admin")
    public Result<Page<Order>> getAllOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String status,
            @RequestParam(defaultValue = "") String keyword) {
        Page<Order> orders = orderService.getAllOrders(page, size, status, keyword);
        return Result.success(orders);
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
}