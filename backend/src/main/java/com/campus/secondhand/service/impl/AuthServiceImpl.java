package com.campus.secondhand.service.impl;

import com.campus.secondhand.dto.LoginRequest;
import com.campus.secondhand.dto.RegisterRequest;
import com.campus.secondhand.dto.ResetPasswordRequest;
import com.campus.secondhand.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现类
 *
 * @author campus-secondhand
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Override
    public Object login(LoginRequest request) {
        // TODO: 实现登录逻辑
        log.info("用户登录: {}", request.getPhone());
        return "登录成功";
    }

    @Override
    public void register(RegisterRequest request) {
        // TODO: 实现注册逻辑
        log.info("用户注册: {}", request.getPhone());
    }

    @Override
    public void sendRegisterCode(String phone) {
        // TODO: 实现发送注册验证码逻辑
        log.info("发送注册验证码: {}", phone);
    }

    @Override
    public void sendResetCode(String phone) {
        // TODO: 实现发送重置密码验证码逻辑
        log.info("发送重置密码验证码: {}", phone);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        // TODO: 实现重置密码逻辑
        log.info("重置密码: {}", request.getPhone());
    }

    @Override
    public Object refreshToken(String token) {
        // TODO: 实现刷新Token逻辑
        log.info("刷新Token");
        return "Token刷新成功";
    }

    @Override
    public void logout(String token) {
        // TODO: 实现登出逻辑
        log.info("用户登出");
    }
}