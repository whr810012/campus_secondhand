package com.campus.secondhand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.entity.Favorite;
import com.campus.secondhand.entity.Product;
import com.campus.secondhand.mapper.FavoriteMapper;
import com.campus.secondhand.mapper.ProductMapper;
import com.campus.secondhand.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
        return favoriteMapper.selectUserFavoriteProducts(pageParam, userId);
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