package com.campus.secondhand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.entity.AuditLog;
import com.campus.secondhand.entity.Product;
import com.campus.secondhand.entity.Review;
import com.campus.secondhand.service.AuditService.AuditRecord;
import com.campus.secondhand.service.AuditService.AuditStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 审核数据访问层
 *
 * @author campus-secondhand
 */
@Mapper
public interface AuditMapper extends BaseMapper<AuditLog> {

    /**
     * 分页查询待审核商品
     */
    @Select({
        "<script>",
        "SELECT p.*, u.nickname as sellerNickname, c.name as categoryName",
        "FROM products p",
        "LEFT JOIN users u ON p.seller_id = u.id",
        "LEFT JOIN categories c ON p.category_id = c.id",
        "WHERE p.deleted = 0 AND p.status = 'pending_review'",
        "<if test='keyword != null and keyword != \"\"'>",
        "  AND (p.title LIKE CONCAT('%', #{keyword}, '%') OR p.description LIKE CONCAT('%', #{keyword}, '%'))",
        "</if>",
        "ORDER BY p.created_at ASC",
        "</script>"
    })
    Page<Product> selectPendingProducts(Page<Product> page, @Param("keyword") String keyword);

    /**
     * 分页查询待审核评价
     */
    @Select({
        "<script>",
        "SELECT r.*, u1.nickname as reviewerNickname, u2.nickname as reviewedNickname, p.title as productTitle",
        "FROM reviews r",
        "LEFT JOIN users u1 ON r.reviewer_id = u1.id",
        "LEFT JOIN users u2 ON r.reviewed_id = u2.id",
        "LEFT JOIN products p ON r.product_id = p.id",
        "WHERE r.deleted = 0 AND r.status = 'pending_review'",
        "<if test='keyword != null and keyword != \"\"'>",
        "  AND (r.content LIKE CONCAT('%', #{keyword}, '%') OR p.title LIKE CONCAT('%', #{keyword}, '%'))",
        "</if>",
        "ORDER BY r.created_at ASC",
        "</script>"
    })
    Page<Review> selectPendingReviews(Page<Review> page, @Param("keyword") String keyword);

    /**
     * 获取审核统计数据
     */
    @Select({
        "SELECT",
        "  (SELECT COUNT(*) FROM products WHERE deleted = 0 AND status = 'pending_review') as pendingProductCount,",
        "  (SELECT COUNT(*) FROM reviews WHERE deleted = 0 AND status = 'pending_review') as pendingReviewCount,",
        "  (SELECT COUNT(*) FROM audit_logs WHERE type = 'product' AND DATE(created_at) = CURDATE()) as todayAuditedProductCount,",
        "  (SELECT COUNT(*) FROM audit_logs WHERE type = 'review' AND DATE(created_at) = CURDATE()) as todayAuditedReviewCount,",
        "  (SELECT COUNT(*) FROM audit_logs WHERE type = 'product') as totalAuditedProductCount,",
        "  (SELECT COUNT(*) FROM audit_logs WHERE type = 'review') as totalAuditedReviewCount,",
        "  (SELECT COUNT(*) FROM audit_logs WHERE type = 'product' AND status = 'approved') as approvedProductCount,",
        "  (SELECT COUNT(*) FROM audit_logs WHERE type = 'product' AND status = 'rejected') as rejectedProductCount,",
        "  (SELECT COUNT(*) FROM audit_logs WHERE type = 'review' AND status = 'approved') as approvedReviewCount,",
        "  (SELECT COUNT(*) FROM audit_logs WHERE type = 'review' AND status = 'rejected') as rejectedReviewCount"
    })
    AuditStats selectAuditStats();

    /**
     * 分页查询审核历史
     */
    @Select({
        "<script>",
        "SELECT",
        "  al.id,",
        "  al.type,",
        "  al.target_id as targetId,",
        "  CASE",
        "    WHEN al.type = 'product' THEN (SELECT title FROM products WHERE id = al.target_id)",
        "    WHEN al.type = 'review' THEN (SELECT CONCAT('评价: ', SUBSTRING(content, 1, 50)) FROM reviews WHERE id = al.target_id)",
        "    ELSE '未知'",
        "  END as targetTitle,",
        "  al.admin_id as adminId,",
        "  u.nickname as adminName,",
        "  al.status,",
        "  al.reason,",
        "  DATE_FORMAT(al.created_at, '%Y-%m-%d %H:%i:%s') as createdAt",
        "FROM audit_logs al",
        "LEFT JOIN users u ON al.admin_id = u.id",
        "WHERE 1=1",
        "<if test='type != null and type != \"\"'>",
        "  AND al.type = #{type}",
        "</if>",
        "<if test='adminId != null'>",
        "  AND al.admin_id = #{adminId}",
        "</if>",
        "ORDER BY al.created_at DESC",
        "</script>"
    })
    Page<AuditRecord> selectAuditHistory(Page<AuditRecord> page, 
                                        @Param("type") String type, 
                                        @Param("adminId") Long adminId);
}