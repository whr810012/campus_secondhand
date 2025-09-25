package com.campus.secondhand.service.impl;

import com.campus.secondhand.entity.User;
import com.campus.secondhand.mapper.UserMapper;
import com.campus.secondhand.service.UserService;
import com.campus.secondhand.service.ProductService;
import com.campus.secondhand.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户服务实现类
 *
 * @author campus-secondhand
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final ProductService productService;

    @Override
    public User getCurrentUser(String token) {
        try {
            // 解析token获取用户ID
            Long userId = jwtUtil.getUserIdFromToken(token.replace("Bearer ", ""));
            
            // 根据用户ID查询用户信息
            User user = userMapper.selectById(userId);
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }
            
            // 更新最后登录时间
            user.setLastLoginTime(LocalDateTime.now());
            userMapper.updateById(user);
            
            return user;
        } catch (Exception e) {
            log.error("获取当前用户信息失败: {}", e.getMessage());
            throw new RuntimeException("获取用户信息失败");
        }
    }

    @Override
    @Transactional
    public void updateProfile(String token, User userInfo) {
        try {
            // 解析token获取用户ID
            Long userId = jwtUtil.getUserIdFromToken(token.replace("Bearer ", ""));
            
            // 查询当前用户
            User currentUser = userMapper.selectById(userId);
            if (currentUser == null) {
                throw new RuntimeException("用户不存在");
            }
            
            // 更新允许修改的字段
            if (userInfo.getNickname() != null) {
                currentUser.setNickname(userInfo.getNickname());
            }
            if (userInfo.getAvatar() != null) {
                currentUser.setAvatar(userInfo.getAvatar());
            }
            if (userInfo.getGender() != null) {
                currentUser.setGender(userInfo.getGender());
            }
            if (userInfo.getRealName() != null) {
                currentUser.setRealName(userInfo.getRealName());
            }
            
            currentUser.setUpdatedAt(LocalDateTime.now());
            userMapper.updateById(currentUser);
            
            log.info("用户资料更新成功: userId={}", userId);
        } catch (Exception e) {
            log.error("更新用户资料失败: {}", e.getMessage());
            throw new RuntimeException("更新用户资料失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void changePassword(String token, String oldPassword, String newPassword) {
        try {
            // 解析token获取用户ID
            Long userId = jwtUtil.getUserIdFromToken(token.replace("Bearer ", ""));
            
            // 查询当前用户
            User currentUser = userMapper.selectById(userId);
            if (currentUser == null) {
                throw new RuntimeException("用户不存在");
            }
            
            // 验证旧密码 - 明文比较
            if (!oldPassword.equals(currentUser.getPassword())) {
                throw new RuntimeException("原密码错误");
            }
            
            // 更新密码 - 明文存储
            currentUser.setPassword(newPassword);
            currentUser.setUpdatedAt(LocalDateTime.now());
            userMapper.updateById(currentUser);
            
            log.info("用户密码修改成功: userId={}", userId);
        } catch (Exception e) {
            log.error("修改密码失败: {}", e.getMessage());
            throw new RuntimeException("修改密码失败: " + e.getMessage());
        }
    }

    @Override
    public com.campus.secondhand.controller.UserController.UserStats getUserStats(String token) {
        try {
            // 从token中获取用户ID
            Long userId = jwtUtil.getUserIdFromToken(token.replace("Bearer ", ""));
            if (userId == null) {
                throw new RuntimeException("无效的token");
            }

            User user = userMapper.selectById(userId);
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }

            com.campus.secondhand.controller.UserController.UserStats stats = 
                new com.campus.secondhand.controller.UserController.UserStats();
            
            // 设置用户统计信息
            stats.setCreditScore(user.getCreditScore());
            stats.setGoodRate(user.getGoodRate());
            stats.setTradeCount(user.getTradeCount());
            
            // TODO: 这里需要根据实际业务逻辑查询商品数、收藏数、订单数
            // 暂时设置为0，后续可以通过其他Service查询
            stats.setProductCount(0L);
            stats.setFavoriteCount(0L);
            stats.setOrderCount(0L);
            
            return stats;
        } catch (Exception e) {
            log.error("获取用户统计信息失败: {}", e.getMessage());
            throw new RuntimeException("获取用户统计信息失败: " + e.getMessage());
        }
    }

    @Override
    public User getUserProfile(Long userId) {
        try {
            User user = userMapper.selectById(userId);
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }
            
            // 创建一个新的User对象，只包含公开信息
            User publicUser = new User();
            publicUser.setId(user.getId());
            publicUser.setNickname(user.getNickname());
            publicUser.setAvatar(user.getAvatar());
            publicUser.setGender(user.getGender());
            publicUser.setSchoolId(user.getSchoolId());
            publicUser.setSchoolName(user.getSchoolName());
            publicUser.setVerifyStatus(user.getVerifyStatus());
            publicUser.setCreatedAt(user.getCreatedAt());
            publicUser.setCreditScore(user.getCreditScore());
            publicUser.setGoodRate(user.getGoodRate());
            publicUser.setTradeCount(user.getTradeCount());
            
            return publicUser;
        } catch (Exception e) {
            log.error("获取用户公开信息失败: userId={}", userId, e);
            throw new RuntimeException("获取用户信息失败: " + e.getMessage());
        }
    }

    @Override
    public Object getUserProducts(Long userId, int page, int size) {
        try {
            // 验证用户是否存在
            User user = userMapper.selectById(userId);
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }
            
            // 调用ProductService来获取用户的商品列表
            return productService.getUserProducts(userId, page, size, null);
        } catch (Exception e) {
            log.error("获取用户商品列表失败: userId={}", userId, e);
            throw new RuntimeException("获取用户商品列表失败: " + e.getMessage());
        }
    }
}