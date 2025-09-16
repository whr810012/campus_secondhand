package com.campus.secondhand.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.entity.Order;

/**
 * 订单服务接口
 *
 * @author campus-secondhand
 */
public interface OrderService {

    /**
     * 创建订单
     *
     * @param productId 商品ID
     * @param buyerId 买家ID
     * @param tradeType 交易方式
     * @param tradeLocation 交易地点
     * @param deliveryAddress 收货地址
     * @param receiverName 收货人姓名
     * @param receiverPhone 收货人电话
     * @param remark 备注
     * @return 订单
     */
    Order createOrder(Long productId, Long buyerId, Integer tradeType, 
                     String tradeLocation, String deliveryAddress, 
                     String receiverName, String receiverPhone, String remark);

    /**
     * 根据ID获取订单详情
     *
     * @param id 订单ID
     * @return 订单详情
     */
    Order getOrderById(Long id);

    /**
     * 分页查询用户订单
     *
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @param status 订单状态
     * @param type 订单类型：buy-购买订单，sell-销售订单
     * @return 订单分页数据
     */
    Page<Order> getUserOrders(Long userId, int page, int size, String status, String type);

    /**
     * 支付订单
     *
     * @param orderId 订单ID
     * @param paymentMethod 支付方式
     * @param paymentNo 支付流水号
     * @return 是否成功
     */
    boolean payOrder(Long orderId, String paymentMethod, String paymentNo);

    /**
     * 确认收货
     *
     * @param orderId 订单ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean confirmReceive(Long orderId, Long userId);

    /**
     * 取消订单
     *
     * @param orderId 订单ID
     * @param userId 用户ID
     * @param reason 取消原因
     * @return 是否成功
     */
    boolean cancelOrder(Long orderId, Long userId, String reason);

    /**
     * 发货
     *
     * @param orderId 订单ID
     * @param sellerId 卖家ID
     * @return 是否成功
     */
    boolean shipOrder(Long orderId, Long sellerId);

    /**
     * 申请退款
     *
     * @param orderId 订单ID
     * @param userId 用户ID
     * @param reason 退款原因
     * @return 是否成功
     */
    boolean requestRefund(Long orderId, Long userId, String reason);

    /**
     * 管理员查看所有订单
     *
     * @param page 页码
     * @param size 每页大小
     * @param status 订单状态
     * @param keyword 搜索关键词
     * @return 订单分页数据
     */
    Page<Order> getAllOrders(int page, int size, String status, String keyword);
}