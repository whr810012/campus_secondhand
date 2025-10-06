package com.campus.secondhand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.entity.Review;
import com.campus.secondhand.service.ReviewService.ReviewStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 评价数据访问层
 *
 * @author campus-secondhand
 */
@Mapper
public interface ReviewMapper extends BaseMapper<Review> {

    /**
     * 分页查询商品评价（包含评价者信息）
     */
    @Select({
        "SELECT r.*, u.nickname as reviewerNickname, u.avatar as reviewerAvatar,",
        "       (SELECT i.base64_data FROM imgs i WHERE i.id = u.avatar AND i.status = 1) as reviewerAvatarData",
        "FROM reviews r",
        "LEFT JOIN users u ON r.reviewer_id = u.id",
        "WHERE r.deleted = 0 AND r.product_id = #{productId}",
        "ORDER BY r.created_at DESC"
    })
    Page<Review> selectProductReviews(Page<Review> page, @Param("productId") Long productId);

    /**
     * 分页查询用户收到的评价（包含评价者和商品信息）
     */
    @Select({
        "SELECT r.*, u.nickname as reviewerNickname, u.avatar as reviewerAvatar,",
        "       (SELECT i.base64_data FROM imgs i WHERE i.id = u.avatar AND i.status = 1) as reviewerAvatarData,",
        "       p.title as productTitle, p.images as productImages",
        "FROM reviews r",
        "LEFT JOIN users u ON r.reviewer_id = u.id",
        "LEFT JOIN products p ON r.product_id = p.id",
        "WHERE r.deleted = 0 AND r.reviewed_id = #{userId}",
        "ORDER BY r.created_at DESC"
    })
    Page<Review> selectUserReceivedReviews(Page<Review> page, @Param("userId") Long userId);

    /**
     * 分页查询用户发出的评价（包含被评价者和商品信息）
     */
    @Select({
        "SELECT r.*, u.nickname as reviewedNickname, u.avatar as reviewedAvatar,",
        "       (SELECT i.base64_data FROM imgs i WHERE i.id = u.avatar AND i.status = 1) as reviewedAvatarData,",
        "       p.title as productTitle, p.images as productImages",
        "FROM reviews r",
        "LEFT JOIN users u ON r.reviewed_id = u.id",
        "LEFT JOIN products p ON r.product_id = p.id",
        "WHERE r.deleted = 0 AND r.reviewer_id = #{userId}",
        "ORDER BY r.created_at DESC"
    })
    Page<Review> selectUserGivenReviews(Page<Review> page, @Param("userId") Long userId);

    /**
     * 获取用户平均评分
     */
    @Select("SELECT AVG(rating) FROM reviews WHERE deleted = 0 AND reviewed_id = #{userId}")
    Double selectUserAverageRating(@Param("userId") Long userId);

    /**
     * 获取商品平均评分
     */
    @Select("SELECT AVG(rating) FROM reviews WHERE deleted = 0 AND product_id = #{productId}")
    Double selectProductAverageRating(@Param("productId") Long productId);

    /**
     * 获取用户评价统计
     */
    @Select({
        "SELECT",
        "  COUNT(*) as totalCount,",
        "  AVG(rating) as averageRating,",
        "  SUM(CASE WHEN rating = 5 THEN 1 ELSE 0 END) as fiveStarCount,",
        "  SUM(CASE WHEN rating = 4 THEN 1 ELSE 0 END) as fourStarCount,",
        "  SUM(CASE WHEN rating = 3 THEN 1 ELSE 0 END) as threeStarCount,",
        "  SUM(CASE WHEN rating = 2 THEN 1 ELSE 0 END) as twoStarCount,",
        "  SUM(CASE WHEN rating = 1 THEN 1 ELSE 0 END) as oneStarCount",
        "FROM reviews",
        "WHERE deleted = 0 AND reviewed_id = #{userId}"
    })
    ReviewStats selectUserReviewStats(@Param("userId") Long userId);

    /**
     * 检查是否已评价
     */
    @Select({
        "SELECT COUNT(*) FROM reviews",
        "WHERE deleted = 0",
        "AND order_id = #{orderId}",
        "AND reviewer_id = #{reviewerId}",
        "AND type = #{type}"
    })
    int selectReviewCount(@Param("orderId") Long orderId, 
                         @Param("reviewerId") Long reviewerId, 
                         @Param("type") String type);
}