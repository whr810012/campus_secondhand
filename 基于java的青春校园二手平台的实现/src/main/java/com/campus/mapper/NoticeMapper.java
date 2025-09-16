package com.campus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.entity.Notice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 系统公告Mapper接口
 * 
 * @author Campus Team
 */
@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {
    
    /**
     * 分页查询公告列表（管理端）
     */
    @Select("<script>" +
            "SELECT n.*, u.nickname as createdUserName FROM notice n " +
            "LEFT JOIN user u ON n.created_user_id = u.id " +
            "WHERE n.deleted = 0" +
            "<if test='keyword != null and keyword != \"\">" +
            " AND (n.title LIKE CONCAT('%', #{keyword}, '%') OR n.content LIKE CONCAT('%', #{keyword}, '%'))" +
            "</if>" +
            "<if test='type != null'>" +
            " AND n.type = #{type}" +
            "</if>" +
            "<if test='status != null'>" +
            " AND n.status = #{status}" +
            "</if>" +
            " ORDER BY n.sort_order ASC, n.created_time DESC" +
            "</script>")
    IPage<Notice> selectNoticePage(Page<Notice> page, @Param("keyword") String keyword,
                                 @Param("type") Integer type, @Param("status") Integer status);
    
    /**
     * 查询启用的公告列表（用户端）
     */
    @Select("<script>" +
            "SELECT * FROM notice WHERE deleted = 0 AND status = 1" +
            "<if test='type != null'>" +
            " AND type = #{type}" +
            "</if>" +
            " ORDER BY sort_order ASC, created_time DESC" +
            "<if test='limit != null'>" +
            " LIMIT #{limit}" +
            "</if>" +
            "</script>")
    List<Notice> selectEnabledNotices(@Param("type") Integer type, @Param("limit") Integer limit);
    
    /**
     * 统计公告数量
     */
    @Select("SELECT COUNT(*) FROM notice WHERE deleted = 0")
    Long countNotices();
}