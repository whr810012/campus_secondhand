package com.campus.secondhand.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * 消息实体类
 *
 * @author campus-secondhand
 */
@TableName("messages")
public class Message {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("sender_id")
    private Long senderId;
    
    @TableField("receiver_id")
    private Long receiverId;
    
    @TableField("product_id")
    private Long productId;
    
    @TableField("content")
    private String content;
    
    @TableField("type")
    private String type;
    
    @TableField("is_read")
    private Boolean isRead;
    
    @TableField("read_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime readTime;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @TableLogic
    @TableField("deleted")
    private Boolean deleted;
    
    // 构造函数
    public Message() {}
    
    public Message(Long senderId, Long receiverId, Long productId, String content, String type) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.productId = productId;
        this.content = content;
        this.type = type;
        this.isRead = false;
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getSenderId() {
        return senderId;
    }
    
    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }
    
    public Long getReceiverId() {
        return receiverId;
    }
    
    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }
    
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public Boolean getIsRead() {
        return isRead;
    }
    
    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }
    
    public LocalDateTime getReadTime() {
        return readTime;
    }
    
    public void setReadTime(LocalDateTime readTime) {
        this.readTime = readTime;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public Boolean getDeleted() {
        return deleted;
    }
    
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
    
    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", senderId=" + senderId +
                ", receiverId=" + receiverId +
                ", productId=" + productId +
                ", content='" + content + '\'' +
                ", type='" + type + '\'' +
                ", isRead=" + isRead +
                ", readTime=" + readTime +
                ", createdAt=" + createdAt +
                ", deleted=" + deleted +
                '}';
    }
}