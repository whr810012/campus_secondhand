package com.campus.secondhand.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.common.Result;
import com.campus.secondhand.entity.Review;
import com.campus.secondhand.service.ReviewService;
import com.campus.secondhand.service.ReviewService.ReviewStats;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 评价控制器
 *
 * @author campus-secondhand
 */
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 创建评价
     */
    @PostMapping
    public Result<Review> createReview(@RequestBody CreateReviewRequest request) {
        try {
            Review review = reviewService.createReview(
                request.getOrderId(),
                request.getReviewerId(),
                request.getReviewedId(),
                request.getProductId(),
                request.getRating(),
                request.getContent(),
                request.getType()
            );
            return Result.success(review);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 分页查询商品评价
     */
    @GetMapping("/product/{productId}")
    public Result<Page<Review>> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Review> reviews = reviewService.getProductReviews(productId, page, size);
        return Result.success(reviews);
    }

    /**
     * 分页查询用户收到的评价
     */
    @GetMapping("/user/{userId}/received")
    public Result<Page<Review>> getUserReceivedReviews(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Review> reviews = reviewService.getUserReceivedReviews(userId, page, size);
        return Result.success(reviews);
    }

    /**
     * 分页查询用户发出的评价
     */
    @GetMapping("/user/{userId}/given")
    public Result<Page<Review>> getUserGivenReviews(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Review> reviews = reviewService.getUserGivenReviews(userId, page, size);
        return Result.success(reviews);
    }

    /**
     * 检查是否可以评价
     */
    @GetMapping("/can-review")
    public Result<Boolean> canReview(
            @RequestParam Long orderId,
            @RequestParam Long reviewerId,
            @RequestParam String type) {
        boolean canReview = reviewService.canReview(orderId, reviewerId, type);
        return Result.success(canReview);
    }

    /**
     * 获取用户平均评分
     */
    @GetMapping("/user/{userId}/average-rating")
    public Result<Double> getUserAverageRating(@PathVariable Long userId) {
        Double rating = reviewService.getUserAverageRating(userId);
        return Result.success(rating);
    }

    /**
     * 获取用户评价统计
     */
    @GetMapping("/user/{userId}/stats")
    public Result<ReviewStats> getUserReviewStats(@PathVariable Long userId) {
        ReviewStats stats = reviewService.getUserReviewStats(userId);
        return Result.success(stats);
    }

    /**
     * 获取商品平均评分
     */
    @GetMapping("/product/{productId}/average-rating")
    public Result<Double> getProductAverageRating(@PathVariable Long productId) {
        Double rating = reviewService.getProductAverageRating(productId);
        return Result.success(rating);
    }

    /**
     * 删除评价
     */
    @DeleteMapping("/{reviewId}")
    public Result<Void> deleteReview(@PathVariable Long reviewId, @RequestParam Long userId) {
        try {
            boolean success = reviewService.deleteReview(reviewId, userId);
            if (success) {
                return Result.success();
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // 内部类定义请求参数
    public static class CreateReviewRequest {
        private Long orderId;
        private Long reviewerId;
        private Long reviewedId;
        private Long productId;
        private Integer rating;
        private String content;
        private String type;

        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }
        public Long getReviewerId() { return reviewerId; }
        public void setReviewerId(Long reviewerId) { this.reviewerId = reviewerId; }
        public Long getReviewedId() { return reviewedId; }
        public void setReviewedId(Long reviewedId) { this.reviewedId = reviewedId; }
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public Integer getRating() { return rating; }
        public void setRating(Integer rating) { this.rating = rating; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }
}