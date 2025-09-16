package com.campus.secondhand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.entity.Message;
import com.campus.secondhand.service.MessageService.ChatListItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 消息数据访问层
 *
 * @author campus-secondhand
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {

    /**
     * 分页查询聊天记录
     */
    @Select({
        "<script>",
        "SELECT * FROM messages",
        "WHERE deleted = 0",
        "AND ((",
        "  (sender_id = #{userId1} AND receiver_id = #{userId2})",
        "  OR (sender_id = #{userId2} AND receiver_id = #{userId1})",
        "))",
        "<if test='productId != null'>",
        "  AND product_id = #{productId}",
        "</if>",
        "ORDER BY created_at DESC",
        "</script>"
    })
    Page<Message> selectChatMessages(Page<Message> page, 
                                   @Param("userId1") Long userId1, 
                                   @Param("userId2") Long userId2,
                                   @Param("productId") Long productId);

    /**
     * 获取用户的聊天列表
     */
    @Select({
        "SELECT DISTINCT",
        "  CASE ",
        "    WHEN m.sender_id = #{userId} THEN m.receiver_id",
        "    ELSE m.sender_id",
        "  END as userId,",
        "  u.nickname,",
        "  u.avatar,",
        "  m.product_id as productId,",
        "  p.title as productTitle,",
        "  (",
        "    SELECT content FROM messages m2",
        "    WHERE m2.deleted = 0",
        "    AND ((",
        "      (m2.sender_id = #{userId} AND m2.receiver_id = CASE WHEN m.sender_id = #{userId} THEN m.receiver_id ELSE m.sender_id END)",
        "      OR (m2.sender_id = CASE WHEN m.sender_id = #{userId} THEN m.receiver_id ELSE m.sender_id END AND m2.receiver_id = #{userId})",
        "    ))",
        "    AND (m.product_id IS NULL OR m2.product_id = m.product_id)",
        "    ORDER BY m2.created_at DESC",
        "    LIMIT 1",
        "  ) as lastMessage,",
        "  (",
        "    SELECT DATE_FORMAT(created_at, '%Y-%m-%d %H:%i') FROM messages m2",
        "    WHERE m2.deleted = 0",
        "    AND ((",
        "      (m2.sender_id = #{userId} AND m2.receiver_id = CASE WHEN m.sender_id = #{userId} THEN m.receiver_id ELSE m.sender_id END)",
        "      OR (m2.sender_id = CASE WHEN m.sender_id = #{userId} THEN m.receiver_id ELSE m.sender_id END AND m2.receiver_id = #{userId})",
        "    ))",
        "    AND (m.product_id IS NULL OR m2.product_id = m.product_id)",
        "    ORDER BY m2.created_at DESC",
        "    LIMIT 1",
        "  ) as lastMessageTime,",
        "  (",
        "    SELECT COUNT(*) FROM messages m3",
        "    WHERE m3.deleted = 0",
        "    AND m3.receiver_id = #{userId}",
        "    AND m3.sender_id = CASE WHEN m.sender_id = #{userId} THEN m.receiver_id ELSE m.sender_id END",
        "    AND m3.is_read = 0",
        "    AND (m.product_id IS NULL OR m3.product_id = m.product_id)",
        "  ) as unreadCount",
        "FROM messages m",
        "LEFT JOIN users u ON u.id = CASE WHEN m.sender_id = #{userId} THEN m.receiver_id ELSE m.sender_id END",
        "LEFT JOIN products p ON p.id = m.product_id",
        "WHERE m.deleted = 0",
        "AND (m.sender_id = #{userId} OR m.receiver_id = #{userId})",
        "ORDER BY lastMessageTime DESC"
    })
    Page<ChatListItem> selectChatList(Page<ChatListItem> page, @Param("userId") Long userId);

    /**
     * 批量标记消息为已读
     */
    @Update({
        "<script>",
        "UPDATE messages SET is_read = 1",
        "WHERE deleted = 0",
        "AND receiver_id = #{receiverId}",
        "AND sender_id = #{senderId}",
        "AND is_read = 0",
        "<if test='productId != null'>",
        "  AND product_id = #{productId}",
        "</if>",
        "</script>"
    })
    int markChatAsRead(@Param("senderId") Long senderId, 
                      @Param("receiverId") Long receiverId,
                      @Param("productId") Long productId);

    /**
     * 获取未读消息数量
     */
    @Select("SELECT COUNT(*) FROM messages WHERE deleted = 0 AND receiver_id = #{userId} AND is_read = 0")
    long selectUnreadCount(@Param("userId") Long userId);
}