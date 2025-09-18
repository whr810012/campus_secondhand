package com.campus.secondhand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.entity.Product;
import com.campus.secondhand.entity.Img;
import com.campus.secondhand.mapper.ProductMapper;
import com.campus.secondhand.service.ProductService;
import com.campus.secondhand.service.ImgService;
import com.campus.secondhand.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import java.time.LocalDateTime;

/**
 * 商品服务实现类
 *
 * @author campus-secondhand
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final JwtUtil jwtUtil;
    private final ImgService imgService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Page<Product> getProductPage(int page, int size, String keyword, String sortBy) {
        Page<Product> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        
        // 只查询未删除的商品
        queryWrapper.eq(Product::getDeleted, 0);
        
        // 关键词搜索
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                .like(Product::getTitle, keyword)
                .or()
                .like(Product::getDescription, keyword)
            );
        }
        
        // 排序
        if ("latest".equals(sortBy)) {
            queryWrapper.orderByDesc(Product::getCreatedAt);
        } else if ("price_asc".equals(sortBy)) {
            queryWrapper.orderByAsc(Product::getPrice);
        } else if ("price_desc".equals(sortBy)) {
            queryWrapper.orderByDesc(Product::getPrice);
        } else {
            // 默认按创建时间倒序
            queryWrapper.orderByDesc(Product::getCreatedAt);
        }
        
        Page<Product> result = productMapper.selectPage(pageParam, queryWrapper);
        
        // 为每个商品加载图片数据
        if (result.getRecords() != null) {
            for (Product product : result.getRecords()) {
                loadProductData(product);
            }
        }
        
        return result;
    }

    @Override
    public Product getProductById(Long id) {
        Product product = productMapper.selectById(id);
        if (product != null) {
            // 加载图片数据
            loadProductData(product);
        }
        return product;
    }

    /**
     * 为商品加载图片数据
     * 从images字段中的图片ID集合获取对应的base64数据
     */
    private void loadProductData(Product product) {
        // 加载图片数据
        if (product != null && StringUtils.hasText(product.getImages())) {
            log.info("开始加载商品图片: productId={}, images={}, 当前imageList={}", product.getId(), product.getImages(), product.getImageList());
            try {
                // 解析图片ID集合
                List<Long> imageIds = objectMapper.readValue(product.getImages(), 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Long.class));
                log.info("解析到图片ID列表: {}", imageIds);
                
                List<String> imageList = new ArrayList<>();
                for (Long imageId : imageIds) {
                    Img img = imgService.getById(imageId);
                    if (img != null && img.getStatus() == 1) { // 只获取正常状态的图片
                        imageList.add(img.getBase64Data());
                        log.info("成功加载图片: imageId={}, dataLength={}", imageId, img.getBase64Data().length());
                    } else {
                        log.warn("图片不存在或状态异常: imageId={}, img={}", imageId, img);
                    }
                }
                product.setImageList(imageList);
                log.info("设置图片列表完成: imageList大小={}, 内容前100字符={}", imageList.size(), 
                    imageList.isEmpty() ? "空" : imageList.get(0).substring(0, Math.min(100, imageList.get(0).length())));
            } catch (Exception e) {
                log.error("加载商品图片失败: productId={}, images={}, error={}", product.getId(), product.getImages(), e.getMessage(), e);
                // 设置空列表，确保有值返回
                product.setImageList(new ArrayList<>());
            }
        } else {
            log.info("商品无图片数据: productId={}, images={}, 当前imageList={}", 
                product != null ? product.getId() : "null", 
                product != null ? product.getImages() : "null",
                product != null ? product.getImageList() : "null");
            if (product != null && product.getImageList() == null) {
                product.setImageList(new ArrayList<>());
            }
        }
        
        // 加载标签数据
        if (product != null && StringUtils.hasText(product.getTags())) {
            try {
                // 解析标签集合
                List<String> tagList = objectMapper.readValue(product.getTags(), 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
                product.setTagList(tagList);
            } catch (Exception e) {
                log.error("加载商品标签失败: productId={}, error={}", product.getId(), e.getMessage());
                product.setTagList(new ArrayList<>()); // 设置空列表避免null
            }
        }
    }

    /**
     * 映射前端状态值到数据库状态值
     * 前端: selling -> 数据库: available
     * 前端: sold -> 数据库: sold
     * 前端: offline -> 数据库: unavailable
     * 前端: pending -> 数据库: pending_review
     */
    private String mapFrontendStatusToDb(String frontendStatus) {
        switch (frontendStatus) {
            case "selling":
                return "available";
            case "sold":
                return "sold";
            case "offline":
                return "unavailable";
            case "pending":
                return "pending_review";
            default:
                return frontendStatus; // 如果没有匹配，返回原值
        }
    }

    @Override
    public Page<Product> getMyProducts(String token, int page, int size, String status) {
        log.info("开始获取用户商品列表: page={}, size={}, status={}", page, size, status);
        try {
            // 从token中获取用户ID
            Long userId = jwtUtil.getUserIdFromToken(token.replace("Bearer ", ""));
            log.info("从token解析用户ID: {}", userId);
            if (userId == null) {
                throw new RuntimeException("无效的token");
            }

            Page<Product> pageParam = new Page<>(page, size);
            LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
            
            // 查询当前用户的商品
            queryWrapper.eq(Product::getSellerId, userId);
            
            // 只查询未删除的商品
            queryWrapper.eq(Product::getDeleted, 0);
            
            // 状态过滤 - 映射前端状态值到数据库状态值
            if (StringUtils.hasText(status)) {
                String dbStatus = mapFrontendStatusToDb(status);
                if (dbStatus != null) {
                    queryWrapper.eq(Product::getStatus, dbStatus);
                }
            }
            
            // 按创建时间倒序
            queryWrapper.orderByDesc(Product::getCreatedAt);
            
            Page<Product> result = productMapper.selectPage(pageParam, queryWrapper);
            
            // 为每个商品加载图片数据
            if (result.getRecords() != null) {
                for (Product product : result.getRecords()) {
                    loadProductData(product);
                }
            }
            
            return result;
        } catch (Exception e) {
            log.error("获取用户商品列表失败: {}", e.getMessage());
            throw new RuntimeException("获取用户商品列表失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Product createProduct(Product product, String token) {
        try {
            // 从token中获取用户ID
            Long userId = jwtUtil.getUserIdFromToken(token.replace("Bearer ", ""));
            if (userId == null) {
                throw new RuntimeException("无效的token");
            }

            // 处理图片数据：将base64图片存储到imgs表，获取图片ID集合
            List<Long> imageIds = new ArrayList<>();
            
            // 首先尝试从imageList获取图片数据（用于兼容）
            List<String> imagesToProcess = new ArrayList<>();
            if (product.getImageList() != null && !product.getImageList().isEmpty()) {
                imagesToProcess.addAll(product.getImageList());
            }
            // 如果imageList为空，尝试从images字段解析
            else if (StringUtils.hasText(product.getImages())) {
                try {
                    // 尝试解析为字符串数组（前端发送的base64数组）
                    List<String> imageArray = objectMapper.readValue(product.getImages(), 
                        objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
                    imagesToProcess.addAll(imageArray);
                } catch (Exception e) {
                    log.warn("解析images字段失败，可能是旧格式数据: {}", e.getMessage());
                }
            }
            
            // 处理图片数据
            if (!imagesToProcess.isEmpty()) {
                for (int i = 0; i < imagesToProcess.size(); i++) {
                    String base64Data = imagesToProcess.get(i);
                    if (StringUtils.hasText(base64Data)) {
                        // 生成图片名称
                        String imageName = "product_" + System.currentTimeMillis() + "_" + i;
                        
                        // 保存图片到imgs表
                        Img savedImg = imgService.saveImg(imageName, base64Data, userId);
                        if (savedImg != null && savedImg.getId() != null) {
                            imageIds.add(savedImg.getId());
                            log.info("图片保存成功: imgId={}, userId={}", savedImg.getId(), userId);
                        }
                    }
                }
            }

            // 将图片ID集合转换为JSON字符串存储在images字段中
            if (!imageIds.isEmpty()) {
                try {
                    product.setImages(objectMapper.writeValueAsString(imageIds));
                } catch (Exception e) {
                    log.error("图片ID序列化失败: {}", e.getMessage());
                    throw new RuntimeException("图片处理失败");
                }
            }

            // 处理标签数据：将tagList转换为JSON字符串存储在tags字段中
            if (product.getTagList() != null && !product.getTagList().isEmpty()) {
                try {
                    product.setTags(objectMapper.writeValueAsString(product.getTagList()));
                    log.info("标签处理成功: tags={}", product.getTagList());
                } catch (Exception e) {
                    log.error("标签序列化失败: {}", e.getMessage());
                    throw new RuntimeException("标签处理失败");
                }
            }

            // 设置商品基本信息
            product.setSellerId(userId);
            product.setStatus("pending"); // 待审核状态
            product.setAuditStatus(0); // 审核状态：0-待审核
            product.setCreatedAt(LocalDateTime.now());
            product.setUpdatedAt(LocalDateTime.now());
            product.setDeleted(0);
            product.setViewCount(0);
            product.setFavoriteCount(0);

            // 保存商品
            int result = productMapper.insert(product);
            if (result > 0) {
                log.info("商品创建成功: productId={}, sellerId={}, imageCount={}", product.getId(), userId, imageIds.size());
                return product;
            } else {
                throw new RuntimeException("商品创建失败");
            }
        } catch (Exception e) {
            log.error("创建商品失败: {}", e.getMessage());
            throw new RuntimeException("创建商品失败: " + e.getMessage());
        }
    }
}