package com.campus.secondhand.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.entity.Img;
import com.campus.secondhand.entity.Product;
import com.campus.secondhand.entity.User;
import com.campus.secondhand.mapper.ProductManageMapper;
import com.campus.secondhand.mapper.UserMapper;
import com.campus.secondhand.service.ImgService;
import com.campus.secondhand.service.ProductManageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StringUtils;
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
    private final ObjectMapper objectMapper;
    private final ImgService imgService;

    @Override
    public Page<Product> getProductList(int page, int size, String keyword, String status, 
                                       String auditStatus, Long categoryId, Long userId) {
        log.info("分页查询商品列表: page={}, size={}, keyword={}, status={}, auditStatus={}, categoryId={}, userId={}", 
                page, size, keyword, status, auditStatus, categoryId, userId);
        
        Page<Product> pageParam = new Page<>(page, size);
        return productManageMapper.selectProductList(pageParam, keyword, status, auditStatus, categoryId, userId);
    }

    @Override
    public java.util.Map<String, Object> getPendingProducts(int page, int size) {
        log.info("管理员查询待审核商品列表: page={}, size={}", page, size);
        
        Page<Product> pageParam = new Page<>(page, size);
        Page<Product> result = productManageMapper.selectPendingProducts(pageParam);
        
        // 处理商品数据，加载图片的base64数据
         List<Product> processedRecords = result.getRecords().stream().map(product -> {
             loadProductImages(product);
             return product;
         }).collect(java.util.stream.Collectors.toList());
        
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("records", processedRecords);
        response.put("total", result.getTotal());
        response.put("current", result.getCurrent());
        response.put("size", result.getSize());
        response.put("pages", result.getPages());
        
        return response;
    }

    /**
     * 为商品加载图片数据
     * 从images字段中的图片ID集合获取对应的base64数据
     */
    private void loadProductImages(Product product) {
        log.info("开始加载商品图片数据: productId={}, images字段={}", product.getId(), product.getImages());
        
        if (product != null && StringUtils.hasText(product.getImages())) {
            try {
                // 解析图片ID集合
                List<Long> imageIds = objectMapper.readValue(product.getImages(), 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Long.class));
                log.info("解析到图片ID列表: {}", imageIds);
                
                List<String> imageList = new java.util.ArrayList<>();
                for (Long imageId : imageIds) {
                    log.info("查询图片: imageId={}", imageId);
                    Img img = imgService.getImgById(imageId);
                    if (img != null && img.getStatus() == 1) { // 只获取正常状态的图片
                        log.info("找到图片: imageId={}, name={}, status={}", img.getId(), img.getName(), img.getStatus());
                        imageList.add(img.getBase64Data());
                        log.info("成功加载图片: imageId={}, dataLength={}", imageId, img.getBase64Data().length());
                    } else {
                        log.warn("图片不存在或状态异常: imageId={}, img={}", imageId, img);
                    }
                }
                
                product.setImageList(imageList);
                log.info("商品图片加载完成: productId={}, 图片数量={}", product.getId(), imageList.size());
            } catch (Exception e) {
                log.warn("解析商品{}的图片字段失败: {}", product.getId(), e.getMessage());
                product.setImageList(new java.util.ArrayList<>());
            }
        } else {
            product.setImageList(new java.util.ArrayList<>());
        }
    }

    @Override
    @Transactional
    public boolean approveProduct(Long productId) {
        log.info("审核通过商品: productId={}", productId);
        
        try {
            // 验证商品是否存在
            Product product = productManageMapper.selectById(productId);
            if (product == null || product.getDeleted() == 1) {
                log.warn("商品不存在或已删除: productId={}", productId);
                return false;
            }

            // 检查审核状态
            if (!Integer.valueOf(0).equals(product.getAuditStatus())) {
                log.warn("商品不是待审核状态: productId={}, auditStatus={}", productId, product.getAuditStatus());
                return false;
            }

            // 更新审核状态为通过
            product.setAuditStatus(1);
            product.setStatus("available");
            product.setUpdatedAt(java.time.LocalDateTime.now());
            
            int result = productManageMapper.updateById(product);
            if (result > 0) {
                log.info("商品审核通过成功: productId={}", productId);
                return true;
            }
            
            return false;
        } catch (Exception e) {
            log.error("审核通过商品失败: productId={}", productId, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean rejectProduct(Long productId, String reason) {
        log.info("审核拒绝商品: productId={}, reason={}", productId, reason);
        
        try {
            // 验证商品是否存在
            Product product = productManageMapper.selectById(productId);
            if (product == null || product.getDeleted() == 1) {
                log.warn("商品不存在或已删除: productId={}", productId);
                return false;
            }

            // 检查审核状态
            if (!Integer.valueOf(0).equals(product.getAuditStatus())) {
                log.warn("商品不是待审核状态: productId={}, auditStatus={}", productId, product.getAuditStatus());
                return false;
            }

            // 更新审核状态为拒绝
            product.setAuditStatus(2);
            product.setStatus("unavailable");
            product.setUpdatedAt(java.time.LocalDateTime.now());
            
            int result = productManageMapper.updateById(product);
            if (result > 0) {
                log.info("商品审核拒绝成功: productId={}, reason={}", productId, reason);
                return true;
            }
            
            return false;
        } catch (Exception e) {
            log.error("审核拒绝商品失败: productId={}, reason={}", productId, reason, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean removeProduct(Long productId, Long adminId, String reason) {
        log.info("下架违规商品: productId={}, adminId={}, reason={}", productId, adminId, reason);
        
        try {
            // 验证商品是否存在
            Product product = productManageMapper.selectById(productId);
            if (product == null || product.getDeleted() == 1) {
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
            if (product == null || product.getDeleted() == 1) {
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