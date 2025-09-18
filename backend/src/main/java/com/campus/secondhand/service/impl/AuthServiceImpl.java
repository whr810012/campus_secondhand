package com.campus.secondhand.service.impl;

import com.campus.secondhand.dto.LoginRequest;
import com.campus.secondhand.dto.LoginResponse;
import com.campus.secondhand.dto.RegisterRequest;
import com.campus.secondhand.dto.ResetPasswordRequest;
import com.campus.secondhand.entity.Img;
import com.campus.secondhand.entity.User;
import com.campus.secondhand.mapper.UserMapper;
import com.campus.secondhand.service.AuthService;
import com.campus.secondhand.service.ImgService;
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
    private final ImgService imgService;

    @Override
    public Object login(LoginRequest request) {
        log.info("用户登录: {}", request.getPhone());
        
        // 1. 根据手机号查询用户
        User user = userMapper.selectByPhone(request.getPhone());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 2. 验证密码（明文比较）
        if (!request.getPassword().equals(user.getPassword())) {
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
        log.info("用户注册: {}", request.getPhone());
        
        // 1. 检查手机号是否已注册
        User existUser = userMapper.selectByPhone(request.getPhone());
        if (existUser != null) {
            throw new RuntimeException("手机号已注册");
        }
        
        // 2. 创建新用户
        User user = new User();
        user.setPhone(request.getPhone());
        user.setPassword(request.getPassword()); // 直接存储明文密码
        user.setNickname(request.getNickname());
        user.setStudentId(request.getStudentId());
        user.setSchoolId(request.getSchoolId());
        user.setRole("user");
        user.setStatus(0); // 正常状态
        user.setVerifyStatus(0); // 未认证
        user.setCreditScore(100);
        user.setTradeCount(0);
        user.setGoodRate(100.0);
        user.setDeleted(0);
        
        // 3. 保存用户
        int result = userMapper.insert(user);
        if (result <= 0) {
            throw new RuntimeException("注册失败");
        }
        
        log.info("用户注册成功: {}", request.getPhone());
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

    @Override
    public Object getCurrentUser(String token) {
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
    public void verifyStudent(String token, com.campus.secondhand.controller.AuthController.StudentVerifyRequest request) {
        try {
            log.info("学生身份认证申请: {}", request.getRealName());
            
            // 从token中获取用户ID
            Long userId = jwtUtil.getUserIdFromToken(token.replace("Bearer ", ""));
            
            // 查询用户信息
            User user = userMapper.selectById(userId);
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }
            
            // 更新用户认证信息
            user.setRealName(request.getRealName());
            user.setIdCard(request.getIdCard());
            user.setStudentId(request.getStudentId());
            user.setSchoolId(request.getSchoolId());
            user.setVerifyStatus(1); // 设置为待审核状态
            user.setUpdatedAt(LocalDateTime.now());
            
            // 保存认证图片到数据库
            if (request.getVerifyImages() != null && !request.getVerifyImages().isEmpty()) {
                log.info("开始保存认证图片，数量: {}", request.getVerifyImages().size());
                
                for (int i = 0; i < request.getVerifyImages().size(); i++) {
                    String base64Data = request.getVerifyImages().get(i);
                    if (base64Data != null && !base64Data.trim().isEmpty()) {
                        String imageName;
                        if (i == 0) {
                            // 第一张图片作为身份证照片
                            imageName = "id_card_" + userId + "_" + System.currentTimeMillis();
                            Img savedImg = imgService.saveImg(imageName, base64Data, userId);
                            if (savedImg != null) {
                                user.setIdCardImgId(savedImg.getId());
                                log.info("身份证图片保存成功，ID: {}", savedImg.getId());
                            }
                        } else if (i == 1) {
                            // 第二张图片作为学生证照片
                            imageName = "student_card_" + userId + "_" + System.currentTimeMillis();
                            Img savedImg = imgService.saveImg(imageName, base64Data, userId);
                            if (savedImg != null) {
                                user.setStudentCardImgId(savedImg.getId());
                                log.info("学生证图片保存成功，ID: {}", savedImg.getId());
                            }
                        }
                    }
                }
            }
            
            userMapper.updateById(user);
            log.info("学生身份认证申请提交成功: userId={}", userId);
            
        } catch (Exception e) {
            log.error("学生身份认证申请失败: {}", e.getMessage());
            throw new RuntimeException("认证申请提交失败: " + e.getMessage());
        }
    }

    @Override
    public Object getVerifyStatus(String token) {
        try {
            log.info("获取认证状态");
            
            // 从token中获取用户ID
            Long userId = jwtUtil.getUserIdFromToken(token.replace("Bearer ", ""));
            
            // 查询用户信息
            User user = userMapper.selectById(userId);
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }
            
            // 构造返回数据
            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("status", user.getVerifyStatus());
            result.put("realName", user.getRealName());
            result.put("studentId", user.getStudentId());
            result.put("schoolId", user.getSchoolId());
            result.put("rejectionReason", user.getVerifyReason());
            
            // 如果有学校信息，可以查询学校名称
            if (user.getSchoolId() != null) {
                // 这里可以查询学校表获取学校名称
                // School school = schoolMapper.selectById(user.getSchoolId());
                // result.put("schoolName", school != null ? school.getName() : null);
            }
            
            return result;
            
        } catch (Exception e) {
            log.error("获取认证状态失败: {}", e.getMessage());
            throw new RuntimeException("获取认证状态失败: " + e.getMessage());
        }
    }
}