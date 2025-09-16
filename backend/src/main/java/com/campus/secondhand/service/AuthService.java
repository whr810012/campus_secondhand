package com.campus.secondhand.service;

import com.campus.secondhand.dto.LoginRequest;
import com.campus.secondhand.dto.RegisterRequest;
import com.campus.secondhand.dto.ResetPasswordRequest;

/**
 * 认证服务接口
 *
 * @author campus-secondhand
 */
public interface AuthService {

    /**
     * 用户登录
     */
    Object login(LoginRequest request);

    /**
     * 用户注册
     */
    void register(RegisterRequest request);

    /**
     * 发送注册验证码
     */
    void sendRegisterCode(String phone);

    /**
     * 发送重置密码验证码
     */
    void sendResetCode(String phone);

    /**
     * 重置密码
     */
    void resetPassword(ResetPasswordRequest request);

    /**
     * 刷新Token
     */
    Object refreshToken(String token);

    /**
     * 用户登出
     */
    void logout(String token);
}