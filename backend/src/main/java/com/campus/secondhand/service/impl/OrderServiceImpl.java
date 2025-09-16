package com.campus.secondhand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.entity.Order;
import com.campus.secondhand.entity.Product;
import com.campus.secondhand.mapper.OrderMapper;
import com.campus.secondhand.mapper.ProductMapper;
import com.campus.secondhand.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * 订单服务实现类
 *
 * @author campus-secondhand
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public Order createOrder(Long productId, Long buyerId, Integer tradeType, 
                           String tradeLocation, String deliveryAddress, 
                           String receiverName, String receiverPhone, String remark) {
        // 检查商品是否存在且可售
        Product product = productMapper.selectById(productId);
        if (product == null || product.getDeleted() == 1) {
            throw new RuntimeException("商品不存在");
        }
        if (!"available".equals(product.getStatus())) {
            throw new RuntimeException("商品不可售");
        }
        if (product.getSellerId().equals(buyerId)) {
            throw new RuntimeException("不能购买自己的商品");
        }

        // 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setProductId(productId);
        order.setBuyerId(buyerId);
        order.setSellerId(product.getSellerId());
        order.setAmount(product.getPrice());
        order.setStatus("pending");
        order.setTradeType(tradeType);
        order.setTradeLocation(tradeLocation);
        order.setDeliveryAddress(deliveryAddress);
        order.setReceiverName(receiverName);
        order.setReceiverPhone(receiverPhone);
        order.setRemark(remark);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        orderMapper.insert(order);

        // 更新商品状态为已预定
        product.setStatus("reserved");
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.updateById(product);

        log.info("创建订单成功，订单号：{}", order.getOrderNo());
        return order;
    }

    @Override
    public Order getOrderById(Long id) {
        return orderMapper.selectById(id);
    }

    @Override
    public Page<Order> getUserOrders(Long userId, int page, int size, String status, String type) {
        Page<Order> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        
        // 根据类型查询
        if ("buy".equals(type)) {
            queryWrapper.eq(Order::getBuyerId, userId);
        } else if ("sell".equals(type)) {
            queryWrapper.eq(Order::getSellerId, userId);
        } else {
            queryWrapper.and(wrapper -> wrapper.eq(Order::getBuyerId, userId)
                    .or().eq(Order::getSellerId, userId));
        }
        
        // 状态筛选
        if (StringUtils.hasText(status)) {
            queryWrapper.eq(Order::getStatus, status);
        }
        
        // 只查询未删除的订单
        queryWrapper.eq(Order::getDeleted, 0);
        queryWrapper.orderByDesc(Order::getCreatedAt);
        
        return orderMapper.selectPage(pageParam, queryWrapper);
    }

    @Override
    @Transactional
    public boolean payOrder(Long orderId, String paymentMethod, String paymentNo) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || order.getDeleted() == 1) {
            throw new RuntimeException("订单不存在");
        }
        if (!"pending".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不正确");
        }

        order.setStatus("paid");
        order.setPaymentMethod(paymentMethod);
        order.setPaymentNo(paymentNo);
        order.setPaymentTime(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        int result = orderMapper.updateById(order);
        if (result > 0) {
            log.info("订单支付成功，订单号：{}", order.getOrderNo());
        }
        return result > 0;
    }

    @Override
    @Transactional
    public boolean confirmReceive(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || order.getDeleted() == 1) {
            throw new RuntimeException("订单不存在");
        }
        if (!order.getBuyerId().equals(userId)) {
            throw new RuntimeException("无权限操作此订单");
        }
        if (!"delivered".equals(order.getStatus()) && !"shipped".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不正确");
        }

        order.setStatus("completed");
        order.setConfirmTime(LocalDateTime.now());
        order.setCompleteTime(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        int result = orderMapper.updateById(order);
        
        if (result > 0) {
            // 更新商品状态为已售出
            Product product = productMapper.selectById(order.getProductId());
            if (product != null) {
                product.setStatus("sold");
                product.setUpdatedAt(LocalDateTime.now());
                productMapper.updateById(product);
            }
            log.info("确认收货成功，订单号：{}", order.getOrderNo());
        }
        return result > 0;
    }

    @Override
    @Transactional
    public boolean cancelOrder(Long orderId, Long userId, String reason) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || order.getDeleted() == 1) {
            throw new RuntimeException("订单不存在");
        }
        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            throw new RuntimeException("无权限操作此订单");
        }
        if ("completed".equals(order.getStatus()) || "cancelled".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不允许取消");
        }

        order.setStatus("cancelled");
        order.setCancelReason(reason);
        order.setCancelTime(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        int result = orderMapper.updateById(order);
        
        if (result > 0) {
            // 恢复商品状态为可售
            Product product = productMapper.selectById(order.getProductId());
            if (product != null) {
                product.setStatus("available");
                product.setUpdatedAt(LocalDateTime.now());
                productMapper.updateById(product);
            }
            log.info("取消订单成功，订单号：{}", order.getOrderNo());
        }
        return result > 0;
    }

    @Override
    @Transactional
    public boolean shipOrder(Long orderId, Long sellerId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || order.getDeleted() == 1) {
            throw new RuntimeException("订单不存在");
        }
        if (!order.getSellerId().equals(sellerId)) {
            throw new RuntimeException("无权限操作此订单");
        }
        if (!"paid".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不正确");
        }

        order.setStatus("shipped");
        order.setShipTime(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        int result = orderMapper.updateById(order);
        if (result > 0) {
            log.info("发货成功，订单号：{}", order.getOrderNo());
        }
        return result > 0;
    }

    @Override
    @Transactional
    public boolean requestRefund(Long orderId, Long userId, String reason) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || order.getDeleted() == 1) {
            throw new RuntimeException("订单不存在");
        }
        if (!order.getBuyerId().equals(userId)) {
            throw new RuntimeException("无权限操作此订单");
        }
        if (!"paid".equals(order.getStatus()) && !"shipped".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不允许退款");
        }

        order.setStatus("refunded");
        order.setRefundReason(reason);
        order.setRefundTime(LocalDateTime.now());
        order.setRefundAmount(order.getAmount());
        order.setUpdatedAt(LocalDateTime.now());

        int result = orderMapper.updateById(order);
        
        if (result > 0) {
            // 恢复商品状态为可售
            Product product = productMapper.selectById(order.getProductId());
            if (product != null) {
                product.setStatus("available");
                product.setUpdatedAt(LocalDateTime.now());
                productMapper.updateById(product);
            }
            log.info("申请退款成功，订单号：{}", order.getOrderNo());
        }
        return result > 0;
    }

    @Override
    public Page<Order> getAllOrders(int page, int size, String status, String keyword) {
        Page<Order> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        
        // 状态筛选
        if (StringUtils.hasText(status)) {
            queryWrapper.eq(Order::getStatus, status);
        }
        
        // 关键词搜索（订单号）
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like(Order::getOrderNo, keyword);
        }
        
        // 只查询未删除的订单
        queryWrapper.eq(Order::getDeleted, 0);
        queryWrapper.orderByDesc(Order::getCreatedAt);
        
        return orderMapper.selectPage(pageParam, queryWrapper);
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%04d", new Random().nextInt(10000));
        return "ORD" + timestamp + random;
    }
}