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

}