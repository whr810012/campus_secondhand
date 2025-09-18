package com.campus.secondhand.service.impl;

import com.campus.secondhand.dto.LoginRequest;
import com.campus.secondhand.dto.LoginResponse;
import com.campus.secondhand.dto.RegisterRequest;
import com.campus.secondhand.dto.ResetPasswordRequest;
import com.campus.secondhand.entity.User;
import com.campus.secondhand.mapper.UserMapper;
import com.campus.secondhand.service.AuthService;
import com.campus.secondhand.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 认证服务实现类
 *
 * @author campus-secondhand
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public Object login(LoginRequest request) {
        log.info("用户登录: {}", request.getPhone());
        
        // 1. 根据手机号查询用户
        User user = userMapper.selectByPhone(request.getPhone());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 2. 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        
        // 3. 检查用户状态
        if (user.getStatus() != null && user.getStatus() == 1) {
            throw new RuntimeException("账户已被禁用");
        }
        
        // 4. 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);
        
        // 5. 生成JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getPhone(), user.getRole());
        
        // 6. 构建用户信息
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
            user.getId(),
            user.getPhone(),
            user.getNickname(),
            user.getAvatar(),
            user.getStudentId(),
            user.getSchoolName(),
            user.getRealName(),
            user.getGender(),
            user.getRole(),
            user.getStatus(),
            user.getVerifyStatus(),
            user.getCreditScore(),
            user.getCreatedAt(),
            user.getLastLoginTime()
        );
        
        // 7. 返回登录响应
        return new LoginResponse(token, userInfo);
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