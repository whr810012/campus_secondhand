package com.campus.secondhand.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.entity.Product;
import com.campus.secondhand.entity.User;
import com.campus.secondhand.mapper.ProductManageMapper;
import com.campus.secondhand.mapper.UserMapper;
import com.campus.secondhand.service.ProductManageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品管理服务实现类
 *
 * @author campus-secondhand
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductManageServiceImpl implements ProductManageService {

    private final ProductManageMapper productManageMapper;
    private final UserMapper userMapper;

    @Override
    public Page<Product> getProductList(int page, int size, String keyword, String status, 
                                       String auditStatus, Long categoryId, Long userId) {
        log.info("分页查询商品列表: page={}, size={}, keyword={}, status={}, auditStatus={}, categoryId={}, userId={}", 
                page, size, keyword, status, auditStatus, categoryId, userId);
        
        Page<Product> pageParam = new Page<>(page, size);
        return productManageMapper.selectProductList(pageParam, keyword, status, auditStatus, categoryId, userId);
    }

    @Override
    @Transactional
    public boolean removeProduct(Long productId, Long adminId, String reason) {
        log.info("下架违规商品: productId={}, adminId={}, reason={}", productId, adminId, reason);
        
        try {
            // 验证商品是否存在
            Product product = productManageMapper.selectById(productId);
            if (product == null || product.getDeleted()) {
                log.warn("商品不存在或已删除: productId={}", productId);
                return false;
            }

            // 验证管理员权限
            User admin = userMapper.selectById(adminId);
            if (admin == null || !"ADMIN".equals(admin.getRole())) {
                log.warn("管理员权限验证失败: adminId={}", adminId);
                return false;
            }

            // 检查商品状态
            if ("REMOVED".equals(product.getStatus())) {
                log.warn("商品已经是下架状态: productId={}", productId);
                return false;
            }

            LocalDateTime removedAt = LocalDateTime.now();
            int result = productManageMapper.removeProduct(productId, reason, removedAt);
            if (result > 0) {
                log.info("商品下架成功: productId={}", productId);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("下架商品失败: productId={}", productId, e);
            throw new RuntimeException("下架商品失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public int batchRemoveProducts(List<Long> productIds, Long adminId, String reason) {
        log.info("批量下架违规商品: productIds={}, adminId={}, reason={}", productIds, adminId, reason);
        
        if (productIds == null || productIds.isEmpty()) {
            return 0;
        }

        try {
            // 验证管理员权限
            User admin = userMapper.selectById(adminId);
            if (admin == null || !"ADMIN".equals(admin.getRole())) {
                log.warn("管理员权限验证失败: adminId={}", adminId);
                throw new RuntimeException("管理员权限验证失败");
            }

            LocalDateTime removedAt = LocalDateTime.now();
            int result = productManageMapper.batchRemoveProducts(productIds, reason, removedAt);
            log.info("批量下架商品完成: 成功数量={}", result);
            return result;
        } catch (Exception e) {
            log.error("批量下架商品失败: productIds={}", productIds, e);
            throw new RuntimeException("批量下架商品失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean restoreProduct(Long productId, Long adminId) {
        log.info("恢复商品: productId={}, adminId={}", productId, adminId);
        
        try {
            // 验证商品是否存在
            Product product = productManageMapper.selectById(productId);
            if (product == null || product.getDeleted()) {
                log.warn("商品不存在或已删除: productId={}", productId);
                return false;
            }

            // 验证管理员权限
            User admin = userMapper.selectById(adminId);
            if (admin == null || !"ADMIN".equals(admin.getRole())) {
                log.warn("管理员权限验证失败: adminId={}", adminId);
                return false;
            }

            // 检查商品状态
            if (!"REMOVED".equals(product.getStatus())) {
                log.warn("商品不是下架状态，无法恢复: productId={}, status={}", productId, product.getStatus());
                return false;
            }

            int result = productManageMapper.restoreProduct(productId);
            if (result > 0) {
                log.info("商品恢复成功: productId={}", productId);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("恢复商品失败: productId={}", productId, e);
            throw new RuntimeException("恢复商品失败: " + e.getMessage());
        }
    }

    @Override
    public ProductDetail getProductDetail(Long productId) {
        log.info("获取商品详细信息: productId={}", productId);
        
        try {
            ProductDetail productDetail = productManageMapper.selectProductDetail(productId);
            if (productDetail == null) {
                log.warn("商品不存在: productId={}", productId);
                return null;
            }
            return productDetail;
        } catch (Exception e) {
            log.error("获取商品详细信息失败: productId={}", productId, e);
            throw new RuntimeException("获取商品详细信息失败: " + e.getMessage());
        }
    }

    @Override
    public ProductStats getProductStats() {
        log.info("获取商品统计信息");
        
        try {
            ProductStats stats = productManageMapper.selectProductStats();
            if (stats == null) {
                stats = new ProductStats();
            }
            return stats;
        } catch (Exception e) {
            log.error("获取商品统计信息失败", e);
            throw new RuntimeException("获取商品统计信息失败: " + e.getMessage());
        }
    }

    @Override
    public Page<Product> getViolationProducts(int page, int size, String keyword) {
        log.info("分页查询违规商品: page={}, size={}, keyword={}", page, size, keyword);
        
        Page<Product> pageParam = new Page<>(page, size);
        return productManageMapper.selectViolationProducts(pageParam, keyword);
    }

    @Override
    public Page<ProductPopularity> getPopularProducts(int page, int size, int days) {
        log.info("分页查询热门商品: page={}, size={}, days={}", page, size, days);
        
        // 默认统计最近7天的数据
        if (days <= 0) {
            days = 7;
        }
        
        Page<ProductPopularity> pageParam = new Page<>(page, size);
        return productManageMapper.selectPopularProducts(pageParam, days);
    }

    @Override
    @Transactional
    public boolean forceDeleteProduct(Long productId, Long adminId, String reason) {
        log.info("强制删除商品: productId={}, adminId={}, reason={}", productId, adminId, reason);
        
        try {
            // 验证商品是否存在
            Product product = productManageMapper.selectById(productId);
            if (product == null) {
                log.warn("商品不存在: productId={}", productId);
                return false;
            }

            // 验证管理员权限
            User admin = userMapper.selectById(adminId);
            if (admin == null || !"ADMIN".equals(admin.getRole())) {
                log.warn("管理员权限验证失败: adminId={}", adminId);
                return false;
            }

            // 删除相关数据
            productManageMapper.deleteFavoritesByProductId(productId);
            productManageMapper.deleteViewsByProductId(productId);
            productManageMapper.deleteMessagesByProductId(productId);
            
            // 物理删除商品
            int result = productManageMapper.forceDeleteProduct(productId);
            if (result > 0) {
                log.info("商品强制删除成功: productId={}", productId);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("强制删除商品失败: productId={}", productId, e);
            throw new RuntimeException("强制删除商品失败: " + e.getMessage());
        }
    }
}