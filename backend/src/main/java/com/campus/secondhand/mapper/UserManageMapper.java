package com.campus.secondhand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.entity.User;
import com.campus.secondhand.service.UserManageService.UserDetail;
import com.campus.secondhand.service.UserManageService.UserStats;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户管理数据访问层
 *
 * @author campus-secondhand
 */
@Mapper
public interface UserManageMapper extends BaseMapper<User> {

    /**
     * 分页查询用户列表
     */
    @Select({
        "<script>",
        "SELECT u.*, ",
        "  (SELECT i.base64_data FROM imgs i WHERE i.id = u.avatar AND i.status = 1) as avatar_data,",
        "  CASE WHEN u.ban_end_time &gt; NOW() THEN 'BANNED' ELSE u.status END as current_status",
        "FROM users u",
        "WHERE u.deleted = 0",
        "<if test='keyword != null and keyword != \"\"'>",
        "  AND (u.nickname LIKE CONCAT('&#37;', #{keyword}, '&#37;')",
        "    OR u.phone LIKE CONCAT('&#37;', #{keyword}, '&#37;')",
        "    OR u.student_id LIKE CONCAT('&#37;', #{keyword}, '&#37;'))",
        "</if>",
        "<if test='status != null and status != \"\"'>",
        "  <if test='status == \"BANNED\"'>",
        "    AND u.ban_end_time &gt; NOW()",
        "  </if>",
        "  <if test='status == \"ACTIVE\"'>",
        "    AND (u.ban_end_time IS NULL OR u.ban_end_time &lt;= NOW())",
        "    AND u.status = 'ACTIVE'",
        "  </if>",
        "</if>",
        "<if test='verifyStatus != null and verifyStatus != \"\"'>",
        "  AND u.verify_status = #{verifyStatus}",
        "</if>",
        "ORDER BY u.created_at DESC",
        "</script>"
    })
    Page<User> selectUserList(Page<User> page, 
                             @Param("keyword") String keyword,
                             @Param("status") String status,
                             @Param("verifyStatus") String verifyStatus);

    /**
     * 管理员分页查询用户列表
     */
    @Select({
        "<script>",
        "SELECT u.*, ",
        "  (SELECT i.base64_data FROM imgs i WHERE i.id = u.avatar AND i.status = 1) as avatar_data,",
        "  CASE WHEN u.ban_end_time &gt; NOW() THEN 'BANNED' ELSE u.status END as current_status",
        "FROM users u",
        "WHERE u.deleted = 0",
        "<if test='keyword != null and keyword != \"\"'>",
        "  AND (u.nickname LIKE CONCAT('&#37;', #{keyword}, '&#37;')",
        "    OR u.phone LIKE CONCAT('&#37;', #{keyword}, '&#37;')",
        "    OR u.student_id LIKE CONCAT('&#37;', #{keyword}, '&#37;')",
        "    OR u.real_name LIKE CONCAT('&#37;', #{keyword}, '&#37;'))",
        "</if>",
        "<choose>",
        "  <when test='sortBy == \"latest\"'>",
        "    ORDER BY u.created_at DESC",
        "  </when>",
        "  <when test='sortBy == \"oldest\"'>",
        "    ORDER BY u.created_at ASC",
        "  </when>",
        "  <when test='sortBy == \"name\"'>",
        "    ORDER BY u.nickname ASC",
        "  </when>",
        "  <otherwise>",
        "    ORDER BY u.created_at DESC",
        "  </otherwise>",
        "</choose>",
        "</script>"
    })
    Page<User> selectUserListForAdmin(Page<User> page, 
                                     @Param("sortBy") String sortBy,
                                     @Param("keyword") String keyword);

    /**
     * 封禁用户
     */
    @Update({
        "UPDATE users SET ",
        "  status = 1,",
        "  ban_reason = #{reason},",
        "  ban_end_time = #{banEndTime, jdbcType=TIMESTAMP},",
        "  updated_at = NOW() ",
        "WHERE id = #{userId} AND deleted = 0"
    })
    int banUser(@Param("userId") Long userId, 
                @Param("reason") String reason, 
                @Param("banEndTime") LocalDateTime banEndTime);

