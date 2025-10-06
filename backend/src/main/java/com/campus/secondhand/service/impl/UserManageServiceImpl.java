package com.campus.secondhand.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.entity.Img;
import com.campus.secondhand.entity.User;
import com.campus.secondhand.mapper.UserManageMapper;
import com.campus.secondhand.mapper.UserMapper;
import com.campus.secondhand.service.ImgService;
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
    private final ImgService imgService;

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
    public java.util.Map<String, Object> getUserList(int page, int size, String sortBy, String keyword) {
        log.info("管理员查询用户列表: page={}, size={}, sortBy={}, keyword={}", page, size, sortBy, keyword);
        
        Page<User> pageParam = new Page<>(page, size);
        Page<User> result = userManageMapper.selectUserListForAdmin(pageParam, sortBy, keyword);
        
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("records", result.getRecords());
        response.put("total", result.getTotal());
        response.put("current", result.getCurrent());
        response.put("size", result.getSize());
        response.put("pages", result.getPages());
        
        return response;
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
            if (admin == null || !"admin".equals(admin.getRole())) {
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
            if (admin == null || !"admin".equals(admin.getRole())) {
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
            if (admin == null || !"admin".equals(admin.getRole())) {
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
    @Transactional
    public int batchUnbanUsers(List<Long> userIds, Long adminId) {
        log.info("批量解封用户: userIds={}, adminId={}", userIds, adminId);
        
        if (userIds == null || userIds.isEmpty()) {
            return 0;
        }

        try {
            // 验证管理员权限
            User admin = userMapper.selectById(adminId);
            if (admin == null || !"admin".equals(admin.getRole())) {
                log.warn("管理员权限验证失败: adminId={}", adminId);
                throw new RuntimeException("管理员权限验证失败");
            }

            int successCount = 0;
            for (Long userId : userIds) {
                try {
                    // 验证用户是否存在
                    User user = userMapper.selectById(userId);
                    if (user == null || user.getDeleted() == 1) {
                        log.warn("用户不存在或已删除: userId={}", userId);
                        continue;
                    }

                    int result = userManageMapper.unbanUser(userId);
                    if (result > 0) {
                        successCount++;
                        log.info("用户解封成功: userId={}", userId);
                    }
                } catch (Exception e) {
                    log.error("解封用户失败: userId={}", userId, e);
                }
            }

            log.info("批量解封完成: 总数={}, 成功={}", userIds.size(), successCount);
            return successCount;
        } catch (Exception e) {
            log.error("批量解封用户失败", e);
            throw new RuntimeException("批量解封用户失败: " + e.getMessage());
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
            // 验证用户是否存在且不是已认证状态
            User user = userMapper.selectById(userId);
            if (user == null || user.getDeleted() == 1 || user.getVerifyStatus() == 2) {
                log.warn("用户不存在、已删除或已是认证状态: userId={}, verifyStatus={}", userId, 
                        user != null ? user.getVerifyStatus() : "null");
                return false;
            }

            // 验证管理员权限
            User admin = userMapper.selectById(adminId);
            if (admin == null || !"admin".equals(admin.getRole())) {
                log.warn("管理员权限验证失败: adminId={}", adminId);
                return false;
            }

            // 验证审核状态并转换为数字
            Integer statusCode;
            if ("APPROVED".equals(status)) {
                statusCode = 2; // 已认证
            } else if ("REJECTED".equals(status)) {
                statusCode = 3; // 认证失败
            } else {
                log.warn("无效的审核状态: status={}", status);
                return false;
            }

            // 拒绝时必须提供原因
            if ("REJECTED".equals(status) && !StringUtils.hasText(reason)) {
                log.warn("拒绝审核时必须提供原因");
                return false;
            }

            int result = userManageMapper.verifyStudentIdentity(userId, statusCode.toString(), reason);
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
            if (admin == null || !"admin".equals(admin.getRole())) {
                log.warn("管理员权限验证失败: adminId={}", adminId);
                throw new RuntimeException("管理员权限验证失败");
            }

            // 验证审核状态并转换为数字
            Integer statusCode;
            if ("APPROVED".equals(status)) {
                statusCode = 2; // 已认证
            } else if ("REJECTED".equals(status)) {
                statusCode = 3; // 认证失败
            } else {
                log.warn("无效的审核状态: status={}", status);
                throw new RuntimeException("无效的审核状态");
            }

            // 拒绝时必须提供原因
            if ("REJECTED".equals(status) && !StringUtils.hasText(reason)) {
                log.warn("拒绝审核时必须提供原因");
                throw new RuntimeException("拒绝审核时必须提供原因");
            }

            int result = userManageMapper.batchVerifyStudentIdentity(userIds, statusCode.toString(), reason);
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
            
            // 获取用户认证图片信息
            User user = userDetail.getUser();
            if (user != null) {
                // 获取身份证图片
                if (user.getIdCardImgId() != null) {
                    try {
                        Img idCardImg = imgService.getImgById(user.getIdCardImgId());
                        if (idCardImg != null && idCardImg.getStatus() == 1) {
                            log.info("获取到身份证图片: imageId={}, name={}", idCardImg.getId(), idCardImg.getName());
                        }
                    } catch (Exception e) {
                        log.warn("获取身份证图片失败: imageId={}, error={}", user.getIdCardImgId(), e.getMessage());
                    }
                }
                
                // 获取学生证图片
                if (user.getStudentCardImgId() != null) {
                    try {
                        Img studentCardImg = imgService.getImgById(user.getStudentCardImgId());
                        if (studentCardImg != null && studentCardImg.getStatus() == 1) {
                            log.info("获取到学生证图片: imageId={}, name={}", studentCardImg.getId(), studentCardImg.getName());
                        }
                    } catch (Exception e) {
                        log.warn("获取学生证图片失败: imageId={}, error={}", user.getStudentCardImgId(), e.getMessage());
                    }
                }
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
            if (admin == null || !"admin".equals(admin.getRole())) {
                log.warn("管理员权限验证失败: adminId={}", adminId);
                throw new RuntimeException("管理员权限验证失败");
            }

            // 生成新密码
            String newPassword = generateRandomPassword();
            // 直接使用明文密码，不再加密
            int result = userManageMapper.resetUserPassword(userId, newPassword);
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

    @Override
    @Transactional
    public boolean deleteUser(Long userId, Long adminId) {
        log.info("删除用户: userId={}, adminId={}", userId, adminId);
        
        try {
            // 验证管理员权限
            User admin = userMapper.selectById(adminId);
            if (admin == null || !"admin".equals(admin.getRole())) {
                log.warn("管理员权限验证失败: adminId={}", adminId);
                throw new RuntimeException("管理员权限验证失败");
            }

            // 验证用户是否存在
            User user = userMapper.selectById(userId);
            if (user == null || user.getDeleted() == 1) {
                log.warn("用户不存在或已被删除: userId={}", userId);
                throw new RuntimeException("用户不存在或已被删除");
            }

            // 不能删除管理员账户
            if ("admin".equals(user.getRole())) {
                log.warn("不能删除管理员账户: userId={}", userId);
                throw new RuntimeException("不能删除管理员账户");
            }

            // 执行软删除
            int result = userManageMapper.deleteUser(userId);
            if (result > 0) {
                log.info("用户删除成功: userId={}", userId);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("删除用户失败: userId={}", userId, e);
            throw new RuntimeException("删除用户失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public int batchDeleteUsers(List<Long> userIds, Long adminId) {
        log.info("批量删除用户: userIds={}, adminId={}", userIds, adminId);
        
        if (userIds == null || userIds.isEmpty()) {
            return 0;
        }

        try {
            // 验证管理员权限
            User admin = userMapper.selectById(adminId);
            if (admin == null || !"admin".equals(admin.getRole())) {
                log.warn("管理员权限验证失败: adminId={}", adminId);
                throw new RuntimeException("管理员权限验证失败");
            }

            int successCount = 0;
            for (Long userId : userIds) {
                try {
                    // 验证用户是否存在
                    User user = userMapper.selectById(userId);
                    if (user == null || user.getDeleted() == 1) {
                        log.warn("用户不存在或已被删除，跳过: userId={}", userId);
                        continue;
                    }

                    // 不能删除管理员账户
                    if ("admin".equals(user.getRole())) {
                        log.warn("不能删除管理员账户，跳过: userId={}", userId);
                        continue;
                    }

                    // 执行软删除
                    int result = userManageMapper.deleteUser(userId);
                    if (result > 0) {
                        successCount++;
                        log.info("用户删除成功: userId={}", userId);
                    }
                } catch (Exception e) {
                    log.error("删除用户失败，跳过: userId={}", userId, e);
                }
            }
            
            log.info("批量删除用户完成: 成功数量={}", successCount);
            return successCount;
        } catch (Exception e) {
            log.error("批量删除用户失败: userIds={}", userIds, e);
            throw new RuntimeException("批量删除用户失败: " + e.getMessage());
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