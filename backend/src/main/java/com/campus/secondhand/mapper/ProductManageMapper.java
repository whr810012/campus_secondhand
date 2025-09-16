package com.campus.secondhand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.entity.Product;
import com.campus.secondhand.service.ProductManageService.ProductDetail;
import com.campus.secondhand.service.ProductManageService.ProductPopularity;
import com.campus.secondhand.service.ProductManageService.ProductStats;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品管理数据访问层
 *
 * @author campus-secondhand
 */
@Mapper
public interface ProductManageMapper extends BaseMapper<Product> {

    /**
     * 分页查询商品列表
     */
    @Select({
        "<script>",
        "SELECT p.*, u.username, c.name as category_name",
        "FROM products p",
        "LEFT JOIN users u ON p.user_id = u.id",
        "LEFT JOIN categories c ON p.category_id = c.id",
        "WHERE p.deleted = 0",
        "<if test='keyword != null and keyword != \"\"'>",
        "  AND (p.title LIKE CONCAT('%', #{keyword}, '%')",
        "    OR p.description LIKE CONCAT('%', #{keyword}, '%')",
        "    OR u.username LIKE CONCAT('%', #{keyword}, '%'))",
        "</if>",
        "<if test='status != null and status != \"\"'>",
        "  AND p.status = #{status}",
        "</if>",
        "<if test='auditStatus != null and auditStatus != \"\"'>",
        "  AND p.audit_status = #{auditStatus}",
        "</if>",
        "<if test='categoryId != null'>",
        "  AND p.category_id = #{categoryId}",
        "</if>",
        "<if test='userId != null'>",
        "  AND p.user_id = #{userId}",
        "</if>",
        "ORDER BY p.created_at DESC",
        "</script>"
    })
    Page<Product> selectProductList(Page<Product> page,
                                   @Param("keyword") String keyword,
                                   @Param("status") String status,
                                   @Param("auditStatus") String auditStatus,
                                   @Param("categoryId") Long categoryId,
                                   @Param("userId") Long userId);

    /**
     * 下架商品
     */
    @Update({
        "UPDATE products SET ",
        "  status = 'REMOVED',",
        "  remove_reason = #{reason},",
        "  removed_at = #{removedAt, jdbcType=TIMESTAMP},",
        "  updated_at = NOW()",
        "WHERE id = #{productId} AND deleted = 0"
    })
    int removeProduct(@Param("productId") Long productId, 
                     @Param("reason") String reason, 
                     @Param("removedAt") LocalDateTime removedAt);

    /**
     * 批量下架商品
     */
    @Update({
        "<script>",
        "UPDATE products SET ",
        "  status = 'REMOVED',",
        "  remove_reason = #{reason},",
        "  removed_at = #{removedAt, jdbcType=TIMESTAMP},",
        "  updated_at = NOW()",
        "WHERE deleted = 0 AND id IN",
        "<foreach collection='productIds' item='productId' open='(' separator=',' close=')'>",
        "  #{productId}",
        "</foreach>",
        "</script>"
    })
    int batchRemoveProducts(@Param("productIds") List<Long> productIds, 
                           @Param("reason") String reason, 
                           @Param("removedAt") LocalDateTime removedAt);

    /**
     * 恢复商品
     */
    @Update({
        "UPDATE products SET ",
        "  status = 'ACTIVE',",
        "  remove_reason = NULL,",
        "  removed_at = NULL,",
        "  updated_at = NOW()",
        "WHERE id = #{productId} AND deleted = 0"
    })
    int restoreProduct(@Param("productId") Long productId);

