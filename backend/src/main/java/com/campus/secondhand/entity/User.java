package com.campus.secondhand.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户实体类
 *
 * @author campus-secondhand
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("users")
public class User {

    /**
     * 用户ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 密码
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 头像数据（base64）
     */
    @TableField(exist = false)
    private String avatarData;

    /**
     * 学号
     */
    private String studentId;

    /**
     * 学校ID
     */
    private Long schoolId;

    /**
     * 学校名称
     */
    @TableField(exist = false)
    private String schoolName;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 身份证号
     */
    private String idCard;

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
     * 认证失败原因
     */
    private String verifyReason;

    /**
     * 身份证图片ID
     */
    private Long idCardImgId;

    /**
     * 学生证图片ID
     */
    private Long studentCardImgId;

    /**
     * 身份证图片数据（base64）
     */
    @TableField(exist = false)
    private String idCardImgData;

    /**
     * 学生证图片数据（base64）
     */
    @TableField(exist = false)
    private String studentCardImgData;

    /**
     * 信誉分数
     */
    private Integer creditScore;

    /**
     * 交易次数
     */
    private Integer tradeCount;

    /**
     * 好评率
     */
    private Double goodRate;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 最后登录IP
     */
    private String lastLoginIp;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer deleted;

    /**
     * 封禁原因
     */
    private String banReason;

    /**
     * 封禁结束时间
     */
    private LocalDateTime banEndTime;

}