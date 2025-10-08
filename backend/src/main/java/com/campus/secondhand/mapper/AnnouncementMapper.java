package com.campus.secondhand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 公告数据访问层
 */
@Mapper
public interface AnnouncementMapper extends BaseMapper<Announcement> {

    /**
     * 分页查询公告列表（带发布者信息）
     */
    @Select("SELECT a.*, u.nickname as author " +
            "FROM announcements a " +
            "LEFT JOIN users u ON a.author_id = u.id " +
            "WHERE a.deleted = 0 " +
            "ORDER BY a.created_at DESC")
    IPage<Announcement> selectAnnouncementPage(Page<Announcement> page);

    /**
     * 根据状态分页查询公告列表
     */
    @Select("SELECT a.*, u.nickname as author " +
            "FROM announcements a " +
            "LEFT JOIN users u ON a.author_id = u.id " +
            "WHERE a.deleted = 0 AND a.status = #{status} " +
            "ORDER BY a.created_at DESC")
    IPage<Announcement> selectAnnouncementPageByStatus(Page<Announcement> page, @Param("status") Integer status);

    /**
     * 根据类型分页查询公告列表
     */
    @Select("SELECT a.*, u.nickname as author " +
            "FROM announcements a " +
            "LEFT JOIN users u ON a.author_id = u.id " +
            "WHERE a.deleted = 0 AND a.type = #{type} " +
            "ORDER BY a.created_at DESC")
    IPage<Announcement> selectAnnouncementPageByType(Page<Announcement> page, @Param("type") String type);

    /**
     * 根据标题搜索公告
     */
    @Select("SELECT a.*, u.nickname as author " +
            "FROM announcements a " +
            "LEFT JOIN users u ON a.author_id = u.id " +
            "WHERE a.deleted = 0 AND a.title LIKE CONCAT('%', #{keyword}, '%') " +
            "ORDER BY a.created_at DESC")
    IPage<Announcement> selectAnnouncementPageByKeyword(Page<Announcement> page, @Param("keyword") String keyword);

    /**
     * 获取公告详情（带发布者信息）
     */
    @Select("SELECT a.*, u.nickname as author " +
            "FROM announcements a " +
            "LEFT JOIN users u ON a.author_id = u.id " +
            "WHERE a.id = #{id} AND a.deleted = 0")
    Announcement selectAnnouncementById(@Param("id") Long id);

    /**
     * 更新公告状态
     */
    @Update("UPDATE announcements SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int updateAnnouncementStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 增加浏览次数
     */
    @Update("UPDATE announcements SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(@Param("id") Long id);

    /**
     * 批量更新状态
     */
    @Update("<script>" +
            "UPDATE announcements SET status = #{status}, updated_at = NOW() " +
            "WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") Integer status);

    /**
     * 获取各状态公告统计
     */
    @Select("SELECT " +
            "COUNT(CASE WHEN status = 1 THEN 1 END) as published, " +
            "COUNT(CASE WHEN status = 0 THEN 1 END) as draft, " +
            "COUNT(CASE WHEN status = 2 THEN 1 END) as offline, " +
            "COUNT(*) as total " +
            "FROM announcements WHERE deleted = 0")
    List<Object> getAnnouncementStats();
}