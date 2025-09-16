package com.campus.secondhand.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应码枚举
 *
 * @author campus-secondhand
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 失败
     */
    ERROR(500, "操作失败"),

    /**
     * 参数错误
     */
    PARAM_ERROR(400, "参数错误"),

    /**
     * 未授权
     */
    UNAUTHORIZED(401, "未授权"),

    /**
     * 禁止访问
     */
    FORBIDDEN(403, "禁止访问"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 请求方法不支持
     */
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),

    /**
     * 请求超时
     */
    REQUEST_TIMEOUT(408, "请求超时"),

    /**
     * 用户不存在
     */
    USER_NOT_FOUND(1001, "用户不存在"),

    /**
     * 用户已存在
     */
    USER_ALREADY_EXISTS(1002, "用户已存在"),

    /**
     * 密码错误
     */
    PASSWORD_ERROR(1003, "密码错误"),

    /**
     * 账户被禁用
     */
    ACCOUNT_DISABLED(1004, "账户被禁用"),

    /**
     * 验证码错误
     */
    VERIFY_CODE_ERROR(1005, "验证码错误"),

    /**
     * 验证码已过期
     */
    VERIFY_CODE_EXPIRED(1006, "验证码已过期"),

    /**
     * Token无效
     */
    TOKEN_INVALID(1007, "Token无效"),

    /**
     * Token已过期
     */
    TOKEN_EXPIRED(1008, "Token已过期"),

    /**
     * 学生认证未通过
     */
    STUDENT_NOT_VERIFIED(1009, "学生认证未通过"),

    /**
     * 商品不存在
     */
    PRODUCT_NOT_FOUND(2001, "商品不存在"),

    /**
     * 商品已下架
     */
    PRODUCT_UNAVAILABLE(2002, "商品已下架"),

    /**
     * 商品已售出
     */
    PRODUCT_SOLD(2003, "商品已售出"),

    /**
     * 不能购买自己的商品
     */
    CANNOT_BUY_OWN_PRODUCT(2004, "不能购买自己的商品"),

    /**
     * 订单不存在
     */
    ORDER_NOT_FOUND(3001, "订单不存在"),

    /**
     * 订单状态错误
     */
    ORDER_STATUS_ERROR(3002, "订单状态错误"),

    /**
     * 支付失败
     */
    PAYMENT_FAILED(3003, "支付失败"),

    /**
     * 文件上传失败
     */
    FILE_UPLOAD_ERROR(4001, "文件上传失败"),

    /**
     * 文件类型不支持
     */
    FILE_TYPE_NOT_SUPPORTED(4002, "文件类型不支持"),

    /**
     * 文件大小超限
     */
    FILE_SIZE_EXCEEDED(4003, "文件大小超限"),

    /**
     * 短信发送失败
     */
    SMS_SEND_ERROR(5001, "短信发送失败"),

    /**
     * 邮件发送失败
     */
    EMAIL_SEND_ERROR(5002, "邮件发送失败"),

    /**
     * 数据库操作失败
     */
    DATABASE_ERROR(6001, "数据库操作失败"),

    /**
     * Redis操作失败
     */
    REDIS_ERROR(6002, "Redis操作失败");

    /**
     * 响应码
     */
    private final Integer code;

    /**
     * 响应消息
     */
    private final String message;

}