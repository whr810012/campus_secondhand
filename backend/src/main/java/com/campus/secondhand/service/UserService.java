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
}