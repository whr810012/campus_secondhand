package com.campus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户Mapper接口
 * 
 * @author Campus Team
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM user WHERE username = #{username} AND deleted = 0")
    User findByUsername(@Param("username") String username);
    
    /**
     * 根据邮箱查询用户
     */
    @Select("SELECT * FROM user WHERE email = #{email} AND deleted = 0")
    User findByEmail(@Param("email") String email);
    
    /**
     * 分页查询用户列表
     */
    @Select("<script>" +
            "SELECT * FROM user WHERE deleted = 0" +
            "<if test='keyword != null and keyword != \"\">" +
            " AND (username LIKE CONCAT('%', #{keyword}, '%') OR nickname LIKE CONCAT('%', #{keyword}, '%') OR email LIKE CONCAT('%', #{keyword}, '%'))" +
            "</if>" +
            "<if test='role != null'>" +
            " AND role = #{role}" +
            "</if>" +
            "<if test='status != null'>" +
            " AND status = #{status}" +
            "</if>" +
            " ORDER BY created_time DESC" +
            "</script>")
    IPage<User> selectUserPage(Page<User> page, @Param("keyword") String keyword, 
                              @Param("role") Integer role, @Param("status") Integer status);
    
    /**
     * 统计用户数量
     */
    @Select("SELECT COUNT(*) FROM user WHERE deleted = 0")
    Long countUsers();
    
    /**
     * 统计今日新增用户数量
     */
    @Select("SELECT COUNT(*) FROM user WHERE DATE(created_time) = CURDATE() AND deleted = 0")
    Long countTodayUsers();
    
    /**
     * 统计各角色用户数量
     */
    @Select("SELECT role, COUNT(*) as count FROM user WHERE deleted = 0 GROUP BY role")
    List<Object> countUsersByRole();
}