package com.campus.secondhand.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.entity.Announcement;

import java.util.List;
import java.util.Map;

/**
 * 公告服务接口
 */
public interface AnnouncementService {

    /**
     * 分页查询公告列表
     */
    IPage<Announcement> getAnnouncementPage(Page<Announcement> page, String keyword, String type, Integer status);

    /**
     * 根据ID获取公告详情
     */
    Announcement getAnnouncementById(Long id);

    /**
     * 创建公告
     */
    boolean createAnnouncement(Announcement announcement);

    /**
     * 更新公告
     */
    boolean updateAnnouncement(Announcement announcement);

    /**
     * 删除公告
     */
    boolean deleteAnnouncement(Long id);

    /**
     * 批量删除公告
     */
    boolean batchDeleteAnnouncements(List<Long> ids);

    /**
     * 发布公告
     */
    boolean publishAnnouncement(Long id);

    /**
     * 下线公告
     */
    boolean offlineAnnouncement(Long id);

    /**
     * 批量发布公告
     */
    boolean batchPublishAnnouncements(List<Long> ids);

    /**
     * 批量下线公告
     */
    boolean batchOfflineAnnouncements(List<Long> ids);

    /**
     * 增加浏览次数
     */
    boolean incrementViewCount(Long id);

    /**
     * 获取公告统计信息
     */
    Map<String, Object> getAnnouncementStats();
}