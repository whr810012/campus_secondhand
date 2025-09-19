package com.campus.secondhand.service;

import com.campus.secondhand.entity.User;

/**
 * 用户服务接口
 *
 * @author campus-secondhand
 */
public interface UserService {

    /**
     * 根据token获取当前用户信息
     *
     * @param token JWT token
     * @return 用户信息
     */
    User getCurrentUser(String token);

    /**
     * 更新用户资料
     *
     * @param token JWT token
     * @param userInfo 用户信息
     */
    void updateProfile(String token, User userInfo);

    /**
     * 修改密码
     *
     * @param token 用户token
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void changePassword(String token, String oldPassword, String newPassword);

    /**
     * 获取用户统计信息
     *
     * @param token 用户token
     * @return 用户统计信息
     */
    com.campus.secondhand.controller.UserController.UserStats getUserStats(String token);

    /**
     * 获取指定用户的公开信息
     *
     * @param userId 用户ID
     * @return 用户公开信息
     */
    User getUserProfile(Long userId);

    /**
     * 获取指定用户的商品列表
     *
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 商品分页数据
     */
    Object getUserProducts(Long userId, int page, int size);
}