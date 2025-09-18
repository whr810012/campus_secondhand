package com.campus.secondhand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 用户数据访问层
 *
 * @author campus-secondhand
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据手机号查询用户
     */
    @Select("SELECT * FROM users WHERE phone = #{phone}")
    User selectByPhone(@Param("phone") String phone);

    /**
     * 根据学号查询用户
     */
    @Select("SELECT * FROM users WHERE student_id = #{studentId} AND deleted = 0")
    User selectByStudentId(@Param("studentId") String studentId);

    /**
     * 分页查询用户列表
     */
    @Select({
        "<script>",
        "SELECT u.*, s.name as schoolName FROM users u",
        "LEFT JOIN schools s ON u.school_id = s.id",
        "WHERE u.deleted = 0",
        "<if test='keyword != null and keyword != \"\"'>",
        "  AND (u.nickname LIKE CONCAT('&#37;', #{keyword}, '&#37;')",
        "       OR u.phone LIKE CONCAT('&#37;', #{keyword}, '&#37;')",
        "       OR u.student_id LIKE CONCAT('&#37;', #{keyword}, '&#37;'))",
        "</if>",
        "<if test='status != null'>",
        "  AND u.status = #{status}",
        "</if>",
        "<if test='verifyStatus != null'>",
        "  AND u.verify_status = #{verifyStatus}",
        "</if>",
        "<if test='schoolId != null'>",
        "  AND u.school_id = #{schoolId}",
        "</if>",
        "ORDER BY u.created_at DESC",
        "</script>"
    })
    Page<User> selectUserPage(Page<User> page,
                             @Param("keyword") String keyword,
                             @Param("status") Integer status,
                             @Param("verifyStatus") Integer verifyStatus,
                             @Param("schoolId") Long schoolId);

    /**
     * 批量更新用户状态
     */
    @Update({
        "<script>",
        "UPDATE users SET status = #{status}",
        "WHERE id IN",
        "<foreach collection='userIds' item='id' open='(' separator=',' close=')'>",
        "  #{id}",
        "</foreach>",
        "</script>"
    })
    int batchUpdateStatus(@Param("userIds") List<Long> userIds, @Param("status") Integer status);

    /**
     * 更新用户信誉分数
     */
    @Update("UPDATE users SET credit_score = #{creditScore} WHERE id = #{userId}")
    int updateCreditScore(@Param("userId") Long userId, @Param("creditScore") Integer creditScore);

    /**
     * 更新用户交易统计
     */
    @Update("UPDATE users SET trade_count = trade_count + 1, good_rate = #{goodRate} WHERE id = #{userId}")
    int updateTradeStats(@Param("userId") Long userId, @Param("goodRate") Double goodRate);

    /**
     * 获取用户统计信息
     */
    @Select({
        "SELECT COUNT(*) as totalUsers,",
        "       SUM(CASE WHEN status = 0 THEN 1 ELSE 0 END) as activeUsers,",
        "       SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END) as bannedUsers,",
        "       SUM(CASE WHEN verify_status = 2 THEN 1 ELSE 0 END) as verifiedUsers",
        "FROM users WHERE deleted = 0"
    })
    UserStats getUserStats();

    /**
     * 用户统计信息内部类
     */
    class UserStats {
        private Long totalUsers;
        private Long activeUsers;
        private Long bannedUsers;
        private Long verifiedUsers;

        // Getter和Setter方法
        public Long getTotalUsers() {
            return totalUsers;
        }

        public void setTotalUsers(Long totalUsers) {
            this.totalUsers = totalUsers;
        }

        public Long getActiveUsers() {
            return activeUsers;
        }

        public void setActiveUsers(Long activeUsers) {
            this.activeUsers = activeUsers;
        }

        public Long getBannedUsers() {
            return bannedUsers;
        }

        public void setBannedUsers(Long bannedUsers) {
            this.bannedUsers = bannedUsers;
        }

        public Long getVerifiedUsers() {
            return verifiedUsers;
        }

        public void setVerifiedUsers(Long verifiedUsers) {
            this.verifiedUsers = verifiedUsers;
        }
    }
}