    /**
     * 获取商品详细信息
     */
    @Select({
        "SELECT p.*, u.username, u.phone as user_phone,",
        "  (SELECT COUNT(*) FROM product_views pv WHERE pv.product_id = p.id) as view_count,",
        "  (SELECT COUNT(*) FROM favorites f WHERE f.product_id = p.id AND f.deleted = 0) as favorite_count,",
        "  (SELECT COUNT(*) FROM messages m WHERE m.product_id = p.id AND m.deleted = 0) as inquiry_count,",
        "  (SELECT MAX(pv.created_at) FROM product_views pv WHERE pv.product_id = p.id) as last_view_time",
        "FROM products p",
        "LEFT JOIN users u ON p.user_id = u.id",
        "WHERE p.id = #{productId} AND p.deleted = 0"
    })
    @Results({
        @Result(column = "id", property = "product.id"),
        @Result(column = "title", property = "product.title"),
        @Result(column = "description", property = "product.description"),
        @Result(column = "price", property = "product.price"),
        @Result(column = "original_price", property = "product.originalPrice"),
        @Result(column = "condition_desc", property = "product.conditionDesc"),
        @Result(column = "images", property = "product.images"),
        @Result(column = "location", property = "product.location"),
        @Result(column = "contact_info", property = "product.contactInfo"),
        @Result(column = "status", property = "product.status"),
        @Result(column = "audit_status", property = "product.auditStatus"),
        @Result(column = "remove_reason", property = "removeReason"),
        @Result(column = "removed_at", property = "removedAt", jdbcType = JdbcType.TIMESTAMP),
        @Result(column = "created_at", property = "product.createdAt"),
        @Result(column = "updated_at", property = "product.updatedAt"),
        @Result(column = "username", property = "userName"),
        @Result(column = "user_phone", property = "userPhone"),
        @Result(column = "view_count", property = "viewCount"),
        @Result(column = "favorite_count", property = "favoriteCount"),
        @Result(column = "inquiry_count", property = "inquiryCount"),
        @Result(column = "last_view_time", property = "lastViewTime", jdbcType = JdbcType.TIMESTAMP)
    })
    ProductDetail selectProductDetail(@Param("productId") Long productId);

    /**
     * 获取商品统计信息
     */
    @Select({
        "SELECT ",
        "  COUNT(*) as total_products,",
        "  SUM(CASE WHEN status = 'ACTIVE' THEN 1 ELSE 0 END) as active_products,",
        "  SUM(CASE WHEN status = 'SOLD' THEN 1 ELSE 0 END) as sold_products,",
        "  SUM(CASE WHEN status = 'REMOVED' THEN 1 ELSE 0 END) as removed_products,",
        "  SUM(CASE WHEN audit_status = 'PENDING' THEN 1 ELSE 0 END) as pending_audit_products,",
        "  SUM(CASE WHEN audit_status = 'REJECTED' THEN 1 ELSE 0 END) as rejected_products,",
        "  SUM(CASE WHEN DATE(created_at) = CURDATE() THEN 1 ELSE 0 END) as today_new_products,",
        "  SUM(CASE WHEN YEARWEEK(created_at) = YEARWEEK(NOW()) THEN 1 ELSE 0 END) as week_new_products,",
        "  SUM(CASE WHEN YEAR(created_at) = YEAR(NOW()) AND MONTH(created_at) = MONTH(NOW()) THEN 1 ELSE 0 END) as month_new_products,",
        "  SUM(CASE WHEN status = 'REMOVED' AND remove_reason IS NOT NULL THEN 1 ELSE 0 END) as violation_products",
        "FROM products",
        "WHERE deleted = 0"
    })
    @Results({
        @Result(column = "total_products", property = "totalProducts"),
        @Result(column = "active_products", property = "activeProducts"),
        @Result(column = "sold_products", property = "soldProducts"),
        @Result(column = "removed_products", property = "removedProducts"),
        @Result(column = "pending_audit_products", property = "pendingAuditProducts"),
        @Result(column = "rejected_products", property = "rejectedProducts"),
        @Result(column = "today_new_products", property = "todayNewProducts"),
        @Result(column = "week_new_products", property = "weekNewProducts"),
        @Result(column = "month_new_products", property = "monthNewProducts"),
        @Result(column = "violation_products", property = "violationProducts")
    })
    ProductStats selectProductStats();

