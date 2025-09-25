package com.campus.secondhand.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.common.Result;
import com.campus.secondhand.entity.User;
import com.campus.secondhand.service.UserManageService;
import com.campus.secondhand.service.UserManageService.UserDetail;
import com.campus.secondhand.service.UserManageService.UserStats;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器
 *
 * @author campus-secondhand
 */
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserManageController {

    private final UserManageService userManageService;

    /**
     * 分页查询用户列表
     */
    @GetMapping
    public Result<?> getUserList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String verifyStatus,
            @RequestParam(required = false) String sortBy) {
        
        // 如果有sortBy参数，使用管理员接口
        if (sortBy != null && !sortBy.isEmpty()) {
            java.util.Map<String, Object> result = userManageService.getUserList(page, size, sortBy, keyword);
            return Result.success(result);
        } else {
            // 否则使用原有接口
            Page<User> users = userManageService.getUserList(page, size, keyword, status, verifyStatus);
            return Result.success(users);
        }
    }

    /**
     * 获取用户详细信息
     */
    @GetMapping("/{userId}")
    public Result<UserDetail> getUserDetail(@PathVariable Long userId) {
        UserDetail userDetail = userManageService.getUserDetail(userId);
        if (userDetail != null) {
            return Result.success(userDetail);
        } else {
            return Result.error("用户不存在");
        }
    }

    /**
     * 封禁用户
     */
    @PostMapping("/{userId}/ban")
    public Result<Void> banUser(
            @PathVariable Long userId,
            @RequestBody BanUserRequest request) {
        try {
            boolean success = userManageService.banUser(
                userId, 
                request.getAdminId(), 
                request.getReason(), 
                request.getBanDays()
            );
            if (success) {
                return Result.success();
            } else {
                return Result.error("封禁用户失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 解封用户
     */
    @PostMapping("/{userId}/unban")
    public Result<Void> unbanUser(
            @PathVariable Long userId,
            @RequestBody UnbanUserRequest request) {
        try {
            boolean success = userManageService.unbanUser(userId, request.getAdminId());
            if (success) {
                return Result.success();
            } else {
                return Result.error("解封用户失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 批量封禁用户
     */
    @PostMapping("/batch-ban")
    public Result<Integer> batchBanUsers(@RequestBody BatchBanUsersRequest request) {
        try {
            int successCount = userManageService.batchBanUsers(
                request.getUserIds(),
                request.getAdminId(),
                request.getReason(),
                request.getBanDays()
            );
            return Result.success(successCount);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 分页查询待审核的学生身份认证
     */
    @GetMapping("/verifications/pending")
    public Result<Page<User>> getPendingVerifications(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        Page<User> users = userManageService.getPendingVerifications(page, size, keyword);
        return Result.success(users);
    }

    /**
     * 审核学生身份认证
     */
    @PostMapping("/verifications/{userId}")
    public Result<Void> verifyStudentIdentity(
            @PathVariable Long userId,
            @RequestBody VerifyIdentityRequest request) {
        try {
            boolean success = userManageService.verifyStudentIdentity(
                userId,
                request.getAdminId(),
                request.getStatus(),
                request.getReason()
            );
            if (success) {
                return Result.success();
            } else {
                return Result.error("审核失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 批量审核学生身份认证
     */
    @PostMapping("/verifications/batch")
    public Result<Integer> batchVerifyStudentIdentity(@RequestBody BatchVerifyIdentityRequest request) {
        try {
            int successCount = userManageService.batchVerifyStudentIdentity(
                request.getUserIds(),
                request.getAdminId(),
                request.getStatus(),
                request.getReason()
            );
            return Result.success(successCount);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取用户统计信息
     */
    @GetMapping("/stats")
    public Result<UserStats> getUserStats() {
        UserStats stats = userManageService.getUserStats();
        return Result.success(stats);
    }

    /**
     * 重置用户密码
     */
    @PostMapping("/{userId}/reset-password")
    public Result<String> resetUserPassword(
            @PathVariable Long userId,
            @RequestBody ResetPasswordRequest request) {
        try {
            String newPassword = userManageService.resetUserPassword(userId, request.getAdminId());
            return Result.success(newPassword);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // 内部类定义请求参数
    public static class BanUserRequest {
        private Long adminId;
        private String reason;
        private int banDays;

        public Long getAdminId() { return adminId; }
        public void setAdminId(Long adminId) { this.adminId = adminId; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        public int getBanDays() { return banDays; }
        public void setBanDays(int banDays) { this.banDays = banDays; }
    }

    public static class UnbanUserRequest {
        private Long adminId;

        public Long getAdminId() { return adminId; }
        public void setAdminId(Long adminId) { this.adminId = adminId; }
    }

    public static class BatchBanUsersRequest {
        private List<Long> userIds;
        private Long adminId;
        private String reason;
        private int banDays;

        public List<Long> getUserIds() { return userIds; }
        public void setUserIds(List<Long> userIds) { this.userIds = userIds; }
        public Long getAdminId() { return adminId; }
        public void setAdminId(Long adminId) { this.adminId = adminId; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        public int getBanDays() { return banDays; }
        public void setBanDays(int banDays) { this.banDays = banDays; }
    }

    public static class VerifyIdentityRequest {
        private Long adminId;
        private String status;
        private String reason;

        public Long getAdminId() { return adminId; }
        public void setAdminId(Long adminId) { this.adminId = adminId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }

    public static class BatchVerifyIdentityRequest {
        private List<Long> userIds;
        private Long adminId;
        private String status;
        private String reason;

        public List<Long> getUserIds() { return userIds; }
        public void setUserIds(List<Long> userIds) { this.userIds = userIds; }
        public Long getAdminId() { return adminId; }
        public void setAdminId(Long adminId) { this.adminId = adminId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }

    public static class ResetPasswordRequest {
        private Long adminId;

        public Long getAdminId() { return adminId; }
        public void setAdminId(Long adminId) { this.adminId = adminId; }
    }
}