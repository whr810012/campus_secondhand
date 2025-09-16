package com.campus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.common.PageResult;
import com.campus.entity.Order;

import java.util.Map;

/**
 * 订单服务接口
 * 
 * @author Campus Team
 */
public interface OrderService extends IService<Order> {
    
    /**
     * 创建订单
     */
    String createOrder(Long productId, Long buyerId, Integer tradeType, 
                      String deliveryAddress, String contactPhone, String remark);
    
    /**
     * 分页查询订单列表（管理端）
     */
    PageResult<Order> getOrderPage(Long current, Long size, String keyword, 
                                 Integer status, Long buyerId, Long sellerId);
    
    /**
     * 查询用户购买的订单
     */
    PageResult<Order> getBuyerOrders(Long current, Long size, Long buyerId, Integer status);
    
    /**
     * 查询用户销售的订单
     */
    PageResult<Order> getSellerOrders(Long current, Long size, Long sellerId, Integer status);
    
    /**
     * 根据订单号查询订单
     */
    Order getOrderByOrderNo(String orderNo);
    
    /**
     * 获取订单详情
     */
    Order getOrderDetail(Long orderId);
    
    /**
     * 确认订单
     */
    void confirmOrder(Long orderId, Long userId);
    
    /**
     * 取消订单
     */
    void cancelOrder(Long orderId, Long userId, String reason);
    
    /**
     * 开始交易
     */
    void startTrade(Long orderId, Long userId);
    
    /**
     * 完成订单
     */
    void completeOrder(Long orderId, Long userId);
    
    /**
     * 更新订单状态
     */
    void updateOrderStatus(Long orderId, Integer status);
    
    /**
     * 删除订单
     */
    void deleteOrder(Long orderId);
    
    /**
     * 获取订单统计信息
     */
    Map<String, Object> getOrderStatistics();
    
    /**
     * 获取用户订单统计
     */
    Map<String, Object> getUserOrderStatistics(Long userId);
    
    /**
     * 检查用户是否可以操作订单
     */
    boolean canOperateOrder(Long userId, Long orderId);
    
    /**
     * 生成订单号
     */
    String generateOrderNo();
    
    /**
     * 获取最近7天订单统计
     */
    Map<String, Object> getOrderStatisticsLast7Days();
    
    /**
     * 获取交易金额统计
     */
    Map<String, Object> getTradeAmountStatistics();
}