    /**
     * 分页查询违规商品
     */
    @Select({
        "<script>",
        "SELECT p.*, u.username",
        "FROM products p",
        "LEFT JOIN users u ON p.user_id = u.id",
        "WHERE p.deleted = 0 AND p.status = 'REMOVED' AND p.remove_reason IS NOT NULL",
        "<if test='keyword != null and keyword != \"\"'>",
        "  AND (p.title LIKE CONCAT('%', #{keyword}, '%')",
        "    OR p.remove_reason LIKE CONCAT('%', #{keyword}, '%')",
        "    OR u.username LIKE CONCAT('%', #{keyword}, '%'))",
        "</if>",
        "ORDER BY p.removed_at DESC",
        "</script>"
    })
    Page<Product> selectViolationProducts(Page<Product> page, @Param("keyword") String keyword);

    /**
     * 分页查询热门商品
     */
    @Select({
        "<script>",
        "SELECT p.*, u.username,",
        "  COALESCE(view_stats.view_count, 0) as view_count,",
        "  COALESCE(favorite_stats.favorite_count, 0) as favorite_count,",
        "  COALESCE(inquiry_stats.inquiry_count, 0) as inquiry_count,",
        "  (COALESCE(view_stats.view_count, 0) * 1.0 + ",
        "   COALESCE(favorite_stats.favorite_count, 0) * 3.0 + ",
        "   COALESCE(inquiry_stats.inquiry_count, 0) * 2.0) as popularity_score",
        "FROM products p",
        "LEFT JOIN users u ON p.user_id = u.id",
        "LEFT JOIN (",
        "  SELECT product_id, COUNT(*) as view_count",
        "  FROM product_views",
        "  WHERE created_at >= DATE_SUB(NOW(), INTERVAL #{days} DAY)",
        "  GROUP BY product_id",
        ") view_stats ON p.id = view_stats.product_id",
        "LEFT JOIN (",
        "  SELECT product_id, COUNT(*) as favorite_count",
        "  FROM favorites",
        "  WHERE deleted = 0 AND created_at >= DATE_SUB(NOW(), INTERVAL #{days} DAY)",
        "  GROUP BY product_id",
        ") favorite_stats ON p.id = favorite_stats.product_id",
        "LEFT JOIN (",
        "  SELECT product_id, COUNT(*) as inquiry_count",
        "  FROM messages",
        "  WHERE deleted = 0 AND created_at >= DATE_SUB(NOW(), INTERVAL #{days} DAY)",
        "  GROUP BY product_id",
        ") inquiry_stats ON p.id = inquiry_stats.product_id",
        "WHERE p.deleted = 0 AND p.status = 'ACTIVE'",
        "ORDER BY popularity_score DESC",
        "</script>"
    })
    @Results({
        @Result(column = "id", property = "product.id"),
        @Result(column = "title", property = "product.title"),
        @Result(column = "description", property = "product.description"),
        @Result(column = "price", property = "product.price"),
        @Result(column = "images", property = "product.images"),
        @Result(column = "status", property = "product.status"),
        @Result(column = "created_at", property = "product.createdAt"),
        @Result(column = "view_count", property = "viewCount"),
        @Result(column = "favorite_count", property = "favoriteCount"),
        @Result(column = "inquiry_count", property = "inquiryCount"),
        @Result(column = "popularity_score", property = "popularityScore")
    })
    Page<ProductPopularity> selectPopularProducts(Page<ProductPopularity> page, @Param("days") int days);

    /**
     * 强制删除商品（物理删除）
     */
    @Delete("DELETE FROM products WHERE id = #{productId}")
    int forceDeleteProduct(@Param("productId") Long productId);

    /**
     * 删除商品相关的收藏记录
     */
    @Delete("DELETE FROM favorites WHERE product_id = #{productId}")
    int deleteFavoritesByProductId(@Param("productId") Long productId);

    /**
     * 删除商品相关的浏览记录
     */
    @Delete("DELETE FROM product_views WHERE product_id = #{productId}")
    int deleteViewsByProductId(@Param("productId") Long productId);

    /**
     * 删除商品相关的消息记录
     */
    @Update("UPDATE messages SET deleted = 1 WHERE product_id = #{productId}")
    int deleteMessagesByProductId(@Param("productId") Long productId);
}