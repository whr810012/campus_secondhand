package com.campus.secondhand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.entity.Order;
import com.campus.secondhand.entity.Product;
import com.campus.secondhand.entity.Review;
import com.campus.secondhand.entity.User;
import com.campus.secondhand.mapper.OrderMapper;
import com.campus.secondhand.mapper.ProductMapper;
import com.campus.secondhand.mapper.ReviewMapper;
import com.campus.secondhand.mapper.UserMapper;
import com.campus.secondhand.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 评价服务实现类
 *
 * @author campus-secondhand
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;
    private final OrderMapper orderMapper;
    private final UserMapper userMapper;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public Review createReview(Long orderId, Long reviewerId, Long reviewedId, Long productId, 
                              Integer rating, String content, String type) {
        // 验证订单
        Order order = orderMapper.selectById(orderId);
        if (order == null || order.getDeleted() == 1) {
            throw new RuntimeException("订单不存在");
        }

        // 验证订单状态（只有已完成的订单才能评价）
        if (!"completed".equals(order.getStatus())) {
            throw new RuntimeException("只有已完成的订单才能评价");
        }

        // 验证评价权限
        if ("buyer_to_seller".equals(type)) {
            if (!order.getBuyerId().equals(reviewerId) || !order.getSellerId().equals(reviewedId)) {
                throw new RuntimeException("无权限评价");
            }
        } else if ("seller_to_buyer".equals(type)) {
            if (!order.getSellerId().equals(reviewerId) || !order.getBuyerId().equals(reviewedId)) {
                throw new RuntimeException("无权限评价");
            }
        } else {
            throw new RuntimeException("评价类型错误");
        }

        // 检查是否已评价
        if (!canReview(orderId, reviewerId, type)) {
            throw new RuntimeException("已评价过该订单");
        }

        // 验证用户
        User reviewer = userMapper.selectById(reviewerId);
        User reviewed = userMapper.selectById(reviewedId);
        if (reviewer == null || reviewed == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证商品
        Product product = productMapper.selectById(productId);
        if (product == null || product.getDeleted() == 1) {
            throw new RuntimeException("商品不存在");
        }

        // 验证评分
        if (rating < 1 || rating > 5) {
            throw new RuntimeException("评分必须在1-5之间");
        }

        // 创建评价
        Review review = new Review();
        review.setOrderId(orderId);
        review.setReviewerId(reviewerId);
        review.setReviewedId(reviewedId);
        review.setProductId(productId);
        review.setRating(rating);
        review.setContent(content);
        review.setType(type);
        review.setDeleted(0);
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());

        int result = reviewMapper.insert(review);
        if (result > 0) {
            log.info("用户{}对用户{}创建评价成功，评价ID: {}", reviewerId, reviewedId, review.getId());
            return review;
        } else {
            throw new RuntimeException("创建评价失败");
        }
    }

    @Override
    public Page<Review> getProductReviews(Long productId, int page, int size) {
        Page<Review> pageParam = new Page<>(page, size);
        return reviewMapper.selectProductReviews(pageParam, productId);
    }

    @Override
    public Page<Review> getUserReceivedReviews(Long userId, int page, int size) {
        Page<Review> pageParam = new Page<>(page, size);
        return reviewMapper.selectUserReceivedReviews(pageParam, userId);
    }

    @Override
    public Page<Review> getUserGivenReviews(Long userId, int page, int size) {
        Page<Review> pageParam = new Page<>(page, size);
        return reviewMapper.selectUserGivenReviews(pageParam, userId);
    }

    @Override
    public boolean canReview(Long orderId, Long reviewerId, String type) {
        int count = reviewMapper.selectReviewCount(orderId, reviewerId, type);
        return count == 0;
    }

    @Override
    public Double getUserAverageRating(Long userId) {
        Double rating = reviewMapper.selectUserAverageRating(userId);
        return rating != null ? Math.round(rating * 10.0) / 10.0 : 0.0;
    }

    @Override
    public ReviewStats getUserReviewStats(Long userId) {
        ReviewStats stats = reviewMapper.selectUserReviewStats(userId);
        if (stats == null) {
            stats = new ReviewStats();
            stats.setTotalCount(0L);
            stats.setAverageRating(0.0);
            stats.setFiveStarCount(0L);
            stats.setFourStarCount(0L);
            stats.setThreeStarCount(0L);
            stats.setTwoStarCount(0L);
            stats.setOneStarCount(0L);
        } else if (stats.getAverageRating() != null) {
            stats.setAverageRating(Math.round(stats.getAverageRating() * 10.0) / 10.0);
        }
        return stats;
    }

    @Override
    public Double getProductAverageRating(Long productId) {
        Double rating = reviewMapper.selectProductAverageRating(productId);
        return rating != null ? Math.round(rating * 10.0) / 10.0 : 0.0;
    }

    @Override
    @Transactional
    public boolean deleteReview(Long reviewId, Long userId) {
        // 只能删除自己发出的评价
        LambdaQueryWrapper<Review> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Review::getId, reviewId)
                   .eq(Review::getReviewerId, userId)
                   .eq(Review::getDeleted, 0);
        
        Review review = reviewMapper.selectOne(queryWrapper);
        if (review == null) {
            throw new RuntimeException("评价不存在或无权限删除");
        }

        // 软删除
        LambdaUpdateWrapper<Review> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Review::getId, reviewId)
                    .set(Review::getDeleted, 1)
                    .set(Review::getUpdatedAt, LocalDateTime.now());
        
        int result = reviewMapper.update(null, updateWrapper);
        if (result > 0) {
            log.info("用户{}删除评价{}成功", userId, reviewId);
        }
        return result > 0;
    }
}