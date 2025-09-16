package com.campus.secondhand.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.entity.Message;

/**
 * 消息服务接口
 *
 * @author campus-secondhand
 */
public interface MessageService {

    /**
     * 发送消息
     *
     * @param senderId 发送者ID
     * @param receiverId 接收者ID
     * @param productId 商品ID（可选）
     * @param content 消息内容
     * @param type 消息类型
     * @return 消息
     */
    Message sendMessage(Long senderId, Long receiverId, Long productId, String content, String type);

    /**
     * 分页查询聊天记录
     *
     * @param userId1 用户1ID
     * @param userId2 用户2ID
     * @param productId 商品ID（可选）
     * @param page 页码
     * @param size 每页大小
     * @return 消息分页数据
     */
    Page<Message> getChatMessages(Long userId1, Long userId2, Long productId, int page, int size);

    /**
     * 获取用户的聊天列表
     *
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 聊天列表
     */
    Page<ChatListItem> getChatList(Long userId, int page, int size);

    /**
     * 标记消息为已读
     *
     * @param messageId 消息ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean markAsRead(Long messageId, Long userId);

    /**
     * 批量标记消息为已读
     *
     * @param senderId 发送者ID
     * @param receiverId 接收者ID
     * @param productId 商品ID（可选）
     * @return 标记数量
     */
    int markChatAsRead(Long senderId, Long receiverId, Long productId);

    /**
     * 获取未读消息数量
     *
     * @param userId 用户ID
     * @return 未读消息数量
     */
    long getUnreadCount(Long userId);

    /**
     * 删除消息
     *
     * @param messageId 消息ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean deleteMessage(Long messageId, Long userId);

    /**
     * 聊天列表项
     */
    class ChatListItem {
        private Long userId;
        private String nickname;
        private String avatar;
        private Long productId;
        private String productTitle;
        private String lastMessage;
        private String lastMessageTime;
        private Long unreadCount;

        // Getters and Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
        public String getAvatar() { return avatar; }
        public void setAvatar(String avatar) { this.avatar = avatar; }
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public String getProductTitle() { return productTitle; }
        public void setProductTitle(String productTitle) { this.productTitle = productTitle; }
        public String getLastMessage() { return lastMessage; }
        public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }
        public String getLastMessageTime() { return lastMessageTime; }
        public void setLastMessageTime(String lastMessageTime) { this.lastMessageTime = lastMessageTime; }
        public Long getUnreadCount() { return unreadCount; }
        public void setUnreadCount(Long unreadCount) { this.unreadCount = unreadCount; }
    }
}