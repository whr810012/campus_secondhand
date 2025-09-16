package com.campus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.common.PageResult;
import com.campus.common.ResultCode;
import com.campus.entity.User;
import com.campus.mapper.UserMapper;
import com.campus.service.UserService;
import com.campus.util.JwtUtil;
import com.campus.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户服务实现类
 * 
 * @author Campus Team
 */
@Service
@Transactional
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    public void register(String username, String password, String email, String nickname) {
        // 检查用户名是否存在
        if (isUsernameExists(username)) {
            throw new RuntimeException(ResultCode.USERNAME_EXISTS.getMessage());
        }
        
        // 检查邮箱是否存在
        if (isEmailExists(email)) {
            throw new RuntimeException(ResultCode.EMAIL_EXISTS.getMessage());
        }
        
        // 创建用户
        User user = new User();
        user.setUsername(username);
        user.setPassword(PasswordUtil.encode(password));
        user.setEmail(email);
        user.setNickname(nickname);
        user.setRole(User.Role.USER.getCode());
        user.setStatus(User.Status.NORMAL.getCode());
        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());
        
        save(user);
    }
    
    @Override
    public Map<String, Object> login(String username, String password) {
        // 查询用户
        User user = findByUsername(username);
        if (user == null) {
            throw new RuntimeException(ResultCode.USER_NOT_FOUND.getMessage());
        }
        
        // 检查用户状态
        if (user.getStatus().equals(User.Status.DISABLED.getCode())) {
            throw new RuntimeException(ResultCode.USER_DISABLED.getMessage());
        }
        
        // 验证密码
        if (!PasswordUtil.matches(password, user.getPassword())) {
            throw new RuntimeException(ResultCode.PASSWORD_ERROR.getMessage());
        }
        
        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        // 返回登录信息
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);
        
        return result;
    }
    
    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }
    
    @Override
    public User findByEmail(String email) {
        return userMapper.findByEmail(email);
    }
    
    @Override
    public PageResult<User> getUserPage(Long current, Long size, String keyword, Integer role, Integer status) {
        Page<User> page = new Page<>(current, size);
        Page<User> result = userMapper.selectUserPage(page, keyword, role, status);
        return PageResult.of(result.getRecords(), result.getTotal(), current, size);
    }
    
    @Override
    public void updateUserInfo(Long userId, User user) {
        User existUser = getById(userId);
        if (existUser == null) {
            throw new RuntimeException(ResultCode.USER_NOT_FOUND.getMessage());
        }
        
        // 更新用户信息
        existUser.setNickname(user.getNickname());
        existUser.setEmail(user.getEmail());
        existUser.setPhone(user.getPhone());
        existUser.setAvatar(user.getAvatar());
        existUser.setGender(user.getGender());
        existUser.setBirthday(user.getBirthday());
        existUser.setSchool(user.getSchool());
        existUser.setMajor(user.getMajor());
        existUser.setGrade(user.getGrade());
        existUser.setUpdatedTime(LocalDateTime.now());
        
        updateById(existUser);
    }
    
    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException(ResultCode.USER_NOT_FOUND.getMessage());
        }
        
        // 验证原密码
        if (!PasswordUtil.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException(ResultCode.OLD_PASSWORD_ERROR.getMessage());
        }
        
        // 更新密码
        user.setPassword(PasswordUtil.encode(newPassword));
        user.setUpdatedTime(LocalDateTime.now());
        updateById(user);
    }
    
    @Override
    public void resetPassword(Long userId, String newPassword) {
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException(ResultCode.USER_NOT_FOUND.getMessage());
        }
        
        user.setPassword(PasswordUtil.encode(newPassword));
        user.setUpdatedTime(LocalDateTime.now());
        updateById(user);
    }
    
    @Override
    public void updateUserStatus(Long userId, Integer status) {
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException(ResultCode.USER_NOT_FOUND.getMessage());
        }
        
        user.setStatus(status);
        user.setUpdatedTime(LocalDateTime.now());
        updateById(user);
    }
    
    @Override
    public void updateUserRole(Long userId, Integer role) {
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException(ResultCode.USER_NOT_FOUND.getMessage());
        }
        
        user.setRole(role);
        user.setUpdatedTime(LocalDateTime.now());
        updateById(user);
    }
    
    @Override
    public void deleteUser(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException(ResultCode.USER_NOT_FOUND.getMessage());
        }
        
        // 不能删除超级管理员
        if (user.getRole().equals(User.Role.SUPER_ADMIN.getCode())) {
            throw new RuntimeException("不能删除超级管理员");
        }
        
        removeById(userId);
    }
    
    @Override
    public Map<String, Object> getUserStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalUsers", userMapper.countUsers());
        statistics.put("todayUsers", userMapper.countTodayUsers());
        statistics.put("usersByRole", userMapper.countUsersByRole());
        return statistics;
    }
    
    @Override
    public boolean isUsernameExists(String username) {
        return findByUsername(username) != null;
    }
    
    @Override
    public boolean isEmailExists(String email) {
        return findByEmail(email) != null;
    }
    
    @Override
    public boolean hasPermission(Long userId, String permission) {
        User user = getById(userId);
        if (user == null) {
            return false;
        }
        
        // 超级管理员拥有所有权限
        if (user.getRole().equals(User.Role.SUPER_ADMIN.getCode())) {
            return true;
        }
        
        // 管理员拥有除用户管理外的权限
        if (user.getRole().equals(User.Role.ADMIN.getCode())) {
            return !"user_management".equals(permission);
        }
        
        return false;
    }
    
    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof String) {
            String username = (String) authentication.getPrincipal();
            return findByUsername(username);
        }
        return null;
    }
}