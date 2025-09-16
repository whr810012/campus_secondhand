package com.campus.common;

/**
 * 响应状态码枚举
 * 
 * @author Campus Team
 */
public enum ResultCode {
    
    // 通用状态码
    SUCCESS(200, "操作成功"),
    ERROR(500, "操作失败"),
    PARAM_ERROR(400, "参数错误"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    
    // 认证授权相关
    UNAUTHORIZED(401, "未授权访问"),
    FORBIDDEN(403, "权限不足"),
    TOKEN_INVALID(4001, "Token无效"),
    TOKEN_EXPIRED(4002, "Token已过期"),
    LOGIN_REQUIRED(4003, "请先登录"),
    
    // 用户相关
    USER_NOT_FOUND(5001, "用户不存在"),
    USER_DISABLED(5002, "用户已被禁用"),
    USERNAME_EXISTS(5003, "用户名已存在"),
    EMAIL_EXISTS(5004, "邮箱已存在"),
    PASSWORD_ERROR(5005, "密码错误"),
    OLD_PASSWORD_ERROR(5006, "原密码错误"),
    
    // 商品相关
    PRODUCT_NOT_FOUND(6001, "商品不存在"),
    PRODUCT_OFFLINE(6002, "商品已下架"),
    PRODUCT_SOLD_OUT(6003, "商品已售出"),
    PRODUCT_AUDIT_PENDING(6004, "商品待审核"),
    PRODUCT_AUDIT_REJECTED(6005, "商品审核被拒绝"),
    CANNOT_BUY_OWN_PRODUCT(6006, "不能购买自己的商品"),
    
    // 订单相关
    ORDER_NOT_FOUND(7001, "订单不存在"),
    ORDER_STATUS_ERROR(7002, "订单状态错误"),
    ORDER_CANNOT_CANCEL(7003, "订单无法取消"),
    
    // 分类相关
    CATEGORY_NOT_FOUND(8001, "分类不存在"),
    CATEGORY_HAS_PRODUCTS(8002, "分类下存在商品，无法删除"),
    
    // 地址相关
    ADDRESS_NOT_FOUND(9001, "地址不存在"),
    
    // 文件上传相关
    FILE_UPLOAD_ERROR(10001, "文件上传失败"),
    FILE_TYPE_ERROR(10002, "文件类型不支持"),
    FILE_SIZE_ERROR(10003, "文件大小超出限制");
    
    private final Integer code;
    private final String message;
    
    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
}