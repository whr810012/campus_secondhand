package com.campus.secondhand.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 登录响应DTO
 *
 * @author campus-secondhand
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    /**
     * JWT Token
     */
    private String token;

    /**
     * 用户信息
     */
    private UserInfo user;

    /**
     * 用户信息内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        /**
         * 用户ID
         */
        private Long id;

        /**
         * 手机号
         */
        private String phone;

        /**
         * 昵称
         */
        private String nickname;

        /**
         * 头像
         */
        private String avatar;

        /**
         * 学号
         */
        private String studentId;

        /**
         * 学校名称
         */
        private String schoolName;

        /**
         * 真实姓名
         */
        private String realName;

        /**
         * 性别：0-未知，1-男，2-女
         */
        private Integer gender;

        /**
         * 角色：user-普通用户，admin-管理员
         */
        private String role;

        /**
         * 账户状态：0-正常，1-禁用
         */
        private Integer status;

        /**
         * 学生认证状态：0-未认证，1-认证中，2-已认证，3-认证失败
         */
        private Integer verifyStatus;

        /**
         * 信誉分数
         */
        private Integer creditScore;

        /**
         * 创建时间
         */
        private LocalDateTime createdAt;

        /**
         * 最后登录时间
         */
        private LocalDateTime lastLoginTime;
    }
}