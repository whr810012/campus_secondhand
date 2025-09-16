package com.campus.secondhand.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.common.Result;
import com.campus.secondhand.entity.Message;
import com.campus.secondhand.service.MessageService;
import com.campus.secondhand.service.MessageService.ChatListItem;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 消息控制器
 *
 * @author campus-secondhand
 */
@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    /**
     * 发送消息
     */
    @PostMapping
    public Result<Message> sendMessage(@RequestBody SendMessageRequest request) {
        try {
            Message message = messageService.sendMessage(
                request.getSenderId(),
                request.getReceiverId(),
                request.getProductId(),
                request.getContent(),
                request.getType()
            );
            return Result.success(message);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 分页查询聊天记录
     */
    @GetMapping("/chat")
    public Result<Page<Message>> getChatMessages(
            @RequestParam Long userId1,
            @RequestParam Long userId2,
            @RequestParam(required = false) Long productId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Message> messages = messageService.getChatMessages(userId1, userId2, productId, page, size);
        return Result.success(messages);
    }

    /**
     * 获取用户的聊天列表
     */
    @GetMapping("/chats/{userId}")
    public Result<Page<ChatListItem>> getChatList(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ChatListItem> chatList = messageService.getChatList(userId, page, size);
        return Result.success(chatList);
    }

    /**
     * 标记消息为已读
     */
    @PutMapping("/{messageId}/read")
    public Result<Void> markAsRead(@PathVariable Long messageId, @RequestParam Long userId) {
        try {
            boolean success = messageService.markAsRead(messageId, userId);
            if (success) {
                return Result.success();
            } else {
                return Result.error("标记失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 批量标记聊天消息为已读
     */
    @PutMapping("/chat/read")
    public Result<Integer> markChatAsRead(@RequestBody MarkChatReadRequest request) {
        int count = messageService.markChatAsRead(
            request.getSenderId(),
            request.getReceiverId(),
            request.getProductId()
        );
        return Result.success(count);
    }

    /**
     * 获取未读消息数量
     */
    @GetMapping("/unread/{userId}")
    public Result<Long> getUnreadCount(@PathVariable Long userId) {
        long count = messageService.getUnreadCount(userId);
        return Result.success(count);
    }

    /**
     * 删除消息
     */
    @DeleteMapping("/{messageId}")
    public Result<Void> deleteMessage(@PathVariable Long messageId, @RequestParam Long userId) {
        try {
            boolean success = messageService.deleteMessage(messageId, userId);
            if (success) {
                return Result.success();
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // 内部类定义请求参数
    public static class SendMessageRequest {
        private Long senderId;
        private Long receiverId;
        private Long productId;
        private String content;
        private String type;

        public Long getSenderId() { return senderId; }
        public void setSenderId(Long senderId) { this.senderId = senderId; }
        public Long getReceiverId() { return receiverId; }
        public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }

    public static class MarkChatReadRequest {
        private Long senderId;
        private Long receiverId;
        private Long productId;

        public Long getSenderId() { return senderId; }
        public void setSenderId(Long senderId) { this.senderId = senderId; }
        public Long getReceiverId() { return receiverId; }
        public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
    }
}