package com.campus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.common.PageResult;
import com.campus.entity.User;

import java.util.Map;

/**
 * 用户服务接口
 * 
 * @author Campus Team
 */
public interface UserService extends IService<User> {
    
    /**
     * 用户注册
     */
    void register(String username, String password, String email, String nickname);
    
    /**
     * 用户登录
     */
    Map<String, Object> login(String username, String password);
    
    /**
     * 根据用户名查询用户
     */
    User findByUsername(String username);
    
    /**
     * 根据邮箱查询用户
     */
    User findByEmail(String email);
    
    /**
     * 分页查询用户列表
     */
    PageResult<User> getUserPage(Long current, Long size, String keyword, Integer role, Integer status);
    
    /**
     * 更新用户信息
     */
    void updateUserInfo(Long userId, User user);
    
    /**
     * 修改密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);
    
    /**
     * 重置密码
     */
    void resetPassword(Long userId, String newPassword);
    
    /**
     * 启用/禁用用户
     */
    void updateUserStatus(Long userId, Integer status);
    
    /**
     * 更新用户角色
     */
    void updateUserRole(Long userId, Integer role);
    
    /**
     * 删除用户
     */
    void deleteUser(Long userId);
    
    /**
     * 获取用户统计信息
     */
    Map<String, Object> getUserStatistics();
    
    /**
     * 检查用户名是否存在
     */
    boolean isUsernameExists(String username);
    
    /**
     * 检查邮箱是否存在
     */
    boolean isEmailExists(String email);
    
    /**
     * 验证用户权限
     */
    boolean hasPermission(Long userId, String permission);
    
    /**
     * 获取当前登录用户信息
     */
    User getCurrentUser();
}