package com.campus.secondhand.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.entity.User;
import com.campus.secondhand.mapper.UserManageMapper;
import com.campus.secondhand.mapper.UserMapper;
import com.campus.secondhand.service.UserManageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户管理服务实现类
 *
 * @author campus-secondhand
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserManageServiceImpl implements UserManageService {

    private final UserManageMapper userManageMapper;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    private static final String DEFAULT_PASSWORD_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int DEFAULT_PASSWORD_LENGTH = 8;

    @Override
    public Page<User> getUserList(int page, int size, String keyword, String status, String verifyStatus) {
        log.info("分页查询用户列表: page={}, size={}, keyword={}, status={}, verifyStatus={}", 
                page, size, keyword, status, verifyStatus);
        
        Page<User> pageParam = new Page<>(page, size);
        return userManageMapper.selectUserList(pageParam, keyword, status, verifyStatus);
    }

    @Override
    @Transactional
    public boolean banUser(Long userId, Long adminId, String reason, int banDays) {
        log.info("封禁用户: userId={}, adminId={}, reason={}, banDays={}", userId, adminId, reason, banDays);
        
        try {
            // 验证用户是否存在
            User user = userMapper.selectById(userId);
            if (user == null || user.getDeleted() == 1) {
                log.warn("用户不存在或已删除: userId={}", userId);
                return false;
            }

            // 验证管理员权限
            User admin = userMapper.selectById(adminId);
            if (admin == null || !"ADMIN".equals(admin.getRole())) {
                log.warn("管理员权限验证失败: adminId={}", adminId);
                return false;
            }

            // 计算封禁结束时间
            LocalDateTime banEndTime = null;
            if (banDays > 0) {
                banEndTime = LocalDateTime.now().plusDays(banDays);
            }

            int result = userManageMapper.banUser(userId, reason, banEndTime);
            if (result > 0) {
                log.info("用户封禁成功: userId={}, banEndTime={}", userId, banEndTime);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("封禁用户失败: userId={}", userId, e);
            throw new RuntimeException("封禁用户失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean unbanUser(Long userId, Long adminId) {
        log.info("解封用户: userId={}, adminId={}", userId, adminId);
        
        try {
            // 验证用户是否存在
            User user = userMapper.selectById(userId);
            if (user == null || user.getDeleted() == 1) {
                log.warn("用户不存在或已删除: userId={}", userId);
                return false;
            }

            // 验证管理员权限
            User admin = userMapper.selectById(adminId);
            if (admin == null || !"ADMIN".equals(admin.getRole())) {
                log.warn("管理员权限验证失败: adminId={}", adminId);
                return false;
            }

            int result = userManageMapper.unbanUser(userId);
            if (result > 0) {
                log.info("用户解封成功: userId={}", userId);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("解封用户失败: userId={}", userId, e);
            throw new RuntimeException("解封用户失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public int batchBanUsers(List<Long> userIds, Long adminId, String reason, int banDays) {
        log.info("批量封禁用户: userIds={}, adminId={}, reason={}, banDays={}", userIds, adminId, reason, banDays);
        
        if (userIds == null || userIds.isEmpty()) {
            return 0;
        }

        try {
            // 验证管理员权限
            User admin = userMapper.selectById(adminId);
            if (admin == null || !"ADMIN".equals(admin.getRole())) {
                log.warn("管理员权限验证失败: adminId={}", adminId);
                throw new RuntimeException("管理员权限验证失败");
            }

            // 计算封禁结束时间
            LocalDateTime banEndTime = null;
            if (banDays > 0) {
                banEndTime = LocalDateTime.now().plusDays(banDays);
            }

            int result = userManageMapper.batchBanUsers(userIds, reason, banEndTime);
            log.info("批量封禁用户完成: 成功数量={}", result);
            return result;
        } catch (Exception e) {
            log.error("批量封禁用户失败: userIds={}", userIds, e);
            throw new RuntimeException("批量封禁用户失败: " + e.getMessage());
        }
    }

    @Override
    public Page<User> getPendingVerifications(int page, int size, String keyword) {
        log.info("分页查询待审核的学生身份认证: page={}, size={}, keyword={}", page, size, keyword);
        
        Page<User> pageParam = new Page<>(page, size);
        return userManageMapper.selectPendingVerifications(pageParam, keyword);
    }

    @Override
    @Transactional
    public boolean verifyStudentIdentity(Long userId, Long adminId, String status, String reason) {
        log.info("审核学生身份认证: userId={}, adminId={}, status={}, reason={}", userId, adminId, status, reason);
        
        try {
            // 验证用户是否存在且状态为待审核
            User user = userMapper.selectById(userId);
            if (user == null || user.getDeleted() == 1 || !"PENDING".equals(user.getVerifyStatus())) {
                log.warn("用户不存在、已删除或不是待审核状态: userId={}, verifyStatus={}", userId, 
                        user != null ? user.getVerifyStatus() : "null");
                return false;
            }

            // 验证管理员权限
            User admin = userMapper.selectById(adminId);
            if (admin == null || !"ADMIN".equals(admin.getRole())) {
                log.warn("管理员权限验证失败: adminId={}", adminId);
                return false;
            }

            // 验证审核状态
            if (!"APPROVED".equals(status) && !"REJECTED".equals(status)) {
                log.warn("无效的审核状态: status={}", status);
                return false;
            }

            // 拒绝时必须提供原因
            if ("REJECTED".equals(status) && !StringUtils.hasText(reason)) {
                log.warn("拒绝审核时必须提供原因");
                return false;
            }

            LocalDateTime verifiedAt = LocalDateTime.now();
            int result = userManageMapper.verifyStudentIdentity(userId, status, reason, verifiedAt);
            if (result > 0) {
                log.info("学生身份认证审核成功: userId={}, status={}", userId, status);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("审核学生身份认证失败: userId={}", userId, e);
            throw new RuntimeException("审核学生身份认证失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public int batchVerifyStudentIdentity(List<Long> userIds, Long adminId, String status, String reason) {
        log.info("批量审核学生身份认证: userIds={}, adminId={}, status={}, reason={}", userIds, adminId, status, reason);
        
        if (userIds == null || userIds.isEmpty()) {
            return 0;
        }

        try {
            // 验证管理员权限
            User admin = userMapper.selectById(adminId);
            if (admin == null || !"ADMIN".equals(admin.getRole())) {
                log.warn("管理员权限验证失败: adminId={}", adminId);
                throw new RuntimeException("管理员权限验证失败");
            }

            // 验证审核状态
            if (!"APPROVED".equals(status) && !"REJECTED".equals(status)) {
                log.warn("无效的审核状态: status={}", status);
                throw new RuntimeException("无效的审核状态");
            }

            // 拒绝时必须提供原因
            if ("REJECTED".equals(status) && !StringUtils.hasText(reason)) {
                log.warn("拒绝审核时必须提供原因");
                throw new RuntimeException("拒绝审核时必须提供原因");
            }

            LocalDateTime verifiedAt = LocalDateTime.now();
            int result = userManageMapper.batchVerifyStudentIdentity(userIds, status, reason, verifiedAt);
            log.info("批量审核学生身份认证完成: 成功数量={}", result);
            return result;
        } catch (Exception e) {
            log.error("批量审核学生身份认证失败: userIds={}", userIds, e);
            throw new RuntimeException("批量审核学生身份认证失败: " + e.getMessage());
        }
    }

    @Override
    public UserDetail getUserDetail(Long userId) {
        log.info("获取用户详细信息: userId={}", userId);
        
        try {
            UserDetail userDetail = userManageMapper.selectUserDetail(userId);
            if (userDetail == null) {
                log.warn("用户不存在: userId={}", userId);
                return null;
            }
            return userDetail;
        } catch (Exception e) {
            log.error("获取用户详细信息失败: userId={}", userId, e);
            throw new RuntimeException("获取用户详细信息失败: " + e.getMessage());
        }
    }

    @Override
    public UserStats getUserStats() {
        log.info("获取用户统计信息");
        
        try {
            UserStats stats = userManageMapper.selectUserStats();
            if (stats == null) {
                stats = new UserStats();
            }
            return stats;
        } catch (Exception e) {
            log.error("获取用户统计信息失败", e);
            throw new RuntimeException("获取用户统计信息失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public String resetUserPassword(Long userId, Long adminId) {
        log.info("重置用户密码: userId={}, adminId={}", userId, adminId);
        
        try {
            // 验证用户是否存在
            User user = userMapper.selectById(userId);
            if (user == null || user.getDeleted() == 1) {
                log.warn("用户不存在或已删除: userId={}", userId);
                throw new RuntimeException("用户不存在或已删除");
            }

            // 验证管理员权限
            User admin = userMapper.selectById(adminId);
            if (admin == null || !"ADMIN".equals(admin.getRole())) {
                log.warn("管理员权限验证失败: adminId={}", adminId);
                throw new RuntimeException("管理员权限验证失败");
            }

            // 生成新密码
            String newPassword = generateRandomPassword();
            String encodedPassword = passwordEncoder.encode(newPassword);

            int result = userManageMapper.resetUserPassword(userId, encodedPassword);
            if (result > 0) {
                log.info("用户密码重置成功: userId={}", userId);
                return newPassword;
            } else {
                throw new RuntimeException("密码重置失败");
            }
        } catch (Exception e) {
            log.error("重置用户密码失败: userId={}", userId, e);
            throw new RuntimeException("重置用户密码失败: " + e.getMessage());
        }
    }

    /**
     * 生成随机密码
     */
    private String generateRandomPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();
        
        for (int i = 0; i < DEFAULT_PASSWORD_LENGTH; i++) {
            int index = random.nextInt(DEFAULT_PASSWORD_CHARS.length());
            password.append(DEFAULT_PASSWORD_CHARS.charAt(index));
        }
        
        return password.toString();
    }
}