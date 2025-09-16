package com.campus.secondhand.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.entity.Product;

/**
 * 收藏服务接口
 *
 * @author campus-secondhand
 */
public interface FavoriteService {

    /**
     * 添加收藏
     *
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 是否成功
     */
    boolean addFavorite(Long userId, Long productId);

    /**
     * 取消收藏
     *
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 是否成功
     */
    boolean removeFavorite(Long userId, Long productId);

    /**
     * 检查是否已收藏
     *
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 是否已收藏
     */
    boolean isFavorited(Long userId, Long productId);

    /**
     * 分页查询用户收藏的商品
     *
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 收藏的商品分页数据
     */
    Page<Product> getUserFavorites(Long userId, int page, int size);

    /**
     * 获取用户收藏数量
     *
     * @param userId 用户ID
     * @return 收藏数量
     */
    long getUserFavoriteCount(Long userId);

    /**
     * 获取商品收藏数量
     *
     * @param productId 商品ID
     * @return 收藏数量
     */
    long getProductFavoriteCount(Long productId);
}