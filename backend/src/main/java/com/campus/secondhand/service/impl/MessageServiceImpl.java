package com.campus.secondhand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.entity.Message;
import com.campus.secondhand.entity.Product;
import com.campus.secondhand.entity.User;
import com.campus.secondhand.mapper.MessageMapper;
import com.campus.secondhand.mapper.ProductMapper;
import com.campus.secondhand.mapper.UserMapper;
import com.campus.secondhand.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 消息服务实现类
 *
 * @author campus-secondhand
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageMapper messageMapper;
    private final UserMapper userMapper;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public Message sendMessage(Long senderId, Long receiverId, Long productId, String content, String type) {
        // 验证发送者和接收者
        User sender = userMapper.selectById(senderId);
        User receiver = userMapper.selectById(receiverId);
        if (sender == null || receiver == null) {
            throw new RuntimeException("用户不存在");
        }

        // 不能给自己发消息
        if (senderId.equals(receiverId)) {
            throw new RuntimeException("不能给自己发消息");
        }

        // 验证商品（如果指定了商品ID）
        if (productId != null) {
            Product product = productMapper.selectById(productId);
            if (product == null || product.getDeleted() == 1) {
                throw new RuntimeException("商品不存在");
            }
        }

        // 创建消息
        Message message = new Message();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setProductId(productId);
        message.setContent(content);
        message.setType(type != null ? type : "text");
        message.setIsRead(false);
        message.setDeleted(false);
        message.setCreatedAt(LocalDateTime.now());

        int result = messageMapper.insert(message);
        if (result > 0) {
            log.info("用户{}向用户{}发送消息成功，消息ID: {}", senderId, receiverId, message.getId());
            return message;
        } else {
            throw new RuntimeException("发送消息失败");
        }
    }

    @Override
    public Page<Message> getChatMessages(Long userId1, Long userId2, Long productId, int page, int size) {
        Page<Message> pageParam = new Page<>(page, size);
        return messageMapper.selectChatMessages(pageParam, userId1, userId2, productId);
    }

    @Override
    public Page<ChatListItem> getChatList(Long userId, int page, int size) {
        Page<ChatListItem> pageParam = new Page<>(page, size);
        return messageMapper.selectChatList(pageParam, userId);
    }

    @Override
    @Transactional
    public boolean markAsRead(Long messageId, Long userId) {
        // 只能标记发给自己的消息为已读
        LambdaUpdateWrapper<Message> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Message::getId, messageId)
                    .eq(Message::getReceiverId, userId)
                    .eq(Message::getDeleted, 0)
                    .set(Message::getIsRead, 1);
        
        int result = messageMapper.update(null, updateWrapper);
        if (result > 0) {
            log.info("用户{}标记消息{}为已读", userId, messageId);
        }
        return result > 0;
    }

    @Override
    @Transactional
    public int markChatAsRead(Long senderId, Long receiverId, Long productId) {
        int result = messageMapper.markChatAsRead(senderId, receiverId, productId);
        if (result > 0) {
            log.info("用户{}标记与用户{}的{}条消息为已读", receiverId, senderId, result);
        }
        return result;
    }

    @Override
    public long getUnreadCount(Long userId) {
        return messageMapper.selectUnreadCount(userId);
    }

    @Override
    @Transactional
    public boolean deleteMessage(Long messageId, Long userId) {
        // 只能删除自己发送或接收的消息
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Message::getId, messageId)
                   .and(wrapper -> wrapper.eq(Message::getSenderId, userId)
                                         .or()
                                         .eq(Message::getReceiverId, userId))
                   .eq(Message::getDeleted, 0);
        
        Message message = messageMapper.selectOne(queryWrapper);
        if (message == null) {
            throw new RuntimeException("消息不存在或无权限删除");
        }

        // 软删除
        LambdaUpdateWrapper<Message> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Message::getId, messageId)
                    .set(Message::getDeleted, 1);
        
        int result = messageMapper.update(null, updateWrapper);
        if (result > 0) {
            log.info("用户{}删除消息{}成功", userId, messageId);
        }
        return result > 0;
    }
}