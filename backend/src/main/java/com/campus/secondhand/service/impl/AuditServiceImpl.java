package com.campus.secondhand.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.entity.AuditLog;
import com.campus.secondhand.entity.Product;
import com.campus.secondhand.entity.Review;
import com.campus.secondhand.entity.User;
import com.campus.secondhand.mapper.AuditMapper;
import com.campus.secondhand.mapper.ProductMapper;
import com.campus.secondhand.mapper.ReviewMapper;
import com.campus.secondhand.mapper.UserMapper;
import com.campus.secondhand.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 审核服务实现类
 *
 * @author campus-secondhand
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditMapper auditMapper;
    private final ProductMapper productMapper;
    private final ReviewMapper reviewMapper;
    private final UserMapper userMapper;

    @Override
    public Page<Product> getPendingProducts(int page, int size, String keyword) {
        Page<Product> pageParam = new Page<>(page, size);
        return auditMapper.selectPendingProducts(pageParam, keyword);
    }

    @Override
    @Transactional
    public boolean auditProduct(Long productId, Long adminId, String status, String reason) {
        // 验证管理员
        User admin = userMapper.selectById(adminId);
        if (admin == null || !"admin".equals(admin.getRole())) {
            throw new RuntimeException("无管理员权限");
        }

        // 验证商品
        Product product = productMapper.selectById(productId);
        if (product == null || product.getDeleted() == 1) {
            throw new RuntimeException("商品不存在");
        }

        if (!"pending_review".equals(product.getStatus())) {
            throw new RuntimeException("商品不在待审核状态");
        }

        // 验证审核状态
        if (!"approved".equals(status) && !"rejected".equals(status)) {
            throw new RuntimeException("审核状态错误");
        }

        // 如果是拒绝，必须填写理由
        if ("rejected".equals(status) && (reason == null || reason.trim().isEmpty())) {
            throw new RuntimeException("拒绝审核必须填写理由");
        }

        // 更新商品状态
        String newStatus = "approved".equals(status) ? "on_sale" : "rejected";
        LambdaUpdateWrapper<Product> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Product::getId, productId)
                    .set(Product::getStatus, newStatus)
                    .set(Product::getUpdatedAt, LocalDateTime.now());
        
        int result = productMapper.update(null, updateWrapper);
        
        if (result > 0) {
            // 记录审核日志
            AuditLog auditLog = new AuditLog();
            auditLog.setType("product");
            auditLog.setTargetId(productId);
            auditLog.setAdminId(adminId);
            auditLog.setStatus(status);
            auditLog.setReason(reason);
            auditLog.setCreatedAt(LocalDateTime.now());
            auditMapper.insert(auditLog);
            
            log.info("管理员{}审核商品{}，状态: {}", adminId, productId, status);
        }
        
        return result > 0;
    }

    @Override
    @Transactional
    public int batchAuditProducts(List<Long> productIds, Long adminId, String status, String reason) {
        int successCount = 0;
        for (Long productId : productIds) {
            try {
                if (auditProduct(productId, adminId, status, reason)) {
                    successCount++;
                }
            } catch (Exception e) {
                log.warn("批量审核商品{}失败: {}", productId, e.getMessage());
            }
        }
        log.info("管理员{}批量审核商品，成功: {}/{}", adminId, successCount, productIds.size());
        return successCount;
    }

    @Override
    public Page<Review> getPendingReviews(int page, int size, String keyword) {
        Page<Review> pageParam = new Page<>(page, size);
        return auditMapper.selectPendingReviews(pageParam, keyword);
    }

    @Override
    @Transactional
    public boolean auditReview(Long reviewId, Long adminId, String status, String reason) {
        // 验证管理员
        User admin = userMapper.selectById(adminId);
        if (admin == null || !"admin".equals(admin.getRole())) {
            throw new RuntimeException("无管理员权限");
        }

        // 验证评价
        Review review = reviewMapper.selectById(reviewId);
        if (review == null || review.getDeleted() == 1) {
            throw new RuntimeException("评价不存在");
        }

        // 验证审核状态
        if (!"approved".equals(status) && !"rejected".equals(status)) {
            throw new RuntimeException("审核状态错误");
        }

        // 如果是拒绝，必须填写理由
        if ("rejected".equals(status) && (reason == null || reason.trim().isEmpty())) {
            throw new RuntimeException("拒绝审核必须填写理由");
        }

        // 更新评价状态（假设Review实体有status字段）
        LambdaUpdateWrapper<Review> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Review::getId, reviewId)
                    .set(Review::getUpdatedAt, LocalDateTime.now());
        
        // 如果拒绝，可以选择软删除或标记状态
        if ("rejected".equals(status)) {
            updateWrapper.set(Review::getDeleted, 1);
        }
        
        int result = reviewMapper.update(null, updateWrapper);
        
        if (result > 0) {
            // 记录审核日志
            AuditLog auditLog = new AuditLog();
            auditLog.setType("review");
            auditLog.setTargetId(reviewId);
            auditLog.setAdminId(adminId);
            auditLog.setStatus(status);
            auditLog.setReason(reason);
            auditLog.setCreatedAt(LocalDateTime.now());
            auditMapper.insert(auditLog);
            
            log.info("管理员{}审核评价{}，状态: {}", adminId, reviewId, status);
        }
        
        return result > 0;
    }

    @Override
    @Transactional
    public int batchAuditReviews(List<Long> reviewIds, Long adminId, String status, String reason) {
        int successCount = 0;
        for (Long reviewId : reviewIds) {
            try {
                if (auditReview(reviewId, adminId, status, reason)) {
                    successCount++;
                }
            } catch (Exception e) {
                log.warn("批量审核评价{}失败: {}", reviewId, e.getMessage());
            }
        }
        log.info("管理员{}批量审核评价，成功: {}/{}", adminId, successCount, reviewIds.size());
        return successCount;
    }

    @Override
    public AuditStats getAuditStats() {
        AuditStats stats = auditMapper.selectAuditStats();
        if (stats == null) {
            stats = new AuditStats();
            stats.setPendingProductCount(0L);
            stats.setPendingReviewCount(0L);
            stats.setTodayAuditedProductCount(0L);
            stats.setTodayAuditedReviewCount(0L);
            stats.setTotalAuditedProductCount(0L);
            stats.setTotalAuditedReviewCount(0L);
            stats.setApprovedProductCount(0L);
            stats.setRejectedProductCount(0L);
            stats.setApprovedReviewCount(0L);
            stats.setRejectedReviewCount(0L);
        }
        return stats;
    }

    @Override
    public Page<AuditRecord> getAuditHistory(int page, int size, String type, Long adminId) {
        Page<AuditRecord> pageParam = new Page<>(page, size);
        return auditMapper.selectAuditHistory(pageParam, type, adminId);
    }
}