package com.campus.secondhand.controller;

import com.campus.secondhand.common.Result;
import com.campus.secondhand.dto.LoginRequest;
import com.campus.secondhand.dto.RegisterRequest;
import com.campus.secondhand.dto.ResetPasswordRequest;
import com.campus.secondhand.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 *
 * @author campus-secondhand
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<?> login(@Validated @RequestBody LoginRequest request) {
        return Result.success(authService.login(request));
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<?> register(@Validated @RequestBody RegisterRequest request) {
        authService.register(request);
        return Result.success("注册成功");
    }

    /**
     * 发送注册验证码
     */
    @PostMapping("/send-register-code")
    public Result<?> sendRegisterCode(@RequestParam String phone) {
        authService.sendRegisterCode(phone);
        return Result.success("验证码已发送");
    }

    /**
     * 发送重置密码验证码
     */
    @PostMapping("/send-reset-code")
    public Result<?> sendResetCode(@RequestParam String phone) {
        authService.sendResetCode(phone);
        return Result.success("验证码已发送");
    }

    /**
     * 重置密码
     */
    @PostMapping("/reset-password")
    public Result<?> resetPassword(@Validated @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return Result.success("密码重置成功");
    }

    /**
     * 刷新Token
     */
    @PostMapping("/refresh")
    public Result<?> refreshToken(@RequestHeader("Authorization") String token) {
        return Result.success(authService.refreshToken(token));
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public Result<?> logout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
        return Result.success("退出成功");
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public Result<?> getCurrentUser(@RequestHeader("Authorization") String token) {
        return Result.success(authService.getCurrentUser(token));
    }

    /**
     * 学生身份认证申请
     */
    @PostMapping("/verify-student")
    public Result<?> verifyStudent(@RequestHeader("Authorization") String token, @RequestBody StudentVerifyRequest request) {
        authService.verifyStudent(token, request);
        return Result.success("认证申请提交成功");
    }

    /**
     * 获取认证状态
     */
    @GetMapping("/verify-status")
    public Result<?> getVerifyStatus(@RequestHeader("Authorization") String token) {
        return Result.success(authService.getVerifyStatus(token));
    }

    /**
     * 学生认证申请请求类
     */
    public static class StudentVerifyRequest {
        private String realName;
        private String idCard;
        private String studentId;
        private Long schoolId;
        private java.util.List<String> verifyImages;

        // Getters and Setters
        public String getRealName() { return realName; }
        public void setRealName(String realName) { this.realName = realName; }
        public String getIdCard() { return idCard; }
        public void setIdCard(String idCard) { this.idCard = idCard; }
        public String getStudentId() { return studentId; }
        public void setStudentId(String studentId) { this.studentId = studentId; }
        public Long getSchoolId() { return schoolId; }
        public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }
        public java.util.List<String> getVerifyImages() { return verifyImages; }
        public void setVerifyImages(java.util.List<String> verifyImages) { this.verifyImages = verifyImages; }
    }

}