    /**
     * 解封用户
     */
    @Update({
        "UPDATE users SET ",
        "  status = 0,",
        "  ban_reason = NULL,",
        "  ban_end_time = NULL,",
        "  updated_at = NOW() ",
        "WHERE id = #{userId} AND deleted = 0"
    })
    int unbanUser(@Param("userId") Long userId);

    /**
     * 分页查询待审核的学生身份认证
     */
    @Select({
        "<script>",
        "SELECT * FROM users",
        "WHERE deleted = 0 AND verify_status = 'PENDING'",
        "<if test='keyword != null and keyword != \"\"'>",
        "  AND (username LIKE CONCAT('&#37;', #{keyword}, '&#37;')",
        "    OR phone LIKE CONCAT('&#37;', #{keyword}, '&#37;')",
        "    OR student_id LIKE CONCAT('&#37;', #{keyword}, '&#37;')",
        "    OR real_name LIKE CONCAT('&#37;', #{keyword}, '&#37;'))",
        "</if>",
        "ORDER BY created_at ASC",
        "</script>"
    })
    Page<User> selectPendingVerifications(Page<User> page, @Param("keyword") String keyword);

    /**
     * 审核学生身份认证
     */
    @Update({
        "UPDATE users SET ",
        "  verify_status = #{status},",
        "  verify_reason = #{reason, jdbcType=VARCHAR},",
        "  updated_at = NOW()",
        "WHERE id = #{userId} AND deleted = 0"
    })
    int verifyStudentIdentity(@Param("userId") Long userId, 
                             @Param("status") String status, 
                             @Param("reason") String reason);

    /**
     * 获取用户详细信息
     */
    @Select({
        "SELECT u.*,",
        "  (SELECT i.base64_data FROM imgs i WHERE i.id = u.avatar AND i.status = 1) as avatar_data,",
        "  (SELECT i.base64_data FROM imgs i WHERE i.id = u.id_card_img_id AND i.status = 1) as id_card_img_data,",
        "  (SELECT i.base64_data FROM imgs i WHERE i.id = u.student_card_img_id AND i.status = 1) as student_card_img_data,",
        "  (SELECT COUNT(*) FROM products p WHERE p.seller_id = u.id AND p.deleted = 0) as product_count,",
        "  (SELECT COUNT(*) FROM orders o WHERE (o.buyer_id = u.id OR o.seller_id = u.id) AND o.deleted = 0) as order_count,",
        "  (SELECT COUNT(*) FROM reviews r WHERE (r.reviewer_id = u.id OR r.reviewee_id = u.id) AND r.status = 1) as review_count,",
        "  (SELECT AVG(r.rating) FROM reviews r WHERE r.reviewee_id = u.id AND r.status = 1) as avg_rating,",
        "  u.last_login_time as last_login_time",
        "FROM users u",
        "WHERE u.id = #{userId} AND u.deleted = 0"
    })
    @Results({
        @Result(column = "id", property = "user.id"),
        @Result(column = "username", property = "user.username"),
        @Result(column = "phone", property = "user.phone"),
        @Result(column = "email", property = "user.email"),
        @Result(column = "real_name", property = "user.realName"),
        @Result(column = "student_id", property = "user.studentId"),
        @Result(column = "school", property = "user.school"),
        @Result(column = "major", property = "user.major"),
        @Result(column = "avatar", property = "user.avatar"),
        @Result(column = "avatar_data", property = "user.avatarData"),
        @Result(column = "id_card_img_data", property = "user.idCardImgData"),
        @Result(column = "student_card_img_data", property = "user.studentCardImgData"),
        @Result(column = "status", property = "user.status"),
        @Result(column = "verify_status", property = "user.verifyStatus"),
        @Result(column = "verify_reason", property = "user.verifyReason"),
        @Result(column = "id_card_img_id", property = "user.idCardImgId"),
        @Result(column = "student_card_img_id", property = "user.studentCardImgId"),
        @Result(column = "ban_reason", property = "banReason"),
        @Result(column = "ban_end_time", property = "banEndTime", jdbcType = JdbcType.TIMESTAMP),
        @Result(column = "created_at", property = "user.createdAt"),
        @Result(column = "updated_at", property = "user.updatedAt"),

        @Result(column = "product_count", property = "productCount"),
        @Result(column = "order_count", property = "orderCount"),
        @Result(column = "review_count", property = "reviewCount"),
        @Result(column = "avg_rating", property = "avgRating"),
        @Result(column = "last_login_time", property = "lastLoginTime", jdbcType = JdbcType.TIMESTAMP)
    })
    UserDetail selectUserDetail(@Param("userId") Long userId);

