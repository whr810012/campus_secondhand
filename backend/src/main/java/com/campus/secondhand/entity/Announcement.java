package com.campus.secondhand.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 公告实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("announcements")
public class Announcement {

    /**
     * 公告ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 公告标题
     */
    @TableField("title")
    private String title;

    /**
     * 公告内容
     */
    @TableField("content")
    private String content;

    /**
     * 公告类型：normal-普通，important-重要，urgent-紧急
     */
    @TableField("type")
    private String type;

    /**
     * 状态：0-隐藏，1-显示
     */
    @TableField("status")
    private Integer status;

    /**
     * 发布人ID
     */
    @TableField("author_id")
    private Long authorId;

    /**
     * 发布时间
     */
    @TableField("publish_time")
    private LocalDateTime publishTime;

    /**
     * 过期时间
     */
    @TableField("expire_time")
    private LocalDateTime expireTime;

    /**
     * 浏览次数
     */
    @TableField("view_count")
    private Integer viewCount;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 逻辑删除
     */
    @TableField("deleted")
    @TableLogic
    private Integer deleted;

    // 前端需要的额外字段（非数据库字段）
    /**
     * 发布者名称（关联查询获得）
     */
    @TableField(exist = false)
    private String author;

    /**
     * 是否置顶（根据业务逻辑判断）
     */
    @TableField(exist = false)
    private Boolean isTop;

    /**
     * 状态标签（前端显示用）
     */
    @TableField(exist = false)
    private String statusLabel;

    /**
     * 类型标签（前端显示用）
     */
    @TableField(exist = false)
    private String typeLabel;
}