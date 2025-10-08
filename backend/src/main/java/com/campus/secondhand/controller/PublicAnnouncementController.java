package com.campus.secondhand.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.common.Result;
import com.campus.secondhand.common.ResultCode;
import com.campus.secondhand.entity.Announcement;
import com.campus.secondhand.service.AnnouncementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 公开公告控制器（用户端）
 */
@Slf4j
@RestController
@RequestMapping("/announcements")
@CrossOrigin(origins = "*")
public class PublicAnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    /**
     * 获取已发布的公告列表（用户端）
     */
    @GetMapping
    public Result<IPage<Announcement>> getPublishedAnnouncements(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String type) {
        
        try {
            Page<Announcement> page = new Page<>(current, size);
            // 只获取已发布的公告（status = 1）
            IPage<Announcement> result = announcementService.getAnnouncementPage(page, null, type, 1);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取公告列表失败", e);
            return Result.error(ResultCode.ERROR, "获取公告列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取公告详情（用户端）
     */
    @GetMapping("/{id}")
    public Result<Announcement> getAnnouncementById(@PathVariable Long id) {
        try {
            Announcement announcement = announcementService.getAnnouncementById(id);
            if (announcement == null) {
                return Result.error(ResultCode.NOT_FOUND, "公告不存在");
            }
            // 检查公告是否已发布
            if (announcement.getStatus() != 1) {
                return Result.error(ResultCode.NOT_FOUND, "公告不存在或未发布");
            }
            
            // 增加浏览次数
            announcementService.incrementViewCount(id);
            
            return Result.success(announcement);
        } catch (Exception e) {
            log.error("获取公告详情失败", e);
            return Result.error(ResultCode.ERROR, "获取公告详情失败: " + e.getMessage());
        }
    }
}