    /**
     * 获取用户统计信息
     */
    @Select({
        "SELECT ",
        "  COUNT(*) as total_users,",
        "  SUM(CASE WHEN (ban_end_time IS NULL OR ban_end_time &lt;= NOW()) AND status = 'ACTIVE' THEN 1 ELSE 0 END) as active_users,",
        "  SUM(CASE WHEN ban_end_time &gt; NOW() THEN 1 ELSE 0 END) as banned_users,",
        "  SUM(CASE WHEN verify_status = 'PENDING' THEN 1 ELSE 0 END) as pending_verifications,",
        "  SUM(CASE WHEN verify_status = 'APPROVED' THEN 1 ELSE 0 END) as approved_verifications,",
        "  SUM(CASE WHEN verify_status = 'REJECTED' THEN 1 ELSE 0 END) as rejected_verifications,",
        "  SUM(CASE WHEN DATE(created_at) = CURDATE() THEN 1 ELSE 0 END) as today_new_users,",
        "  SUM(CASE WHEN YEARWEEK(created_at) = YEARWEEK(NOW()) THEN 1 ELSE 0 END) as week_new_users,",
        "  SUM(CASE WHEN YEAR(created_at) = YEAR(NOW()) AND MONTH(created_at) = MONTH(NOW()) THEN 1 ELSE 0 END) as month_new_users",
        "FROM users",
        "WHERE deleted = 0"
    })
    @Results({
        @Result(column = "total_users", property = "totalUsers"),
        @Result(column = "active_users", property = "activeUsers"),
        @Result(column = "banned_users", property = "bannedUsers"),
        @Result(column = "pending_verifications", property = "pendingVerifications"),
        @Result(column = "approved_verifications", property = "approvedVerifications"),
        @Result(column = "rejected_verifications", property = "rejectedVerifications"),
        @Result(column = "today_new_users", property = "todayNewUsers"),
        @Result(column = "week_new_users", property = "weekNewUsers"),
        @Result(column = "month_new_users", property = "monthNewUsers")
    })
    UserStats selectUserStats();

    /**
     * 批量封禁用户
     */
    @Update({
        "<script>",
        "UPDATE users SET ",
        "  status = 1,",
        "  ban_reason = #{reason},",
        "  ban_end_time = #{banEndTime, jdbcType=TIMESTAMP},",
        "  updated_at = NOW()",
        "WHERE deleted = 0 AND id IN",
        "<foreach collection='userIds' item='userId' open='(' separator=',' close=')'>",
        "  #{userId}",
        "</foreach>",
        "</script>"
    })
    int batchBanUsers(@Param("userIds") List<Long> userIds, 
                      @Param("reason") String reason, 
                      @Param("banEndTime") LocalDateTime banEndTime);

    /**
     * 批量审核学生身份认证
     */
    @Update({
        "<script>",
        "UPDATE users SET ",
        "  verify_status = #{status},",
        "  verify_reason = #{reason, jdbcType=VARCHAR},",
        "  updated_at = NOW()",
        "WHERE deleted = 0 AND verify_status != 2 AND id IN",
        "<foreach collection='userIds' item='userId' open='(' separator=',' close=')'>",
        "  #{userId}",
        "</foreach>",
        "</script>"
    })
    int batchVerifyStudentIdentity(@Param("userIds") List<Long> userIds, 
                                   @Param("status") String status, 
                                   @Param("reason") String reason);

    /**
     * 重置用户密码
     */
    @Update({
        "UPDATE users SET ",
        "  password = #{newPassword},",
        "  updated_at = NOW()",
        "WHERE id = #{userId} AND deleted = 0"
    })
    int resetUserPassword(@Param("userId") Long userId, @Param("newPassword") String newPassword);

    /**
     * 删除用户（软删除）
     */
    @Update({
        "UPDATE users SET ",
        "  deleted = 1,",
        "  updated_at = NOW()",
        "WHERE id = #{userId} AND deleted = 0"
    })
    int deleteUser(@Param("userId") Long userId);
}