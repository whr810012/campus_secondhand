package com.campus.secondhand.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.common.Result;
import com.campus.secondhand.entity.Product;
import com.campus.secondhand.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 收藏控制器
 *
 * @author campus-secondhand
 */
@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    /**
     * 添加收藏
     */
    @PostMapping
    public Result<Void> addFavorite(@RequestBody FavoriteRequest request) {
        try {
            boolean success = favoriteService.addFavorite(request.getUserId(), request.getProductId());
            if (success) {
                return Result.success();
            } else {
                return Result.error("收藏失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 取消收藏
     */
    @DeleteMapping
    public Result<Void> removeFavorite(@RequestParam Long userId, @RequestParam Long productId) {
        try {
            boolean success = favoriteService.removeFavorite(userId, productId);
            if (success) {
                return Result.success();
            } else {
                return Result.error("取消收藏失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 检查是否已收藏
     */
    @GetMapping("/check")
    public Result<Boolean> checkFavorite(@RequestParam Long userId, @RequestParam Long productId) {
        boolean isFavorited = favoriteService.isFavorited(userId, productId);
        return Result.success(isFavorited);
    }

    /**
     * 分页查询用户收藏的商品
     */
    @GetMapping("/user/{userId}")
    public Result<Page<Product>> getUserFavorites(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size) {
        Page<Product> favorites = favoriteService.getUserFavorites(userId, page, size);
        return Result.success(favorites);
    }

    /**
     * 获取用户收藏数量
     */
    @GetMapping("/user/{userId}/count")
    public Result<Long> getUserFavoriteCount(@PathVariable Long userId) {
        long count = favoriteService.getUserFavoriteCount(userId);
        return Result.success(count);
    }

    /**
     * 获取商品收藏数量
     */
    @GetMapping("/product/{productId}/count")
    public Result<Long> getProductFavoriteCount(@PathVariable Long productId) {
        long count = favoriteService.getProductFavoriteCount(productId);
        return Result.success(count);
    }

    // 内部类定义请求参数
    public static class FavoriteRequest {
        private Long userId;
        private Long productId;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
    }
}