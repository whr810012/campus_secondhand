package com.campus.secondhand.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.entity.Review;

/**
 * 评价服务接口
 *
 * @author campus-secondhand
 */
public interface ReviewService {

    /**
     * 创建评价
     *
     * @param orderId 订单ID
     * @param reviewerId 评价者ID
     * @param reviewedId 被评价者ID
     * @param productId 商品ID
     * @param rating 评分（1-5）
     * @param content 评价内容
     * @param type 评价类型（buyer_to_seller, seller_to_buyer）
     * @return 评价
     */
    Review createReview(Long orderId, Long reviewerId, Long reviewedId, Long productId, 
                       Integer rating, String content, String type);

    /**
     * 分页查询商品评价
     *
     * @param productId 商品ID
     * @param page 页码
     * @param size 每页大小
     * @return 评价分页数据
     */
    Page<Review> getProductReviews(Long productId, int page, int size);

    /**
     * 分页查询用户收到的评价
     *
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 评价分页数据
     */
    Page<Review> getUserReceivedReviews(Long userId, int page, int size);

    /**
     * 分页查询用户发出的评价
     *
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 评价分页数据
     */
    Page<Review> getUserGivenReviews(Long userId, int page, int size);

    /**
     * 检查是否可以评价
     *
     * @param orderId 订单ID
     * @param reviewerId 评价者ID
     * @param type 评价类型
     * @return 是否可以评价
     */
    boolean canReview(Long orderId, Long reviewerId, String type);

    /**
     * 获取用户平均评分
     *
     * @param userId 用户ID
     * @return 平均评分
     */
    Double getUserAverageRating(Long userId);

    /**
     * 获取用户评价统计
     *
     * @param userId 用户ID
     * @return 评价统计
     */
    ReviewStats getUserReviewStats(Long userId);

    /**
     * 获取商品平均评分
     *
     * @param productId 商品ID
     * @return 平均评分
     */
    Double getProductAverageRating(Long productId);

    /**
     * 删除评价（软删除）
     *
     * @param reviewId 评价ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean deleteReview(Long reviewId, Long userId);

    /**
     * 评价统计
     */
    class ReviewStats {
        private Long totalCount;
        private Double averageRating;
        private Long fiveStarCount;
        private Long fourStarCount;
        private Long threeStarCount;
        private Long twoStarCount;
        private Long oneStarCount;

        // Getters and Setters
        public Long getTotalCount() { return totalCount; }
        public void setTotalCount(Long totalCount) { this.totalCount = totalCount; }
        public Double getAverageRating() { return averageRating; }
        public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }
        public Long getFiveStarCount() { return fiveStarCount; }
        public void setFiveStarCount(Long fiveStarCount) { this.fiveStarCount = fiveStarCount; }
        public Long getFourStarCount() { return fourStarCount; }
        public void setFourStarCount(Long fourStarCount) { this.fourStarCount = fourStarCount; }
        public Long getThreeStarCount() { return threeStarCount; }
        public void setThreeStarCount(Long threeStarCount) { this.threeStarCount = threeStarCount; }
        public Long getTwoStarCount() { return twoStarCount; }
        public void setTwoStarCount(Long twoStarCount) { this.twoStarCount = twoStarCount; }
        public Long getOneStarCount() { return oneStarCount; }
        public void setOneStarCount(Long oneStarCount) { this.oneStarCount = oneStarCount; }
    }
}