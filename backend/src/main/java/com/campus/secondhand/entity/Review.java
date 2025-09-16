package com.campus.secondhand.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评价实体类
 *
 * @author campus-secondhand
 */
@Data
@TableName("reviews")
public class Review {

    /**
     * 评价ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 评价者ID
     */
    private Long reviewerId;

    /**
     * 被评价者ID
     */
    private Long reviewedId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 评分（1-5）
     */
    private Integer rating;

    /**
     * 评价内容
     */
    private String content;

    /**
     * 评价类型（buyer_to_seller: 买家评价卖家, seller_to_buyer: 卖家评价买家）
     */
    private String type;

    /**
     * 是否删除（0: 未删除, 1: 已删除）
     */
    private Integer deleted;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}