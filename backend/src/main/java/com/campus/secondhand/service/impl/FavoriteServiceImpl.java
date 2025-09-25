package com.campus.secondhand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.entity.Favorite;
import com.campus.secondhand.entity.Product;
import com.campus.secondhand.entity.Img;
import com.campus.secondhand.mapper.FavoriteMapper;
import com.campus.secondhand.mapper.ProductMapper;
import com.campus.secondhand.service.FavoriteService;
import com.campus.secondhand.service.ImgService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 收藏服务实现类
 *
 * @author campus-secondhand
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteMapper favoriteMapper;
    private final ProductMapper productMapper;
    private final ImgService imgService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public boolean addFavorite(Long userId, Long productId) {
        // 检查商品是否存在
        Product product = productMapper.selectById(productId);
        if (product == null || product.getDeleted() == 1) {
            throw new RuntimeException("商品不存在");
        }

        // 检查是否已收藏
        if (isFavorited(userId, productId)) {
            throw new RuntimeException("已收藏该商品");
        }

        // 不能收藏自己的商品
        if (product.getSellerId().equals(userId)) {
            throw new RuntimeException("不能收藏自己的商品");
        }

        // 创建收藏记录
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setProductId(productId);
        favorite.setCreatedAt(LocalDateTime.now());

        int result = favoriteMapper.insert(favorite);
        
        if (result > 0) {
            // 更新商品收藏数
            product.setFavoriteCount(product.getFavoriteCount() + 1);
            productMapper.updateById(product);
            log.info("用户{}收藏商品{}成功", userId, productId);
        }
        
        return result > 0;
    }

    @Override
    @Transactional
    public boolean removeFavorite(Long userId, Long productId) {
        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getUserId, userId)
                   .eq(Favorite::getProductId, productId);
        
        int result = favoriteMapper.delete(queryWrapper);
        
        if (result > 0) {
            // 更新商品收藏数
            Product product = productMapper.selectById(productId);
            if (product != null && product.getFavoriteCount() > 0) {
                product.setFavoriteCount(product.getFavoriteCount() - 1);
                productMapper.updateById(product);
            }
            log.info("用户{}取消收藏商品{}成功", userId, productId);
        }
        
        return result > 0;
    }

    @Override
    public boolean isFavorited(Long userId, Long productId) {
        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getUserId, userId)
                   .eq(Favorite::getProductId, productId);
        
        return favoriteMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public Page<Product> getUserFavorites(Long userId, int page, int size) {
        Page<Product> pageParam = new Page<>(page, size);
        Page<Product> favorites = favoriteMapper.selectUserFavoriteProducts(pageParam, userId);
        
        // 为每个商品加载图片和标签数据
        for (Product product : favorites.getRecords()) {
            loadProductData(product);
        }
        
        return favorites;
    }
    
    /**
     * 加载商品数据（图片和标签）
     */
    private void loadProductData(Product product) {
        log.info("开始加载收藏商品数据: productId={}, images字段={}", product.getId(), product.getImages());
        // 加载图片数据
        if (product != null && StringUtils.hasText(product.getImages())) {
            log.info("开始加载收藏商品图片: productId={}, images={}", product.getId(), product.getImages());
            try {
                // 解析图片ID集合
                List<Long> imageIds = objectMapper.readValue(product.getImages(), 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Long.class));
                log.info("解析到图片ID列表: {}", imageIds);
                
                List<String> imageList = new ArrayList<>();
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
                log.info("最终图片列表大小: {}", imageList.size());
                product.setImageList(imageList);
                log.info("设置图片列表完成: imageList大小={}", imageList.size());
            } catch (Exception e) {
                log.error("加载收藏商品图片失败: productId={}, images={}, error={}", product.getId(), product.getImages(), e.getMessage(), e);
                // 设置空列表，确保有值返回
                product.setImageList(new ArrayList<>());
            }
        } else {
            log.info("收藏商品无图片数据: productId={}, images={}", 
                product != null ? product.getId() : "null", 
                product != null ? product.getImages() : "null");
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
                log.error("加载收藏商品标签失败: productId={}, error={}", product.getId(), e.getMessage());
                product.setTagList(new ArrayList<>()); // 设置空列表避免null
            }
        }
    }

    @Override
    public long getUserFavoriteCount(Long userId) {
        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getUserId, userId);
        return favoriteMapper.selectCount(queryWrapper);
    }

    @Override
    public long getProductFavoriteCount(Long productId) {
        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getProductId, productId);
        return favoriteMapper.selectCount(queryWrapper);
    }
}