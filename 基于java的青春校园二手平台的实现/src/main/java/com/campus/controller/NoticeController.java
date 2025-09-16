package com.campus.controller;

import com.campus.common.PageResult;
import com.campus.common.Result;
import com.campus.entity.Notice;
import com.campus.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 系统公告控制器
 * 
 * @author Campus Team
 */
@RestController
@RequestMapping("/notice")
@CrossOrigin
public class NoticeController {
    
    @Autowired
    private NoticeService noticeService;
    
    /**
     * 创建公告
     */
    @PostMapping("/create")
    public Result<Void> createNotice(@RequestBody Notice notice) {
        try {
            noticeService.createNotice(notice);
            return Result.success("公告创建成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 分页查询公告列表（管理端）
     */
    @GetMapping("/admin/page")
    public Result<PageResult<Notice>> getNoticePageForAdmin(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder) {
        try {
            PageResult<Notice> result = noticeService.getNoticePageForAdmin(current, size, keyword, type, status, sortBy, sortOrder);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 查询启用的公告列表（用户端）
     */
    @GetMapping("/enabled")
    public Result<List<Notice>> getEnabledNotices(
            @RequestParam(required = false) Integer type,
            @RequestParam(defaultValue = "20") Integer limit) {
        try {
            List<Notice> notices = noticeService.getEnabledNotices(type, limit);
            return Result.success(notices);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取公告详情
     */
    @GetMapping("/{noticeId}")
    public Result<Notice> getNoticeDetail(@PathVariable Long noticeId) {
        try {
            Notice notice = noticeService.getNoticeDetail(noticeId);
            return Result.success(notice);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新公告信息
     */
    @PutMapping("/{noticeId}")
    public Result<Void> updateNotice(@PathVariable Long noticeId, @RequestBody Notice notice) {
        try {
            noticeService.updateNotice(noticeId, notice);
            return Result.success("公告更新成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除公告
     */
    @DeleteMapping("/{noticeId}")
    public Result<Void> deleteNotice(@PathVariable Long noticeId) {
        try {
            noticeService.deleteNotice(noticeId);
            return Result.success("公告删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 启用/禁用公告
     */
    @PutMapping("/status/{noticeId}")
    public Result<Void> updateNoticeStatus(@PathVariable Long noticeId, @RequestBody Map<String, Integer> params) {
        try {
            Integer status = params.get("status");
            noticeService.updateNoticeStatus(noticeId, status);
            return Result.success("状态更新成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新公告排序
     */
    @PutMapping("/sort/{noticeId}")
    public Result<Void> updateNoticeSort(@PathVariable Long noticeId, @RequestBody Map<String, Integer> params) {
        try {
            Integer sortOrder = params.get("sortOrder");
            noticeService.updateNoticeSort(noticeId, sortOrder);
            return Result.success("排序更新成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取公告统计信息
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getNoticeStatistics() {
        try {
            Map<String, Object> statistics = noticeService.getNoticeStatistics();
            return Result.success(statistics);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取最新公告
     */
    @GetMapping("/latest")
    public Result<List<Notice>> getLatestNotices(@RequestParam(defaultValue = "5") Integer limit) {
        try {
            List<Notice> notices = noticeService.getLatestNotices(limit);
            return Result.success(notices);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取重要公告
     */
    @GetMapping("/important")
    public Result<List<Notice>> getImportantNotices(@RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<Notice> notices = noticeService.getEnabledNotices(Notice.NoticeType.IMPORTANT.getValue(), limit);
            return Result.success(notices);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取系统公告
     */
    @GetMapping("/system")
    public Result<List<Notice>> getSystemNotices(@RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<Notice> notices = noticeService.getEnabledNotices(Notice.NoticeType.SYSTEM.getValue(), limit);
            return Result.success(notices);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 批量删除公告
     */
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteNotices(@RequestBody Map<String, List<Long>> params) {
        try {
            List<Long> noticeIds = params.get("noticeIds");
            for (Long noticeId : noticeIds) {
                noticeService.deleteNotice(noticeId);
            }
            return Result.success("批量删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}