package com.campus.secondhand.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.common.Result;
import com.campus.secondhand.common.ResultCode;
import com.campus.secondhand.entity.Announcement;
import com.campus.secondhand.service.AnnouncementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 公告管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin/announcements")
@CrossOrigin(origins = "*")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    /**
     * 分页查询公告列表
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<IPage<Announcement>> getAnnouncementPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer status) {
        
        try {
            Page<Announcement> page = new Page<>(current, size);
            IPage<Announcement> result = announcementService.getAnnouncementPage(page, keyword, type, status);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取公告列表失败", e);
            return Result.error(ResultCode.ERROR, "获取公告列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取公告详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Announcement> getAnnouncementById(@PathVariable Long id) {
        try {
            Announcement announcement = announcementService.getAnnouncementById(id);
            if (announcement == null) {
                return Result.error(ResultCode.NOT_FOUND, "公告不存在");
            }
            return Result.success(announcement);
        } catch (Exception e) {
            log.error("获取公告详情失败", e);
            return Result.error(ResultCode.ERROR, "获取公告详情失败: " + e.getMessage());
        }
    }

    /**
     * 创建公告
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> createAnnouncement(@RequestBody Announcement announcement, HttpServletRequest request) {
        try {
            // 从JWT中获取用户ID（这里简化处理，实际应该从JWT中解析）
            announcement.setAuthorId(1L); // 暂时设为1，实际应该从JWT中获取
            
            boolean success = announcementService.createAnnouncement(announcement);
            if (success) {
                return Result.success("公告创建成功");
            } else {
                return Result.error(ResultCode.ERROR, "公告创建失败");
            }
        } catch (Exception e) {
            log.error("公告创建失败", e);
            return Result.error(ResultCode.ERROR, "公告创建失败: " + e.getMessage());
        }
    }

    /**
     * 更新公告
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> updateAnnouncement(@PathVariable Long id, @RequestBody Announcement announcement) {
        try {
            announcement.setId(id);
            boolean success = announcementService.updateAnnouncement(announcement);
            if (success) {
                return Result.success("公告更新成功");
            } else {
                return Result.error(ResultCode.ERROR, "公告更新失败");
            }
        } catch (Exception e) {
            log.error("公告更新失败", e);
            return Result.error(ResultCode.ERROR, "公告更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除公告
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> deleteAnnouncement(@PathVariable Long id) {
        try {
            boolean success = announcementService.deleteAnnouncement(id);
            if (success) {
                return Result.success("公告删除成功");
            } else {
                return Result.error(ResultCode.ERROR, "公告删除失败");
            }
        } catch (Exception e) {
            log.error("公告删除失败", e);
            return Result.error(ResultCode.ERROR, "公告删除失败: " + e.getMessage());
        }
    }

    /**
     * 批量删除公告
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> batchDeleteAnnouncements(@RequestBody List<Long> ids) {
        try {
            boolean success = announcementService.batchDeleteAnnouncements(ids);
            if (success) {
                return Result.success("批量删除成功");
            } else {
                return Result.error(ResultCode.ERROR, "批量删除失败");
            }
        } catch (Exception e) {
            log.error("批量删除失败", e);
            return Result.error(ResultCode.ERROR, "批量删除失败: " + e.getMessage());
        }
    }

    /**
     * 发布公告
     */
    @PutMapping("/{id}/publish")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> publishAnnouncement(@PathVariable Long id) {
        try {
            boolean success = announcementService.publishAnnouncement(id);
            if (success) {
                return Result.success("公告发布成功");
            } else {
                return Result.error(ResultCode.ERROR, "公告发布失败");
            }
        } catch (Exception e) {
            log.error("公告发布失败", e);
            return Result.error(ResultCode.ERROR, "公告发布失败: " + e.getMessage());
        }
    }

    /**
     * 下线公告
     */
    @PutMapping("/{id}/offline")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> offlineAnnouncement(@PathVariable Long id) {
        try {
            boolean success = announcementService.offlineAnnouncement(id);
            if (success) {
                return Result.success("公告下线成功");
            } else {
                return Result.error(ResultCode.ERROR, "公告下线失败");
            }
        } catch (Exception e) {
            log.error("公告下线失败", e);
            return Result.error(ResultCode.ERROR, "公告下线失败: " + e.getMessage());
        }
    }

    /**
     * 批量发布公告
     */
    @PutMapping("/batch/publish")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> batchPublishAnnouncements(@RequestBody List<Long> ids) {
        try {
            boolean success = announcementService.batchPublishAnnouncements(ids);
            if (success) {
                return Result.success("批量发布成功");
            } else {
                return Result.error(ResultCode.ERROR, "批量发布失败");
            }
        } catch (Exception e) {
            log.error("批量发布失败", e);
            return Result.error(ResultCode.ERROR, "批量发布失败: " + e.getMessage());
        }
    }

    /**
     * 批量下线公告
     */
    @PutMapping("/batch/offline")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> batchOfflineAnnouncements(@RequestBody List<Long> ids) {
        try {
            boolean success = announcementService.batchOfflineAnnouncements(ids);
            if (success) {
                return Result.success("批量下线成功");
            } else {
                return Result.error(ResultCode.ERROR, "批量下线失败");
            }
        } catch (Exception e) {
            log.error("批量下线失败", e);
            return Result.error(ResultCode.ERROR, "批量下线失败: " + e.getMessage());
        }
    }

    /**
     * 增加浏览次数
     */
    @PutMapping("/{id}/view")
    public Result<String> incrementViewCount(@PathVariable Long id) {
        try {
            boolean success = announcementService.incrementViewCount(id);
            if (success) {
                return Result.success("浏览次数更新成功");
            } else {
                return Result.error(ResultCode.ERROR, "浏览次数更新失败");
            }
        } catch (Exception e) {
            log.error("增加浏览量失败", e);
            return Result.error(ResultCode.ERROR.getCode(), "增加浏览量失败: " + e.getMessage());
        }
    }

    /**
     * 获取公告统计信息
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Map<String, Object>> getAnnouncementStats() {
        try {
            Map<String, Object> stats = announcementService.getAnnouncementStats();
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取统计数据失败", e);
            return Result.error(ResultCode.ERROR.getCode(), "获取统计数据失败: " + e.getMessage());
        }
    }
}