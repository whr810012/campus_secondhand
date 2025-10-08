package com.campus.secondhand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.entity.Order;
import com.campus.secondhand.entity.Product;
import com.campus.secondhand.mapper.OrderMapper;
import com.campus.secondhand.mapper.ProductMapper;
import com.campus.secondhand.service.OrderService;
import com.campus.secondhand.service.ProductService;
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
    private final ProductService productService;

    @Override
    @Transactional
    public Order createOrder(Long productId, Long buyerId, Long sellerId, Double amount, Integer tradeType, 
                           String tradeLocation, String deliveryAddress, 
                           String receiverName, String receiverPhone, String paymentMethod, String remark) {
        // 检查商品是否存在且可售
        Product product = productMapper.selectById(productId);
        if (product == null || product.getDeleted() == 1) {
            throw new RuntimeException("商品不存在");
        }
        if (!"available".equals(product.getStatus()) && !"1".equals(product.getStatus())) {
            throw new RuntimeException("商品不可售");
        }
        if (product.getSellerId().equals(buyerId)) {
            throw new RuntimeException("不能购买自己的商品");
        }
        
        // 验证卖家ID是否匹配
        if (!product.getSellerId().equals(sellerId)) {
            throw new RuntimeException("卖家信息不匹配");
        }

        // 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setProductId(productId);
        order.setBuyerId(buyerId);
        order.setSellerId(sellerId);
        order.setAmount(amount != null ? BigDecimal.valueOf(amount) : product.getPrice());
        order.setStatus("pending");
        order.setTradeType(tradeType);
        order.setTradeLocation(tradeLocation);
        order.setDeliveryAddress(deliveryAddress);
        order.setReceiverName(receiverName);
        order.setReceiverPhone(receiverPhone);
        order.setPaymentMethod(paymentMethod);
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
        Order order = orderMapper.selectById(id);
        if (order != null && order.getProductId() != null) {
            // 使用ProductService的getProductById方法来获取包含图片数据的商品信息
            Product product = productService.getProductById(order.getProductId());
            order.setProduct(product);
        }
        return order;
    }

    @Override
    public Page<Order> getUserOrders(Long userId, int page, int size, String status, String keyword, String startDate, String endDate) {
        Page<Order> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        
        // 用户ID筛选
        queryWrapper.eq(Order::getBuyerId, userId);
        
        // 状态筛选
        if (StringUtils.hasText(status)) {
            queryWrapper.eq(Order::getStatus, status);
        }
        
        // 关键词搜索（订单号）
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like(Order::getOrderNo, keyword);
        }
        
        // 日期范围筛选
        if (StringUtils.hasText(startDate) && StringUtils.hasText(endDate)) {
            queryWrapper.between(Order::getCreatedAt, startDate, endDate);
        }
        
        // 只查询未删除的订单
        queryWrapper.eq(Order::getDeleted, 0);
        queryWrapper.orderByDesc(Order::getCreatedAt);
        
        Page<Order> orderPage = orderMapper.selectPage(pageParam, queryWrapper);
        
        // 为每个订单关联商品信息
        for (Order order : orderPage.getRecords()) {
            if (order.getProductId() != null) {
                // 使用ProductService的getProductById方法来获取包含图片数据的商品信息
                Product product = productService.getProductById(order.getProductId());
                order.setProduct(product);
            }
        }
        
        return orderPage;
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
        if (!"delivered".equals(order.getStatus()) && !"shipped".equals(order.getStatus()) && !"paid".equals(order.getStatus())) {
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
        
        Page<Order> orderPage = orderMapper.selectPage(pageParam, queryWrapper);
        
        // 为每个订单关联商品信息
        for (Order order : orderPage.getRecords()) {
            if (order.getProductId() != null) {
                // 使用ProductService的getProductById方法来获取包含图片数据的商品信息
                Product product = productService.getProductById(order.getProductId());
                order.setProduct(product);
            }
        }
        
        return orderPage;
    }

    @Override
    public Page<Order> getOrderListForAdmin(int page, int size, String status, String keyword) {
        log.info("管理员查询订单列表: page={}, size={}, status={}, keyword={}", page, size, status, keyword);
        
        // 直接调用已有的getAllOrders方法
        return getAllOrders(page, size, status, keyword);
    }

    @Override
    @Transactional
    public boolean deleteOrder(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || order.getDeleted() == 1) {
            throw new RuntimeException("订单不存在");
        }
        
        // 检查权限：只有买家、卖家或管理员可以删除订单
        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            // 这里可以添加管理员权限检查
            throw new RuntimeException("无权限删除此订单");
        }
        
        // 只有已取消或已完成的订单才能删除
        if (!"cancelled".equals(order.getStatus()) && !"completed".equals(order.getStatus()) && !"refunded".equals(order.getStatus())) {
            throw new RuntimeException("只能删除已取消、已完成或已退款的订单");
        }

        // 使用MyBatis-Plus的逻辑删除功能
        int result = orderMapper.deleteById(orderId);
        
        if (result > 0) {
            log.info("删除订单成功，订单号：{}", order.getOrderNo());
        }
        return result > 0;
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