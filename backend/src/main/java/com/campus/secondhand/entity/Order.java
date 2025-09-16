package com.campus.secondhand.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 *
 * @author campus-secondhand
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("orders")
public class Order {

    /**
     * 订单ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品信息
     */
    @TableField(exist = false)
    private Product product;

    /**
     * 买家ID
     */
    private Long buyerId;

    /**
     * 买家信息
     */
    @TableField(exist = false)
    private User buyer;

    /**
     * 卖家ID
     */
    private Long sellerId;

    /**
     * 卖家信息
     */
    @TableField(exist = false)
    private User seller;

    /**
     * 订单金额
     */
    private BigDecimal amount;

    /**
     * 订单状态：pending-待付款，paid-已付款，shipped-已发货，delivered-已送达，completed-已完成，cancelled-已取消，refunded-已退款
     */
    private String status;

    /**
     * 支付方式：online-线上支付，offline-线下支付
     */
    private String paymentMethod;

    /**
     * 支付时间
     */
    private LocalDateTime paymentTime;

    /**
     * 支付流水号
     */
    private String paymentNo;

    /**
     * 交易方式：1-仅线下，2-仅线上，3-线上线下均可
     */
    private Integer tradeType;

    /**
     * 交易地点
     */
    private String tradeLocation;

    /**
     * 收货地址
     */
    private String deliveryAddress;

    /**
     * 收货人姓名
     */
    private String receiverName;

    /**
     * 收货人电话
     */
    private String receiverPhone;

    /**
     * 发货时间
     */
    private LocalDateTime shipTime;

    /**
     * 确认收货时间
     */
    private LocalDateTime confirmTime;

    /**
     * 完成时间
     */
    private LocalDateTime completeTime;

    /**
     * 取消时间
     */
    private LocalDateTime cancelTime;

    /**
     * 取消原因
     */
    private String cancelReason;

    /**
     * 退款时间
     */
    private LocalDateTime refundTime;

    /**
     * 退款原因
     */
    private String refundReason;

    /**
     * 退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 备注
     */
    private String remark;

    /**
     * 买家评价状态：0-未评价，1-已评价
     */
    private Integer buyerRated;

    /**
     * 卖家评价状态：0-未评价，1-已评价
     */
    private Integer sellerRated;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer deleted;

}