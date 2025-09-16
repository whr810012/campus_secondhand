package com.campus.secondhand.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审核日志实体类
 *
 * @author campus-secondhand
 */
@Data
@TableName("audit_logs")
public class AuditLog {

    /**
     * 审核日志ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 审核类型（product: 商品, review: 评价）
     */
    private String type;

    /**
     * 目标ID（商品ID或评价ID）
     */
    private Long targetId;

    /**
     * 管理员ID
     */
    private Long adminId;

    /**
     * 审核状态（approved: 通过, rejected: 拒绝）
     */
    private String status;

    /**
     * 审核理由
     */
    private String reason;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}