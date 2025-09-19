package com.campus.secondhand.controller;

import com.campus.secondhand.common.Result;
import com.campus.secondhand.entity.User;
import com.campus.secondhand.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户控制器
 *
 * @author campus-secondhand
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 获取当前用户资料
     */
    @GetMapping("/profile")
    public Result<User> getProfile(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        User user = userService.getCurrentUser(token);
        return Result.success(user);
    }

    /**
     * 更新用户资料
     */
    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody User userInfo, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        userService.updateProfile(token, userInfo);
        return Result.success();
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public Result<Void> changePassword(
            @RequestHeader("Authorization") String token,
            @RequestBody ChangePasswordRequest request) {
        try {
            userService.changePassword(token, request.getOldPassword(), request.getNewPassword());
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取用户统计信息
     */
    @GetMapping("/stats")
    public Result<UserStats> getUserStats(
            @RequestHeader("Authorization") String token) {
        try {
            UserStats stats = userService.getUserStats(token);
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取指定用户的公开信息
     */
    @GetMapping("/profile/{userId}")
    public Result<User> getUserProfile(@PathVariable Long userId) {
        try {
            User user = userService.getUserProfile(userId);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取指定用户的商品列表
     */
    @GetMapping("/{userId}/products")
    public Result<?> getUserProducts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            return Result.success(userService.getUserProducts(userId, page, size));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    public static class ChangePasswordRequest {
        private String oldPassword;
        private String newPassword;

        public String getOldPassword() { return oldPassword; }
        public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }

    public static class UserStats {
        private Long productCount;      // 发布商品数
        private Long favoriteCount;     // 收藏数
        private Long orderCount;        // 订单数
        private Integer creditScore;    // 信誉分数
        private Double goodRate;        // 好评率
        private Integer tradeCount;     // 交易次数

        public Long getProductCount() { return productCount; }
        public void setProductCount(Long productCount) { this.productCount = productCount; }
        public Long getFavoriteCount() { return favoriteCount; }
        public void setFavoriteCount(Long favoriteCount) { this.favoriteCount = favoriteCount; }
        public Long getOrderCount() { return orderCount; }
        public void setOrderCount(Long orderCount) { this.orderCount = orderCount; }
        public Integer getCreditScore() { return creditScore; }
        public void setCreditScore(Integer creditScore) { this.creditScore = creditScore; }
        public Double getGoodRate() { return goodRate; }
        public void setGoodRate(Double goodRate) { this.goodRate = goodRate; }
        public Integer getTradeCount() { return tradeCount; }
        public void setTradeCount(Integer tradeCount) { this.tradeCount = tradeCount; }
    }
}